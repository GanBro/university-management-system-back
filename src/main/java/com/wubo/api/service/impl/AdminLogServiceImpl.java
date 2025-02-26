package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wubo.api.entity.AdminLog;
import com.wubo.api.entity.User;
import com.wubo.api.mapper.AdminLogMapper;
import com.wubo.api.mapper.UserMapper;
import com.wubo.api.service.AdminLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminLogServiceImpl extends ServiceImpl<AdminLogMapper, AdminLog> implements AdminLogService {

    private final UserMapper userMapper;

    @Override
    @Transactional
    public void logAdminAction(Integer adminId, String action) {
        AdminLog adminLog = new AdminLog();
        adminLog.setAdminId(adminId);
        adminLog.setAction(action);
        adminLog.setActionTime(LocalDateTime.now());

        try {
            save(adminLog);
            log.info("记录管理员操作日志成功: adminId={}, action={}", adminId, action);
        } catch (Exception e) {
            log.error("记录管理员操作日志失败: adminId={}, action={}", adminId, action, e);
            throw e;
        }
    }

    @Override
    public Page<Map<String, Object>> getAdminLogs(String startTime, String endTime) {
        // 创建分页对象 - 使用默认的页码(1)和大小(10)
        Page<AdminLog> pageParam = new Page<>(1, 10);

        // 构建查询条件
        LambdaQueryWrapper<AdminLog> wrapper = new LambdaQueryWrapper<AdminLog>()
                .ge(startTime != null, AdminLog::getActionTime, LocalDateTime.parse(startTime))
                .le(endTime != null, AdminLog::getActionTime, LocalDateTime.parse(endTime))
                .orderByDesc(AdminLog::getActionTime);

        // 执行分页查询
        Page<AdminLog> logPage = page(pageParam, wrapper);

        // 转换结果，添加管理员信息
        Page<Map<String, Object>> resultPage = new Page<>(
                logPage.getCurrent(),
                logPage.getSize(),
                logPage.getTotal()
        );

        List<Map<String, Object>> records = logPage.getRecords().stream().map(log -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", log.getId());
            map.put("action", log.getAction());
            map.put("actionTime", log.getActionTime());

            // 获取管理员信息
            User admin = userMapper.selectById(log.getAdminId());
            if (admin != null) {
                map.put("adminName", admin.getUsername());
            }

            return map;
        }).collect(Collectors.toList());

        resultPage.setRecords(records);
        return resultPage;
    }

    @Override
    public List<Map<String, Object>> getAdminActionStats(String startTime, String endTime) {
        // 构建查询条件
        LambdaQueryWrapper<AdminLog> wrapper = new LambdaQueryWrapper<AdminLog>()
                .ge(startTime != null, AdminLog::getActionTime, LocalDateTime.parse(startTime))
                .le(endTime != null, AdminLog::getActionTime, LocalDateTime.parse(endTime));

        // 查询所有记录
        List<AdminLog> logs = list(wrapper);

        // 使用 Stream API 进行统计
        return logs.stream()
                .collect(Collectors.groupingBy(
                        AdminLog::getAction,
                        Collectors.collectingAndThen(
                                Collectors.counting(),
                                count -> {
                                    Map<String, Object> stat = new HashMap<>();
                                    stat.put("action", count);
                                    stat.put("count", count);
                                    return stat;
                                }
                        )
                ))
                .values()
                .stream()
                .sorted((a, b) -> Long.compare(
                        (Long) b.get("count"),
                        (Long) a.get("count")
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getRecentLogs(Integer adminId, Integer limit) {
        // 构建查询条件
        LambdaQueryWrapper<AdminLog> wrapper = new LambdaQueryWrapper<AdminLog>()
                .eq(AdminLog::getAdminId, adminId)
                .orderByDesc(AdminLog::getActionTime)
                .last("LIMIT " + limit);

        // 执行查询
        List<AdminLog> logs = list(wrapper);

        // 转换结果
        return logs.stream().map(log -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", log.getId());
            map.put("action", log.getAction());
            map.put("actionTime", log.getActionTime());
            return map;
        }).collect(Collectors.toList());
    }
}
