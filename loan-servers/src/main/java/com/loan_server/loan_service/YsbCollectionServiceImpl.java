package com.loan_server.loan_service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.loan_api.app.UserService;
import com.loan_api.loan.BankService;
import com.loan_api.loan.DelayQueueService;
import com.loan_api.loan.YsbCollectionService;
import com.loan_api.loan.YsbpayService;
import com.loan_entity.app.Bank;
import com.loan_entity.app.BorrowList;
import com.loan_entity.app.Card;
import com.loan_entity.app.NoteResult;
import com.loan_entity.loan.PerAccountLog;
import com.loan_entity.loan_vo.BatchCollectEntity;
import com.loan_entity.manager.Order;
import com.loan_entity.utils.BorrPerInfo;
import com.loan_entity.utils.Callback;
import com.loan_server.app_mapper.BankMapper;
import com.loan_server.app_mapper.BorrowListMapper;
import com.loan_server.app_mapper.CardMapper;
import com.loan_server.loan_mapper.PerAccountLogMapper;
import com.loan_server.loan_service.helper.BatchCollectTask;
import com.loan_server.manager_mapper.CodeValueMapper;
import com.loan_server.manager_mapper.CollectorsListMapper;
import com.loan_server.manager_mapper.OrderMapper;
import com.loan_server.manager_mapper.RepaymentPlanMapper;
import com.loan_utils.payment.CollectUtil;
import com.loan_utils.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisCluster;

import java.text.DecimalFormat;
import java.util.*;

//import static org.springframework.transaction.TransactionDefinition.ISOLATION_REPEATABLE_READ;


public class YsbCollectionServiceImpl implements YsbCollectionService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private BorrowListMapper borrowListMapper;
    @Autowired
    private BankService bankService;
    @Autowired
    private BankMapper bankMapper;
    @Autowired
    private CodeValueMapper codeValueMapper;
    @Autowired
    private CardMapper cardMapper;
    @Autowired
    private CollectorsListMapper collectorsListMapper;
//    @Autowired
//    private DelayQueueService delayQueueService;


    // @Autowired
    // private CompanyAccountLogMapper companyAccountLogMapper;
    // @Autowired
    // private CompanyAccountMapper companyAccountMapper;
    @Autowired
    private PerAccountLogMapper perAccountLogMapper;
    @Autowired
    private RepaymentPlanMapper repaymentPlanMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private YsbpayService ysbpayService;
    @Autowired
    ThreadPoolTaskExecutor threadPool;
    //    @Autowired
