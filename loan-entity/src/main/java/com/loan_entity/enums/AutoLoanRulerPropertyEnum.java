package com.loan_entity.enums;

/**
 * 自动借款规则属性枚举
 * Created by chenchao on 2017/10/30.
 */
public enum AutoLoanRulerPropertyEnum {

    time_switch("time_switch", "自动放款时间限制开关"),
    start_time("start_time", "自动放款起始时间"),
    end_time("end_time", "自动放款结束时间"),
    concurrency_switch("concurrency_switch", "自动放款并发数限制开关"),
    concurrency_limit("concurrency_limit", "自动放款并发数限制（单位：次/分钟）"),
    amount_switch("amount_switch", "自动放款金额限制开关"),
    amount_limit("amount_limit", "自动放款金额限制"),
    last_order_switch("last_order_switch", "上单状态开关"),
    normal_order_switch("normal_order_switch", "上单状态正常结清开关"),
    overdue_order_switch("overdue_order_switch", "上单状态逾期结清开关"),
    overdue_order_day("overdue_order_day", "上单状态逾期天数"),
    none_loan_switch("none_loan_switch", "上单状态无借款开关"),;

    private String property;
    private String description;

    private AutoLoanRulerPropertyEnum(String property, String description) {
        this.property = property;
        this.description = description;
    }

    public String getProperty() {
        return property;
    }

    public String getDescription() {
        return description;
    }

    public static boolean contains(String rulerName) {
        AutoLoanRulerPropertyEnum[] properties = values();
        for (AutoLoanRulerPropertyEnum property : properties) {
            if (property.name().equals(rulerName)) {
                return true;
            }
        }
        return false;
    }
}
