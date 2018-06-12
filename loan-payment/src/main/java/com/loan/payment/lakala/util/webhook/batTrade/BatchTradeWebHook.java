package com.loan.payment.lakala.util.webhook.batTrade;

import com.loan.payment.lakala.exception.LakalaCommonException;
import com.loan.payment.lakala.exception.LakalaEncryptException;
import com.loan.payment.lakala.util.LakalaMsgUtil;
import com.loan.payment.lakala.util.webhook.LakalaWebHookIntf;
import com.loan.payment.lakala.util.webhook.WebHookHandler;
import com.loan_entity.lakala.LakalaCrossPayEncryptRequest;
import com.loan_entity.lakala.LakalaCrossPayEncryptResponse;
import com.loan_entity.lakala.webHook.BatchTradeNotify;
import com.loan_entity.lakala.webHook.BatchTradeNotifyRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 批量交易webhook
 * Created by jiang on 2016/10/25.
 */
@RestController
public class BatchTradeWebHook implements LakalaWebHookIntf<LakalaCrossPayEncryptResponse, LakalaCrossPayEncryptRequest> {
    private static final Logger logger = LoggerFactory.getLogger(BatchTradeWebHook.class);

    @Autowired(required = false)
    @Qualifier("lklBatTradeWebHook")
    private WebHookHandler<BatchTradeNotify> webHookIntf;


    /**
     * 响应拉卡拉批量交易回调
     *
     * @param notify 拉卡拉回调参数
     * @return
     */
    @RequestMapping(value = "/lklBatTrade/webHook", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public LakalaCrossPayEncryptResponse proceed(@RequestBody LakalaCrossPayEncryptRequest notify) {
        logger.debug("entering method proceed,req={}", notify.toString());
        //BatchTradeNotify为解密后的回调报文
        BatchTradeNotify batchTradeNotify = null;
        //LakalaCrossPayEncryptRes 为响应拉卡拉回调的密文报文类
        LakalaCrossPayEncryptResponse res = new LakalaCrossPayEncryptResponse();
        //BatchTradeNotifyRes 为响应拉卡拉回调的明文报文类
        BatchTradeNotifyRes response = new BatchTradeNotifyRes();

        res.setMerId(notify.getMerId());
        res.setTs(notify.getTs());
        res.setReqType("B0005");
        res.setRetCode("0000");
        res.setRetMsg("成功");
        res.setVer("3.0.0");

        try {
            //解密拉卡拉回调报文，生成明文报文对象
            batchTradeNotify = LakalaMsgUtil.decryptMsgFromLkl(notify, BatchTradeNotify.class);
            logger.info("decrypted msgis {}", batchTradeNotify.toString());
            if (null != batchTradeNotify.getNonce() && !"".equals(batchTradeNotify.getNonce())) {
                //该消息为注册回调地址时发送的消息，不进行业务处理，返回随机数即可
                String nonce = batchTradeNotify.getNonce();
                response.setNonce(nonce);
            }
            //否则需根据消息内容处理自己的业务
            else {
                batchTradeNotify.setMerchantId(notify.getMerId());
                webHookIntf.handle(batchTradeNotify);
            }
        } catch (LakalaCommonException e) {
            logger.error("lakala batch trade webhook error", e);
            res.setRetCode("9999");
            res.setRetMsg(e.getMessage());
        } catch (LakalaEncryptException e) {
            logger.error("lakala batch trade webhook error", e);
            res.setRetCode("9999");
            res.setRetMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("lakala batch trade webhook error", e);
            res.setRetCode("9999");
            res.setRetMsg("系统异常");
        }
        //加密明文响应报文
        logger.info("response is {}", response.toString());
        res = LakalaMsgUtil.encryptWebHookMsg(response, res);
        logger.debug("exiting method proceed,res ={}", res.toString());
        return res;
    }

}
