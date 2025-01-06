package com.wubo.api.dto;

import com.wubo.api.entity.Admission;
import com.wubo.api.entity.SatisfactionRating;
import com.wubo.api.entity.RecommendationRating;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UniversityDetailDTO extends UniversityDTO {
    private List<Admission> admissionScores;
    private List<SatisfactionRating> satisfactionRatings;
    private List<RecommendationRating> recommendationRatings;
    private List<String> features;
    private String introduction;    // 学校简介
    private String departments;     // 院系设置
    private String majors;         // 专业介绍
    private String admissionRules; // 录取规则
    private String scholarships;   // 奖学金设置
    private String accommodation;  // 食宿条件
    private String contactInfo;    // 联系办法
}
