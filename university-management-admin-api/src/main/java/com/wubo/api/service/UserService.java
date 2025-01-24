package com.wubo.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.entity.UpdatePasswordRequest;
import com.wubo.api.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    /**
     * 用户登录
     */
    User login(User user);

    /**
     * 根据token获取用户信息
     */
    User getUserByToken(String token);

    /**
     * 用户注册
     */
    boolean register(User user);

    /**
     * 获取用户列表
     */
    Page<User> getUserList(Page<User> page, Map<String, Object> params);

    /**
     * 获取用户详情
     */
    User getUserDetail(Integer userId);

    /**
     * 更新用户信息
     */
    boolean updateUser(User user);

    /**
     * 删除单个用户
     */
    void deleteUser(Integer id);

    /**
     * 批量删除用户
     */
    void batchDeleteUsers(List<Integer> ids);

    /**
     * 创建新用户
     */
    boolean createUser(User user);

    /**
     * 根据用户名获取用户
     */
    User getUserByUsername(String username);

    User getUserById(Integer userId);

    boolean updateProfile(User user);

    boolean updatePassword(UpdatePasswordRequest request);
}
