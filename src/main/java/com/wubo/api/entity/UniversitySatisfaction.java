package com.wubo.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 院校满意度实体类
 */
@Data
@Accessors(chain = true)
@TableName("university_satisfaction")
public class UniversitySatisfaction {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 大学ID
     */
    private Integer universityId;

    /**
     * 综合满意度
     */
    private BigDecimal overallRating;

    /**
     * 综合满意度投票人数
     */
    private Integer overallCount;

    /**
     * 环境满意度
     */
    private BigDecimal environmentRating;

    /**
     * 环境满意度投票人数
     */
    private Integer environmentCount;

    /**
     * 生活满意度
     */
    private BigDecimal lifeRating;

    /**
     * 生活满意度投票人数
     */
    private Integer lifeCount;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
