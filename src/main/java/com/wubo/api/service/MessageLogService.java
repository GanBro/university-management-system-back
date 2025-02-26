// MessageLogService.java
package com.wubo.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.entity.MessageLog;
import java.util.List;

public interface MessageLogService {
    Page<MessageLog> getMessageLogList(Integer page, Integer limit, Integer userId, String status);
    void sendMessage(MessageLog messageLog);

    /**
     * 为所有用户创建消息记录
     * @param notificationId 通知ID
     */
    void createMessageLogsForAllUsers(Integer notificationId);

    /**
     * 获取用户的通知列表
     * @param userId 用户ID
     * @return 通知列表
     */
    List<MessageLog> getUserNotifications(Integer userId);

    /**
     * 标记通知为已读
     * @param notificationId 通知ID
     * @param userId 用户ID
     */
    void markAsRead(Integer notificationId, Integer userId);

    /**
     * 标记所有通知为已读
     * @param userId 用户ID
     */
    void markAllAsRead(Integer userId);

    /**
     * 获取未读通知数量
     * @param userId 用户ID
     * @return 未读数量
     */
    int getUnreadCount(Integer userId);
}
