package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wubo.api.entity.*;
import com.wubo.api.mapper.*;
import com.wubo.api.service.DashboardService;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
    public Map<String, Object> getStats(String startDate, String endDate) {
        Map<String, Object> stats = new HashMap<>();
        stats.putAll(getUniversityStats(startDate, endDate));
        stats.putAll(getUserStats(startDate, endDate));
        stats.putAll(getInteractionStats(startDate, endDate));
        return stats;
    }

    @Override
    public List<Map<String, Object>> getUniversityGrowthTrend(String startDate, String endDate) {
        // 如果没有提供日期范围，使用默认范围（最近一年）
        if (!StringUtils.hasText(startDate) || !StringUtils.hasText(endDate)) {
            YearMonth now = YearMonth.now();
            endDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            startDate = now.minusMonths(11).format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }

        // 解析日期范围
        YearMonth start = YearMonth.parse(startDate);
        YearMonth end = YearMonth.parse(endDate);

        // 构建查询
        QueryWrapper<University> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(
            "DATE_FORMAT(created_at, '%Y-%m') as date",
            "COUNT(*) as value"
        );
        
        // 添加日期范围条件
        queryWrapper.ge("DATE_FORMAT(created_at, '%Y-%m')", startDate)
                   .le("DATE_FORMAT(created_at, '%Y-%m')", endDate)
                   .groupBy("DATE_FORMAT(created_at, '%Y-%m')")
                   .orderByAsc("DATE_FORMAT(created_at, '%Y-%m')");
                
        List<Map<String, Object>> result = universityMapper.selectMaps(queryWrapper);
        
        // 创建数据映射，用于快速查找已有数据
        Map<String, Long> dataMap = result.stream()
            .collect(Collectors.toMap(
                m -> (String) m.get("date"),
                m -> ((Number) m.get("value")).longValue(),
                (v1, v2) -> v2
            ));
        
        // 填充所有月份的数据
        List<Map<String, Object>> filledResult = new ArrayList<>();
        YearMonth current = start;
        
        // 计算累计值
        long cumulativeValue = 0;
        
        while (!current.isAfter(end)) {
            String monthStr = current.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("date", monthStr);
            
            // 获取当月新增数量
            long monthlyValue = dataMap.getOrDefault(monthStr, 0L);
            cumulativeValue += monthlyValue;
            
            // 存储累计值
            monthData.put("value", cumulativeValue);
            filledResult.add(monthData);
            
            current = current.plusMonths(1);
        }
        
        return filledResult;
    }

    private Map<String, Object> getUniversityStats(String startDate, String endDate) {
        Map<String, Object> stats = new HashMap<>();

        // 获取总数
        QueryWrapper<University> totalQuery = new QueryWrapper<>();
        if (StringUtils.hasText(startDate)) {
            totalQuery.ge("DATE_FORMAT(created_at, '%Y-%m')", startDate);
        }
        if (StringUtils.hasText(endDate)) {
            totalQuery.le("DATE_FORMAT(created_at, '%Y-%m')", endDate);
        }
        stats.put("totalUniversityCount", universityMapper.selectCount(totalQuery));

        // 获取最近新增数
        QueryWrapper<University> recentQuery = new QueryWrapper<>();
        if (StringUtils.hasText(endDate)) {
            recentQuery.le("DATE_FORMAT(created_at, '%Y-%m')", endDate);
        }
        recentQuery.ge("created_at", getLast30Days());
        stats.put("recentUniversityCount", universityMapper.selectCount(recentQuery));

        // 获取地理分布
        stats.put("universityDistribution", universityMapper.selectUniversityDistribution());

        return stats;
    }

    private Map<String, Object> getUserStats(String startDate, String endDate) {
        Map<String, Object> stats = new HashMap<>();

        QueryWrapper<User> weeklyQuery = new QueryWrapper<>();
        weeklyQuery.ge("last_login", getLast7Days());
        if (StringUtils.hasText(startDate)) {
            weeklyQuery.ge("DATE_FORMAT(created_at, '%Y-%m')", startDate);
        }
        if (StringUtils.hasText(endDate)) {
            weeklyQuery.le("DATE_FORMAT(created_at, '%Y-%m')", endDate);
        }
        stats.put("activeUserCount", userMapper.selectCount(weeklyQuery));

        QueryWrapper<User> monthlyQuery = new QueryWrapper<>();
        monthlyQuery.ge("last_login", getLast30Days());
        if (StringUtils.hasText(startDate)) {
            monthlyQuery.ge("DATE_FORMAT(created_at, '%Y-%m')", startDate);
        }
        if (StringUtils.hasText(endDate)) {
            monthlyQuery.le("DATE_FORMAT(created_at, '%Y-%m')", endDate);
        }
        stats.put("monthlyActiveUserCount", userMapper.selectCount(monthlyQuery));

        return stats;
    }

    private Map<String, Object> getInteractionStats(String startDate, String endDate) {
        Map<String, Object> stats = new HashMap<>();

        // 计算互动总数
        QueryWrapper<Interaction> totalQuery = new QueryWrapper<>();
        if (StringUtils.hasText(startDate)) {
            totalQuery.ge("DATE_FORMAT(created_at, '%Y-%m')", startDate);
        }
        if (StringUtils.hasText(endDate)) {
            totalQuery.le("DATE_FORMAT(created_at, '%Y-%m')", endDate);
        }
        long totalInteractions = interactionMapper.selectCount(totalQuery);
        stats.put("totalInteractionCount", totalInteractions);

        // 统计待处理的咨询数量
        QueryWrapper<Interaction> pendingQuery = new QueryWrapper<>();
        pendingQuery.eq("status", "pending");
        if (StringUtils.hasText(startDate)) {
            pendingQuery.ge("DATE_FORMAT(created_at, '%Y-%m')", startDate);
        }
        if (StringUtils.hasText(endDate)) {
            pendingQuery.le("DATE_FORMAT(created_at, '%Y-%m')", endDate);
        }
        long pendingCount = interactionMapper.selectCount(pendingQuery);
        stats.put("pendingInteractionCount", pendingCount);

        // 计算用户行为统计
        Map<String, Object> behaviorStats = new HashMap<>();
        behaviorStats.put("consultationCount", totalInteractions);
        
        // 获取回复数量
        QueryWrapper<InteractionReply> replyQuery = new QueryWrapper<>();
        if (StringUtils.hasText(startDate)) {
            replyQuery.ge("DATE_FORMAT(created_at, '%Y-%m')", startDate);
        }
        if (StringUtils.hasText(endDate)) {
            replyQuery.le("DATE_FORMAT(created_at, '%Y-%m')", endDate);
        }
        long replyCount = replyMapper.selectCount(replyQuery);
        behaviorStats.put("replyCount", replyCount);
        
        // 获取关注数量
        QueryWrapper<UserUniversityFollow> followQuery = new QueryWrapper<>();
        if (StringUtils.hasText(startDate)) {
            followQuery.ge("DATE_FORMAT(created_at, '%Y-%m')", startDate);
        }
        if (StringUtils.hasText(endDate)) {
            followQuery.le("DATE_FORMAT(created_at, '%Y-%m')", endDate);
        }
        long followCount = followMapper.selectCount(followQuery);
        behaviorStats.put("followCount", followCount);
        
        stats.put("userBehaviorStats", behaviorStats);

        // 计算平均响应时间和响应率
        stats.put("avgResponseTime", calculateAverageResponseTime(startDate, endDate));
        stats.put("responseRate", calculateResponseRate(startDate, endDate));

        // 获取活跃用户排行
        stats.put("activeUserRanking", getActiveUserRanking(startDate, endDate));

        return stats;
    }

    private Double calculateAverageResponseTime(String startDate, String endDate) {
        QueryWrapper<Interaction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "replied");
        if (StringUtils.hasText(startDate)) {
            queryWrapper.ge("DATE_FORMAT(created_at, '%Y-%m')", startDate);
        }
        if (StringUtils.hasText(endDate)) {
            queryWrapper.le("DATE_FORMAT(created_at, '%Y-%m')", endDate);
        }
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

    private Double calculateResponseRate(String startDate, String endDate) {
        QueryWrapper<Interaction> totalQuery = new QueryWrapper<>();
        if (StringUtils.hasText(startDate)) {
            totalQuery.ge("DATE_FORMAT(created_at, '%Y-%m')", startDate);
        }
        if (StringUtils.hasText(endDate)) {
            totalQuery.le("DATE_FORMAT(created_at, '%Y-%m')", endDate);
        }
        long totalCount = interactionMapper.selectCount(totalQuery);
        
        if (totalCount == 0) {
            return 0.0;
        }

        QueryWrapper<Interaction> respondedQuery = new QueryWrapper<>();
        respondedQuery.eq("status", "replied");
        if (StringUtils.hasText(startDate)) {
            respondedQuery.ge("DATE_FORMAT(created_at, '%Y-%m')", startDate);
        }
        if (StringUtils.hasText(endDate)) {
            respondedQuery.le("DATE_FORMAT(created_at, '%Y-%m')", endDate);
        }
        long respondedCount = interactionMapper.selectCount(respondedQuery);

        return (respondedCount * 100.0) / totalCount;
    }

    private List<Map<String, Object>> getActiveUserRanking(String startDate, String endDate) {
        QueryWrapper<User> userQuery = new QueryWrapper<>();
        userQuery.ge("last_login", getLast30Days());
        if (StringUtils.hasText(startDate)) {
            userQuery.ge("DATE_FORMAT(created_at, '%Y-%m')", startDate);
        }
        if (StringUtils.hasText(endDate)) {
            userQuery.le("DATE_FORMAT(created_at, '%Y-%m')", endDate);
        }
        userQuery.orderByDesc("last_login")
                .last("LIMIT 10");

        return userMapper.selectList(userQuery).stream()
                .map(user -> {
                    Map<String, Object> ranking = new HashMap<>();
                    ranking.put("userId", user.getUserId());
                    ranking.put("userName", user.getUsername());
                    ranking.put("actionCount", getUserActionCount(user.getUserId(), startDate, endDate));
                    return ranking;
                })
                .collect(Collectors.toList());
    }

    private long getUserActionCount(Integer userId, String startDate, String endDate) {
        QueryWrapper<Interaction> interactionQuery = new QueryWrapper<>();
        QueryWrapper<InteractionReply> replyQuery = new QueryWrapper<>();
        QueryWrapper<UserUniversityFollow> followQuery = new QueryWrapper<>();

        interactionQuery.eq("user_id", userId);
        replyQuery.eq("user_id", userId);
        followQuery.eq("user_id", userId);

        if (StringUtils.hasText(startDate)) {
            interactionQuery.ge("DATE_FORMAT(created_at, '%Y-%m')", startDate);
            replyQuery.ge("DATE_FORMAT(created_at, '%Y-%m')", startDate);
            followQuery.ge("DATE_FORMAT(created_at, '%Y-%m')", startDate);
        }
        if (StringUtils.hasText(endDate)) {
            interactionQuery.le("DATE_FORMAT(created_at, '%Y-%m')", endDate);
            replyQuery.le("DATE_FORMAT(created_at, '%Y-%m')", endDate);
            followQuery.le("DATE_FORMAT(created_at, '%Y-%m')", endDate);
        }

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
