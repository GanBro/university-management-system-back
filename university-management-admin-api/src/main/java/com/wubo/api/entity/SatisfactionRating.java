package com.wubo.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("satisfaction_rating") // 指定数据库表名
public class SatisfactionRating {

    @TableId(value = "id", type = IdType.AUTO) // 主键，自增
    private Long id;

    @TableField("university_id") // 外键字段
    private Long universityId;

    @TableField("category") // 评分类别
    private String category;

    @TableField("rating") // 评分
    private double rating;

    @TableField("rating_count") // 评分人数
    private int ratingCount;
}
