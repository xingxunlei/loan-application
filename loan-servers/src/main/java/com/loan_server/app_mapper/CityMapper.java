package com.loan_server.app_mapper;

import java.util.List;

import com.loan_entity.app.City;

public interface CityMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(City record);

    int insertSelective(City record);

    City selectByPrimaryKey(Integer id);
    
    List<City> findByPid(Integer pid);

    int updateByPrimaryKeySelective(City record);
        
    int updateByPrimaryKey(City record);
}