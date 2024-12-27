package com.wubo.api.controller.admin;

import com.wubo.api.entity.University;
import com.wubo.api.service.UniversityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UniversityService universityService;

    // 创建高校信息
    @PostMapping("/universities")
    public University createUniversity(@RequestBody University university) {
        return universityService.saveUniversity(university);
    }

    // 删除高校信息
    @DeleteMapping("/universities/{id}")
    public void deleteUniversity(@PathVariable Long id) {
        universityService.deleteUniversity(id);
    }
}
