package com.loan_manage.service;

import java.util.List;
import java.util.Map;

import com.loan_entity.app.BankVo;
import com.loan_entity.loan.ExportWorkReport;
import com.loan_entity.manager_vo.CardPicInfoVo;
import com.loan_entity.manager_vo.PhoneBookVo;
import com.loan_entity.manager_vo.PrivateVo;
import com.loan_entity.manager_vo.ReviewVo;
import com.loan_manage.entity.DownloadOrder;
import com.loan_manage.entity.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface LoanService {
	/**
	 * 查询流水列表
	 * @param map
	 * @return
	 */
	public List getOrders(Map<String, String[]> map,int offset, int size) ;
	/**
	 * 查询合同列表
	 * @param borrId
	 * @return
	 */
	public Map<String, Object> getBorrowList(String borrId) ;
	
	/**
	 * 查询用户基本信息
	 * @param perId
	 * @return
	 */
	public PrivateVo getUserInfo(int perId);
	
	/**
	 * 身份证信息
	 * @param perId
	 * @return
	 */
	CardPicInfoVo getCardInfo(int perId);
	
	/**
	 * 查看优惠券列表
	 * @param couponId
	 * @param prodId
	 * @return
	 */
	public List<Map<String, Object>> getPerCoupon(String couponId, String prodId);
	/**
	 * 银行卡信息列表
	 * @return
	 */
	public List<BankVo> getBanks(Integer perId);
	
	/**
	 * 备注信息
	 * @return
	 */
	public List getMemos(Map<String, String[]> param , Integer borrId);
	
	/**
	 * 获取工作报表
	 * @param map
	 * @return
	 */
	public List<ExportWorkReport> workReport(Map<String, Object> map);

	/**
	 * 导出工作报表
	 * @param map
	 * @return
	 */
	public void exportWorkReport(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response);
	
	/**
	 * 聚信立信用
	 * @return
	 */
	public List getPolyXinliCredit(String idCard, String name, int offset, int size, Integer perId);
	/**
	 * 征信报告
	 * @return
	 */
	public String getCreditInvestigation(String idCard, String name);
	/**
	 * 通讯录信息
	 */
	List getContact(Integer perId, String phones, int offset, int size);

	/**
	 * 导出通讯录信息查询
	 * @param perId
	 * @return
	 */
	List<PhoneBookVo> getContactForExport(Integer perId);

	/**
	 *	保存单子表
	 * @param borrId
	 * @return
	 */
	public void saveCollectorsList(String borrId, String status);

	/**
	 * 黑名单记录操作
	 * @param perId
	 * @return
	 */
	List<ReviewVo> getReviewVoBlackList(int perId);

    List<DownloadOrder> selectDownloadOrder(Map<String, Object> queryMap ,String userNo);

	/**
	 * 查询银生宝回调
	 * @param serialNo
	 * @return
	 */
	Result queryCostState(String serialNo);
}
