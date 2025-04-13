package com.wubo.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wubo.api.dto.UniversitySatisfactionDTO;
import com.wubo.api.entity.UniversitySatisfaction;

import java.util.Map;

/**
 * 院校满意度服务接口
 */
public interface UniversitySatisfactionService extends IService<UniversitySatisfaction> {

    /**
     * 分页查询院校满意度列表
     *
     * @param page 分页参数
     * @param params 查询条件
     * @return 分页结果
     */
    IPage<UniversitySatisfactionDTO> getSatisfactionPage(Page<UniversitySatisfactionDTO> page, Map<String, Object> params);

    /**
     * 获取特定院校的满意度详情
     *
     * @param universityId 院校ID
     * @return 满意度详情DTO
     */
    UniversitySatisfactionDTO getSatisfactionByUniversityId(Integer universityId);

    /**
     * 保存或更新满意度数据
     *
     * @param satisfaction 满意度数据
     * @return 是否成功
     */
    boolean saveOrUpdateSatisfaction(UniversitySatisfaction satisfaction);
}
