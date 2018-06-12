package com.loan_manage.service;

import com.loan_entity.manager_vo.BpmNodeVo;

import java.util.List;

/**
 * Create by Jxl on 2017/9/12
 */
public interface BpmService {
    /**
     * 获取个人认证相关节点信息
     * @Author Jxl
     * @Date 2017/9/12 15:26
     * @param personId
     * @return
     */
    List<BpmNodeVo>  getBpmNodesByPerId(String personId);
 }
