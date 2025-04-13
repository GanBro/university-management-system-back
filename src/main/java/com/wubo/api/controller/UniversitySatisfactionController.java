package com.wubo.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.dto.Result;
import com.wubo.api.dto.UniversitySatisfactionDTO;
import com.wubo.api.entity.UniversitySatisfaction;
import com.wubo.api.service.UniversitySatisfactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 院校满意度控制器
 */
@Slf4j
@RestController
@RequestMapping("/university-satisfaction")
@Tag(name = "院校满意度管理", description = "院校满意度查询与管理相关接口")
public class UniversitySatisfactionController {

    @Resource
    private UniversitySatisfactionService universitySatisfactionService;

    /**
     * 分页查询院校满意度列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询院校满意度", description = "根据条件分页查询院校满意度列表")
    public Result<IPage<UniversitySatisfactionDTO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer limit,
            @Parameter(description = "院校名称") @RequestParam(required = false) String name,
            @Parameter(description = "院校所在地") @RequestParam(required = false) String location,
            @Parameter(description = "主管部门") @RequestParam(required = false) String department,
            @Parameter(description = "办学层次") @RequestParam(required = false) String level,
            @Parameter(description = "院校特性") @RequestParam(required = false) String feature
    ) {
        try {
            log.info("分页查询院校满意度, 页码: {}, 每页条数: {}, 名称: {}, 地区: {}", page, limit, name, location);

            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            params.put("name", name);
            params.put("location", location);
            params.put("department", department);
            params.put("level", level);
            params.put("feature", feature);

            // 分页查询
            Page<UniversitySatisfactionDTO> pageParam = new Page<>(page, limit);
            IPage<UniversitySatisfactionDTO> pageResult = universitySatisfactionService.getSatisfactionPage(pageParam, params);

            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询院校满意度异常", e);
            return Result.error(500, "查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取院校满意度详情
     */
    @GetMapping("/{universityId}")
    @Operation(summary = "获取院校满意度详情", description = "根据院校ID获取满意度详情")
    public Result<UniversitySatisfactionDTO> detail(
            @Parameter(description = "院校ID", required = true) @PathVariable Integer universityId
    ) {
        try {
            log.info("获取院校满意度详情, universityId: {}", universityId);

            UniversitySatisfactionDTO dto = universitySatisfactionService.getSatisfactionByUniversityId(universityId);
            if (dto == null) {
                return Result.error(404, "未找到该院校的满意度信息");
            }

            return Result.success(dto);
        } catch (Exception e) {
            log.error("获取院校满意度详情异常, universityId: {}", universityId, e);
            return Result.error(500, "获取详情失败: " + e.getMessage());
        }
    }

    /**
     * 保存或更新院校满意度
     */
    @PostMapping("/save")
    @Operation(summary = "保存或更新院校满意度", description = "保存或更新特定院校的满意度数据")
    public Result<?> save(@RequestBody UniversitySatisfaction satisfaction) {
        try {
            log.info("保存或更新院校满意度, universityId: {}", satisfaction.getUniversityId());

            boolean success = universitySatisfactionService.saveOrUpdateSatisfaction(satisfaction);
            if (success) {
                return Result.success("保存成功");
            } else {
                return Result.error(500, "保存失败");
            }
        } catch (IllegalArgumentException e) {
            log.warn("保存院校满意度参数错误: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("保存院校满意度异常", e);
            return Result.error(500, "保存失败: " + e.getMessage());
        }
    }
}
