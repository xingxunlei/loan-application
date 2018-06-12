package com.loan_entity.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Create by Jxl on 2017/9/12
 */
public enum BorrowStatusEnum {

    APPLY_PROCESS("BS001","申请中"),
    WAIT_SIGN("BS002","待签约"),
    SIGNED("BS003","已签约"),
    WAIT_REPAY("BS004","待还款"),
    EXPIRE_REPAY("BS005","逾期未还"),
    NORMAL_REPAY("BS006","正常结清"),
    CANCEL("BS007","已取消"),
    REJECT_AUDIT("BS008","审核未通过"),
    REJECT_AUTO_AUDIT("BS009","电审未通过"),
    EXPIRE_SETTLE("BS010","逾期结清"),
    LOAN_PROCESS("BS011","放款中"),
    LOAN_FAIL("BS012","放款失败");

    BorrowStatusEnum(String code,String describe){
        this.code = code;
        this.describe = describe;
    }

    public static String getDescByCode(String code){
        if(StringUtils.isBlank(code)){
            return null;
        }
        for (BorrowStatusEnum statusEnum : BorrowStatusEnum.values()) {
            if(StringUtils.equals(statusEnum.code,code)){
                return statusEnum.describe;
            }
        }
        return null;
    }


    private String code;

    private String describe;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
