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

    @Value("${file.upload.avatar-path}")
    private String avatarPath;

    @Value("${file.upload.url-prefix}")
    private String urlPrefix;

    @Value("${file.upload.logo-url-prefix}")
    private String logoUrlPrefix;

    @Value("${file.upload.avatar-url-prefix}")
    private String avatarUrlPrefix;

    @Operation(summary = "上传头像", description = "上传用户头像并返回访问URL")
    @PostMapping("/upload/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            log.warn("上传失败：头像文件为空");
            return Result.error(400, "请选择要上传的头像文件");
        }

        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

        // 检查文件类型
        if (!".jpg".equals(suffix) && !".jpeg".equals(suffix) && !".png".equals(suffix)) {
            return Result.error(400, "头像只支持JPG、JPEG、PNG格式");
        }

        // 检查文件大小（2MB）
        if (file.getSize() > 2 * 1024 * 1024) {
            return Result.error(400, "头像文件大小不能超过2MB");
        }

        // 生成新的文件名
        String newFileName = UUID.randomUUID().toString() + suffix;

        // 确保目录存在
        File avatarDir = new File(avatarPath);
        if (!avatarDir.exists()) {
            avatarDir.mkdirs();
        }

        File dest = new File(avatarPath + newFileName);

        try {
            file.transferTo(dest);
            // 返回相对路径，不包含域名和端口
            String fileUrl = "/files/avatars/" + newFileName;
            log.info("头像上传成功，访问地址: {}", fileUrl);
            return Result.success(fileUrl);
        } catch (IOException e) {
            log.error("头像上传失败", e);
            return Result.error(500, "头像上传失败：" + e.getMessage());
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
        String newFileName = UUID.randomUUID().toString() + suffix;

        // 确保目录存在
        File logoDir = new File(logoPath);
        if (!logoDir.exists()) {
            logoDir.mkdirs();
        }

        File dest = new File(logoPath + newFileName);

        try {
            file.transferTo(dest);
            // 返回相对路径，不包含域名和端口
            String fileUrl = "/files/logos/" + newFileName;
            log.info("Logo上传成功，访问地址: {}", fileUrl);
            return Result.success(fileUrl);
        } catch (IOException e) {
            log.error("Logo上传失败", e);
            return Result.error(500, "Logo上传失败：" + e.getMessage());
        }
    }
}
