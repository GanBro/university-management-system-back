// NotificationController.java
package com.wubo.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.dto.NotificationQueryDTO;
import com.wubo.api.dto.Result;
import com.wubo.api.entity.Notification;
import com.wubo.api.entity.User;
import com.wubo.api.service.NotificationService;
import com.wubo.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/notifications")
@Tag(name = "通知管理接口", description = "提供通知的增删改查功能")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Operation(summary = "获取通知列表")
    @GetMapping
    public Result<Page<Notification>> list(@ModelAttribute NotificationQueryDTO queryDTO) {
        log.info("获取通知列表, 页码: {}, 每页数量: {}, 类型: {}, 状态: {}",
                queryDTO.getPage(), queryDTO.getLimit(), queryDTO.getType(), queryDTO.getStatus());
        Page<Notification> result = notificationService.getNotificationList(
                queryDTO.getPage(), queryDTO.getLimit(), queryDTO.getType(), queryDTO.getStatus());
        return Result.success(result);
    }

    @Operation(summary = "获取通知详情")
    @GetMapping("/{id}")
    public Result<Notification> detail(@Parameter(description = "通知ID", required = true) @PathVariable Integer id) {
        log.info("获取通知详情, id: {}", id);
        Notification notification = notificationService.getById(id);
        if (notification == null) {
            return Result.error("通知不存在");
        }
        return Result.success(notification);
    }

    @Operation(summary = "创建通知")
    @PostMapping
    public Result<?> create(@RequestBody Notification notification) {
        log.info("创建通知: {}", notification);
        notificationService.createNotification(notification);
        return Result.success("创建成功");
    }

    @Operation(summary = "更新通知")
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Integer id, @RequestBody Notification notification) {
        log.info("更新通知, id: {}, 更新内容: {}", id, notification);
        notification.setId(id);
        notificationService.updateNotification(notification);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除通知")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        log.info("删除通知, id: {}", id);
        notificationService.deleteNotification(id);
        return Result.success("删除成功");
    }

    @Operation(summary = "发布通知")
    @PostMapping("/{id}/publish")
    public Result<?> publishNotification(@PathVariable Integer id) {
        log.info("发布通知, id: {}", id);
        int count = notificationService.publishAndDeliverNotification(id);
        return Result.success("通知已发布给" + count + "个用户");
    }

    @Operation(summary = "归档通知")
    @PostMapping("/{id}/archive")
    public Result<?> archiveNotification(@PathVariable Integer id) {
        log.info("归档通知, id: {}", id);
        int result = notificationService.archiveNotification(id);
        if (result > 0) {
            return Result.success("通知已归档");
        }
        return Result.error("通知不存在或归档失败");
    }

    // 用户端接口 - 修改后的方法
    @Operation(summary = "获取用户通知列表")
    @GetMapping("/user/notifications")
    public Result<?> getUserNotifications(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false, defaultValue = "false") Boolean pageable,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            HttpServletRequest request) {

        // 如果未提供userId，则从Token获取
        Integer currentUserId = userId;
        if (currentUserId == null) {
            // 从请求头获取Token
            String token = request.getHeader("token");
            log.info("从请求头获取Token: {}", token);

            if (token == null || token.isEmpty()) {
                log.warn("请求中未包含Token");
                return Result.error("未登录");
            }

            // 使用UserService验证Token
            User user = userService.getUserByToken(token);
            if (user == null) {
                log.warn("Token无效: {}", token);
                return Result.error("无效的Token");
            }

            currentUserId = user.getUserId();
            log.info("Token验证成功, 获取到用户ID: {}", currentUserId);
        }

        log.info("获取用户通知列表, userId: {}, pageable: {}, page: {}, limit: {}",
                currentUserId, pageable, page, limit);

        try {
            if (Boolean.TRUE.equals(pageable)) {
                Page<Notification> notifications = notificationService.getUserNotificationsWithPage(currentUserId, page, limit);
                return Result.success(notifications);
            } else {
                List<Notification> notifications = notificationService.getUserNotifications(currentUserId);
                return Result.success(notifications);
            }
        } catch (Exception e) {
            log.error("获取用户通知失败", e);
            return Result.error("获取用户通知失败: " + e.getMessage());
        }
    }

    @Operation(summary = "标记通知为已读")
    @PutMapping("/user/notifications/{id}/read")
    public Result<?> markAsRead(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer userId,
            HttpServletRequest request) {

        Integer currentUserId = userId;
        if (currentUserId == null) {
            String token = request.getHeader("token");
            User user = userService.getUserByToken(token);
            if (user == null) {
                return Result.error("未登录或无效的Token");
            }
            currentUserId = user.getUserId();
        }

        log.info("标记通知为已读, notificationId: {}, userId: {}", id, currentUserId);
        notificationService.markAsRead(id, currentUserId);
        return Result.success("标记成功");
    }

    @Operation(summary = "标记所有通知为已读")
    @PutMapping("/user/notifications/read-all")
    public Result<?> markAllAsRead(
            @RequestParam(required = false) Integer userId,
            HttpServletRequest request) {

        Integer currentUserId = userId;
        if (currentUserId == null) {
            String token = request.getHeader("token");
            User user = userService.getUserByToken(token);
            if (user == null) {
                return Result.error("未登录或无效的Token");
            }
            currentUserId = user.getUserId();
        }

        log.info("标记所有通知为已读, userId: {}", currentUserId);
        notificationService.markAllAsRead(currentUserId);
        return Result.success("标记成功");
    }

    @Operation(summary = "获取未读通知数量")
    @GetMapping("/user/notifications/unread-count")
    public Result<Integer> getUnreadCount(
            @RequestParam(required = false) Integer userId,
            HttpServletRequest request) {

        Integer currentUserId = userId;
        if (currentUserId == null) {
            String token = request.getHeader("token");
            User user = userService.getUserByToken(token);
            if (user == null) {
                return Result.error("未登录或无效的Token");
            }
            currentUserId = user.getUserId();
        }

        log.info("获取未读通知数量, userId: {}", currentUserId);
        Integer count = notificationService.getUnreadCount(currentUserId);
        return Result.success(count);
    }
    @Operation(summary = "恢复归档通知")
    @PostMapping("/{id}/restore")
    public Result<?> restoreNotification(@PathVariable Integer id) {
        log.info("恢复归档通知, id: {}", id);
        int result = notificationService.restoreNotification(id);
        if (result > 0) {
            return Result.success("通知已恢复");
        }
        return Result.error("通知不存在或恢复失败");
    }
}
