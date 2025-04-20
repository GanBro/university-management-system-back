package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wubo.api.entity.RecommendationRating;
import com.wubo.api.mapper.RecommendationRatingMapper;
import com.wubo.api.service.RecommendationRatingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
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

    @Override
    public List<RecommendationRating> getRecommendationsByUniversityId(Integer universityId) {
        LambdaQueryWrapper<RecommendationRating> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RecommendationRating::getUniversityId, universityId);
        queryWrapper.orderByDesc(RecommendationRating::getRating);
        return this.list(queryWrapper);
    }

    @Override
    @Transactional
    public boolean updateUniversityRecommendation(Integer universityId, Map<String, Object> data) {
        // 构造推荐对象
        RecommendationRating rating = new RecommendationRating();

        // 设置ID（如果存在）
        if (data.containsKey("id") && data.get("id") != null) {
            rating.setId((Integer) data.get("id"));
        }

        // 设置大学ID
        rating.setUniversityId(universityId);

        // 设置专业名称（使用name或majorCategory）
        if (data.containsKey("name") && data.get("name") != null) {
            rating.setMajorCategory((String) data.get("name"));
        } else if (data.containsKey("majorCategory") && data.get("majorCategory") != null) {
            rating.setMajorCategory((String) data.get("majorCategory"));
        } else {
            return false; // 专业名称不能为空
        }

        // 设置评分
        if (data.containsKey("rating") && data.get("rating") != null) {
            if (data.get("rating") instanceof Double) {
                rating.setRating((Double) data.get("rating"));
            } else if (data.get("rating") instanceof String) {
                rating.setRating(Double.parseDouble((String) data.get("rating")));
            } else if (data.get("rating") instanceof Integer) {
                rating.setRating((Integer) data.get("rating"));
            }
        } else {
            return false; // 推荐评分不能为空
        }

        // 设置推荐人数
        if (data.containsKey("count") && data.get("count") != null) {
            rating.setRecommendationCount((Integer) data.get("count"));
        } else if (data.containsKey("recommendationCount") && data.get("recommendationCount") != null) {
            rating.setRecommendationCount((Integer) data.get("recommendationCount"));
        } else {
            rating.setRecommendationCount(0); // 默认值
        }

        // 保存或更新
        return this.saveOrUpdate(rating);
    }
}
