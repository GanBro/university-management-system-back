// Notification.java
package com.wubo.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("notification")
@Schema(description = "通知实体")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @Schema(description = "通知标题")
    private String title;

    @Schema(description = "通知内容")
    private String content;

    @Schema(description = "通知类型：system-系统通知，announcement-公告")
    private String type;

    @Schema(description = "通知状态：draft-草稿，published-已发布，archived-已归档")
    private String status;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime updatedAt;

    @Schema(description = "创建者ID")
    private Integer createdBy;

    @Schema(description = "是否删除")
    @TableLogic(value = "0", delval = "1")
    private Boolean isDeleted;
}
