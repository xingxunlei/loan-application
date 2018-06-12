package com.loan_entity.lakala.recon;

import com.loan_entity.lakala.LakalaCrossPaySuperResponse;

/**
 * 拉卡拉跨境支付-对账文件预约响应参数
 */
public class ReconSubscribeResponse extends LakalaCrossPaySuperResponse {

    private static final long serialVersionUID = -3576788035004252867L;

    /**
     * 开始日期,格式yyyyMMdd
     */
    private String startDate;

    /**
     * 结束日期，格式yyyyMMdd
     */
    private String endDate;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ReconSubscribeResponse{");
        sb.append("super=").append(super.toString());
        sb.append("startDate='").append(startDate).append('\'');
        sb.append(", endDate='").append(endDate).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
