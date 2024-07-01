package com.example.service;

import com.example.common.Result;
import com.example.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.request.UserRequest;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author feixia0g
 * @since 2024-04-24 10:44:04
 */
public interface UserService extends IService<User> {
    public boolean existUser(String username);
    public Result addUser(UserRequest userRequest);
    public boolean verityPassword(String username,String password);
    public Result loginStatus(UserRequest userRequest, HttpSession session);

}
