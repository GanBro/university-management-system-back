package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wubo.api.entity.University;
import com.wubo.api.mapper.UniversityMapper;
import com.wubo.api.service.DashboardService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DashboardServiceImpl implements DashboardService {

    @Resource
    private UniversityMapper universityMapper;

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 查询高校总数量
        long totalUniversityCount = universityMapper.selectCount(null);
        stats.put("totalUniversityCount", totalUniversityCount);

        // 查询最近30天新增高校数量
        QueryWrapper<University> recentUniversityQuery = new QueryWrapper<>();
        recentUniversityQuery.ge("created_at", getLast30Days());
        long recentUniversityCount = universityMapper.selectCount(recentUniversityQuery);
        stats.put("recentUniversityCount", recentUniversityCount);

        // 查询高校分布（按省份分组统计）
        List<Map<String, Object>> universityDistribution = universityMapper.selectUniversityDistribution();
        stats.put("universityDistribution", universityDistribution != null ? universityDistribution : List.of());

        return stats;
    }

    @Override
    public List<Map<String, Object>> getUniversityGrowthTrend() {
        QueryWrapper<University> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("YEAR(created_at) as year", "COUNT(*) as university_count");
        queryWrapper.groupBy("YEAR(created_at)");
        queryWrapper.orderByAsc("YEAR(created_at)");

        // 查询结果返回 List<Map<String, Object>>
        return universityMapper.selectMaps(queryWrapper);
    }

    /**
     * 获取当前时间30天前的日期
     * @return 格式化的日期字符串
     */
    private String getLast30Days() {
        return LocalDate.now().minusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
