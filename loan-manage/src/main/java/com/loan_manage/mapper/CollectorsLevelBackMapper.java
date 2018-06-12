package com.loan_manage.mapper;

import com.loan_entity.loan.CollectorsLevelBack;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface CollectorsLevelBackMapper extends Mapper<CollectorsLevelBack> {

    List<CollectorsLevelBack> queryCreateUserName(Map<String ,Object> map);

    List<CollectorsLevelBack> queryCollectionUserName(Map<String ,Object> map);

    Integer insertList(List list);

    Integer delAll();
}
