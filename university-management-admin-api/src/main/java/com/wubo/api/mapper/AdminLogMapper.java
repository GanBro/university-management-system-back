package com.wubo.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wubo.api.entity.AdminLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminLogMapper extends BaseMapper<AdminLog> {
    // 使用 MyBatis-Plus 提供的方法即可
}
