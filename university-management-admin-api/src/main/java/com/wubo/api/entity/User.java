// src/main/java/com/wubo/api/entity/User.java
package com.wubo.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
public class User {
    @TableId(value = "user_id", type = IdType.AUTO) // 主键，自增
    private Integer userId;

    private String username;
    private String password;
    private String email;
    private String avatar;
    private String token;
    private Timestamp lastLogin;
    @TableField(exist = false)
    private List<Role> roles;
    @TableField(exist = false)
    private List<String> roleNames; // 给前端发送
}
