package com.wubo.universitymanagementsystem.service;

import com.wubo.universitymanagementsystem.entity.University;
import com.wubo.universitymanagementsystem.mapper.UniversityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UniversityService {

    @Autowired
    private UniversityMapper universityMapper;

    public List<University> getAllUniversities() {
        return universityMapper.selectList(null);
    }

    public void addUniversity(University university) {
        universityMapper.insert(university);
    }

    public void updateUniversity(University university) {
        universityMapper.updateById(university);
    }

    public void deleteUniversity(Long id) {
        universityMapper.deleteById(id);
    }
}
