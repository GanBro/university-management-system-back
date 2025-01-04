// src/main/java/com/wubo/api/controller/UniversityController.java
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
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

@RestController
@RequestMapping("/api/universities")
public class UniversityController {

    @Autowired
    private UniversityService universityService;

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

    @GetMapping("/{id}")
    public Result<UniversityDetailDTO> detail(@PathVariable Integer id) {
        return Result.success(universityService.getUniversityDetail(id));
    }

    @PostMapping
    public Result<?> create(@RequestBody UniversityDTO universityDTO) {
        universityService.createUniversity(universityDTO);
        return Result.success(null);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Integer id, @RequestBody UniversityDTO universityDTO) {
        universityDTO.setId(id);
        universityService.updateUniversity(universityDTO);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        universityService.deleteUniversity(id);
        return Result.success(null);
    }

    @DeleteMapping("/batch")
    public Result<?> batchDelete(@RequestBody List<Integer> ids) {
        universityService.batchDeleteUniversities(ids);
        return Result.success(null);
    }
    @GetMapping("/export")
    public void export(
            HttpServletResponse response,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String fields // 改为接收字符串
    ) throws IOException {
        try {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("高校列表", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            params.put("name", name);
            params.put("province", province);
            params.put("type", type);
            params.put("level", level);

            // 处理字段参数
            List<String> fieldList = fields != null ?
                    Arrays.asList(fields.split(",")) :
                    Collections.emptyList();

            // 获取数据并转换
            List<UniversityExportDTO> exportData = universityService.getExportData(params, fieldList);

            // 导出数据
            EasyExcel.write(response.getOutputStream(), UniversityExportDTO.class)
                    .includeColumnFieldNames(fieldList) // 只导出选定的字段
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
}
