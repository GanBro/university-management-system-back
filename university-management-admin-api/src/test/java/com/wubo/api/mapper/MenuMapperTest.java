package com.wubo.api.mapper;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MenuMapperTest {

    @Resource
    private MenuMapper menuMapper;

    @Test
    void getMenuList() {
        List<Menu> menuList = menuMapper.getMenuList();
        System.out.println(menuList);
    }

}
