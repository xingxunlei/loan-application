package com.loan_manage.service;

import java.util.List;

public interface BaseDrainageService<T,R> {
    int addDrainage(T t);
    int deleteDrainage(String ids);
    int updateDrainage(T t);
    List<R> queryDrains(T t);
    R queryById(String id);
}
