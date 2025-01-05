package com.wubo.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.dto.Result;
import com.wubo.api.entity.User;
import com.wubo.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping
@Tag(name = "用户管理接口", description = "提供用户相关的登录、退出、信息查询等 API")
public class UserController {

    @Resource
    private UserService userService;

    @Operation(summary = "用户登录接口", description = "通过用户名和密码进行登录认证")
    @PostMapping(value = "/login")
    public Result<User> login(@RequestBody User user) {
        User login = userService.login(user);
        if (Objects.isNull(login)) {
            return Result.error(500, "登录失败(用户名或密码错误)");
        } else {
            return Result.success(login);
        }
    }

    @Operation(summary = "根据 Token 获取用户信息", description = "通过 Token 获取用户的详细信息和菜单数据")
    @GetMapping("/getUserByToken")
    public Result<Map<String, Object>> generateToken(String token) {
        System.out.println("接收到的 Token: " + token);
        User user = userService.getUserByToken(token);
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        if (user == null) {
            return Result.error(401, "Token 无效或已过期");
        }
        return Result.success(map);
    }

    @Operation(summary = "用户注册接口", description = "通过提交用户名、密码等信息进行注册")
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        try {
            boolean isRegistered = userService.register(user);
            if (isRegistered) {
                return Result.success("注册成功");
            } else {
                return Result.error(400, "用户名已存在");
            }
        } catch (Exception e) {
            return Result.error(500, "注册失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取用户列表")
    @GetMapping("/users")
    public Result<Page<User>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role
    ) {
        Page<User> pageParam = new Page<>(page, limit);
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("email", email);
        params.put("role", role);

        return Result.success(userService.getUserList(pageParam, params));
    }

    @Operation(summary = "创建新用户", description = "创建一个新的用户账号")
    @PostMapping("/users")
    public Result<?> create(@RequestBody User user) {
        try {
            // 检查必填字段
            if (!StringUtils.hasText(user.getUsername())) {
                return Result.error(400, "用户名不能为空");
            }
            if (!StringUtils.hasText(user.getPassword())) {
                return Result.error(400, "密码不能为空");
            }
            if (!StringUtils.hasText(user.getEmail())) {
                return Result.error(400, "邮箱不能为空");
            }

            // 检查用户名是否已存在
            User existingUser = userService.getUserByUsername(user.getUsername());
            if (existingUser != null) {
                return Result.error(400, "用户名已存在");
            }

            // 设置默认值
            user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            user.setToken(java.util.UUID.randomUUID().toString());

            // 如果没有指定角色，设置默认角色
            if (!StringUtils.hasText(user.getRole())) {
                user.setRole("user");
            }

            // 保存用户
            boolean success = userService.createUser(user);
            if (success) {
                return Result.success("用户创建成功");
            } else {
                return Result.error(500, "用户创建失败");
            }
        } catch (Exception e) {
            return Result.error(500, "创建用户时发生错误: " + e.getMessage());
        }
    }

}
