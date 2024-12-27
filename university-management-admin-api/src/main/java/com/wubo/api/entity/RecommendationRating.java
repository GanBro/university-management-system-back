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
@TableName("recommendation_rating") // 指定数据库表名
public class RecommendationRating {
    @TableId(value = "id", type = IdType.AUTO) // 主键，自增
    private Long id;

    private Long universityId; // 外键，指向高校表
    private String majorCategory; // 专业类别
    private double rating; // 推荐指数
    private int recommendationCount; // 推荐人数
}
