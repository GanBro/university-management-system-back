package com.wubo.api.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import java.util.Collections;

public class CodeGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/university_management?useSSL=false&serverTimezone=UTC", "root", "your_password")
                .globalConfig(builder -> {
                    builder.author("wubo") // 设置作者
                            .outputDir(System.getProperty("user.dir") + "/src/main/java") // 设置生成路径
                            .disableOpenDir(); // 生成后不自动打开目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.wubo.universitymanagement") // 设置包路径
                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/src/main/resources/mapper")); // 设置 XML 映射文件生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("universities") // 设置需要生成的表名
                            .addTablePrefix("tbl_", "sys_"); // 设置表前缀过滤
                })
                .execute();
    }
}
