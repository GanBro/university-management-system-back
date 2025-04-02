package com.wubo.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("message_log")
public class MessageLog {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer notificationId;
    private Integer userId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
