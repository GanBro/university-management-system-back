// RecommendationRatingServiceImpl.java
package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wubo.api.entity.RecommendationRating;
import com.wubo.api.mapper.RecommendationRatingMapper;
import com.wubo.api.service.RecommendationRatingService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class RecommendationRatingServiceImpl extends ServiceImpl<RecommendationRatingMapper, RecommendationRating> implements RecommendationRatingService {

    @Override
    public IPage<RecommendationRating> getRecommendationRatingPage(Page<RecommendationRating> page, Map<String, Object> params) {
        LambdaQueryWrapper<RecommendationRating> queryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        Integer universityId = (Integer) params.get("universityId");
        String majorCategory = (String) params.get("majorCategory");
        Double minRating = (Double) params.get("minRating");
        Double maxRating = (Double) params.get("maxRating");

        if (universityId != null) {
            queryWrapper.eq(RecommendationRating::getUniversityId, universityId);
        }

        if (StringUtils.hasText(majorCategory)) {
            queryWrapper.like(RecommendationRating::getMajorCategory, majorCategory);
        }

        if (minRating != null) {
            queryWrapper.ge(RecommendationRating::getRating, minRating);
        }

        if (maxRating != null) {
            queryWrapper.le(RecommendationRating::getRating, maxRating);
        }

        // 按评分降序排序
        queryWrapper.orderByDesc(RecommendationRating::getRating);

        return this.page(page, queryWrapper);
    }
}
