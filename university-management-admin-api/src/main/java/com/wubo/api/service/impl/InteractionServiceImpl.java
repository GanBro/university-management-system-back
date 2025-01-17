package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wubo.api.entity.Interaction;
import com.wubo.api.entity.InteractionReply;
import com.wubo.api.mapper.InteractionMapper;
import com.wubo.api.mapper.InteractionReplyMapper;
import com.wubo.api.mapper.UniversityMapper;
import com.wubo.api.mapper.UserMapper;
import com.wubo.api.service.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class InteractionServiceImpl extends ServiceImpl<InteractionMapper, Interaction> implements InteractionService {

    @Autowired
    private InteractionMapper interactionMapper;

    @Autowired
    private InteractionReplyMapper replyMapper;

    @Autowired
    private UniversityMapper universityMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Page<Interaction> getInteractionList(Integer page, Integer limit, Map<String, Object> params) {
        // 创建分页对象
        Page<Interaction> pageParam = new Page<>(page, limit);

        // 使用 LambdaQueryWrapper 构建查询条件
        LambdaQueryWrapper<Interaction> queryWrapper = new LambdaQueryWrapper<>();

        // 处理各种查询条件
        Object universityIdObj = params.get("universityId");
        if (universityIdObj != null) {
            queryWrapper.eq(Interaction::getUniversityId, universityIdObj);
        }

        String status = (String) params.get("status");
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(Interaction::getStatus, status);
        }

        Object userIdObj = params.get("userId");
        if (userIdObj != null) {
            queryWrapper.eq(Interaction::getUserId, userIdObj);
        }

        Object isPublicObj = params.get("isPublic");
        if (isPublicObj != null) {
            queryWrapper.eq(Interaction::getIsPublic, isPublicObj);
        }

        String keyword = (String) params.get("keyword");
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Interaction::getTitle, keyword)
                    .or()
                    .like(Interaction::getContent, keyword));
        }

        // 添加排序
        queryWrapper.orderByDesc(Interaction::getCreatedAt);

        // 执行分页查询
        Page<Interaction> resultPage = interactionMapper.selectPage(pageParam, queryWrapper);

        // 填充关联信息
        resultPage.getRecords().forEach(this::fillAssociatedInfo);

        return resultPage;
    }

    @Override
    public Interaction getInteractionDetail(Integer id) {
        Interaction interaction = interactionMapper.selectById(id);
        if (interaction != null) {
            // 增加浏览次数
            interaction.setViewCount(interaction.getViewCount() + 1);
            interactionMapper.updateById(interaction);

            // 填充关联信息
            fillAssociatedInfo(interaction);
        }
        return interaction;
    }

    private void fillAssociatedInfo(Interaction interaction) {
        // 填充大学名称
        if (interaction.getUniversityId() != null) {
            interaction.setUniversityName(
                    universityMapper.selectById(interaction.getUniversityId()).getName()
            );
        }

        // 填充用户名
        if (interaction.getUserId() != null) {
            interaction.setUserName(
                    userMapper.selectById(interaction.getUserId()).getUsername()
            );
        }

        // 查询回复列表
        LambdaQueryWrapper<InteractionReply> replyWrapper = new LambdaQueryWrapper<>();
        replyWrapper.eq(InteractionReply::getInteractionId, interaction.getId())
                .orderByAsc(InteractionReply::getCreatedAt);
        List<InteractionReply> replies = replyMapper.selectList(replyWrapper);

        // 填充回复中的用户名
        replies.forEach(reply -> {
            if (reply.getUserId() != null) {
                reply.setUserName(
                        userMapper.selectById(reply.getUserId()).getUsername()
                );
            }
        });

        interaction.setReplies(replies);
    }

    @Override
    @Transactional
    public void createInteraction(Interaction interaction) {
        interaction.setStatus("pending");
        interaction.setViewCount(0);
        interaction.setCreatedAt(LocalDateTime.now());  // 设置创建时间
        interaction.setUpdatedAt(LocalDateTime.now());  // 设置更新时间
        interactionMapper.insert(interaction);
    }

    @Override
    @Transactional
    public void replyInteraction(InteractionReply reply) {
        reply.setCreatedAt(LocalDateTime.now());
        replyMapper.insert(reply);
        // 更新互动状态为已回复
        Interaction interaction = new Interaction();
        interaction.setId(reply.getInteractionId());
        interaction.setStatus("replied");
        interaction.setUpdatedAt(LocalDateTime.now());
        interactionMapper.updateById(interaction);
    }

    @Override
    @Transactional
    public void closeInteraction(Integer id) {
        Interaction interaction = new Interaction();
        interaction.setId(id);
        interaction.setStatus("closed");
        interaction.setUpdatedAt(LocalDateTime.now());
        interactionMapper.updateById(interaction);
    }

    @Override
    public void reopenInteraction(Integer id) {
        Interaction interaction = this.getById(id);
        if (interaction == null) {
            throw new RuntimeException("互动不存在");
        }
        // 检查当前状态
        if (!"closed".equals(interaction.getStatus())) {
            throw new RuntimeException("只有已关闭的互动才能重新开启");
        }
        // 将状态更新为待处理
        interaction.setStatus("pending");
        interaction.setUpdatedAt(LocalDateTime.now());
        this.updateById(interaction);
    }

    @Override
    @Transactional
    public void deleteInteraction(Integer id) {
        interactionMapper.deleteById(id);
    }

    @Override
    public Map<String, Object> getInteractionStats(Integer universityId) {
        LambdaQueryWrapper<Interaction> queryWrapper = new LambdaQueryWrapper<>();
        if (universityId != null) {
            queryWrapper.eq(Interaction::getUniversityId, universityId);
        }

        // 获取总数
        long total = count(queryWrapper);

        // 获取待处理数量
        long pending = count(queryWrapper.clone().eq(Interaction::getStatus, "pending"));

        // 获取已回复数量
        long replied = count(queryWrapper.clone().eq(Interaction::getStatus, "replied"));

        // 计算平均响应时间（小时）
        double avgResponseTime = calculateAverageResponseTime(universityId);

        // 计算回复率
        double responseRate = total > 0 ? (replied * 100.0 / total) : 0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("pending", pending);
        stats.put("avgResponseTime", Math.round(avgResponseTime));
        stats.put("responseRate", Math.round(responseRate));

        return stats;
    }

    private double calculateAverageResponseTime(Integer universityId) {
        // 查询所有有回复时间的互动
        LambdaQueryWrapper<Interaction> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Interaction::getStatus, "replied");
        if (universityId != null) {
            queryWrapper.eq(Interaction::getUniversityId, universityId);
        }

        List<Interaction> interactions = list(queryWrapper);
        if (interactions.isEmpty()) {
            return 0;
        }

        long totalHours = 0;
        int count = 0;

        for (Interaction interaction : interactions) {
            if (interaction.getCreatedAt() != null && interaction.getUpdatedAt() != null) {
                long diffInHours = ChronoUnit.HOURS.between(
                        interaction.getCreatedAt(),
                        interaction.getUpdatedAt()
                );
                totalHours += diffInHours;
                count++;
            }
        }

        return count > 0 ? (double) totalHours / count : 0;
    }
}
