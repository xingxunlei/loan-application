package com.loan_utils.util;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;


public class HttpTools{
	private static Logger log = Logger.getLogger(HttpTools.class);
    public static String post(String url, String params, int time) {
		String log_str = url + "==" + params + "==" + time;
        // log.error(log_str);
        // 实例化httpClient
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 实例化post方法
        HttpPost httpPost = new HttpPost(url);

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(time).setConnectTimeout(time).build();// 设置请求和传输超时时间
        httpPost.setConfig(requestConfig);

        // 处理参数

        // JSONObject jsonParam =JSONObject.parseObject(params);

        // 结果
        CloseableHttpResponse response = null;
        String content = "";
        try {
            String paramUrl = URLEncoder.encode(params, "utf-8");
            // 提交的参数
            StringEntity entity = new StringEntity(paramUrl, "utf-8");
            entity.setContentEncoding("utf-8");
            entity.setContentType("application/json");

            // 将参数给post方法
            httpPost.setEntity(entity);
            // 执行post方法
            response = httpclient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                content = EntityUtils.toString(response.getEntity(), "utf-8");
                // System.out.println(content);
            }
        } catch (ClientProtocolException e) {
			log.error(log_str + "报错=>" + e);
        } catch (IOException e) {
			log.error(log_str + "报错=>" + e);
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		log.error(log_str + "返回=>" + content);
        return content;
    }

    //json
    public static String post(String url,String params){  
		String log_str = url + "==" + params;
        // log.error(log_str);
        //实例化httpClient  
        CloseableHttpClient httpclient = HttpClients.createDefault();         
        //实例化post方法  
        HttpPost httpPost = new HttpPost(url); 
    
     
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(200000).setConnectTimeout(100000).build();// 设置请求和传输超时时间
        httpPost.setConfig(requestConfig);  
           
        //处理参数  
        
        //JSONObject jsonParam =JSONObject.parseObject(params);
        
        
        //结果  
        CloseableHttpResponse response = null;  
        String content="";  
        try {  
            String paramUrl = URLEncoder.encode(params, "utf-8");
            //提交的参数  
            StringEntity entity = new StringEntity(paramUrl,"utf-8");
            entity.setContentEncoding("utf-8");    
            entity.setContentType("application/json"); 
            
            //将参数给post方法  
            httpPost.setEntity(entity);  
            //执行post方法  
            response = httpclient.execute(httpPost);  
            if(response.getStatusLine().getStatusCode()==200){  
                content = EntityUtils.toString(response.getEntity(),"utf-8");  
                // System.out.println(content);
            }  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
			log.error(log_str + "报错=>" + e);
        } catch (IOException e) {  
            e.printStackTrace();  
			log.error(log_str + "报错=>" + e);
        } finally{  
            // 关闭连接,释放资源  
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }   
		log.error(log_str + "返回=>" + content);
        return content;  
    } 
    
    public static String postNoUrl(String url, String params) {
        // 实例化httpClient
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 实例化post方法
        HttpPost httpPost = new HttpPost(url);

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(100000).setConnectTimeout(100000).build();// 设置请求和传输超时时间
        httpPost.setConfig(requestConfig);

        // 处理参数

        // JSONObject jsonParam =JSONObject.parseObject(params);

        // 结果
        CloseableHttpResponse response = null;
        String content = "";
        try {
            // String paramUrl = URLEncoder.encode(params, "utf-8");
            // 提交的参数
            StringEntity entity = new StringEntity(params, "utf-8");
            entity.setContentEncoding("utf-8");
            entity.setContentType("application/json");

            // 将参数给post方法
            httpPost.setEntity(entity);
            // 执行post方法
            response = httpclient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                content = EntityUtils.toString(response.getEntity(), "utf-8");
                // System.out.println(content);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    public static CloseableHttpClient createSSLClientDefault() {

        SSLContext sslContext;
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    
                    return true;
                }
               
            }).build();

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);

            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (Exception e) {
           e.printStackTrace();
        }

        return HttpClients.createDefault();
    }

    

    public static String faceOcr2(String url, File file, String api_key, String api_secret) throws Exception {
        CloseableHttpClient httpClient = HttpTools.createSSLClientDefault();
        String res = "";
        try {
            // 把一个普通参数和文件上传给下面这个地址 是一个servlet
            HttpPost httpPost = new HttpPost(url);
            // 把文件转换成流对象FileBody
            // FileBody bin = new FileBody(file);
            // 普通字段 重新设置了编码方式
            StringBody key = new StringBody(api_key, ContentType.create("text/plain", Consts.UTF_8));
            // StringBody comment = new StringBody("这里是一个评论",
            // ContentType.TEXT_PLAIN);

            StringBody secret = new StringBody(api_secret, ContentType.create("text/plain", Consts.UTF_8));

            // String fileName = file.getOriginalFilename();

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    // .addPart("image", bin)//相当于<input type="file"
                    // name="media"/>
                    .addBinaryBody("image", file, ContentType.MULTIPART_FORM_DATA, file.getName())
                    .addPart("api_key", key).addPart("api_secret", secret)// 相当于<input
                                                                          // type="text"
                                                                          // name="name"
                                                                          // value=name>
                    .build();

            httpPost.setEntity(reqEntity);

            // 发起请求 并返回请求的响应
            CloseableHttpResponse response = httpClient.execute(httpPost);
            try {
                // 打印响应状态
                // System.out.println(response.getStatusLine());
                // 获取响应对象
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    // 打印响应内容

                    res = EntityUtils.toString(resEntity, Charset.forName("UTF-8"));
                    // System.out.println(res);
                }
                // 销毁
                EntityUtils.consume(resEntity);
            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }
        return res;
    }

    public static void main(String[] args) {
        try {
            String url = "https://api.faceid.com/faceid/v1/detect";
            String url2 = "https://api.faceid.com/faceid/v1/ocridcard";
            String api_key = "EeWHpRcOgtuMPbpzActRaRPwAfm7t9w2";
            String api_secret = "rPLncIKeF6Ad3RpzzdQX9CrghhMMmBEw";
            File file = new File("D:\\310101198805021521z.jpg");

            String res = faceOcr2(url2, file, api_key, api_secret);
            System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}