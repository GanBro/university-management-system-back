package com.wubo.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("university_admin_mapping")
public class UniversityAdminMapping {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer universityId;

    @TableField(exist = false)
    private String universityName;
}
