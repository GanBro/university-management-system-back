package com.wubo.api.service.impl;

import com.wubo.api.entity.Menu;
import com.wubo.api.mapper.MenuMapper;
import com.wubo.api.service.MenuService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class MenuServiceImpl implements MenuService {

    @Resource
    private MenuMapper menuMapper;

    @Override
    public List<Menu> getMenuTree() {
        return menuMapper.getMenuList();
    }
}
