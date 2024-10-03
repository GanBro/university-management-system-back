package com.wubo.universitymanagementsystem.controller;

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

    @GetMapping
    public List<University> getAllUniversities() {
        return universityService.getAllUniversities();
    }

    @PostMapping
    public void addUniversity(@RequestBody University university) {
        universityService.addUniversity(university);
    }

    @PutMapping("/{id}")
    public void updateUniversity(@PathVariable Long id, @RequestBody University university) {
        university.setId(id);
        universityService.updateUniversity(university);
    }

    @DeleteMapping("/{id}")
    public void deleteUniversity(@PathVariable Long id) {
        universityService.deleteUniversity(id);
    }
}
