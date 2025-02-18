package com.wubo.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wubo.api.entity.AdminLog;

import java.util.List;
import java.util.Map;

public interface AdminLogService extends IService<AdminLog> {
    /**
     * 记录管理员操作
     */
    void logAdminAction(Integer adminId, String action);

    /**
     * 获取指定时间范围内的操作日志
     */
    Page<Map<String, Object>> getAdminLogs(String startTime, String endTime);

    /**
     * 获取操作统计数据
     */
    List<Map<String, Object>> getAdminActionStats(String startTime, String endTime);

    /**
     * 获取管理员最近的操作日志
     */
    List<Map<String, Object>> getRecentLogs(Integer adminId, Integer limit);
}
