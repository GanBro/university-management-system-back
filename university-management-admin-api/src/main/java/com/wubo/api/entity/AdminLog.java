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
@TableName("admin_logs") // 指定数据库表名
public class AdminLog {
    @TableId(value = "id", type = IdType.AUTO) // 主键，自增
    private Long id;

    private Long adminId; // 外键，指向管理员
    private String action; // 操作内容
    private String actionTime; // 操作时间
}
