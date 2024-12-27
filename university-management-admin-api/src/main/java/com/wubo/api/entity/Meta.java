package com.wubo.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Meta {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String title;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String icon;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean affix;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> roles;
}
