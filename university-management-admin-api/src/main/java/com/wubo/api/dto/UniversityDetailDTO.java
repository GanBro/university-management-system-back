// src/main/java/com/wubo/api/dto/UniversityDetailDTO.java
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
}
