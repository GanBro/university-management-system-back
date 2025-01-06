// src/main/java/com/wubo/api/dto/UniversityDTO.java
package com.wubo.api.dto;

import lombok.Data;

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
    private List<String> features;
    private String introduction;
    private String departments;
    private String majors;
    private String admissionRules;
    private String scholarships;
    private String accommodation;
    private String contactInfo;
}
