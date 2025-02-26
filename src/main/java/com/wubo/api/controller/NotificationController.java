// NotificationController.java
package com.wubo.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.dto.NotificationQueryDTO;
import com.wubo.api.dto.Result;
import com.wubo.api.entity.Notification;
import com.wubo.api.entity.MessageLog;
import com.wubo.api.service.NotificationService;
import com.wubo.api.service.MessageLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/notifications")
@Tag(name = "通知管理接口", description = "提供通知的增删改查功能")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private MessageLogService messageLogService;

    @Operation(summary = "获取通知列表")
    @GetMapping
    public Result<Page<Notification>> list(@ModelAttribute NotificationQueryDTO queryDTO) {
        log.info("获取通知列表, 页码: {}, 每页数量: {}, 类型: {}, 状态: {}",
                queryDTO.getPage(), queryDTO.getLimit(), queryDTO.getType(), queryDTO.getStatus());
        Page<Notification> result = notificationService.getNotificationList(
                queryDTO.getPage(), queryDTO.getLimit(), queryDTO.getType(), queryDTO.getStatus());
        return Result.success(result);
    }

    @Operation(summary = "创建通知")
    @PostMapping
    public Result<?> create(@RequestBody Notification notification) {
        log.info("创建通知: {}", notification);
        notificationService.createNotification(notification);
        // 创建通知后，自动为所有用户创建消息记录
        messageLogService.createMessageLogsForAllUsers(notification.getId());
        return Result.success(null);
    }

    @Operation(summary = "更新通知")
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Integer id, @RequestBody Notification notification) {
        log.info("更新通知, id: {}, 更新内容: {}", id, notification);
        notification.setId(id);
        notificationService.updateNotification(notification);
        return Result.success(null);
    }

    @Operation(summary = "删除通知")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        log.info("删除通知, id: {}", id);
        notificationService.deleteNotification(id);
        return Result.success(null);
    }

    // 用户端接口
    @Operation(summary = "获取用户通知列表")
    @GetMapping("/user/notifications")
    public Result<List<MessageLog>> getUserNotifications(@RequestParam Integer userId) {
        log.info("获取用户通知列表, userId: {}", userId);
        List<MessageLog> notifications = messageLogService.getUserNotifications(userId);
        return Result.success(notifications);
    }

    @Operation(summary = "标记通知为已读")
    @PutMapping("/user/notifications/{id}/read")
    public Result<?> markAsRead(@PathVariable Integer id, @RequestParam Integer userId) {
        log.info("标记通知为已读, notificationId: {}, userId: {}", id, userId);
        messageLogService.markAsRead(id, userId);
        return Result.success();
    }

    @Operation(summary = "标记所有通知为已读")
    @PutMapping("/user/notifications/read-all")
    public Result<?> markAllAsRead(@RequestParam Integer userId) {
        log.info("标记所有通知为已读, userId: {}", userId);
        messageLogService.markAllAsRead(userId);
        return Result.success();
    }

    @Operation(summary = "获取未读通知数量")
    @GetMapping("/user/notifications/unread-count")
    public Result<Integer> getUnreadCount(@RequestParam Integer userId) {
        log.info("获取未读通知数量, userId: {}", userId);
        int count = messageLogService.getUnreadCount(userId);
        return Result.success(count);
    }
}
