package com.example.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class UserRequest {
    private Integer id;

    private String name;

    private String password;
}
