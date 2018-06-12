package com.loan.payment.controller;

import com.loan.payment.lakala.util.DateUtil;
import com.loan.payment.lakala.util.LakalaCrossPayEnv;
import com.loan.payment.service.LakalaPayService;
import com.loan_entity.enums.CertType;
import com.loan_entity.enums.CrossBorderBizType;
import com.loan_entity.enums.LakalaCurrency;
import com.loan_entity.lakala.LakalaCrossPayEncryptRequest;
import com.loan_entity.lakala.gather.LakalaGatherRequest;
import com.loan_entity.lakala.gather.LakalaGatherResponse;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class PayController {

    @Autowired
    private LakalaPayService payService;

    @ApiOperation("启动 任务 （开始跑批）")
    @RequestMapping(value = "/start", method = RequestMethod.GET)
    @ResponseBody
    public LakalaGatherResponse testGather(){

        LakalaGatherRequest req = new LakalaGatherRequest();

        req.setCardNo("6222021001116245702");
        req.setBgUrl("http://baidu.com");
        req.setBusiCode(CrossBorderBizType.CARGO_TRADE.getCode());
        req.setCertType(CertType.ID.getCode());
        req.setClientId("360000000000000000");
        req.setClientName("孙xx");
        req.setCurrency(LakalaCurrency.CNY.getCode());
        req.setCvv("123");
        req.setCustomNumberId("NA");
        req.setDateOfExpire("1602");
        req.setMerOrderId("SH20160420194563");
        req.setMobile("15000000000");
        req.setOrderAmount("223123.09");
        req.setOrderEffTime("20160422194550");
        req.setOrderSummary("111312");
        req.setOrderTime("20160420194550");
        req.setPayeeAmount("123123.09");
        req.setExt1("1231231你好我");
        req.setExt2("中华人民共和国");
        Map<String, String> ext = new HashMap<String, String>();
        ext.put("cuId", "2");
        ext.put("bizTypeCode", "E10");
        ext.put("goodsFee", "3123.98");
        ext.put("taxFee", "12.09");
        ext.put("buyForexKind", "0221");
        req.setExtension(ext);

        LakalaCrossPayEncryptRequest head = new LakalaCrossPayEncryptRequest();
        head.setVer("1.0.0");
        head.setTs(DateUtil.getCurrentTime());
        head.setReqType("B0013");
        head.setPayTypeId("4");
        head.setMerId(LakalaCrossPayEnv.getEnvConfig().getMerId());

        return payService.gather(req,head);
    }
}
