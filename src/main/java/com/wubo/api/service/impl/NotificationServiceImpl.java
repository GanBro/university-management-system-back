// NotificationServiceImpl.java
package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wubo.api.entity.MessageLog;
import com.wubo.api.entity.Notification;
import com.wubo.api.entity.User;
import com.wubo.api.mapper.MessageLogMapper;
import com.wubo.api.mapper.NotificationMapper;
import com.wubo.api.mapper.UserMapper;
import com.wubo.api.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Autowired
    private MessageLogMapper messageLogMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Page<Notification> getNotificationList(Integer page, Integer limit, String type, String status) {
        Page<Notification> pageParam = new Page<>(page, limit);

        // 使用LambdaQueryWrapper构建查询条件
        LambdaQueryWrapper<Notification> queryWrapper = new LambdaQueryWrapper<>();

        // 只添加不为空的条件
        queryWrapper.eq(StringUtils.hasText(type), Notification::getType, type)
                .eq(StringUtils.hasText(status), Notification::getStatus, status)
                .orderByDesc(Notification::getCreatedAt);

        return this.page(pageParam, queryWrapper);
    }

    @Override
    @Transactional
    public void createNotification(Notification notification) {
        // 设置默认值
        if (notification.getStatus() == null) {
            notification.setStatus("draft");
        }

        if (notification.getPriority() == null) {
            notification.setPriority(0);
        }

        if (notification.getTargetType() == null) {
            notification.setTargetType("all");
        }

        // 如果是系统通知类型，转换为广播通知
        if ("system".equals(notification.getType())) {
            notification.setType("broadcast");
        }

        // 手动设置创建时间和更新时间
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());

        this.save(notification);

        // 如果状态是已发布，则自动投递通知
        if ("published".equals(notification.getStatus())) {
            publishAndDeliverNotification(notification.getId());
        }
    }

    @Override
    @Transactional
    public void updateNotification(Notification notification) {
        Notification existingNotification = this.getById(notification.getId());
        if (existingNotification == null) {
            throw new RuntimeException("通知不存在");
        }

        // 如果是系统通知类型，转换为广播通知
        if ("system".equals(notification.getType())) {
            notification.setType("broadcast");
        }

        // 如果状态从草稿变为已发布，则设置发布时间并自动投递
        boolean shouldPublish = "draft".equals(existingNotification.getStatus())
                && "published".equals(notification.getStatus());

        // 更新时间
        notification.setUpdatedAt(LocalDateTime.now());
        this.updateById(notification);

        if (shouldPublish) {
            publishAndDeliverNotification(notification.getId());
        }
    }

    @Override
    @Transactional
    public void deleteNotification(Integer id) {
        // 删除通知关联的消息日志
        LambdaQueryWrapper<MessageLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageLog::getNotificationId, id);
        messageLogMapper.delete(wrapper);

        // 使用MyBatis-Plus的逻辑删除功能
        this.removeById(id);
    }

    @Override
    @Transactional
    public int publishAndDeliverNotification(Integer notificationId) {
        Notification notification = this.getById(notificationId);
        if (notification == null) {
            return 0;
        }

        // 如果是系统通知类型，转换为广播通知
        if ("system".equals(notification.getType())) {
            notification.setType("broadcast");
        }

        // 更新通知状态为已发布
        notification.setStatus("published");
        notification.setPublishTime(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        this.updateById(notification);

        // 投递通知
        return deliverNotification(notificationId, notification.getTargetType(), notification.getTargetValues());
    }

    @Override
    @Transactional
    public int archiveNotification(Integer notificationId) {
        Notification notification = this.getById(notificationId);
        if (notification == null) {
            return 0;
        }

        // 更新通知状态为已归档
        notification.setStatus("archived");
        notification.setUpdatedAt(LocalDateTime.now());
        this.updateById(notification);

        return 1;
    }

    @Override
    @Transactional
    public int deliverNotification(Integer notificationId, String targetType, String targetValues) {
        // 获取通知详情
        Notification notification = this.getById(notificationId);
        if (notification == null) {
            log.error("通知不存在, id: {}", notificationId);
            return 0;
        }

        // 获取目标用户列表
        List<Integer> userIds = getUserIdsByTarget(targetType, targetValues);
        if (userIds.isEmpty()) {
            log.warn("没有找到目标用户, 通知ID: {}, 目标类型: {}", notificationId, targetType);
            return 0;
        }

        // 批量创建消息日志
        int count = 0;
        for (Integer userId : userIds) {
            // 检查是否已存在消息日志(避免重复投递)
            LambdaQueryWrapper<MessageLog> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(MessageLog::getNotificationId, notificationId)
                    .eq(MessageLog::getUserId, userId);

            if (messageLogMapper.selectCount(checkWrapper) == 0) {
                MessageLog messageLog = new MessageLog();
                messageLog.setNotificationId(notificationId);
                messageLog.setUserId(userId);
                messageLog.setStatus("unread");
                messageLog.setCreatedAt(LocalDateTime.now());
                messageLogMapper.insert(messageLog);
                count++;
            }
        }

        return count;
    }

    @Override
    public Page<Notification> getUserNotificationsWithPage(Integer userId, Integer page, Integer limit) {
        Page<Notification> resultPage = new Page<>(page, limit);

        // 步骤1: 获取用户的消息日志（带分页）
        LambdaQueryWrapper<MessageLog> logWrapper = new LambdaQueryWrapper<>();
        logWrapper.eq(MessageLog::getUserId, userId)
                .orderByDesc(MessageLog::getCreatedAt);

        Page<MessageLog> logPage = new Page<>(page, limit);
        messageLogMapper.selectPage(logPage, logWrapper);

        List<MessageLog> logs = logPage.getRecords();
        if (logs.isEmpty()) {
            return resultPage; // 返回空结果
        }

        // 步骤2: 提取通知ID列表
        List<Integer> notificationIds = logs.stream()
                .map(MessageLog::getNotificationId)
                .collect(Collectors.toList());

        // 步骤3: 批量查询通知详情
        LambdaQueryWrapper<Notification> notificationWrapper = new LambdaQueryWrapper<>();
        notificationWrapper.in(Notification::getId, notificationIds);
        List<Notification> notifications = this.list(notificationWrapper);

        // 步骤4: 关联阅读状态并按原顺序排序
        Map<Integer, Notification> notificationMap = notifications.stream()
                .collect(Collectors.toMap(
                        Notification::getId,
                        notification -> notification,
                        (existing, replacement) -> existing // 如果有重复键保留现有的
                ));

        List<Notification> orderedResult = new ArrayList<>();
        for (MessageLog log : logs) {
            Notification notification = notificationMap.get(log.getNotificationId());
            if (notification != null) {
                // 处理系统通知类型，转换显示
                if ("system".equals(notification.getType())) {
                    notification.setType("broadcast");
                }
                notification.setReadStatus(log.getStatus());
                orderedResult.add(notification);
            }
        }

        // 设置结果到分页对象
        resultPage.setRecords(orderedResult);
        resultPage.setTotal(logPage.getTotal());

        return resultPage;
    }

    @Override
    public List<Notification> getUserNotifications(Integer userId) {
        // 查询用户的消息日志
        LambdaQueryWrapper<MessageLog> logWrapper = new LambdaQueryWrapper<>();
        logWrapper.eq(MessageLog::getUserId, userId)
                .orderByDesc(MessageLog::getCreatedAt);

        List<MessageLog> logs = messageLogMapper.selectList(logWrapper);

        if (logs.isEmpty()) {
            return new ArrayList<>();
        }

        // 提取通知ID列表
        List<Integer> notificationIds = logs.stream()
                .map(MessageLog::getNotificationId)
                .collect(Collectors.toList());

        // 批量查询通知详情
        LambdaQueryWrapper<Notification> notificationWrapper = new LambdaQueryWrapper<>();
        notificationWrapper.in(Notification::getId, notificationIds);
        List<Notification> notifications = this.list(notificationWrapper);

        // 关联阅读状态
        Map<Integer, String> statusMap = logs.stream()
                .collect(Collectors.toMap(
                        MessageLog::getNotificationId,
                        MessageLog::getStatus,
                        (s1, s2) -> s1 // 如果有重复键，保留第一个
                ));

        notifications.forEach(notification -> {
            // 处理系统通知类型，转换显示
            if ("system".equals(notification.getType())) {
                notification.setType("broadcast");
            }
            notification.setReadStatus(statusMap.get(notification.getId()));
        });

        // 按通知ID在日志中的顺序重新排序
        Map<Integer, Integer> orderMap = new HashMap<>();
        for (int i = 0; i < logs.size(); i++) {
            orderMap.put(logs.get(i).getNotificationId(), i);
        }

        notifications.sort(Comparator.comparing(n ->
                orderMap.getOrDefault(n.getId(), Integer.MAX_VALUE)));

        return notifications;
    }

    @Override
    @Transactional
    public void markAsRead(Integer notificationId, Integer userId) {
        LambdaQueryWrapper<MessageLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageLog::getNotificationId, notificationId)
                .eq(MessageLog::getUserId, userId);

        MessageLog messageLog = messageLogMapper.selectOne(wrapper);

        if (messageLog != null) {
            messageLog.setStatus("read");
            messageLog.setUpdatedAt(LocalDateTime.now());
            messageLogMapper.updateById(messageLog);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(Integer userId) {
        LambdaQueryWrapper<MessageLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageLog::getUserId, userId)
                .eq(MessageLog::getStatus, "unread");

        List<MessageLog> messageLogs = messageLogMapper.selectList(wrapper);

        for (MessageLog log : messageLogs) {
            log.setStatus("read");
            log.setUpdatedAt(LocalDateTime.now());
            messageLogMapper.updateById(log);
        }
    }

    @Override
    public Integer getUnreadCount(Integer userId) {
        LambdaQueryWrapper<MessageLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageLog::getUserId, userId)
                .eq(MessageLog::getStatus, "unread");

        return Math.toIntExact(messageLogMapper.selectCount(wrapper));
    }

    /**
     * 根据目标类型和值获取用户ID列表
     */
    private List<Integer> getUserIdsByTarget(String targetType, String targetValues) {
        List<Integer> userIds = new ArrayList<>();

        if ("all".equals(targetType)) {
            // 所有用户
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            List<User> users = userMapper.selectList(wrapper);
            userIds = users.stream()
                    .map(User::getUserId)
                    .collect(Collectors.toList());
        } else if ("roles".equals(targetType) && StringUtils.hasText(targetValues)) {
            // 指定角色
            try {
                List<String> roles = objectMapper.readValue(targetValues, new TypeReference<List<String>>() {});
                LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
                wrapper.in(User::getRole, roles);
                List<User> users = userMapper.selectList(wrapper);
                userIds = users.stream()
                        .map(User::getUserId)
                        .collect(Collectors.toList());
            } catch (JsonProcessingException e) {
                log.error("解析角色JSON失败: {}", e.getMessage());
            }
        } else if ("users".equals(targetType) && StringUtils.hasText(targetValues)) {
            // 指定用户
            try {
                userIds = objectMapper.readValue(targetValues, new TypeReference<List<Integer>>() {});
            } catch (JsonProcessingException e) {
                log.error("解析用户ID JSON失败: {}", e.getMessage());
            }
        }

        return userIds;
    }
    @Override
    @Transactional
    public int restoreNotification(Integer notificationId) {
        Notification notification = this.getById(notificationId);
        if (notification == null) {
            return 0;
        }

        // 将状态从已归档改为已发布
        if (!"archived".equals(notification.getStatus())) {
            log.warn("只有已归档的通知才能被恢复, 当前状态: {}", notification.getStatus());
            return 0;
        }

        notification.setStatus("published");
        notification.setUpdatedAt(LocalDateTime.now());
        this.updateById(notification);

        return 1;
    }
}
