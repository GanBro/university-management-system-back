package com.wubo.api.controller.api;

import com.wubo.api.dto.Result;
import com.wubo.api.entity.University;
import com.wubo.api.service.UniversityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/universities")
public class UniversityController {

    @Autowired
    private UniversityService universityService;

    // 获取所有高校
    @GetMapping
    public Result<List<University>> getAllUniversities() {
        List<University> universities = universityService.getAllUniversities();
        return Result.success("获取所有高校成功", universities); // 添加自定义消息
    }

    // 根据省份查询高校
    @GetMapping("/province/{province}")
    public Result<List<University>> getUniversitiesByProvince(@PathVariable String province) {
        List<University> universities = universityService.getUniversitiesByProvince(province);
        return Result.success("获取指定省份高校成功", universities); // 添加自定义消息
    }

    // 模糊搜索高校
    @GetMapping("/search")
    public Result<List<University>> searchUniversities(@RequestParam String keyword) {
        List<University> universities = universityService.searchUniversitiesByName(keyword);
        return Result.success("高校搜索成功", universities); // 添加自定义消息
    }

    // 新增或更新高校
    @PostMapping
    public Result<Boolean> saveOrUpdateUniversity(@RequestBody University university) {
        boolean result = universityService.saveOrUpdateUniversity(university);
        if (result) {
            return Result.success("高校保存或更新成功", true); // 添加成功消息
        } else {
            return Result.error(500, "高校保存或更新失败"); // 使用通用错误方法
        }
    }

    // 删除高校
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteUniversity(@PathVariable Long id) {
        universityService.deleteUniversity(id);
        return Result.success("高校删除成功", true); // 添加自定义消息
    }
}
