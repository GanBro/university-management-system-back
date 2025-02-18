// src/main/java/com/wubo/api/dto/UniversityDTO.java
package com.wubo.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UniversityDTO {
    private Integer id;
    private String name;
    private String province;
    private String address;
    private String adminDepartment;
    private String website;
    private String admissionWebsite;
    private String contactNumber;
    private String type;
    private String level;
    // 新增字段
    private Integer studentCount;
    private Integer teacherCount;
    private Integer libraryCount;
    private Integer campusArea;
    // 现有字段
    private String introduction;
    private String departments;
    private String majors;
    private String admissionRules;
    private String scholarships;
    private String accommodation;
    private String contactInfo;
    // 特性列表
    private List<String> features;
}
