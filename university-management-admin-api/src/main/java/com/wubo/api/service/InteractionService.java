// InteractionService.java
package com.wubo.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.entity.Interaction;
import com.wubo.api.entity.InteractionReply;
import java.util.Map;

public interface InteractionService {
    Page<Interaction> getInteractionList(Integer page, Integer limit, Map<String, Object> params);
    Interaction getInteractionDetail(Integer id);
    void createInteraction(Interaction interaction);
    void replyInteraction(InteractionReply reply);
    void closeInteraction(Integer id);
    void reopenInteraction(Integer id);
    void deleteInteraction(Integer id);
}
