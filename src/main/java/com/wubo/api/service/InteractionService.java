// InteractionService.java
package com.wubo.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wubo.api.dto.InteractionDetailDTO;
import com.wubo.api.entity.Interaction;
import com.wubo.api.entity.InteractionReply;
import java.util.Map;

public interface InteractionService extends IService<Interaction> {
    Page<Interaction> getInteractionList(Integer page, Integer limit, Map<String, Object> params);
    InteractionDetailDTO getInteractionDetail(Integer id); // Changed return type to InteractionDetailDTO
    void createInteraction(Interaction interaction);
    void replyInteraction(InteractionReply reply);
    void closeInteraction(Integer id);
    void reopenInteraction(Integer id);
    void deleteInteraction(Integer id);
    Map<String, Object> getInteractionStats(Integer universityId);
    void deleteReply(Integer replyId);
}
