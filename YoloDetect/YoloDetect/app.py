import argparse
import asyncio
import json
import logging
import os
import platform
import ssl
import time

import cv2
from aiohttp import web
from aiortc import (
    MediaStreamTrack,
    RTCDataChannel,
    RTCPeerConnection,
    RTCSessionDescription,
    VideoStreamTrack,
)
from aiortc.contrib.media import MediaPlayer, MediaRelay
from av import VideoFrame

relay = None
webcam = None

async def offer(request):
    params = await request.json()
    offer = RTCSessionDescription(sdp=params["sdp"], type=params["type"])

    pc = RTCPeerConnection()
    pcs.add(pc)

    # 创建数据通道并保存引用
    data_channel = pc.createDataChannel("text_channel")
    data_channels.append(data_channel)

    await server(pc, offer)

    # 启动消息发送器任务
    asyncio.ensure_future(example_message_sender())

    return web.Response(
        content_type="application/json",
        text=json.dumps(
            {"sdp": pc.localDescription.sdp, "type": pc.localDescription.type}
        ),
    )

# 添加处理 OPTIONS 请求的方法
async def handle_options(request):
    headers = {
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Methods': 'POST, GET, OPTIONS',
        'Access-Control-Allow-Headers': 'Content-Type'
    }
    return web.Response(status=200, headers=headers)

pcs = set()
data_channels = []

async def server(pc, offer):
    @pc.on("connectionstatechange")
    async def on_connectionstatechange():
        print("Connection state is %s" % pc.connectionState)
        if pc.connectionState == "failed":
            await pc.close()
            pcs.discard(pc)

    @pc.on("track")
    def on_track(track):
        print("======= received track: ", track)
        if track.kind == "video":
            t = FaceSwapper(track)
            pc.addTrack(t)

    # 添加数据通道处理逻辑
    @pc.on("datachannel")
    def on_datachannel(channel):

        @channel.on("open")
        def on_open():
            print("Data channel is open")

        @channel.on("message")
        def on_message(message):
            print("Received message on data channel:", message)

    await pc.setRemoteDescription(offer)
    answer = await pc.createAnswer()
    await pc.setLocalDescription(answer)

async def on_shutdown(app):
    # close peer connections
    coros = [pc.close() for pc in pcs]
    await asyncio.gather(*coros)
    pcs.clear()


class FaceSwapper(VideoStreamTrack):
    kind = "video"

    def __init__(self, track):
        super().__init__()
        self.track = track
        self.face_detector = cv2.CascadeClassifier("./haarcascade_frontalface_alt.xml")
        self.face = cv2.imread("./wu.png")

    async def recv(self):
        timestamp, video_timestamp_base = await self.next_timestamp()
        frame = await self.track.recv()
        frame = frame.to_ndarray(format="bgr24")
        s = time.time()
        face_zones = self.face_detector.detectMultiScale(
            cv2.cvtColor(frame, code=cv2.COLOR_BGR2GRAY)
        )
        for x, y, w, h in face_zones:
            face = cv2.resize(self.face, dsize=(w, h))
            frame[y : y + h, x : x + w] = face
        frame = VideoFrame.from_ndarray(frame, format="bgr24")
        frame.pts = timestamp
        frame.time_base = video_timestamp_base
        return frame


# 定义一个函数，在需要的时候调用它来发送消息
def send_message_to_all_channels(message):
    for channel in data_channels:
        if channel.readyState == "open":
            channel.send(message)

# 示例：在10秒后发送一条消息到所有打开的数据通道
async def example_message_sender():
    await asyncio.sleep(10)
    send_message_to_all_channels("Hello from the server after 10 seconds!")

# 添加CORS中间件
@web.middleware
async def cors_middleware(request, handler):
    response = await handler(request)
    response.headers['Access-Control-Allow-Origin'] = '*'
    response.headers['Access-Control-Allow-Methods'] = 'POST, GET, OPTIONS'
    response.headers['Access-Control-Allow-Headers'] = 'Content-Type'
    return response

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="WebRTC webcam demo")
    parser.add_argument(
        "--host", default="0.0.0.0", help="Host for HTTP server (default: 0.0.0.0)"
    )
    parser.add_argument(
        "--port", type=int, default=8080, help="Port for HTTP server (default: 8080)"
    )
    args = parser.parse_args()

    logging.basicConfig(level=logging.INFO)

    app = web.Application(middlewares=[cors_middleware])
    app.on_shutdown.append(on_shutdown)
    app.router.add_post("/offer", offer)
    app.router.add_options("/offer", handle_options)  # 添加这一行
    web.run_app(app, host=args.host, port=args.port)
