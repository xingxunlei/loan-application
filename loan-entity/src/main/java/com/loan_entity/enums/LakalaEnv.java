package com.loan_entity.enums;

/**
 * <p>
 * 拉卡拉跨境支付环境枚举
 * </p>
 */
public enum LakalaEnv {

    SANDBOX("sandbox"),
    LIVE("live");

    LakalaEnv(String env) {
        this.env = env;
    }

    private String env;

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }
}
