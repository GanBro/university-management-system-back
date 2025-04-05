package com.wubo.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.entity.University;
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

    void followUniversity(Integer userId, Integer universityId);

    void unfollowUniversity(Integer userId, Integer universityId);

    List<University> getFollowedUniversities(Integer userId);

    /**
     * 发送密码重置验证码
     * @param username 用户名
     * @param email 邮箱
     * @return 是否发送成功
     */
    boolean sendPasswordResetCode(String username, String email);

    /**
     * 验证密码重置验证码
     * @param username 用户名
     * @param email 邮箱
     * @param code 验证码
     * @return 重置令牌，如果验证失败返回null
     */
    String verifyPasswordResetCode(String username, String email, String code);

    /**
     * 重置密码
     * @param username 用户名
     * @param resetToken 重置令牌
     * @param newPassword 新密码
     * @return 是否重置成功
     */
    boolean resetPassword(String username, String resetToken, String newPassword);
}
