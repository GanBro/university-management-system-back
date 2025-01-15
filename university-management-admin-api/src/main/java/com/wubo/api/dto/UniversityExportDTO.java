package com.wubo.api.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "高校导出数据传输对象")
public class UniversityExportDTO {

    @ExcelProperty("高校名称")
    @ColumnWidth(20)
    @Schema(description = "高校名称")
    private String name;

    @ExcelProperty("省份")
    @ColumnWidth(15)
    @Schema(description = "所在省份")
    private String province;

    @ExcelProperty("地址")
    @ColumnWidth(40)
    @Schema(description = "详细地址")
    private String address;

    @ExcelProperty("主管部门")
    @ColumnWidth(15)
    @Schema(description = "主管部门")
    private String adminDepartment;

    @ExcelProperty("学校网站")
    @ColumnWidth(30)
    @Schema(description = "学校官网")
    private String website;

    @ExcelProperty("招生网站")
    @ColumnWidth(30)
    @Schema(description = "招生网站")
    private String admissionWebsite;

    @ExcelProperty("联系电话")
    @ColumnWidth(15)
    @Schema(description = "联系电话")
    private String contactNumber;

    @ExcelProperty("办学类型")
    @ColumnWidth(10)
    @Schema(description = "办学类型")
    private String type;

    @ExcelProperty("办学层次")
    @ColumnWidth(10)
    @Schema(description = "办学层次")
    private String level;

    @ExcelProperty("在校生数量")
    @ColumnWidth(15)
    @Schema(description = "在校生数量")
    private Integer studentCount;

    @ExcelProperty("教师数量")
    @ColumnWidth(15)
    @Schema(description = "教师数量")
    private Integer teacherCount;

    @ExcelProperty("图书馆藏书量")
    @ColumnWidth(15)
    @Schema(description = "图书馆藏书量")
    private Integer libraryCount;

    @ExcelProperty("校园面积(平方米)")
    @ColumnWidth(20)
    @Schema(description = "校园面积(平方米)")
    private Double campusArea;

    @ExcelProperty("特色标签")
    @ColumnWidth(30)
    @Schema(description = "特色标签")
    private String features;

    @ExcelProperty("创建时间")
    @ColumnWidth(20)
    @Schema(description = "创建时间")
    private String createdAt;

    @ExcelProperty("更新时间")
    @ColumnWidth(20)
    @Schema(description = "更新时间")
    private String updatedAt;
}
