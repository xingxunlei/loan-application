package com.loan_server.app_mapper;

import com.loan_entity.app.Sensetime;

public interface SensetimeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Sensetime record);

    int insertSelective(Sensetime record);

    Sensetime selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Sensetime record);

    int updateByPrimaryKey(Sensetime record);
    
    int selectTimes(String per_id);
}