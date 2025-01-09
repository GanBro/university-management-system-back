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

    @Value("${file.upload.url-prefix}")
    private String urlPrefix;

    @Operation(summary = "文件上传", description = "上传文件并返回访问URL")
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            log.warn("上传失败：文件为空");
            return Result.error(400, "请选择要上传的文件");
        }

        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 获取文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 生成新的文件名
        String newFileName = UUID.randomUUID().toString() + suffix;

        // 创建文件对象
        File dest = new File(uploadPath + newFileName);

        // 判断目录是否存在，不存在则创建
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        try {
            // 保存文件
            file.transferTo(dest);
            // 返回文件访问URL
            String fileUrl = urlPrefix + newFileName;
            log.info("文件上传成功，访问地址: {}", fileUrl);
            return Result.success(fileUrl);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error(500, "文件上传失败：" + e.getMessage());
        }
    }
}
