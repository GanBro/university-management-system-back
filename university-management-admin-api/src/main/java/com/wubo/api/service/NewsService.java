// NewsService.java
package com.wubo.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.entity.News;
import java.util.Map;

public interface NewsService {
    Page<News> getNewsList(Integer page, Integer limit, Map<String, Object> params);
    News getNewsDetail(Integer id);
    void createNews(News news);
    void updateNews(News news);
    void deleteNews(Integer id);
    void publishNews(Integer id);
    void incrementViewCount(Integer id);
}
