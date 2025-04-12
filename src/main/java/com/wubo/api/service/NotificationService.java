// NotificationService.java
package com.wubo.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wubo.api.entity.Notification;

import java.util.List;

public interface NotificationService extends IService<Notification> {
    /**
     * 获取通知列表（分页）
     */
    Page<Notification> getNotificationList(Integer page, Integer limit, String type, String status);

    /**
     * 创建通知
     */
    void createNotification(Notification notification);

    /**
     * 更新通知
     */
    void updateNotification(Notification notification);

    /**
     * 删除通知
     */
    void deleteNotification(Integer id);

    /**
     * 发布并投递通知
     * @return 投递的用户数量
     */
    int publishAndDeliverNotification(Integer notificationId);

    /**
     * 归档通知
     * @return 操作成功的记录数
     */
    int archiveNotification(Integer notificationId);

    /**
     * 投递通知
     * @param notificationId 通知ID
     * @param targetType 目标类型
     * @param targetValues 目标值
     * @return 投递的用户数量
     */
    int deliverNotification(Integer notificationId, String targetType, String targetValues);

    /**
     * 获取用户通知列表（分页）
     */
    Page<Notification> getUserNotificationsWithPage(Integer userId, Integer page, Integer limit);

    /**
     * 获取用户通知列表（不分页）
     */
    List<Notification> getUserNotifications(Integer userId);

    /**
     * 标记通知为已读
     */
    void markAsRead(Integer notificationId, Integer userId);

    /**
     * 标记所有通知为已读
     */
    void markAllAsRead(Integer userId);

    /**
     * 获取未读通知数量
     */
    Integer getUnreadCount(Integer userId);
}
