package com.loan_api.app;

import com.loan_entity.app.NoteResult;
import com.loan_entity.app.Person;
import com.loan_entity.manager.Feedback;

public interface UserService {
	
	public String userLogin(String phone,String password);
	public String userRegister(Person user);
	public String userInviceCoupon(int userid,String couponName);
	public String updatePassword(Person user);
	public String getPersonInfo(String userId);
	public String getPersonByPhone(String phone);
	
	public String personUpdatePassword(Person user);
	public String userFeedBack(Feedback feed);
	public String getQuestion();
	public String getMessageByUserId(String userId,int nowPage,int pageSize);
	public String setMessage(String userId,String templateId,String params);
	public String getMyBorrowList(String userId,int nowPage,int pageSize);
	public String updateMessageStatus(String userId,String messageId);
	public String getProdModeByBorrId(String borrId);
	public String getRepaymentDetails(String userId,String borrId);
	public String perAccountLog(String userId,int nowPage,int pageSize);
	public String canOfOrder(String orderId,String token,String info);
	public String canbaiqishi(String tokenKey,String event,String phone);
	public String updatePasswordCanbaiqishi(String tokenKey,String event,String userId);
	public String getMyCoupon(String userId,String couponStatus,int nowPage,int pageSize);
	public int yanzhengtoken(String userId,String token);
	public NoteResult checkBlack(String phone);
	public String deleteRedis(String per_id);

	public NoteResult getWithdrawInformation(int userid);

	public boolean isInWhiteList(String userid);

	public void syncWhiteList();

	public void syncPhoneWhiteList();

}