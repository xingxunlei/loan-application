package com.loan_manage.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.loan_entity.common.Constants;
import com.loan_manage.entity.Riewer;
import com.loan_manage.service.CollectorsLevelService;
import com.loan_manage.service.RedisService;
import com.loan_manage.utils.Assertion;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loan_manage.mapper.RiewerMapper;
import com.loan_manage.service.ManageUserService;
import com.loan_manage.utils.Detect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManageUserServiceImpl implements ManageUserService {
	@Resource
	RiewerMapper riewerMapper;
	@Autowired
	private RedisService redisService;

	@Autowired
	private CollectorsLevelService userLevelService;

	@Override
	public List  getusers(Map<String, String[]> args) {
		return riewerMapper.getLoanUsersNew(getargs(args));
	}

	private Map getargs(Map<String, String[]> args) {
		Iterator<String> keys = args.keySet().iterator();
		Map arg = new HashMap();
		while (keys.hasNext()) {
			String key = keys.next();
			// System.out.println(args.get(key)[0]);
			if ("filter".equals(key)) {
				String[] filter = args.get(key);
				if (filter.length > 0 && StringUtils.isNotEmpty(filter[0])) {
					String st = filter[0];
					JSONArray js = JSON.parseArray(st);
					for (int i = 0; i < js.size(); i++) {
						if (!"and".equals(js.get(i).toString())) {
							if (js.get(i) instanceof JSONArray) {
								JSONArray jss = JSON.parseArray(js.get(i)
										.toString());
								if (jss.get(0) instanceof JSONArray) {
									JSONArray jsdate = (JSONArray) jss;
									for (int j = 0; j < jsdate.size(); j++) {
										setDate(arg, jsdate.get(j));
									}
								} else {
									Object o = jss.get(2);
									if (o instanceof JSONObject) {
										arg.put(jss.get(0),
												((JSONObject) o).get("value"));
									} else {
										setDate(arg, jss);
										arg.put(jss.get(0), jss.get(2));
									}
								}

							} else {
								Object o = js.get(2);
								if (o instanceof JSONObject) {
									arg.put(js.get(0),
											((JSONObject) o).get("value"));
								} else {
									arg.put(js.get(0), js.get(2));
								}
								break;
							}

						}
					}

				}
			} else if ("sort".equals(key)) {
				String[] sort = args.get(key);
				if (sort.length > 0 && StringUtils.isNotEmpty(sort[0])) {
					JSONObject jo = JSON.parseArray(sort[0]).getJSONObject(0);
					arg.put("selector", jo.get("selector"));
					arg.put("desc", jo.get("desc"));
				}
			}

		}
		return arg;
	}

	private void setDate(Map<String, Object> arg, Object js) {

		if (js instanceof JSONArray) {
			JSONArray jss = (JSONArray) js;
			if (jss.get(1).toString().indexOf(">") > -1) {
				arg.put(jss.getString(0) + "_start", jss.getString(2));
			} else if (jss.get(1).toString().indexOf("<") > -1) {
				arg.put(jss.getString(0) + "_end", jss.getString(2));
			}
		} else {

		}

	}
	@Override
	public List getaudits(Map<String, String[]> parameterMap) {
		Map sss = getargs(parameterMap);


		return riewerMapper.getaudits(sss);
	}

	@Override
	public PageInfo<Map<String,Object>> selectAudits(Map<String,Object> queryParams, Integer start, Integer size, String userNo) {
		Long totalCount = getTotalCount(queryParams, start, userNo);

		PageHelper.offsetPage(start,size,false);

		List<Map<String,Object>> audits = riewerMapper.selectAudits(queryParams);

		PageInfo<Map<String,Object>> info = new PageInfo<>(audits);

		info.setTotal(totalCount);

		if(info.getTotal() == 0){
			totalCount = riewerMapper.selectAuditsCount(queryParams);
			info.setTotal(totalCount);
		}

		return info;
	}


	private Long getTotalCount(Map<String, Object> queryParams, Integer start, String userNo) {
		Long totalCount = null;
		if(start == 0){//查询条件发生变化,从库中取出总条数
			if(queryParams.isEmpty()){//查询条件为空,直接从redis里取数据
				totalCount = redisService.selectQueryTotalItem(Constants.TYPE_AUTH_INFO,"total_item");
			}else{//先从redis里取，取不到再从数据库中取，并将值设置入redis
				totalCount = redisService.selectQueryTotalItem(Constants.TYPE_AUTH_INFO,userNo);
				if(totalCount == null  || totalCount == 0){
					totalCount = riewerMapper.selectAuditsCount(queryParams);
					redisService.saveQueryTotalItem(Constants.TYPE_AUTH_INFO,userNo,totalCount);
				}
			}
		}else{//从redis中取出总条数，取不到再从数据库中取，并将值设置入redis
			totalCount = redisService.selectQueryTotalItem(Constants.TYPE_AUTH_INFO,userNo);
			if(totalCount == null  || totalCount == 0){
				totalCount = riewerMapper.selectAuditsCount(queryParams);
				redisService.saveQueryTotalItem(Constants.TYPE_AUTH_INFO,userNo,totalCount);
			}
		}
		return totalCount;
	}

	@Override
	public List getauditsforUser(Map<String, String[]> parameterMap) {
		if (parameterMap.get("employ_num") == null
				|| StringUtils.isEmpty(parameterMap.get("employ_num")[0])) {
			return new ArrayList();
		}
		Map<String, Object> sss = getargs(parameterMap);
		sss.put("employ_num", parameterMap.get("employ_num")[0]);
		if(Detect.notEmpty(parameterMap.get("isManual"))){
			sss.put("isManual", parameterMap.get("isManual")[0]);
		}
//		sss.put("selector", "desc");
		return riewerMapper.getauditsforUser(sss);
	}

	@Override
	public List getManuallyReview(Map<String, String[]> parameterMap, int offset, int size, boolean count) {

		Map<String, Object> sss = getargs(parameterMap);
		if(Detect.notEmpty(parameterMap.get("employ_num")) && Detect.notEmpty(parameterMap.get("employ_num")[0])){
			if(!userLevelService.isManager(parameterMap.get("employ_num")[0])) {
				sss.put("employ_num", parameterMap.get("employ_num")[0]);
			}
		}
		if(Detect.notEmpty(parameterMap.get("meaning")) && Detect.notEmpty(parameterMap.get("meaning")[0])){
			sss.put("meaning", parameterMap.get("meaning")[0]);
		}
//		sss.put("selector", "desc");
		if(count){
			PageHelper.offsetPage(offset, size);
		}
		return riewerMapper.getManuallyReview(sss);
	}

	@Override
	public List getSource(String code) {
		Assertion.notEmpty(code, "code不能为空");

		return riewerMapper.getRegisterSource(code);
	}


	@Override
	public List manualAuditReport(Map map) {
		List manualAuditReport = riewerMapper.manualAuditReport(map);
		sumManualAuditReportCount(manualAuditReport);

		return manualAuditReport;
	}

	/**
	 * 人工审核报表合计
	 * @param manualAuditReport
	 */
	private void sumManualAuditReportCount(List<Map<String, Object>> manualAuditReport){

		if(Detect.notEmpty(manualAuditReport) &&
				!manualAuditReport.get(manualAuditReport.size() - 1).get("employName").equals("合计")){
			Map<String, Object> count = new HashMap();
			for (Map<String, Object> map : manualAuditReport){

				count.put("cancelNum", (Long) map.get("cancelNum") +
						Long.valueOf(count.get("cancelNum") == null ? "0" : count.get("cancelNum") + "") );

				count.put("rejectNum", (Long) map.get("rejectNum") +
						Long.valueOf(count.get("rejectNum") == null ? "0" : count.get("rejectNum") + ""));

				count.put("passNum", (Long) map.get("passNum") +
						Long.valueOf(count.get("passNum") == null ? "0" : count.get("passNum") + ""));

				count.put("sumNum", (Long) map.get("sumNum") +
						Long.valueOf(count.get("sumNum") == null ? "0" : count.get("sumNum") + ""));

				count.put("passRate", Double.valueOf(map.get("passRate") + "") +
						Double.valueOf(count.get("passRate") == null ? "0" : count.get("passRate") + ""));

//				map.put("passRate",  map.get("passRate") + "%");
			}
			DecimalFormat df = new DecimalFormat("######0.00");
			count.put("passRate", df.format(Double.valueOf(count.get("passRate") + "") /
					(Double.valueOf(count.get("passRate") + "") + Double.valueOf(count.get("rejectNum") + "")) * 100));
			count.put("employName", "合计");
			manualAuditReport.add(manualAuditReport.size() ,count);
		}
	}

	@Override
	public List getRiewer(){
		return riewerMapper.select(new Riewer());
	}

}
