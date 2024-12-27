package com.wubo.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("users") // 对应数据库表名
public class User {
    @TableId(value = "id", type = IdType.AUTO) // 主键，自增
    private Long id;

    private String username;
    private String password;
    private String role; // 角色（ADMIN/USER）
    private String email;
    private String avatar;
    private String token;
}
