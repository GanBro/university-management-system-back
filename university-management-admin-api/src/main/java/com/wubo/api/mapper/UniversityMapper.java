package com.wubo.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wubo.api.entity.University;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UniversityMapper extends BaseMapper<University> {

//    @Select("SELECT province, COUNT(*) AS count FROM university GROUP BY province")
    List<Map<String, Object>> selectUniversityDistribution();

    List<Map<String, Object>> selectUniversityGrowthTrend();


}
