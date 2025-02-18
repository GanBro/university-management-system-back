// NewsDTO.java
package com.wubo.api.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NewsDTO {
    private Integer id;
    private String title;
    private String content;
    private String type;
    private Integer status;
    private LocalDateTime publishTime;
    private Integer viewCount;
    private String author;
    private Integer universityId;
    private String universityName;  // 大学名称
}
