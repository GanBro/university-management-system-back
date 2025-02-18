// UniversityFeatureMapper.java
package com.wubo.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wubo.api.entity.UniversityFeature;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UniversityFeatureMapper extends BaseMapper<UniversityFeature> {
    /**
     * 查询所有可用的特性标签
     */
    @Select("SELECT DISTINCT feature_name FROM university_feature ORDER BY feature_name ASC")
    List<String> selectAllFeatures();
}
