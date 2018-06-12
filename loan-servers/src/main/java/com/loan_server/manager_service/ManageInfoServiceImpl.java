
package com.loan_server.manager_service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.loan_api.manager.ManageInfoService;
import com.loan_entity.manager.CodeType;
import com.loan_entity.manager.CodeValue;
import com.loan_entity.manager.Coupon;
import com.loan_entity.manager.Msg;
import com.loan_entity.manager.MsgTemplate;
import com.loan_entity.manager.Question;
import com.loan_entity.manager_vo.CouponVo;
import com.loan_entity.manager_vo.FeedbackVo;
import com.loan_entity.manager_vo.MsgTemplateVo;
import com.loan_entity.manager_vo.QuestionVo;
import com.loan_entity.utils.ManagerResult;
import com.loan_server.manager_mapper.CodeTypeMapper;
import com.loan_server.manager_mapper.CodeValueMapper;
import com.loan_server.manager_mapper.CouponMapper;
import com.loan_server.manager_mapper.FeedbackMapper;
import com.loan_server.manager_mapper.MsgMapper;
import com.loan_server.manager_mapper.MsgTemplateMapper;
import com.loan_server.manager_mapper.QuestionMapper;

/**
 *描述：
 *@author: Wanyan
 *@date： 日期：2016年10月19日 时间：上午11:53:57
 *@version 1.0
 */


public class ManageInfoServiceImpl implements ManageInfoService{
	private static Logger log = Logger.getLogger(ManageInfoServiceImpl.class);
	@Autowired
	private CodeTypeMapper codeTypeMapper;
	/* (non-Javadoc)
	 * @see com.loan_api.manager.ManageInfoService#getCodeTypeList()
	 */
	@Override
	public List<CodeType> getCodeTypeList() {

		return codeTypeMapper.getCodeTypeList();
	}

