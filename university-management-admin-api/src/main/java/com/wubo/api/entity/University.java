package com.wubo.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("university")
public class University {
    @TableId(type = IdType.AUTO)
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
    private Integer studentCount;
    private Integer teacherCount;
    private Integer libraryCount;
    private Integer campusArea;
    private String introduction;
    private String departments;
    private String majors;
    private String admissionRules;
    private String scholarships;
    private String accommodation;
    private String contactInfo;
}
