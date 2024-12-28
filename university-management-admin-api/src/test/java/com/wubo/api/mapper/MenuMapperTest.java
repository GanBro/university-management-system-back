package com.wubo.api.mapper;

import com.wubo.api.entity.Menu;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
