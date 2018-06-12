package com.loan_manage.controller;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loan_api.manager.ManageLoanService;
import com.loan_entity.manager.CodeValue;
import com.loan_entity.manager.Review;
import com.loan_manage.entity.AuditLoanDo;
import com.loan_manage.entity.AuditLoanVo;
import com.loan_manage.entity.ManuallyReview;
import com.loan_manage.entity.Result;
import com.loan_manage.service.AuditLoanService;
import com.loan_manage.service.CollectorsLevelService;
import com.loan_manage.service.RedisService;
import com.loan_manage.utils.Detect;
import com.loan_manage.utils.QueryParamUtils;
import com.loan_utils.util.DateUtil;
import com.loan_utils.util.ExcelUtils;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.vo.NormalExcelConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.loan_manage.service.ManageUserService;
import com.loan_manage.task.UserTask;
import com.loan_manage.web_service.WebServiceClient;



@Controller
@RequestMapping("/user")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	private void buildPage(HttpServletRequest request) {
		int offset =  Integer.valueOf(request.getParameter("skip") == null ? "0" : request.getParameter("skip"));
		int size = Integer.valueOf(request.getParameter("take") == null ? Integer.MAX_VALUE + "" : request.getParameter("take"));
		PageHelper.offsetPage(offset, size);
	}
	private WebServiceClient client = WebServiceClient.getInstance();
	@Resource
	private ManageUserService manageUserService;
	@Resource
	private UserTask userTask;
	@Resource
	private RedisService redisService;
	@Autowired
	private CollectorsLevelService collectorsLevelService;
	@Autowired
	private AuditLoanService auditLoanService;
	@ResponseBody
	@RequestMapping(value ="/userLogin", produces = "application/json", method = RequestMethod.POST )
	public Result userLogin(String loginname,String loginpassword,Integer source,String loginVerifyCode) {
		Result result = new Result();
		try {
			result = collectorsLevelService.loadLoginUser(loginname, loginpassword,source,loginVerifyCode);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(Result.FAIL);
			result.setMessage("账号校验失败，账户不存在.");
		}
		return result;
	}

	@ResponseBody
	@RequestMapping(value ="/userAuthInfo", produces = "application/json", method = RequestMethod.POST )
	public Result userAuthInfo(String userAuth) {
		Result result = new Result();
		try {
			result = collectorsLevelService.loadUserAuthInfo(userAuth);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(Result.FAIL);
			result.setMessage("账号校验失败，账户不存在.");
		}
		return result;
	}

	@ResponseBody
	@RequestMapping(value ="/userRoleInfo", produces = "application/json", method = RequestMethod.POST )
	public Result userRoleInfo(String userCategory) {
		Result result = new Result();
		try {
			result = collectorsLevelService.loadUserRoleInfo(userCategory);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(Result.FAIL);
			result.setMessage("账号校验失败，账户不存在.");
		}
		return result;
	}

	@ResponseBody
	@RequestMapping(value ="/checkLoginName",  method = RequestMethod.POST )
	public Result checkLoginName(String loginname) {
		Result result = new Result();
		try {
			result = collectorsLevelService.checkLoginName(loginname);
		} catch (Exception e) {
			result.setCode(Result.FAIL);
			result.setMessage("账号校验失败，账户不存在.");
		}
		return result;
	}
	@ResponseBody
	@RequestMapping(value ="/loadUserInfoById",  method = RequestMethod.POST )
	public String loadUserInfoById(String himid) {
		String result =client.getPersonServiceDao().findPersonForId("oa",
				"1q2w3e",  Integer.parseInt(himid));
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/getusers", method = RequestMethod.GET)
	public String getusers(HttpServletRequest request) {
		request.getParameterMap();
		buildPage(request);
		List resutl = manageUserService.getusers(request.getParameterMap());
		return JSON.toJSONString(new PageInfo(resutl));
	}

	@ResponseBody
	@RequestMapping(value = "/getaudits", method = RequestMethod.GET)
	public String getaudits(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		request.setCharacterEncoding("UTF-8");
		request.getParameterMap();
		buildPage(request);
		List resutl = manageUserService.getaudits(request.getParameterMap());
		return JSON.toJSONString(new PageInfo(resutl));
	}

	@ResponseBody
	@RequestMapping(value = "/loadAudits", method = RequestMethod.POST,produces = "application/json")
	public Result loadAudits(HttpServletRequest request) {
		Result result = new Result();
		try{
			int offset =  Integer.valueOf(request.getParameter("skip") == null ? "0" : request.getParameter("skip"));
			int size = Integer.valueOf(request.getParameter("take") == null ? Integer.MAX_VALUE + "" : request.getParameter("take"));
			String userNo = StringUtils.isEmpty(request.getParameter("userNo")) ? "SYSTEM" : request.getParameter("userNo");

			Map<String,Object> queryMap = QueryParamUtils.getargs(request.getParameterMap());

			PageInfo<Map<String,Object>> info = manageUserService.selectAudits(queryMap,offset,size,userNo);

			result.setCode(Result.SUCCESS);
			result.setMessage("加载成功");
			result.setObject(info);
		}catch (Exception e){
			e.printStackTrace();
			result.setCode(Result.FAIL);
			result.setMessage("加载失败");
		}
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/getauditsforUser", method = RequestMethod.GET)
	public String getauditsforUser(HttpServletRequest request) {
		request.getParameterMap();
		buildPage(request);
		List resutl = manageUserService.getauditsforUser(request.getParameterMap());
		return JSON.toJSONString(new PageInfo(resutl));
	}


	@ResponseBody
	@RequestMapping(value = "/getManuallyReview", method = RequestMethod.GET)
	public String getManuallyReview(HttpServletRequest request) {
		int offset =  Integer.valueOf(request.getParameter("skip") == null ? "0" : request.getParameter("skip"));
		int size = Integer.valueOf(request.getParameter("take") == null ? Integer.MAX_VALUE + "" : request.getParameter("take"));

		List resutl = manageUserService.getManuallyReview(request.getParameterMap(), offset, size,true);
		return JSON.toJSONString(new PageInfo(resutl));
	}

	@ResponseBody
	@RequestMapping(value = "/manualAuditReport", method = RequestMethod.POST)
	public String manualAuditReport(HttpServletRequest request) {
		Map<String, Object> map = QueryParamUtils.getargs(request.getParameterMap());
		String employName = request.getParameter("employName");
		if (Detect.notEmpty(employName)) {
			map.put("employName", employName);
		}
		Calendar now = Calendar.getInstance();

		map.put("endDate", Detect.notEmpty(request.getParameter("endDate"))
				? request.getParameter("endDate")
				:DateUtil.getDateString(now.getTime()));

		//日期减一天
		now.set(Calendar.DATE, now.get(Calendar.DATE) - 1);
		map.put("beginDate", Detect.notEmpty(request.getParameter("beginDate"))
				? request.getParameter("beginDate")
				:DateUtil.getDateString(now.getTime()));

		return JSON.toJSONString(new PageInfo(manageUserService.manualAuditReport(map)));
	}

	@ResponseBody
	@RequestMapping(value = "/riewer", method = RequestMethod.GET)
	public String getRiewer() {
		return JSON.toJSONString(manageUserService.getRiewer());
	}

	/**
	 * 导出人工审核结果
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/exportManuallyReview")
	public void exportPhoneBook(HttpServletRequest request, HttpServletResponse response){
		try {
			List results = manageUserService.getManuallyReview(request.getParameterMap(), 0, 0,false);
			//字典值转换
			setBorrStauts(results);

			Map<String, Object> map = new HashMap<>();
			map.put(NormalExcelConstants.FILE_NAME, "人工审核结果" + DateUtil.getDateString(new Date()));
			map.put(NormalExcelConstants.CLASS, ManuallyReview.class);
			map.put(NormalExcelConstants.DATA_LIST, JSONObject.parseArray(JSON.toJSONString(results), ManuallyReview.class));
			map.put(NormalExcelConstants.PARAMS, new ExportParams());


			ExcelUtils.jeecgSingleExcel(map,request,response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *  审核放款报表
	 * @param vo 条件
	 * @return
     */
	@RequestMapping(value = "/auditLoan",method = RequestMethod.POST)
	@ResponseBody
	public String getAuditLoan(HttpServletRequest request,AuditLoanVo vo){
		logger.info("-------审核放款报表接受审核条件为"+vo);
		Map<String,Object> queryMap = QueryParamUtils.getargs(request.getParameterMap());
		if (StringUtils.isEmpty(vo.getBeginDate()) && StringUtils.isEmpty(vo.getEndDate())){
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MONTH, 0);
			c.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
			vo.setBeginDate(new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()));
		}
		queryMap.put("vo",vo);
		List<AuditLoanDo> ado = auditLoanService.getAuditLoanAll(queryMap);
		return JSON.toJSONString(new PageInfo(ado));
	}

	/**
	 * 转换类型
	 * @param results
	 */
	public void setBorrStauts(List results){
		List codeList = manageUserService.getSource("borr_status");

		if(Detect.notEmpty(results) && Detect.notEmpty(codeList)){
			Map codeMap = new HashMap();
			for(int i=0;i<codeList.size();i++){
				Map result = (Map)codeList.get(i);
				codeMap.put(result.get("CODE"), result.get("VALUE"));
			}

			for(int i = 0 ; i < results.size(); i++){
				Map result = (Map)results.get(i);
				Object value = codeMap.get(result.get("up_status"));
				if(value==null){
					value = "其它";
				}
				result.put("up_status", value);

				Object borrStatusValue = codeMap.get(result.get("borr_status"));
				if(borrStatusValue==null){
					borrStatusValue = "其它";
				}
				result.put("borr_status", borrStatusValue);

				//日期格式转换
				if(result.get("createDate") != null){
					result.put("createDate", DateUtil.getDateStringToHHmmss((Date) result.get("createDate")));
				}
				if(result.get("auditTime") != null){
					result.put("auditTime", DateUtil.getDateStringToHHmmss((Date) result.get("auditTime")));
				}
			}
		}
	}
	
	/*@ResponseBody
	@RequestMapping(value = "/sync", method = RequestMethod.GET)
	public String syncUser(HttpServletRequest request) {
		userTask.syncUser();
		return "同步成功";
	}*/

	@ResponseBody
	@RequestMapping(value = "/source", method = RequestMethod.GET)
	public Result source(@RequestParam String code) {
		Result result = new Result();
		List userSource = manageUserService.getSource(code);

		result.setCode(Result.SUCCESS);
		result.setMessage("加载成功");
		result.setObject(userSource);
		return result;
	}

}
