package com.wubo.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wubo.api.entity.Menu;

import java.util.List;

public interface MenuMapper extends BaseMapper<Menu> {
    /**
     * 查询菜单树
     * @return
     */
    List<Menu> getMenuList();

    List<Menu> getSubMenuList(Integer parentId);
}
