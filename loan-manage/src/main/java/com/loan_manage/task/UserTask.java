package com.loan_manage.task;

import com.loan_manage.service.BorrowListService;
import com.loan_manage.service.MasterService;
import com.loan_manage.service.PersonService;
import com.loan_manage.service.RedisService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserTask {
	private Logger logger = LoggerFactory.getLogger(UserTask.class);
	@Autowired
	private PersonService personService;
	@Autowired
	private RedisService redisService;
	@Autowired
	private BorrowListService borrowListService;
	@Autowired
	private MasterService masterService;

	//@Scheduled(fixedRate = 1000 * 60 * 60 * 24)
	/*public void syncUser(){
		try {
			logger.info("=============>同步OA信息开始");
			redisService.syanLoginUser();
			logger.info("=============>同步OA信息结束");
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}*/

	//@Scheduled(fixedRate = 1000 * 60 * 60 * 24)
	/*public void syncOa(){
		logger.info("=============>进入贷后催收人员同步");
		try{
			personService.syncOa();
		}catch (Exception e){
			logger.info("=============>贷后催收人员同步失败"+e.getMessage());
		}
	}*/

	@Scheduled(cron = "0 0 20 * * ? ") // 晚上八点开始
	public void rejectAudit() {
		if(masterService.isMaster()) {
			logger.info("rejectAudit start");
			borrowListService.rejectAudit();
		}
	}
	/**
	 * 机器人上午打电话
	 */
	@Scheduled(cron = "0 0 9 * * ? ") // 早上9点开始
	public void rcCallPhone() {
		if(masterService.isMaster()) {
			logger.info("rcCallPhone start");
			borrowListService.rcCallPhone();
		}
	}

	//@Scheduled(fixedRate = 1000 * 60 * 60 * 24)
	/*public void syncLevelBack(){
		logger.info("=============>同步OA人员");
		personService.syncLevelBack();
		logger.info("=============>同步OA人员结束");
	}*/
}
