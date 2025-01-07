package com.wubo.api.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UniversityExportDTO {
    @ExcelProperty("高校名称")
    private String name;

    @ExcelProperty("省份")
    private String province;

    @ExcelProperty("城市")
    private String city;

    @ExcelProperty("类型")
    private String type;

    @ExcelProperty("层次")
    private String level;

    @ExcelProperty("地址")
    private String address;

    @ExcelProperty("主管部门")
    private String adminDepartment;

    @ExcelProperty("学校网站")
    private String website;

    @ExcelProperty("招生网站")
    private String admissionWebsite;

    @ExcelProperty("联系电话")
    private String contactNumber;

    @ExcelProperty("特色标签")
    private String features;
}
