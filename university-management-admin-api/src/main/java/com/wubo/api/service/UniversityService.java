package com.wubo.api.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wubo.api.dto.UniversityDTO;
import com.wubo.api.dto.UniversityDetailDTO;
import com.wubo.api.dto.UniversityExportDTO;
import com.wubo.api.entity.University;
import com.wubo.api.mapper.UniversityMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UniversityService extends ServiceImpl<UniversityMapper, University> {

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

    public UniversityDetailDTO getUniversityDetail(Integer id) {
        // 通过 Mapper 获取详细信息，包括关联数据
        return baseMapper.selectUniversityDetail(id);
    }

    @Transactional
    public void createUniversity(UniversityDTO universityDTO) {
        University university = new University();
        BeanUtils.copyProperties(universityDTO, university);
        save(university);
    }

    @Transactional
    public void updateUniversity(UniversityDTO universityDTO) {
        University university = new University();
        BeanUtils.copyProperties(universityDTO, university);
        updateById(university);
    }

    @Transactional
    public void deleteUniversity(Integer id) {
        removeById(id);
    }

    @Transactional
    public void batchDeleteUniversities(List<Integer> ids) {
        removeBatchByIds(ids);
    }

    // 保留原有方法
    public List<University> getAllUniversities() {
        return list();
    }

    public University getUniversityById(Integer id) {
        University university = getById(id);
        if (university == null) {
            throw new RuntimeException("University with ID " + id + " not found.");
        }
        return university;
    }

    public List<University> getUniversitiesByProvince(String province) {
        LambdaQueryWrapper<University> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(University::getProvince, province);
        return list(queryWrapper);
    }

    public List<University> searchUniversitiesByName(String keyword) {
        LambdaQueryWrapper<University> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(University::getName, keyword);
        return list(queryWrapper);
    }
    public List<UniversityExportDTO> getExportData(Map<String, Object> params, List<String> fields) {
        // 查询数据
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

        // 获取数据列表
        List<University> universities = list(queryWrapper);

        // 转换为导出DTO
        return universities.stream().map(university -> {
            UniversityExportDTO exportDTO = new UniversityExportDTO();
            BeanUtils.copyProperties(university, exportDTO);
            return exportDTO;
        }).collect(Collectors.toList());
    }
}
