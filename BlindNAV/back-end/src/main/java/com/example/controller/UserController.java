package com.example.controller;

import com.example.common.Result;
import com.example.request.UserRequest;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author feixia0g
 * @since 2024-04-24 10:44:04
 */
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * Todo 前台页面调用 注册
     * 新用户注册
     */
    @PostMapping("/user/add")
    public Result addUser(@RequestBody UserRequest userRequest){
        return userService.addUser(userRequest);
    }

    /**
     * Todo 前台页面调用 登录
     * 用户登录
     */
    @PostMapping("/user/login/status")
    public Result loginStatus(@RequestBody UserRequest userRequest, HttpSession session){
        return userService.loginStatus(userRequest,session);
    }



}
