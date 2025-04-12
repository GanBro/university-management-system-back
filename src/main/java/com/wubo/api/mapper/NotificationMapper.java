// NotificationMapper.java
package com.wubo.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wubo.api.entity.Notification;

// 不再需要自定义方法，直接使用MyBatis-Plus提供的BaseMapper接口
public interface NotificationMapper extends BaseMapper<Notification> {
}
