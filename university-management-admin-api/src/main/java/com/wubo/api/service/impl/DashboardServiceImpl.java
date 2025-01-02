package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wubo.api.entity.University;
import com.wubo.api.entity.User;
import com.wubo.api.mapper.UniversityMapper;
import com.wubo.api.mapper.UserMapper;
import com.wubo.api.service.DashboardService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class DashboardServiceImpl implements DashboardService {

    @Resource
    private UniversityMapper universityMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 查询总高校数量
        long totalUniversityCount = universityMapper.selectCount(null);
        stats.put("totalUniversityCount", totalUniversityCount);

        // 查询最近30天新增高校数量
        QueryWrapper<University> recentUniversityQuery = new QueryWrapper<>();
        recentUniversityQuery.ge("created_at", getThirtyDaysAgo());
        long recentUniversityCount = universityMapper.selectCount(recentUniversityQuery);
        stats.put("recentUniversityCount", recentUniversityCount);

        // 查询最近7天活跃用户数量
        QueryWrapper<User> activeUserQuery = new QueryWrapper<>();
        activeUserQuery.ge("last_login", getSevenDaysAgo());
        long activeUserCount = userMapper.selectCount(activeUserQuery);
        stats.put("activeUserCount", activeUserCount);

        // 查询高校分布（按省份分组统计）
        List<Map<String, Object>> universityDistribution = universityMapper.selectUniversityDistribution();
        stats.put("universityDistribution", universityDistribution);

        return stats;
    }

    /**
     * 获取当前时间 30 天前的时间
     */
    private String getThirtyDaysAgo() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return thirtyDaysAgo.format(formatter);
    }

    /**
     * 获取当前时间 7 天前的时间
     */
    private String getSevenDaysAgo() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return sevenDaysAgo.format(formatter);
    }
}
