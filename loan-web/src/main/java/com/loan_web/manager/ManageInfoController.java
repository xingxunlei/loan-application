package com.loan_web.manager;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.loan_api.manager.ManageInfoService;
import com.loan_entity.manager.CodeType;
import com.loan_entity.manager.CodeValue;
import com.loan_entity.manager.Coupon;
import com.loan_entity.manager.MsgTemplate;
import com.loan_entity.manager.Question;
import com.loan_entity.manager_vo.CouponVo;
import com.loan_entity.manager_vo.FeedbackVo;
import com.loan_entity.manager_vo.MsgTemplateVo;
import com.loan_entity.manager_vo.QuestionVo;
import com.loan_entity.utils.ManagerResult;

import javax.servlet.http.HttpServletResponse;

/**
 * 描述：
 *
 * @author: Wanyan
 * @date： 日期：2016年10月19日 时间：上午11:53:57
 * @version 1.0
 */
@Controller
@RequestMapping("/info")
public class ManageInfoController {
	@Autowired
	private ManageInfoService manageInfoService;

	@ResponseBody
	@RequestMapping(value = "/getCodeTypeList", method = RequestMethod.POST)
	public List<CodeType> getCodeTypeList(HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		return manageInfoService.getCodeTypeList();
	}

	@ResponseBody
	@RequestMapping(value = "/deleteCodeType", method = RequestMethod.POST)
	public ManagerResult deleteCodeType(String idfordel) {

		return manageInfoService.deleteCodeType(idfordel);
	}

	@ResponseBody
	@RequestMapping(value = "/insertCodeType", method = RequestMethod.POST)
	public ManagerResult insertCodeType(CodeType record) {
		// TODO Auto-generated method stub
		return manageInfoService.insertCodeType(record);
	}

	@ResponseBody
	@RequestMapping(value = "/UpdateCodeType", method = RequestMethod.POST)
	public ManagerResult UpdateCodeType(CodeType record) {
		// TODO Auto-generated method stub
		return manageInfoService.UpdateCodeType(record);
	}

	@ResponseBody
	@RequestMapping(value = "/getCodeValueListByCode", method = RequestMethod.POST)
	public List<CodeValue> getCodeValueListByCode(String code_type) {
		return manageInfoService.getCodeValueListByCode(code_type);
	}

	@ResponseBody
	@RequestMapping(value = "/deleteCodeValue", method = RequestMethod.POST)
	public ManagerResult deleteCodeValue(String idfordel) {
		return manageInfoService.deleteCodeValue(idfordel);
	}

	@ResponseBody
	@RequestMapping(value = "/insertCodeValue", method = RequestMethod.POST)
	public ManagerResult insertCodeValue(CodeValue record) {
		// TODO Auto-generated method stub
		return manageInfoService.insertCodeValue(record);
	}

	@ResponseBody
	@RequestMapping(value = "/UpdateCodeValue", method = RequestMethod.POST)
	public ManagerResult UpdateCodeValue(CodeValue record) {

        // System.out.println(record.getId() + record.getCodeType()
        // + record.getEnabledFlag());
		// TODO Auto-generated method stub
		return manageInfoService.UpdateCodeValue(record);
	}

	@ResponseBody
	@RequestMapping(value = "/insertQuestion", method = RequestMethod.POST)
	public ManagerResult insertQuestion(Question record) {
		// TODO Auto-generated method stub
		record.setUpdateDate(new Date());
		record.setCreateTime(new Date());
		return manageInfoService.insertQuestion(record);
	}

	@ResponseBody
	@RequestMapping(value = "/UpdateQuestion", method = RequestMethod.POST)
	public ManagerResult UpdateQuestion(Question record) {
		// TODO Auto-generated method stub
		return manageInfoService.UpdateQuestion(record);
	}

	@ResponseBody
	@RequestMapping(value = "/getAllQuestionList", method = RequestMethod.POST)
	public List<QuestionVo> getAllQuestionList(HttpServletResponse response) {
		// TODO Auto-generated method stub
		response.setHeader("Access-Control-Allow-Origin", "*");
		return manageInfoService.getAllQuestionList();
	}

	@ResponseBody
	@RequestMapping(value = "/insertMsg", method = RequestMethod.POST)
	public ManagerResult insertMsg() {
		// TODO Auto-generated method stub
		return null;
	}

	@ResponseBody
	@RequestMapping(value = "/UpdateMsg", method = RequestMethod.POST)
	public ManagerResult UpdateMsg() {
		// TODO Auto-generated method stub
		return null;
	}

	@ResponseBody
	@RequestMapping(value = "/insertMsgTemplate", method = RequestMethod.POST)
	public ManagerResult insertMsgTemplate(MsgTemplate record) {
		// TODO Auto-generated method stub
		record.setUpdateDate(new Date());
		record.setCreateTime(new Date());
		return manageInfoService.insertMsgTemplate(record);
	}

	@ResponseBody
	@RequestMapping(value = "/UpdateMsgTemplate", method = RequestMethod.POST)
	public ManagerResult UpdateMsgTemplate(MsgTemplate record) {
		// TODO Auto-generated method stub
		return manageInfoService.UpdateMsgTemplate(record);
	}

	@ResponseBody
	@RequestMapping(value = "/getAllMsgTemplateList", method = RequestMethod.POST)
	public List<MsgTemplateVo> getAllMsgTemplateList() {
		// TODO Auto-generated method stub
		return manageInfoService.getAllMsgTemplateList();
	}

	@ResponseBody
	@RequestMapping(value = "/getFeedbackList", method = RequestMethod.POST)
	public List<FeedbackVo> getFeedbackList() {
		// TODO Auto-generated method stub
		return manageInfoService.getFeedbackList();
	}
	@ResponseBody
	@RequestMapping(value = "/insertCoupon", method = RequestMethod.POST)
	public ManagerResult insertCoupon(Coupon record) {
		// TODO Auto-generated method stub
		record.setUpdateDate(new Date());
		record.setCreationDate(new Date());
		return manageInfoService.insertCoupon(record);
	}
	@ResponseBody
	@RequestMapping(value = "/updateCoupon", method = RequestMethod.POST)
	public ManagerResult updateCoupon(Coupon record) {
		// TODO Auto-generated method stub
		return manageInfoService.updateCoupon(record);
	}
	@ResponseBody
	@RequestMapping(value = "/getAllCouponList", method = RequestMethod.POST)
	public List<CouponVo> getAllCouponList() {
		// TODO Auto-generated method stub
		return manageInfoService.getAllCouponList();
	}
}
