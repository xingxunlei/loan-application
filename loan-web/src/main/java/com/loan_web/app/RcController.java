package com.loan_web.app;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.loan_api.app.JuxinliService;
import com.loan_api.app.LoanService;
import com.loan_api.app.UserService;
import com.loan_api.manager.ManageLoanService;
import com.loan_entity.Juxinli.ReqDtoBasicInfo;
import com.loan_entity.app.NoteResult;
import com.loan_entity.enums.JuxinliEnum;
import com.loan_entity.manager_vo.ReqBackPhoneCheckVo;
import com.loan_entity.utils.ManagerResult;
import com.loan_utils.util.Base64;
import com.loan_utils.util.CodeReturn;
import com.loan_utils.util.DateUtil;
import com.loan_utils.util.VerifyMD5;
import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Map;

/**
 * @author xuepengfei
 */
@Controller
@RequestMapping("/RC")
public class RcController {

    @Autowired
    private JuxinliService juxinliService;
    @Autowired
    private LoanService loanService;



    private static final Logger logger = LoggerFactory.getLogger(RcController.class);

    /*
       拉黑接口
    */
    @ResponseBody
    @RequestMapping("/goBlack")
    public String orderStatus(String Phone) {
        NoteResult result = juxinliService.goBlack(Phone);
        if (CodeReturn.SUCCESS_CODE.equals(result.getCode())){
            return "1";
        }else{
            return "0";
        }
    }

    /*
           聚信立回调
     */
    @ResponseBody
    @RequestMapping(value = "/jxlCallback", method = RequestMethod.POST)
    public String backPhoneCheckMessage(HttpServletRequest request) {

        String RCParam = request.getParameter("RCParam");
        String afterDecode = "";
        ReqBackPhoneCheckVo callback = new ReqBackPhoneCheckVo();
        try {
            afterDecode = URLDecoder.decode(RCParam, "UTF-8");
            byte[] decode = java.util.Base64.getUrlDecoder().decode(afterDecode);
            String afterBase64Decode = new String(decode);
            System.out.println("jxlCallback:总参数RCParam："+afterBase64Decode);
            JSONObject paramJson = JSONObject.parseObject(afterBase64Decode);
            String modelStr = paramJson.getString("model");
            System.out.println("jxlCallback:参数model:"+modelStr);
            JSONObject req = JSONObject.parseObject(modelStr);
            callback.setPhone(req.getString("phone"));
            callback.setDescription(paramJson.getString("msg"));
            callback.setNode_status(paramJson.getString("code"));
            callback.setRequestId(req.getString("requestId"));
        } catch (Exception e) {
            e.printStackTrace();
            return JSONObject.toJSONString(new NoteResult("2000", "参数错误"));
        }

        return JSONObject.toJSONString(juxinliService.backPhoneCheckMessage(callback));
    }

