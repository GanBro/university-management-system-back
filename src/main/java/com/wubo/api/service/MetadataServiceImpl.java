package com.wubo.api.service;

import com.wubo.api.mapper.UniversityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MetadataServiceImpl implements MetadataService {

    private final UniversityMapper universityMapper;

    private static final List<String> DEFAULT_LEVELS = Arrays.asList(
            "双一流", "985", "211", "普通高校"
    );

    private static final List<String> DEFAULT_TYPES = Arrays.asList(
            "公立", "私立", "中外合作"
    );

    private static final List<String> DEFAULT_ADMIN_DEPARTMENTS = Arrays.asList(
            "教育部", "省级教育厅", "其他部委"
    );

    @Override
    @Cacheable(value = "provinces", unless = "#result == null || #result.isEmpty()")
    public List<String> getAllProvinces() {
        List<String> provinces = universityMapper.selectAllProvinces();
        return provinces != null && !provinces.isEmpty() ? provinces : Collections.emptyList();
    }

    @Override
    @Cacheable(value = "types", unless = "#result == null || #result.isEmpty()")
    public List<String> getAllTypes() {
        List<String> types = universityMapper.selectAllTypes();
        return types != null && !types.isEmpty() ? types : DEFAULT_TYPES;
    }

    @Override
    @Cacheable(value = "levels", unless = "#result == null || #result.isEmpty()")
    public List<String> getAllLevels() {
        List<String> levels = universityMapper.selectAllLevels();
        return levels != null && !levels.isEmpty() ? levels : DEFAULT_LEVELS;
    }

    @Override
    @Cacheable(value = "adminDepartments", unless = "#result == null || #result.isEmpty()")
    public List<String> getAllAdminDepartments() {
        List<String> departments = universityMapper.selectAllAdminDepartments();
        return departments != null && !departments.isEmpty() ? departments : DEFAULT_ADMIN_DEPARTMENTS;
    }
}
