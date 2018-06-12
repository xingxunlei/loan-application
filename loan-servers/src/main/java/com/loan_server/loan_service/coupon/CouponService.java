package com.loan_server.loan_service.coupon;

import com.loan_entity.utils.BorrPerInfo;

public abstract interface CouponService{
	
  public static final String REPAYMENT_BACK_FIVE = "正常还款立减5元券";
  
  public static final String REPAYMENT_BACK_HUNDRED_SEVEN_DAY = "正常还款500元7天";
  public static final String REPAYMENT_BACK_HUNDRED_FOURTEEN_DAY = "正常还款500元14天";
  public static final String REPAYMENT_BACK_THOUSAND_SEVEN_DAY = "正常还款1000元7天";
  public static final String REPAYMENT_BACK_THOUSAND_FOURTEEN_DAY = "正常还款1000元14天";
  

  public void grantCoupon(String paramString, int paramInt, BorrPerInfo paramBorrPerInfo);
}