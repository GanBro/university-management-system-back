package com.wubo.api.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wubo.api.entity.University;
import com.wubo.api.entity.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UniversityMapperTest {
    @Resource
    private UserMapper userMapper;

    @Test
    void addUser() {
        User user = new User();
        user.setUsername("ganbro");
        user.setPassword("1234567");
        user.setEmail("2551921037@qq.com");
        user.setAvatar("ceshi.jpg");
        userMapper.insert(user);
    }

    @Test
    void getUser() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", "ganbro");
        queryWrapper.eq("password", "1234567");
        User user = userMapper.selectOne(queryWrapper);
        System.out.println(user);
    }
}
