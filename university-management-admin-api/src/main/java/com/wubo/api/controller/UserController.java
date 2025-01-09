package com.wubo.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.dto.Result;
import com.wubo.api.entity.User;
import com.wubo.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;
import java.awt.*;
import java.util.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
@Tag(name = "用户管理接口", description = "提供用户相关的登录、退出、信息查询等 API")
public class UserController {

    @Resource
    private UserService userService;

    @Operation(summary = "用户登录接口", description = "通过用户名和密码进行登录认证")
    @PostMapping(value = "/login")
    public Result<User> login(@RequestBody User user) {
        log.info("用户登录请求: {}", user.getUsername());
        User login = userService.login(user);
        if (Objects.isNull(login)) {
            log.warn("用户登录失败: {}", user.getUsername());
            return Result.error(500, "登录失败(用户名或密码错误)");
        } else {
            log.info("用户登录成功: {}", user.getUsername());
            return Result.success(login);
        }
    }

    @Operation(summary = "根据 Token 获取用户信息", description = "通过 Token 获取用户的详细信息和菜单数据")
    @GetMapping("/getUserByToken")
    public Result<Map<String, Object>> generateToken(String token) {
        log.info("接收到的 Token: {}", token);
        User user = userService.getUserByToken(token);
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        if (user == null) {
            log.warn("Token无效: {}", token);
            return Result.error(401, "Token 无效或已过期");
        }
        log.info("Token验证成功, 用户: {}", user.getUsername());
        return Result.success(map);
    }

    @Operation(summary = "用户注册接口", description = "通过提交用户名、密码等信息进行注册")
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        log.info("用户注册请求: {}", user.getUsername());
        try {
            boolean isRegistered = userService.register(user);
            if (isRegistered) {
                log.info("用户注册成功: {}", user.getUsername());
                return Result.success("注册成功");
            } else {
                log.warn("用户注册失败，用户名已存在: {}", user.getUsername());
                return Result.error(400, "用户名已存在");
            }
        } catch (Exception e) {
            log.error("用户注册异常: {}", user.getUsername(), e);
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
        log.info("获取用户列表, 页码: {}, 每页数量: {}, 用户名: {}, 邮箱: {}, 角色: {}",
                page, limit, username, email, role);
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
        log.info("创建新用户请求: {}", user.getUsername());
        try {
            // 检查必填字段
            if (!StringUtils.hasText(user.getUsername())) {
                log.warn("创建用户失败：用户名为空");
                return Result.error(400, "用户名不能为空");
            }
            if (!StringUtils.hasText(user.getPassword())) {
                log.warn("创建用户失败：密码为空");
                return Result.error(400, "密码不能为空");
            }
            if (!StringUtils.hasText(user.getEmail())) {
                log.warn("创建用户失败：邮箱为空");
                return Result.error(400, "邮箱不能为空");
            }

            // 检查用户名是否已存在
            User existingUser = userService.getUserByUsername(user.getUsername());
            if (existingUser != null) {
                log.warn("创建用户失败：用户名已存在: {}", user.getUsername());
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
                log.info("用户创建成功: {}", user.getUsername());
                return Result.success("用户创建成功");
            } else {
                log.warn("用户创建失败: {}", user.getUsername());
                return Result.error(500, "用户创建失败");
            }
        } catch (Exception e) {
            log.error("创建用户时发生错误: {}", user.getUsername(), e);
            return Result.error(500, "创建用户时发生错误: " + e.getMessage());
        }
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/users/{userId}")
    public Result<?> delete(@PathVariable Integer userId) {
        log.info("删除用户请求, userId: {}", userId);
        if (userId == null) {
            log.warn("删除用户失败：用户ID为空");
            return Result.error(400, "用户ID不能为空");
        }
        try {
            userService.deleteUser(userId);
            log.info("用户删除成功, userId: {}", userId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("删除用户失败, userId: {}", userId, e);
            return Result.error(500, "删除用户失败: " + e.getMessage());
        }
    }

    @Operation(summary = "批量删除用户", description = "批量删除多个用户")
    @DeleteMapping("/users/batch")
    public Result<?> batchDelete(@RequestBody List<Integer> userIds) {
        log.info("批量删除用户请求, userIds: {}", userIds);
        try {
            userService.batchDeleteUsers(userIds);
            log.info("批量删除用户成功, userIds: {}", userIds);
            return Result.success(null);
        } catch (IllegalArgumentException e) {
            log.warn("批量删除用户参数错误: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (RuntimeException e) {
            log.error("批量删除用户运行时错误", e);
            return Result.error(500, e.getMessage());
        } catch (Exception e) {
            log.error("批量删除用户失败", e);
            return Result.error(500, "批量删除用户失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/users/{userId}")
    public Result<User> getUserDetail(@PathVariable Integer userId) {
        log.info("获取用户详情请求, userId: {}", userId);
        if (userId == null) {
            log.warn("获取用户详情失败：用户ID为空");
            return Result.error(400, "用户ID不能为空");
        }
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                log.warn("获取用户详情失败：用户不存在, userId: {}", userId);
                return Result.error(404, "用户不存在");
            }
            log.info("获取用户详情成功, userId: {}", userId);
            return Result.success(user);
        } catch (Exception e) {
            log.error("获取用户详情失败, userId: {}", userId, e);
            return Result.error(500, "获取用户详情失败: " + e.getMessage());
        }
    }
}
