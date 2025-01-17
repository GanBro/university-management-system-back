package com.wubo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "互动DTO")
public class InteractionDTO {
    @Schema(description = "高校ID")
    @NotNull(message = "高校ID不能为空")
    private Integer universityId;

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    @Schema(description = "标题")
    @NotBlank(message = "标题不能为空")
    private String title;

    @Schema(description = "内容")
    @NotBlank(message = "内容不能为空")
    private String content;

    @Schema(description = "是否公开")
    private Boolean isPublic = true;
}
