package com.loan_entity.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Create by Jxl on 2017/9/13
 */
public enum OrderRLStatusEnum {

            PROCESS("p","处理中"),
            SUCCESS("s","成功"),
            FAIL("f","失败"),
            PROCESS_SETTELMENT("q","清算处理中"),
            FAIL_SETTELMENT("c","清结算失败");

            OrderRLStatusEnum(String code,String describe){
                this.code = code;
                this.describe = describe;
            }

            public static String getDescByCode(String code){
                if(StringUtils.isBlank(code)){
                    return null;
                }
                for (OrderRLStatusEnum statusEnum : OrderRLStatusEnum.values()) {
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
