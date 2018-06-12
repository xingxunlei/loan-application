package com.loan_utils.util;

import com.alibaba.fastjson.JSONObject;

public class WangDaiUtil {
    public static void main(String[] args) {
        JSONObject objParam = new JSONObject();
        JSONObject objData = new JSONObject();
        objData.put("order_term", 3);
        objData.put("order_no", "123456789");
        objData.put("order_time", "2017-01-01");
        objData.put("order_amount", 5000.00);
        objData.put("term_unit", 2);
        objData.put("order_status", 1);
        objData.put("user_mobile", "13000000000");

     

        String app_key = "lapiymsd";
        String app_secret = "7927222atm";
        String method = "api.order.approvefeedback";
        String timestamp = "1481340000";
        String version = "1.0";
        System.out.println();
        String jiamichuan = app_key + version + timestamp + app_secret + MD5Util.encodeToMd5(objData.toString());
        String sign = MD5Util.encodeToMd5(jiamichuan);
        System.out.println(jiamichuan);
        System.out.println(sign);
        objParam.put("sign", sign);
        objParam.put("app_key", app_key);
        objParam.put("method", method);
        objParam.put("timestamp", timestamp);
        objParam.put("version", version);
        objParam.put("data", objData);
        //
        // String params = JSONObject.toJSONString(objParam);
        // String result =
        // HttpTools.postNoUrl("http://test.api.fqgj.net:9099/openapi/gateway",
        // params);
        //
        // System.out.println(result);
    }
}
