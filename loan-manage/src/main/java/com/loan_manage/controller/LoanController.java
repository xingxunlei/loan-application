package com.loan_manage.controller;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.loan_entity.app.User;
import com.loan_entity.common.Constants;
import com.loan_entity.manager_vo.PhoneBookVo;
import com.loan_entity.manager_vo.ReviewVo;
import com.loan_manage.entity.DownloadOrder;
import com.loan_manage.entity.PolyXinLi;
import com.loan_manage.entity.Result;
import com.loan_manage.service.ExportConcurrentService;
import com.loan_manage.service.PersonService;
import com.loan_manage.utils.QueryParamUtils;
import com.loan_utils.util.DateUtil;
import com.loan_utils.util.ExcelUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.vo.NormalExcelConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.loan_entity.manager_vo.CardPicInfoVo;
import com.loan_entity.manager_vo.PrivateVo;
import com.loan_manage.service.LoanService;
import com.loan_manage.utils.Detect;

@Controller
public class LoanController {

	private static final Logger logger = org.apache.log4j.LogManager.getLogger(LoanController.class);
	
	@Autowired
	private LoanService loanService;
	@Autowired
	private PersonService personService;
	@Autowired
	private ExportConcurrentService exportConcurrentService;
	
	/**
	 * 列表分页
	 * @param request
	 */
	private void buildPage(HttpServletRequest request) {
		int offset =  Integer.valueOf(request.getParameter("skip") == null ? "0" : request.getParameter("skip"));
		int size = Integer.valueOf(request.getParameter("take") == null ? Integer.MAX_VALUE + "" : request.getParameter("take"));
		PageHelper.offsetPage(offset, size);
	}
	

