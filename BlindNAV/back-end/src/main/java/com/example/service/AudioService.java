package com.example.service;

import com.example.common.Result;
import com.example.entity.Audio;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.request.AudioRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author feixia0g
 * @since 2024-04-25 02:54:16
 */
public interface AudioService extends IService<Audio> {
    public Result SpeechRecognition(AudioRequest audioRequest);
}
