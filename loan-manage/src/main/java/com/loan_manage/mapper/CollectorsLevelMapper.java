package com.loan_manage.mapper;

import com.loan_entity.loan.CollectorsLevel;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface CollectorsLevelMapper extends Mapper<CollectorsLevel> {

    Integer selectMaxId();

    List<CollectorsLevel> selectDsUsers(Map<String,Object> params);
}
