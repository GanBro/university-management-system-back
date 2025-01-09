package com.wubo.api.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.dto.Result;
import com.wubo.api.dto.UniversityDTO;
import com.wubo.api.dto.UniversityDetailDTO;
import com.wubo.api.dto.UniversityExportDTO;
import com.wubo.api.entity.University;
import com.wubo.api.service.UniversityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/universities")
@Tag(name = "高校管理接口", description = "提供高校的增删改查、导出功能")
public class UniversityController {

    @Autowired
    private UniversityService universityService;

    @Operation(summary = "分页查询高校列表", description = "根据分页参数、名称、所在省份、类型、级别、主管部门查询高校列表")
    @Parameters({
            @Parameter(name = "page", description = "当前页码", required = true),
            @Parameter(name = "limit", description = "每页显示数量", required = true),
            @Parameter(name = "name", description = "高校名称", required = false),
            @Parameter(name = "province", description = "高校所在省份", required = false),
            @Parameter(name = "type", description = "高校类型", required = false),
            @Parameter(name = "level", description = "高校级别", required = false),
            @Parameter(name = "adminDepartment", description = "主管部门", required = false)
    })
    @GetMapping
    public Result<Page<University>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String adminDepartment
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("province", province);
        params.put("type", type);
        params.put("level", level);
        params.put("adminDepartment", adminDepartment);

        log.info("查询高校列表, 页码: {}, 每页数量: {}, 查询参数: {}", page, limit, params);
        Page<University> result = universityService.getUniversityList(page, limit, params);
        log.info("查询高校列表完成, 总记录数: {}", result.getTotal());
        return Result.success(result);
    }

    @Operation(summary = "获取高校详情", description = "根据高校ID获取高校详细信息")
    @Parameter(name = "id", description = "高校ID", required = true)
    @GetMapping("/{id}")
    public Result<UniversityDetailDTO> detail(@PathVariable Integer id) {
        log.info("获取高校详情, id: {}", id);
        UniversityDetailDTO detail = universityService.getUniversityDetail(id);
        if (detail == null) {
            log.warn("高校详情不存在, id: {}", id);
            return Result.error("高校不存在");
        }
        log.info("获取高校详情成功, id: {}", id);
        return Result.success(detail);
    }

    @Operation(summary = "新增高校", description = "创建新的高校信息")
    @PostMapping
    public Result<?> create(@RequestBody UniversityDTO universityDTO) {
        log.info("创建高校信息: {}", universityDTO);
        try {
            universityService.createUniversity(universityDTO);
            log.info("创建高校成功, 名称: {}", universityDTO.getName());
            return Result.success(null);
        } catch (Exception e) {
            log.error("创建高校失败", e);
            return Result.error("创建高校失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新高校信息", description = "根据高校ID更新高校信息")
    @Parameter(name = "id", description = "高校ID", required = true)
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Integer id, @RequestBody UniversityDTO universityDTO) {
        log.info("更新高校信息, id: {}, 更新内容: {}", id, universityDTO);
        try {
            universityDTO.setId(id);
            universityService.updateUniversity(universityDTO);
            log.info("更新高校信息成功, id: {}", id);
            return Result.success(null);
        } catch (Exception e) {
            log.error("更新高校信息失败, id: {}", id, e);
            return Result.error("更新高校信息失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除高校", description = "根据高校ID删除高校信息")
    @Parameter(name = "id", description = "高校ID", required = true)
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        log.info("删除高校, id: {}", id);
        try {
            universityService.deleteUniversity(id);
            log.info("删除高校成功, id: {}", id);
            return Result.success(null);
        } catch (Exception e) {
            log.error("删除高校失败, id: {}", id, e);
            return Result.error("删除高校失败: " + e.getMessage());
        }
    }

    @Operation(summary = "批量删除高校", description = "批量删除高校信息")
    @PostMapping("/batch")
    public Result<?> batchDelete(@RequestBody List<Integer> ids) {
        log.info("批量删除高校, ids: {}", ids);
        try {
            universityService.batchDeleteUniversities(ids);
            log.info("批量删除高校成功, 数量: {}", ids.size());
            return Result.success(null);
        } catch (Exception e) {
            log.error("批量删除高校失败", e);
            return Result.error("批量删除高校失败: " + e.getMessage());
        }
    }

    @Operation(summary = "导出高校列表", description = "根据筛选条件导出高校数据")
    @Parameters({
            @Parameter(name = "name", description = "高校名称", required = false),
            @Parameter(name = "province", description = "高校所在省份", required = false),
            @Parameter(name = "type", description = "高校类型", required = false),
            @Parameter(name = "level", description = "高校级别", required = false),
            @Parameter(name = "fields", description = "导出的字段列表，逗号分隔", required = false)
    })
    @GetMapping("/export")
    public void export(
            HttpServletResponse response,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String fields
    ) throws IOException {
        log.info("导出高校列表, 筛选条件: name={}, province={}, type={}, level={}, fields={}",
                name, province, type, level, fields);
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("高校列表", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

            Map<String, Object> params = new HashMap<>();
            params.put("name", name);
            params.put("province", province);
            params.put("type", type);
            params.put("level", level);

            List<String> fieldList = fields != null ?
                    Arrays.asList(fields.split(",")) :
                    Collections.emptyList();

            List<UniversityExportDTO> exportData = universityService.getExportData(params, fieldList);

            log.info("开始导出高校数据, 数据量: {}", exportData.size());
            EasyExcel.write(response.getOutputStream(), UniversityExportDTO.class)
                    .includeColumnFieldNames(fieldList)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("高校列表")
                    .doWrite(exportData);
            log.info("导出高校数据完成");

        } catch (Exception e) {
            log.error("导出高校列表失败", e);
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().println(Result.error("下载文件失败：" + e.getMessage()));
        }
    }

    @Operation(summary = "获取筛选条件", description = "获取高校类型、级别、所在省份等筛选条件列表")
    @GetMapping("/options")
    public Result<Map<String, List<String>>> getOptions() {
        log.info("获取高校筛选条件");
        try {
            Map<String, List<String>> options = new HashMap<>();
            options.put("types", universityService.getAllTypes());
            options.put("levels", universityService.getAllLevels());
            options.put("provinces", universityService.getAllProvinces());
            log.info("获取筛选条件成功");
            return Result.success(options);
        } catch (Exception e) {
            log.error("获取筛选条件失败", e);
            return Result.error("获取筛选条件失败: " + e.getMessage());
        }
    }
}