    /**
     * 提交手机运营商密码 (手机风控)
     * @param per_id 用户ID
     * @param phone_pwd 手机运营商密码
     * @return
     */
    @ResponseBody
    @RequestMapping("/risk")
    public String risk(String per_id, String command_code,String phone_pwd,String query_code,String verify_code,String token,String md5sign) {
        NoteResult result = new NoteResult(JuxinliEnum.JXL_ERROR.getCode(),"参数错误");
        if (StringUtils.isEmpty(per_id) ||StringUtils.isEmpty(token) || StringUtils.isEmpty(md5sign)){
            return JSONObject.toJSONString(result);
        }
        command_code = StringUtils.isEmpty(command_code) ? "" : command_code;
        phone_pwd = StringUtils.isEmpty(phone_pwd) ? "" : phone_pwd;
        query_code = StringUtils.isEmpty(query_code) ? "" : query_code;
        verify_code = StringUtils.isEmpty(verify_code) ? "" : verify_code;
        System.out.println("RCController-request:"+md5sign+"," +per_id+"," +command_code+"," +phone_pwd+"," +query_code+"," +verify_code+"," +token);
//        logger.info(md5sign+"," +per_id+"," +command_code+"," +phone_pwd+"," +query_code+"," +verify_code+"," +token);
        boolean flag = VerifyMD5.Sign(md5sign, per_id,command_code,phone_pwd,query_code,verify_code,token);
        if(flag){
            String verify = loanService.verifyTokenId(per_id, token);
            if(CodeReturn.SUCCESS_CODE.equals(verify)){
                ReqDtoBasicInfo reqDtoBasicInfo = new ReqDtoBasicInfo();
//                if (StringUtils.isEmpty(command_code) && StringUtils.isNotEmpty(verify_code)){
//                    command_code = "10002";
//                }
//                if (StringUtils.isEmpty(command_code) && StringUtils.isNotEmpty(query_code)){
//                    command_code = "10022";
//                }
                reqDtoBasicInfo.setPer_id(per_id);
                reqDtoBasicInfo.setCode(command_code);
                reqDtoBasicInfo.setPassword(phone_pwd);
                reqDtoBasicInfo.setQueryPwd(query_code);
                reqDtoBasicInfo.setCaptcha(verify_code);

                result = juxinliService.risk(reqDtoBasicInfo);
            }else{
                result.setCode(CodeReturn.TOKEN_WRONG);
                result.setInfo("您的账号已在另一台设备上登录，请重新登录");
            }
        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }

        System.out.println("RCController-response:"+JSONObject.toJSONString(result));
        return JSONObject.toJSONString(result);
    }

    public static void main(String[] args) {
        String RCParam = "eyJtc2ciOiLogZrkv6Hnq4votoXml7YiLCJjb2RlIjoiMjAwMCIsIm1vZGVsIjp7ImlkIjpudWxsLCJwcm9kdWN0SWQiOm51bGwsIm5hbWUiOm51bGwsInBob25lIjoiMTUzMDk5OTQ2MDciLCJpZENhcmQiOm51bGwsInN0YXR1cyI6bnVsbCwic3RhdHVzRGVzYyI6IuiBmuS_oeeri-iHquWKqOi2heaXtiIsImNyZWF0ZVRpbWUiOm51bGwsInVwZGF0ZVRpbWUiOm51bGwsImV4cGFuZCI6bnVsbCwiZXhwYW5kMiI6bnVsbCwicmVxdWVzdElkIjpudWxsLCJyaXNrT3JnQ29kZSI6bnVsbH19";
//        String RCParam = request.getParameter("RCParam");
        String afterDecode = "";
        ReqBackPhoneCheckVo callback = new ReqBackPhoneCheckVo();
        try {
//            afterDecode = URLDecoder.decode(RCParam, "UTF-8");
//            byte[] decode = java.util.Base64.getUrlDecoder().decode(afterDecode);
//            String afterBase64Decode = new String(decode);
//            System.out.println("jxlCallback:总参数RCParam："+afterBase64Decode);

            String afterBase64Decode = "{\"msg\":\"\",\"code\":\"0000\",\"model\":{\"id\":null,\"productId\":45,\"name\":\"杨梅丽\",\"phone\":\"15278656188\",\"idCard\":\"452626198006102605\",\"status\":1,\"statusDesc\":\"\",\"createTime\":\"2017-11-20 14:25:03\",\"updateTime\":null,\"expand\":null,\"expand2\":null,\"requestId\":\"15111589240655037\",\"riskOrgCode\":\"jxl\"}}";
            JSONObject paramJson = JSONObject.parseObject(afterBase64Decode);
            String modelStr = paramJson.getString("model");
            System.out.println("jxlCallback:参数model:"+modelStr);
            JSONObject req = JSONObject.parseObject(modelStr);
            callback.setPhone(req.getString("phone"));
            callback.setDescription(paramJson.getString("msg"));
            callback.setNode_status(paramJson.getString("code"));
            callback.setRequestId(req.getString("requestId"));
            System.out.println(JSONObject.toJSONString(callback));
            System.out.println(DateUtils.formatDate(new Date(15111589240655037L)));
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
