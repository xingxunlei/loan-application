package com.loan_manage.service.impl;

import com.loan_entity.app.BorrowList;
import com.loan_entity.enums.BorrowStatusEnum;
import com.loan_manage.entity.Result;
import com.loan_manage.mapper.BorrowListMapper;
import com.loan_manage.service.RiskService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Create by Jxl on 2017/9/11
 */
@Service
public class RiskServiceImpl implements RiskService {
    @Autowired
    BorrowListMapper borrowListMapper;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Result cancelLoan(String borrowId,String userId) {
        Result result = new Result();
        if(!StringUtils.isNumeric(borrowId) || StringUtils.isBlank(userId)){
            result .setCode(Result.FAIL);
            result.setMessage("请求参数异常!");
            return result;
        }
        BorrowList borrow = borrowListMapper.selectByPrimaryKey(Integer.valueOf(borrowId).intValue());
        if( borrow == null ){
            result.setCode(Result.FAIL);
            result.setMessage("未找到要取消的借款记录!");
            return result;
        }
        if(!StringUtils.equals(borrow.getBorrStatus(), BorrowStatusEnum.LOAN_FAIL.getCode()) &&
                !StringUtils.equals(borrow.getBorrStatus(),BorrowStatusEnum.SIGNED.getCode())){
            //只有已签约或放款失败状态可以做取消放款操作
            result.setCode(Result.FAIL);
            result.setMessage("当前借款状态无法进行取消借款操作!");
            return result;
        }
        borrow.setBorrStatus(BorrowStatusEnum.CANCEL.getCode());
        borrow.setUpdateDate(new Date());
        //TODO  borrow表update_user  和create_user 都是integer类型的.暂时不记录操作者
//        borrow.setUpdateUser(Integer.valueOf(userId));
        borrowListMapper.updateByPrimaryKey(borrow);
        result.setCode(Result.SUCCESS);
        result.setMessage("借款已取消!");
        return result;
    }
}
