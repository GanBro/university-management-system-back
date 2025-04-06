package com.wubo.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wubo.api.entity.UniversityAdminMapping;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface UniversityAdminMappingMapper extends BaseMapper<UniversityAdminMapping> {

    @Select("SELECT m.*, u.name as universityName FROM university_admin_mapping m " +
            "LEFT JOIN university u ON m.university_id = u.id " +
            "WHERE m.user_id = #{userId}")
    UniversityAdminMapping selectWithUniversityName(@Param("userId") Integer userId);
}
