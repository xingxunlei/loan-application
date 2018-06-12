package com.loan_manage.task;

import com.loan_entity.common.Constants;
import com.loan_manage.service.MasterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import redis.clients.jedis.JedisCluster;

@Component
public class CleanCacheTask {

    @Autowired
    private MasterService masterService;

    @Autowired
    private JedisCluster jedisCluster;

    private Logger logger = LoggerFactory.getLogger(CleanCacheTask.class);

    private static final String KEY = Constants.YM_ADMIN_SYSTEN_KEY + Constants.COLLECTORS_USERID;
    ;

    @Scheduled(cron = "0 0 0 * * ? ") // 晚上0点开始
//	@Scheduled(cron = "0 0/1 * * * ? ")
    public void cleanCollectorsUserid() {
        if (masterService.isMaster()) {
            if (logger.isDebugEnabled()) {
                logger.info("=====================================task run=====================================");
            }
            jedisCluster.del(KEY);
        }
    }

}
