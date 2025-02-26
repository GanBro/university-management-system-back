package com.wubo.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("admin_log") // 指定数据库表名
public class AdminLog {
    @TableId(value = "id", type = IdType.AUTO) // 主键，自增
    private Integer id;

    private Integer adminId; // 外键，指向管理员
    private String action; // 操作内容
    private LocalDateTime actionTime; // 操作时间
}
