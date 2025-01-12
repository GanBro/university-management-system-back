package com.wubo.api.controller;

import com.wubo.api.dto.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@Tag(name = "文件上传接口", description = "提供文件上传功能")
public class FileController {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.logo-path}")
    private String logoPath;

    @Value("${file.upload.url-prefix}")
    private String urlPrefix;

    @Value("${file.upload.logo-url-prefix}")
    private String logoUrlPrefix;

    @Operation(summary = "文件上传", description = "上传文件并返回访问URL")
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            log.warn("上传失败：文件为空");
            return Result.error(400, "请选择要上传的文件");
        }

        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + suffix;
        File dest = new File(uploadPath + newFileName);

        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        try {
            file.transferTo(dest);
            String fileUrl = urlPrefix + newFileName;
            log.info("文件上传成功，访问地址: {}", fileUrl);
            return Result.success(fileUrl);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error(500, "文件上传失败：" + e.getMessage());
        }
    }

    @Operation(summary = "上传Logo", description = "上传高校Logo并返回访问URL")
    @PostMapping("/upload/logo")
    public Result<String> uploadLogo(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            log.warn("上传失败：Logo文件为空");
            return Result.error(400, "请选择要上传的Logo文件");
        }

        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

        // 检查文件类型
        if (!".jpg".equals(suffix) && !".jpeg".equals(suffix) && !".png".equals(suffix)) {
            return Result.error(400, "Logo只支持JPG、JPEG、PNG格式");
        }

        // 生成新的文件名
        String newFileName = "logo_" + UUID.randomUUID().toString() + suffix;

        // 确保目录存在
        File logoDir = new File(logoPath);
        if (!logoDir.exists()) {
            logoDir.mkdirs();
        }

        File dest = new File(logoPath + newFileName);

        try {
            file.transferTo(dest);
            String fileUrl = logoUrlPrefix + newFileName;
            log.info("Logo上传成功，访问地址: {}", fileUrl);
            return Result.success(fileUrl);
        } catch (IOException e) {
            log.error("Logo上传失败", e);
            return Result.error(500, "Logo上传失败：" + e.getMessage());
        }
    }
}
