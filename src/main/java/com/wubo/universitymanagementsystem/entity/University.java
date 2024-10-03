package com.wubo.universitymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

// 使用Lombok的@Data注解自动生成getters和setters
@Data
@TableName("universities") // 指定对应数据库表名
public class University {

    @TableId(type = IdType.AUTO) // ID自动生成
    private Long id;

    private String name; // 高校名称
    private String province; // 所在省份
    private String city; // 所在城市
    private String address; // 详细地址
    private String adminDepartment; // 教育主管部门
    private String website; // 官方网站
    private String admissionWebsite; // 招生网站
    private String contactNumber; // 联系电话
    private String specialFeatures; // 学校特性

    // 所有字段的getter和setter将通过Lombok自动生成
}
