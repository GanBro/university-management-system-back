// InteractionMapper.java
package com.wubo.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.entity.Interaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface InteractionMapper extends BaseMapper<Interaction> {
    Page<Interaction> selectInteractionPage(Page<Interaction> page, @Param("params") Map<String, Object> params);
}
