package com.loan_server.loan_mapper;

import org.apache.ibatis.annotations.Param;

/**
 * 规则表
 */
public interface AutoLoanRulerMapper {

    /**
     *  修改自动放款规则表
     * @param brake 开关
     * @param flag 状态
     */
    public void updateSwitch(@Param("brake") String brake, @Param("flag") String flag);
}
