package com.wubo.api.service;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    /**
     * 获取仪表盘统计数据
     * 包括：高校总数、近期新增高校数、活跃用户数、互动数据等
     */
    Map<String, Object> getStats();

    /**
     * 获取高校数量增长趋势
     * 返回按年份统计的高校数量数据
     */
    List<Map<String, Object>> getUniversityGrowthTrend();
}
