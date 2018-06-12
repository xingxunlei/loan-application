package com.loan_manage.mapper;

import java.util.List;
import java.util.Map;

public interface DrainageMapper {
    List<Map> queryDrainages(Map m);
    int insertDrainage(Map m);
    int updateDrainage(Map m);
    int deleteDrainageById(String ids);
    Map queryById(String id);
}
