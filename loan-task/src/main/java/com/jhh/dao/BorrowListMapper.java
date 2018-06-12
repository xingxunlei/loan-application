package com.jhh.dao;

import java.util.List;
import java.util.Map;

import com.jhh.model.*;

public interface BorrowListMapper {

    List<BorrowList> getMingtianhuankuanId(String date,String date1);
    //获取借款专家订单信息
    List<BorrowingExpertsOrder> getBorrowingOrder(String source);

    BorrPerInfo selectByBorrId(Integer borrId);

    List<RobotData> getRobotData();

    int rejectManualReview();

    List<MoneyManagement> sendMoneyManagement(Map map);

    List<MoneyManagementSecond> sendMoneyManagementSecond(Map map);

    List<MoneyHaier> sendMoneyToHaier(Map map);
}
