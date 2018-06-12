package com.loan_manage.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.loan_entity.app.BorrowList;
import com.loan_entity.app.Person;
import com.loan_entity.common.Constants;
import com.loan_manage.entity.Result;
import com.loan_manage.mapper.BorrowListMapper;
import com.loan_manage.mapper.PersonMapper;
import com.loan_manage.utils.Assertion;
import com.loan_manage.utils.Detect;
import org.springframework.beans.factory.annotation.Autowired;

import com.loan_manage.entity.Riewer;
import com.loan_manage.mapper.RiewerMapper;
import com.loan_manage.service.RiewerService;
import org.springframework.stereotype.Service;

@Service
public class RiewerServiceImpl implements RiewerService{
	
	@Autowired
	private RiewerMapper riewerMapper;

	@Autowired
	private BorrowListMapper borrowListMapper;

	@Autowired
	private PersonMapper personMapper;

	/* (non-Javadoc)
	 * @see com.loan_manage.service.RiewerService#deleteByPrimaryKey(java.lang.Integer)
	 */
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return riewerMapper.deleteByPrimaryKey(id);
	}

	/* (non-Javadoc)
	 * @see com.loan_manage.service.RiewerService#insert(com.loan_manage.entity.Riewer)
	 */
	public int insert(Riewer record) {
		// TODO Auto-generated method stub
		return riewerMapper.insert(record);
	}

	/* (non-Javadoc)
	 * @see com.loan_manage.service.RiewerService#insertSelective(com.loan_manage.entity.Riewer)
	 */
	public int insertSelective(Riewer record) {
		// TODO Auto-generated method stub
		return riewerMapper.insertSelective(record);
	}

	/* (non-Javadoc)
	 * @see com.loan_manage.service.RiewerService#selectByPrimaryKey(java.lang.Integer)
	 */
	public Riewer selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return riewerMapper.selectByPrimaryKey(id);
	}

	/* (non-Javadoc)
	 * @see com.loan_manage.service.RiewerService#updateByPrimaryKeySelective(com.loan_manage.entity.Riewer)
	 */
	public int updateByPrimaryKeySelective(Riewer record) {
		// TODO Auto-generated method stub
		return updateByPrimaryKeySelective(record);
	}

	/* (non-Javadoc)
	 * @see com.loan_manage.service.RiewerService#updateByPrimaryKey(com.loan_manage.entity.Riewer)
	 */
	public int updateByPrimaryKey(Riewer record) {
		// TODO Auto-generated method stub
		return riewerMapper.updateByPrimaryKey(record);
	}

	@Override
	public Result manuallyReview(Integer borroId,String reason, String userNum, Integer operationType) {
		Assertion.isPositive(borroId, "合同Id不能为空");
		Assertion.isPositive(operationType, "操作类型不能为空");
		Result result = new Result();
		result.setCode(Result.FAIL);

		BorrowList bl = borrowListMapper.selectByPrimaryKey(borroId);
		if(bl != null){
			if(operationType == Constants.OperationType.PASS){
				//审核通过
				bl.setBorrStatus("BS002");
				borrowListMapper.updateByPrimaryKeySelective(bl);
				//更新审核人
				saveRiewer(borroId, reason, userNum);
				result.setCode(Result.SUCCESS);
			}else if(operationType == Constants.OperationType.REJECT){
				//审核拒绝
				bl.setBorrStatus("BS008");
				borrowListMapper.updateByPrimaryKeySelective(bl);
				//更新审核人
				saveRiewer(borroId, reason, userNum);

				result.setCode(Result.SUCCESS);
			}else if(operationType == Constants.OperationType.BLACK){
				//审核拒绝
				bl.setBorrStatus("BS008");
				borrowListMapper.updateByPrimaryKeySelective(bl);
				//更新审核人
				saveRiewer(borroId, reason, userNum);
				//新增拉黑记录
				Map<String, Object> map = new HashMap<String,Object>();
				map.put("borrId",bl.getPerId());
				map.put("reason",reason);
				map.put("reviewType",Constants.OperationType.BLACK);
				map.put("employNum",userNum);
				riewerMapper.insertReview(map);
				//人员拉黑
				Person person = new Person();
				person.setId(bl.getPerId());
				person.setBlacklist("Y");
				personMapper.updateByPrimaryKeySelective(person);
				result.setCode(Result.SUCCESS);

			}
		}

		return result;
	}

	private void saveRiewer(Integer borroId, String reason, String employNum){
		//更新人工审核理由
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("borrId",borroId);
		map.put("reason",reason);
		map.put("employNum",employNum);
		riewerMapper.updateByBorrId(map);
	}


}