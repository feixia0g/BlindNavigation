package com.example.config;

import com.example.entity.YoloModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YoloConfig {
    @Bean
    public YoloModel yoloModel() {
        //Todo 完善cfg和weights文件路径
        return new YoloModel("path/to/config.cfg", "path/to/weights.weights");
    }
}
