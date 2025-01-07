// NewsServiceImpl.java
package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.entity.News;
import com.wubo.api.mapper.NewsMapper;
import com.wubo.api.mapper.UniversityMapper;
import com.wubo.api.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Transactional
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsMapper newsMapper;
    @Autowired
    private UniversityMapper universityMapper;

    @Override
    public Page<News> getNewsList(Integer page, Integer limit, Map<String, Object> params) {
        Page<News> pageParam = new Page<>(page, limit);
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();

        String type = (String) params.get("type");
        String keyword = (String) params.get("keyword");
        Integer status = (Integer) params.get("status");
        Integer universityId = (Integer) params.get("universityId");  // 新增参数

        if (StringUtils.hasText(type)) {
            queryWrapper.eq(News::getType, type);
        }
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(News::getTitle, keyword)
                    .or()
                    .like(News::getContent, keyword);
        }
        if (status != null) {
            queryWrapper.eq(News::getStatus, status);
        }
        if (universityId != null) {
            queryWrapper.eq(News::getUniversityId, universityId);
        }

        queryWrapper.orderByDesc(News::getPublishTime, News::getId);
        Page<News> newsPage = newsMapper.selectPage(pageParam, queryWrapper);

        // 填充大学信息
        if (newsPage.getRecords() != null && !newsPage.getRecords().isEmpty()) {
            for (News news : newsPage.getRecords()) {
                if (news.getUniversityId() != null) {
                    news.setUniversity(universityMapper.selectById(news.getUniversityId()));
                }
            }
        }

        return newsPage;
    }

    @Override
    public News getNewsDetail(Integer id) {
        return newsMapper.selectById(id);
    }

    @Override
    @Transactional
    public void createNews(News news) {
        newsMapper.insert(news);
    }

    @Override
    @Transactional
    public void updateNews(News news) {
        newsMapper.updateById(news);
    }

    @Override
    @Transactional
    public void deleteNews(Integer id) {
        newsMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void publishNews(Integer id) {
        News news = new News();
        news.setId(id);
        news.setStatus(1);
        news.setPublishTime(LocalDateTime.now());
        newsMapper.updateById(news);
    }

    @Override
    @Transactional
    public void incrementViewCount(Integer id) {
        News news = newsMapper.selectById(id);
        if (news != null) {
            news.setViewCount(news.getViewCount() + 1);
            newsMapper.updateById(news);
        }
    }
}
