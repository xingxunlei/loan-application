package com.loan_web.app;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import com.loan_api.loan.DelayQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.loan_api.app.UserService;
import com.loan_api.loan.YsbCollectionService;
import com.loan_api.loan.YsbpayService;
import com.loan_entity.app.NoteResult;
import com.loan_entity.utils.Callback;
import com.loan_utils.util.CodeReturn;
import com.loan_utils.util.VerifyMD5;

@Controller
@RequestMapping("/callback")
public class CallbackController {

	@Autowired
	private YsbCollectionService ysbCollectionService;

	@Autowired
	private YsbpayService ysbpayService;

	@Autowired
	private UserService userService;

	@Autowired
	private DelayQueueService delayQueueService;

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory
			.getLogger(CallbackController.class);

	/**
	 * 第三方回调接口
	 * 
	 * @param
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/collect")
	public String collectCallback(HttpServletRequest request) {
		logger.info("===============收到第三方通知代收结果============");
		Callback callback = new Callback();
		try {
			callback.setResult_code(request.getParameter("result_code"));
			callback.setResult_msg(request.getParameter("result_msg"));
			callback.setAmount(request.getParameter("amount"));
			callback.setOrderId(request.getParameter("orderId"));
			callback.setMac(request.getParameter("mac"));
			String desc = request.getParameter("desc");
			if(desc != null && !"".equals(desc)){
			    callback.setDesc(desc);
			}
			logger.info("第三方代扣回调结果：" + JSONObject.toJSONString(callback));
			NoteResult result = ysbCollectionService.collectCallback(callback);
			return JSONObject.toJSONString(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "201";
		}




	}

	/**
	 * 还款认证支付接口
	 *
	 *            用户ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/repayment")
	public String repayment(HttpServletRequest request) {
		String result = "";
		String per_id = request.getParameter("per_id");
		String amount = request.getParameter("amount");
		String conctact_id = request.getParameter("conctact_id");
		String bank_id = request.getParameter("bank_id");
		String cardNo = request.getParameter("cardNo");
		String tokenKey = request.getParameter("tokenKey");
		String token = request.getParameter("token");

		// md5加密防串改
		String md5sign = request.getParameter("md5sign");
		boolean aa = VerifyMD5.Sign(md5sign, per_id, amount,conctact_id,bank_id,cardNo,tokenKey,token);
		if (!aa) {
			JSONObject obj = new JSONObject();
			obj.put("code", CodeReturn.MD5_WRONG);
			obj.put("info", "防串改不通过！");
			obj.put("data", "");
			return obj.toString();
		}

		userService.updatePasswordCanbaiqishi(tokenKey, "repayment", per_id);
		result = ysbpayService.ysbPayment(per_id, amount, conctact_id, bank_id,
				cardNo, token);
		return result;
	}

	/**
	 * 还款认证支付回调后台地址
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/callbackBackground")
	public String callbackBackground(HttpServletRequest request) {
		String result = "";
		String result_code = request.getParameter("result_code");
		String result_msg = request.getParameter("result_msg");
		String amount = request.getParameter("amount");
		String orderId = request.getParameter("orderId");
		String mac = request.getParameter("mac");
		try {
			result = ysbpayService.callbackBackground(result_code, result_msg,
					amount, orderId, mac);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * .net流水处理完回调方法
	 * 
	 * @param request
	 *            用户ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/callbackBackgroundByNet")
	public String callbackBackgroundByNet(HttpServletRequest request) {
		String result = "";
		// 接受.net的参数
		StringBuffer info = new StringBuffer();
		InputStream is = null;
		String xing = "";
		try {
			is = request.getInputStream();
			BufferedInputStream buf = new BufferedInputStream(is);
			byte[] buffer = new byte[1024];
			int iRed;
			while ((iRed = buf.read(buffer)) != -1) {
				info.append(new String(buffer, 0, iRed, "UTF-8"));
			}

			xing = URLDecoder.decode(info.toString(), "UTF-8");
            // System.out.println("zzzzz====" + xing);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
        // System.out.println(xing);
		// String orderJson = request.getParameter("orderJson");
		try {
			result = ysbpayService.callbackBackgroundByNet(xing);
		} catch (Exception e) {
			e.printStackTrace();
			result = "201";
		}
		return result;
	}

	/**
	 * 实时代付接口(放款)
	 * 
	 * @param per_id
	 *            用户ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/payCont")
	public String payCont(HttpServletRequest request, String per_id,
			String borrId) {
		String result = "";
		try {
			// md5加密防串改
			String md5sign = request.getParameter("md5sign");
			boolean aa = VerifyMD5.Sign(md5sign, per_id, borrId);
			if (!aa) {
				JSONObject obj = new JSONObject();
				obj.put("code", CodeReturn.MD5_WRONG);
				obj.put("info", "防串改不通过！");
				obj.put("data", "");
				return obj.toString();
			}
			result = ysbpayService.payCont(per_id, borrId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 实时代付回调接口(放款)
	 * 
	 * @param request
	 * @param result_code
	 * @param result_msg
	 * @param amount
	 * @param orderId
	 * @param mac
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/payContCallBack")
	public String payContCallBack(HttpServletRequest request,
			String result_code, String result_msg, String amount,
			String orderId, String mac) {
		String result = "";
		try {
			result = ysbpayService.payContCallBack(result_code, result_msg,
					amount, orderId, mac);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@ResponseBody
	@RequestMapping("/testCallback")
	public String testCallback( String orderId) {
		return ysbCollectionService.testCallback(orderId);
	}

	@ResponseBody
	@RequestMapping("/testDelay")
	public String testCallback( String orderId,String type,String times) {
		delayQueueService.addToDeplayQueue(orderId,Integer.valueOf(type),Integer.valueOf(times));
		return "1";
	}
}
