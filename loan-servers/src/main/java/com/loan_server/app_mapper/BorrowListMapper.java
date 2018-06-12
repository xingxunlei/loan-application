package com.loan_server.app_mapper;

import java.util.List;
import java.util.Map;

import com.loan_entity.contract.IdEntity;
import com.loan_entity.loan_vo.RobotData;
import org.apache.ibatis.annotations.Param;

import com.loan_entity.app.BorrowList;
import com.loan_entity.app.ProdMode;
import com.loan_entity.utils.BorrPerInfo;
import com.loan_entity.utils.RepaymentDetails;

public interface BorrowListMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BorrowList record);

    int insertSelective(BorrowList record);

    BorrowList selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BorrowList record);

    int updateByPrimaryKey(BorrowList record);

    //根据per_id查询用户当前的的borrow_list
    BorrowList selectNow(Integer per_id);

    //根据per_id查询用户所有borrow_list
    List<BorrowList> selectByPerId(@Param("per_id")Integer per_id);

    //根据per_id,借款状态查询borrow_list
    List<BorrowList> selectBorrowingByPerId(@Param("per_id")Integer per_id,@Param("borr_status")String borr_status);

    List<BorrowList> getMyBorrowList(String userId,int start,int end);

    List<ProdMode> getProdModeByBorrId(String borrId);


    RepaymentDetails getRepaymentDetails(String borrId);

    BorrPerInfo selectByBorrId(Integer borrId);

    List<BorrowList> getMingtianhuankuanId(String date,String date1);

    int updateByBrroNum(BorrowList record);

    BorrowList selectByPrimaryKey2(Integer id);

    int selectDoing(Integer per_id);

    BorrowList selectByBorrNum(String borr_num);

    int rejectManualReview();

    List<BorrowList> getBorrList(Map<String, Object> map);

    List<RobotData> getRobotData();

    List<Integer> syncWhiteList();
    
    List<String> syncPhoneWhiteList();
    /**查询上一单*/
    BorrowList lastOrder(@Param("perId") Integer per_id);
    /**查询该用户有无借款*/
    List<BorrowList> selectBorrowPay(@Param("perId") Integer per_id);
    /*****根据Id查询身份信息*******/
    IdEntity queryIdentityById(Integer borrId);

    /**
     * 查询用户结清的借款笔数
     */
    int selectCount(Integer per_id);

    Map<String,String> selectPersonByBorrId(Integer borrId);
}
