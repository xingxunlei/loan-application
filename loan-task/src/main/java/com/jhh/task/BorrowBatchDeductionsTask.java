package com.jhh.task;

import com.jhh.service.BorrowBatchDeductionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by wanzezhong on 2018/4/2.
 */
@Component
public class BorrowBatchDeductionsTask {

    @Autowired
    BorrowBatchDeductionsService borrowBatchDeductionsService;

    /**
     * 全量新增批量扣款状态
     */
    public void creatFullDeductionsStatus(){
        borrowBatchDeductionsService.creatFullDeductionsStatus();
    }

    /**
     * 更新农行用户批量扣款状态
     */
    public void saveAgriculturalBankStatus(){
        borrowBatchDeductionsService.saveAgriculturalBankStatus();
    }

    /**
     * 每日更新批量扣款状态
     */
    @Scheduled(cron = "0 0 6 * * ? ")
    public void saveDeductionsStatus(){
        borrowBatchDeductionsService.saveDeductionsStatus();
    }

}
