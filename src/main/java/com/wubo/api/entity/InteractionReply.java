// InteractionReply.java
package com.wubo.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("interaction_reply")
public class InteractionReply {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer interactionId;
    private Integer userId;
    private String content;
    private Boolean isOfficial;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // 非数据库字段
    @TableField(exist = false)
    private String userName;
}
