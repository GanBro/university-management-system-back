package com.wubo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "互动查询DTO")
public class InteractionQueryDTO {
    @Schema(description = "页码")
    private Integer page = 1;

    @Schema(description = "每页数量")
    private Integer limit = 10;

    @Schema(description = "高校ID")
    private Integer universityId;

    @Schema(description = "用户ID")
    private Integer userId;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "是否公开")
    private Boolean isPublic;

    @Schema(description = "关键词")
    private String keyword;
}
