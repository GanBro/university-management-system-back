package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.dto.ConsultationDTO;
import com.wubo.api.entity.Interaction;
import com.wubo.api.entity.InteractionReply;
import com.wubo.api.entity.User;
import com.wubo.api.entity.University;
import com.wubo.api.mapper.*;
import com.wubo.api.service.ConsultationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultationServiceImpl implements ConsultationService {

    private final InteractionMapper interactionMapper;
    private final InteractionReplyMapper replyMapper;
    private final UniversityMapper universityMapper;
    private final UserMapper userMapper;

    @Override
    public Page<Map<String, Object>> getConsultationList(Integer page, Integer limit, Map<String, Object> params) {
        Page<Interaction> pageParam = new Page<>(page, limit);
        LambdaQueryWrapper<Interaction> wrapper = new LambdaQueryWrapper<>();

        // 设置查询条件
        wrapper.eq(Interaction::getType, "consult"); // 只查询咨询类型

        // 筛选条件
        if (params != null) {
            // 大学ID
            Object universityId = params.get("universityId");
            if (universityId != null) {
                wrapper.eq(Interaction::getUniversityId, universityId);
            }

            // 状态
            String status = (String) params.get("status");
            if (StringUtils.hasText(status)) {
                wrapper.eq(Interaction::getStatus, status);
            }

            // 是否公开
            Object isPublic = params.get("isPublic");
            if (isPublic != null) {
                wrapper.eq(Interaction::getIsPublic, isPublic);
            }

            // 关键词搜索
            String keyword = (String) params.get("keyword");
            if (StringUtils.hasText(keyword)) {
                wrapper.and(w -> w.like(Interaction::getTitle, keyword)
                        .or()
                        .like(Interaction::getContent, keyword));
            }
        }

        // 排序
        wrapper.orderByDesc(Interaction::getCreatedAt);

        // 执行查询
        Page<Interaction> result = interactionMapper.selectPage(pageParam, wrapper);

        // 转换结果
        Page<Map<String, Object>> mapPage = new Page<>(page, limit, result.getTotal());
        List<Map<String, Object>> records = new ArrayList<>();

        for (Interaction interaction : result.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", interaction.getId());
            map.put("title", interaction.getTitle());
            map.put("content", interaction.getContent());
            map.put("status", interaction.getStatus());
            map.put("viewCount", interaction.getViewCount());
            map.put("createTime", interaction.getCreatedAt());

            // 获取大学信息
            University university = universityMapper.selectById(interaction.getUniversityId());
            if (university != null) {
                map.put("universityName", university.getName());
            }

            // 获取用户信息
            User user = userMapper.selectById(interaction.getUserId());
            if (user != null) {
                map.put("userName", user.getUsername());
            }

            // 获取回复数量
            LambdaQueryWrapper<InteractionReply> replyWrapper = new LambdaQueryWrapper<>();
            replyWrapper.eq(InteractionReply::getInteractionId, interaction.getId());
            long replyCount = replyMapper.selectCount(replyWrapper);
            map.put("replyCount", replyCount);

            records.add(map);
        }

        mapPage.setRecords(records);
        return mapPage;
    }

    @Override
    public Map<String, Object> getConsultationDetail(Integer id) {
        // 获取咨询信息
        Interaction interaction = interactionMapper.selectById(id);
        if (interaction == null) {
            return null;
        }

        // 增加浏览次数
        interaction.setViewCount(interaction.getViewCount() + 1);
        interactionMapper.updateById(interaction);

        // 构建返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("id", interaction.getId());
        result.put("title", interaction.getTitle());
        result.put("content", interaction.getContent());
        result.put("status", interaction.getStatus());
        result.put("viewCount", interaction.getViewCount());
        result.put("createTime", interaction.getCreatedAt());

        // 获取大学信息
        University university = universityMapper.selectById(interaction.getUniversityId());
        if (university != null) {
            result.put("universityName", university.getName());
            result.put("universityId", university.getId());
        }

        // 获取发起用户信息
        User user = userMapper.selectById(interaction.getUserId());
        if (user != null) {
            result.put("userName", user.getUsername());
            result.put("userAvatar", user.getAvatar());
        }

        // 获取回复列表
        LambdaQueryWrapper<InteractionReply> replyWrapper = new LambdaQueryWrapper<>();
        replyWrapper.eq(InteractionReply::getInteractionId, id)
                .orderByAsc(InteractionReply::getCreatedAt);
        List<InteractionReply> replies = replyMapper.selectList(replyWrapper);

        // 处理回复信息
        List<Map<String, Object>> replyList = new ArrayList<>();
        for (InteractionReply reply : replies) {
            Map<String, Object> replyMap = new HashMap<>();
            replyMap.put("id", reply.getId());
            replyMap.put("content", reply.getContent());
            replyMap.put("isOfficial", reply.getIsOfficial());
            replyMap.put("createTime", reply.getCreatedAt());

            // 获取回复用户信息
            User replyUser = userMapper.selectById(reply.getUserId());
            if (replyUser != null) {
                replyMap.put("userName", replyUser.getUsername());
                replyMap.put("userAvatar", replyUser.getAvatar());
            }

            replyList.add(replyMap);
        }
        result.put("replies", replyList);

        return result;
    }

    @Override
    @Transactional
    public void submitConsultation(ConsultationDTO consultationDTO) {
        Interaction interaction = new Interaction();
        interaction.setUniversityId(consultationDTO.getUniversityId());
        interaction.setUserId(consultationDTO.getUserId());
        interaction.setType("consult");
        interaction.setTitle(consultationDTO.getTitle());
        interaction.setContent(consultationDTO.getContent());
        interaction.setStatus("pending");
        interaction.setIsPublic(consultationDTO.getIsPublic());
        interaction.setViewCount(0);
        interaction.setCreatedAt(LocalDateTime.now());
        interaction.setUpdatedAt(LocalDateTime.now());

        interactionMapper.insert(interaction);
    }

    @Override
    @Transactional
    public void replyConsultation(Integer id, InteractionReply reply) {
        // 保存回复
        reply.setInteractionId(id);
        reply.setCreatedAt(LocalDateTime.now());
        replyMapper.insert(reply);

        // 更新咨询状态
        Interaction interaction = new Interaction();
        interaction.setId(id);
        interaction.setStatus("replied");
        interaction.setUpdatedAt(LocalDateTime.now());
        interactionMapper.updateById(interaction);
    }

    @Override
    public List<Map<String, Object>> getRelatedConsultations(Integer universityId, Integer currentId) {
        LambdaQueryWrapper<Interaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Interaction::getUniversityId, universityId)
                .eq(Interaction::getType, "consult")
                .eq(Interaction::getIsPublic, true)
                .ne(Interaction::getId, currentId)
                .orderByDesc(Interaction::getCreatedAt)
                .last("LIMIT 5");

        List<Interaction> interactions = interactionMapper.selectList(wrapper);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Interaction interaction : interactions) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", interaction.getId());
            map.put("title", interaction.getTitle());
            map.put("createTime", interaction.getCreatedAt());
            result.add(map);
        }

        return result;
    }

    @Override
    public Map<String, Object> getConsultationStats(Integer universityId) {
        LambdaQueryWrapper<Interaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Interaction::getUniversityId, universityId)
                .eq(Interaction::getType, "consult");

        Map<String, Object> stats = new HashMap<>();

        // 总咨询数
        long total = interactionMapper.selectCount(wrapper);
        stats.put("total", total);

        // 待回复数量
        wrapper.clear();
        wrapper.eq(Interaction::getUniversityId, universityId)
                .eq(Interaction::getType, "consult")
                .eq(Interaction::getStatus, "pending");
        long pending = interactionMapper.selectCount(wrapper);
        stats.put("pending", pending);

        // 已回复数量
        stats.put("replied", total - pending);

        // 回复率
        stats.put("replyRate", total > 0 ? ((double)(total - pending) / total * 100) : 0);

        return stats;
    }

    @Override
    @Transactional
    public void updateConsultationStatus(Integer id, String status) {
        Interaction interaction = new Interaction();
        interaction.setId(id);
        interaction.setStatus(status);
        interaction.setUpdatedAt(LocalDateTime.now());
        interactionMapper.updateById(interaction);
    }

    @Override
    @Transactional
    public void deleteConsultation(Integer id) {
        // 删除相关回复
        LambdaQueryWrapper<InteractionReply> replyWrapper = new LambdaQueryWrapper<>();
        replyWrapper.eq(InteractionReply::getInteractionId, id);
        replyMapper.delete(replyWrapper);

        // 删除咨询
        interactionMapper.deleteById(id);
    }
}
