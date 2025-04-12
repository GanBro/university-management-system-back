package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wubo.api.dto.ConsultationDTO;
import com.wubo.api.dto.UniversityDTO;
import com.wubo.api.dto.UniversityDetailDTO;
import com.wubo.api.dto.UniversityExportDTO;
import com.wubo.api.entity.*;
import com.wubo.api.mapper.InteractionMapper;
import com.wubo.api.mapper.UniversityFeatureMapper;
import com.wubo.api.mapper.UniversityMapper;
import com.wubo.api.mapper.UserUniversityFollowMapper;
import com.wubo.api.service.UniversityService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class UniversityServiceImpl extends ServiceImpl<UniversityMapper, University> implements UniversityService {

    @Autowired
    private UniversityMapper universityMapper;

    @Autowired
    private UniversityFeatureMapper universityFeatureMapper;

    @Autowired
    private InteractionMapper interactionMapper;

    @Autowired
    private UserUniversityFollowMapper userUniversityFollowMapper;

    @Override
    public Page<University> getUniversityList(Integer page, Integer limit, Map<String, Object> params) {
        Page<University> pageParam = new Page<>(page, limit);

        LambdaQueryWrapper<University> queryWrapper = new LambdaQueryWrapper<>();

        String name = (String) params.get("name");
        String province = (String) params.get("province");
        String type = (String) params.get("type");
        String level = (String) params.get("level");
        String adminDepartment = (String) params.get("adminDepartment");

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
        if (StringUtils.hasText(adminDepartment)) {
            queryWrapper.eq(University::getAdminDepartment, adminDepartment);
        }

        queryWrapper.orderByDesc(University::getId);
        Page<University> result = universityMapper.selectPage(pageParam, queryWrapper);

        // 更新分页信息，使用新的API
        long total = result.getRecords().size();
        result.setTotal(total);
        // 不再需要手动设置pages，Page对象会自动计算

        return result;
    }

    @Override
    @Cacheable(value = "universityDetail", key = "#id", unless = "#result == null")
    public UniversityDetailDTO getUniversityDetail(Integer id) {
        // 1. 获取基本信息
        UniversityDetailDTO result = universityMapper.selectUniversityDetail(id);

        if (result != null) {
            // 2. 获取特性标签
            List<String> features = universityMapper.selectUniversityFeatures(id);
            result.setFeatures(features);

            // 3. 获取招生数据
            List<Admission> admissions = universityMapper.selectUniversityAdmissions(id);
            result.setAdmissionScores(admissions);

            // 4. 获取满意度评价
            List<SatisfactionRating> satisfactionRatings = universityMapper.selectUniversitySatisfactionRatings(id);
            result.setSatisfactionRatings(satisfactionRatings);

            // 5. 获取推荐评级
            List<RecommendationRating> recommendationRatings = universityMapper.selectUniversityRecommendationRatings(id);
            result.setRecommendationRatings(recommendationRatings);
        }

        return result;
    }

    @Override
    @Transactional
    @CacheEvict(value = "universityDetail", key = "#universityDTO.id")
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
    @CacheEvict(value = "universityDetail", key = "#universityDTO.id")
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
    @CacheEvict(value = "universityDetail", key = "#id")
    public void deleteUniversity(Integer id) {
        universityMapper.deleteById(id);
    }

    @Override
    @Transactional
    @CacheEvict(value = "universityDetail", allEntries = true)
    public void batchDeleteUniversities(List<Integer> ids) {
        // 使用新的批量删除API
        LambdaQueryWrapper<University> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(University::getId, ids);
        universityMapper.delete(queryWrapper);
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

            // 获取特性标签
            List<String> featureList = universityMapper.selectUniversityFeatures(university.getId());

            // 将特性列表转换为逗号分隔的字符串
            exportDTO.setFeatures(String.join("、", featureList));

            return exportDTO;
        }).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "universityOptions", key = "'types'")
    public List<String> getAllTypes() {
        return universityMapper.selectAllTypes();
    }

    @Override
    @Cacheable(value = "universityOptions", key = "'levels'")
    public List<String> getAllLevels() {
        return universityMapper.selectAllLevels();
    }

    @Override
    @Cacheable(value = "universityOptions", key = "'provinces'")
    public List<String> getAllProvinces() {
        return universityMapper.selectAllProvinces();
    }

    @Override
    public Map<String, Integer> getAdmissionStats(Integer universityId, Integer year) {
        return universityMapper.selectAdmissionStats(universityId, year);
    }

    @Override
    @Cacheable(value = "satisfactionData", key = "#universityId", unless = "#result == null")
    public Map<String, Object> getSatisfactionData(Integer universityId) {
        List<SatisfactionRating> satisfactionList = universityMapper.selectSatisfactionData(universityId);
        Map<String, Object> result = new HashMap<>();

        // 处理综合满意度数据
        double overall = 0.0;
        int overallCount = 0;

        // 处理环境满意度数据
        double environment = 0.0;
        int environmentCount = 0;

        // 处理生活满意度数据
        double life = 0.0;
        int lifeCount = 0;

        for (SatisfactionRating rating : satisfactionList) {
            switch(rating.getCategory()) {
                case "综合评价":
                    overall = rating.getRating();
                    overallCount = rating.getRatingCount();
                    break;
                case "环境":
                    environment = rating.getRating();
                    environmentCount = rating.getRatingCount();
                    break;
                case "生活":
                    life = rating.getRating();
                    lifeCount = rating.getRatingCount();
                    break;
            }
        }

        result.put("overall", overall);
        result.put("overall_count", overallCount);
        result.put("environment", environment);
        result.put("environment_count", environmentCount);
        result.put("life", life);
        result.put("life_count", lifeCount);

        return result;
    }

    @Override
    @Cacheable(value = "majorSatisfaction", key = "#universityId", unless = "#result == null")
    public List<Map<String, Object>> getMajorSatisfaction(Integer universityId) {
        return universityMapper.selectMajorSatisfaction(universityId);
    }

    @Override
    @Cacheable(value = "recommendationData", key = "#universityId", unless = "#result == null")
    public Map<String, Object> getRecommendationData(Integer universityId) {
        Map<String, Object> result = new HashMap<>();
        result.put("counts", universityMapper.selectRecommendationCounts(universityId));
        result.put("index", universityMapper.selectRecommendationIndex(universityId));
        return result;
    }

    @Override
    public List<Map<String, Object>> getConsultations(Integer universityId) {
        return universityMapper.selectConsultations(universityId);
    }

    @Override
    @Transactional
    public void submitConsultation(Integer universityId, ConsultationDTO consultationDTO) {
        // 封装交互实体
        Interaction interaction = new Interaction();
        interaction.setUniversityId(universityId);
        interaction.setUserId(consultationDTO.getUserId());
        interaction.setTitle(consultationDTO.getTitle());
        interaction.setContent(consultationDTO.getContent());
        interaction.setStatus("pending");
        interaction.setIsPublic(consultationDTO.getIsPublic());

        // 手动设置时间
        interaction.setCreatedAt(LocalDateTime.now()); // 设置创建时间
        interaction.setUpdatedAt(LocalDateTime.now()); // 设置更新时间

        // 保存交互记录
        interactionMapper.insert(interaction);

        // 如果需要,这里可以添加发送通知等其他逻辑
    }

    @Override
    public List<Map<String, Object>> searchUniversities(String keyword, int limit) {
        return universityMapper.selectMaps(
                new QueryWrapper<University>()
                        .select("id", "name")
                        .like(StringUtils.hasText(keyword), "name", keyword)
                        .orderByAsc("name")
                        .last("LIMIT " + limit)
        );
    }

    @Override
    public List<UniversityExportDTO> getFollowedUniversitiesExportData(Integer userId, List<String> fields) {
        // 查询用户关注的高校ID列表
        LambdaQueryWrapper<UserUniversityFollow> followWrapper = new LambdaQueryWrapper<>();
        followWrapper.eq(UserUniversityFollow::getUserId, userId);
        List<UserUniversityFollow> follows = userUniversityFollowMapper.selectList(followWrapper);

        if (follows.isEmpty()) {
            return Collections.emptyList();
        }

        // 提取高校ID列表
        List<Integer> universityIds = follows.stream()
                .map(UserUniversityFollow::getUniversityId)
                .collect(Collectors.toList());

        // 查询这些高校的详细信息
        LambdaQueryWrapper<University> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(University::getId, universityIds);

        List<University> universities = universityMapper.selectList(queryWrapper);

        // 转换为导出DTO
        return universities.stream().map(university -> {
            UniversityExportDTO exportDTO = new UniversityExportDTO();
            BeanUtils.copyProperties(university, exportDTO);

            // 获取特性标签
            List<String> featureList = universityMapper.selectUniversityFeatures(university.getId());

            // 将特性列表转换为逗号分隔的字符串
            exportDTO.setFeatures(String.join("、", featureList));

            return exportDTO;
        }).collect(Collectors.toList());
    }

    // 获取招生数据
    @Override
    @Cacheable(value = "admissionData", key = "#universityId", unless = "#result == null")
    public List<Map<String, Object>> getAdmissionData(Integer universityId) {
        return universityMapper.selectAdmissionData(universityId);
    }
}
