package com.wubo.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("admission")
public class Admission {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer universityId;

    private String province;

    private Integer year;

    private Integer scoreRequired;

    private Integer planCount;

    private Integer actualCount;

    private Integer lowestRank;
}
