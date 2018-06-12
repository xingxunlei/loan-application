package com.jhh.task;

import com.jhh.service.TimerService;
import com.jhh.util.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wanzezhong on 2017/8/22.
 * 还款消息定时
 */
@Component
public class PaymentMessageTask {

    @Autowired
    private TimerService timerService;


    /**
     * 每天上午10点对当天和明天要还款的人进行短信提醒
     */
    @Scheduled(cron = "0 0 10 * * ? ")
//    @Scheduled(cron = "0 0/5 * * * ? ")
    public void smsAlert() {
        timerService.smsAlert();
    }

    /**
     * 每天定时给贷后发邮件 逾期数据RobotData
     */
    @Scheduled(cron = "0 0 08 * * ? ")
//    @Scheduled(cron = "0 0/1 * * * ? ")// 间隔30分钟执行  测试间隔5分钟
    public void sendRobotData() {
        System.out.println("机器人数据定时任务");
        timerService.sendRobotData();

    }

    /**
     * 每天定时给资金管理发邮件 每天早上9点发前一天的放款数据
     */
    @Scheduled(cron = "0 0 09 * * ? ")
//    @Scheduled(cron = "0 0/1 * * * ? ")
    public void sendMoneyManagement() {
        System.out.println("财务报表定时任务");
        timerService.sendMoneyManagement();

    }

    /**
     * 每天定时给资金管理发邮件 海尔每日还款流水给财务
     */
    @Scheduled(cron = "0 0 09 * * ? ")
//    @Scheduled(cron = "0 0/1 * * * ? ")
    public void sendHaierOrder() {
        System.out.println("财务海尔报表定时任务");
        timerService.sendHaierOrder();

    }
}
