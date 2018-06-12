package com.loan_entity.lakala.recon;

import java.io.Serializable;

/**
 * 拉卡拉跨境支付-对账文件预约请求参数
 */
public class ReconSubscribeRequest implements Serializable {

    private static final long serialVersionUID = 3957882917136902801L;

    /**
     * 商户号
     */
    private String merId;

    /**
     * 开始日期,格式yyyyMMdd
     */
    private String startDate;

    /**
     * 结束日期，格式yyyyMMddHHmmss
     */
    private String endDate;

    /**
     * 商户接受拉卡拉对账文件下载通知接口url
     */
    private String notifyAddr;

    public String getMerId() {
        return merId;
    }

    public void setMerId(String merId) {
        this.merId = merId;
    }

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

    public String getNotifyAddr() {
        return notifyAddr;
    }

    public void setNotifyAddr(String notifyAddr) {
        this.notifyAddr = notifyAddr;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ReconSubscribeRequest{");
        sb.append("merId='").append(merId).append('\'');
        sb.append(", startDate='").append(startDate).append('\'');
        sb.append(", endDate='").append(endDate).append('\'');
        sb.append(", notifyAddr='").append(notifyAddr).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
