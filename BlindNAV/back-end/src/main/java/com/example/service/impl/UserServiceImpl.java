package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.common.Result;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.request.UserRequest;
import com.example.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;

import static com.example.constant.Constant.SALT;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author feixia0g
 * @since 2024-04-24 10:44:04
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean existUser(String username) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("name", username);
        return userMapper.selectCount(userQueryWrapper) > 0;
    }

    /**
     * @param userRequest
     * @return
     */
    @Override
    public Result addUser(UserRequest userRequest) {
        //Todo
        if (this.existUser(userRequest.getName())) {
            return Result.warning("该用户已被注册！");
        }
        User user = new User();
        BeanUtils.copyProperties(userRequest, user);

        //对密码进行md5加密
        String password = DigestUtils.md5DigestAsHex((SALT + userRequest.getPassword()).getBytes(StandardCharsets.UTF_8));
        user.setPassword(password);

        //对数据库进行操作  新增consumer新用户
        try {
            if (userMapper.insert(user) > 0) {
                return Result.success("注册成功");
            } else {
                return Result.error("注册失败");
            }
        } catch (DataAccessException e) {
            return Result.fatal(e.getMessage());
        }
    }

    @Override
    public boolean verityPassword(String username, String password) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("name", username);

        String secretPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes(StandardCharsets.UTF_8));
        userQueryWrapper.eq("password", secretPassword);
        return userMapper.selectCount(userQueryWrapper) > 0;
    }

    @Override
    public Result loginStatus(UserRequest userRequest, HttpSession session) {
        String username = userRequest.getName();
        String password = userRequest.getPassword();

        if (verityPassword(username, password)) {
            session.setAttribute("name",username);
            User user = new User();
            user.setName(username);
            return Result.success("登录成功",userMapper.selectList(new QueryWrapper<>(user)));
        }else {
            return Result.error("用户名或密码错误");
        }
    }
}
