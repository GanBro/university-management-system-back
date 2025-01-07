package com.wubo.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.dto.Result;
import com.wubo.api.entity.News;
import com.wubo.api.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/news")
@Tag(name = "信息发布管理接口")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @Operation(summary = "获取信息列表")
    @GetMapping
    public Result<Page<News>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) Integer universityId,  // 新增参数
            @RequestParam(required = false) Map<String, Object> params) {
        if (universityId != null) {
            params.put("universityId", universityId);
        }
        return Result.success(newsService.getNewsList(page, limit, params));
    }

    @Operation(summary = "获取信息详情")
    @GetMapping("/{id}")
    public Result<News> detail(@Parameter(description = "信息ID", required = true) @PathVariable Integer id) {
        if (id == null || id <= 0) {
            return Result.error("无效的ID");
        }
        News news = newsService.getNewsDetail(id);
        if (news == null) {
            return Result.error("信息不存在");
        }
        newsService.incrementViewCount(id);
        return Result.success(news);
    }

    @Operation(summary = "创建信息")
    @PostMapping
    public Result<?> create(@RequestBody News news) {
        if (news == null) {
            return Result.error("参数不能为空");
        }
        newsService.createNews(news);
        return Result.success(null);
    }

    @Operation(summary = "更新信息")
    @PutMapping("/{id}")
    public Result<?> update(
            @Parameter(description = "信息ID", required = true) @PathVariable Integer id,
            @RequestBody News news) {
        if (id == null || id <= 0) {
            return Result.error("无效的ID");
        }
        if (news == null) {
            return Result.error("参数不能为空");
        }
        news.setId(id);
        newsService.updateNews(news);
        return Result.success(null);
    }

    @Operation(summary = "删除信息")
    @DeleteMapping("/{id}")
    public Result<?> delete(@Parameter(description = "信息ID", required = true) @PathVariable Integer id) {
        if (id == null || id <= 0) {
            return Result.error("无效的ID");
        }
        newsService.deleteNews(id);
        return Result.success(null);
    }

    @Operation(summary = "发布信息")
    @PostMapping("/{id}/publish")
    public Result<?> publish(@Parameter(description = "信息ID", required = true) @PathVariable Integer id) {
        if (id == null || id <= 0) {
            return Result.error("无效的ID");
        }
        newsService.publishNews(id);
        return Result.success(null);
    }
}
