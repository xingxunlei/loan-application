package com.loan_web.common.constant;

/**
 *  融之家常量
 */
public class ErrorCode {

    /** 参数缺失 */
    public final static String PARAMETERS_ARE_MISSING = "456";
    public final static String PARAMETERS_ARE_MISSING_VALUE = "参数错误，不能被解析%s";

    /** mac校验出错 */
    public final static String CHECK_MAC_ERROR = "451";
    public final static String CHECK_MAC_ERROR_VALUE = "据不合法，签名结果 与 signature 值不一致";
    /**返回融之家错误code值*/
    public final static String ERROR_CODE = "499";
    /**解密错误*/
    public final static String DECRYPTION_ERROR = "453";
    /**解密错误*/
    public final static String DECRYPTION_ERROR_VALUE = "解密错误%s";
    /** json不正确 */
    public final static String JSON_ERROR_VALUE = "JSON数据格式不正确";
    /** json不正确 */
    public final static String SYSTEM_ERROR = "系统内部错误";
}
