package com.loan.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.jhh.settlementsoa.model.request.GuestRepaySettlementRequest;
import com.jhh.settlementsoa.model.response.GuestRepaySettlementResponse;
import com.jhh.settlementsoa.rmi.RMISettlementService;
import com.jhh.settlementsoa.util.ResponseUtil;
import com.loan.payment.lakala.exception.LakalaClientException;
import com.loan.payment.lakala.util.DateUtil;
import com.loan.payment.lakala.util.LakalaCrossPayEnv;
import com.loan.payment.mapper.*;
import com.loan.payment.service.LakalaPayService;
import com.loan.payment.service.RedisService;
import com.loan_entity.app.*;
import com.loan_entity.common.Constants;
import com.loan_entity.enums.CertType;
import com.loan_entity.enums.CrossBorderBizType;
import com.loan_entity.enums.LakalaCurrency;
import com.loan_entity.lakala.LakalaCrossPayEncryptRequest;
import com.loan_entity.lakala.gather.LakalaGatherRequest;
import com.loan_entity.lakala.gather.LakalaGatherResponse;
import com.loan_entity.manager.Order;
import com.loan_entity.payment.Gather;
import com.loan_utils.util.BorrNum_util;
import com.loan_utils.util.SerialNumUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LakalaPaymentServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(LakalaPaymentServiceImpl.class);

    @Autowired
    private LakalaPayService payService;
    @Autowired
    private JedisCluster jedisCluster;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RepaymentPlanMapper repaymentPlanMapper;
    @Autowired
    private RMISettlementService rmiSettlementService;
    @Autowired
    private BankInfoMapper bankMapper;
    @Autowired
    private BorrowListMapper borrowListMapper;
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private CardMapper cardMapper;

    /**
     * 处理单个代扣
     * @param gather 代扣信息
     * @param fee 手续费
     * @return 处理结果
     */
    public NoteResult doSigleGatherByLakala(Gather gather,String fee){
        NoteResult result = new NoteResult(Constants.CODE_201,"处理异常");
        //拉卡拉不支持的银行
        if("12".equals(gather.getBankId()) || "11".equals(gather.getBankId()) || "7".equals(gather.getBankId())){
            result.setInfo("拉卡拉不支持该银行代扣!");
            return result;
        }

        logger.info("===============>>>>>>>>>>>>>>>>>开始代收,请求参数:{}",gather.toString());
        NoteResult canGather = checkOrder(gather);
        if(canGather != null && Constants.CODE_200.equals(canGather.getCode())){
            //保存主订单
            Order parentOrder = createParentOrder(gather);
            int parentOrderInsert = orderMapper.insertSelective(parentOrder);
            if(parentOrderInsert > 0){//保存成功
                //保存子订单
                Order feeOrder = createFeeOrder(fee, parentOrder);
                int feeOrderInsert = orderMapper.insertSelective(feeOrder);

                if(feeOrderInsert > 0){////子订单保存成功
                    logger.info("===============>>>>>>>>>>>>>>>>>单笔代收,构建请求参数");
                    LakalaGatherRequest req = createRequest(gather);
                    LakalaCrossPayEncryptRequest head = createPayEncrypt();
                    try{
                        logger.info("===============>>>>>>>>>>>>>>>>>向拉卡拉发起请求");
                        LakalaGatherResponse response =  payService.gather(req,head);
                        if(response != null){
                            String retMsg = response.getRetMsg();
                            logger.error("实时返回结果:{}",response.toString());
                            if("0000".equals(response.getRetCode())){//返回支付状态
                                //请求清结算接口
                                GuestRepaySettlementRequest request = createRepaySettlement(gather, parentOrder, retMsg);
                                ResponseUtil<GuestRepaySettlementResponse> settlementResponse = null;
                                try {
                                    logger.info("拉卡拉跨境支付-发起清结算!");
                                    settlementResponse = rmiSettlementService.guestRepaySettlement(request);
                                    logger.info("拉卡拉跨境支付-清结算调用结果：{}",settlementResponse.toString());
                                    if(settlementResponse == null){
                                        //将订单更新为失败.
                                        updateOrderStatus(parentOrder, feeOrder, "清结算调用失败", "m");
                                        logger.info("拉卡拉跨境支付-单笔实时收款成功,清结算调用失败!实时参数:{}",response.toString());
                                        result.setInfo("拉卡拉跨境支付-单笔实时收款成功,清结算调用失败!");
                                    }
                                    if(settlementResponse.getStatus() != 1){
                                        //将订单更新为失败.
                                        updateOrderStatus(parentOrder, feeOrder, "清结算处理失败,原因:"+settlementResponse.getMessage(), "f");
                                        logger.info("拉卡拉跨境支付-单笔实时收款成功,清结算调用成功但处理失败,原因:{}!实时参数:{}",settlementResponse.getMessage(),response.toString());
                                        result.setInfo("拉卡拉跨境支付-单笔实时收款成功,清结算调用成功但处理失败!");
                                    }else{
                                        logger.info("拉卡拉跨境支付-实时代扣成功!");
                                        //处理成功
                                        result.setCode("0000");
                                        result.setInfo("实时代扣成功!");
                                    }
                                }catch (Exception e){
                                    //请求出异常
                                    logger.info("拉卡拉跨境支付-单笔实时收款成功,清结算调用失败!实时参数:{}",response.toString());
                                    result.setInfo("拉卡拉跨境支付-单笔实时收款成功,清结算调用失败!");
                                }
                            }else{
                                //将订单更新为失败.
                                updateOrderStatus(parentOrder, feeOrder, retMsg, "f");
                                logger.info("===============>>>>>>>>>>>>>>>>>拉卡拉跨境支付失败,原因:{}",retMsg);
                                result.setInfo(retMsg);
                            }
                        }else{
                            result.setInfo("拉卡拉跨境支付-单笔实时收款异常,返回结果为空");
                        }
                    }catch (LakalaClientException e){
                        e.printStackTrace();
                        result.setInfo("拉卡拉跨境支付-单笔实时收款异常");
                    }
                }else{
                    result.setInfo("拉卡拉跨境支付-悠米手续费订单生成失败！");
                }
            }else{
                result.setInfo("拉卡拉跨境支付-悠米订单生成失败！");
            }
            return result;
        }else{
            return canGather;
        }
    }

    /**
     * 分页获取拉卡拉代扣的订单
     * @return
     */
    public List<Order> selectBatchGatherOrders(){
        List<Order> dealOrder = new ArrayList<Order>();
        PageHelper.offsetPage(0,200,false);
        List<Order> parentOrders = orderMapper.selectBatchGatherOrders();
        if(parentOrders != null && parentOrders.size() > 0){
            //取得对应的人原信息
            for(Order parnetOrder : parentOrders){
                Person person = personMapper.selectByPrimaryKey(parnetOrder.getPerId());
                if(person == null){
                    continue;
                }

                Card queryCard = new Card();
                queryCard.setPerId(parnetOrder.getPerId());
                Card personCard = cardMapper.selectOne(queryCard);
                if(personCard == null){
                    continue;
                }
                Bank queryBank = new Bank();
                queryBank.setPerId(parnetOrder.getPerId());
                queryBank.setStatus("1");
                List<Bank> banks = bankMapper.select(queryBank);
                if(banks == null){
                    continue;
                }

                Bank bank = banks.get(0);
                BorrowList borrowList = borrowListMapper.selectByPrimaryKey(parnetOrder.getConctactId());
                if(borrowList == null){
                    continue;
                }

                parnetOrder.setUserName(personCard.getName());
                parnetOrder.setCardNnum(personCard.getCardNum());
                parnetOrder.setUserPhone(person.getPhone());
                parnetOrder.setBorrNum(borrowList.getBorrNum());
                parnetOrder.setBankNum(bank.getBankNum());

                dealOrder.add(parnetOrder);
            }
            return dealOrder;
        }
        return null;
    }

    /**
     * 处理批量代扣订单
     * @param batchOrders 批量代扣订单
     */
    public void doBatchGatherByLakala(List<Order> batchOrders){
        if(batchOrders != null && batchOrders.size() > 0){
            int success = 0, fail = 0;
            for(Order order : batchOrders){
                LakalaGatherRequest request = createRequest(order);
                LakalaCrossPayEncryptRequest head = createPayEncrypt();
                try{
                    logger.info("===============>>>>>>>>>>>>>>>>>向拉卡拉发起请求");
                    LakalaGatherResponse response =  payService.gather(request,head);
                    if(response != null){
                        String retMsg = response.getRetMsg();
                        logger.error("===============>>>>>>>>>>>>>>>>>实时返回结果:{}",response.toString());
                        if("0000".equals(response.getRetCode())){//返回支付状态
                            //请求清结算接口
                            GuestRepaySettlementRequest settlementRequest = createRepaySettlement(order, retMsg);
                            ResponseUtil<GuestRepaySettlementResponse> settlementResponse = null;
                            try {
                                settlementResponse = rmiSettlementService.guestRepaySettlement(settlementRequest);
                                if(settlementResponse == null){
                                    fail++;
                                    //将订单更新为失败.
                                    updateBatchOrderStatus(order, "清结算调用失败", "m");
                                    logger.info("===============>>>>>>>>>>>>>>>>>拉卡拉跨境支付-单笔实时收款成功,清结算调用失败!实时参数:{}",response.toString());
                                }
                                if(settlementResponse.getStatus() != 1){
                                    fail++;
                                    //将订单更新为失败.
                                    updateBatchOrderStatus(order, "清结算处理失败", "m");
                                    logger.info("===============>>>>>>>>>>>>>>>>>拉卡拉跨境支付-单笔实时收款成功,清结算调用成功但处理失败!实时参数:{}",response.toString());
                                }else{
                                    success++;
                                    //处理成功
                                    logger.info("===============>>>>>>>>>>>>>>>>>实时代扣成功，数据内容{}",order.toString());
                                }
                            }catch (Exception e){
                                //请求出异常
                                logger.error("===============>>>>>>>>>>>>>>>>>拉卡拉跨境支付-单笔实时收款成功,清结算调用失败!实时参数:{}",response.toString());
                            }
                        }else{
                            fail++;
                            //将订单更新为失败.
                            updateBatchOrderStatus(order, retMsg, "f");
                            logger.info("===============>>>>>>>>>>>>>>>>>拉卡拉跨境支付失败,原因:{}",retMsg);
                            logger.info(retMsg);
                        }
                    }else{
                        fail++;
                        logger.info("===============>>>>>>>>>>>>>>>>>拉卡拉跨境支付-单笔实时收款异常,返回结果为空");
                    }
                }catch (LakalaClientException e){
                    e.printStackTrace();
                    logger.info("===============>>>>>>>>>>>>>>>>>拉卡拉跨境支付-单笔实时收款异常");
                }
            }
            logger.info("本次共处理{}条订单，成功{}条，失败{}条",batchOrders.size(),success,fail);
        }
    }

    private void updateBatchOrderStatus(Order order, String retMsg, String f) {
        Order parentOrder = orderMapper.selectByPrimaryKey(order.getId());
        parentOrder.setReason(retMsg);
        parentOrder.setRlState(f);

        int updateParentCorderCount = orderMapper.updateByPrimaryKeySelective(parentOrder);
        if(updateParentCorderCount > 0){
            logger.info("===============>>>>>>>>>>>>>>>>>主订单状态更新成功,更新内容:{}",parentOrder.toString());

            Order subOrder = orderMapper.selectByPid(parentOrder.getId());
            subOrder.setReason(retMsg);
            subOrder.setRlState(f);
            int updateSarentCorderCount = orderMapper.updateByPrimaryKeySelective(subOrder);
            if(updateSarentCorderCount > 0){
                logger.info("===============>>>>>>>>>>>>>>>>>子订单状态更新成功,更新内容:{}",subOrder.toString());
            }
        }
    }

    private GuestRepaySettlementRequest createRepaySettlement(Order order, String retMsg) {
        GuestRepaySettlementRequest request = new GuestRepaySettlementRequest();
        request.setBorrNum(String.valueOf(order.getBorrNum()));
        request.setOrderId(order.getSerialNo());
        request.setStatus("s");
        request.setMsg(retMsg);
        return request;
    }

    private LakalaGatherRequest createRequest(Gather gather) {
        LakalaGatherRequest req = new LakalaGatherRequest();

        req.setCardNo(gather.getBankNum());
        req.setBgUrl("http://www.jinhuhang.com/");
        req.setBusiCode(CrossBorderBizType.CARGO_TRADE.getCode());
        req.setCertType(CertType.ID.getCode());
        req.setClientId(gather.getIdCardNo());
        req.setClientName(gather.getName());
        req.setCurrency(LakalaCurrency.CNY.getCode());
        req.setCustomNumberId("NA");
        req.setMerOrderId(gather.getSerNo());
        req.setMobile(gather.getPhone());
        req.setOrderAmount(gather.getOptAmount());
        req.setOrderEffTime(DateUtil.getCurrentDate("yyyyMMddHHmmss"));
        req.setOrderSummary("请求拉卡拉代收");
        req.setOrderTime(DateUtil.getCurrentDate("yyyyMMddHHmmss"));
        req.setPayeeAmount(gather.getOptAmount());

        return req;
    }

    private LakalaGatherRequest createRequest(Order order) {
        LakalaGatherRequest req = new LakalaGatherRequest();
        req.setCardNo(order.getBankNum());
        req.setBgUrl("http://www.jinhuhang.com/");
        req.setBusiCode(CrossBorderBizType.CARGO_TRADE.getCode());
        req.setCertType(CertType.ID.getCode());
        req.setClientId(order.getCardNnum());
        req.setClientName(order.getUserName());
        req.setCurrency(LakalaCurrency.CNY.getCode());
        req.setCustomNumberId("NA");
        req.setMerOrderId(order.getSerialNo());
        req.setMobile(order.getUserPhone());
        req.setOrderAmount(order.getRealOptAmount());
        req.setOrderEffTime(DateUtil.getCurrentDate("yyyyMMddHHmmss"));
        req.setOrderSummary("请求拉卡拉代收");
        req.setOrderTime(DateUtil.getCurrentDate("yyyyMMddHHmmss"));
        req.setPayeeAmount(order.getRealOptAmount());

        return req;
    }

    private NoteResult checkOrder(Gather gather) {
        NoteResult result = new NoteResult();
        //清结算锁
        String settleLock = jedisCluster.get("ym_bt:st:power:switch");
        if (!StringUtils.isEmpty(settleLock) && "off".equals(settleLock)){
            result.setInfo("凌晨0点-5点开始为系统维护时间，期间不能进行还款，给您带来的不便敬请谅解！");
            return result;
        }

        //查看订单怎么提交
        Order repeat = orderMapper.selectByGuid(gather.getGuid());
        if(repeat!=null){//该订单已存在，不予操作
            return new NoteResult("9996","订单已提交，不可重复提交");
        }

        double total = 0.00 , thisAmount = Double.valueOf(gather.getOptAmount());

        if(thisAmount < 0){
            result.setCode("9995");
            result.setInfo("扣款金额不能为负数！");
            return result;
        }

        //查出该借款下所有状态为2，4，5且状态为p的订单
        Map<String, Object> queryMaps = new HashMap<String, Object>();
        queryMaps.put("borrNum",gather.getBorrId());
        queryMaps.put("rlState","p");
        List<Order> orders = orderMapper.selectPayOrders(queryMaps);
        Double alsoAmount = repaymentPlanMapper.selectAlsoRepay(gather.getBorrId());

        for (Order order : orders) {

            double optAmount = Double.parseDouble(order.getOptAmount());
            total = total + optAmount;

        }
        logger.error("本次提交borrId:" + gather.getBorrId() + ",本次提交金额：" + thisAmount + ",剩余应还金额：" + alsoAmount);

        // 如果状态为p的订单总额+本次订单金额大于应还金额 不允许还款及代扣
        if (total + thisAmount > alsoAmount) {
            double canPay = alsoAmount - total;
            result.setInfo("有正在处理中的还款，当前最多可以还款" + canPay + "元");
            return result;
        } else {
            result.setCode(Constants.CODE_200);
            return result;
        }
    }

    private LakalaCrossPayEncryptRequest createPayEncrypt() {
        LakalaCrossPayEncryptRequest head = new LakalaCrossPayEncryptRequest();
        head.setVer("1.0.0");
        head.setTs(DateUtil.getCurrentTime());
        head.setReqType("B0013");
        head.setPayTypeId("4");
        head.setMerId(LakalaCrossPayEnv.getEnvConfig().getMerId());
        return head;
    }

    private GuestRepaySettlementRequest createRepaySettlement(Gather gather, Order parentOrder, String retMsg) {
        GuestRepaySettlementRequest request = new GuestRepaySettlementRequest();
        request.setBorrNum(String.valueOf(gather.getBorrNum()));
        request.setOrderId(parentOrder.getSerialNo());
        request.setStatus("s");
        request.setMsg(retMsg);
        return request;
    }

    private Order createFeeOrder(String fee, Order parentOrder) {
        Order feeOrder = new Order();
        feeOrder.setGuid(BorrNum_util.createBorrNum());
        //手续费订单的pid为主订单id
        feeOrder.setpId(parentOrder.getId());
        feeOrder.setSerialNo(SerialNumUtil.createByType("03"));
        feeOrder.setPerId(parentOrder.getPerId());
        feeOrder.setCompanyId(parentOrder.getCompanyId());
        feeOrder.setConctactId(parentOrder.getConctactId());
        feeOrder.setOptAmount(fee);
        feeOrder.setActAmount(fee);
        feeOrder.setRlState("p");
        feeOrder.setType("3");//手续费
        return feeOrder;
    }

    private Order createParentOrder(Gather gather) {
        Order parentOrder = new Order();
        parentOrder.setGuid(gather.getGuid());
        parentOrder.setpId(0);
        parentOrder.setSerialNo(gather.getSerNo());
        parentOrder.setPerId(gather.getPerId());
        parentOrder.setCompanyId(1);
        parentOrder.setBankId(Integer.valueOf(gather.getBankInfoId()));
        parentOrder.setConctactId(Integer.valueOf(gather.getBorrId()));
        parentOrder.setOptAmount(gather.getOptAmount());
        parentOrder.setActAmount(gather.getOptAmount());
        if (StringUtils.isNotEmpty(gather.getDescription())) {
            parentOrder.setRlRemark(gather.getDescription());
        }
        parentOrder.setRlState("p");
        parentOrder.setType("9");// 代收
        parentOrder.setCreateUser(gather.getCreateUser());
        parentOrder.setCollectionUser(gather.getCollectionUser());
        return parentOrder;
    }

    private void updateOrderStatus(Order parentOrder, Order feeOrder, String retMsg, String m) {
        //将订单更新为失败.
        parentOrder.setRlState(m);
        parentOrder.setRlRemark(retMsg);
        int parentOrderUpdate = orderMapper.updateByPrimaryKey(parentOrder);
        if(parentOrderUpdate > 0){
            logger.info("===============>>>>>>>>>>>>>>>>>拉卡拉跨境支付,parentOrderUpdateCount:{}",parentOrderUpdate);
            feeOrder.setRlState(m);
            feeOrder.setRlRemark(retMsg);
            int feeOrderUpdate = orderMapper.updateByPrimaryKey(feeOrder);
            if(feeOrderUpdate > 0){
                logger.info("===============>>>>>>>>>>>>>>>>>拉卡拉跨境支付,feeOrderUpdate:{}",feeOrderUpdate);
            }
        }
    }
}
