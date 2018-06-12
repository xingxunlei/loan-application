package com.loan_entity.rzj;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 融之家注册参数
 */
@Setter
@Getter
@ToString
public class RzjRegisterDo implements Serializable {

    private static final long serialVersionUID = -4107866355395736692L;

    public RzjRegisterDo(String code, String msg, String is_new_user, String apply_url) {
        this.code = code;
        this.msg = msg;
        this.is_new_user = is_new_user;
        this.apply_url = apply_url;
    }

    public RzjRegisterDo(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public RzjRegisterDo() {
    }

    /**
     * 返回代码，0表示成功，其他标识错误
     */
    private String code;

    /**
     * 错误描述，用户可读的错误信息，可能会呈现给用户
     */
    private String msg;
    /**
     * 是否为机构的新注册用户；1:是 ，2：通过借点钱渠道推送的老用户，3：其他渠道推送的老用户
     */
    private String is_new_user;
    /**
     * 用户需要跳转的url
     */
    private String apply_url;

}
