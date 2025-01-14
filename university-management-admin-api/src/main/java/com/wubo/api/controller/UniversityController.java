package com.wubo.api.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.dto.*;
import com.wubo.api.entity.University;
import com.wubo.api.service.UniversityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
    @GetMapping
    public Result<Page<University>> list(@ModelAttribute UniversityQueryDTO queryDTO) {
        // 设置默认值
        if (queryDTO.getPage() == null) {
            queryDTO.setPage(1); // 默认页码
        }
        if (queryDTO.getLimit() == null) {
            queryDTO.setLimit(10); // 默认每页数量
        }
        log.info("分页查询高校列表, 查询参数: {}", queryDTO);
        // 业务逻辑
        Map<String, Object> params = new HashMap<>();
        params.put("name", queryDTO.getName());
        params.put("province", queryDTO.getProvince());
        params.put("type", queryDTO.getType());
        params.put("level", queryDTO.getLevel());
        params.put("adminDepartment", queryDTO.getAdminDepartment());

        Page<University> result = universityService.getUniversityList(
                queryDTO.getPage(), queryDTO.getLimit(), params);
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
    @GetMapping("/export")
    public void export(
            HttpServletResponse response,
            @ModelAttribute UniversityExportQueryDTO queryDTO
    ) throws IOException {
        log.info("导出高校列表, 筛选条件: name={}, province={}, type={}, level={}, fields={}",
                queryDTO.getName(), queryDTO.getProvince(), queryDTO.getType(), queryDTO.getLevel(), queryDTO.getFields());

        try {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("高校列表", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            params.put("name", queryDTO.getName());
            params.put("province", queryDTO.getProvince());
            params.put("type", queryDTO.getType());
            params.put("level", queryDTO.getLevel());

            // 解析导出的字段
            List<String> fieldList = queryDTO.getFields() != null ?
                    Arrays.asList(queryDTO.getFields().split(",")) :
                    Collections.emptyList();

            // 获取导出数据
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

    @GetMapping("/{id}/satisfaction")
    @Operation(summary = "获取院校满意度", description = "获取院校综合满意度、环境满意度、生活满意度等数据")
    public Result<Map<String, Object>> getSatisfaction(@PathVariable Integer id) {
        log.info("获取院校满意度数据, id: {}", id);
        try {
            Map<String, Object> satisfactionData = universityService.getSatisfactionData(id);
            return Result.success(satisfactionData);
        } catch (Exception e) {
            log.error("获取院校满意度数据失败", e);
            return Result.error("获取院校满意度数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/major-satisfaction")
    @Operation(summary = "获取专业满意度", description = "获取院校各专业满意度评分数据")
    public Result<List<Map<String, Object>>> getMajorSatisfaction(@PathVariable Integer id) {
        log.info("获取专业满意度数据, id: {}", id);
        try {
            List<Map<String, Object>> majorSatisfaction = universityService.getMajorSatisfaction(id);
            return Result.success(majorSatisfaction);
        } catch (Exception e) {
            log.error("获取专业满意度数据失败", e);
            return Result.error("获取专业满意度数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/recommendations")
    @Operation(summary = "获取专业推荐数据", description = "获取专业推荐人数和推荐指数数据")
    public Result<Map<String, Object>> getRecommendations(@PathVariable Integer id) {
        log.info("获取专业推荐数据, id: {}", id);
        try {
            Map<String, Object> recommendations = universityService.getRecommendationData(id);
            return Result.success(recommendations);
        } catch (Exception e) {
            log.error("获取专业推荐数据失败", e);
            return Result.error("获取专业推荐数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/consultations")
    @Operation(summary = "获取咨询列表", description = "获取院校咨询问答列表")
    public Result<List<Map<String, Object>>> getConsultations(@PathVariable Integer id) {
        log.info("获取咨询列表, id: {}", id);
        try {
            List<Map<String, Object>> consultations = universityService.getConsultations(id);
            return Result.success(consultations);
        } catch (Exception e) {
            log.error("获取咨询列表失败", e);
            return Result.error("获取咨询列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/consultations")
    @Operation(summary = "提交咨询", description = "提交院校咨询问题")
    public Result<?> submitConsultation(
            @PathVariable Integer id,
            @RequestBody @Valid ConsultationDTO consultationDTO
    ) {
        log.info("提交咨询, id: {}, data: {}", id, consultationDTO);
        try {
            universityService.submitConsultation(id, consultationDTO);
            return Result.success();
        } catch (Exception e) {
            log.error("提交咨询失败", e);
            return Result.error("提交咨询失败: " + e.getMessage());
        }
    }
}
