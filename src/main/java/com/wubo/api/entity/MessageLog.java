// MessageLog.java
package com.wubo.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("message_log")
public class MessageLog {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer notificationId;
    private Integer userId;
    private String status;
    private Date createdAt;
}
