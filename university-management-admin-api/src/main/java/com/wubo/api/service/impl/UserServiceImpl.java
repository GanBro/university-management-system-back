package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wubo.api.entity.User;
import com.wubo.api.mapper.UserMapper;
import com.wubo.api.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
@Transactional
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;

    @Override
    public User login(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());  // 直接比较明文密码
        User existingUser = userMapper.selectOne(queryWrapper);
        if (existingUser != null) {
            // 更新最后登录时间
            existingUser.setLastLogin(new java.sql.Timestamp(System.currentTimeMillis()));
            userMapper.updateById(existingUser);
        }
        return existingUser;
    }

    @Override
    public User getUserByToken(String token) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("token", token);
        User user = userMapper.selectOne(queryWrapper);
        return user;
    }

    @Override
    @Transactional
    public boolean register(User user) {
        // 检查用户名是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        User existingUser = userMapper.selectOne(queryWrapper);
        if (existingUser != null) {
            // 用户名已存在
            return false;
        }

        // 生成唯一 token
        user.setToken(java.util.UUID.randomUUID().toString());

        // 设置创建时间
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // 设置默认角色为 user
        user.setRole("user");

        // 保存用户
        return userMapper.insert(user) > 0;
    }
}
