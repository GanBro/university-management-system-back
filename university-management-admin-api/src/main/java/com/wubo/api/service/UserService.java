package com.wubo.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.entity.User;

import java.util.List;
import java.util.Map;

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

    Page<User> getUserList(Page<User> page, Map<String, Object> params);
    User getUserDetail(Integer userId);
    boolean updateUser(User user);
    boolean deleteUsers(List<Integer> ids);

    /**
     * 创建新用户
     * @param user 用户信息
     * @return 是否创建成功
     */
    boolean createUser(User user);

    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);

}
