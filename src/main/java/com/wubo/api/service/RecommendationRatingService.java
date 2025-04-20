package com.wubo.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wubo.api.entity.RecommendationRating;

import java.util.List;
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

    /**
     * 获取大学专业推荐列表
     *
     * @param universityId 大学ID
     * @return 推荐列表
     */
    List<RecommendationRating> getRecommendationsByUniversityId(Integer universityId);

    /**
     * 更新大学专业推荐
     *
     * @param universityId 大学ID
     * @param data 更新数据
     * @return 是否成功
     */
    boolean updateUniversityRecommendation(Integer universityId, Map<String, Object> data);
}
