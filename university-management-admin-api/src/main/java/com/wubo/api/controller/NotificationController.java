// NotificationController.java
package com.wubo.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.dto.NotificationQueryDTO;
import com.wubo.api.dto.Result;
import com.wubo.api.entity.Notification;
import com.wubo.api.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/notifications")
@Tag(name = "通知管理接口", description = "提供通知的增删改查功能")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

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
}
