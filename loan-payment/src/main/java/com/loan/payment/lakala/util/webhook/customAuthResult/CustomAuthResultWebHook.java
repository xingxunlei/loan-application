package com.loan.payment.lakala.util.webhook.customAuthResult;

import com.loan.payment.lakala.exception.LakalaCommonException;
import com.loan.payment.lakala.exception.LakalaEncryptException;
import com.loan.payment.lakala.util.LakalaCrossPayEnv;
import com.loan.payment.lakala.util.LakalaMsgUtil;
import com.loan_entity.enums.LakalaEnv;
import com.loan_entity.lakala.LakalaCrossPayEncryptRequest;
import com.loan_entity.lakala.LakalaCrossPayEncryptResponse;
import com.loan_entity.lakala.webHook.CustomAuthNotify;
import com.loan_entity.lakala.webHook.CustomAuthResultNotifyRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CustomAuthResultWebHook {

	private static final Logger logger = LoggerFactory.getLogger(CustomAuthResultWebHook .class);
    
    @Autowired
    private LakalaCrossPayEnv lklCrossPayEnv;
    
    @RequestMapping(value = "lklCustomAuthResult/handle")
	public LakalaCrossPayEncryptResponse proceed(@RequestBody LakalaCrossPayEncryptRequest notify) {
        //注册应用环境
            	logger.debug("entering method proceed,req={}", notify.toString());
    	 CustomAuthNotify customAuthNotify = null;
         LakalaCrossPayEncryptResponse res = new LakalaCrossPayEncryptResponse();
         res.setMerId(notify.getMerId());
         res.setTs(notify.getTs());
         res.setRetMsg("通知成功");
         res.setRetCode("0000");
         res.setVer("3.0.0");
         res.setEncKey(notify.getEncKey());

         try {
        	 customAuthNotify= LakalaMsgUtil.decryptMsgFromLklCustomAuth(notify, CustomAuthNotify.class);
            // 得到通知的结果进行业务处理
             CustomAuthResultNotifyRes response = new CustomAuthResultNotifyRes();
             response.setAmount(customAuthNotify.getAmount());
             response.setBgUrl(customAuthNotify.getBgUrl());
             response.setBizTypeCode(customAuthNotify.getBizTypeCode());
             response.setCbpName(customAuthNotify.getCbpName());
             response.setClientId(customAuthNotify.getClientId());
             response.setCuId(customAuthNotify.getCuId());
             response.setCustomcomCode(customAuthNotify.getCustomcomCode());
             response.setGoodsFee(customAuthNotify.getGoodsFee());
             response.setMobile(customAuthNotify.getMobile());
             response.setName(customAuthNotify.getName());
             response.setOrderNo(customAuthNotify.getOrderNo());
             response.setOrderNote(customAuthNotify.getOrderNote());
             response.setPayerMail(customAuthNotify.getPayerMail());
             response.setPayOrderId(customAuthNotify.getPayOrderId());
             response.setTaxFee(customAuthNotify.getTaxFee());
             res = LakalaMsgUtil.encryptWebHookMsgCustomAuth(response, res);
         } catch (LakalaCommonException e) {
             logger.error("lakala custom auth error", e);
             res.setRetCode("9999");
             res.setRetMsg(e.getMessage());
         } catch (LakalaEncryptException e) {
             logger.error("lakala custom auth error", e);
             res.setRetCode("9999");
             res.setRetMsg(e.getMessage());
         } catch (Exception e) {
             logger.error("lakala custom auth error", e);
             res.setRetCode("9999");
             res.setRetMsg("系统异常");
         }
         logger.debug("exiting method proceed,res ={}", res.toString());
         return res;
	}

}
