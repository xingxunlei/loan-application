package com.loan_api.loan;

import com.loan_entity.app.NoteResult;

public interface YsbpayService {

	public String ysbPayment(String userId, String amount, String conctact_id,
			String bank_id, String cardNo,String token);

	/**
	 * 还款认证支付回调后台地址
	 * 
	 * @param result_code
	 * @param result_msg
	 * @param amount
	 * @param orderId
	 * @param mac
	 * @return
	 * @throws Exception
	 */
	public String callbackBackground(String result_code, String result_msg,
			String amount, String orderId, String mac) throws Exception;
	
	public String callbackBackgroundByNet(String orderJson) throws Exception;
	
	public String payCont(String per_id,String borrId) throws Exception;
	
	public String payContCallBack(String result_code, String result_msg,
			String amount, String orderId, String mac) throws Exception;
	//定时任务查询处理中的订单
	public void queryCall();
	//定时查询还款处理中的订单
	public String queryPayment();
	//2017.03.27---APP还款提交
	public NoteResult AppRepay(String per_id,String serial,String amount);

    public void queryQ();

    public int settlement(String orderId, String status, String msg);

	public NoteResult canPayCollect(String borrId, double thisAmount);

//	public NoteResult testBeike();

	/*
	 * 检查是否可以还款、代扣   true可以还款、代扣，并set lock ; false不可以
	 * @param borrId : borrow_list 主键
	 */

//	public boolean checkLock(Integer borrId);
//
//	/*
//	 * 删除还款、代扣、批量代扣的锁
//	 * @param borrId : borrow_list 主键
//	 */
//	public boolean deleteLock(Integer borrId);

	/*
		给其他系统查询本系统内用户的借款金额
	 */
	public String getAmountByCardNum(String cardNum);

	public boolean canPayCont(double amount, String cardNum);

	public void fileCaozuo(String orderId, String result_msg);

	public void paymentFail(String orderId, String desc);

}