	/**
	 * 悠米后台查看交易流水
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping(value = "/order",produces = "application/json")
	public Result getOrders(HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		Result result = new Result();
		int offset =  Integer.valueOf(request.getParameter("skip") == null ? "0" : request.getParameter("skip"));
		int size = Integer.valueOf(request.getParameter("take") == null ? Integer.MAX_VALUE + "" : request.getParameter("take"));
		buildPage(request);
		request.getParameterValues("type");
		List info = loanService.getOrders(request.getParameterMap(),offset,size);
		result.setCode(Result.SUCCESS);
		result.setMessage("加载成功");
		result.setObject(new PageInfo(info));
		return result;
	}


	@ResponseBody @RequestMapping(value = "/queryCostState",produces = "application/json")
	public Result queryCostState(String serialNo, HttpServletRequest request){
		Result result = new Result();
		try{
			request.getParameter("serialNo");
			result = loanService.queryCostState(serialNo);
		}catch (Exception e){
			e.printStackTrace();
			result.setCode(Result.FAIL);
			result.setMessage("查询回调失败");
		}
		return result;
	}

	@RequestMapping(value = "/downloadOrder")
	public void downloadOrder(HttpServletRequest request, HttpServletResponse response, String userNo,Integer count){
		try{
			if( Constants.DOWNLOAD_MAX_ITEMS <= count.intValue()){
				response.setContentType("text/html;charset=utf-8");
				response.getWriter().write("<!DOCTYPE html><head><script> if(confirm('一次性下数据量多于5W条!')){window.location.href = document.referrer;}else{window.location.href = document.referrer;} </script></head>");
				return;
			}
			if(!exportConcurrentService.getExportToken()){
				response.setContentType("text/html;charset=utf-8");
				response.getWriter().write("<!DOCTYPE html><head><script> if(confirm('下载人数过多,请稍后重试!')){window.location.href = document.referrer;}else{window.location.href = document.referrer;} </script></head>");
				return;
			}
			Map<String,Object> queryMap = QueryParamUtils.getargs(request.getParameterMap());
			List<DownloadOrder> orders = loanService.selectDownloadOrder(queryMap,userNo);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(NormalExcelConstants.FILE_NAME, "还款流水" + new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date()));
			map.put(NormalExcelConstants.CLASS, DownloadOrder.class);
			map.put(NormalExcelConstants.DATA_LIST, orders);
			map.put(NormalExcelConstants.PARAMS, new ExportParams());

			ExcelUtils.jeecgSingleExcel(map,request,response);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 悠米后台查看银行卡信息
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping(value = "/bank", method = RequestMethod.GET)
	public String getBanks(HttpServletRequest request, HttpServletResponse response,@RequestParam Integer perId) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		buildPage(request);
		List resutl = loanService.getBanks(perId);
		return JSON.toJSONString(new PageInfo(resutl));
	}
	
	/**
	 * 悠米后台查看催收备注信息
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping(value = "/memo", method = RequestMethod.GET)
	public String getMemos(HttpServletRequest request, HttpServletResponse response,@RequestParam Integer borrId) throws UnsupportedEncodingException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		buildPage(request);
		List resutl = loanService.getMemos(request.getParameterMap(), borrId);
		return JSON.toJSONString(new PageInfo(resutl));
	}
	
	/**
	 * 悠米后台查看合同
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/borrowList", method = RequestMethod.GET)
	public String getBorrow(HttpServletRequest request , HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		String borrId = request.getParameter("borrId");
		if(!Detect.notEmpty(borrId)){
			return "";
		}
		return JSON.toJSONString(loanService.getBorrowList(borrId));
	}
	
	/**
	 * 悠米后台查看基本信息
	 * 
	 * @param perId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/userInfo", method = RequestMethod.GET)
	public PrivateVo getUserInfo(@RequestParam int perId, HttpServletResponse response) {
		System.out.println(perId);
		return loanService.getUserInfo(perId);
	}
	
	/**
	 * 悠米后台查看基本信息
	 * 
	 * @param perId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/cardInfo", method = RequestMethod.GET)
	public CardPicInfoVo getCardInfo(@RequestParam int perId, HttpServletResponse response) {
		System.out.println(perId);
		return loanService.getCardInfo(perId);
	}
	
	/**
	 * 悠米后台查看使用优惠券
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/coupon", method = RequestMethod.GET)
	public List getCoupon(HttpServletRequest request , HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		String couponId = request.getParameter("couponId");
		String prodId = request.getParameter("prodId");
		if(!Detect.notEmpty(couponId)){
			return null;
		}
		List<Map<String, Object>> resutl = loanService.getPerCoupon(couponId, prodId);
		
		return resutl;
	}
	/**
	 * 聚信立信用报告列表
	 */
	@ResponseBody
	@RequestMapping(value = "polyXinli/credit", method = RequestMethod.GET)
	public String getjuxinliCredit(HttpServletRequest request , HttpServletResponse response
			, @RequestParam String idCard,@RequestParam String name, @RequestParam Integer perId){
		response.setHeader("Access-Control-Allow-Origin", "*");

		int offset =  Integer.valueOf(request.getParameter("skip") == null ? "0" : request.getParameter("skip"));
		int size = Integer.valueOf(request.getParameter("take") == null ? Integer.MAX_VALUE + "" : request.getParameter("take"));

		List result = loanService.getPolyXinliCredit(idCard, name, offset, size, perId);

		return JSON.toJSONString(new PageInfo(result));
		
	}
	
	/**
	 * 征信报告接口
	 */
	@ResponseBody
	@RequestMapping(value = "creditInvestigation", method = RequestMethod.GET)
	public String getCreditInvestigation(HttpServletRequest request , HttpServletResponse response
			, @RequestParam String idCard, @RequestParam String name){

		response.setHeader("Access-Control-Allow-Origin", "*");

		return loanService.getCreditInvestigation(idCard, name);
		
	}
	
	
	/**
	 * 手机通讯录联系人
	 */
	@ResponseBody
	@RequestMapping(value = "contact", method = RequestMethod.GET)
	public String getContact(HttpServletRequest request , HttpServletResponse response,
			@RequestParam Integer perId, @RequestParam String phones ){
		response.setHeader("Access-Control-Allow-Origin", "*");
		buildPage(request);
		int offset =  Integer.valueOf(request.getParameter("skip") == null ? "0" : request.getParameter("skip"));
		int size = Integer.valueOf(request.getParameter("take") == null ? Integer.MAX_VALUE + "" : request.getParameter("take"));
//		if(Detect.notEmpty(phones)){
			List result = loanService.getContact(perId, phones, offset, size);
			return JSON.toJSONString(new PageInfo(result));
//		}
//		return JSON.toJSONString(new PageInfo());
	}

