package com.wubo.universitymanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.wubo.universitymanagementsystem.entity.University;

@Mapper
public interface UniversityMapper extends BaseMapper<University> {
    // 继承MyBatis-Plus的BaseMapper进行CRUD操作
}
