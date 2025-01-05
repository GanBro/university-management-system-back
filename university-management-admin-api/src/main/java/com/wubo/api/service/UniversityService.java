package com.wubo.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wubo.api.dto.UniversityDTO;
import com.wubo.api.dto.UniversityDetailDTO;
import com.wubo.api.dto.UniversityExportDTO;
import com.wubo.api.entity.University;

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

    // 新增的获取选项方法
    List<String> getAllTypes();
    List<String> getAllLevels();
    List<String> getAllProvinces();
}
