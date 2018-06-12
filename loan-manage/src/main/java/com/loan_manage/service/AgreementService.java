package com.loan_manage.service;

import java.util.List;
import java.util.Map;

/**
 * @author xingmin 
 */
public interface AgreementService {

    /**
     * 生成合同文件
     * @param perId
     * @param borrId
     * @return
     */
    List<Map<String,String>> generateFiles(String perId, String borrId);
}
