package com.wubo.api.service;

import com.wubo.api.entity.User;

public interface UserService {
    /**
     * 登录
     * @param user 用户信息
     * @return user
     */
    User login(User user);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);

    User getUserByToken(String token);

}
