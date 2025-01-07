package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.dto.UniversityDTO;
import com.wubo.api.dto.UniversityDetailDTO;
import com.wubo.api.dto.UniversityExportDTO;
import com.wubo.api.entity.University;
import com.wubo.api.entity.UniversityFeature;
import com.wubo.api.mapper.UniversityFeatureMapper;
import com.wubo.api.mapper.UniversityMapper;
import com.wubo.api.service.UniversityService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class UniversityServiceImpl implements UniversityService {

    @Autowired
    private UniversityMapper universityMapper;

    @Autowired
    private UniversityFeatureMapper universityFeatureMapper;

    @Override
    public Page<University> getUniversityList(Integer page, Integer limit, Map<String, Object> params) {
        Page<University> pageParam = new Page<>(page, limit);
        LambdaQueryWrapper<University> queryWrapper = new LambdaQueryWrapper<>();

        String name = (String) params.get("name");
        String province = (String) params.get("province");
        String type = (String) params.get("type");
        String level = (String) params.get("level");

        if (StringUtils.hasText(name)) {
            queryWrapper.like(University::getName, name);
        }
        if (StringUtils.hasText(province)) {
            queryWrapper.eq(University::getProvince, province);
        }
        if (StringUtils.hasText(type)) {
            queryWrapper.eq(University::getType, type);
        }
        if (StringUtils.hasText(level)) {
            queryWrapper.eq(University::getLevel, level);
        }

        queryWrapper.orderByDesc(University::getId);
        return universityMapper.selectPage(pageParam, queryWrapper);
    }

    @Override
    public UniversityDetailDTO getUniversityDetail(Integer id) {
        UniversityDetailDTO result = universityMapper.selectUniversityDetail(id);
        if (result != null) {
            // 获取特性标签
            LambdaQueryWrapper<UniversityFeature> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UniversityFeature::getUniversityId, id);
            List<String> features = universityFeatureMapper.selectList(wrapper)
                    .stream()
                    .map(UniversityFeature::getFeatureName)
                    .collect(Collectors.toList());
            result.setFeatures(features);
        }
        return result;
    }

    @Override
    @Transactional
    public void createUniversity(UniversityDTO universityDTO) {
        // 保存基本信息
        University university = new University();
        BeanUtils.copyProperties(universityDTO, university);
        universityMapper.insert(university);

        // 保存特性标签
        saveFeatures(university.getId(), universityDTO.getFeatures());
    }

    @Override
    @Transactional
    public void updateUniversity(UniversityDTO universityDTO) {
        // 更新基本信息
        University university = new University();
        BeanUtils.copyProperties(universityDTO, university);
        universityMapper.updateById(university);

        // 更新特性标签
        LambdaQueryWrapper<UniversityFeature> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UniversityFeature::getUniversityId, university.getId());
        universityFeatureMapper.delete(wrapper);
        saveFeatures(university.getId(), universityDTO.getFeatures());
    }

    private void saveFeatures(Integer universityId, List<String> features) {
        if (features != null && !features.isEmpty()) {
            features.forEach(featureName -> {
                UniversityFeature feature = new UniversityFeature();
                feature.setUniversityId(universityId);
                feature.setFeatureName(featureName);
                universityFeatureMapper.insert(feature);
            });
        }
    }

    @Override
    @Transactional
    public void deleteUniversity(Integer id) {
        universityMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void batchDeleteUniversities(List<Integer> ids) {
        universityMapper.deleteBatchIds(ids);
    }

    @Override
    public List<UniversityExportDTO> getExportData(Map<String, Object> params, List<String> fields) {
        LambdaQueryWrapper<University> queryWrapper = new LambdaQueryWrapper<>();

        String name = (String) params.get("name");
        String province = (String) params.get("province");
        String type = (String) params.get("type");
        String level = (String) params.get("level");

        if (StringUtils.hasText(name)) {
            queryWrapper.like(University::getName, name);
        }
        if (StringUtils.hasText(province)) {
            queryWrapper.eq(University::getProvince, province);
        }
        if (StringUtils.hasText(type)) {
            queryWrapper.eq(University::getType, type);
        }
        if (StringUtils.hasText(level)) {
            queryWrapper.eq(University::getLevel, level);
        }

        List<University> universities = universityMapper.selectList(queryWrapper);

        return universities.stream().map(university -> {
            UniversityExportDTO exportDTO = new UniversityExportDTO();
            BeanUtils.copyProperties(university, exportDTO);

            // 获取特性标签并转换为字符串
            LambdaQueryWrapper<UniversityFeature> featureWrapper = new LambdaQueryWrapper<>();
            featureWrapper.eq(UniversityFeature::getUniversityId, university.getId());
            List<String> featureList = universityFeatureMapper.selectList(featureWrapper)
                    .stream()
                    .map(UniversityFeature::getFeatureName)
                    .collect(Collectors.toList());

            // 将特性列表转换为逗号分隔的字符串
            exportDTO.setFeatures(String.join("、", featureList));

            return exportDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<String> getAllTypes() {
        return universityMapper.selectAllTypes();
    }

    @Override
    public List<String> getAllLevels() {
        return universityMapper.selectAllLevels();
    }

    @Override
    public List<String> getAllProvinces() {
        return universityMapper.selectAllProvinces();
    }

    @Override
    public Map<String, Integer> getAdmissionStats(Integer universityId, Integer year) {
        return universityMapper.selectAdmissionStats(universityId, year);
    }
}
