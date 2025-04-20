package com.wubo.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wubo.api.entity.RecommendationRating;

import java.util.Map;

public interface RecommendationRatingService extends IService<RecommendationRating> {
    /**
     * 分页查询专业推荐列表
     *
     * @param page 分页参数
     * @param params 查询条件
     * @return 分页结果
     */
    IPage<RecommendationRating> getRecommendationRatingPage(Page<RecommendationRating> page, Map<String, Object> params);
}
