// MessageLogService.java
package com.wubo.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.entity.MessageLog;

public interface MessageLogService {
    Page<MessageLog> getMessageLogList(Integer page, Integer limit, Integer userId, String status);
    void sendMessage(MessageLog messageLog);
}
