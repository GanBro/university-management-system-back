package com.wubo.api.mapper;

import com.wubo.api.entity.Role;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RoleMapperTest {

    @Resource
    private RoleMapper roleMapper;

    @Test
    void addRole() {
        Role role = new Role();
        role.setRoleName("平台管理员");
        roleMapper.insert(role);

    }

    @Test
    void getRole() {
        Role role = roleMapper.selectById(1);
        System.out.println(role);
    }

}
