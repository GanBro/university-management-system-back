package com.wubo.api.dto;

import lombok.Data;

@Data
public class MessageLogQueryDTO {
    private Integer page = 1;       // 当前页码，默认值为 1
    private Integer limit = 10;    // 每页数量，默认值为 10
    private Integer userId;        // 用户ID
    private String status;         // 消息状态
}
