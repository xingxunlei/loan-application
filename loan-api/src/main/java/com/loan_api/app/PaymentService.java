package com.loan_api.app;

import com.loan_entity.app.NoteResult;
import com.loan_entity.loan_vo.BatchCollectEntity;
import com.loan_entity.payment.Gather;

import java.util.List;

/**
 * 支付接口
 */
public interface PaymentService {

    /**
     * 单笔代收
     * @param gather
     * @return
     */
    NoteResult sigleGatherByLakala(Gather gather);

    /**
     * 批量代收
     * @return
     */
    NoteResult batchGatherByLakala(List<BatchCollectEntity> gathers);
}
