package com.loan.payment.service;

import com.loan.payment.lakala.exception.LakalaClientException;
import com.loan_entity.lakala.LakalaCrossPayEncryptRequest;
import com.loan_entity.lakala.batchTrade.*;

/**
 * 批量交易
 */
public interface LakalaPayBatchTradeService {

    /**
     * 申请批量交易令牌
     * @param order    报文体
     * @param dataHead 报文头
     * @return
     * @throws LakalaClientException
     */
    ApplyTradeTokenResponse applyTradeToken(ApplyTradeTokenRequest order, LakalaCrossPayEncryptRequest dataHead);

    /**
     * 上传批量文件
     * @param order
     * @return BatUploadFileResponse
     * throws LakalaClientException
     */
    BatUploadFileResponse uploadFile(BatUploadFileRequest order);

    /**
     * 下载拉卡拉（补）回盘文件,若写文件失败则会抛出异常
     * @param req
     * @throws LakalaClientException
     */
    void downLoadLakalaResFile(DownLoadFileRequest req);

    /**
     * 查询批量交易状态
     *
     * @param order
     * @param dataHead
     * @return
     * @throws LakalaClientException
     */
    BatchBizQueryResponse batchBizQuery(BatchBizQueryRequest order, LakalaCrossPayEncryptRequest dataHead) throws LakalaClientException;
}
