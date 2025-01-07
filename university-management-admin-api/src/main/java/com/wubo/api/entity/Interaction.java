package com.wubo.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("interaction")
public class Interaction {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer universityId;
    private Integer userId;
    private String type;
    private String title;
    private String content;
    private String status;
    private Boolean isPublic;
    private Integer viewCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // 非数据库字段
    @TableField(exist = false)
    private String universityName;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private List<InteractionReply> replies;
}
