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

    Page<University> getUniversityList(Integer page, Integer limit, Map<String, Object> params);

    UniversityDetailDTO getUniversityDetail(Integer id);

    void createUniversity(UniversityDTO universityDTO);

    void updateUniversity(UniversityDTO universityDTO);

    void deleteUniversity(Integer id);

    void batchDeleteUniversities(List<Integer> ids);

    List<UniversityExportDTO> getExportData(Map<String, Object> params, List<String> fields);

    List<String> getAllTypes();
    List<String> getAllLevels();
    List<String> getAllProvinces();
    // 新增获取招生统计信息的方法
    Map<String, Integer> getAdmissionStats(Integer universityId, Integer year);

    // 获取院校满意度数据
    Map<String, Object> getSatisfactionData(Integer universityId);

    // 获取专业满意度数据
    List<Map<String, Object>> getMajorSatisfaction(Integer universityId);

    // 获取专业推荐数据
    Map<String, Object> getRecommendationData(Integer universityId);

    // 获取咨询列表
    List<Map<String, Object>> getConsultations(Integer universityId);

    // 提交咨询
    void submitConsultation(Integer universityId, ConsultationDTO consultationDTO);

    List<Map<String, Object>> searchUniversities(String keyword, int limit);
    /**
     * 获取用户关注的高校导出数据
     * @param userId 用户ID
     * @param fields 导出的字段列表
     * @return 导出数据列表
     */
    List<UniversityExportDTO> getFollowedUniversitiesExportData(Integer userId, List<String> fields);
}
