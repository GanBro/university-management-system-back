package com.wubo.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wubo.api.entity.University;
import com.wubo.api.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
