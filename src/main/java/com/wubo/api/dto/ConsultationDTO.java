package com.wubo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "咨询提交数据传输对象")
public class ConsultationDTO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private Integer userId;

    @NotNull(message = "高校ID不能为空")
    @Schema(description = "高校ID")
    private Integer universityId;

    @NotBlank(message = "咨询标题不能为空")
    @Size(min = 5, max = 50, message = "标题长度必须在5-50个字符之间")
    @Schema(description = "咨询标题")
    private String title;

    @NotBlank(message = "咨询内容不能为空")
    @Size(min = 10, max = 500, message = "内容长度必须在10-500个字符之间")
    @Schema(description = "咨询内容")
    private String content;

    @Schema(description = "咨询类型", allowableValues = {"consult", "feedback", "message", "alumni"})
    private String type = "consult";

    @Schema(description = "是否公开", defaultValue = "true")
    private Boolean isPublic = true;

    @Schema(description = "提交时间", hidden = true)
    private String createTime;

    @Schema(description = "更新时间", hidden = true)
    private String updateTime;
}
