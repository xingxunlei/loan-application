package com.loan_server.manager_mapper;

import java.util.List;

import com.loan_entity.app.BorrowList;
import com.loan_entity.app.Image;
import com.loan_entity.app.Riewer;
import com.loan_entity.manager.Review;
import com.loan_entity.manager_vo.BankInfoVo;
import com.loan_entity.manager_vo.CardPicInfoVo;
import com.loan_entity.manager_vo.LoanInfoVo;
import com.loan_entity.manager_vo.PrivateVo;
import com.loan_entity.manager_vo.ReqBackPhoneCheckVo;

public interface ManagerOfLoanMapper {
    

    int updateByPrimaryKeySelective(ReqBackPhoneCheckVo record);
    
    List<Riewer> selectRiewerList(String status);
    List<Riewer> selectRiewerListAll();
    
    PrivateVo selectUserPrivateVo(int perid);
    
    List<LoanInfoVo>  selectLoanInfoPrivateVo(int himid);
    
    List<BankInfoVo> selectBankInfoVo(int himid);
    
    int personCheckMessage(Review record);
    int transferPersonCheck(Review record);
    
    CardPicInfoVo getCardPicById(int himid);
    List<Image> PicBatchVo(int pageIndex, int pageSize);
}