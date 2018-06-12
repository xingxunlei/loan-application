package com.jhh.task;

import com.jhh.service.ShellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *  贝壳钱包 审核失败定时任务
 */
@Component
public class ShellTask {

    @Autowired
    private ShellService shellService;

    /**
     *  贝壳钱包定时任务 审核失败
     */
    @Scheduled(fixedRate = 10*60*1000) // 间隔10分钟执行
    public void ApplyFail() throws Exception{
        shellService.applyFail();
    }
}
