// MessageLogController.java
package com.wubo.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.dto.MessageLogQueryDTO;
import com.wubo.api.dto.Result;
import com.wubo.api.entity.MessageLog;
import com.wubo.api.service.MessageLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/message-logs")
@Tag(name = "消息记录接口", description = "提供消息记录的查询和发送功能")
public class MessageLogController {

    @Autowired
    private MessageLogService messageLogService;

    @Operation(summary = "获取消息记录")
    @GetMapping
    public Result<Page<MessageLog>> list(@ModelAttribute MessageLogQueryDTO queryDTO) {
        log.info("获取消息记录, 页码: {}, 每页数量: {}, 用户ID: {}, 状态: {}",
                queryDTO.getPage(), queryDTO.getLimit(), queryDTO.getUserId(), queryDTO.getStatus());
        Page<MessageLog> result = messageLogService.getMessageLogList(
                queryDTO.getPage(), queryDTO.getLimit(), queryDTO.getUserId(), queryDTO.getStatus());
        return Result.success(result);
    }

    @Operation(summary = "发送消息")
    @PostMapping
    public Result<?> send(@RequestBody MessageLog messageLog) {
        log.info("发送消息: {}", messageLog);
        messageLogService.sendMessage(messageLog);
        return Result.success(null);
    }
}
