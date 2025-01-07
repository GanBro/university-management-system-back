// InteractionServiceImpl.java
package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class InteractionServiceImpl implements InteractionService {

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

        // 构建查询条件
        LambdaQueryWrapper<Interaction> queryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件（处理参数类型转换）
        Object universityIdObj = params.get("universityId");
        Integer universityId = null;
        if (universityIdObj != null) {
            if (universityIdObj instanceof Integer) {
                universityId = (Integer) universityIdObj;
            } else if (universityIdObj instanceof String) {
                try {
                    universityId = Integer.parseInt((String) universityIdObj);
                } catch (NumberFormatException e) {
                    // 转换失败时忽略该条件
                }
            }
        }

        String type = params.get("type") != null ? params.get("type").toString() : null;
        String status = params.get("status") != null ? params.get("status").toString() : null;
        String keyword = params.get("keyword") != null ? params.get("keyword").toString() : null;

        Object userIdObj = params.get("userId");
        Integer userId = null;
        if (userIdObj != null) {
            if (userIdObj instanceof Integer) {
                userId = (Integer) userIdObj;
            } else if (userIdObj instanceof String) {
                try {
                    userId = Integer.parseInt((String) userIdObj);
                } catch (NumberFormatException e) {
                    // 转换失败时忽略该条件
                }
            }
        }

        Object isPublicObj = params.get("isPublic");
        Boolean isPublic = null;
        if (isPublicObj != null) {
            if (isPublicObj instanceof Boolean) {
                isPublic = (Boolean) isPublicObj;
            } else if (isPublicObj instanceof String) {
                isPublic = Boolean.parseBoolean((String) isPublicObj);
            }
        }

        // 添加查询条件
        if (universityId != null) {
            queryWrapper.eq(Interaction::getUniversityId, universityId);
        }
        if (StringUtils.hasText(type)) {
            queryWrapper.eq(Interaction::getType, type);
        }
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(Interaction::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Interaction::getTitle, keyword)
                    .or()
                    .like(Interaction::getContent, keyword));
        }
        if (userId != null) {
            queryWrapper.eq(Interaction::getUserId, userId);
        }
        if (isPublic != null) {
            queryWrapper.eq(Interaction::getIsPublic, isPublic);
        }

        // 添加排序
        queryWrapper.orderByDesc(Interaction::getId);

        // 执行查询
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
        interactionMapper.insert(interaction);
    }

    @Override
    @Transactional
    public void replyInteraction(InteractionReply reply) {
        replyMapper.insert(reply);
        // 更新互动状态为已回复
        Interaction interaction = new Interaction();
        interaction.setId(reply.getInteractionId());
        interaction.setStatus("replied");
        interactionMapper.updateById(interaction);
    }

    @Override
    @Transactional
    public void closeInteraction(Integer id) {
        Interaction interaction = new Interaction();
        interaction.setId(id);
        interaction.setStatus("closed");
        interactionMapper.updateById(interaction);
    }

    @Override
    @Transactional
    public void deleteInteraction(Integer id) {
        // 删除互动时会自动删除相关回复（通过外键级联删除）
        interactionMapper.deleteById(id);
    }
}
