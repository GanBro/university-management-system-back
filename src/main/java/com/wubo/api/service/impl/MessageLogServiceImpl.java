// MessageLogServiceImpl.java
package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wubo.api.entity.MessageLog;
import com.wubo.api.entity.User;
import com.wubo.api.mapper.MessageLogMapper;
import com.wubo.api.mapper.UserMapper;
import com.wubo.api.service.MessageLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageLogServiceImpl extends ServiceImpl<MessageLogMapper, MessageLog> implements MessageLogService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public void createMessageLogsForAllUsers(Integer notificationId) {
        // 获取所有用户
        List<User> users = userMapper.selectList(null);
        
        // 为每个用户创建消息记录
        for (User user : users) {
            MessageLog messageLog = new MessageLog();
            messageLog.setNotificationId(notificationId);
            messageLog.setUserId(user.getUserId());
            messageLog.setStatus("unread");
            save(messageLog);
        }
    }

    @Override
    public List<MessageLog> getUserNotifications(Integer userId) {
        LambdaQueryWrapper<MessageLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageLog::getUserId, userId)
               .orderByDesc(MessageLog::getCreatedAt);
        return list(wrapper);
    }

    @Override
    public void markAsRead(Integer notificationId, Integer userId) {
        LambdaQueryWrapper<MessageLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageLog::getNotificationId, notificationId)
               .eq(MessageLog::getUserId, userId);
        
        MessageLog messageLog = getOne(wrapper);
        if (messageLog != null) {
            messageLog.setStatus("read");
            messageLog.setUpdatedAt(LocalDateTime.now());
            updateById(messageLog);
        }
    }

    @Override
    public void markAllAsRead(Integer userId) {
        LambdaQueryWrapper<MessageLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageLog::getUserId, userId)
               .eq(MessageLog::getStatus, "unread");
        
        List<MessageLog> unreadLogs = list(wrapper);
        for (MessageLog log : unreadLogs) {
            log.setStatus("read");
            log.setUpdatedAt(LocalDateTime.now());
            updateById(log);
        }
    }

    @Override
    public int getUnreadCount(Integer userId) {
        LambdaQueryWrapper<MessageLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageLog::getUserId, userId)
               .eq(MessageLog::getStatus, "unread");
        return Math.toIntExact(count(wrapper));
    }

    @Override
    public Page<MessageLog> getMessageLogList(Integer page, Integer limit, Integer userId, String status) {
        Page<MessageLog> pageParam = new Page<>(page, limit);
        return this.page(pageParam);
    }

    @Override
    public void sendMessage(MessageLog messageLog) {
        messageLog.setCreatedAt(LocalDateTime.now());
        messageLog.setUpdatedAt(LocalDateTime.now());
        this.save(messageLog);
    }
}
