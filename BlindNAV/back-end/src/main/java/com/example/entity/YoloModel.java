package com.example.entity;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;

public class YoloModel {
    private Net net;

    public YoloModel(String configPath, String weightsPath) {
        net = Dnn.readNetFromDarknet(configPath, weightsPath);
    }

    public Mat detectObjects(Mat image) {
        //blob 神经网络的输入格式
        Mat blob = Dnn.blobFromImage(image, 1.0 / 255.0, new Size(416, 416), new Scalar(0, 0, 0), true, false);

        net.setInput(blob);
        Mat detections = net.forward();

        return detections;
    }
}
