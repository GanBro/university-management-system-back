// NotificationServiceImpl.java
package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wubo.api.entity.Notification;
import com.wubo.api.mapper.NotificationMapper;
import com.wubo.api.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

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
        this.removeById(id);
    }
}
