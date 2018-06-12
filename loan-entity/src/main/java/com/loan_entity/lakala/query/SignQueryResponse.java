package com.loan_entity.lakala.query;

import com.loan_entity.lakala.LakalaCrossPaySuperResponse;

/**
 * 签约状态查询响应参数
 */
public class SignQueryResponse extends LakalaCrossPaySuperResponse {

    private static final long serialVersionUID = 1876697969455013057L;

    /**
     * 签约状态
     * <ul>
     * <li>0-未签约</li>
     * <li>1-签约</li>
     * <li>2-签约中</li>
     * </ul>
     */
    private String agstat;

    private String ext1;

    private String ext2;
}
