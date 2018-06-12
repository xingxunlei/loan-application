package com.loan.payment.service.impl;

import com.loan.payment.mapper.BankInfoMapper;
import com.loan.payment.mapper.OrderMapper;
import com.loan.payment.service.RedisService;
import com.loan_api.app.PaymentService;
import com.loan_entity.app.Bank;
import com.loan_entity.app.NoteResult;
import com.loan_entity.common.Constants;
import com.loan_entity.loan_vo.BatchCollectEntity;
import com.loan_entity.manager.Order;
import com.loan_entity.payment.Gather;
import com.loan_utils.util.BorrNum_util;
import com.loan_utils.util.SerialNumUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    private LakalaPaymentServiceImpl lakalaPaymentService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private BankInfoMapper bankInfoMapper;
    @Autowired
    private OrderMapper orderMapper;

    public NoteResult sigleGatherByLakala(Gather gather) {
        logger.info("===============>>>>>>>>>>>>>>>>>进入代收");
        String fee = redisService.selectGatherFee();
        return lakalaPaymentService.doSigleGatherByLakala(gather,fee);
    }

    public NoteResult batchGatherByLakala(List<BatchCollectEntity> gathers) {
        NoteResult result = new NoteResult("9999", "批量代收错误");
        logger.info("===============>>>>>>>>>>>>>>>>>进入批量代收");
        String fee = redisService.selectGatherFee();
        try{
            int countOrder = 0,countSubOrder = 0;
            for(BatchCollectEntity entity : gathers){

                //拉卡拉不支持的银行
                if("12".equals(entity.getBankId()) || "11".equals(entity.getBankId()) || "7".equals(entity.getBankId())){
                    continue;
                }

                String serNo = SerialNumUtil.createByType("04");
                Order order = new Order();
                order.setGuid(entity.getGuid());
                // 主订单 pid设置为0 没有父订单
                order.setpId(0);
                order.setSerialNo(serNo);
                order.setPerId(Integer.valueOf(entity.getPerId()));
                order.setCompanyId(1);
                order.setBankId(Integer.valueOf(entity.getBankInfoId()));
                order.setConctactId(Integer.valueOf(entity.getBorrId()));
                double opt = Double.valueOf(entity.getOptAmount());
                String amount = String.format("%.2f", opt);
                order.setOptAmount(amount);
                order.setActAmount(amount);
                if (StringUtils.isNotEmpty(entity.getDescription())) {
                    order.setRlRemark(entity.getDescription());
                }
                order.setCreateUser(entity.getCreateUser());
                order.setCollectionUser(entity.getCollectionUser());
                order.setDeductionsType(entity.getDeductionsType());
                order.setRlState("p");
                order.setType("10");//批量

                int countOrderInsert = orderMapper.insertSelective(order);
                if(countOrderInsert > 0){
                    logger.info("生成主订单,订单内容:{}",order.toString());
                    countOrder = countOrder + countOrderInsert;

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

                    int countInsertSubOrder = orderMapper.insertSelective(feeOrder);
                    if(countInsertSubOrder > 0){
                        logger.info("生成子订单,订单内容:{}",order.toString());
                        countSubOrder = countSubOrder + countInsertSubOrder;
                    }
                }else{
                    continue;//主订单插入失败,不处理
                }
            }
            result.setCode(Constants.CODE_200);
            result.setInfo("批量代扣受理成功:生成代扣订单"+countOrder+"条,代扣手续费订单:"+countSubOrder+"条,请于半小时左右查询结果。");
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Constants.CODE_201);
            result.setInfo("批量代扣受理失败");
        }
        return result;
    }
}
