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
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

    @Override
    @Cacheable(value = "dashboardStats", unless = "#result == null")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.putAll(getUniversityStats());
        stats.putAll(getUserStats());
        stats.putAll(getInteractionStats());
        return stats;
    }

    @Override
    public List<Map<String, Object>> getUniversityGrowthTrend() {
        QueryWrapper<University> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("YEAR(created_at) as year", "COUNT(*) as university_count")
                .groupBy("YEAR(created_at)")
                .orderByAsc("YEAR(created_at)");
        return universityMapper.selectMaps(queryWrapper);
    }

    private Map<String, Object> getUniversityStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUniversityCount", universityMapper.selectCount(null));

        QueryWrapper<University> recentQuery = new QueryWrapper<>();
        recentQuery.ge("created_at", getLast30Days());
        stats.put("recentUniversityCount", universityMapper.selectCount(recentQuery));

        stats.put("universityDistribution",
                universityMapper.selectUniversityDistribution());

        return stats;
    }

    private Map<String, Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();

        QueryWrapper<User> weeklyQuery = new QueryWrapper<>();
        weeklyQuery.ge("last_login", getLast7Days());
        stats.put("activeUserCount", userMapper.selectCount(weeklyQuery));

        QueryWrapper<User> monthlyQuery = new QueryWrapper<>();
        monthlyQuery.ge("last_login", getLast30Days());
        stats.put("monthlyActiveUserCount", userMapper.selectCount(monthlyQuery));

        return stats;
    }

    private Map<String, Object> getInteractionStats() {
        Map<String, Object> stats = new HashMap<>();

        // 计算互动总数（仅包括咨询记录）
        long totalInteractions = interactionMapper.selectCount(null);
        stats.put("totalInteractionCount", totalInteractions);

        // 统计待处理的咨询数量
        QueryWrapper<Interaction> pendingQuery = new QueryWrapper<>();
        pendingQuery.eq("status", "pending");
        long pendingCount = interactionMapper.selectCount(pendingQuery);
        stats.put("pendingInteractionCount", pendingCount);

        // 计算用户行为统计
        Map<String, Object> behaviorStats = new HashMap<>();
        behaviorStats.put("consultationCount", totalInteractions);
        // 获取回复数量
        long replyCount = replyMapper.selectCount(null);
        behaviorStats.put("replyCount", replyCount);
        // 获取关注数量
        long followCount = followMapper.selectCount(null);
        behaviorStats.put("followCount", followCount);
        stats.put("userBehaviorStats", behaviorStats);

        // 计算平均响应时间
        stats.put("avgResponseTime", calculateAverageResponseTime());

        // 计算响应率
        stats.put("responseRate", calculateResponseRate());

        // 获取活跃用户排行
        stats.put("activeUserRanking", getActiveUserRanking());

        return stats;
    }

    private Double calculateAverageResponseTime() {
        QueryWrapper<Interaction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "replied");
        List<Interaction> repliedInteractions = interactionMapper.selectList(queryWrapper);

        if (repliedInteractions.isEmpty()) {
            return 0.0;
        }

        double totalMinutes = 0;
        int count = 0;

        for (Interaction interaction : repliedInteractions) {
            QueryWrapper<InteractionReply> replyQuery = new QueryWrapper<>();
            replyQuery.eq("interaction_id", interaction.getId())
                    .orderByAsc("created_at")
                    .last("LIMIT 1");

            InteractionReply firstReply = replyMapper.selectOne(replyQuery);
            if (firstReply != null) {
                // 添加 ZoneOffset.systemDefault() 来将 LocalDateTime 转换为 Instant
                long diffInMinutes = ChronoUnit.MINUTES.between(
                        interaction.getCreatedAt().atZone(ZoneOffset.systemDefault()).toInstant(),
                        firstReply.getCreatedAt().atZone(ZoneOffset.systemDefault()).toInstant()
                );
                totalMinutes += diffInMinutes;
                count++;
            }
        }

        return count > 0 ? totalMinutes / count : 0.0;
    }

    private Double calculateResponseRate() {
        long totalCount = interactionMapper.selectCount(null);
        if (totalCount == 0) {
            return 0.0;
        }

        QueryWrapper<Interaction> respondedQuery = new QueryWrapper<>();
        respondedQuery.eq("status", "replied");
        long respondedCount = interactionMapper.selectCount(respondedQuery);

        return (respondedCount * 100.0) / totalCount;
    }

    private List<Map<String, Object>> getActiveUserRanking() {
        QueryWrapper<User> userQuery = new QueryWrapper<>();
        userQuery.ge("last_login", getLast30Days())
                .orderByDesc("last_login")
                .last("LIMIT 10");

        return userMapper.selectList(userQuery).stream()
                .map(user -> {
                    Map<String, Object> ranking = new HashMap<>();
                    ranking.put("userId", user.getUserId());
                    ranking.put("userName", user.getUsername());
                    ranking.put("actionCount", getUserActionCount(user.getUserId()));
                    return ranking;
                })
                .collect(Collectors.toList());
    }

    private long getUserActionCount(Integer userId) {
        QueryWrapper<Interaction> interactionQuery = new QueryWrapper<>();
        QueryWrapper<InteractionReply> replyQuery = new QueryWrapper<>();
        QueryWrapper<UserUniversityFollow> followQuery = new QueryWrapper<>();

        interactionQuery.eq("user_id", userId);
        replyQuery.eq("user_id", userId);
        followQuery.eq("user_id", userId);

        return interactionMapper.selectCount(interactionQuery) +
                replyMapper.selectCount(replyQuery) +
                followMapper.selectCount(followQuery);
    }

    private String getLast30Days() {
        return LocalDate.now().minusDays(30)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private String getLast7Days() {
        return LocalDate.now().minusDays(7)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
