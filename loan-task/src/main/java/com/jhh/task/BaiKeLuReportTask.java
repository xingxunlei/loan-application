package com.jhh.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jhh.service.BaiKeLuService;

@Component
public class BaiKeLuReportTask {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private BaiKeLuService baiKeLuService;
	
	/**
     * 每天定时发送百可录报表
     */
    @Scheduled(cron = "0 0 09 ? * 2")
//    @Scheduled(cron = "0 0/10 * * * ? ")
    public void sendReport() {
    		if(logger.isDebugEnabled()) {
    			logger.debug("sendBaiKeLuReport now------------------------------------------");
    		}
        baiKeLuService.sendReport();
    }

}
