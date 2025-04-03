// NotificationService.java
package com.wubo.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wubo.api.entity.Notification;

import java.util.List;

public interface NotificationService extends IService<Notification> {
    Page<Notification> getNotificationList(Integer page, Integer limit, String type, String status);
    void createNotification(Notification notification);
    void updateNotification(Notification notification);
    void deleteNotification(Integer id);
    List<Notification> getUserNotifications(Integer userId);
    void markAsRead(Integer notificationId, Integer userId);
    void markAllAsRead(Integer userId);
    Integer getUnreadCount(Integer userId);
}
