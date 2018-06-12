package com.loan_utils.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpUtils {

    private final static Logger log = LoggerFactory.getLogger(HttpUtils.class);
    /**
     * 发送http请求超时时间
     */
    private static int timeout = Integer.valueOf(60000);

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

    public static String postToFile(String url, Map<String, String> param, File file) throws Exception {
        CloseableHttpClient httpClient = HttpUtils.createSSLClientDefault();

        String res = "";
        try {
            // 把一个普通参数和文件上传给下面这个地址 是一个servlet
            HttpPost httpPost = new HttpPost(url);
            MultipartEntityBuilder mb = MultipartEntityBuilder.create();
            //文件上传key为file，value为别名
            mb.addBinaryBody(param.get("file"), file, ContentType.MULTIPART_FORM_DATA, file.getName());
            if (Detect.notEmpty(param)) {
                //普通参数传递
                for (String key : param.keySet()) {
                    mb.addPart(key, new StringBody(param.get(key), ContentType.create("text/plain", Consts.UTF_8)));
                }
            }
            HttpEntity reqEntity = mb.build();
            httpPost.setEntity(reqEntity);

            // 发起请求 并返回请求的响应
            CloseableHttpResponse response = httpClient.execute(httpPost);

            try {
                // 获取响应对象
                HttpEntity resEntity = response.getEntity();

                if (resEntity != null) {
                    // 打印响应内容
                    res = EntityUtils.toString(resEntity, Charset.forName("UTF-8"));
                    System.out.print(res);
                    log.error(res);
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

    /**
     * 在HttpServletRequest获取提交数据
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static String getRequestMsg(HttpServletRequest request) throws IOException {
        request.setCharacterEncoding("UTF-8");
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            InputStream is = request.getInputStream();
            byte[] buffer = new byte[256];

            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }

            byte[] readBytes = bos.toByteArray();
            return new String(readBytes, "UTF-8");
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * 字符串转map
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static Map<String, Object> getStringRequestByMap(HttpServletRequest request) throws IOException {
        Map<String, Object> map = null;
        String result = getRequestMsg(request);
        if (Detect.notEmpty(result)) {
            map = new HashMap<String, Object>();
            String[] prama = result.split("&");
            if (Detect.notEmpty(prama)) {
                for (String backStr : prama) {
                    String[] myMap = backStr.split("=");
                    if (Detect.notEmpty(myMap) && myMap.length == 1) {
                        map.put(myMap[0], null);
                    } else {
                        map.put(myMap[0], myMap[1]);
                    }
                }
            }
        }

        return map;
    }

    /**
     * 将收到的数据放入到map
     *
     * @param request
     * @return
     */
    public static TreeMap<String, String> getReq2Map(HttpServletRequest request) {
        TreeMap<String, String> treeMap = new TreeMap<>();
        Enumeration<String> names = request.getParameterNames();
        log.info("getReq2Map()....................." + names);
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            treeMap.put(name, request.getParameter(name));
        }
        log.info("getReq2Map()....................." + JSONObject.toJSONString(treeMap));
        return treeMap;
    }

    /**
     * 在HttpServletRequest获取提交数据
     * 穿过的的参数中已是UTF-8的编码 则不需要重新编码
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static String getRequestMsgUTF(HttpServletRequest request) throws IOException {
        request.setCharacterEncoding("UTF-8");
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            InputStream is = request.getInputStream();
            byte[] buffer = new byte[256];

            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }

            byte[] readBytes = bos.toByteArray();
            return new String(readBytes);
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }


    /**
     * servlet中发送响应数据
     *
     * @param response
     * @param msg
     * @return
     * @throws IOException
     */
    public static void sendResponse(HttpServletResponse response, String msg) throws IOException {

        OutputStream os = null;
        try {
            byte[] data = msg.getBytes("UTF-8");
            response.setContentLength(data.length);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("HTTP-Version", "HTTP/1.1");
            os = response.getOutputStream();
            os.write(data);
            os.flush();
        } catch (IOException e) {

            throw new IOException(e);
        }
    }

    public static String sendPost(String url, String data) throws Exception {
        return sendPost(url, data, "");
    }


    /**
     * @param url
     * @param file
     * @return
     * @throws Exception
     */
    public static String toRobot(String url, File file) throws Exception {
        CloseableHttpClient httpClient = HttpTools.createSSLClientDefault();


        String res = "";
        try {
            // 把一个普通参数和文件上传给下面这个地址 是一个servlet
            HttpPost httpPost = new HttpPost(url);
            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addBinaryBody("file", file, ContentType.MULTIPART_FORM_DATA, file.getName())
                    // .addPart("image", bin)//相当于<input type="file"  name="media"/>
//					.addPart("client_name",new StringBody("金互行（悠米闪借）", ContentType.create("text/plain", Consts.UTF_8)))
//					.addPart("access_token", new StringBody("youmishanjie20170816", ContentType.create("text/plain", Consts.UTF_8)))

                    .build();

            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            formparams.add(new BasicNameValuePair("client_name", "金互行（悠米闪借）"));
            formparams.add(new BasicNameValuePair("access_token", "youmishanjie20170816"));
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httpPost.setEntity(uefEntity);
            httpPost.setEntity(reqEntity);

            // 发起请求 并返回请求的响应
            CloseableHttpResponse response = httpClient.execute(httpPost);

            try {
                // 获取响应对象
                HttpEntity resEntity = response.getEntity();

                if (resEntity != null) {
                    // 打印响应内容
                    res = EntityUtils.toString(resEntity, Charset.forName("UTF-8"));
                    System.out.print(res);
                    log.error(res);
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

    public static void main(String[] arge) {
        try {
//			Map<String, Object> param = new HashMap<String, Object>();
//			param.put("per_id","6000249");
//			param.put("phone_pwd","123456");
//			param.put("token","f4efaa4a-63e4-43ec-92cd-d511af439897");
//			param.put("md5sign","07e92f9da020f84385a7b462209514c8");
//
//			HttpUtils.sendPost("http://localhost:8089/loan-web/loan/getPhonePwd.action", HttpUtils.toParam(param));
            for (int i = 0; i < 10000; i++) {
                try {
                    HttpUtils.sendPost("http://cas.youmishanjie.com/zloan-manage/user/userLogin.action", "loginname");

                } catch (Exception e) {

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送http post请求，编码方式UTF-8
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static String sendPost(String url, String data, String contentType) throws Exception {

        OutputStream outputStream = null;
        HttpURLConnection conn = null;

        try {
            URL remoteUrl = new URL(url);
            conn = (HttpURLConnection) remoteUrl.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            if (Detect.notEmpty(contentType)) {
                conn.setRequestProperty("Content-type", contentType);
            }

            // 发送数据
            byte[] datas = data.getBytes("UTF-8");
            outputStream = conn.getOutputStream();
            outputStream.write(datas, 0, datas.length);
            outputStream.flush();
            log.info("http发送报文:" + data);
            // 读取返回数据
            InputStream inputStream = conn.getInputStream();

            String res = null;
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[256];
                int len = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                byte[] readBytes = bos.toByteArray();
                res = new String(readBytes, "UTF-8");
            }

            log.info("http post 收到响应数据:" + res);
            return res;
        } catch (MalformedURLException e) {
            log.error("发送http post异常", e);
            throw new Exception(e.getMessage());
        } catch (IOException e) {
            log.error("发送http post异常", e);
            throw new Exception(e.getMessage());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error("发送http post异常", e);
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }


    public static String sendGet(String url, String params) {
        String result = "";
        InputStream inputStream = null;
        try {
            String urlNameString = url;
            if (params != null && (!"".equals(params))) {
                urlNameString = url + "?" + params;
            }

            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();

            // 定义 BufferedReader输入流来读取URL的响应
            inputStream = connection.getInputStream();

            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[256];
                int len = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                byte[] readBytes = bos.toByteArray();
                result = new String(readBytes, "UTF-8");
            }
        } catch (Exception e) {
            log.error("发送GET请求出现异常", e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 时间转换机制 yyyy-MM-dd HH:mm:ss 24小时制
     *
     * @param str
     * @return
     */
    public static Date stringToDateYYYYMMDD(String str) {
        try {
            if (null == str || "".equals(str)) {
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(str);
            return date;
        } catch (Exception e) {
            log.error("处理请求数据异常");
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 把请求参数解析到MAP中
     *
     * @param hsr
     * @return
     */
    public static TreeMap<String, String> getReqStringValue2Map(HttpServletRequest hsr) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        Map<String, String[]> names = hsr.getParameterMap();
        log.info("请求转换参数名称 : " + JSONObject.toJSONString(names));
        log.info("请求转换参数值: " + JSONObject.toJSONString(hsr.getParameterMap()));
        if (Detect.notEmpty(names)) {
            for (String key : names.keySet()) {
                String[] values = names.get(key);
                if (Detect.notEmpty(values)) {
                    params.put(key, values[0]);
                } else {
                    params.put(key, "");
                }
            }
        }
        log.info(" -end-- " + params);
        return params;
    }

    /**
     * 把请求参数解析到MAP中
     *
     * @param hsr
     * @return TreeMap<String ,Object>
     * @throws IOException
     */
    public static Map<String, Object> getReqObjectValueMap(HttpServletRequest hsr) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        String jsonStr = getRequestMsg(hsr);
        JSONObject jsonObject = (JSONObject) JSONObject.parse(jsonStr);
        if (Detect.notEmpty(jsonStr)) {
            Set<String> set = jsonObject.keySet();
            Iterator<String> it = set.iterator();

            while (it.hasNext()) {
                String key = it.next();
                params.put(key, jsonObject.get(key));
            }
        }
        return params;
    }

    /**
     * 把请求参数解析到MAP中
     *
     * @param hsr
     * @return TreeMap<String ,Object>
     * @throws IOException
     */
    public static Map<String, String> getReqStringtValueMap(HttpServletRequest hsr) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        String jsonStr = getRequestMsg(hsr);
        JSONObject jsonObject = (JSONObject) JSONObject.parse(jsonStr);
        if (Detect.notEmpty(jsonStr)) {
            Set<String> set = jsonObject.keySet();
            Iterator<String> it = set.iterator();

            while (it.hasNext()) {
                String key = it.next();
                params.put(key, jsonObject.getString(key));
            }
        } else {
            params = getReq2Map(hsr);
        }
        return params;
    }


    public static String toParam(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            if (entry.getValue() != null) {
                sb.append(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        return sb.toString();
    }

    public static String getBasePath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String requestUrl = request.getRequestURL().toString();
        int index = requestUrl.indexOf(contextPath);
        String basePath;
        if (index != -1) {
            basePath = requestUrl.substring(0, index);
            return basePath;
        } else {
            basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        }
        return basePath;
    }

    public static String getContextFullPath(HttpServletRequest request) {
        return getBasePath(request) + request.getContextPath();
    }

}
