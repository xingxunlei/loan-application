package com.loan.payment.task;

import com.loan.payment.lakala.util.DateUtil;
import com.loan.payment.service.impl.LakalaPaymentServiceImpl;
import com.loan_entity.manager.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import scala.collection.LinearSeq;

import java.util.List;

/**
 * 批量代扣定时任务
 */
@Component
public class BatchGatherSchedule {

    private static final Logger logger = LoggerFactory.getLogger(BatchGatherSchedule.class);

    @Autowired
    private LakalaPaymentServiceImpl paymentService;

    @Scheduled(fixedDelay = 1000 * 60 * 5)   //每10分钟执行一次
    public void startBatchGather(){

        logger.info("================>>>>>>>>>>>{}进入批量代扣定时任务。", DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));

        doBatchGatherSchedule();

        logger.info("================>>>>>>>>>>>{}结束批量代扣定时任务。", DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
    }

    public void doBatchGatherSchedule(){

        List<Order> batchOrders = paymentService.selectBatchGatherOrders();
        if(batchOrders != null && batchOrders.size() > 0){//查询到数据,立即处理
            logger.info("查询到符合条件的订单{}条，内容是：{}",batchOrders.size(),batchOrders.toString());
            paymentService.doBatchGatherByLakala(batchOrders);
        }
        logger.info("未查询到符合条件的订单");
    }
}
