package com.loan_web.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import com.loan_api.app.TimerService;
import com.loan_api.app.UserApi;
import com.loan_api.app.UserService;
import com.loan_api.loan.YsbpayService;
import com.loan_entity.app.NoteResult;
import com.loan_entity.app.Person;
import com.loan_entity.app.User;
import com.loan_entity.manager.Feedback;
import com.loan_utils.util.*;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.loan_api.app.TimerService;
import com.loan_api.app.UserApi;
import com.loan_api.app.UserService;
import com.loan_api.loan.YsbpayService;
import com.loan_entity.app.Person;
import com.loan_entity.app.User;
import com.loan_entity.manager.Feedback;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserApi userApi;

    @Autowired
    private UserService userService;

    @Autowired
    private YsbpayService ysbpayService;

    @Autowired
    private TimerService timerService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @ResponseBody
    @RequestMapping("/goIndex")
    public String goIndex() {
        User user = userApi.findUserById(1);
        // System.out.println(">>>>> USER API =" + user.getId()
        // + user.getNickyName());
        return "index" + user.getId() + user.getNickyName();
    }

    /**
     * 登录接口
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/userLogin")
    public String userLogin(HttpServletRequest request) {

        String result = "";
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String tokenKey = request.getParameter("tokenKey");
        String md5sign = request.getParameter("md5sign");
        boolean aa = VerifyMD5.Sign(md5sign, phone, password, tokenKey);
        if (!aa) {
            JSONObject obj = new JSONObject();
            obj.put("code", CodeReturn.MD5_WRONG);
            obj.put("info", "防串改不通过！");
            return obj.toString();
        }
        password = MD5Util.encodeToMd5(password);
//-------------------------------------白骑士------------------------------------------------

        String res = userService.canbaiqishi(tokenKey, "login", phone);

//-------------------------------------以上是白骑士白骑士----------------------------------------
//		String res = "0000";
//-------------------------------------以上是屏蔽白骑士-----------------------------------------
        if ("0000".equals(res)) {
            result = userService.userLogin(phone, password);
            return result.toString();
        } else {
//			JSONObject obj = new JSONObject();
//			jj.put("per_id", "");
//			jj.put("token", "");
//			obj.put("code", CodeReturn.baiqishi);
//			obj.put("info", "白骑士不通过！");
//			obj.put("data", jj);
//			return obj.toString();
            return CodeReturn.getBaiqishiResult(res);
        }
    }

    /**
     * 注册接口
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/userRegister")
    public String userRegister(HttpServletRequest request) {
        String result = "";
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String md5sign = request.getParameter("md5sign");
        boolean aa = VerifyMD5.Sign(md5sign, phone, password);
        if (!aa) {
            JSONObject obj = new JSONObject();
            obj.put("code", CodeReturn.MD5_WRONG);
            obj.put("info", "防串改不通过！");
            return obj.toString();
        }
        password = MD5Util.encodeToMd5(password);
        Person user = new Person();
        user.setPhone(phone);
        user.setPassword(password);
        user.setIsLogin(1);
        user.setSource("1");
        result = userService.userRegister(user);
        return result;
    }

    /**
     * 发送验证码
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/getCode")
    public String getCode(HttpServletRequest request) {
        JSONObject obj = new JSONObject();
        String result = "";
        String phone = request.getParameter("phone");
        String event = request.getParameter("event");
        String tokenKey = request.getParameter("tokenKey");

        String md5sign = request.getParameter("md5sign");
        boolean md = VerifyMD5.Sign(md5sign, phone, event, tokenKey);
        if (!md) {
            obj.put("code", CodeReturn.MD5_WRONG);
            obj.put("info", "防串改不通过！");
            return obj.toString();
        }

        // -----------------------------白骑士-----------------------------------------
        if ("register".equals(event)) {
            String res = userService.canbaiqishi(tokenKey, "register", phone);
            if ("0000".equals(res)) {
                res = userService.canbaiqishi(tokenKey, "sendDynamic", phone);
                if (!"0000".equals(res)) {
//					obj.put("code", CodeReturn.baiqishi);
//					obj.put("info", "白骑士不通过！");
//					obj.put("data", "");
//					return obj.toString();
                    return CodeReturn.getBaiqishiResult(res);
                }
            } else {
//				obj.put("code", CodeReturn.baiqishi);
//				obj.put("info", "白骑士不通过！");
//				obj.put("data", "");
//				return obj.toString();
                return CodeReturn.getBaiqishiResult(res);
            }
        } else if ("sendDynamic".equals(event)) {
            String res = userService
                    .canbaiqishi(tokenKey, "sendDynamic", phone);
            if (!"0000".equals(res)) {
//				obj.put("code", CodeReturn.baiqishi);
//				obj.put("info", "白骑士不通过！");
//				obj.put("data", "");
                return CodeReturn.getBaiqishiResult(res);
            }
        }
        // --------------------------------白骑士--------------------------------
        // 生成6位数的验证码
        Random random = new Random();

        String radomInt = "";

        for (int i = 0; i < 6; i++) {

            radomInt += random.nextInt(10);

        }
        logger.info("生成的验证码==" + radomInt);

        // 新悠米的短信接口，不需要加标题模版
        // boolean re = smsService.send("【悠米闪借】您的验证码是:"+radomInt, phone);
        // if(re){
        // obj.put("code", CodeReturn.success);
        // obj.put("info", "发送成功");
        // obj.put("data", radomInt);
        // }else{
        // obj.put("code", CodeReturn.fail);
        // obj.put("info", "发送失败");
        // obj.put("data", "");
        // }

        // 老悠米的短信接口，要加标题模版
        // 2017.4.19更新 短信send第三个参数 0-悠兔 ，1-悠米，2-吾老板
        String remessage = SmsUtil.sendSms(SmsUtil.MGYZM_CODE, radomInt, phone);
        if (EmaySmsUtil.send(remessage, phone, 1)) {
            logger.info("验证码发送成功！");
            obj.put("code", CodeReturn.success);
            obj.put("info", "发送成功");
            obj.put("data", radomInt);
        } else {
            logger.info("验证码发送失败！");
            obj.put("code", CodeReturn.fail);
            obj.put("info", "发送失败");
            obj.put("data", "");
        }
        result = obj.toString();
        return result;
    }


    /**
     * 描述：
     *
     * @param request
     * @return
     * @author: wanyan
     * @date： 日期：2017年2月22日 时间：下午2:55:24
     * @version 1.0
     */
    @ResponseBody
    @RequestMapping(value = "/getCodeForSourceNew", method = RequestMethod.POST)
    public String getCodeForSourceNew(String phone, String tokenKey, HttpServletRequest request) {
        //打印请求参数
        logger.error("phone" + phone + "tokenKey" + tokenKey);
//		logger.error("getCodeForSource被调用，客户端系统版本："+request.getHeader("User-Agent")+",IP:"+request.getRemoteAddr()+",主机名："+request.getRemoteHost()+",端口"+request.getRemotePort());
        JSONObject obj = new JSONObject();
        String result = "";
        String result_user = userService.getPersonByPhone(phone);
        // System.out.println("dd+="+result_user);
        com.alibaba.fastjson.JSONObject phone_obj = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(result_user);
        if (phone_obj.getInteger("code") != 200) {
            obj.put("code", 999);
            obj.put("info", "该手机号已经注册!");
            obj.put("data", "");
        } else {

            String baiqishi = userService.canbaiqishi(tokenKey, "sendDynamic", phone);
            if (!"0000".equals(baiqishi)) {
                logger.error("白骑士没有通过！");
                obj.put("code", "9999");
                obj.put("info", "系统安全问题");
                obj.put("data", "");
                return obj.toString();
            }
            logger.error("白骑士通过！");
            // 生成6位数的验证码
            Random random = new Random();
            String radomInt = "";
            for (int i = 0; i < 6; i++) {
                radomInt += random.nextInt(10);
            }
            logger.info("生成的验证码==" + radomInt);
            // 新悠米的短信接口，不需要加标题模版
            // boolean re = smsService.send("【悠米闪借】您的验证码是:"+radomInt, phone);
            // if(re){
            // obj.put("code", CodeReturn.success);
            // obj.put("info", "发送成功");
            // obj.put("data", radomInt);
            // }else{
            // obj.put("code", CodeReturn.fail);
            // obj.put("info", "发送失败");
            // obj.put("data", "");
            // }

            // 老悠米的短信接口，要加标题模版
            // 2017.4.19更新 短信send第三个参数 0-悠兔 ，1-悠米，2-吾老板
            String remessage = SmsUtil.sendSms(SmsUtil.MGYZM_CODE, radomInt, phone);
            if (EmaySmsUtil.send(remessage, phone, 1)) {
				logger.error("验证码发送成功！");
				obj.put("code", CodeReturn.success);
				obj.put("info", "发送成功!");
				obj.put("data", radomInt);
			} else {
				logger.error("验证码发送失败！");
				obj.put("code", CodeReturn.fail);
				obj.put("info", "发送失败!");
				obj.put("data", "");
			}
		}
		
		result = obj.toString();
		return result;
	}


    @RequestMapping(value = "/getCodeForSource", method = RequestMethod.POST)
    public String getCodeForSource(String phone, String tokenKey, HttpServletRequest request) {

        return "redirect: http://www.baidu.com";
    }
	/**
	 * 
	*描述：
	*@author: wanyan
	*@date： 日期：2017年2月22日 时间：下午3:33:59
	*@return
	*@version 1.0
	 */
	@ResponseBody
	@RequestMapping(value = "/userRegisterForSoruse", method = RequestMethod.POST)
	public String userRegisterForSoruse(String phone,String password, String source) {

		if (StringUtils.isEmpty(source)){
			JSONObject obj = new JSONObject();
			obj.put("code", CodeReturn.fail);
			obj.put("info", "没有渠道信息");
			return com.alibaba.fastjson.JSONObject.toJSONString(obj);
		}

	    logger.info("phone"+phone+"password"+password+"source"+source);
	    if(source.contains("&")){
	        int position = source.indexOf("&");
	        source = source.substring(0,position);	        
	    }
	    if(source.contains("?")){
	        int position2 = source.indexOf("?");
	        source = source.substring(0,position2);
	    }
		String result = "";
		password = MD5Util.encodeToMd5(password);
		
		Person user = new Person();
		user.setPhone(phone);
		user.setPassword(password);
		user.setIsLogin(1);
		if ("x".equals(source.substring(0, 1)) ) {
			user.setSource(source);
		}else{
			user.setInviter(Integer.parseInt(source));
			user.setSource("2");
		}
		result = userService.userRegister(user);
		return result;
	}
	/**
	 * 
	*描述：
	*@author: wanyan
	*@date： 日期：2017年2月22日 时间：下午3:33:59
	*@return
	*@version 1.0
	 */
	@ResponseBody
	@RequestMapping(value = "/userInviceCoupon", method = RequestMethod.POST)
	public String userInviceCoupon(int per_id) {
		String result = "";
		String couponName = "分享立减3元券";
		result = userService.userInviceCoupon(per_id, couponName);
		return result;
	}
	/**
	 * 找回密码
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updatePassword")
	public String updatePassword(HttpServletRequest request) {
		String result = "";
		String phone = request.getParameter("phone");
		String password = request.getParameter("password");

		String md5sign = request.getParameter("md5sign");
		boolean aa = VerifyMD5.Sign(md5sign, phone, password);
		if (!aa) {
			JSONObject obj = new JSONObject();
			obj.put("code", CodeReturn.MD5_WRONG);
			obj.put("info", "防串改不通过！");
			return obj.toString();
		}

		password = MD5Util.encodeToMd5(password);
		Person user = new Person();
		user.setPhone(phone);
		user.setPassword(password);
		result = userService.updatePassword(user);
		return result;
	}

	/**
	 * 查询个人资料
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getPersonInfo")
	public String getPersonInfo(HttpServletRequest request) {
		String result = "";
		String userId = request.getParameter("per_id");

		JSONObject obj = new JSONObject();
		String token = request.getParameter("token");
		// md5加密防串改
		String md5sign = request.getParameter("md5sign");
		boolean aa = VerifyMD5.Sign(md5sign, userId, token);
		if (!aa) {
			obj.put("code", CodeReturn.MD5_WRONG);
			obj.put("info", "防串改不通过！");
			return obj.toString();
		}

		int yan = userService.yanzhengtoken(userId, token);
		if (201 == yan) {
			obj.put("code", CodeReturn.TOKEN_WRONG);
			obj.put("info", "该帐号已在别的设备登录，请重新登录");
			result = obj.toString();
			return result;
		}

		result = userService.getPersonInfo(userId);
		return result;
	}

	/**
	 * 查询手机号有没有注册过
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getPersonByPhone")
	public String getPersonByPhone(HttpServletRequest request) {
		String result = "";
		String phone = request.getParameter("phone");

		// md5加密防串改
		String md5sign = request.getParameter("md5sign");
		boolean aa = VerifyMD5.Sign(md5sign, phone);
		if (!aa) {
			JSONObject obj = new JSONObject();
			obj.put("code", CodeReturn.MD5_WRONG);
			obj.put("info", "防串改不通过！");
			return obj.toString();
		}

		result = userService.getPersonByPhone(phone);
		return result;
	}

	/**
	 * 修改密码
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/personUpdatePassword")
	public String personUpdatePassword(HttpServletRequest request) {
		JSONObject obj = new JSONObject();
		String result = "";
		String userId = request.getParameter("per_id");
		String oldPassword = request.getParameter("oldPassword");
		String newPassword = request.getParameter("newPassword");
		String tokenKey = request.getParameter("tokenKey");

		String token = request.getParameter("token");

		// md5加密防串改
		String md5sign = request.getParameter("md5sign");
		boolean aa = VerifyMD5.Sign(md5sign, userId, oldPassword, newPassword,
				tokenKey, token);
		if (!aa) {
			obj.put("code", CodeReturn.MD5_WRONG);
			obj.put("info", "防串改不通过！");
			return obj.toString();
		}

		int yan = userService.yanzhengtoken(userId, token);
		if (201 == yan) {
			obj.put("code", CodeReturn.TOKEN_WRONG);
			obj.put("info", "该帐号已在别的设备登录，请重新登录");
			result = obj.toString();
			return result;
		}
//-------------------------------------白骑士------------------------------------------------
        String res = userService.updatePasswordCanbaiqishi(tokenKey, "modify",
                userId);
//-----------------------------------以上是白骑士----------------------------------------------
//		String res = "0000";
//-----------------------------------以上屏蔽是白骑士----------------------------------------------
        if (!"0000".equals(res)) {
//			obj.put("code", CodeReturn.baiqishi);
//			obj.put("info", "白骑士不通过！");
//			obj.put("data", "");
//			result = obj.toString();
            result = CodeReturn.getBaiqishiResult(res);
        } else {
            oldPassword = MD5Util.encodeToMd5(oldPassword);
            newPassword = MD5Util.encodeToMd5(newPassword);
            Person per = new Person();
            per.setId(Integer.parseInt(userId));
            per.setPassword(newPassword);
            per.setOldPassword(oldPassword);
            result = userService.personUpdatePassword(per);
        }
        return result;
    }

    /**
     * 意见反馈
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/userFeedBack")
    public String userFeedBack(HttpServletRequest request) {
        String result = "";
        String userId = request.getParameter("per_id");
        String content = request.getParameter("content");

        JSONObject obj = new JSONObject();
        String token = request.getParameter("token");
        // md5加密防串改
        String md5sign = request.getParameter("md5sign");
        boolean aa = VerifyMD5.Sign(md5sign, userId, content, token);
        if (!aa) {
            obj.put("code", CodeReturn.MD5_WRONG);
            obj.put("info", "防串改不通过！");
            return obj.toString();
        }
        int yan = userService.yanzhengtoken(userId, token);
        if (201 == yan) {
            obj.put("code", CodeReturn.TOKEN_WRONG);
            obj.put("info", "该帐号已在别的设备登录，请重新登录");
            result = obj.toString();
            return result;
        }

        Feedback feed = new Feedback();
        feed.setPerId(Integer.parseInt(userId));
        feed.setContent(content);
        feed.setCreateTime(new Date());
        result = userService.userFeedBack(feed);
        return result;
    }

    /**
     * 常见问题
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/getQuestion")
    public String getQuestion(HttpServletRequest request) {
        String result = "";
        String userId = request.getParameter("per_id");

        JSONObject obj = new JSONObject();
        String token = request.getParameter("token");

        // md5加密防串改
        String md5sign = request.getParameter("md5sign");
        boolean aa = VerifyMD5.Sign(md5sign, userId, token);
        if (!aa) {
            obj.put("code", CodeReturn.MD5_WRONG);
            obj.put("info", "防串改不通过！");
            return obj.toString();
        }

        int yan = userService.yanzhengtoken(userId, token);
        if (201 == yan) {
            obj.put("code", CodeReturn.TOKEN_WRONG);
            obj.put("info", "该帐号已在别的设备登录，请重新登录");
            result = obj.toString();
            return result;
        }

        // System.out.println(userId);
        result = userService.getQuestion();
        return result;
    }

    /**
     * 消息列表
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/getMessageByUserId")
    public String getMessage(HttpServletRequest request) {
        String result = "";
        String userId = request.getParameter("per_id");
        String nowPage = request.getParameter("nowPage");
        String pageSize = request.getParameter("pageSize");

        JSONObject obj = new JSONObject();
        String token = request.getParameter("token");

        // md5加密防串改
        String md5sign = request.getParameter("md5sign");
        boolean aa = VerifyMD5.Sign(md5sign, userId, nowPage, pageSize, token);
        if (!aa) {
            obj.put("code", CodeReturn.MD5_WRONG);
            obj.put("info", "防串改不通过！");
            return obj.toString();
        }

        int yan = userService.yanzhengtoken(userId, token);
        if (201 == yan) {
            obj.put("code", CodeReturn.TOKEN_WRONG);
            obj.put("info", "该帐号已在别的设备登录，请重新登录");
            result = obj.toString();
            return result;
        }

        // System.out.println(userId);
        result = userService.getMessageByUserId(userId,
                Integer.parseInt(nowPage), Integer.parseInt(pageSize));
        return result;
    }

    /**
     * 通用发送站内信消息
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/setMessage")
    public String setMessage(HttpServletRequest request) {
        String result = "";
        String userId = request.getParameter("per_id");
        String templateId = request.getParameter("templateId");
        String params = request.getParameter("params");

        // md5加密防串改
        String md5sign = request.getParameter("md5sign");
        boolean aa = VerifyMD5.Sign(md5sign, userId, templateId, params);
        if (!aa) {
            JSONObject obj = new JSONObject();
            obj.put("code", CodeReturn.MD5_WRONG);
            obj.put("info", "防串改不通过！");
            return obj.toString();
        }

        result = userService.setMessage(userId, templateId, params);
        return result;
    }

    /**
     * 获取我的历史借款记录
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/getMyBorrowList")
    public String getMyBorrowList(HttpServletRequest request) {
        String result = "";
        String userId = request.getParameter("per_id");
        String nowPage = request.getParameter("nowPage");
        String pageSize = request.getParameter("pageSize");

        JSONObject obj = new JSONObject();
        String token = request.getParameter("token");

        // md5加密防串改
        String md5sign = request.getParameter("md5sign");
        boolean aa = VerifyMD5.Sign(md5sign, userId, nowPage, pageSize, token);
        if (!aa) {
            obj.put("code", CodeReturn.MD5_WRONG);
            obj.put("info", "防串改不通过！");
            return obj.toString();
        }

        int yan = userService.yanzhengtoken(userId, token);
        if (201 == yan) {
            obj.put("code", CodeReturn.TOKEN_WRONG);
            obj.put("info", "该帐号已在别的设备登录，请重新登录");
            result = obj.toString();
            return result;
        }

        result = userService.getMyBorrowList(userId, Integer.parseInt(nowPage),
                Integer.parseInt(pageSize));
        return result;
    }

    /**
     * 消息未读变已读
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/updateMessageStatus")
    public String updateMessageStatus(HttpServletRequest request) {
        String result = "";
        String userId = request.getParameter("per_id");
        String messageId = request.getParameter("messageId");

        JSONObject obj = new JSONObject();
        String token = request.getParameter("token");

        // md5加密防串改
        String md5sign = request.getParameter("md5sign");
        boolean aa = VerifyMD5.Sign(md5sign, userId, messageId, token);
        if (!aa) {
            obj.put("code", CodeReturn.MD5_WRONG);
            obj.put("info", "防串改不通过！");
            return obj.toString();
        }

        int yan = userService.yanzhengtoken(userId, token);
        if (201 == yan) {
            obj.put("code", CodeReturn.TOKEN_WRONG);
            obj.put("info", "该帐号已在别的设备登录，请重新登录");
            result = obj.toString();
            return result;
        }

        result = userService.updateMessageStatus(userId, messageId);
        return result;
    }

    /**
     * 查看borrList里面单个的详情
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/getProdModeByBorrId")
    public String getProdModeByBorrId(HttpServletRequest request) {
        String result = "";
        String borrId = request.getParameter("borrId");

        // md5加密防串改
        String md5sign = request.getParameter("md5sign");
        boolean aa = VerifyMD5.Sign(md5sign, borrId);
        if (!aa) {
            JSONObject obj = new JSONObject();
            obj.put("code", CodeReturn.MD5_WRONG);
            obj.put("info", "防串改不通过！");
            return obj.toString();
        }

        result = userService.getProdModeByBorrId(borrId);
        return result;
    }

    /**
     * 还款详情
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/getRepaymentDetails")
    public String getRepaymentDetails(HttpServletRequest request) {
        String result = "";
        String userId = request.getParameter("per_id");
        String borrId = request.getParameter("borr_id");

        JSONObject obj = new JSONObject();
        String token = request.getParameter("token");

        // md5加密防串改
        String md5sign = request.getParameter("md5sign");
        boolean aa = VerifyMD5.Sign(md5sign, userId, borrId, token);
        if (!aa) {
            obj.put("code", CodeReturn.MD5_WRONG);
            obj.put("info", "防串改不通过！");
            return obj.toString();
        }

        int yan = userService.yanzhengtoken(userId, token);
        if (201 == yan) {
            obj.put("code", CodeReturn.TOKEN_WRONG);
            obj.put("info", "该帐号已在别的设备登录，请重新登录");
            result = obj.toString();
            return result;
        }

        result = userService.getRepaymentDetails(userId, borrId);
        return result;
    }

    /**
     * 资金记录
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/perAccountLog")
    public String perAccountLog(HttpServletRequest request, int nowPage,
                                int pageSize) {
        JSONObject obj = new JSONObject();
        String result = "";
        String userId = request.getParameter("per_id");

        String token = request.getParameter("token");

        // md5加密防串改
        String md5sign = request.getParameter("md5sign");
        boolean aa = VerifyMD5.Sign(md5sign, userId, token,
                String.valueOf(nowPage), String.valueOf(pageSize));
        if (!aa) {
            obj.put("code", CodeReturn.MD5_WRONG);
            obj.put("info", "防串改不通过！");
            return obj.toString();
        }

        int yan = userService.yanzhengtoken(userId, token);
        if (201 == yan) {
            obj.put("code", CodeReturn.TOKEN_WRONG);
            obj.put("info", "该帐号已在别的设备登录，请重新登录");
            result = obj.toString();
            return result;
        }

        result = userService.perAccountLog(userId, nowPage, pageSize);
        return result;
    }

    /**
     * 手工同步白名单
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/syncWhiteList")
    public String syncWhiteList(HttpServletRequest request) {
        NoteResult result = new NoteResult(CodeReturn.SUCCESS_CODE, "手工同步白名单成功");
        userService.syncWhiteList();
        return com.alibaba.fastjson.JSONObject.toJSONString(result);
    }

    /**
     * 手工同步白名单
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/syncPhoneWhiteList")
    public String syncPhoneWhiteList(HttpServletRequest request) {
        NoteResult result = new NoteResult(CodeReturn.SUCCESS_CODE, "手工同步白名单手机号成功");
        userService.syncPhoneWhiteList();
        return com.alibaba.fastjson.JSONObject.toJSONString(result);
    }

    /**
     * 检测是否可以引导用户下载金狐贷
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/getWithdrawInformation")
    public String getWithdrawInformation(HttpServletRequest request) {
        NoteResult result = new NoteResult(CodeReturn.FAIL_CODE, "不可用的参数");

        String userId = request.getParameter("per_id");
        String md5Sign = request.getParameter("md5sign");

        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(md5Sign)) {
            logger.warn("无效的请求参数");
            return com.alibaba.fastjson.JSONObject.toJSONString(result);
        }

        boolean verify = VerifyMD5.Sign(md5Sign, userId);
        if (!verify) {
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("防串改不通过！");
            return com.alibaba.fastjson.JSONObject.toJSONString(result);
        }

        try {
            result = userService.getWithdrawInformation(Integer.parseInt(userId));
            com.alibaba.fastjson.JSONObject dataObject = (com.alibaba.fastjson.JSONObject) result.getData();
            if (dataObject != null && dataObject.containsKey("isAvailable") && Boolean.TRUE.equals(dataObject.getBoolean("isAvailable"))) {
                String jhdRegisterUrl = PropertiesReaderUtil.read("common","jhd.register");
                dataObject.put("gotoUrl", jhdRegisterUrl);
            }else{
                dataObject.put("gotoUrl", "");
            }
        } catch (NumberFormatException e) {
            logger.warn("非法的用户ID", e);
            return com.alibaba.fastjson.JSONObject.toJSONString(result);
        } catch (Throwable e) {
            logger.error("检测用户是否可以下载使用金狐贷提款失败", e);
            return com.alibaba.fastjson.JSONObject.toJSONString(result);
        }

        return com.alibaba.fastjson.JSONObject.toJSONString(result);
    }

    /**
     * 取消订单
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/canOfOrder")
    public String canOfOrder(HttpServletRequest request, String orderId,
                             String token, String info) {
        String result = "";

        // md5加密防串改
        String md5sign = request.getParameter("md5sign");
        boolean aa = VerifyMD5.Sign(md5sign, orderId, token, info);
        if (!aa) {
            JSONObject obj = new JSONObject();
            obj.put("code", CodeReturn.MD5_WRONG);
            obj.put("info", "防串改不通过！");
            return obj.toString();
        }

        result = userService.canOfOrder(orderId, token, info);
        return result;
    }

    /**
     * 我的优惠券
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/getMyCoupon")
    public String getMyCoupon(HttpServletRequest request, String per_id,
                              String couponStatus, int nowPage, int pageSize) {

        String result = "";

        JSONObject obj = new JSONObject();
        String token = request.getParameter("token");

        // md5加密防串改
        String md5sign = request.getParameter("md5sign");
        boolean aa = VerifyMD5.Sign(md5sign, per_id, couponStatus,
                String.valueOf(nowPage), String.valueOf(pageSize), token);
        if (!aa) {
            obj.put("code", CodeReturn.MD5_WRONG);
            obj.put("info", "防串改不通过！");
            return obj.toString();
        }

        int yan = userService.yanzhengtoken(per_id, token);
        if (201 == yan) {
            obj.put("code", CodeReturn.TOKEN_WRONG);
            obj.put("info", "该帐号已在别的设备登录，请重新登录");
            result = obj.toString();
            return result;
        }

        result = userService.getMyCoupon(per_id, couponStatus, nowPage,
                pageSize);
        return result;
    }

    /**
     * 手动查询还款中的订单
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryPayment")
    public String queryPayment() {
        String res = ysbpayService.queryPayment();
        return res;
    }

    /**
     * 用户协议查看
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/agreement")
    public String queryAgreement() {
        JSONObject obj = new JSONObject();
        obj.put("code", CodeReturn.success);
        obj.put("info", "成功");
        String fileString = "";
        try {
            InputStream is = getClass().getResourceAsStream("/../../agreement.html");
            BufferedReader bis = new BufferedReader(new InputStreamReader(is));

            String szTemp;
            while ((szTemp = bis.readLine()) != null) {
                fileString += szTemp;
            }
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        obj.put("data", fileString);
        // System.out.println(obj.get("data"));
        return obj.toString();
    }

    @RequestMapping("/sendSinaMail")
    @ResponseBody
    public void sendSinaMail(@RequestParam(required = false) String host) {
        MailSender cn = new MailSender();
        if (!Detect.notEmpty(host)) {
            host = "smtp.sina.com";
        }
        String[] to = {"wanzezhong@jinhuhang.com.cn"};
        String[] copyto = {"330402499@qq.com", "chenzhen@jinhuhang.com.cn"};

        // 设置要发送附件的位置和标题
//		cn.setAffix("C:\\Users\\wanzezhong\\Desktop\\20170621.xls", "20170621.xls");
        cn.setAffix("/data/www/youmi/youmiapp/billingNotice/20170621.xls", "20170621.xls");

        cn.setAddress("jinhuhanghlwzx@sina.com", to, copyto, "通知还款短信号码", "号码推送");
        cn.send(host, "jinhuhanghlwzx@sina.com", "jhh123456");
    }

    @RequestMapping("/sendWangYiMail")
    @ResponseBody
    public void sendWangYiMail(@RequestParam(required = false) String host) {
        MailSender cn = new MailSender();
        if (!Detect.notEmpty(host)) {
            host = "smtp.163.com";
        }
        String[] to = {"wanzezhong@jinhuhang.com.cn"};
        String[] copyto = {"330402499@qq.com", "chenzhen@jinhuhang.com.cn"};

        // 设置要发送附件的位置和标题
//		cn.setAffix("C:\\Users\\wanzezhong\\Desktop\\20170621.xls", "20170621.xls");
        cn.setAffix("/data/www/youmi/youmiapp/billingNotice/20170621.xls", "20170621.xls");

        cn.setAddress("carl_wanzezhong@163.com", to, copyto, "通知还款短信号码", "号码推送");
        cn.send(host, "carl_wanzezhong@163.com", "wan123123");
    }

    @RequestMapping("/sendjhhMail")
    @ResponseBody
    public void sendjhhMail(@RequestParam(required = false) String host) {
        MailSender cn = new MailSender();
        if (!Detect.notEmpty(host)) {
            host = "mail.jinhuhang.com.cn";
        }
        String[] to = {"wanzezhong@jinhuhang.com.cn"};
        String[] copyto = {"330402499@qq.com", "chenzhen@jinhuhang.com.cn"};

        // 设置要发送附件的位置和标题
//		cn.setAffix("C:\\Users\\wanzezhong\\Desktop\\20170621.xls", "20170621.xls");
        cn.setAffix("/data/www/youmi/youmiapp/billingNotice/20170621.xls", "20170621.xls");

        cn.setAddress("wanzezhong@jinhuhang.com.cn", to, copyto, "通知还款短信号码", "号码推送");
        cn.send(host, "wanzezhong", "wan+123");
    }


    @RequestMapping("/checkBlack")
    @ResponseBody
    public String checkBlack(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return com.alibaba.fastjson.JSONObject.toJSONString(new NoteResult(CodeReturn.FAIL_CODE, "电话号码为空"));
        } else {
            return com.alibaba.fastjson.JSONObject.toJSONString(userService.checkBlack(phone));
        }
    }

    @RequestMapping("/deleteRedis")
    @ResponseBody
    public String deleteRedis(String per_id) {
        if (StringUtils.isEmpty(per_id)) {
            return "per_id为空";
        } else {
            return userService.deleteRedis(per_id);
        }
    }

    @RequestMapping("/getAmount")
    @ResponseBody
    public NoteResult getAmount(String cardNum) {
        NoteResult result = new NoteResult(CodeReturn.FAIL_CODE,"失败");
        try {
            if (StringUtils.isEmpty(cardNum)) {
                //不传cardNum  返回无限大
                result.setData("99999");
            } else {
                result.setData(ysbpayService.getAmountByCardNum(cardNum));
            }
            result.setCode(CodeReturn.SUCCESS_CODE);
            result.setInfo("成功");
        }catch (Exception e){
            e.printStackTrace();
            return new NoteResult(CodeReturn.FAIL_CODE,"失败");
        }
        return result;
    }

    @RequestMapping("/testCanPay")
    @ResponseBody
    public String testCanPay(String amount,String cardNum) {
        if(ysbpayService.canPayCont(Double.valueOf(amount), cardNum)){
            return "1";
        }else {
            return "0";
        }
    }


    public static void main(String[] args) {
    }
}