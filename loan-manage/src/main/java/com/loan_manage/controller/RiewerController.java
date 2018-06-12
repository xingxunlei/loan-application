package com.loan_manage.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.loan_entity.common.Constants;
import com.loan_manage.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.loan_manage.entity.Riewer;
import com.loan_manage.service.RiewerService;



@Controller
@RequestMapping("/Riewer")
public class RiewerController {

	@Autowired
	private RiewerService riewerService;

	// 商品查询
	@ResponseBody
	@RequestMapping("/selectRiewer")
	public String selectRiewer(HttpServletRequest request) throws Exception {
	Riewer riewer =	riewerService.selectByPrimaryKey(1);
		System.out.println(">>>>>>>>>>>>>>>>>"+riewer.getEmplloyeeName());
		
		return null;

	}

	// 人工审核通过
	@ResponseBody
	@RequestMapping("/pass")
	public Result pass(@RequestParam(required = true) Integer brroId,
					   @RequestParam(required = true) String reason,
					   @RequestParam(required = true) String userNum) throws Exception {
		Result result = riewerService.manuallyReview(brroId, reason, userNum, Constants.OperationType.PASS);
		return result;
	}

	// 人工审拒绝
	@ResponseBody
	@RequestMapping("/reject")
	public Result reject(@RequestParam(required = true) Integer brroId,
						 @RequestParam(required = true) String userNum,
						 @RequestParam(required = true) String reason) throws Exception {
		Result result = riewerService.manuallyReview(brroId, reason, userNum, Constants.OperationType.REJECT);

		return result;
	}

	// 人工审拉黑
	@ResponseBody
	@RequestMapping("/black")
	public Result black(@RequestParam(required = true) Integer brroId,
						 @RequestParam(required = true) String userNum,
						 @RequestParam(required = true) String reason) throws Exception {
		Result result = riewerService.manuallyReview(brroId, reason, userNum, Constants.OperationType.BLACK);

		return result;
	}
}
