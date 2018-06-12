package com.loan_manage.service;

import java.util.List;
import java.util.Map;

/**
 * Create by wanzezhong on
 * 2018年3月28日 15:01:50
 */
public interface ArbitrationService {

    /**
     * 仲裁项目准备材料
     * @param perId
     * @param borrId
     * @return
     */
    List<Map<String, String>> transferArbitration(String perId, String borrId);
}
