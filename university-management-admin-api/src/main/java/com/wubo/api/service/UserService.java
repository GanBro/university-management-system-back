package com.wubo.api.service;

import com.wubo.api.entity.User;

public interface UserService {
    /**
     * 登录
     * @param user 用户信息
     * @return user
     */
    User login(User user);

    User getUserByToken(String token);

    // 注册方法
    boolean register(User user);

}