	/* (non-Javadoc)
	 * @see com.loan_api.manager.ManageInfoService#deleteCodeType(java.lang.Integer)
	 */
	@Override
	public ManagerResult deleteCodeType(String idfordel) {
		String[] brroid= idfordel.split(",");
		
		ManagerResult managerResult  = new ManagerResult();
		for (int i = 0; i < brroid.length; i++) {
			try {
				int result =codeTypeMapper.deleteByPrimaryKey(Integer.parseInt(brroid[i]));
					managerResult.setCode(result);
					if(result>0){
						managerResult.setMessage("处理成功！");
					}else{
						managerResult.setMessage("处理失败！");
					}
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		return managerResult;
	}
	public ManagerResult insertCodeType(CodeType record) {
		ManagerResult managerResult  = new ManagerResult();
		try {
			int result =codeTypeMapper.insert(record);
				managerResult.setCode(result);
				if(result>0){
					managerResult.setMessage("处理成功！");
				}else{
					managerResult.setMessage("处理失败！");
				}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return managerResult;
	}

	public ManagerResult UpdateCodeType(CodeType record) {
		ManagerResult managerResult  = new ManagerResult();
		try {
			int result =codeTypeMapper.updateByPrimaryKeySelective(record);
				managerResult.setCode(result);
				if(result>0){
					managerResult.setMessage("处理成功！");
				}else{
					managerResult.setMessage("处理失败！");
				}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return managerResult;
	}
	@Autowired
	private CodeValueMapper codeValueMapper;
	/* (non-Javadoc)
	 * @see com.loan_api.manager.ManageInfoService#getCodeValueListByCode(java.lang.String)
	 */
	@Override
	public List<CodeValue> getCodeValueListByCode(String code_type) {
		// TODO Auto-generated method stub
		return codeValueMapper.getCodeValueListByCode(code_type);
	}

	/* (non-Javadoc)
	 * @see com.loan_api.manager.ManageInfoService#deleteCodeValue(java.lang.Integer)
	 */
	@Override
	public ManagerResult deleteCodeValue(String idfordel) {
	String[] brroid= idfordel.split(",");
		
		ManagerResult managerResult  = new ManagerResult();
		for (int i = 0; i < brroid.length; i++) {
			try {
				int result =codeValueMapper.deleteByPrimaryKey(Integer.parseInt(brroid[i]));
					managerResult.setCode(result);
					if(result>0){
						managerResult.setMessage("处理成功！");
					}else{
						managerResult.setMessage("处理失败！");
					}
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		return managerResult;
	}

	public ManagerResult insertCodeValue(CodeValue record) {
		ManagerResult managerResult  = new ManagerResult();
		try {
			int result =codeValueMapper.insert(record);
				managerResult.setCode(result);
				if(result>0){
					managerResult.setMessage("处理成功！");
				}else{
					managerResult.setMessage("处理失败！");
				}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return managerResult;
	}

	public ManagerResult UpdateCodeValue(CodeValue record) {
		ManagerResult managerResult  = new ManagerResult();
		try {
			int result =codeValueMapper.updateByPrimaryKeySelective(record);
				managerResult.setCode(result);
				if(result>0){
					managerResult.setMessage("处理成功！");
				}else{
					managerResult.setMessage("处理失败！");
				}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return managerResult;
	}
	@Autowired
	private QuestionMapper questionMapper;
	public ManagerResult insertQuestion(Question record) {
		ManagerResult managerResult  = new ManagerResult();
		try {
			int result =questionMapper.insert(record);
				managerResult.setCode(result);
				if(result>0){
					managerResult.setMessage("处理成功！");
				}else{
					managerResult.setMessage("处理失败！");
				}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return managerResult;
	}

	public ManagerResult UpdateQuestion(Question record) {
		ManagerResult managerResult  = new ManagerResult();
		try {
			int result =questionMapper.updateByPrimaryKeySelective(record);
				managerResult.setCode(result);
				if(result>0){
					managerResult.setMessage("处理成功！");
				}else{
					managerResult.setMessage("处理失败！");
				}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return managerResult;
	}
	/* (non-Javadoc)
	 * @see com.loan_api.manager.ManageInfoService#getAllQuestionList()
	 */
	@Override
	public List<QuestionVo> getAllQuestionList() {
		// TODO Auto-generated method stub
		return questionMapper.getAllQuestionList();
	}

	@Autowired
	private MsgMapper msgMapper;
	public ManagerResult insertMsg(Msg record) {
		ManagerResult managerResult  = new ManagerResult();
		try {
			int result =msgMapper.insert(record);
				managerResult.setCode(result);
				if(result>0){
					managerResult.setMessage("处理成功！");
				}else{
					managerResult.setMessage("处理失败！");
				}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return managerResult;
	}

	public ManagerResult UpdateMsg(Msg record) {
		ManagerResult managerResult  = new ManagerResult();
		try {
			int result =msgMapper.updateByPrimaryKeySelective(record);
				managerResult.setCode(result);
				if(result>0){
					managerResult.setMessage("处理成功！");
				}else{
					managerResult.setMessage("处理失败！");
				}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return managerResult;
	}
	@Autowired
	private MsgTemplateMapper msgTemplateMapper;
	public ManagerResult insertMsgTemplate(MsgTemplate record) {
		ManagerResult managerResult  = new ManagerResult();
		try {
			int result =msgTemplateMapper.insert(record);
				managerResult.setCode(result);
				if(result>0){
					managerResult.setMessage("处理成功！");
				}else{
					managerResult.setMessage("处理失败！");
				}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return managerResult;
	}

	public ManagerResult UpdateMsgTemplate(MsgTemplate record) {
		ManagerResult managerResult  = new ManagerResult();
		try {
			int result =msgTemplateMapper.updateByPrimaryKeySelective(record);
				managerResult.setCode(result);
				if(result>0){
					managerResult.setMessage("处理成功！");
				}else{
					managerResult.setMessage("处理失败！");
				}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return managerResult;
	}
	/* (non-Javadoc)
	 * @see com.loan_api.manager.ManageInfoService#getAllMsgTemplateList()
	 */
	@Override
	public List<MsgTemplateVo> getAllMsgTemplateList() {
		// TODO Auto-generated method stub
		return msgTemplateMapper.getAllMsgTemplateList();
	}

	/* (non-Javadoc)
	 * @see com.loan_api.manager.ManageInfoService#getFeedbackList()
	 */
	@Autowired
	private FeedbackMapper feedbackMapper;
	@Override
	public List<FeedbackVo> getFeedbackList() {
		// TODO Auto-generated method stub
		return feedbackMapper.getFeedbackList();
	}

	/* (non-Javadoc)
	 * @see com.loan_api.manager.ManageInfoService#insertCoupon(com.loan_entity.manager.Coupon)
	 */
	@Autowired
	private CouponMapper couponMapper;
	/* (non-Javadoc)
	 * @see com.loan_api.manager.ManageInfoService#insertCoupon(com.loan_entity.manager.Coupon)
	 */
	@Override
	public ManagerResult insertCoupon(Coupon record) {
		ManagerResult managerResult  = new ManagerResult();
		try {
			int result =couponMapper.insert(record);
				managerResult.setCode(result);
				if(result>0){
					managerResult.setMessage("处理成功！");
				}else{
					managerResult.setMessage("处理失败！");
				}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return managerResult;
	}

	/* (non-Javadoc)
	 * @see com.loan_api.manager.ManageInfoService#updateCoupon(com.loan_entity.manager.Coupon)
	 */
	@Override
	public ManagerResult updateCoupon(Coupon record) {
		ManagerResult managerResult  = new ManagerResult();
		try {
			int result =couponMapper.updateByPrimaryKeySelective(record);
				managerResult.setCode(result);
				if(result>0){
					managerResult.setMessage("处理成功！");
				}else{
					managerResult.setMessage("处理失败！");
				}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return managerResult;
	}

	/* (non-Javadoc)
	 * @see com.loan_api.manager.ManageInfoService#getAllCouponList()
	 */
	@Override
	public List<CouponVo> getAllCouponList() {
		// TODO Auto-generated method stub
		return couponMapper.getAllCouponList();
	}
	
	
	

	
	
}
