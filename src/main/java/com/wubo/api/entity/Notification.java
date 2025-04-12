// Notification.java - 添加额外字段
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

    @Schema(description = "通知类型：system-系统通知，user-用户通知，broadcast-广播通知")
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

    @Schema(description = "优先级：0-普通，1-重要，2-紧急")
    private Integer priority;

    @Schema(description = "目标类型：all-所有用户，roles-指定角色，users-指定用户")
    private String targetType;

    @Schema(description = "目标值：角色ID列表或用户ID列表")
    private String targetValues;

    @Schema(description = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime publishTime;

    @Schema(description = "是否删除")
    @TableLogic(value = "0", delval = "1")
    private Boolean isDeleted;

    @TableField(exist = false)
    @Schema(description = "阅读状态：unread-未读，read-已读")
    private String readStatus;

    @TableField(exist = false)
    @Schema(description = "未读数量")
    private Integer unreadCount;
}
