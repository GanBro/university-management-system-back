package com.wubo.api.dto;

import lombok.Data;

@Data
public class NotificationQueryDTO {
    private Integer page = 1;       // 当前页码，默认值为 1
    private Integer limit = 10;    // 每页数量，默认值为 10
    private String type;           // 通知类型
    private String status;         // 通知状态
}
