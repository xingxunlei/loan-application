package com.loan_server.app_mapper;

import org.apache.ibatis.annotations.Param;

import com.loan_entity.app.PerBpm;

public interface PerBpmMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PerBpm record);

    int insertSelective(PerBpm record);

    PerBpm selectByPrimaryKey(Integer id);
    
    //根据per_id查询流程
    PerBpm selectByPerId(@Param("per_id")Integer per_id);

    int updateByPrimaryKeySelective(PerBpm record);

    int updateByPrimaryKey(PerBpm record);
    
}