package com.loan_api.app;

import com.loan_entity.app.NoteResult;
import com.loan_entity.drainage.DrainageStat;

/**
 * Created by zhangqi on 17-11-20.
 */
public interface DrainageService {

    /**
     * 拿到所有展示的引流渠道产品
     * @param per_id
     * @return
     */
    NoteResult getDrainage(String per_id);

    String drainageEvent(DrainageStat drainageStat);

    void syncDrainage();
}
