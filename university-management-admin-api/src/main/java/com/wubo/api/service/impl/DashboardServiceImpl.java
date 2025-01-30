package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wubo.api.entity.*;
import com.wubo.api.mapper.*;
import com.wubo.api.service.DashboardService;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class DashboardServiceImpl implements DashboardService {

    @Resource
    private InteractionMapper interactionMapper;

    @Resource
    private InteractionReplyMapper replyMapper;

    @Resource
    private UserUniversityFollowMapper followMapper;

    @Resource
    private UniversityMapper universityMapper;

    @Resource
    private UserMapper userMapper;

    // 获取仪表盘统计数据
    @Override
    @Cacheable(value = "dashboardStats", unless = "#result == null")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 统计高校总数
        long totalUniversityCount = universityMapper.selectCount(null);
        stats.put("totalUniversityCount", totalUniversityCount);

        // 统计最近30天新增高校数量
        QueryWrapper<University> recentUniversityQuery = new QueryWrapper<>();
        recentUniversityQuery.ge("created_at", getLast30Days());
        long recentUniversityCount = universityMapper.selectCount(recentUniversityQuery);
        stats.put("recentUniversityCount", recentUniversityCount);

        // 统计高校分布（按省份分组统计）
        List<Map<String, Object>> universityDistribution = universityMapper.selectUniversityDistribution();
        stats.put("universityDistribution", universityDistribution != null ? universityDistribution : List.of());

        // 统计活跃用户数量（过去7天内活跃）
        String last7Days = getLast7Days();
        QueryWrapper<com.wubo.api.entity.User> activeUserQuery = new QueryWrapper<>();
        activeUserQuery.ge("last_login", last7Days);
        long activeUserCount = userMapper.selectCount(activeUserQuery);
        stats.put("activeUserCount", activeUserCount);

        // 统计月活跃用户数量（过去30天内活跃）
        String last30Days = getLast30Days();
        QueryWrapper<com.wubo.api.entity.User> monthlyActiveUserQuery = new QueryWrapper<>();
        monthlyActiveUserQuery.ge("last_login", last30Days);
        long monthlyActiveUserCount = userMapper.selectCount(monthlyActiveUserQuery);
        stats.put("monthlyActiveUserCount", monthlyActiveUserCount);

        return stats;
    }

    @Override
    public List<Map<String, Object>> getUniversityGrowthTrend() {
        QueryWrapper<University> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("YEAR(created_at) as year", "COUNT(*) as university_count");
        queryWrapper.groupBy("YEAR(created_at)");
        queryWrapper.orderByAsc("YEAR(created_at)");

        // 查询结果返回 List<Map<String, Object>>
        return universityMapper.selectMaps(queryWrapper);
    }

    /**
     * 获取当前时间30天前的日期
     * @return 格式化的日期字符串
     */
    private String getLast30Days() {
        return LocalDate.now().minusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 获取当前时间7天前的日期
     * @return 格式化的日期字符串
     */
    private String getLast7Days() {
        return LocalDate.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @Override
    @Cacheable(value = "activityStats", unless = "#result == null")
    public Map<String, Object> getActivityStats() {
        Map<String, Object> stats = new HashMap<>();

        // 使用 MyBatis-Plus 获取用户行为统计
        Map<String, Object> behaviorStats = new HashMap<>();
        // 统计咨询数量
        long consultationCount = interactionMapper.selectCount(null);
        // 统计回复数量
        long replyCount = replyMapper.selectCount(null);
        // 统计关注数量
        long followCount = followMapper.selectCount(null);

        behaviorStats.put("consultationCount", consultationCount);
        behaviorStats.put("replyCount", replyCount);
        behaviorStats.put("followCount", followCount);

        // 获取活跃用户排行
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("last_login", thirtyDaysAgo)
                .orderByDesc("last_login")
                .last("LIMIT 10");
        List<User> activeUsers = userMapper.selectList(queryWrapper);

        List<Map<String, Object>> userRanking = activeUsers.stream()
                .map(user -> {
                    Map<String, Object> ranking = new HashMap<>();
                    ranking.put("userId", user.getUserId());
                    ranking.put("userName", user.getUsername());

                    // 统计用户行为总数
                    long userActions = getUserActionCount(user.getUserId());
                    ranking.put("actionCount", userActions);
                    ranking.put("lastActiveTime", user.getLastLogin());

                    return ranking;
                })
                .collect(Collectors.toList());

        stats.put("behaviorStats", behaviorStats);
        stats.put("userRanking", userRanking);

        return stats;
    }

    private long getUserActionCount(Integer userId) {
        // 统计用户的互动数
        QueryWrapper<Interaction> interactionQuery = new QueryWrapper<>();
        interactionQuery.eq("user_id", userId);
        long interactionCount = interactionMapper.selectCount(interactionQuery);

        // 统计用户的回复数
        QueryWrapper<InteractionReply> replyQuery = new QueryWrapper<>();
        replyQuery.eq("user_id", userId);
        long replyCount = replyMapper.selectCount(replyQuery);

        // 统计用户的关注数
        QueryWrapper<UserUniversityFollow> followQuery = new QueryWrapper<>();
        followQuery.eq("user_id", userId);
        long followCount = followMapper.selectCount(followQuery);

        return interactionCount + replyCount + followCount;
    }
}
