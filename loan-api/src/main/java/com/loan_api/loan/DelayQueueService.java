package com.loan_api.loan;

import com.loan_entity.loan_vo.NotExistOrder;

/**
 * Created by xuepengfei on 2017/11/15.
 */
public interface DelayQueueService {

    public void transferFromDelayQueue();

    public void addToDeplayQueue(String serialNo, int type, int times);
}
