
package com.loan_api.manager;

import java.util.List;

import com.loan_entity.manager.CodeType;
import com.loan_entity.manager.CodeValue;
import com.loan_entity.manager.Coupon;
import com.loan_entity.manager.Feedback;
import com.loan_entity.manager.Msg;
import com.loan_entity.manager.MsgTemplate;
import com.loan_entity.manager.Question;
import com.loan_entity.manager_vo.CouponVo;
import com.loan_entity.manager_vo.FeedbackVo;
import com.loan_entity.manager_vo.MsgTemplateVo;
import com.loan_entity.manager_vo.QuestionVo;
import com.loan_entity.utils.ManagerResult;

/**
 *描述：
 *@author: Wanyan
 *@date： 日期：2016年10月18日 时间：下午2:51:11
 *@version 1.0
 */
public interface ManageInfoService {
	
	public List<CodeType> getCodeTypeList();
	public ManagerResult deleteCodeType(String idfordel);
	public ManagerResult insertCodeType(CodeType record);
	public ManagerResult UpdateCodeType(CodeType record);
	
	public List<CodeValue> getCodeValueListByCode(String code_type);
    public ManagerResult deleteCodeValue(String idfordel);
	public ManagerResult insertCodeValue(CodeValue record);
	public ManagerResult UpdateCodeValue(CodeValue record);
	
	public ManagerResult insertQuestion(Question record);
	public ManagerResult UpdateQuestion(Question record);
	public List<QuestionVo> getAllQuestionList();
	
	public ManagerResult insertMsg(Msg record);
	public ManagerResult UpdateMsg(Msg record);
	
	public ManagerResult insertMsgTemplate(MsgTemplate record);
	public ManagerResult UpdateMsgTemplate(MsgTemplate record);
	public List<MsgTemplateVo> getAllMsgTemplateList();
	
	public List<FeedbackVo> getFeedbackList();
	
	public ManagerResult insertCoupon(Coupon record);
	public ManagerResult updateCoupon(Coupon record);
	public List<CouponVo> getAllCouponList();
}
