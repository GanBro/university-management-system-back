package com.wubo.api.entity;

import lombok.Data;
@Data
public class UpdatePasswordRequest {
    private Integer userId; // 添加 userId 字段
    private String oldPassword;
    private String newPassword;
}
