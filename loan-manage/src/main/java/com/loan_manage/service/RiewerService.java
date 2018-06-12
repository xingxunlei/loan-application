package com.loan_manage.service;

import java.util.Date;

import com.loan_manage.entity.Result;
import com.loan_manage.entity.Riewer;

public interface RiewerService {
	
	int deleteByPrimaryKey(Integer id);

    int insert(Riewer record);

    int insertSelective(Riewer record);

    Riewer selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Riewer record);

    int updateByPrimaryKey(Riewer record);

    /**
     * 人工审核操作
     * @param borroId
     * @param operationType
     * @return
     */
    Result manuallyReview(Integer borroId, String reason, String userNum, Integer operationType);

}