package com.loan_utils.util;

import com.alibaba.fastjson.JSONObject;

/**
 * 返回码
 * @author zhangweixing	
 * @time 2016年10月25日 上午10:30:16
 */
public class CodeReturn {
	//成功
	public static final int success = 200;
	//失败
	public static final int fail = 201;
	//手机号已存在
	public static final String PHONE_EXIST = "1001";
	//系统错误
	public static final int systemerror = 202;
	
	public static final int baiqishi = 300;
	
	public static final String TOKEN_WRONG = "301";
	
	public static final String MD5_WRONG = "302";
	
	//返回参数及消息设置
	public static final String SUCCESS_CODE = "200";
   
	public static final String FAIL_CODE = "201";
	public static final String NOW_BORROW_CODE = "202";
	public static final String WAIT_SIGN_CODE = "203";
	public static final String VERIFY_FAIL_CODE = "203";
	public static final String SIGNED_CODE = "204";
	public static final String TOREPAY_CODE = "205";
	public static final String APPLY_CODE = "206";
    //认证状态常量
	public static final String BPM_UNDO_CODE = "207";
    //未认证
    //已认证
	public static final String BPM_FINISH_CODE = "208";
    // 已认证 但重新拉通讯录
    public static final String BPM_PHONE_CODE = "209";
    public static final String BPM_ZHIMA_CODE = "220";
    // 已认证 但重新走聚信立 重新拉通讯录
    public static final String JUXINLI_CODE = "210";
    // 放款中给前台返回的状态码
    public static final String PAYING_CODE = "211";
    //风控返回code
    
    //认证状态编码
	public static final String STATUS_BPM_N = "NS001";//未认证
	public static final String STATUS_BPM_Y = "NS002";//已认证
	public static final String STATUS_BPM_FAIL = "NS003";//认证失败
	public static final String STATUS_BPM_FAIL_B = "NS005";//认证失败且进黑名单
	public static final String STATUS_BPM_UP = "NS004";//已提交,仅手机认证节点有次状态  
    //认证过期天数
	public static final int LIMIT_DAY = 90;
    //拒绝以后不能再次申请的天数
	public static final int REFUSE_DAY = 30;
    //借款状态常量
	public static final String STATUS_APLLY = "BS001";//申请中
	public static final String STATUS_CANCEL = "BS007";//已取消
	public static final String STATUS_WAIT_SIGN = "BS002";//待签约
	public static final String STATUS_SIGNED = "BS003";//已签约
	public static final String STATUS_TO_REPAY = "BS004";//待还款
	public static final String STATUS_LATE_REPAY = "BS005";//逾期未还
	public static final String STATUS_PAY_BACK = "BS006";//已还清
	public static final String STATUS_REVIEW_FAIL = "BS008";//审核未通过
	public static final String STATUS_PHONE_REVIEW_FAIL = "BS009";//电审未通过
	public static final String STATUS_DELAY_PAYBACK = "BS010";//逾期还清
	public static final String STATUS_COM_PAYING = "BS011";//放款中
	public static final String STATUS_COM_PAY_FAIL = "BS012";//放款失败
    public static final String STATUS_PAYING = "BS013";// 还款中
    
    //内部接口请求报文常量
	public static final String RISK_COMMAND = "1001"; //个人认证风控命令头 
	public static final String PHONERISK_COMMAND = "1002"; //手机认证风控命令头
	public static final String REPEAT_COMMAND = "1004";//重获手机验证码命令头
	public static final String RISK_USER = "测试用户";
	public static final String RISK_PASSWORD = "";//user与Password使用“金互行”分配的帐号与校验码
	public static final String RISK_LANG = "zh-CN";
    
    //Guid没有匹配时，返回的code  
	public static final String GUID_WRONG = "1111";//返回系统繁忙,暂时用聚信立的系统繁忙状态码
    
	public static final String RISK_SUCCESS_CODE = "0000";

