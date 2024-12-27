package com.wubo.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user") // 对应数据库表名
public class User {
    @TableId(value = "user_id", type = IdType.AUTO) // 主键，自增
    private Long userId;

    private String username;
    private String password;
    private String email;
    private String avatar;
    private String token;
    @TableField(exist = false)
    private List<Role> roles;
}
