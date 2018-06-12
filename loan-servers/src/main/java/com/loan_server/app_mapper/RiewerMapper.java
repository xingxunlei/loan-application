package com.loan_server.app_mapper;

import java.util.List;

import com.loan_entity.app.Riewer;

public interface RiewerMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Riewer record);

    int insertSelective(Riewer record);

    Riewer selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Riewer record);

    int updateByPrimaryKey(Riewer record);
    
    //查询所有审核人的员工编号
    List<String> selectEmployNum();
}