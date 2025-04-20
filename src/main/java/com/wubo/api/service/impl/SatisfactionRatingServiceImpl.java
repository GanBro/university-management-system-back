package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wubo.api.entity.SatisfactionRating;
import com.wubo.api.mapper.SatisfactionRatingMapper;
import com.wubo.api.service.SatisfactionRatingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SatisfactionRatingServiceImpl extends ServiceImpl<SatisfactionRatingMapper, SatisfactionRating> implements SatisfactionRatingService {

    @Override
    public IPage<SatisfactionRating> getSatisfactionRatingPage(Page<SatisfactionRating> page, Map<String, Object> params) {
        LambdaQueryWrapper<SatisfactionRating> queryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        Integer universityId = (Integer) params.get("universityId");
        String category = (String) params.get("category");
        Double minRating = (Double) params.get("minRating");
        Double maxRating = (Double) params.get("maxRating");
        List<String> excludeCategories = (List<String>) params.get("excludeCategories");

        // 筛选特定大学的数据
        if (universityId != null) {
            queryWrapper.eq(SatisfactionRating::getUniversityId, universityId);
        }

        // 类别模糊匹配
        if (StringUtils.hasText(category)) {
            queryWrapper.like(SatisfactionRating::getCategory, category);
        }

        // 排除特定类别（如"综合评价"、"环境"、"生活"等）
        if (excludeCategories != null && !excludeCategories.isEmpty()) {
            queryWrapper.notIn(SatisfactionRating::getCategory, excludeCategories);
        }

        // 评分范围筛选
        if (minRating != null) {
            queryWrapper.ge(SatisfactionRating::getRating, minRating);
        }

        if (maxRating != null) {
            queryWrapper.le(SatisfactionRating::getRating, maxRating);
        }

        // 按评分降序排序
        queryWrapper.orderByDesc(SatisfactionRating::getRating);

        // 执行查询
        IPage<SatisfactionRating> result = this.page(page, queryWrapper);

        // 修正：确保total字段不为0
        if (result.getTotal() == 0 && !result.getRecords().isEmpty()) {
            long count = this.count(queryWrapper);
            ((Page<SatisfactionRating>) result).setTotal(count > 0 ? count : result.getRecords().size());
        }

        return result;
    }
}
