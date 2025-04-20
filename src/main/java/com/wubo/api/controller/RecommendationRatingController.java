package com.wubo.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.dto.Result;
import com.wubo.api.entity.RecommendationRating;
import com.wubo.api.service.RecommendationRatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/recommendation-rating")
@Tag(name = "专业推荐管理", description = "专业推荐查询与管理相关接口")
public class RecommendationRatingController {

    @Autowired
    private RecommendationRatingService recommendationRatingService;

    @GetMapping("/list")
    @Operation(summary = "分页查询专业推荐", description = "根据条件分页查询专业推荐列表")
    public Result<IPage<RecommendationRating>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer limit,
            @Parameter(description = "大学ID") @RequestParam(required = false) Integer universityId,
            @Parameter(description = "专业类别") @RequestParam(required = false) String majorCategory,
            @Parameter(description = "最低评分") @RequestParam(required = false) Double minRating,
            @Parameter(description = "最高评分") @RequestParam(required = false) Double maxRating
    ) {
        try {
            log.info("分页查询专业推荐, 页码: {}, 每页条数: {}, 大学ID: {}, 专业类别: {}", page, limit, universityId, majorCategory);

            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            params.put("universityId", universityId);
            params.put("majorCategory", majorCategory);
            params.put("minRating", minRating);
            params.put("maxRating", maxRating);

            // 分页查询
            Page<RecommendationRating> pageParam = new Page<>(page, limit);
            IPage<RecommendationRating> pageResult = recommendationRatingService.getRecommendationRatingPage(pageParam, params);

            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询专业推荐异常", e);
            return Result.error(500, "查询失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取专业推荐详情", description = "根据ID获取推荐详情")
    public Result<RecommendationRating> detail(
            @Parameter(description = "记录ID", required = true) @PathVariable Integer id
    ) {
        try {
            log.info("获取专业推荐详情, id: {}", id);

            RecommendationRating entity = recommendationRatingService.getById(id);
            if (entity == null) {
                return Result.error(404, "未找到该推荐记录");
            }

            return Result.success(entity);
        } catch (Exception e) {
            log.error("获取专业推荐详情异常, id: {}", id, e);
            return Result.error(500, "获取详情失败: " + e.getMessage());
        }
    }

    @PostMapping("/save")
    @Operation(summary = "保存或更新专业推荐", description = "保存或更新专业推荐数据")
    public Result<?> save(@RequestBody RecommendationRating entity) {
        try {
            log.info("保存或更新专业推荐, universityId: {}, majorCategory: {}", entity.getUniversityId(), entity.getMajorCategory());

            boolean success = recommendationRatingService.saveOrUpdate(entity);
            if (success) {
                return Result.success("保存成功");
            } else {
                return Result.error(500, "保存失败");
            }
        } catch (Exception e) {
            log.error("保存专业推荐异常", e);
            return Result.error(500, "保存失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除专业推荐记录", description = "根据ID删除专业推荐记录")
    public Result<?> delete(@PathVariable Integer id) {
        try {
            log.info("删除专业推荐记录, id: {}", id);

            boolean success = recommendationRatingService.removeById(id);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error(404, "记录不存在或删除失败");
            }
        } catch (Exception e) {
            log.error("删除专业推荐记录异常, id: {}", id, e);
            return Result.error(500, "删除失败: " + e.getMessage());
        }
    }
}
