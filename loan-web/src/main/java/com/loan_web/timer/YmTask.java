package com.loan_web.timer;

import com.loan_api.app.DrainageService;
import com.loan_api.app.UserService;
import com.loan_api.contract.ElectronicContractService;
import com.loan_api.loan.YsbCollectionService;
import com.loan_api.loan.YsbpayService;
import com.loan_web.service.MasterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.loan_api.loan.YsbCollectionService;
import com.loan_api.loan.YsbpayService;

import javax.annotation.PostConstruct;

/**
 * 定时任务
 *
 * @author xuepengfei
 *         2016年11月15日上午9:09:29
 */
@Component
public class YmTask {
    @Autowired
    private YsbCollectionService ysbCollectionService;

    @Autowired
    private YsbpayService ysbService;

    @Autowired
    private UserService userService;

    @Autowired
    private MasterService masterService;

//    @Autowired
//    private DelayQueueService delayQueueService;

    @Autowired
    private DrainageService mDrainageService;

    @Autowired
    ElectronicContractService electronicContractService;

    private static final Logger logger = LoggerFactory
            .getLogger(YmTask.class);


    @Scheduled(fixedRate = 5 * 60 * 1000) // 间隔30分钟执行   测试间隔5分钟
    public void taskCycle() {
        if (masterService.isMaster()) {
            logger.info("YmTask:查询代扣");
            ysbCollectionService.queryCollectStatus();
        }
    }

    /**
     * 半个小时查询一次放款中的信息，对放款成功和失败的进行操作。
     */
    @Scheduled(fixedRate = 5 * 60 * 1000) // 间隔30分钟执行  测试间隔5分钟
    public void queryCall() {
        if (masterService.isMaster()) {
            logger.info("YmTask:查询放款");
            ysbService.queryCall();
        }
    }

    /**
     * 半个小时查询一次认证支付中的信息，对认证支付成功和失败的进行操作。
     */
    @Scheduled(fixedRate = 5 * 60 * 1000) // 间隔30分钟执行  测试间隔5分钟
    public void queryPayment() {
        if (masterService.isMaster()) {
            logger.info("YmTask:查询主动还款");
            ysbService.queryPayment();
        }
    }

//    @PostConstruct
//    public void orderNotExistTask() {
//        logger.info("YmTask:检查订单不存在的任务");
//        if (masterService.isMaster()) {
//            logger.info("YmTask:查询代扣");
//            ysbCollectionService.queryCollectStatus();
//        }
//    }
//
//    /**
//     * 半个小时查询一次放款中的信息，对放款成功和失败的进行操作。
//     */
//    @Scheduled(fixedRate = 5 * 60 * 1000) // 间隔30分钟执行  测试间隔5分钟
//    public void queryCall() {
//        if (masterService.isMaster()) {
//            logger.info("YmTask:查询放款");
//            ysbService.queryCall();
//        }
//    }
//
//    /**
//     * 半个小时查询一次认证支付中的信息，对认证支付成功和失败的进行操作。
//     */
//    @Scheduled(fixedRate = 5 * 60 * 1000) // 间隔30分钟执行  测试间隔5分钟
//    public void queryPayment() {
//        if (masterService.isMaster()) {
//            logger.info("YmTask:查询主动还款");
//            ysbService.queryPayment();
//        }
//    }
//
////    @PostConstruct
////    public void orderNotExistTask() {
////        logger.info("YmTask:检查订单不存在的任务");
////        if (masterService.isMaster()) {
////            delayQueueService.transferFromDelayQueue();
////        }
////    }
//
    /**
     * 同步白名单数据
     */
    @Scheduled(cron = "0 0 8 * * ? ") // 早上8点开始
    public void whiteListSync() {
        if (masterService.isMaster()) {
            logger.info("whiteListSync start");
            userService.syncWhiteList();
        }
    }

    /**
     * 同步所有的白名单手机数据
     */
    @Scheduled(cron = "0 0 8 * * ? ") // 早上8点开始
    public void whiteListPhoneSync() {
        if (masterService.isMaster()) {
            logger.info("whiteListPhoneSync start");
            userService.syncPhoneWhiteList();
        }
    }

    /**
     * 处理异常电子合同数据
     */
    @Scheduled(fixedRate = 5 * 60 * 1000) // 间隔30分钟执行  测试间隔5分钟
    public void disposeExceptionContract() {
        if (masterService.isMaster()) {
            logger.info("disposeExceptionContract start");
            electronicContractService.disposeExceptionContract();
        }
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void draginageTask(){
        if (masterService.isMaster()) {
            mDrainageService.syncDrainage();
        }
    }

    /**
     * 同步白名单数据
     */
//    @Scheduled(cron = "0 0 8 * * ? ") // 早上8点开始
//    public void whiteListSync() {
//        if (masterService.isMaster()) {
//            logger.info("whiteListSync start");
//            userService.syncWhiteList();
//        }
//    }

    /**
     * 同步所有的白名单手机数据
     */
    //@Scheduled(cron = "0 0 8 * * ? ") // 早上8点开始
//    public void whiteListPhoneSync() {
//        if (masterService.isMaster()) {
//            logger.info("whiteListPhoneSync start");
//            userService.syncPhoneWhiteList();
//        }
//    }

//    @Scheduled(fixedRate = 10 * 60 * 1000)
//    public void draginageTask(){
//        if (masterService.isMaster()) {
//            mDrainageService.syncDrainage();
//        }
//    }
}
