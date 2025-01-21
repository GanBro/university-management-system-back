package com.wubo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

// InteractionReplyDTO.java
@Data
@Schema(description = "互动回复DTO")
public class InteractionReplyDTO {
    @Schema(description = "回复ID")
    private Integer id;

    @Schema(description = "回复内容")
    private String content;

    @Schema(description = "是否官方回复")
    private Boolean isOfficial;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "用户ID")
    private Integer userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "用户头像")
    private String avatar;
}
