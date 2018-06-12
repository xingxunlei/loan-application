package com.loan_server.app_mapper;

import com.loan_entity.app.AppVersion;

public interface AppVersionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppVersion record);

    int insertSelective(AppVersion record);

    AppVersion selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AppVersion record);

    int updateByPrimaryKey(AppVersion record);
    
    //根据app系统名称获取最新版本
    AppVersion selectByAppName(String name);
    
    //根据app系统名称及版本号查询是否强制更新
    AppVersion selectByAppNameVersion(String name,String version);
}