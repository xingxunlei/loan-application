package com.loan_manage.task;

import com.loan_manage.service.MasterService;
import com.loan_manage.service.PageQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 针对页面查询优化的task
 */
@Component
public class PageQueryTask {

    private Logger logger = LoggerFactory.getLogger(PageQueryTask.class);

    @Autowired
    private PageQueryService pageQueryService;
    @Autowired
    private MasterService masterService;

    /**每隔10min刷新一次贷后管理特定参数的总条数**/
//    @Scheduled(fixedRate = 1000 * 60 * 10)
    public void initLoanManagementInfoItem(){
        if(masterService.isMaster()){
            logger.info("=============>>>>>>>>>>>>>>>>>{}贷后管理总条数查询开始<<<<<<<<<<<=============",new Date());
            pageQueryService.initLoanManagementInfoItem();
            logger.info("=============>>>>>>>>>>>>>>>>>{}贷后管理总条数查询结束<<<<<<<<<<<=============",new Date());
        }
    }

    /**每隔10min刷新一次还款流水特定参数的总条数**/
//    @Scheduled(fixedRate = 1000 * 60 * 10)
    public void initRepaymentInfoItem(){
        if(masterService.isMaster()) {
            logger.info("=============>>>>>>>>>>>>>>>>>{}还款流水总条数查询开始<<<<<<<<<<<=============", new Date());
            pageQueryService.initRepaymentInfoItem();
            logger.info("=============>>>>>>>>>>>>>>>>>{}还款流水总条数查询结束<<<<<<<<<<<=============", new Date());
        }
    }

    /**每隔10min刷新一次还款流水特定参数的总条数**/
//    @Scheduled(fixedRate = 1000 * 60 * 10)
    public void initRepaymentPlanInfoItem(){
        if(masterService.isMaster()) {
            logger.info("=============>>>>>>>>>>>>>>>>>{}还款计划总条数查询开始<<<<<<<<<<<=============", new Date());
            pageQueryService.initRepaymentPlanInfoItem();
            logger.info("=============>>>>>>>>>>>>>>>>>{}还款计划总条数查询结束<<<<<<<<<<<=============", new Date());
        }
    }

    /**每隔10min刷新一次催收信息特定参数的总条数**/
//    @Scheduled(fixedRate = 1000 * 60 * 10)
    public void initCollectorsInfoItem(){
        if(masterService.isMaster()) {
            logger.info("=============>>>>>>>>>>>>>>>>>{}催收信息总条数查询开始<<<<<<<<<<<=============", new Date());
            pageQueryService.initCollectorsInfoItem();
            logger.info("=============>>>>>>>>>>>>>>>>>{}催收信息总条数查询结束<<<<<<<<<<<=============" + new Date());
        }
    }

    /**每隔10min刷新一次审核管理特定参数的总条数**/
//    @Scheduled(fixedRate = 1000 * 60 * 10)
    public void initAuthManageInfoItem(){
        if(masterService.isMaster()) {
            logger.info("=============>>>>>>>>>>>>>>>>>{}审核管理总条数查询开始<<<<<<<<<<<=============", new Date());
            pageQueryService.initAuthManageInfoItem();
            logger.info("=============>>>>>>>>>>>>>>>>>{}审核管理总条数查询结束<<<<<<<<<<<=============", new Date());
        }
    }
}
