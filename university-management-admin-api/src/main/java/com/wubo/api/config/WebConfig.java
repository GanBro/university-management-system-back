package com.wubo.api.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源映射，注意 Windows 路径使用 file:/// 开头
        String path = "file:///" + uploadPath.replace("\\", "/");

        // 通用文件访问
        registry.addResourceHandler("/files/**")
                .addResourceLocations(path);

        // Logo 专用目录
        registry.addResourceHandler("/files/logos/**")
                .addResourceLocations(path + "logos/");

        // 头像专用目录
        registry.addResourceHandler("/files/avatars/**")
                .addResourceLocations(path + "avatars/");

        // Knife4j 资源
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
