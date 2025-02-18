package com.wubo.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.dto.ConsultationDTO;
import com.wubo.api.entity.InteractionReply;
import java.util.List;
import java.util.Map;

public interface ConsultationService {

    /**
     * 获取咨询列表
     */
    Page<Map<String, Object>> getConsultationList(Integer page, Integer limit, Map<String, Object> params);

    /**
     * 获取咨询详情
     */
    Map<String, Object> getConsultationDetail(Integer id);

    /**
     * 提交咨询
     */
    void submitConsultation(ConsultationDTO consultationDTO);

    /**
     * 回复咨询
     */
    void replyConsultation(Integer id, InteractionReply reply);

    /**
     * 获取相关咨询
     */
    List<Map<String, Object>> getRelatedConsultations(Integer universityId, Integer currentId);

    /**
     * 获取咨询统计数据
     */
    Map<String, Object> getConsultationStats(Integer universityId);

    /**
     * 更新咨询状态
     */
    void updateConsultationStatus(Integer id, String status);

    /**
     * 删除咨询
     */
    void deleteConsultation(Integer id);
}
