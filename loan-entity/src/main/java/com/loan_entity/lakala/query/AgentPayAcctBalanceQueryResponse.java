package com.loan_entity.lakala.query;

import com.loan_entity.enums.LakalaCurrency;
import com.loan_entity.lakala.LakalaCrossPaySuperResponse;

/**
 * 拉卡拉跨境支付-商户代付账户余额查询响应参数
 */
public class AgentPayAcctBalanceQueryResponse extends LakalaCrossPaySuperResponse {

    private static final long serialVersionUID = -7664158215318110860L;

    /**
     * 余额，单位：元，小数点后两位
     */
    private String amount;

    /**
     * 币种，目前仅支持人民币
     *
     * @see LakalaCurrency
     */
    private String currency;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AgentPayAcctBalanceQueryResponse{");
        sb.append("super=").append(super.toString());
        sb.append("amount='").append(amount).append('\'');
        sb.append(", currency='").append(currency).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
