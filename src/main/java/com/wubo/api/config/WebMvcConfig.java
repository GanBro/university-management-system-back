package com.wubo.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 处理Windows路径格式
        String basePath = "file:///" + uploadPath.replace("\\", "/");
        
        // 通用文件上传路径
        registry.addResourceHandler("/files/**")
                .addResourceLocations(basePath);
                
        // Logo图片上传路径
        registry.addResourceHandler("/files/logos/**")
                .addResourceLocations(basePath + "logos/");
                
        // 头像图片上传路径
        registry.addResourceHandler("/files/avatars/**")
                .addResourceLocations(basePath + "avatars/");
                
        // 新闻图片上传路径
        registry.addResourceHandler("/files/news/**")
                .addResourceLocations(basePath + "news/");

        // Knife4j API文档资源
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
                
        // Markdown编辑器静态资源
        registry.addResourceHandler("/markdown/**")
                .addResourceLocations("classpath:/static/markdown/");
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
    }
} 