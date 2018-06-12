package com.loan_utils.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class HttpUrlPost {
	/** 
     * 向指定URL发送POST请求 
     * @param url 
     * @param paramMap 
     * @return 响应结果 
     */  
    public static String sendPost(String url, Map<String, String> paramMap) {  
        PrintWriter out = null;  
        BufferedReader in = null;  
        String result = "";  
        try {  
            URL realUrl = new URL(url);  
            // 打开和URL之间的连接  
            URLConnection conn = realUrl.openConnection();  
            // 设置通用的请求属性  
            conn.setRequestProperty("accept", "*/*");  
            conn.setRequestProperty("connection", "Keep-Alive");  
            conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");  
            conn.setRequestProperty("charset", "UTF-8");  
            // 发送POST请求必须设置如下两行  
            conn.setDoOutput(true);  
            conn.setDoInput(true);  
            // 获取URLConnection对象对应的输出流  
            out = new PrintWriter(conn.getOutputStream());  
  
            // 设置请求属性  
            String param = "";  
            if (paramMap != null && paramMap.size() > 0) {  
                Iterator<String> ite = paramMap.keySet().iterator();  
                while (ite.hasNext()) {  
                    String key = ite.next();// key  
                    String value = paramMap.get(key);  
                    param += key + "=" + value + "&";  
                }  
                param = param.substring(0, param.length() - 1);  
            }  
//            String temp = new String(param.getBytes("utf-8"),"ISO-8859-1");
//            param = URLDecoder.decode(temp, "utf-8");
//            param = URLDecoder.decode(param,"utf-8");
            // 发送请求参数  
            out.print(param);  
            // flush输出流的缓冲  
            out.flush();  
            // 定义BufferedReader输入流来读取URL的响应  
            in = new BufferedReader(  
                    new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line;  
            while ((line = in.readLine()) != null) {  
                result += line;  
            }  
        } catch (Exception e) {  
            System.err.println("发送 POST 请求出现异常！" + e);  
            e.printStackTrace();  
        }  
        // 使用finally块来关闭输出流、输入流  
        finally {  
            try {  
                if (out != null) {  
                    out.close();  
                }  
                if (in != null) {  
                    in.close();  
                }  
            } catch (IOException ex) {  
                ex.printStackTrace();  
            }  
        }  
        return result;  
    }  
    
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            /*
             * Map<String, List<String>> map = connection.getHeaderFields(); //
             * 遍历所有的响应头字段 for (String key : map.keySet()) {
             * System.out.println(key + "--->" + map.get(key)); }
             */
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            // System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
      
    /** 
     * 测试主方法 
     * @param args 
     * @throws UnsupportedEncodingException 
     */  
    public static void main(String[] args) throws UnsupportedEncodingException {  
//    	String message = "";
//		message = URLDecoder.decode(message, "gb2312");
//
//		Map<String, String> paramMap = new HashMap<String, String>();
////		paramMap.put("message", message);
//        String res = sendPost("http://localhost:8088/loan-web/user/getAmount.action", paramMap);
//        System.out.println(res);

        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("per_id", "6286048");
        paramMap.put("phone_list", "[{\"name\":\"周师言\", \"phone\":\"15945833477\"},{\"name\":\"赵亮亮\", \"phone\":\"13149689526\"},{\"name\":\"老蒋\", \"phone\":\"13946763996\"},{\"name\":\"朱老师朱宝\", \"phone\":\"15845466007\"},{\"name\":\"贾志博\", \"phone\":\"13613687178\"},{\"name\":\"王海存\", \"phone\":\"13351967856\"},{\"name\":\"梁国栋\", \"phone\":\"15804683550\"},{\"name\":\"宋庆雪\", \"phone\":\"15146878957\"},{\"name\":\"宋庆雪\", \"phone\":\"18746179273\"},{\"name\":\"王亮\", \"phone\":\"13664683017\"},{\"name\":\"杨天瑞\", \"phone\":\"13845050627\"},{\"name\":\"李仰导员\", \"phone\":\"18746170680\"},{\"name\":\"藏新\", \"phone\":\"18646030141\"},{\"name\":\"将帅\", \"phone\":\"18201094422\"},{\"name\":\"将帅\", \"phone\":\"18646875795\"},{\"name\":\"海康\", \"phone\":\"18745775535\"},{\"name\":\"金宇泽\", \"phone\":\"18845906111\"},{\"name\":\"大雨哥\", \"phone\":\"18222597983\"},{\"name\":\"王贵鹏\", \"phone\":\"15846889679\"},{\"name\":\"姜博文\", \"phone\":\"13091880213\"},{\"name\":\"高松\", \"phone\":\"15221501645\"},{\"name\":\"高松\", \"phone\":\"13836071095\"},{\"name\":\"樊姨\", \"phone\":\"13936645660\"},{\"name\":\"张海涛\", \"phone\":\"18745054993\"},{\"name\":\"赵秋多\", \"phone\":\"13945186139\"},{\"name\":\"刘佳\", \"phone\":\"18646876903\"},{\"name\":\"李刚\", \"phone\":\"15046336413\"},{\"name\":\"郭宇\", \"phone\":\"15204609358\"},{\"name\":\"蒋志国\", \"phone\":\"18645004094\"},{\"name\":\"张志鹏导员\", \"phone\":\"15546116543\"},{\"name\":\"张鹤\", \"phone\":\"15046775260\"},{\"name\":\"松华志\", \"phone\":\"15045485637\"},{\"name\":\"思修吕金微\", \"phone\":\"15045108060\"},{\"name\":\"老哥\", \"phone\":\"18646882796\"},{\"name\":\"吴佳雨\", \"phone\":\"18346860770\"},{\"name\":\"乔缘野\", \"phone\":\"15164689001\"},{\"name\":\"维导\", \"phone\":\"18646969861\"},{\"name\":\"赵导\", \"phone\":\"15124573217\"},{\"name\":\"杨宇\", \"phone\":\"18944659585\"},{\"name\":\"雷哥\", \"phone\":\"13037559000\"},{\"name\":\"周姨\", \"phone\":\"18746806166\"},{\"name\":\"猴子\", \"phone\":\"15590886705\"},{\"name\":\"毛有禄\", \"phone\":\"15663869527\"},{\"name\":\"二姨\", \"phone\":\"18246665359\"},{\"name\":\"王海存\", \"phone\":\"18664684776\"},{\"name\":\"大娘\", \"phone\":\"13019027567\"},{\"name\":\"刘导\", \"phone\":\"1590461048\"},{\"name\":\"曹忠朴\", \"phone\":\" 18045189015\"},{\"name\":\"齐宝哥\", \"phone\":\"13234685552\"},{\"name\":\"毕鹏\", \"phone\":\"15804626981\"},{\"name\":\"韩鹏\", \"phone\":\"15663629978\"},{\"name\":\"王丽名\", \"phone\":\"13804587156\"},{\"name\":\"任洪亮\", \"phone\":\"18621908001\"},{\"name\":\"娟姐\", \"phone\":\"18346857778\"},{\"name\":\"刘心雨\", \"phone\":\"18604615867\"},{\"name\":\"赵庆梅\", \"phone\":\"18246898202\"},{\"name\":\"张桂东\", \"phone\":\"18258143303\"},{\"name\":\"杨雨\", \"phone\":\"18944659585\"},{\"name\":\"孙洪亮\", \"phone\":\"13936339044\"},{\"name\":\"高国鹏\", \"phone\":\"13089981671\"},{\"name\":\"陈志斌\", \"phone\":\"13019018331\"},{\"name\":\"马娜\", \"phone\":\"13521040496\"},{\"name\":\"曹兵姐夫\", \"phone\":\"13359775500\"},{\"name\":\"姬淞南\", \"phone\":\"18600168980\"},{\"name\":\"二哥\", \"phone\":\"18745003133\"},{\"name\":\"杨老大\", \"phone\":\"15921116872\"},{\"name\":\"李佳奇\", \"phone\":\"13261277795\"},{\"name\":\"卢亚楠\", \"phone\":\"18810664927\"},{\"name\":\"张宏达\", \"phone\":\"18600508489\"},{\"name\":\"漫道\", \"phone\":\"02869514351\"},{\"name\":\"漫道\", \"phone\":\"02869514352\"},{\"name\":\"漫道\", \"phone\":\"02869514353\"},{\"name\":\"漫道\", \"phone\":\"02869514354\"},{\"name\":\"漫道\", \"phone\":\"02869514355\"},{\"name\":\"漫道多人通话\", \"phone\":\"01052729739\"},{\"name\":\"漫道多人通话\", \"phone\":\"02462781276\"},{\"name\":\"钉钉官方短信\", \"phone\":\"10655059113144\"},{\"name\":\"钉钉官方短信\", \"phone\":\"106590256203144\"},{\"name\":\"钉钉官方短信\", \"phone\":\"106575258192144\"},{\"name\":\"钉钉官方短信\", \"phone\":\"106906199604916\"},{\"name\":\"钉钉官方短信\", \"phone\":\"106904068039604916\"},{\"name\":\"钉钉官方短信\", \"phone\":\"1065752551629604916\"},{\"name\":\"钉钉专属顾问\", \"phone\":\"057156215888\"},{\"name\":\"未知\", \"phone\":\"02131772035\"},{\"name\":\"未知\", \"phone\":\"057126883142\"},{\"name\":\"未知\", \"phone\":\"01053912831\"},{\"name\":\"未知\", \"phone\":\"057189896546\"},{\"name\":\"未知\", \"phone\":\"051482043245\"},{\"name\":\"陈超\", \"phone\":\"15527093815\"},]");
        String result = HttpUrlPost.sendPost("http://localhost:8088/loan-web/loan/getPhoneList.action", paramMap);
        System.out.println(result);


    }  
}
