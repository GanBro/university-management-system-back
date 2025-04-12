package com.wubo.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wubo.api.dto.ConsultationDTO;
import com.wubo.api.dto.UniversityDTO;
import com.wubo.api.dto.UniversityDetailDTO;
import com.wubo.api.dto.UniversityExportDTO;
import com.wubo.api.entity.SatisfactionRating;
import com.wubo.api.entity.University;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UniversityService extends IService<University> {

    /**
     * 获取高校列表
     * @param page 页码
     * @param limit 每页数量
     * @param params 查询参数
     * @return 分页结果
     */
    Page<University> getUniversityList(Integer page, Integer limit, Map<String, Object> params);

    /**
     * 获取高校详情
     * @param id 高校ID
     * @return 高校详情
     */
    UniversityDetailDTO getUniversityDetail(Integer id);

    /**
     * 创建高校
     * @param universityDTO 高校数据
     */
    void createUniversity(UniversityDTO universityDTO);

    /**
     * 更新高校
     * @param universityDTO 高校数据
     */
    void updateUniversity(UniversityDTO universityDTO);

    /**
     * 删除高校
     * @param id 高校ID
     */
    void deleteUniversity(Integer id);

    /**
     * 批量删除高校
     * @param ids 高校ID集合
     */
    void batchDeleteUniversities(List<Integer> ids);

    /**
     * 获取导出数据
     * @param params 查询参数
     * @param fields 导出字段
     * @return 导出数据
     */
    List<UniversityExportDTO> getExportData(Map<String, Object> params, List<String> fields);

    /**
     * 获取所有高校类型
     * @return 类型列表
     */
    List<String> getAllTypes();

    /**
     * 获取所有高校层次
     * @return 层次列表
     */
    List<String> getAllLevels();

    /**
     * 获取所有高校所在省份
     * @return 省份列表
     */
    List<String> getAllProvinces();

    /**
     * 获取招生统计信息
     * @param universityId 高校ID
     * @param year 年份
     * @return 统计信息
     */
    Map<String, Integer> getAdmissionStats(Integer universityId, Integer year);

    /**
     * 获取院校满意度数据
     * @param universityId 高校ID
     * @return 满意度数据
     */
    Map<String, Object> getSatisfactionData(Integer universityId);

    /**
     * 获取专业满意度数据
     * @param universityId 高校ID
     * @return 专业满意度数据
     */
    List<Map<String, Object>> getMajorSatisfaction(Integer universityId);

    /**
     * 获取专业推荐数据
     * @param universityId 高校ID
     * @return 专业推荐数据
     */
    Map<String, Object> getRecommendationData(Integer universityId);

    /**
     * 获取咨询列表
     * @param universityId 高校ID
     * @return 咨询列表
     */
    List<Map<String, Object>> getConsultations(Integer universityId);

    /**
     * 提交咨询
     * @param universityId 高校ID
     * @param consultationDTO 咨询内容
     */
    void submitConsultation(Integer universityId, ConsultationDTO consultationDTO);

    /**
     * 搜索高校
     * @param keyword 关键词
     * @param limit 限制数量
     * @return 高校列表
     */
    List<Map<String, Object>> searchUniversities(String keyword, int limit);

    /**
     * 获取用户关注的高校导出数据
     * @param userId 用户ID
     * @param fields 导出的字段列表
     * @return 导出数据列表
     */
    List<UniversityExportDTO> getFollowedUniversitiesExportData(Integer userId, List<String> fields);

    /**
     * 获取招生数据
     * @param universityId 高校ID
     * @return 招生数据列表
     */
    List<Map<String, Object>> getAdmissionData(Integer universityId);
}
