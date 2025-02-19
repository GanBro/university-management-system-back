package com.wubo.api.controller;

import com.wubo.api.dto.Result;
import com.wubo.api.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Operation(summary = "获取仪表盘统计数据")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            Map<String, Object> stats = dashboardService.getStats(startDate, endDate);
            log.info("获取仪表盘统计数据成功");
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取仪表盘统计数据失败", e);
            return Result.error("获取统计数据失败，请稍后重试");
        }
    }

    @Operation(summary = "获取高校数量增长趋势")
    @GetMapping("/growth-trend")
    public Result<List<Map<String, Object>>> getGrowthTrend(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            List<Map<String, Object>> growthTrend = dashboardService.getUniversityGrowthTrend(startDate, endDate);
            log.info("获取高校数量增长趋势数据成功");
            return Result.success(growthTrend);
        } catch (Exception e) {
            log.error("获取高校数量增长趋势失败", e);
            return Result.error("获取增长趋势失败，请稍后重试");
        }
    }
}
