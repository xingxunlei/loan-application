package com.loan_manage.task;

import com.loan_manage.service.AutoTranService;
import com.loan_manage.service.MasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 自动转件task，定时将催收人为特殊的单子均分到风控和客服部门
 *
 * @author wuhanhong
 * @date 2017 - 11 - 14
 */
@Component
public class AutoTranTask {
    @Autowired
    private AutoTranService autoTranService;
    @Autowired
    private MasterService masterService;

    @Scheduled(cron = "0 0 12 * * ? ") // 每天中午12点
    public void excetueTran() {
        try {
            if(masterService.isMaster()) {
                autoTranService.autoTran();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
