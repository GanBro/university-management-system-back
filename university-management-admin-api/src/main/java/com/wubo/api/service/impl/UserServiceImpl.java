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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public User findByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectOne(queryWrapper);
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
}
