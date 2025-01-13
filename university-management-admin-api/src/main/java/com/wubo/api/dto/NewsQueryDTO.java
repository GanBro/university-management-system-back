package com.wubo.api.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class NewsQueryDTO {
    private Integer page = 1;           // 当前页码，默认值为 1
    private Integer limit = 10;        // 每页数量，默认值为 10
    private Integer universityId;      // 高校ID
    private Map<String, Object> params = new HashMap<>(); // 初始化 params
}
