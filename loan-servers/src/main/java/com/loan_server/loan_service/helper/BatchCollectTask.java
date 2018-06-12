package com.loan_server.loan_service.helper;

import com.alibaba.fastjson.JSONObject;
import com.loan_api.loan.YsbpayService;
import com.loan_entity.app.Bank;
import com.loan_entity.app.NoteResult;
import com.loan_entity.common.Constants;
import com.loan_entity.loan_vo.BatchCollectEntity;
import com.loan_entity.manager.Order;
import com.loan_server.app_mapper.BankMapper;
import com.loan_server.manager_mapper.CodeValueMapper;
import com.loan_server.manager_mapper.OrderMapper;
import com.loan_server.manager_service.helper.SynDateTask;
import com.loan_utils.payment.CollectUtil;
import com.loan_utils.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * 批量代扣任务
 */
//@Component("batchCollectTask")
//@Scope("prototype")
public class BatchCollectTask implements Callable<Object>, Serializable {

    private static Logger log = Logger.getLogger(BatchCollectTask.class);

    private List<BatchCollectEntity> entitys;

    private CodeValueMapper codeValueMapper;

    private OrderMapper orderMapper;

    private BankMapper bankMapper;

    private JedisCluster jedisCluster;

    private YsbpayService ysbpayService;

    private String isTest = PropertiesReaderUtil.read("third", "isTest");

    public BatchCollectTask(CodeValueMapper codeValueMapper, OrderMapper orderMapper, BankMapper bankMapper, JedisCluster jedisCluster,YsbpayService ysbpayService) {
        this.codeValueMapper = codeValueMapper;
        this.orderMapper = orderMapper;
        this.bankMapper = bankMapper;
        this.jedisCluster = jedisCluster;
        this.ysbpayService = ysbpayService;
    }

