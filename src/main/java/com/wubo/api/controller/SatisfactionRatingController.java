package com.wubo.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.dto.Result;
import com.wubo.api.entity.SatisfactionRating;
import com.wubo.api.mapper.SatisfactionRatingMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/satisfaction-rating")
@Tag(name = "专业满意度管理", description = "专业满意度查询与管理相关接口")
public class SatisfactionRatingController {

    @Autowired
    private SatisfactionRatingMapper satisfactionRatingMapper;

    @GetMapping("/list")
    @Operation(summary = "分页查询专业满意度", description = "根据条件分页查询专业满意度列表")
    public Result<IPage<SatisfactionRating>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer limit,
            @Parameter(description = "大学ID") @RequestParam(required = false) Integer universityId,
            @Parameter(description = "专业类别") @RequestParam(required = false) String category,
            @Parameter(description = "最低评分") @RequestParam(required = false) Double minRating,
            @Parameter(description = "最高评分") @RequestParam(required = false) Double maxRating,
            @Parameter(description = "排除的类别，逗号分隔") @RequestParam(required = false) String excludeCategories
    ) {
        try {
            log.info("分页查询专业满意度, 页码: {}, 每页条数: {}, 大学ID: {}, 专业类别: {}", page, limit, universityId, category);

            // 构建查询条件
            LambdaQueryWrapper<SatisfactionRating> queryWrapper = new LambdaQueryWrapper<>();

            // 筛选特定大学的数据
            if (universityId != null) {
                queryWrapper.eq(SatisfactionRating::getUniversityId, universityId);
            }

            // 类别模糊匹配
            if (StringUtils.hasText(category)) {
                queryWrapper.like(SatisfactionRating::getCategory, category);
            }

            // 排除特定类别（如"综合评价"、"环境"、"生活"等）
            List<String> excludeCategoriesList;
            if (StringUtils.hasText(excludeCategories)) {
                excludeCategoriesList = Arrays.asList(excludeCategories.split(","));
            } else {
                // 默认排除这些非专业类别
                excludeCategoriesList = Arrays.asList("综合评价", "环境", "生活");
            }
            queryWrapper.notIn(SatisfactionRating::getCategory, excludeCategoriesList);

            // 评分范围筛选
            if (minRating != null) {
                queryWrapper.ge(SatisfactionRating::getRating, minRating);
            }

            if (maxRating != null) {
                queryWrapper.le(SatisfactionRating::getRating, maxRating);
            }

            // 按评分降序排序
            queryWrapper.orderByDesc(SatisfactionRating::getRating);

            // 分页查询
            Page<SatisfactionRating> pageParam = new Page<>(page, limit);
            IPage<SatisfactionRating> result = satisfactionRatingMapper.selectPage(pageParam, queryWrapper);

            // 确保有正确的总数
            if (result.getTotal() == 0 && !result.getRecords().isEmpty()) {
                long count = satisfactionRatingMapper.selectCount(queryWrapper);
                ((Page<SatisfactionRating>) result).setTotal(count);
            }

            log.info("查询专业满意度成功, 数据量: {}", result.getRecords().size());
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询专业满意度异常", e);
            return Result.error(500, "查询失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取专业满意度详情", description = "根据ID获取满意度详情")
    public Result<SatisfactionRating> detail(
            @Parameter(description = "记录ID", required = true) @PathVariable Integer id
    ) {
        try {
            log.info("获取专业满意度详情, id: {}", id);

            SatisfactionRating entity = satisfactionRatingMapper.selectById(id);
            if (entity == null) {
                return Result.error(404, "未找到该满意度记录");
            }

            return Result.success(entity);
        } catch (Exception e) {
            log.error("获取专业满意度详情异常, id: {}", id, e);
            return Result.error(500, "获取详情失败: " + e.getMessage());
        }
    }

    @PostMapping("/save")
    @Operation(summary = "保存或更新专业满意度", description = "保存或更新专业满意度数据")
    public Result<?> save(@RequestBody SatisfactionRating entity) {
        try {
            log.info("保存或更新专业满意度, universityId: {}, category: {}", entity.getUniversityId(), entity.getCategory());

            if (entity.getId() != null) {
                // 更新
                int rows = satisfactionRatingMapper.updateById(entity);
                if (rows > 0) {
                    return Result.success("更新成功");
                } else {
                    return Result.error(404, "记录不存在");
                }
            } else {
                // 新增
                int rows = satisfactionRatingMapper.insert(entity);
                if (rows > 0) {
                    return Result.success("添加成功");
                } else {
                    return Result.error(500, "添加失败");
                }
            }
        } catch (Exception e) {
            log.error("保存专业满意度异常", e);
            return Result.error(500, "保存失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除专业满意度记录", description = "根据ID删除专业满意度记录")
    public Result<?> delete(@PathVariable Integer id) {
        try {
            log.info("删除专业满意度记录, id: {}", id);

            int rows = satisfactionRatingMapper.deleteById(id);
            if (rows > 0) {
                return Result.success("删除成功");
            } else {
                return Result.error(404, "记录不存在或删除失败");
            }
        } catch (Exception e) {
            log.error("删除专业满意度记录异常, id: {}", id, e);
            return Result.error(500, "删除失败: " + e.getMessage());
        }
    }
}
