// MessageLog.java
package com.wubo.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("message_log")
@Schema(description = "消息日志实体")
public class MessageLog {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @Schema(description = "通知ID")
    private Integer notificationId;

    @Schema(description = "用户ID")
    private Integer userId;

    @Schema(description = "消息状态: unread-未读, read-已读")
    private String status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime updatedAt;
}
