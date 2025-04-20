package com.wubo.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wubo.api.entity.SatisfactionRating;

import java.util.Map;

public interface SatisfactionRatingService extends IService<SatisfactionRating> {
    /**
     * 分页查询专业满意度列表
     *
     * @param page 分页参数
     * @param params 查询条件
     * @return 分页结果
     */
    IPage<SatisfactionRating> getSatisfactionRatingPage(Page<SatisfactionRating> page, Map<String, Object> params);
}
