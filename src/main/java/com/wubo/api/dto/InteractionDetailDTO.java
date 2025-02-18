package com.wubo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

// InteractionDetailDTO.java
@Data
@Schema(description = "互动详情DTO")
public class InteractionDetailDTO {
    @Schema(description = "互动ID")
    private Integer id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "是否公开")
    private Boolean isPublic;

    @Schema(description = "浏览次数")
    private Integer viewCount;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "用户ID")
    private Integer userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "大学ID")
    private Integer universityId;

    @Schema(description = "大学名称")
    private String universityName;

    @Schema(description = "回复列表")
    private List<InteractionReplyDTO> replies;
}
