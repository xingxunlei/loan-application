package com.loan_server.app_mapper;

import com.loan_entity.app.ZhiMa;

public interface ZhiMaMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ZhiMa record);

    int insertSelective(ZhiMa record);

    ZhiMa selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ZhiMa record);

    int updateByPrimaryKey(ZhiMa record);

    ZhiMa selectByPer_Id(Integer per_id);
}