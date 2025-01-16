// InteractionController.java
package com.wubo.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.dto.Result;
import com.wubo.api.entity.Interaction;
import com.wubo.api.entity.InteractionReply;
import com.wubo.api.service.InteractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/interactions")
@Tag(name = "互动管理接口")
public class InteractionController {

    @Autowired
    private InteractionService interactionService;

    @Operation(summary = "获取互动列表")
    @GetMapping
    public Result<Page<Interaction>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) Map<String, Object> params) {
        log.info("获取互动列表, 页码: {}, 每页数量: {}, 参数: {}", page, limit, params);
        return Result.success(interactionService.getInteractionList(page, limit, params));
    }

    @Operation(summary = "获取互动详情")
    @GetMapping("/{id}")
    public Result<Interaction> detail(@PathVariable Integer id) {
        log.info("获取互动详情, id: {}", id);
        return Result.success(interactionService.getInteractionDetail(id));
    }

    @Operation(summary = "创建互动")
    @PostMapping
    public Result<?> create(@RequestBody Interaction interaction) {
        log.info("创建互动: {}", interaction);
        interactionService.createInteraction(interaction);
        return Result.success(null);
    }

    @Operation(summary = "回复互动")
    @PostMapping("/{id}/reply")
    public Result<?> reply(@PathVariable Integer id, @RequestBody InteractionReply reply) {
        log.info("回复互动, id: {}, 回复内容: {}", id, reply);
        reply.setInteractionId(id);
        interactionService.replyInteraction(reply);
        return Result.success(null);
    }

    @Operation(summary = "关闭互动")
    @PostMapping("/{id}/close")
    public Result<?> close(@PathVariable Integer id) {
        log.info("关闭互动, id: {}", id);
        interactionService.closeInteraction(id);
        return Result.success(null);
    }

    @Operation(summary = "重新开启互动")
    @PostMapping("/{id}/reopen")
    public Result<?> reopen(@PathVariable Integer id) {
        log.info("重新开启互动, id: {}", id);
        interactionService.reopenInteraction(id);
        return Result.success(null);
    }

    @Operation(summary = "删除互动")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        log.info("删除互动, id: {}", id);
        interactionService.deleteInteraction(id);
        return Result.success(null);
    }
}
