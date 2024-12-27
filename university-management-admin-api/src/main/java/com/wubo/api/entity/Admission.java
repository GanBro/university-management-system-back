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
@TableName("admission") // 指定数据库表名
public class Admission {
    @TableId(value = "id", type = IdType.AUTO) // 主键，自增
    private Long id;

    private Long universityId; // 外键，指向高校表
    private String province; // 省份
    private int year; // 年份
    private int scoreRequired; // 录取分数线
}
