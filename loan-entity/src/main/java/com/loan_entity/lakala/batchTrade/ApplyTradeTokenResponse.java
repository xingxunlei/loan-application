package com.loan_entity.lakala.batchTrade;

import com.google.gson.annotations.SerializedName;
import com.loan_entity.lakala.LakalaCrossPaySuperResponse;

/**
 * 批量代收申请交易令牌响应参数
 */
public class ApplyTradeTokenResponse extends LakalaCrossPaySuperResponse {
    private static final long serialVersionUID = 2898423208121893923L;

    /**
     * 批量交易令牌
     */
    @SerializedName("token")
    private String bizToken;

    /**
     * 批次号，用于拼接上传文件名
     */
    private String batchId;

    public String getBizToken() {
        return bizToken;
    }

    public void setBizToken(String bizToken) {
        this.bizToken = bizToken;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ApplyTradeTokenResponse{");
        sb.append("bizToken='").append(bizToken).append('\'');
        sb.append(", batchId='").append(batchId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
