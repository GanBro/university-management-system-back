package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

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
        return baseMapper.selectSatisfactionPage(page, params);
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

        // 查询满意度数据
        UniversitySatisfaction satisfaction = baseMapper.selectByUniversityId(universityId);

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
        LambdaQueryWrapper<UniversitySatisfaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UniversitySatisfaction::getUniversityId, satisfaction.getUniversityId());
        UniversitySatisfaction existingSatisfaction = baseMapper.selectOne(wrapper);

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
