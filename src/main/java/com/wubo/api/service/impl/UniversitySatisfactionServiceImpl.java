package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wubo.api.dto.UniversitySatisfactionDTO;
import com.wubo.api.entity.University;
import com.wubo.api.entity.UniversitySatisfaction;
import com.wubo.api.mapper.UniversityMapper;
import com.wubo.api.mapper.UniversitySatisfactionMapper;
import com.wubo.api.service.UniversitySatisfactionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 院校满意度服务实现类
 */
@Slf4j
@Service
public class UniversitySatisfactionServiceImpl extends ServiceImpl<UniversitySatisfactionMapper, UniversitySatisfaction> implements UniversitySatisfactionService {

    @Resource
    private UniversityMapper universityMapper;

    @Override
    public IPage<UniversitySatisfactionDTO> getSatisfactionPage(Page<UniversitySatisfactionDTO> page, Map<String, Object> params) {
        log.info("分页查询院校满意度, 参数: {}", params);

        // 创建大学查询的页对象
        Page<University> universityPage = new Page<>(page.getCurrent(), page.getSize());

        // 构建大学查询条件
        LambdaQueryWrapper<University> universityWrapper = Wrappers.lambdaQuery();

        // 添加名称模糊查询
        String name = (String) params.get("name");
        if (StringUtils.isNotBlank(name)) {
            universityWrapper.like(University::getName, name);
        }

        // 添加所在地筛选
        String location = (String) params.get("location");
        if (StringUtils.isNotBlank(location)) {
            universityWrapper.eq(University::getProvince, location);
        }

        // 添加主管部门筛选
        String department = (String) params.get("department");
        if (StringUtils.isNotBlank(department)) {
            universityWrapper.eq(University::getAdminDepartment, department);
        }

        // 添加办学层次筛选
        String level = (String) params.get("level");
        if (StringUtils.isNotBlank(level)) {
            universityWrapper.eq(University::getLevel, level);
        }

        // 添加特性筛选
        String feature = (String) params.get("feature");
        if (StringUtils.isNotBlank(feature)) {
            // 使用exists实现与XML中相同的效果
            universityWrapper.exists(
                    "SELECT 1 FROM university_feature uf WHERE uf.university_id = university.id AND uf.feature_name = {0}",
                    feature
            );
        }

        // 执行大学查询
        IPage<University> universityResult = universityMapper.selectPage(universityPage, universityWrapper);

        // 创建返回结果页对象
        IPage<UniversitySatisfactionDTO> resultPage = new Page<>(
                universityResult.getCurrent(),
                universityResult.getSize(),
                universityResult.getTotal()
        );

        if (universityResult.getRecords().isEmpty()) {
            resultPage.setRecords(new ArrayList<>());
            return resultPage;
        }

        // 获取所有大学ID
        List<Integer> universityIds = universityResult.getRecords().stream()
                .map(University::getId)
                .collect(Collectors.toList());

        // 批量查询满意度数据
        List<UniversitySatisfaction> satisfactions = lambdaQuery()
                .in(UniversitySatisfaction::getUniversityId, universityIds)
                .list();

        // 构建大学ID到满意度的映射
        Map<Integer, UniversitySatisfaction> satisfactionMap = satisfactions.stream()
                .collect(Collectors.toMap(UniversitySatisfaction::getUniversityId, s -> s, (s1, s2) -> s1));

        // 构建返回结果
        List<UniversitySatisfactionDTO> resultRecords = new ArrayList<>();
        for (University university : universityResult.getRecords()) {
            UniversitySatisfactionDTO dto = new UniversitySatisfactionDTO();
            dto.setId(university.getId());
            dto.setName(university.getName());
            dto.setLocation(university.getProvince());

            // 填充满意度数据
            UniversitySatisfaction satisfaction = satisfactionMap.get(university.getId());
            if (satisfaction != null) {
                dto.setOverallRating(satisfaction.getOverallRating().floatValue());
                dto.setOverallCount(satisfaction.getOverallCount());
                dto.setEnvironmentRating(satisfaction.getEnvironmentRating().floatValue());
                dto.setEnvironmentCount(satisfaction.getEnvironmentCount());
                dto.setLifeRating(satisfaction.getLifeRating().floatValue());
                dto.setLifeCount(satisfaction.getLifeCount());
            }

            resultRecords.add(dto);
        }

        // 根据综合满意度排序：先排有评分的，再按评分从高到低
        resultRecords.sort((dto1, dto2) -> {
            // 如果一个有评分，一个没有评分，有评分的排前面
            if (dto1.getOverallRating() == null && dto2.getOverallRating() != null) {
                return 1;
            }
            if (dto1.getOverallRating() != null && dto2.getOverallRating() == null) {
                return -1;
            }
            // 如果都有评分，按评分从高到低
            if (dto1.getOverallRating() != null && dto2.getOverallRating() != null) {
                return Float.compare(dto2.getOverallRating(), dto1.getOverallRating());
            }
            // 如果都没有评分，按ID排序
            return dto1.getId().compareTo(dto2.getId());
        });

        resultPage.setRecords(resultRecords);
        return resultPage;
    }

    @Override
    public UniversitySatisfactionDTO getSatisfactionByUniversityId(Integer universityId) {
        log.info("查询院校满意度详情, universityId: {}", universityId);

        // 查询大学基本信息
        University university = universityMapper.selectById(universityId);
        if (university == null) {
            log.warn("未找到大学信息, universityId: {}", universityId);
            return null;
        }

        // 使用LambdaQueryWrapper查询满意度数据
        UniversitySatisfaction satisfaction = lambdaQuery()
                .eq(UniversitySatisfaction::getUniversityId, universityId)
                .one();

        // 构建返回DTO
        UniversitySatisfactionDTO dto = new UniversitySatisfactionDTO();
        dto.setId(university.getId());
        dto.setName(university.getName());
        dto.setLocation(university.getProvince());

        // 填充满意度数据
        if (satisfaction != null) {
            dto.setOverallRating(satisfaction.getOverallRating().floatValue());
            dto.setOverallCount(satisfaction.getOverallCount());
            dto.setEnvironmentRating(satisfaction.getEnvironmentRating().floatValue());
            dto.setEnvironmentCount(satisfaction.getEnvironmentCount());
            dto.setLifeRating(satisfaction.getLifeRating().floatValue());
            dto.setLifeCount(satisfaction.getLifeCount());
        }

        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateSatisfaction(UniversitySatisfaction satisfaction) {
        log.info("保存或更新院校满意度, universityId: {}", satisfaction.getUniversityId());

        if (satisfaction.getUniversityId() == null) {
            throw new IllegalArgumentException("大学ID不能为空");
        }

        // 验证大学是否存在
        University university = universityMapper.selectById(satisfaction.getUniversityId());
        if (university == null) {
            log.warn("大学不存在, universityId: {}", satisfaction.getUniversityId());
            throw new IllegalArgumentException("大学不存在");
        }

        // 查询是否已存在
        UniversitySatisfaction existingSatisfaction = lambdaQuery()
                .eq(UniversitySatisfaction::getUniversityId, satisfaction.getUniversityId())
                .one();

        if (existingSatisfaction != null) {
            // 更新
            satisfaction.setId(existingSatisfaction.getId());
            return updateById(satisfaction);
        } else {
            // 新增
            return save(satisfaction);
        }
    }
}
