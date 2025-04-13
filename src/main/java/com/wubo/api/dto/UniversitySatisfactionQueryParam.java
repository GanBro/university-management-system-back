package com.wubo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 满意度查询DTO
 */
@Data
@Accessors(chain = true)
@Schema(description = "院校满意度查询参数")
class UniversitySatisfactionQueryParam {

    @Schema(description = "院校名称")
    private String name;

    @Schema(description = "院校所在地")
    private String location;

    @Schema(description = "主管部门")
    private String department;

    @Schema(description = "办学层次")
    private String level;

    @Schema(description = "院校特性")
    private String feature;

    @Schema(description = "页码")
    private Integer page;

    @Schema(description = "每页条数")
    private Integer limit;
}
