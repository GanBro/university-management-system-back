package com.wubo.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.dto.UniversitySatisfactionDTO;
import com.wubo.api.entity.UniversitySatisfaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * 院校满意度Mapper接口
 */
@Mapper
public interface UniversitySatisfactionMapper extends BaseMapper<UniversitySatisfaction> {

    /**
     * 根据大学ID查询满意度
     *
     * @param universityId 大学ID
     * @return 满意度实体
     */
    @Select("SELECT * FROM university_satisfaction WHERE university_id = #{universityId}")
    UniversitySatisfaction selectByUniversityId(@Param("universityId") Integer universityId);

    /**
     * 获取院校满意度分页列表
     *
     * @param page 分页参数
     * @param params 查询参数
     * @return 分页结果
     */
    IPage<UniversitySatisfactionDTO> selectSatisfactionPage(Page<UniversitySatisfactionDTO> page, @Param("params") Map<String, Object> params);
}
