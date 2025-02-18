// NotificationService.java
package com.wubo.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.entity.Notification;

public interface NotificationService {
    Page<Notification> getNotificationList(Integer page, Integer limit, String type, String status);
    void createNotification(Notification notification);
    void updateNotification(Notification notification);
    void deleteNotification(Integer id);
}
