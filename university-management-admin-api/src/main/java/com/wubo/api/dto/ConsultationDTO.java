package com.wubo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Schema(description = "咨询提交DTO")
public class ConsultationDTO {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    @Schema(description = "咨询标题")
    @NotBlank(message = "咨询标题不能为空")
    @Size(min = 2, max = 50, message = "标题长度必须在2-50个字符之间")
    private String title;

    @Schema(description = "咨询内容")
    @NotBlank(message = "咨询内容不能为空")
    @Size(min = 10, max = 500, message = "内容长度必须在10-500个字符之间")
    private String content;

    @Schema(description = "是否公开")
    private Boolean isPublic = true;
}
