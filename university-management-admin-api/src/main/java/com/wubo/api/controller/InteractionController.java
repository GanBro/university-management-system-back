// InteractionController.java
package com.wubo.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.dto.Result;
import com.wubo.api.entity.Interaction;
import com.wubo.api.entity.InteractionReply;
import com.wubo.api.service.InteractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
        return Result.success(interactionService.getInteractionList(page, limit, params));
    }

    @Operation(summary = "获取互动详情")
    @GetMapping("/{id}")
    public Result<Interaction> detail(@PathVariable Integer id) {
        return Result.success(interactionService.getInteractionDetail(id));
    }

    @Operation(summary = "创建互动")
    @PostMapping
    public Result<?> create(@RequestBody Interaction interaction) {
        interactionService.createInteraction(interaction);
        return Result.success(null);
    }

    @Operation(summary = "回复互动")
    @PostMapping("/{id}/reply")
    public Result<?> reply(@PathVariable Integer id, @RequestBody InteractionReply reply) {
        reply.setInteractionId(id);
        interactionService.replyInteraction(reply);
        return Result.success(null);
    }

    @Operation(summary = "关闭互动")
    @PostMapping("/{id}/close")
    public Result<?> close(@PathVariable Integer id) {
        interactionService.closeInteraction(id);
        return Result.success(null);
    }

    @Operation(summary = "删除互动")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        interactionService.deleteInteraction(id);
        return Result.success(null);
    }
}
