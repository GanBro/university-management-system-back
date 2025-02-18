package com.wubo.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.dto.UniversityDetailDTO;
import com.wubo.api.entity.SatisfactionRating;
import com.wubo.api.entity.University;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UniversityMapper extends BaseMapper<University> {

    UniversityDetailDTO selectUniversityDetail(@Param("id") Integer id);

    List<Map<String, Object>> selectUniversityGrowthTrend();

    List<Map<String, Object>> selectUniversityDistribution();

    List<String> selectAllTypes();

    List<String> selectAllLevels();

    List<String> selectAllProvinces();

    List<String> selectAllAdminDepartments();

    Map<String, Integer> selectAdmissionStats(@Param("universityId") Integer universityId,
                                              @Param("year") Integer year);

    List<SatisfactionRating> selectSatisfactionData(@Param("universityId") Integer universityId);

    List<Map<String, Object>> selectMajorSatisfaction(@Param("universityId") Integer universityId);

    List<Map<String, Object>> selectRecommendationCounts(@Param("universityId") Integer universityId);

    List<Map<String, Object>> selectRecommendationIndex(@Param("universityId") Integer universityId);

    List<Map<String, Object>> selectConsultations(@Param("universityId") Integer universityId);
}
