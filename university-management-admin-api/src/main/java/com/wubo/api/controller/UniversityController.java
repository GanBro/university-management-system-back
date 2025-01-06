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
@RequestMapping("/api/universities")
@Tag(name = "高校管理接口", description = "提供高校的增删改查、导出功能")
public class UniversityController {

    @Autowired
    private UniversityService universityService;

    @Operation(summary = "分页查询高校列表", description = "根据分页参数、名称、所在省份、类型、级别查询高校列表")
    @Parameters({
            @Parameter(name = "page", description = "当前页码", required = true),
            @Parameter(name = "limit", description = "每页显示数量", required = true),
            @Parameter(name = "name", description = "高校名称", required = false),
            @Parameter(name = "province", description = "高校所在省份", required = false),
            @Parameter(name = "type", description = "高校类型", required = false),
            @Parameter(name = "level", description = "高校级别", required = false)
    })
    @GetMapping
    public Result<Page<University>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String level
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("province", province);
        params.put("type", type);
        params.put("level", level);

        return Result.success(universityService.getUniversityList(page, limit, params));
    }

    @Operation(summary = "获取高校详情", description = "根据高校ID获取高校详细信息")
    @Parameter(name = "id", description = "高校ID", required = true)
    @GetMapping("/{id}")
    public Result<UniversityDetailDTO> detail(@PathVariable Integer id) {
        return Result.success(universityService.getUniversityDetail(id));
    }

    @Operation(summary = "新增高校", description = "创建新的高校信息")
    @PostMapping
    public Result<?> create(@RequestBody UniversityDTO universityDTO) {
        universityService.createUniversity(universityDTO);
        return Result.success(null);
    }

    @Operation(summary = "更新高校信息", description = "根据高校ID更新高校信息")
    @Parameter(name = "id", description = "高校ID", required = true)
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Integer id, @RequestBody UniversityDTO universityDTO) {
        universityDTO.setId(id);
        universityService.updateUniversity(universityDTO);
        log.info("更新高校信息：{}", universityDTO);
        return Result.success(null);
    }

    @Operation(summary = "删除高校", description = "根据高校ID删除高校信息")
    @Parameter(name = "id", description = "高校ID", required = true)
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        universityService.deleteUniversity(id);
        return Result.success(null);
    }

    @Operation(summary = "批量删除高校", description = "批量删除高校信息")
    @PostMapping("/batch")
    public Result<?> batchDelete(@RequestBody List<Integer> ids) {
        universityService.batchDeleteUniversities(ids);
        return Result.success(null);
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

            EasyExcel.write(response.getOutputStream(), UniversityExportDTO.class)
                    .includeColumnFieldNames(fieldList)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("高校列表")
                    .doWrite(exportData);

        } catch (Exception e) {
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().println(Result.error("下载文件失败：" + e.getMessage()));
        }
    }

    @Operation(summary = "获取筛选条件", description = "获取高校类型、级别、所在省份等筛选条件列表")
    @GetMapping("/options")
    public Result<Map<String, List<String>>> getOptions() {
        Map<String, List<String>> options = new HashMap<>();
        options.put("types", universityService.getAllTypes());
        options.put("levels", universityService.getAllLevels());
        options.put("provinces", universityService.getAllProvinces());
        return Result.success(options);
    }
}
