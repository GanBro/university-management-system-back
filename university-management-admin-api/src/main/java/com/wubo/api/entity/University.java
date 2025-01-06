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
    @TableId(value = "id", type = IdType.AUTO)
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
    private String introduction;    // 学校简介
    private String departments;     // 院系设置
    private String majors;         // 专业介绍
    private String admissionRules; // 录取规则
    private String scholarships;   // 奖学金设置
    private String accommodation;  // 食宿条件
    private String contactInfo;    // 联系办法
}
