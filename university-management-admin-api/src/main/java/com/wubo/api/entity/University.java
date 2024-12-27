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
@TableName("universities") // 指定对应的数据库表名
public class University {
    @TableId(value = "id", type = IdType.AUTO) // 指定主键策略
    private Long id;

    private String name;
    private String province;
    private String city;
    private String address;
    private String adminDepartment;
    private String website;
    private String admissionWebsite;
    private String contactNumber;
    private String type;
    private String level;
}
