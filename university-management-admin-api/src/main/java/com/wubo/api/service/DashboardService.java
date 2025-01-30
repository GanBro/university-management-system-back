package com.wubo.api.service;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    // 获取仪表盘统计信息
    Map<String, Object> getStats();

    // 获取高校增长趋势
    List<Map<String, Object>> getUniversityGrowthTrend();

    // 获取活跃度统计数据
    Map<String, Object> getActivityStats();
}
