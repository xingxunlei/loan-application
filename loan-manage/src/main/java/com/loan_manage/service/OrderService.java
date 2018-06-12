package com.loan_manage.service;

import com.github.pagehelper.PageInfo;
import com.loan_manage.entity.OrderVo;

import java.util.Map;

public interface OrderService {

    PageInfo<OrderVo> selectOrderVoInfo(Map<String,Object> queryMap, Integer start, Integer size, String userNo);
}
