package com.loan_manage.utils.arbitration.zcgj;

import com.alibaba.fastjson.JSONObject;
import com.loan_manage.utils.arbitration.config.Config;
import com.loan_manage.utils.arbitration.dao.ReqBean;
import com.loan_manage.utils.arbitration.util.ErrorCode;
import com.loan_manage.utils.arbitration.util.FileUitl;
import com.loan_manage.utils.arbitration.util.UtilPrint;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

public class UploadZipFileService {
    public ReqBean checkFileUpload(String filePath, String fileName) {
        ReqBean reqBean = new ReqBean();
        Map<String, String> textMap = new HashMap();
        try {
            String base64Code = FileUitl.encodeBase64File(filePath);
            textMap.put("fileBody", base64Code);
            textMap.put("fileName", fileName);
            textMap.put("token", Config.getToken());
            String ret = formUpload(Config.getUrl(), textMap);
            UtilPrint.log_debug("上传返回数据：", ret);
            JSONObject json = JSONObject.parseObject(ret);
            JSONObject.toJSONString(json, true);
            reqBean.setCode(json.get("code").toString());
        } catch (Exception e) {
            e.printStackTrace();
            reqBean.setCode("0");
        }
        return reqBean;
    }

    public String formUpload(String urlStr, Map<String, String> textMap) throws Exception {
        String res = "";
        HttpURLConnection conn = null;
        String BOUNDARY = "---------------------------123821742118716";

        URL url = new URL(urlStr);
        conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(30000);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

        OutputStream out = new DataOutputStream(conn.getOutputStream());
        if (textMap != null) {
            StringBuffer strBuf = new StringBuffer();
            Iterator iterator = textMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String inputName = (String) entry.getKey();
                String inputValue = (String) entry.getValue();
                if (inputValue != null) {
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
                    strBuf.append(inputValue);
                }
            }
            out.write(strBuf.toString().getBytes());
        }
        byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
        out.write(endData);
        out.flush();
        out.close();

        StringBuffer strBuf = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            strBuf.append(line).append("\n");
        }
        res = strBuf.toString();
        reader.close();
        return res;
    }

    public ReqBean sendUploadFile(String interfaceName, String fileName, String base64Code, String authorization) {
        String url = Config.getUrl();
        JSONObject json = new JSONObject();
        ReqBean returnData = new ReqBean();
        String code = "";
        String fileKey = "";
        long startTotal = System.currentTimeMillis();
        List<NameValuePair> nameValuePairs = new ArrayList();
        nameValuePairs.add(new BasicNameValuePair("fileName", fileName));
        nameValuePairs.add(new BasicNameValuePair("fileBody", base64Code));

        CloseableHttpClient httpclient = null;
        try {
            httpclient = createSSLClientDefault();

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            httpPost.setHeader("Authorization", authorization);
            String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
            httpPost.setHeader("User-Agent", userAgent);

            RequestConfig reqConfig = RequestConfig.custom()
                    .setConnectTimeout(8000).setConnectionRequestTimeout(5000)
                    .setSocketTimeout(8000).build();
            httpPost.setConfig(reqConfig);

            HttpResponse response = httpclient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                UtilPrint.log_debug("服务器正常响应.....", Integer.valueOf(200));
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    json = JSONObject.parseObject(EntityUtils.toString(entity));

                    JSONObject.toJSONString(json, true);
                    code = json.get("code").toString();
                    fileKey = json.get("fileKey").toString();
                }
                EntityUtils.consume(entity);
                if (code.equals("1")) {
                    returnData.setCode(fileKey);
                } else {
                    returnData.setCode(ErrorCode.ERROR_HTTPCLIENT_CODE);
                }
            } else {
                UtilPrint.log_debug("服务器异常响应.....", Integer.valueOf(statusCode));
                returnData.setCode(ErrorCode.ERROR_HTTPCLIENT_TIMEOUT_CODE);
            }
            long end = (System.currentTimeMillis() - startTotal) / 1000L;
            UtilPrint.log_debug("广州返回结果[耗时s=" + end + "]:", fileName + ">----<" + json);
            return returnData;
        } catch (Exception e) {
            e.printStackTrace();
            returnData.setCode(ErrorCode.ERROR_CC_CODE);
        } finally {
            HttpClientUtils.closeQuietly(httpclient);
        }
        long end = (System.currentTimeMillis() - startTotal) / 1000L;
        UtilPrint.log_debug("广州返回结果[耗时s=" + end + "]:", fileName + ">----<" + json);
        return returnData;
    }

    public static CloseableHttpClient createSSLClientDefault() throws Exception {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null,
                new TrustStrategy() {
                    public boolean isTrusted(X509Certificate[] x509Certificates, String s) {
                        return true;
                    }
                }).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslContext, new HostnameVerifier() {
            public boolean verify(String hostName, SSLSession sslSession) {
                return true;
            }
        });
        return HttpClients.custom().setSSLSocketFactory(sslsf).build();
    }
}
