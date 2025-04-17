package com.wubo.api.controller;

import com.wubo.api.dto.Result;
import com.wubo.api.entity.Admission;
import com.wubo.api.service.UniversityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admissions")
@Tag(name = "招生数据管理接口", description = "提供招生数据的增删改查功能")
public class AdmissionController {

    @Autowired
    private UniversityService universityService;

    @PostMapping
    @Operation(summary = "创建招生数据", description = "创建高校招生分数线及录取情况")
    public Result<?> createAdmissionData(@Valid @RequestBody Admission admission) {
        log.info("创建招生数据, data: {}", admission);
        try {
            boolean success = universityService.createAdmissionData(admission);
            if (success) {
                log.info("创建招生数据成功");
                return Result.success();
            } else {
                log.warn("创建招生数据失败");
                return Result.error("创建招生数据失败");
            }
        } catch (Exception e) {
            log.error("创建招生数据失败", e);
            return Result.error("创建招生数据失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新招生数据", description = "更新高校招生分数线及录取情况")
    public Result<?> updateAdmissionData(
            @PathVariable Integer id,
            @Valid @RequestBody Admission admission) {
        log.info("更新招生数据, id: {}, data: {}", id, admission);
        try {
            // Set the ID to ensure we update the correct record
            admission.setId(id);

            boolean success = universityService.updateAdmissionData(admission);
            if (success) {
                log.info("更新招生数据成功, id: {}", id);
                return Result.success();
            } else {
                log.warn("更新招生数据失败, id: {}", id);
                return Result.error("更新招生数据失败");
            }
        } catch (Exception e) {
            log.error("更新招生数据失败, id: {}", id, e);
            return Result.error("更新招生数据失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除招生数据", description = "删除高校招生分数线及录取情况")
    public Result<?> deleteAdmissionData(@PathVariable Integer id) {
        log.info("删除招生数据, id: {}", id);
        try {
            boolean success = universityService.deleteAdmissionData(id);
            if (success) {
                log.info("删除招生数据成功, id: {}", id);
                return Result.success();
            } else {
                log.warn("删除招生数据失败, id: {}", id);
                return Result.error("删除招生数据失败");
            }
        } catch (Exception e) {
            log.error("删除招生数据失败, id: {}", id, e);
            return Result.error("删除招生数据失败: " + e.getMessage());
        }
    }
}
