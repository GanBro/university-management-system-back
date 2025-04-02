// NotificationServiceImpl.java
package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wubo.api.entity.MessageLog;
import com.wubo.api.entity.Notification;
import com.wubo.api.mapper.MessageLogMapper;
import com.wubo.api.mapper.NotificationMapper;
import com.wubo.api.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Autowired
    private MessageLogMapper messageLogMapper;
    @Override
    public Page<Notification> getNotificationList(Integer page, Integer limit, String type, String status) {
        Page<Notification> pageParam = new Page<>(page, limit);
        return this.page(pageParam);
    }

    @Override
    public void createNotification(Notification notification) {
        this.save(notification);
    }

    @Override
    public void updateNotification(Notification notification) {
        this.updateById(notification);
    }

    @Override
    public void deleteNotification(Integer id) {
        // 使用逻辑删除
        this.removeById(id);
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
        List<Integer> notificationIds = new ArrayList<>();
        for (MessageLog log : logs) {
            notificationIds.add(log.getNotificationId());
        }

        // 查询通知详情
        LambdaQueryWrapper<Notification> notificationWrapper = new LambdaQueryWrapper<>();
        notificationWrapper.in(Notification::getId, notificationIds)
                .orderByDesc(Notification::getCreatedAt);

        List<Notification> notifications = this.list(notificationWrapper);

        // 添加读取状态
        for (Notification notification : notifications) {
            for (MessageLog log : logs) {
                if (notification.getId().equals(log.getNotificationId())) {
                    notification.setStatus(log.getStatus()); // 假设Notification有status字段存储已读/未读状态
                    break;
                }
            }
        }

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
}
