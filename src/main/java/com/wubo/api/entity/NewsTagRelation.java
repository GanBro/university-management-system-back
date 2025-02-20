package com.wubo.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("news_tag_relation")
public class NewsTagRelation {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer newsId;
    private Integer tagId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
} 