    //风控结果属于黑名单
	public static final String[] BLACK_CODE = 
       {"0001","0002","0003","0009","0010","0016",
           "2005","2006","2007","2008","2009","2010","2011",
           "2012","3002","3003","3015","4002","9990","9991","9992","9998","9980"};
    //风控结果拒绝，不进黑名单
	public static final String[] REFUSE_CODE = 
       {"0004","0005","0007","0011","0012","0013","0014","0015","0017","0006","0008",
            "2001","2002","2003","3001","3006","3007","3008","1000",
            "3009","3010","3011","3012","3013","3014","4001","4005","4006","4007","4008","9993"};
    //系统繁忙码 参数错误  都重新再来一遍
	public static final String[] SYSTEM_CODE = 
        {"1110","1111","2000","2004","3004","3005","4003","4004","9995","9999","2002","2003","3001","4001"};
  
	
	public static void main(String[] args) throws Exception {
		String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAOi19UraFz/HMkj34uQsnsXa2jPNx1GzUxJqVc8nEMa+iDSKnAiNBM3Z8bDc6NY9KmdEdQ4Uv2Eo668NmidJ8FsZPbc1BdDU9UEcyWRNLZjUOPKEx1Os0PZ+70jRWLdwSXycZCB/4io8T+f6ACV0AMnHzZfVujbQhFt7prV7OPfzAgMBAAECgYA24bLX1FdnUzMowk1gQqWvQ83yKbW4LvKIhUt3b18lgAnfWlEyMw/O/HdjnrXK0OIPOLXifEImR+BNiIeyfF2Sq19vOz5wCw+O4PYvmK8gzOQ28srFMAEM5gxo3SE0vs/RXTpC7BFuRDWYsg4gm1vIirPc9ULgDi2imwCboXQ1AQJBAPZ/nVmzPTUT4ti2KVyfL2m78Fj3R2te5T2RhVa2IH+UoUteRwQPXaoH9AXMj9/DP+UkpNRXgHiQCldPU5qD7wcCQQDxrkotP9bHo3TIfxSIKdDV/9womqYMVxZwHnGypoihILaHbCQcnNSxfQGbkzV8+rD/3uJsc9mSj1XE4mck0Ui1AkAGMdjTe0Srg89soM9TS5DWeGmm4rgBw8XBEVonfrY8XpDbIwhn2rcA4ehJCQYImpCNgqk9yOEqpnwMC6tPM8ebAkASA7DJwKlsxhVNYJOSNrO3T9QEPsrDvrYQc2R8MYebowmX/IqQi2wbMrss8oMZJsKMv/tWpCgCkaaN/2Y6y1LFAkEA0x58K2bEVFXeCcS1sVykfagTOa60fQehRShLdk1yIHvzBRp0JKzjNXwBGIxOEzZe1lCzKevpGiLid5fxYVxG4g==";
		String data = "6000057|张琦|632548754123665412|320611199404071815";
//		               6000057|张琦|632548754123665412|320611199404071815
		System.out.println("data加密前=="+data);
		byte[] encodedData = RSAUtils.encryptByPrivateKey(data.toString().getBytes(), privateKey);
    	data = Base64Utils.encode(encodedData);
    	//特殊符号处理
    	String data2 = data.replace("+", "%2B");
    	data2 = data2.replace("/","%2F");
    	data2 = data2.replace("=","%3D");
    	System.out.println("data加密后=="+data2);
    	System.out.println("==========cas8fJWTsVsYn7QE4QzlVmw0m56vQOVO6utosP1fjIUvvR6A6hdVyYUhZdpRrN0pLTemXzi47qh0A1FMu0MF2y9wSf6fDKhytJUvxoWTqoHkpfoF2tkF6ZDvip8W0DQQpL4ck2v35NFLvC4alBRPatvnXVrh%2FHLfrCcKzL31%2FEg%3D");
    	String sign = "1120161024170827001&0216111610352712909&1000&http://172.16.11.130:8080/loan-web/callback/callbackBackground.action&http://172.16.11.130:8080/loan-web/callback/callbackBackground.action&";
    	sign = sign+data;
    	System.out.println("sign加密前=="+sign);
    	sign = RSAUtils.sign(sign.toString().getBytes(), privateKey);
		sign = sign.replace("+", "%2B");
		sign = sign.replace("/","%2F");
		sign = sign.replace("=","%3D");
		System.out.println("sign加密后=="+sign);
		System.out.println("==========mHBeLiJeF%2BP7qvdMsvJuR7%2FzZiXGF2%2BMvTa2%2BmVFvMNanEKiXWgwhGMcsTK1RAGhIlSwWckb8x4OtqUAFGo%2FdhiOu9UiLFhEUSeT28AQeXKOfwVuih8sWGPvMXAMQnBGkXX%2BxeTQrFGNKSlykx4BTHQIZPHN70l7r5pWp1KvZO0%3D");
    	
	}
	
	/**
	 * 聚信立五条风控触碰code
	 * @author wanzezhong
	 *
	 */
	public abstract class JuLiXinCode{
		public final static String RC_RULE = "8888";
		/**风控为通过*/
		public static final String RISK_FAIL_CODE = "1000";
		/**风控出现异常*/
		public static final String RISK_ERROR_CODE = "2000";
	}
	
	public abstract class YesNo{
		public final static int YES = 1;
		
		public final static int NO = 2;
	}
	/**
	 * 节点Id
	 * @author wanzezhong
	 *
	 */
	public abstract class NodeId{
		public final static int GENERAL_REVIEW = 3;
		
		public final static int JULIXIN_REVIEW = 5;
	}
	
	/**
	 * 白骑士code
	 * @author wanzezhong
	 *
	 */
	public abstract class baiqishiCode{
		/****白骑士建议拒绝****/
		public final static String REFUSE = "5001";
		/****通过****/
		public final static String SUCESS = "0000";
		/****白骑士参数不合法****/
		public final static String PARAMETER_NOT = "5002";
		/****白骑士出现系统繁忙****/
		public final static String SYSTEM_BUSY = "5003";
	}
	
	
	
	/**
	 * 白骑士code转义说明
	 * @param code
	 * @return
	 */
	public static String getBaiqishiResult(String code){
		JSONObject obj = new JSONObject();
		obj.put("code", CodeReturn.baiqishi);
		obj.put("info", "请检查您的信用等级");
		if(Detect.notEmpty(code)){
			if(code.equals(CodeReturn.baiqishiCode.REFUSE)){
				
			}else if(code.equals(CodeReturn.baiqishiCode.PARAMETER_NOT)){
				obj.put("code", CodeReturn.fail);
				obj.put("info", "参数不合法！");
			}else if(code.equals(CodeReturn.baiqishiCode.SYSTEM_BUSY)){
				obj.put("code", CodeReturn.fail);
				obj.put("info", "系统繁忙！");
			}
		}
		return obj.toString();
		
	}
}
