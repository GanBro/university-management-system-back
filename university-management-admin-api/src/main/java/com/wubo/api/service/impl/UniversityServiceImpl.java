package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wubo.api.dto.UniversityDTO;
import com.wubo.api.dto.UniversityDetailDTO;
import com.wubo.api.dto.UniversityExportDTO;
import com.wubo.api.entity.University;
import com.wubo.api.mapper.UniversityMapper;
import com.wubo.api.service.UniversityService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UniversityServiceImpl extends ServiceImpl<UniversityMapper, University> implements UniversityService {

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
        return page(pageParam, queryWrapper);
    }

    @Override
    public UniversityDetailDTO getUniversityDetail(Integer id) {
        return baseMapper.selectUniversityDetail(id);
    }

    @Override
    @Transactional
    public void createUniversity(UniversityDTO universityDTO) {
        University university = new University();
        BeanUtils.copyProperties(universityDTO, university);
        save(university);
    }

    @Override
    @Transactional
    public void updateUniversity(UniversityDTO universityDTO) {
        University university = new University();
        BeanUtils.copyProperties(universityDTO, university);
        updateById(university);
    }

    @Override
    @Transactional
    public void deleteUniversity(Integer id) {
        removeById(id);
    }

    @Override
    @Transactional
    public void batchDeleteUniversities(List<Integer> ids) {
        removeBatchByIds(ids);
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

        List<University> universities = list(queryWrapper);

        return universities.stream().map(university -> {
            UniversityExportDTO exportDTO = new UniversityExportDTO();
            BeanUtils.copyProperties(university, exportDTO);
            return exportDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<String> getAllTypes() {
        return baseMapper.selectAllTypes();
    }

    @Override
    public List<String> getAllLevels() {
        return baseMapper.selectAllLevels();
    }

    @Override
    public List<String> getAllProvinces() {
        return baseMapper.selectAllProvinces();
    }
}
