package com.example.controller;

import com.example.entity.YoloModel;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;

@RestController
public class YoloController {
    @Autowired
    private YoloModel yoloModel;

    @PostMapping("/yoloProcess")
    public byte[] processImage(@RequestBody byte[] imageData) {
        //Todo 动态库加载很可能不写在这个位置，写在这里动态库可能在yoloModel加载后才加载
        //先加载动态库
        loadDll();
        // Decode image data
        Mat image = Imgcodecs.imdecode(new MatOfByte(imageData), Imgcodecs.IMREAD_UNCHANGED);

        // Detect objects using YOLO model
        Mat detections = yoloModel.detectObjects(image);

        // Process detections (draw bounding boxes, labels, etc.)
        Mat processedImage = processDetections(image, detections);

        // Encode processed image to byte array
        MatOfByte result = new MatOfByte();
        Imgcodecs.imencode(".jpg", processedImage, result);

        //Todo 修改返回给前端的值
        return result.toArray();
    }

    private Mat processDetections(Mat image, Mat detections) {
        int cols = image.cols();
        int rows = image.rows();

        for (int i = 0; i < detections.rows(); ++i) {
            double confidence = detections.get(i, 5)[0];
            if (confidence > 0.5) { // 设置一个阈值，只绘制置信度高于阈值的边界框
                int x = (int) (detections.get(i, 0)[0] * cols);
                int y = (int) (detections.get(i, 1)[0] * rows);
                int w = (int) (detections.get(i, 2)[0] * cols);
                int h = (int) (detections.get(i, 3)[0] * rows);

                // Draw rectangle on the original image
                Imgproc.rectangle(image, new Point(x, y), new Point(x + w, y + h), new Scalar(0, 255, 0), 2);
            }
        }
        // Return the processed image
        return image;
    }

    public static void loadDll() {
        // 解决awt报错问题
        System.setProperty("java.awt.headless", "false");
        System.out.println(System.getProperty("java.library.path"));
        // 加载动态库
        URL url = ClassLoader.getSystemResource("dlls/opencv_java452.dll");
        System.load(url.getPath());
    }
}
