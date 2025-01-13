package com.wubo.api.dto;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public class UniversityQueryDTO {
    private Integer page;
    private Integer limit;
    private String name;
    private String province;
    private String type;
    private String level;
    private String adminDepartment;
}
