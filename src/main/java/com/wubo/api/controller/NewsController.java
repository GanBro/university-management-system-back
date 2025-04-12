package com.wubo.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.dto.NewsQueryDTO;
import com.wubo.api.dto.Result;
import com.wubo.api.entity.News;
import com.wubo.api.entity.User;
import com.wubo.api.service.NewsService;
import com.wubo.api.service.UserService;
import com.wubo.api.service.UniversityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/news")
@Tag(name = "信息发布管理接口")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private UserService userService;

    @Autowired
    private UniversityService universityService;

    @Operation(summary = "获取信息列表")
    @GetMapping("/list")
    public Result<Page<News>> list(@ModelAttribute NewsQueryDTO queryDTO) {
        if (queryDTO.getParams() == null) {
            queryDTO.setParams(new HashMap<>()); // 初始化 params
        }
        if (queryDTO.getUniversityId() != null) {
            queryDTO.getParams().put("universityId", queryDTO.getUniversityId());
        }
        Page<News> newsPage = newsService.getNewsList(
                queryDTO.getPage(), queryDTO.getLimit(), queryDTO.getParams());

        // 填充大学信息
        if (newsPage.getRecords() != null) {
            for (News news : newsPage.getRecords()) {
                if (news.getUniversityId() != null) {
                    news.setUniversity(universityService.getById(news.getUniversityId()));
                }
            }
        }

        return Result.success(newsPage);
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
        return Result.success(news);
    }

    @Operation(summary = "增加浏览量")
    @PostMapping("/{id}/view")
    public Result<?> incrementViewCount(@Parameter(description = "信息ID", required = true) @PathVariable Integer id) {
        if (id == null || id <= 0) {
            return Result.error("无效的ID");
        }
        newsService.incrementViewCount(id);
        return Result.success(null);
    }

    @Operation(summary = "创建信息")
    @PostMapping
    public Result<?> create(@RequestBody News news, @RequestHeader("token") String token) {
        if (news == null) {
            return Result.error("参数不能为空");
        }

        // 获取当前登录用户
        User currentUser = userService.getUserByToken(token);
        if (currentUser == null) {
            return Result.error("用户未登录");
        }

        // 修改为：只在作者为空时设置为当前用户名
        if (news.getAuthor() == null || news.getAuthor().trim().isEmpty()) {
            news.setAuthor(currentUser.getUsername());
        }

        // 验证关联的大学是否存在
        if (news.getUniversityId() != null) {
            if (universityService.getById(news.getUniversityId()) == null) {
                return Result.error("关联的大学不存在");
            }
        }

        newsService.createNews(news);
        return Result.success(null);
    }

    @Operation(summary = "更新信息")
    @PutMapping("/{id}")
    public Result<?> update(
            @Parameter(description = "信息ID", required = true) @PathVariable Integer id,
            @RequestBody News news,
            @RequestHeader("token") String token) {
        if (id == null || id <= 0) {
            return Result.error("无效的ID");
        }
        if (news == null) {
            return Result.error("参数不能为空");
        }

        // 获取当前登录用户
        User currentUser = userService.getUserByToken(token);
        if (currentUser == null) {
            return Result.error("用户未登录");
        }

        // 与创建方法保持一致，只在作者为空时设置为当前用户名
        if (news.getAuthor() == null || news.getAuthor().trim().isEmpty()) {
            news.setAuthor(currentUser.getUsername());
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
