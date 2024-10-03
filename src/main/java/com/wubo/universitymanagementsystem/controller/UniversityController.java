package com.wubo.universitymanagementsystem.controller;

import com.wubo.universitymanagementsystem.common.Result;
import com.wubo.universitymanagementsystem.entity.University;
import com.wubo.universitymanagementsystem.service.UniversityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/universities")
public class UniversityController {

    @Autowired
    private UniversityService universityService;

    // 获取所有大学信息
    @GetMapping
    public Result<List<University>> getAllUniversities() {
        List<University> universities = universityService.getAllUniversities();
        return Result.success(universities);
    }

    // 添加大学
    @PostMapping
    public Result<String> addUniversity(@RequestBody University university) {
        universityService.addUniversity(university);
        return Result.success("University added successfully");
    }

    // 更新大学信息
    @PutMapping("/{id}")
    public Result<String> updateUniversity(@PathVariable Long id, @RequestBody University university) {
        university.setId(id);
        universityService.updateUniversity(university);
        return Result.success("University updated successfully");
    }

    // 删除大学
    @DeleteMapping("/{id}")
    public Result<String> deleteUniversity(@PathVariable Long id) {
        universityService.deleteUniversity(id);
        return Result.success("University deleted successfully");
    }
}
