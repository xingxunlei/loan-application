package com.loan_server.constant;

import com.loan_entity.common.Constants;

/**
 * 放款常量类
 */
public class LoanConstant {

    /**自动放款总开关关闭*/
    public static final String STATUS_CLOSE = "0";
    /**自动放款总开关打开*/
    public static final String STATUS_OPEN = "1";
    /**关闭开关*/
    public static final int DATA_TYPE_CLOSE = 3;
    /**自动放款并发条数redis key*/
    public static final String CONCURRENT_KEY = Constants.YM_ADMIN_SYSTEN_KEY +"concurrency_limit_key";
    /**自动放款全局 key*/
    public static final String GLOBAL_KEY = Constants.YM_ADMIN_SYSTEN_KEY +"global_key";
    /**自动放款并发条数redis key 过期时间*/
    public static final int CONCURRENT_KEY_TIME = 60;
    /**自动放款规则 结束时间*/
    public static final String END_TIME = "end_time";
    /**发送邮件 结束时间*/
    public static final String EMAIL_END_TIME = "自动放款当前时间已超出结束时间，自动放款关闭";
    /**自动放款规则 并发上限*/
    public static final String CONCURRENCY_LIMIT = "concurrency_limit";
    /**发送邮件 并发上限*/
    public static final String EMAIL_CONCURRENCY_LIMIT = "自动放款并发次数已超出上限，自动放款关闭";
    /**自动放款规则 金币上限*/
    public static final String AMOUNT_LIMIT = "amount_limit";
    /**发送邮件 金币上限*/
    public static final String EMAIL_AMOUNT_LIMIT = "自动放款放款金额已超出设置金额，自动放款关闭";
    /**保证金不足 */
    public static final String UNDERMARGIN_CODE = "3000";
    /**保证金不足规则*/
    public static final String UNDERMARGIN = "undermargin";
    /**发送邮件 保证金不足*/
    public static final String EMAIL_UNDERMARGIN = "自动放款保证金余额不足，自动放款关闭";
    /**自动审核人的员工编号 9999*/
    public static final String AUTO_REVIEWER = "9999";


    /**系统异常*/
    public static final String ERR_EXCEPTION = "err_Exception";
    /**系统异常value*/
    public static final String ERR_EXCEPTION_VALUE = "system error";
    /**小开关关闭*/
    public static final String SWITCH_CODE = "switch_code";
    /**小开关value*/
    public static final String SWITCH_CODE_VALUE = "all close";
    /**邮件 小开关*/
    public static final String EMAIL_SWITCH_CODE = "自动放款开关全部关闭，自动放款关闭";
    /**发送邮件 系统异常*/
    public static final String EMAIL_ERR_EXCEPTION = "自动放款系统出现错误，自动放款关闭";


}