    public synchronized void setEntitys(List<BatchCollectEntity> entitys) {
        this.entitys = entitys;
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Object call() throws Exception {
        long starttime = System.currentTimeMillis();
        //从快速编码表查出手续费  1：代收
        log.info("进入task");
        String fee = codeValueMapper.getMeaningByTypeCode("payment_fee", "1");
        String responseUrl = PropertiesReaderUtil.read("third", "collectResponseUrl");
        log.info("本次批量代扣订单数：" + entitys.size());
        for (BatchCollectEntity entity : entitys) {
            try {
                log.info("进入循环请求银生宝");
                String borrId = entity.getBorrId();
                String optAmount = entity.getOptAmount();

                //先检查该笔订单是否可扣
                if ("0".equals(optAmount)){
                    //传0就是扣所有剩余应还
                    NoteResult canPay = ysbpayService.canPayCollect(borrId, 0);
                    if (CodeReturn.SUCCESS_CODE.equals(canPay.getCode())) {
                        optAmount = canPay.getData().toString();
                    }else {
                        log.info("批量代扣(全额)=======borrId:"+borrId+"检查是否可以扣款失败，未发起代扣,当前可扣金额："+canPay.getData());
                        continue;
                    }
                }else{
                    //非全额扣款
                    NoteResult canPay = ysbpayService.canPayCollect(borrId, Double.valueOf(optAmount));
                    if (!CodeReturn.SUCCESS_CODE.equals(canPay.getCode())) {
                        //没有通过检查
                        log.info("批量代扣(定额)=======borrId:"+borrId+"检查是否可以扣款失败，未发起代扣,当前可扣金额："+canPay.getData());
                        continue;
                    }
                }

                // 重构后 订单号自己生成 规则 时间精确到秒+随机字符串
                String serNo = SerialNumUtil.createByType("04");
                UUID uuid = UUID.randomUUID();
                String perId = entity.getPerId();

                String bankNum = entity.getBankNum();
                String phone = entity.getPhone();
                String description = entity.getDescription();
                String createUser = StringUtils.isEmpty(entity.getCreateUser()) ? "" : entity.getCreateUser();
                String collectionUser = StringUtils.isEmpty(entity.getCollectionUser()) ? "" : entity.getCollectionUser();
                String deductionsType = StringUtils.isEmpty(entity.getDeductionsType()) ? "" : entity.getDeductionsType();

                askCollectionSimple(perId, uuid.toString(), borrId, optAmount, bankNum, phone, description, serNo, fee, responseUrl, createUser, collectionUser, deductionsType);

                //测试增加3秒钟
//                Thread.sleep(3000);

            } catch (Exception e) {
                log.error(e.getMessage());
            }

        }
        log.error(Thread.currentThread().getName() + "线程执行结束用时"
                + (System.currentTimeMillis() - starttime));
        return 1;
    }


    private void askCollectionSimple(String perId, String guid, String borrId, String optAmount, String bankNum, String phone, String description, String serNo, String fee, String responseUrl, String createUser, String collectionUser, String deductionsType) {

        try {
            Order order = new Order();
            order.setGuid(guid);
            // 主订单 pid设置为0 没有父订单
            order.setpId(0);
            order.setSerialNo(serNo);
            order.setPerId(Integer.valueOf(perId));
            order.setCompanyId(1);
            Bank bank = bankMapper.selectByBankNum(bankNum);
            String subContractId = bank.getSubContractNum();
            order.setBankId(bank.getId());

            order.setConctactId(Integer.valueOf(borrId));
            // 操作金额----------真实-----------------------------------
            double opt = Double.valueOf(optAmount);
            String amount = String.format("%.2f", opt);
            order.setOptAmount(amount);
            order.setActAmount(amount);
            if (description != null && !"".equals(description.trim())) {
                order.setRlRemark(description);
            }

            // 09-07 批量代扣全改为8 凌晨批量代扣操作人为8888
            order.setType("8");
            if (StringUtils.isEmpty(createUser)) {
                createUser = "8888";
            }

            //2017-07-28新加3个参数
            order.setCreateUser(createUser);
            order.setCollectionUser(collectionUser);
            order.setDeductionsType(deductionsType);
            order.setRlState("p");

            //生成手续费订单
            Order feeOrder = new Order();
            feeOrder.setGuid(BorrNum_util.createBorrNum());
            //手续费订单的pid为主订单id
            feeOrder.setpId(order.getId());
            feeOrder.setSerialNo(SerialNumUtil.createByType("03"));
            feeOrder.setPerId(order.getPerId());
            feeOrder.setCompanyId(order.getCompanyId());
            feeOrder.setConctactId(order.getConctactId());
            feeOrder.setOptAmount(fee);
            feeOrder.setActAmount(fee);
            feeOrder.setRlState("p");
            feeOrder.setType("3");//手续费

            //insert之前set key
            if (!"OK".equals(jedisCluster.set(RedisConst.PAY_ORDER_KEY + borrId, "off", "NX", "EX", 2 * 60 * 60))) {
                log.info("批量扣款borrId：" + borrId + ",serNo:" + serNo + " insert失败，该笔借款已锁");
                return;
            }

            int i = orderMapper.insertSelective(order);
            int j = orderMapper.insertSelective(feeOrder);


            if (i > 0 && j > 0) {// 插入订单成功
//               //insert成功，delete key
                jedisCluster.del(RedisConst.PAY_ORDER_KEY + borrId);

                //真正请求第三方的金额为opt + 手续费
                String finalAmount = String.format("%.2f", opt + Double.valueOf(fee));
                //发起代扣请求
                log.info(Thread.currentThread().getName() + ">>>>>>>>>发起代扣请求,请求参数：");
                log.info("amount:" + finalAmount + " phone:" + phone + " subContractId:" + subContractId
                        +
                        " orderId:" + order.getSerialNo() + " responseUrl:" + responseUrl);
                //---------------------------------真实第三方--------------------------------------------------

                String response = CollectUtil.requestCollect(finalAmount, phone, subContractId, order.getSerialNo(),
                        responseUrl);
                JSONObject res = JSONObject.parseObject(response);
                String result_code = res.getString("result_code");
                String result_msg = res.getString("result_msg");
                //----------------------------------模拟第三方-------------------------------------------------
                if ("on".equals(isTest)) {
                    result_code = "0000";
                    result_msg = "模拟第三方";
                }
                //------------------------------------------------------------------------------
                if ("0000".equals(result_code)) {
                    log.info("第三方受理成功" + order.getSerialNo());
                } else {
                    //第三方受理失败
                    order.setRlState("f");
                    order.setRlRemark(result_msg);
                    orderMapper.updateByPrimaryKeySelective(order);
                    feeOrder.setRlState("f");
                    feeOrder.setRlRemark(result_msg);
                    orderMapper.updateByPrimaryKeySelective(feeOrder);
                    log.info("第三方受理失败" + order.getSerialNo());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
