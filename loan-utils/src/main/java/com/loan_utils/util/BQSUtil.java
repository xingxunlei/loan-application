package com.loan_utils.util;

import java.text.SimpleDateFormat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loan_utils.entity.BQSEntity;
import com.loan_utils.entity.Body;
import com.loan_utils.entity.Head;
import com.loan_utils.entity.PersonRiskEntity;

/**白骑士工具接口
 * @author xuepengfei
 *2016年11月30日下午4:59:41
 */
public class BQSUtil {
    public static String send(String url,BQSEntity bqs){  
        String content = JSONObject.toJSONString(bqs);
        Body body = new Body();
        body.setContent(content);
        Head head = new Head();
        head.setCommand("1009");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        long currentTime = System.currentTimeMillis();
        String time = sdf.format(currentTime);      
        head.setReqTime(time);
        String Guid = BorrNum_util.createBorrNum();
        head.setGuid(Guid);
        head.setUser("测试");
        head.setPassWord("");
        head.setRespCode("");
        head.setRespMessage("");      
        head.setMd5(MD5Util.encodeToMd5(content));
        head.setLang("zh-CN");
        
        PersonRiskEntity entity = new PersonRiskEntity();
        entity.setHead(head);
        entity.setBody(body);
        String params = JSON.toJSONString(entity);
        // System.out.println(params);
        String response = HttpTools.post(url, params);
        JSONObject json = JSONObject.parseObject(response);
        String resHeadStr = json.getString("Head");
        JSONObject resHead = JSONObject.parseObject(resHeadStr);
        String respCode = resHead.getString("RespCode");
       // String respMessage = resHead.getString("RespMessage");
        return respCode;       
    }
    
    /**测试方法
     * @param args
     */
    public static void main(String[] args) {
        BQSEntity bqs = new BQSEntity();      
        bqs.setName("薛鹏飞");
        bqs.setPhone("18888888888");
        bqs.setEventType("login");     
        String response = BQSUtil.send("http://192.168.1.49:8090/api/YM/RiskPost", bqs);
        System.out.println(response);
    }
}
