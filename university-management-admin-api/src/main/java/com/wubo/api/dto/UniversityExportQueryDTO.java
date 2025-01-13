package com.wubo.api.dto;

import lombok.Data;

@Data
public class UniversityExportQueryDTO {
    private String name;         // 高校名称
    private String province;     // 高校所在省份
    private String type;         // 高校类型
    private String level;        // 高校级别
    private String fields;       // 导出的字段列表（逗号分隔）
}
