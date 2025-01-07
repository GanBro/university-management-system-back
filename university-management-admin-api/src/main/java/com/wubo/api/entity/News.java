// News.java
package com.wubo.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("news")
public class News {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String title;
    private String content;
    private String type;
    private Integer status;
    private LocalDateTime publishTime;
    private Integer viewCount;
    private String author;
    private Integer universityId;  // 新增字段

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // 关联大学信息（非数据库字段）
    @TableField(exist = false)
    private University university;
}
