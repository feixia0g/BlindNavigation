package com.example.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class AudioRequest {
    private Integer id;

    private Integer userId;

    private String name;

    private String url;

    private String sentences;
}
