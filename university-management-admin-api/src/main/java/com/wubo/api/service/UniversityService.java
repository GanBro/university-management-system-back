package com.wubo.api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wubo.api.entity.University;
import com.wubo.api.mapper.UniversityMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UniversityService extends ServiceImpl<UniversityMapper, University> {

    // 获取所有高校
    public List<University> getAllUniversities() {
        return list();
    }

    // 根据 ID 获取高校信息
    public University getUniversityById(Long id) {
        University university = getById(id);
        if (university == null) {
            throw new RuntimeException("University with ID " + id + " not found.");
        }
        return university;
    }

    // 根据省份查询高校
    public List<University> getUniversitiesByProvince(String province) {
        QueryWrapper<University> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("province", province);
        return list(queryWrapper);
    }

    // 模糊查询高校名称
    public List<University> searchUniversitiesByName(String keyword) {
        QueryWrapper<University> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", keyword);
        return list(queryWrapper);
    }

    // 新增高校信息
    public University saveUniversity(University university) {
        if (save(university)) {
            return university;
        } else {
            throw new RuntimeException("Failed to save university");
        }
    }

    // 新增或更新高校信息
    public boolean saveOrUpdateUniversity(University university) {
        return saveOrUpdate(university);
    }

    // 删除高校信息
    public void deleteUniversity(Long id) {
        if (!removeById(id)) {
            throw new RuntimeException("Failed to delete university with id: " + id);
        }
    }
}
