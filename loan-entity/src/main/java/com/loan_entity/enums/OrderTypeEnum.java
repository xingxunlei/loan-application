package com.loan_entity.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Create by Jxl on 2017/9/13
 */
public enum OrderTypeEnum {

            LOAN("1","放款"),
            REPAY("2","主动充值"),
            PROCEDURE_FEE("3","手续费"),
            REPAY_AGENT("4","还款(代收)(银生宝)"),
            SERVICE_FEE("5","主动还款(银生宝)"),
            MITIGATE("6","申请减免"),
            MANUAL("7","线下还款"),
            BATCH("8","批量扣款(银生宝)"),
            REPAY_AGENT_HAIER("13","还款(代收)(海尔)"),
            SERVICE_FEE_HAIER("12","主动还款(海尔)"),
            BATCH_HAIER("14","批量扣款(海尔)");

            OrderTypeEnum(String code,String describe){
                this.code = code;
                this.describe = describe;
            }

            public static String getDescByCode(String code){
                if(StringUtils.isBlank(code)){
                    return null;
                }
                for (OrderTypeEnum statusEnum : OrderTypeEnum.values()) {
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
