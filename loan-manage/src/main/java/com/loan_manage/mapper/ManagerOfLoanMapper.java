package com.loan_manage.mapper;

import com.loan_entity.manager_vo.BankInfoVo;
import com.loan_entity.manager_vo.CardPicInfoVo;
import com.loan_entity.manager_vo.LoanInfoVo;
import com.loan_entity.manager_vo.PrivateVo;

import java.util.List;

public interface ManagerOfLoanMapper  {

    PrivateVo selectUserPrivateVo(int perid);

    CardPicInfoVo getCardPicById(int himid);

    List<LoanInfoVo> selectLoanInfoPrivateVo(int himid);

    List<BankInfoVo> selectBankInfoVo(int himid);

    List<LoanInfoVo> selectLoanInfoPrivateVoForOperator(int himid);
}
