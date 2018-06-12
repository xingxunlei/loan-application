package com.loan_web.app;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.loan_api.app.MiscellService;
import com.loan_entity.app.YmFeedback;

@Controller
@RequestMapping("/miscell")
public class MiscellController {
	
//	MiscellService misService;
	
	 /**
	 * 消息列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getMessageByUserId")
	public String getMessageByUserId(HttpServletRequest request){
		String result = "";
		String userId = request.getParameter("userId");
		
//		result = misService.getMessageByUserId(userId);
		return result;
	}
	
	/**
	 * 获取我的历史借款记录
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getMyBorrowList")
	public String getMyBorrowList(HttpServletRequest request){
		String result = "";
		String userId = request.getParameter("userId");
		
//		result = misService.getMyBorrowList(userId);
		return result;
	}
	
	/**
	 * 常见问题
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/commonProblem")
	public String CommonProblem(HttpServletRequest request){
		String result = "";
		String userId = request.getParameter("userId");
		
//		result = misService.commonProblem(userId);
		return result;
	}
	
	/**
	 * 意见反馈
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/feedback")
	public String feedback(HttpServletRequest request){
		String result = "";
		String userId = request.getParameter("userId");
		String content = request.getParameter("content");
		YmFeedback feed = new YmFeedback();
		feed.setPer_id(Integer.parseInt(userId));
		feed.setContent(content);
//		result = misService.feedback(feed);
		return result;
	}
	
	/**
	 * 个人资料
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getPersonInfo")
	public String getPersonInfo(HttpServletRequest request){
		String result = "";
		String userId = request.getParameter("userId");
		
//		result = misService.getPersonInfo(userId);
		return result;
	}
	
	
}
