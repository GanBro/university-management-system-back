package com.wubo.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

public interface MetadataService {
    /**
     * 获取所有省份列表
     */
    List<String> getAllProvinces();

    /**
     * 获取所有类型列表
     */
    List<String> getAllTypes();

    /**
     * 获取所有办学层次
     */
    List<String> getAllLevels();

    /**
     * 获取主管部门列表
     */
    List<String> getAllAdminDepartments();
}
