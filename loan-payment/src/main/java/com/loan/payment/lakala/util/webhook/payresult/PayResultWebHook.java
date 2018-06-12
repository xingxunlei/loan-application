package com.loan.payment.lakala.util.webhook.payresult;

import com.loan.payment.lakala.exception.LakalaCommonException;
import com.loan.payment.lakala.exception.LakalaEncryptException;
import com.loan.payment.lakala.util.LakalaMsgUtil;
import com.loan.payment.lakala.util.webhook.LakalaWebHookIntf;
import com.loan.payment.lakala.util.webhook.WebHookHandler;
import com.loan_entity.lakala.LakalaCrossPayEncryptRequest;
import com.loan_entity.lakala.LakalaCrossPayEncryptResponse;
import com.loan_entity.lakala.notify.PayResultNotify;
import com.loan_entity.lakala.notify.PayResultNotifyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * <em>处理拉卡拉支付结果回调</em>
 * </p>
 */
@RestController
public class PayResultWebHook implements LakalaWebHookIntf<LakalaCrossPayEncryptResponse, LakalaCrossPayEncryptRequest> {

    private static final Logger logger = LoggerFactory.getLogger(PayResultWebHook.class);

    @Autowired(required = false)
    @Qualifier("lklpayResultHandle")
    private WebHookHandler<PayResultNotify> webHookIntf;

    /**
     * 响应拉卡拉支付通知回调
     *
     * @param notify 拉卡拉支付结果通知
     * @return
     */
    @RequestMapping(value = "/lklpayResult/handle", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public LakalaCrossPayEncryptResponse proceed(@RequestBody LakalaCrossPayEncryptRequest notify) {
        logger.debug("entering method proceed,req={}", notify.toString());
        PayResultNotify payResultNotify = null;
        LakalaCrossPayEncryptResponse res = new LakalaCrossPayEncryptResponse();
        res.setMerId(notify.getMerId());
        res.setTs(notify.getTs());
        res.setReqType("B0005");
        res.setRetCode("0000");
        res.setVer("1.0.0");

        try {
            payResultNotify = LakalaMsgUtil.decryptMsgFromLkl(notify, PayResultNotify.class);
            webHookIntf.handle(payResultNotify);
            PayResultNotifyResponse response = new PayResultNotifyResponse();
            response.setMerOrderId(payResultNotify.getMerOrderId());
            response.setTransactionId(payResultNotify.getTransactionId());

            res = LakalaMsgUtil.encryptWebHookMsg(response, res);
        } catch (LakalaCommonException e) {
            logger.error("lakala batch trade error", e);
            res.setRetCode("9999");
            res.setRetMsg(e.getMessage());
        } catch (LakalaEncryptException e) {
            logger.error("lakala batch trade error", e);
            res.setRetCode("9999");
            res.setRetMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("lakala batch trade error", e);
            res.setRetCode("9999");
            res.setRetMsg("系统异常");
        }
        logger.debug("exiting method proceed,res ={}", res.toString());
        return res;
    }
}
