package com.wubo.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdmissionDTO {
    private Integer id;

    private Integer universityId;

    @NotBlank(message = "省份不能为空")
    private String province;

    @NotNull(message = "年份不能为空")
    private Integer year;

    @NotNull(message = "最低分数线不能为空")
    @Min(value = 0, message = "最低分数线不能小于0")
    private Integer scoreRequired;

    private Integer planCount;

    private Integer actualCount;

    private Integer lowestRank;
}
