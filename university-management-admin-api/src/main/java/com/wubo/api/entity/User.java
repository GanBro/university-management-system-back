package com.wubo.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
public class User {
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    private String username;
    private String password;
    private String email;
    private String avatar;
    private String token;
    private Timestamp lastLogin;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String role; // 添加角色字段
}
