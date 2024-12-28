package com.wubo.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wubo.api.handler.MetaTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 菜单实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("menu") // 指定对应的数据库表名
public class Menu {

    @TableId(value = "menu_id", type = IdType.AUTO) // 指定主键字段和自增策略
    private Integer menuId; // 菜单ID, 主键自增
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name; // 路由名
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String redirect; // 重定向地址
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String component; // 组件路径
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String path; // 路由地址
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer parentId; // 父ID (默认为0)
    @TableField(typeHandler = MetaTypeHandler.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Meta meta;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Menu> children;
}
