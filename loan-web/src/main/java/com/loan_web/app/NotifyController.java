package com.loan_web.app;

import com.loan_api.app.NotifyService;
import com.loan_entity.app.NoteResult;
import com.loan_entity.app.PersonNotify;
import com.loan_entity.enums.NotifyStatusEnum;
import com.loan_utils.util.CodeReturn;
import com.loan_utils.util.VerifyMD5;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;

/**
 * APP消息推送接口。
 */
@Controller
@RequestMapping("/notify")
public class NotifyController {

    private static final Logger logger = LoggerFactory.getLogger(NotifyController.class);

    @Autowired
    private NotifyService notifyService;

    @ResponseBody
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerPushId(HttpServletRequest request) {
        NoteResult result = new NoteResult(CodeReturn.FAIL_CODE, "不可用的参数");

        String userId = request.getParameter("per_id");
        String pushId = request.getParameter("push_id");
        String md5Sign = request.getParameter("md5sign");

        if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(pushId) || StringUtils.isEmpty(md5Sign)){
            logger.warn("无效的请求参数");
            return JSONObject.toJSONString(result);
        }

        boolean verify = VerifyMD5.Sign(md5Sign, userId, pushId);
        if (!verify) {
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("防串改不通过！");
            return com.alibaba.fastjson.JSONObject.toJSONString(result);
        }

        PersonNotify personNotify = new PersonNotify();
        try {
            personNotify.setPerId(Integer.parseInt(userId));
            Date now = new Date(System.currentTimeMillis());
            personNotify.setUpdateDate(now);
            personNotify.setCreationDate(now);
            personNotify.setNotifyId(pushId);
            personNotify.setStatus(NotifyStatusEnum.Register.getStatusCode());
        } catch (NumberFormatException e) {
            logger.warn("非法的数字请求参数", e);
            return JSONObject.toJSONString(result);
        }
        result = notifyService.registerPersonNotify(personNotify);
        return JSONObject.toJSONString(result);
    }

    @ResponseBody
    @RequestMapping(value = "/unregister", method = RequestMethod.POST)
    public String unregisterPushId(HttpServletRequest request) {
        NoteResult result = new NoteResult(CodeReturn.FAIL_CODE, "不可用的参数");

        String userId = request.getParameter("per_id");
        String md5Sign = request.getParameter("md5sign");

        if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(md5Sign)){
            logger.warn("无效的请求参数");
            return JSONObject.toJSONString(result);
        }
        ;
        boolean verify = VerifyMD5.Sign(md5Sign, userId);
        if (!verify) {
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("防串改不通过！");
            return com.alibaba.fastjson.JSONObject.toJSONString(result);
        }

        PersonNotify personNotify = new PersonNotify();
        try {
            personNotify.setPerId(Integer.parseInt(userId));
            Date now = new Date(System.currentTimeMillis());
            personNotify.setUpdateDate(now);
            personNotify.setCreationDate(now);
            personNotify.setStatus(NotifyStatusEnum.Unregister.getStatusCode());
        } catch (NumberFormatException e) {
            logger.warn("非法的数字请求参数", e);
            return JSONObject.toJSONString(result);
        }
        result = notifyService.unregisterPersonNotify(personNotify);
        return JSONObject.toJSONString(result);
    }
}
