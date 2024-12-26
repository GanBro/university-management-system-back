package com.wubo.universitymanagement;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.wubo.universitymanagement.mapper") // 显式指定 Mapper 包
public class UniversityManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(UniversityManagementApplication.class, args);
    }
}
