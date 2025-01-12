// MessageLogServiceImpl.java
package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wubo.api.entity.MessageLog;
import com.wubo.api.mapper.MessageLogMapper;
import com.wubo.api.service.MessageLogService;
import org.springframework.stereotype.Service;

@Service
public class MessageLogServiceImpl extends ServiceImpl<MessageLogMapper, MessageLog> implements MessageLogService {

    @Override
    public Page<MessageLog> getMessageLogList(Integer page, Integer limit, Integer userId, String status) {
        Page<MessageLog> pageParam = new Page<>(page, limit);
        return this.page(pageParam);
    }

    @Override
    public void sendMessage(MessageLog messageLog) {
        this.save(messageLog);
    }
}
