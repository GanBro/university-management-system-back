package com.wubo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 大学满意度数据传输对象
 */
@Data
@Accessors(chain = true)
@Schema(description = "院校满意度DTO")
public class UniversitySatisfactionDTO {

    @Schema(description = "院校ID")
    private Integer id;

    @Schema(description = "院校名称")
    private String name;

    @Schema(description = "院校所在地")
    private String location;

    @Schema(description = "综合满意度")
    private Float overallRating;

    @Schema(description = "综合满意度投票人数")
    private Integer overallCount;

    @Schema(description = "环境满意度")
    private Float environmentRating;

    @Schema(description = "环境满意度投票人数")
    private Integer environmentCount;

    @Schema(description = "生活满意度")
    private Float lifeRating;

    @Schema(description = "生活满意度投票人数")
    private Integer lifeCount;
}
