package com.wubo.api.controller;

import com.wubo.api.dto.Result;
import com.wubo.api.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "仪表盘接口", description = "提供仪表盘相关的 API")
public class DashboardController {

    @Resource
    private DashboardService dashboardService;

    /**
     * 获取仪表盘统计数据
     */
    @Operation(summary = "获取仪表盘统计数据", description = "获取仪表盘上的统计数据，包括高校总数、活跃用户数量等")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = dashboardService.getStats();
        log.info("获取仪表盘统计数据成功: {}", stats);
        return Result.success(stats);
    }

    /**
     * 获取高校数量增长趋势
     *
     * @return 包含年份和高校数量的列表
     */
    @Operation(summary = "获取高校数量增长趋势", description = "获取每年的高校数量增长情况")
    @GetMapping("/growth-trend")
    public Result<List<Map<String, Object>>> getGrowthTrend() {
        try {
            List<Map<String, Object>> growthTrend = dashboardService.getUniversityGrowthTrend();
            log.info("获取高校数量增长趋势数据成功");
            return Result.success(growthTrend);
        } catch (Exception e) {
            log.error("获取高校数量增长趋势失败", e);
            return Result.error("获取高校数量增长趋势失败，请稍后重试！");
        }
    }
}
