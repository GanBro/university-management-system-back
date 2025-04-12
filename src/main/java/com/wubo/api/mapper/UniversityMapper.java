package com.wubo.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.dto.UniversityDetailDTO;
import com.wubo.api.entity.Admission;
import com.wubo.api.entity.RecommendationRating;
import com.wubo.api.entity.SatisfactionRating;
import com.wubo.api.entity.University;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UniversityMapper extends BaseMapper<University> {

    /**
     * 查询大学基础信息(不包含关联表)
     * @param id 大学ID
     * @return 大学详细信息DTO
     */
    UniversityDetailDTO selectUniversityDetail(@Param("id") Integer id);

    /**
     * 查询大学特性
     * @param universityId 大学ID
     * @return 特性列表
     */
    List<String> selectUniversityFeatures(@Param("universityId") Integer universityId);

    /**
     * 查询大学招生信息
     * @param universityId 大学ID
     * @return 招生信息列表
     */
    List<Admission> selectUniversityAdmissions(@Param("universityId") Integer universityId);

    /**
     * 查询大学满意度评价
     * @param universityId 大学ID
     * @return 满意度评价列表
     */
    List<SatisfactionRating> selectUniversitySatisfactionRatings(@Param("universityId") Integer universityId);

    /**
     * 查询大学推荐评级
     * @param universityId 大学ID
     * @return 推荐评级列表
     */
    List<RecommendationRating> selectUniversityRecommendationRatings(@Param("universityId") Integer universityId);

    /**
     * 查询高校数量增长趋势
     * @return 增长趋势数据
     */
    List<Map<String, Object>> selectUniversityGrowthTrend();

    /**
     * 查询高校地域分布
     * @return 分布数据
     */
    List<Map<String, Object>> selectUniversityDistribution();

    /**
     * 查询所有高校类型
     * @return 类型列表
     */
    List<String> selectAllTypes();

    /**
     * 查询所有高校层次
     * @return 层次列表
     */
    List<String> selectAllLevels();

    /**
     * 查询所有高校所在省份
     * @return 省份列表
     */
    List<String> selectAllProvinces();

    /**
     * 查询所有主管部门
     * @return 主管部门列表
     */
    List<String> selectAllAdminDepartments();

    /**
     * 查询招生统计信息
     * @param universityId 大学ID
     * @param year 年份
     * @return 统计信息
     */
    Map<String, Integer> selectAdmissionStats(@Param("universityId") Integer universityId,
                                              @Param("year") Integer year);

    /**
     * 查询满意度数据
     * @param universityId 大学ID
     * @return 满意度数据
     */
    List<SatisfactionRating> selectSatisfactionData(@Param("universityId") Integer universityId);

    /**
     * 查询专业满意度
     * @param universityId 大学ID
     * @return 专业满意度数据
     */
    List<Map<String, Object>> selectMajorSatisfaction(@Param("universityId") Integer universityId);

    /**
     * 查询专业推荐人数
     * @param universityId 大学ID
     * @return 推荐人数数据
     */
    List<Map<String, Object>> selectRecommendationCounts(@Param("universityId") Integer universityId);

    /**
     * 查询专业推荐指数
     * @param universityId 大学ID
     * @return 推荐指数数据
     */
    List<Map<String, Object>> selectRecommendationIndex(@Param("universityId") Integer universityId);

    /**
     * 查询咨询列表
     * @param universityId 大学ID
     * @return 咨询列表
     */
    List<Map<String, Object>> selectConsultations(@Param("universityId") Integer universityId);

    /**
     * 查询招生数据
     * @param universityId 大学ID
     * @return 招生数据列表
     */
    List<Map<String, Object>> selectAdmissionData(@Param("universityId") Integer universityId);
}
