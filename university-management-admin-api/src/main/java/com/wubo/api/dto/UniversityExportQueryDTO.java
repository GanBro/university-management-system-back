package com.wubo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "高校导出查询条件数据传输对象")
public class UniversityExportQueryDTO {

    @Schema(description = "高校名称")
    private String name;

    @Schema(description = "所在省份")
    private String province;

    @Schema(description = "办学类型")
    private String type;

    @Schema(description = "办学层次", allowableValues = {"双一流", "985", "211", "普通高校"})
    private String level;

    @Schema(description = "主管部门")
    private String adminDepartment;

    @Schema(description = "导出字段列表，多个字段用逗号分隔",
            example = "name,province,type,level,studentCount")
    @Pattern(regexp = "^[a-zA-Z]+(,[a-zA-Z]+)*$",
            message = "字段列表格式不正确，应为英文字段名，多个字段用逗号分隔")
    private String fields;

    @Schema(description = "开始时间", example = "2024-01-01")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "开始时间格式不正确，应为yyyy-MM-dd")
    private String startTime;

    @Schema(description = "结束时间", example = "2024-12-31")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "结束时间格式不正确，应为yyyy-MM-dd")
    private String endTime;

    @Schema(description = "排序字段")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "排序字段只能包含英文字母")
    private String sortField;

    @Schema(description = "排序方向", allowableValues = {"asc", "desc"})
    @Pattern(regexp = "^(asc|desc)$", message = "排序方向只能是asc或desc")
    private String sortOrder;
}
