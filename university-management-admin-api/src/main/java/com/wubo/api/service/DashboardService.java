package com.wubo.api.service;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    Map<String, Object> getStats();

    // 新增方法：获取高校增长趋势
    List<Map<String, Object>> getUniversityGrowthTrend();
}
