package com.example.controller;

import com.example.common.Result;
import com.example.request.AudioRequest;
import com.example.service.AudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author feixia0g
 * @since 2024-04-25 02:54:16
 */
@RestController
public class AudioController {
    @Autowired
    private AudioService audioService;

    /**
     * Todo 前台页面调用 语音识别处理
     * 处理前台传递Blob音频文件
     * 返回识别的文本信息和语音包
     */
    @PostMapping("/audio/SR")
    public Result SpeechRecognition(@RequestBody AudioRequest audioRequest) throws IOException {
        return audioService.SpeechRecognition(audioRequest);
    }

}
