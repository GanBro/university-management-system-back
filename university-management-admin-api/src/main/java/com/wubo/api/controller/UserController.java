package com.wubo.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.dto.Result;
import com.wubo.api.entity.User;
import com.wubo.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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

}
