package com.wubo.api.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wubo.api.entity.UserRole;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRoleMapperTest {

    @Resource
    private UserRoleMapper userRoleMapper;

    @Test
    void getUserRoleList() {
        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", 1);
        userRoleMapper.selectList(queryWrapper).forEach(System.out::println);
    }

}
