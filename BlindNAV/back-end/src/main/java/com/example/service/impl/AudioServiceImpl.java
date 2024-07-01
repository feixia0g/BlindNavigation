package com.example.service.impl;

import com.example.common.Result;
import com.example.entity.Audio;
import com.example.mapper.AudioMapper;
import com.example.request.AudioRequest;
import com.example.service.AudioService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author feixia0g
 * @since 2024-04-25 02:54:16
 */
@Service
public class AudioServiceImpl extends ServiceImpl<AudioMapper, Audio> implements AudioService {
    @Override
    public Result SpeechRecognition(AudioRequest audioRequest) {
        //Todo 实现录音功能
        //Todo pyPath待定
        String pyPath = "";
        String line = "ok";
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", "cd /d " + pyPath + " && python record.py");
            builder.redirectErrorStream(true);
            Process process = builder.start();

            // 读取Python脚本的输出结果
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            //先输出到控制台方便debug
            String nextLine = reader.readLine();
            while (nextLine != null) {
                line = nextLine;
                System.out.println(line);
                nextLine = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }

        //Todo 实现语音转换文字功能
        //Todo 如何获取文字
        String sentences = "";
        pyPath = "";
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", "cd /d " + pyPath + " && python stt.py");
            builder.redirectErrorStream(true);
            Process process = builder.start();

            // 读取Python脚本的输出结果
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            //先输出到控制台方便debug
            String nextLine = reader.readLine();
            while (nextLine != null) {
                sentences = StringUtils.join(sentences, nextLine);
                System.out.println(nextLine);
                nextLine = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }

        //Todo 调用模型模块将文本信息转化为人工音音频，返回url
        //Todo url待定
        String url = "";
        pyPath = "";
        line = "ok";
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", "cd /d " + pyPath + " && python tts.py --text=\"" + sentences + "\"");
            builder.redirectErrorStream(true);
            Process process = builder.start();

            // 读取Python脚本的输出结果
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            //先输出到控制台方便debug
            String nextLine = reader.readLine();
            while (nextLine != null) {
                line = nextLine;
                System.out.println(line);
                nextLine = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }

        //Todo 将转化后的文本和合成语音的url封装并返回给前台
        Audio audio = new Audio();
        BeanUtils.copyProperties(audioRequest, audio);
        audio.setUrl(url);
        audio.setSentences(sentences);

        return Result.success("转换文本和生成音频成功", audio);
    }
}
