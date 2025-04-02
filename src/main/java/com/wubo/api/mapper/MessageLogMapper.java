package com.wubo.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wubo.api.entity.MessageLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageLogMapper extends BaseMapper<MessageLog> {
}
