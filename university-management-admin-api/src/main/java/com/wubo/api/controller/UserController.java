package com.wubo.api.controller;
import com.wubo.api.dto.Result;
import com.wubo.api.entity.Menu;
import com.wubo.api.entity.User;
import com.wubo.api.service.MenuService;
import com.wubo.api.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private MenuService menuService;

    @PostMapping(value = "/login")
    public Result<User> login(@RequestBody User user) {
        User login = userService.login(user);
        if (Objects.isNull(login)) {
            // 登录失败返回错误
            return Result.error(500, "登录失败(用户名或密码错误)");
        } else {
            return Result.success(login);
        }

    }

    @GetMapping("/getUserByToken")
    public Result<Map<String, Object>> generateToken(String token) {
        System.out.println("接收到的 Token: " + token);
        User user = userService.getUserByToken(token);
        List<Menu> menus = menuService.getMenuTree();
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("menus", menus);
        if (user == null) {
            return Result.error(401, "Token 无效或已过期");
        }
        return Result.success(map);
    }


    /**
     * 用户退出登录接口
     */
    @PostMapping("/logout")
    public Result<?> logout() {
        // 前端清除 token 即可，后端可以选择记录 token 无效
        return Result.success("成功退出登录", null);
    }
}
