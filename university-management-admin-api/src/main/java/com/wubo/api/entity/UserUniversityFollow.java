package com.wubo.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_university_follow")
public class UserUniversityFollow {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;
    private Integer universityId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