	/**
	 * 个人通讯录导出
	 * @param request
	 * @param response
	 * @param perId
	 * @return
	 */
	@RequestMapping("/export/phonebook")
	public void exportPhoneBook(HttpServletRequest request, HttpServletResponse response,@RequestParam Integer perId){
	    try {
			List<PhoneBookVo> phoneBookVoList = loanService.getContactForExport(perId);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(NormalExcelConstants.FILE_NAME, personService.getNameByPersonId(perId)+"的通信录" + DateUtil.getDateStringToHHmmss(new Date()).replace(" ","-"));
			map.put(NormalExcelConstants.CLASS, PhoneBookVo.class);
			map.put(NormalExcelConstants.DATA_LIST, phoneBookVoList);
			map.put(NormalExcelConstants.PARAMS, new ExportParams());
			ExcelUtils.jeecgSingleExcel(map,request,response);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	/**
	 * 聚信立报告导出
	 * @param request
	 * @param response
	 * @param perId
	 * @return
	 */
	@RequestMapping("/export/polyXinliCredit")
	public void exportPolyXinliCredit(HttpServletRequest request, HttpServletResponse response,@RequestParam Integer perId,@RequestParam String idCard ,@RequestParam String name){
		try {
			List result = loanService.getPolyXinliCredit(idCard, name, 0, Integer.MAX_VALUE, perId);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put(NormalExcelConstants.FILE_NAME, personService.getNameByPersonId(perId)+((JSONObject)JSON.parse(personService.getPhone(perId))).get("phone")+"聚信立报告");
			map.put(NormalExcelConstants.CLASS, PolyXinLi.class);
			List dataList = ((Page)result).getResult();
			if(dataList == null){
				dataList = Collections.EMPTY_LIST;
			}
			map.put(NormalExcelConstants.DATA_LIST, dataList.get(0));
			map.put(NormalExcelConstants.PARAMS, new ExportParams());
			ExcelUtils.jeecgSingleExcel(map,request,response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 统计报告列表
	 */
	@ResponseBody
	@RequestMapping(value = "/workReport", method = RequestMethod.POST)
	public String getWorkReport(HttpServletRequest request,
			HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
//		buildPage(request);
		
		Map<String, Object> map = new HashMap<String, Object>();
		String bedueName = request.getParameter("bedueName");
		String levelType = request.getParameter("levelType");
		if (Detect.notEmpty(bedueName)) {
			map.put("bedueName", bedueName);
		}
		if (StringUtils.isNotBlank(levelType)&&StringUtils.isNumeric(levelType)) {
			map.put("levelType", levelType);
		}
		map.put("beginDate", request.getParameter("beginDate"));
		map.put("endDate", request.getParameter("endDate"));

		return JSON.toJSONString(new PageInfo(loanService.workReport(map)));
	}

    /**
	 * 统计报告导出
	 */
	@RequestMapping(value = "/workReport/export")
	public void exportWorkReport(HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		buildPage(request);
		Map<String, Object> map = new HashMap<String, Object>();

		String bedueName = request.getParameter("bedueName");
		if (Detect.notEmpty(bedueName)) {
			map.put("bedueName", bedueName);
		}
		map.put("beginDate", request.getParameter("beginDate"));
		map.put("endDate", request.getParameter("endDate"));

		loanService.exportWorkReport(map, request, response);
	}

	/**
	 * 更新单子表
	 */
	@RequestMapping(value = "/collectorsList/borrId")
	@ResponseBody
	public String collectorsList(HttpServletRequest request, HttpServletResponse response) {
		String borrId = request.getParameter("borrId");
		String status = request.getParameter("status");

		if(Detect.notEmpty(borrId)){
			if(!Detect.notEmpty(status)){
				status = "B";
			}
			loanService.saveCollectorsList(borrId, status);
			return Constants.SUCESS;
		}
		return Constants.FAIL;
	}

	/**
	 * 查询黑名单记录
	 * @param perId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/reviewVoBlackList", method = RequestMethod.POST)
	public  List<ReviewVo> getReviewVoBlackList(Integer perId){
		return loanService.getReviewVoBlackList(perId);
	}

	/**
	 * 查询黑名单记录
	 * @param user
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/indexTest", method = RequestMethod.GET)
	public String index(@RequestBody User user){
		return user.getId();
	}
}
