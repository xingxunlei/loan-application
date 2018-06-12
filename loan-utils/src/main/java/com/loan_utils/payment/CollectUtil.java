package com.loan_utils.payment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.loan_utils.util.HttpUrlPost;
import com.loan_utils.util.MD5Util;
import com.loan_utils.util.PropertiesReaderUtil;

public class CollectUtil{

    private static final Logger logger = LoggerFactory.getLogger(CollectUtil.class);

    /**
     * 请求第三方绑定银行卡操作
     * @param bankNum
     * @param name
     * @param cardNum
     * @param phone
     * @return
     * @throws Exception
     */
    public static String requestBind(String bankNum,String name,String cardNum,String phone) throws Exception{
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        //协议当天有效 需要协议开始时间为当前时间的前一天
        Date start = new Date(System.currentTimeMillis()-24*60*60*1000);
        //协议结束时间  
          
        String startDate = sdf.format(start);
        
        //读取配置文件   
        String key = PropertiesReaderUtil.read("third","key");  
        String accountId = PropertiesReaderUtil.read("third","accountId"); 
        String contractId = PropertiesReaderUtil.read("third","contractId");
        String url = PropertiesReaderUtil.read("third","bindingUrl"); 
        String endDate = PropertiesReaderUtil.read("third","endDate");
        
        String mac = "accountId="+accountId+"&contractId="+contractId+"&name="+name+
                     "&phoneNo="+phone+"&cardNo="+bankNum+"&idCardNo="+cardNum+
                     "&startDate="+startDate+"&endDate="+endDate+"&key="+key;
        String macMD5 = MD5Util.encodeToMd5(mac);
        Map<String,String> param = new HashMap<String,String>();
        param.put("cardNo", bankNum);
        param.put("name", name);
        param.put("idCardNo", cardNum);
        param.put("phoneNo", phone);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        param.put("contractId", contractId);
        param.put("accountId", accountId);
        param.put("mac", macMD5.toUpperCase());

        //发送查询请求
        System.out.println("请求第三方绑卡参数=====" + "cardNo:" + bankNum + ",name:" + name + ",idCardNo:" + cardNum + ",requestUrl:" + url);
        String response = HttpUrlPost.sendPost(url, param);
        System.out.println("请求第三方绑卡响应=====cardNo："+bankNum + ",response:"+response);
        
        return response;
    }
    
    /**
     * 请求第三方查询银行卡是否绑定操作
     * @param bankNum
     * @param name
     * @param cardNum
     * @return
     * @throws Exception
     */
    public static String requestQuery(String bankNum,String name,String cardNum) throws Exception{
            
        String key = PropertiesReaderUtil.read("third","key");  
        String accountId = PropertiesReaderUtil.read("third","accountId"); 
        String url = PropertiesReaderUtil.read("third","queryUrl"); 
        
        String mac = "accountId="+accountId+"&name="+name+"&cardNo="+bankNum+
                     "&idCardNo="+cardNum+"&key="+key;  
        String macMD5 = MD5Util.encodeToMd5(mac);
        
        //请求参数
        Map<String,String> param = new HashMap<String,String>();
        param.put("cardNo", bankNum);
        param.put("name", name);
        param.put("idCardNo", cardNum);
        param.put("accountId", accountId);
        param.put("mac", macMD5.toUpperCase());
        
        //发送查询请求
        String response = HttpUrlPost.sendPost(url, param);
        
        return response;
    }
    
    public static String requestCollect(String amount,String phone,String subContractId,String orderId,String responseUrl) throws Exception{
		// uat环境金额限制
    	String isTest = PropertiesReaderUtil.read("third", "isTest");
		if ("on".equals(isTest)) {
			amount = "0.01";
		}

        //读取配置文件             
        String key = PropertiesReaderUtil.read("third","key");  
        String accountId = PropertiesReaderUtil.read("third","accountId"); 
        String url = PropertiesReaderUtil.read("third","collectUrl");  
       // System.out.println("读配置文件：key="+key+"accountId="+accountId+"url="+url);
        String purpose = "还贷";    
        String mac = "accountId="+accountId+"&subContractId="+subContractId+"&orderId="+orderId+
                     "&purpose="+purpose+"&amount="+amount+"&phoneNo="+phone+
                     "&responseUrl="+responseUrl+"&key="+key;  
        String macMD5 = MD5Util.encodeToMd5(mac);
        
        //请求参数
        Map<String,String> param = new HashMap<String,String>();
        param.put("accountId", accountId);
        param.put("subContractId", subContractId);
        param.put("orderId", orderId);
        param.put("purpose", purpose);
        param.put("amount", amount);
        param.put("phoneNo", phone);
        param.put("responseUrl", responseUrl);
        param.put("mac", macMD5.toUpperCase());

        System.out.println("请求第三方代扣参数====="+"orderId:"+ orderId+",amount:"+ amount+",phoneNo:"+ phone+",subContractId:"+ subContractId+",requestUrl："+url+",responseUrl："+ responseUrl);
        String response = HttpUrlPost.sendPost(url, param);
        System.out.println("第三方代扣响应=====orderId:"+orderId+",response:"+response);
        
        return response;
    }  
    
    public static String queryOrder(String serialNo){
        String accountId = PropertiesReaderUtil.read("third","accountId"); 
        String key = PropertiesReaderUtil.read("third","key"); 
        String url = PropertiesReaderUtil.read("third","orderUrl");  
        // System.out.println("读配置文件：key="+key+"accountId="+accountId+"url="+url);
        String str = "accountId="+accountId+"&orderId="+serialNo+"&key="+key;
        String MD5 = MD5Util.encodeToMd5(str);
        //请求参数
        Map<String,String> param = new HashMap<String,String>();
        param.put("accountId", accountId);     
        param.put("orderId", serialNo);                 
        param.put("mac", MD5.toUpperCase());

        System.out.println("查询代扣第三方参数====="+"orderId:"+ serialNo+",requestUrl："+url);
        String response = HttpUrlPost.sendPost(url, param);
        System.out.println("查询代扣第三方响应=====orderId:"+serialNo+",response:" + response);
        
        return response;
        
    }

}
