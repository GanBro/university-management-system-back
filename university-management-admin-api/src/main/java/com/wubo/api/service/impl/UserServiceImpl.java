package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wubo.api.entity.Role;
import com.wubo.api.entity.User;
import com.wubo.api.entity.UserRole;
import com.wubo.api.mapper.RoleMapper;
import com.wubo.api.mapper.UserMapper;
import com.wubo.api.mapper.UserRoleMapper;
import com.wubo.api.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private RoleMapper roleMapper;

    /**
     * 用户登录
     *
     * @param user 用户信息
     * @return 登录成功的用户信息
     */
    @Override
    public User login(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());
        User existingUser = userMapper.selectOne(queryWrapper);
        if (existingUser != null) {
            // 更新 last_login 字段为当前时间
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
        // 通过用户id查询当前用户对应的角色列表
        QueryWrapper<UserRole> query = new QueryWrapper<>();
        query.eq("user_id", user.getUserId());
        List<UserRole> userRoles = userRoleMapper.selectList(query);
        List<Integer> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        List<Role> roles = roleMapper.selectByIds(roleIds);
        user.setRoles(roles);
        List<String> roleNames = roles.stream().map(Role::getRoleName).collect(Collectors.toList());
        user.setRoleNames(roleNames);
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

        // 加密密码
        String encryptedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
        user.setPassword(encryptedPassword);

        // 生成唯一 token
        user.setToken(java.util.UUID.randomUUID().toString());

        // 设置创建时间
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // 保存用户到 user 表
        int rows = userMapper.insert(user);
        if (rows > 0) {
            // 查询默认角色（假设默认角色名称是 "用户"）
            QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
            roleQueryWrapper.eq("role_name", "用户");
            Role defaultRole = roleMapper.selectOne(roleQueryWrapper);

            // 保存用户角色关联到 user_role 表
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getUserId());
            userRole.setRoleId(defaultRole.getRoleId());
            userRoleMapper.insert(userRole);

            return true;
        }
        return false;
    }


   /* @Override
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

        // 加密密码（推荐使用 BCrypt）
        String encryptedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
        user.setPassword(encryptedPassword);

        // 设置创建时间
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // 保存用户到 user 表
        int rows = userMapper.insert(user);
        if (rows > 0) {
            // 查询默认角色（假设默认角色名称是 "用户"）
            QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
            roleQueryWrapper.eq("role_name", "用户");
            Role defaultRole = roleMapper.selectOne(roleQueryWrapper);

            // 保存用户角色关联到 user_role 表
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getUserId());
            userRole.setRoleId(defaultRole.getRoleId());
            userRoleMapper.insert(userRole);

            return true;
        }
        return false;
    }

*/
    /**
     * 对密码进行加密
     * @param password 明文密码
     * @return 加密后的密码
     */
    private String encryptPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

}