//    BatchCollectTask batchCollectTask;
    @Autowired
    private JedisCluster jedisCluster;


    private String isTest = PropertiesReaderUtil.read("third", "isTest");

    private static final Logger logger = LoggerFactory.getLogger(YsbCollectionServiceImpl.class);

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public NoteResult askCollection(String guid, String borrNum, String name, String idCardNo,
                                    String optAmount, String bankId, String bankNum,
                                    String phone, String description, String serNo, String createUser, String collectionUser) {

        NoteResult result = new NoteResult("9999", "系统繁忙");

//        logger.info("单笔请求参数："+guid+"," +borrNum+","+name+","+idCardNo+","+optAmount+","+bankId+","+bankNum+","+phone+","+description+","+serNo+","+createUser+","+collectionUser);

        //清结算锁
        String settleLock = jedisCluster.get(RedisConst.SETTLE_LOCK_KEY);
        if (!StringUtils.isEmpty(settleLock) && "off".equals(settleLock)) {
            result.setInfo("凌晨0点-5点开始为系统维护时间，期间不能进行还款，给您带来的不便敬请谅解！");
            return result;
        }

//        if (!ysbpayService.checkLock(Integer.valueOf(borrNum))){
//            result.setInfo("有正在处理的还款，请不要重复还款");
//            return result;
//        }

        String responseUrl = PropertiesReaderUtil.read("third", "collectResponseUrl");
        Order repeat = orderMapper.selectByGuid(guid);
        if (repeat != null) {//该订单已存在，不予操作
            return new NoteResult("9996", "订单已提交，不可重复提交");
        }

        try {
            Card card = cardMapper.selectByCardNo(idCardNo);
            String per_id = String.valueOf(card.getPerId());
            //从快速编码表查出手续费  1：代收
            String fee = codeValueMapper.getMeaningByTypeCode("payment_fee", "1");

            //首先判断该银行卡是否已绑定，未绑定，去绑定接口
            boolean binding = false;
            String subContractId = null;
            NoteResult query = bankService.queryContractId(per_id, bankNum);
            if ("200".equals(query.getCode())) {
                //已绑定
                subContractId = (String) query.getData();
                binding = true;
            } else {
                //未绑定，去绑定     绑定不设置为用户默认银行卡  status = 0
                NoteResult bind = bankService.bindingBank(per_id, bankId, bankNum, phone, "2", "");
                if ("200".equals(bind.getCode())) {
                    //绑定成功
                    subContractId = (String) bind.getData();
                    binding = true;
                } else {
                    //绑定失败
                    result.setCode("9998");
                    result.setInfo("绑定银行卡失败:" + bind.getInfo());
                    return result;
                }
            }

            if (binding) {//银行卡已绑定，可以请求代扣

                Order order = new Order();
                order.setGuid(guid);
                // 主订单 pid设置为0 没有父订单
                order.setpId(0);
                order.setSerialNo(serNo);
                order.setPerId(Integer.valueOf(per_id));
                order.setCompanyId(1);
                Bank bank = bankMapper.selectByBankNum(bankNum);
                order.setBankId(bank.getId());

                NoteResult canPay = ysbpayService.canPayCollect(borrNum, Double.valueOf(optAmount));
                if (CodeReturn.FAIL_CODE.equals(canPay.getCode())) {
                    result.setCode("9995");
                    result.setInfo(canPay.getInfo());
                    return result;
                }

                order.setConctactId(Integer.valueOf(borrNum));
                // 操作金额----------真实-----------------------------------
//            double opt = Double.valueOf(optAmount) + Double.valueOf(fee);
                double opt = Double.valueOf(optAmount);

                if (opt < 0) {
                    result.setCode("9995");
                    result.setInfo("扣款金额不能为负数！");
                    return result;
                }

                String amount = String.valueOf(opt);
                order.setOptAmount(amount);
                order.setActAmount(amount);
                if (description != null && !"".equals(description.trim())) {
                    order.setRlRemark(description);
                }
                order.setRlState("p");
                order.setType("4");// 代收

                //2017-07-28新加两个参数
                order.setCreateUser(createUser);
                order.setCollectionUser(collectionUser);

                //修改 在第三方受理成功之前，生成手续费订单
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
                if (!"OK".equals(jedisCluster.set(RedisConst.PAY_ORDER_KEY + borrNum, "off", "NX", "EX", 2 * 60 * 60))) {
                    return new NoteResult(CodeReturn.FAIL_CODE, "当前有还款在处理中，请稍后");
                }

                int i = orderMapper.insertSelective(order);
                int j = orderMapper.insertSelective(feeOrder);

                if (i > 0 && j > 0) {// 插入主订单成功

                    //insert成功，delete key
                    jedisCluster.del(RedisConst.PAY_ORDER_KEY + borrNum);

                    String finalAmount = String.valueOf(opt + Double.valueOf(fee));

                    //发起代扣请求
                    logger.info(">>>>>>>>>发起代扣请求,请求参数：");
                    logger.info("amount:" + finalAmount + " phone:" + phone + " subContractId:" + subContractId
                            +
                            " orderId:" + order.getSerialNo() + " responseUrl:" + responseUrl);
                    //---------------------------------真实第三方--------------------------------------------------

                    String response = CollectUtil.requestCollect(finalAmount, phone, subContractId, order.getSerialNo(),
                            responseUrl);
                    logger.info("请求银生宝response:" + response);
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
                        result.setCode("0000");
                        result.setInfo("代收请求已受理");
                        result.setData(serNo);
                        logger.info("代扣最终返回的参数：" + JSONObject.toJSONString(result));
                        return result;
                    } else {
                        //第三方受理失败
                        order.setRlState("f");
                        order.setRlRemark(result_msg);
                        orderMapper.updateByPrimaryKeySelective(order);
                        feeOrder.setRlState("f");
                        feeOrder.setRlRemark(result_msg);
                        orderMapper.updateByPrimaryKeySelective(feeOrder);
                        result.setCode(result_code);
                        result.setInfo(result_msg);
                        logger.info("代扣最终返回的参数：" + JSONObject.toJSONString(result));
                        return result;
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            result.setInfo("操作失败:信息不正确或网络繁忙");

            return result;
        }

        logger.info("代扣最终返回的参数：" + JSONObject.toJSONString(result));
        return result;
    }


    @Override
    @Transactional
    public NoteResult collectCallback(Callback callback) {
        NoteResult result = new NoteResult("201", "失败");
        try {

            logger.error("第三方代扣回调结果：" + JSONObject.toJSONString(callback));
//            System.out.println("========");
            //解析回调参数  验证mac
            String result_code = callback.getResult_code();
            String result_msg = callback.getResult_msg();
            String amount = callback.getAmount();
            String serialNo = callback.getOrderId();
            String mac = callback.getMac();
            //读取配置文件
            String key = PropertiesReaderUtil.read("third", "key");
            String accountId = PropertiesReaderUtil.read("third", "accountId");
            String myStr = "accountId=" + accountId + "&orderId=" + serialNo +
                    "&amount=" + amount + "&result_code=" + result_code +
                    "&result_msg=" + result_msg + "&key=" + key;
            String myMac = MD5Util.encodeToMd5(myStr).toUpperCase();
            if (!mac.equals(myMac)) {//mac签名验证失败
                result.setInfo("mac签名比对失败");
                return result;
            }
            //mac签名验证成功

            // ------------------模拟回调全成功--------------
            if ("on".equals(isTest)) {
                result_code = "0000";
                result_msg = "模拟回调结果全成功";
            }

            if ("0000".equals(result_code)) {//扣款成功
                String code = collectSuccess(serialNo, amount, result_msg);
                result.setCode(code);
                result.setInfo("扣款成功结果处理成功");
            } else {//扣款不成功
                String desc = callback.getDesc();
                String code = collectFail(serialNo, result_code, result_msg, desc);
                result.setCode(code);
                result.setInfo("扣款失败结果处理成功");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.setCode("201");
            result.setInfo("系统错误");
            return result;
        }
        return result;
    }


    /**
     * 代扣成功后的操作
     *
     * @param serialNo 订单号（流水号）
     * @param amount   请求第三方的交易金额
     * @return
     */
    @Transactional
    public String collectSuccess(String serialNo, String amount, String result_msg) {
        try {

            int settle = ysbpayService.settlement(serialNo, "s", result_msg);

            if (settle == 1) {

                Order order = orderMapper.selectBySerial(serialNo);


                //资金流水
                PerAccountLog pal = new PerAccountLog();
                pal.setPerId(order.getPerId());
                pal.setOrderId(order.getId());
                pal.setOperationType("10");
                pal.setAmount(order.getOptAmount());
                pal.setRemark("代收");
                pal.setAddtime(new Date());
                perAccountLogMapper.insertSelective(pal);
                logger.info("增加一条ym_per_account_log新增一条资金明细成功》...");


                BorrowList borrowList = borrowListMapper.selectByPrimaryKey(order.getConctactId());
                // 合同和人的信息
                BorrPerInfo bpi = new BorrPerInfo();
                bpi = borrowListMapper.selectByBorrId(order.getConctactId());
                DecimalFormat df = new DecimalFormat("######0.00");
                // 代扣成功改发代扣站内信,成功结清发送成功结清短信
                String time = String.format("%tF", new Date());
                String fee = codeValueMapper.getMeaningByTypeCode("payment_fee", "1");
                String money = String.format("%.2f", Double.valueOf(order.getOptAmount()) + Double.valueOf(fee));
                String message = "【悠米闪借】尊敬的用户，悠米闪借于" + time + "已成功扣款" + money + "元，感谢您的支持！";

                if (!"on".equals(isTest)) {//非测试环境才发短信
//                    logger.info("非测试环境，扣款成功发短信");
                    EmaySmsUtil.send(message, String.valueOf(bpi.getPhone()), 1);
                }

                String result = "";
                if ("BS010".equals(borrowList.getBorrStatus()) || "BS006".equals(borrowList.getBorrStatus())) {
                    result = userService.setMessage(String.valueOf(order.getPerId()), "5",
                            bpi.getName() + "," + df.format(bpi.getMaximum_amount()));

                    //逾期结清 算业绩
                    int yeji = collectorsListMapper.updateCollectorsList(order.getConctactId());
                    if (yeji > 0) {
                        logger.info("结清算业绩成功");
                    }

                } else {

                    result = userService.setMessage(String.valueOf(order.getPerId()), "10",
                            bpi.getName() + "," + DateUtil.getDateStringyyyymmdd(new Date()) + ","
                                    + df.format(Double.parseDouble(money)));
                }

                JSONObject obje = JSONObject.parseObject(result);
                if ("200".equals(obje.get("code"))) {
                    logger.debug("还款成功消息发送成功！");
                } else {
                    logger.debug(obje.get("info").toString());
                }
                return "200";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "201";
    }

    @Transactional
    public String collectFail(String serialNo, String result_code, String result_msg, String desc) {
        try {
            int settle = ysbpayService.settlement(serialNo, "f", desc);

            if (settle == 1) {
                return "200";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "201";
        }

        return "201";
    }

    @Override
    public void queryCollectStatus() {


        logger.error("进入定时器查询代扣的状态为处理中的订单======");

        //查询订单状态为p,订单类型为4还款（代收）的数据，订单创建时间<任务运行的时间-30分钟
        List<Order> list = orderMapper.selectOrders("p", "4", 5);
        List<Order> list8 = orderMapper.selectOrders("p", "8", 5);
        logger.error("type4:" + list.size());
        logger.error("type8:" + list8.size());


        list.addAll(list8);

        if (list.isEmpty()) {
            //没有p的订单 不查询
            logger.info("queryCollectStatus没有p状态的代扣订单");
        } else {

            for (Order order : list) {
                try {
                    String serialNo = order.getSerialNo();
                    String amount = order.getOptAmount();
                    //------------------------真实请求第三方查询---------------
                    String response = CollectUtil.queryOrder(serialNo);
                    JSONObject res = JSONObject.parseObject(response);
                    String result_code = res.getString("result_code");
                    String result_msg = res.getString("result_msg");


                    //------------------------模拟请求第三方-------------------
                    if ("on".equals(isTest)) {
                        //成功失败随机出现
//                        Random ran = new Random();
//                        if (ran.nextInt(5) > 1) {
//                            result_code = "0000";
//                        } else {
//                            result_code = "1111";
//                        }
                        result_code = "0000";
                        result_msg = "模拟第三方";
                    }
                    //-----------------------------------------------------
                    if ("0000".equals(result_code)) {//第三方受理成功
                        String status = res.getString("status");
                        String desc = res.getString("desc");
                        if ("on".equals(isTest)) {
                            //三种结果随机出现
                            Random random = new Random();
                            int statusFlag = random.nextInt(7);
                            if (statusFlag == 0) {
                                status = "00";
                            } else {
                                status = "20";
                            }
//                            status = "00";
                        }
                        if ("00".equals(status)) {//交易成功
                            String code = collectSuccess(serialNo, amount, result_msg);
                            logger.debug(code + "代收成功结果处理成功");
                        } else if ("20".equals(status)) {
                            result_msg = "扣款失败";
                            String code = collectFail(serialNo, result_code, "最终结果为：" + result_msg, desc);
                            logger.debug(code + "代收失败结果处理成功");
                        }
                    } else {
                        //交易失败
                        String code = collectFail(serialNo, result_code, "", result_msg);
                        logger.debug(code + "代收失败结果处理成功");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }

    @Override
    public NoteResult netQueryOrder(String serNo) {
        NoteResult result = new NoteResult("201", "失败");
        //请求第三方查询
        try {
            String response = CollectUtil.queryOrder(serNo);
            JSONObject res = JSONObject.parseObject(response);
            String result_code = res.getString("result_code");
            String result_msg = res.getString("result_msg");
            if ("0000".equals(result_code)) {//第三方受理成功
                String status = res.getString("status");

                if ("00".equals(status)) {//交易成功
                    logger.debug(".net单独查询订单" + serNo + "结果：代收处理成功" + result_code);
                    result.setCode(result_code);
                    result.setInfo(result_msg);
                    result.setData("s");
                } else if ("20".equals(status)) {//交易失败
                    String desc = res.getString("desc");
                    logger.debug(".net单独查询订单" + serNo + "结果：代收处理失败" + result_code);
                    result.setCode(result_code);
                    result.setInfo(result_msg + "," + desc);
                    result.setData("f");
                }
            } else if ("2010".equals(result_code)) {//交易不存在   改状态为f
                logger.debug(".net单独查询订单" + serNo + "结果：交易不存在" + result_code);
                result.setCode("0000");
                result.setInfo(result_msg);
                result.setData("n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new NoteResult("201", "通讯失败");
        }
        return result;
    }

    @Override
    public NoteResult askCollectionBatch(List<BatchCollectEntity> param) {

//        if(!"on".equals(isTest)){
//            return new NoteResult("9999", "失败");
//        }

        logger.error("batch请求参数：" + JSONObject.toJSONString(param));
        NoteResult result = new NoteResult("9999", "系统繁忙");

        logger.error("请求批量代扣开始时间" + System.currentTimeMillis());

        BatchCollectTask batchCollectTask = new BatchCollectTask(codeValueMapper, orderMapper, bankMapper, jedisCluster,ysbpayService);

        batchCollectTask.setEntitys(param);
        threadPool.submit(batchCollectTask);

        // 执行完毕 返回200
        result.setCode("200");
        result.setInfo("成功");

        logger.error("批量代扣结束时间" + System.currentTimeMillis());
        return result;
    }

    @Override
    public String testCallback(String orderId) {
        if (!"on".equals(isTest)) {
            //不是测试环境
            return "go away!";
        }


        if ("01".equals(orderId.substring(0, 2))) {
            // -----------模拟银生宝放款回调接口--------------------
            String amount = "425";
            String result_code = "0000";
            String result_msg = "薛测试回调";
            String key = "123456abc";
            StringBuffer macStr = new StringBuffer("accountId=" +
                    "1120161026094353001");
            macStr.append("&").append("orderId=" + orderId);
            macStr.append("&").append("amount=" + amount);
            macStr.append("&").append("result_code=" + result_code);
            macStr.append("&").append("result_msg=" + result_msg);
            macStr.append("&").append("key=" + key);
            System.out.println(macStr.toString());
            String mac1 = MD5Util.encodeToMd5(macStr.toString()).toUpperCase();
            Map<String, String> param = new HashMap<String, String>();
            param.put("orderId", orderId);
            param.put("amount", amount);
            param.put("result_code", result_code);
            param.put("result_msg", result_msg);
            param.put("mac", mac1);
            String res =
                    HttpUrlPost.sendPost("http://uatnew.youmishanjie.com/loan-web/callback/payContCallBack.action",
                            param);
            return res;
        } else {
            // --------------模拟银生宝还款回调---------------------
            String amount = "425";
            String result_code = "0000";
            String result_msg = "薛测试回调";
            String key = "123456abc";
            StringBuffer macStr = new StringBuffer("accountId=" + "1120161026094353001");
            macStr.append("&").append("orderId=" + orderId);
            macStr.append("&").append("amount=" + amount);
            macStr.append("&").append("result_code=" + result_code);
            macStr.append("&").append("result_msg=" + result_msg);
            macStr.append("&").append("key=" + key);
            System.out.println(macStr.toString());
            String mac1 = MD5Util.encodeToMd5(macStr.toString()).toUpperCase();
            Map<String, String> param = new HashMap<String, String>();
            param.put("orderId", orderId);
            param.put("amount", amount);
            param.put("result_code", result_code);
            param.put("result_msg", result_msg);
            param.put("mac", mac1);
            String res = HttpUrlPost.sendPost("http://uatnew.youmishanjie.com/loan-web/callback/callbackBackground.action",
                    param);
            return res;
        }
    }

    @Override
    @Transactional
    public NoteResult orderStatus(String serialNo) {
        NoteResult result = new NoteResult("10", "处理中");
        //请求第三方查询
        try {
            String response = CollectUtil.queryOrder(serialNo);
            JSONObject res = JSONObject.parseObject(response);
            String result_code = res.getString("result_code");
            String result_msg = res.getString("result_msg");
            String amount = res.getString("amount");
            String desc = res.getString("desc");

            //------------------------模拟请求第三方-------------------
            if ("on".equals(isTest)) {
                //成功失败随机出现
//                Random ran = new Random();
//                if (ran.nextInt(8)>1){
//                    result_code = "0000";
//                }else{
//                    result_code = "1111";
//                }
                result_code = "0000";
                result_msg = "模拟第三方";
                desc = "测试模拟第三方";
            }
            //-----------------------------------------------------
            if ("0000".equals(result_code)) {//第三方受理成功
                String status = res.getString("status");
                if ("on".equals(isTest)) {
                    //三种结果随机出现
//                    Random random = new Random();
//                    int statusFlag = random.nextInt(8);
//                    if (statusFlag == 0) {
//                        status = "20";
//                    }else {
//                        status = "00";
//                    }
                    status = "00";
                }
                if ("00".equals(status)) {//交易成功
                    String code = collectSuccess(serialNo, amount, result_msg);
                    logger.info(code + "代收成功结果处理成功");
                    result.setCode("00");
                    result.setInfo(result_msg);
                } else if ("20".equals(status)) {
                    String code = collectFail(serialNo, result_code, result_msg, desc);
                    logger.info(code + "代收失败结果处理成功");
                    result.setCode("20");
                    result.setInfo("最终结果为：" + result_msg);
                }
            } else {//交易失败
                String code = collectFail(serialNo, result_code, result_msg, desc);
                logger.info(code + "代收失败结果处理成功");
                result.setCode("20");
                result.setInfo(result_msg);
            }


        } catch (Exception e) {
            e.printStackTrace();
            return new NoteResult("20", "系统出了点问题");
        }
        return result;
    }


    public static void main(String[] args) {
        BatchCollectEntity entity = new BatchCollectEntity();


        entity.setPerId("6319820");
        entity.setBorrId("5490469");
        entity.setOptAmount("100");
        entity.setBankNum("6212263400005743462");
        entity.setPhone("13840841042");
        entity.setDescription("测试");

        String json = JSONObject.toJSONString(entity);
        System.out.println(json);
    }


}
