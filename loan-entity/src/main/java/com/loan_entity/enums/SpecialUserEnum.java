package com.loan_entity.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Create by Jxl on 2017/9/13
 */
public enum SpecialUserEnum {

            USER_BLANK("7777","空白"),
            USER_SYS("8888","SYS"),
            USER_SPECIAL("9999","特殊");

            SpecialUserEnum(String code,String describe){
                this.code = code;
                this.describe = describe;
            }

            public static String getDescByCode(String code){
                if(StringUtils.isBlank(code)){
                    return null;
                }
                for (SpecialUserEnum statusEnum : SpecialUserEnum.values()) {
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
