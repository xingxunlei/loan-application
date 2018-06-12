
package com.loan_api.manager;

import java.util.List;

import com.loan_entity.app.Bank;
import com.loan_entity.app.BorrowList;
import com.loan_entity.app.Product;
import com.loan_entity.app.ProductChargeModel;
import com.loan_entity.app.ProductTerm;
import com.loan_entity.app.Riewer;
import com.loan_entity.manager.Order;
import com.loan_entity.manager.RepaymentPlan;
import com.loan_entity.manager_vo.BankInfoVo;
import com.loan_entity.manager_vo.CardPicInfoVo;
import com.loan_entity.manager_vo.LoanInfoVo;
import com.loan_entity.manager_vo.PrivateVo;
import com.loan_entity.manager_vo.ReqBackPhoneCheckVo;
import com.loan_entity.manager_vo.ReviewVo;
import com.loan_entity.utils.ManagerResult;
import com.loan_entity.utils.ManagerResultForNet;

/**
 *描述：
 *@author: Wanyan
 *@date： 日期：2016年10月18日 时间：下午2:51:11
 *@version 1.0
 */
public interface ManageLoanService {

	public ManagerResult backPhoneCheckMessage(ReqBackPhoneCheckVo record);
	public ManagerResultForNet offlineTransfer(String record);
	
	public ManagerResult personCheckMessage(String brroid,String status,String employ_num, String reason);
	
	public ManagerResult transferPersonCheck(String brroid_list,String transfer);
	public ManagerResult UpdateBorrowList(BorrowList record);
	
	public ManagerResult insertRepaymentPlan(RepaymentPlan record);
	public ManagerResult UpdateRepaymentPlan(RepaymentPlan record);
	
	public ManagerResult insertOrder(Order record);
	
	public ManagerResult insertBank(Bank record);
	public ManagerResult UpdateBank(Bank record);
	
	public ManagerResult insertProduct(Product record);
	public ManagerResult UpdateProduct(Product record);
	public List<Product> getAllProduct();
	
	public ManagerResult insertProductTerm(ProductTerm record);
	public ManagerResult UpdateProductTerm(ProductTerm record);
	
	public ManagerResult insertProductChargeModel(ProductChargeModel record);
	public ManagerResult UpdateProductChargeModel(ProductChargeModel record);

	List<BorrowList> getBorrList(String borrIds, String borrStatus);
	
	public List<Riewer> selectRiewerList(String status);
	List<Riewer> selectRiewerListAll();
	  
	PrivateVo selectUserPrivateVo(int perid);
	
	List<LoanInfoVo> selectLoanInfoPrivateVo(int himid);
	
	List<BankInfoVo> selectBankInfoVo(int himid);
	
	public ManagerResult managerbymanager( String brroid_list, String status);
	
	
	CardPicInfoVo getCardPicById(int himid);
	public  ManagerResult getRiskReport(int himid);
	
	public ManagerResult DoDsBatchVo();
	public ManagerResult PicBatchVo(int pageIndex,int pageSize);
	
	public ManagerResult goBlackList(String himid_list, String blacklist);
	public ManagerResult goReviewCheck(String himid_list, String blacklist,String reason,String usernum,String type);
	
	List<ReviewVo> getReviewVoBlackList(int himid);

	/**
	 * 。net清结算后回调同步
	 * 
	 * @return
	 */
	public String doDsBatchVoback();

	public List getOrders(String uid);
}
