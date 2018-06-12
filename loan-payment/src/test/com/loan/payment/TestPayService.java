package com.loan.payment;

import com.alibaba.fastjson.JSON;
import com.loan.payment.lakala.util.DateUtil;
import com.loan.payment.lakala.util.LakalaCrossPayEnv;
import com.loan.payment.service.LakalaPayService;
import com.loan.payment.service.impl.LakalaPaymentServiceImpl;
import com.loan_api.app.PaymentService;
import com.loan_entity.app.NoteResult;
import com.loan_entity.enums.CertType;
import com.loan_entity.enums.CrossBorderBizType;
import com.loan_entity.enums.LakalaCurrency;
import com.loan_entity.lakala.LakalaCrossPayEncryptRequest;
import com.loan_entity.lakala.gather.LakalaGatherRequest;
import com.loan_entity.lakala.gather.LakalaGatherResponse;
import com.loan_entity.loan_vo.BatchCollectEntity;
import com.loan_entity.manager.Order;
import com.loan_entity.payment.Gather;
import com.loan_utils.util.BorrNum_util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/spring-context.xml" })
public class TestPayService {

    @Autowired
    private LakalaPayService payService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private LakalaPaymentServiceImpl lakalaPaymentService;

    @Test
    public void testGatherTrue(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSS");
        String serNo = "YM" + sdf.format(new Date()) + BorrNum_util.getStringRandom(10);

        Gather gather = new Gather();
        gather.setGuid(UUID.randomUUID().toString().replace("-",""));
        gather.setBankId("159322");
        gather.setBankNum("6228480606724760875");
        gather.setName("徐立立");
        gather.setPhone("13512129781");
        gather.setIdCardNo("310227198407250010");
        gather.setPerId(6376953);
        gather.setBorrNum("14979482976574102");
        gather.setBorrId("5193720");
        gather.setOptAmount("100");
        gather.setDescription("代扣");
        gather.setSerNo(serNo);
        gather.setCreateUser("SH2129");
        gather.setCollectionUser("SH2129");

        NoteResult result = paymentService.sigleGatherByLakala(gather);

        System.out.println(JSON.toJSONString(result));
    }

    @Test
    public void testGatherBatch(){

        List<BatchCollectEntity> entities = new ArrayList<BatchCollectEntity>();
        for(int i=0;i<3;i++){
            BatchCollectEntity entity = new BatchCollectEntity();
            entity.setGuid(UUID.randomUUID().toString().replace("-",""));
            entity.setBankId("8257");
            entity.setBankNum("6228482968681868770");
            entity.setBorrId("5193720");
            entity.setName("徐立立");
            entity.setPhone("13512129781");
            entity.setIdCardNo("310227198407250010");
            entity.setPerId("6376953");
            entity.setDescription("批量代扣");
            entity.setDeductionsType("2");
            entity.setCollectionUser("SH2129");
            entity.setCreateUser("SH2129");
            entity.setOptAmount("10");

            entities.add(entity);
        }

        NoteResult result = paymentService.batchGatherByLakala(entities);
        System.out.println(JSON.toJSONString(result));
    }

    @Test
    public void testGatherPro(){
        List<Order> batchOrders = lakalaPaymentService.selectBatchGatherOrders();
        if(batchOrders != null && batchOrders.size() > 0){//查询到数据,立即处理
            lakalaPaymentService.doBatchGatherByLakala(batchOrders);
        }
    }

    @Test
    public void testGather(){
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
        req.setMerOrderId("SH20160420194564");
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

        LakalaGatherResponse response =  payService.gather(req,head);

        System.out.println(JSON.toJSONString(response));
    }
}
