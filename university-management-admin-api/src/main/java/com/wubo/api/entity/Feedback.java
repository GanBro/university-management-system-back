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
@TableName("feedback") // 指定数据库表名
public class Feedback {
    @TableId(value = "id", type = IdType.AUTO) // 主键，自增
    private Long id;

    private Long universityId; // 外键，指向高校表
    private Long userId; // 外键，指向用户表
    private String feedbackText; // 用户反馈内容
}
