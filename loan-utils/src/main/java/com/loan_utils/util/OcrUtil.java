package com.loan_utils.util;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

public class OcrUtil {

    // FACE DETECT地址
    private static String DETECT_URL = PropertiesReaderUtil.read("sign", "faceDetectUrl");
    // private static String DETECT_URL =
    // "https://api.faceid.com/faceid/v1/detect";

    // ORC地址
    private static String OCR_URL = PropertiesReaderUtil.read("sign", "faceOcrUrl");
    // private static String OCR_URL =
    // "https://api.faceid.com/faceid/v1/ocridcard";

    // verify地址
    private static String VERIFY_URL = PropertiesReaderUtil.read("sign", "verifyUrl");
    // private static String VERIFY_URL =
    // "https://api.megvii.com/faceid/v2/verify";

    // OCR key
    private static String OCR_KEY = PropertiesReaderUtil.read("sign", "faceOcrKey");
    // private static String OCR_KEY = "EeWHpRcOgtuMPbpzActRaRPwAfm7t9w2";

    // OCR secret
    private static String OCR_SECRET = PropertiesReaderUtil.read("sign", "faceOcrSecret");
    // private static String OCR_SECRET = "rPLncIKeF6Ad3RpzzdQX9CrghhMMmBEw";

    /**
     * 身份证OCR请求第三方方法
     *
     * @param url
     * @param file
     * @param api_key
     * @param api_secret
     * @return
     * @throws Exception
     */
    public static String faceOcr(String url, MultipartFile file, String api_key, String api_secret)
            throws Exception {
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
            // 返回身份证照片合法性检查结果，值只取“0”或“1”。“1”：返回； “0”：不返回。默认“0”。
            StringBody legality = new StringBody("1", ContentType.create("text/plain", Consts.UTF_8));

            String fileName = file.getOriginalFilename();

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    // .addPart("image", bin)//相当于<input type="file"
                    // name="media"/>
                    .addBinaryBody("image", file.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName)
                    .addPart("api_key", key).addPart("api_secret", secret)// 相当于<input
                    // type="text"
                    // name="name"
                    // value=name>
                    .addPart("legality", legality).build();

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

    /**
     * 人脸识别请求第三方meglive方法post请求
     *

     * @return
     * @throws Exception
     */
    public static String faceVerifyMegPost(String url, MultipartFile imageBest, String apiKey, String apiSecret,
                                           String idCardName, String idCardNumber, String delta)
            throws Exception {
        CloseableHttpClient httpClient = HttpTools.createSSLClientDefault();
        String res = "";
        try {
            // 把一个普通参数和文件上传给下面这个地址 是一个servlet
            HttpPost httpPost = new HttpPost(url);
            // 把文件转换成流对象FileBody
            // FileBody bin = new FileBody(file);
            // 普通字段 重新设置了编码方式
            StringBody api_key = new StringBody(apiKey, ContentType.create("text/plain", Consts.UTF_8));
            // StringBody comment = new StringBody("这里是一个评论",
            // ContentType.TEXT_PLAIN);

            StringBody api_secret = new StringBody(apiSecret, ContentType.create("text/plain", Consts.UTF_8));
            // 确定本次比对为“有源比对”或“无源比对”。取值只为“1”或“0”
            StringBody comparison_type = new StringBody("1", ContentType.create("text/plain", Consts.UTF_8));
            // 确定待比对图片的类型。取值只为“meglive”、“facetoken”、“raw_image”三者之一
            StringBody face_image_type = new StringBody("meglive", ContentType.create("text/plain", Consts.UTF_8));

            StringBody idcard_name = new StringBody(idCardName, ContentType.create("text/plain", Consts.UTF_8));

            StringBody idcard_number = new StringBody(idCardNumber, ContentType.create("text/plain", Consts.UTF_8));

            String fileName = imageBest.getOriginalFilename();

//            HttpEntity reqEntity = MultipartEntityBuilder.create()
//                    // .addPart("image", bin)//相当于<input type="file"
//                    // name="media"/>
//                    .addBinaryBody("image", file.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName)
//                    .addPart("api_key", api_key).addPart("api_secret", api_secret)
//                    .addPart("face_image_type", face_image_type).addPart("idcard_name", idcard_name)
//                    .addPart("idcard_number", idcard_number).addPart("comparison_type", comparison_type).build();

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();


            // 根据type不同 传递不同参数 type为meglive，参数为delta，image_best


            StringBody del = new StringBody(delta, ContentType.create("text/plain", Consts.UTF_8));

            builder.addBinaryBody("image_best", imageBest.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName)
                    .addPart("api_key", api_key).addPart("api_secret", api_secret)
                    .addPart("face_image_type", face_image_type).addPart("idcard_name", idcard_name)
                    .addPart("idcard_number", idcard_number).addPart("comparison_type", comparison_type)
                    .addPart("delta", del);

            HttpEntity reqEntity = builder.build();

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

    /**
     * 人脸识别请求第三方meglive方法post请求（2张图）重载方法
     *
     * @param url

     * @throws Exception
     */
    public static String faceVerifyMegPost(String url, MultipartFile imageBest, MultipartFile imageEnv, String apiKey,
                                           String apiSecret,
                                           String idCardName, String idCardNumber, String delta) throws Exception {
        CloseableHttpClient httpClient = HttpTools.createSSLClientDefault();
        String res = "";
        try {
            // 把一个普通参数和文件上传给下面这个地址 是一个servlet
            HttpPost httpPost = new HttpPost(url);
            // 把文件转换成流对象FileBody
            // FileBody bin = new FileBody(file);
            // 普通字段 重新设置了编码方式
            StringBody api_key = new StringBody(apiKey, ContentType.create("text/plain", Consts.UTF_8));
            // StringBody comment = new StringBody("这里是一个评论",
            // ContentType.TEXT_PLAIN);

            StringBody api_secret = new StringBody(apiSecret, ContentType.create("text/plain", Consts.UTF_8));
            // 确定本次比对为“有源比对”或“无源比对”。取值只为“1”或“0”
            StringBody comparison_type = new StringBody("1", ContentType.create("text/plain", Consts.UTF_8));
            // 确定待比对图片的类型。取值只为“meglive”、“facetoken”、“raw_image”三者之一
            StringBody face_image_type = new StringBody("meglive", ContentType.create("text/plain", Consts.UTF_8));

            StringBody idcard_name = new StringBody(idCardName, ContentType.create("text/plain", Consts.UTF_8));

            StringBody idcard_number = new StringBody(idCardNumber, ContentType.create("text/plain", Consts.UTF_8));

            String fileName = imageBest.getOriginalFilename();

            String fileNameEnv = imageEnv.getOriginalFilename();

            // HttpEntity reqEntity = MultipartEntityBuilder.create()
            // // .addPart("image", bin)//相当于<input type="file"
            // // name="media"/>
            // .addBinaryBody("image", file.getInputStream(),
            // ContentType.MULTIPART_FORM_DATA, fileName)
            // .addPart("api_key", api_key).addPart("api_secret", api_secret)
            // .addPart("face_image_type",
            // face_image_type).addPart("idcard_name", idcard_name)
            // .addPart("idcard_number",
            // idcard_number).addPart("comparison_type",
            // comparison_type).build();

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            // 根据type不同 传递不同参数 type为meglive，参数为delta，image_best

            StringBody del = new StringBody(delta, ContentType.create("text/plain", Consts.UTF_8));

            builder.addBinaryBody("image_best", imageBest.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName)
                    .addBinaryBody("image_env", imageEnv.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileNameEnv)
                    .addPart("api_key", api_key).addPart("api_secret", api_secret)
                    .addPart("face_image_type", face_image_type).addPart("idcard_name", idcard_name)
                    .addPart("idcard_number", idcard_number).addPart("comparison_type", comparison_type)
                    .addPart("delta", del);

            HttpEntity reqEntity = builder.build();

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

    public static String faceVerifyMegPost2(String url, File file, String apiKey, String apiSecret, String idCardName,
                                            String idCardNumber, String delta) throws Exception {
        CloseableHttpClient httpClient = HttpTools.createSSLClientDefault();
        String res = "";
        try {
            // 把一个普通参数和文件上传给下面这个地址 是一个servlet
            HttpPost httpPost = new HttpPost(url);
            // 把文件转换成流对象FileBody
            // FileBody bin = new FileBody(file);
            // 普通字段 重新设置了编码方式
            StringBody api_key = new StringBody(apiKey, ContentType.create("text/plain", Consts.UTF_8));
            // StringBody comment = new StringBody("这里是一个评论",
            // ContentType.TEXT_PLAIN);

            StringBody api_secret = new StringBody(apiSecret, ContentType.create("text/plain", Consts.UTF_8));
            // 确定本次比对为“有源比对”或“无源比对”。取值只为“1”或“0”
            StringBody comparison_type = new StringBody("1", ContentType.create("text/plain", Consts.UTF_8));
            // 确定待比对图片的类型。取值只为“meglive”、“facetoken”、“raw_image”三者之一
            StringBody face_image_type = new StringBody("meglive", ContentType.create("text/plain", Consts.UTF_8));

            StringBody idcard_name = new StringBody(idCardName, ContentType.create("text/plain", Consts.UTF_8));

            StringBody idcard_number = new StringBody(idCardNumber, ContentType.create("text/plain", Consts.UTF_8));


            // HttpEntity reqEntity = MultipartEntityBuilder.create()
            // // .addPart("image", bin)//相当于<input type="file"
            // // name="media"/>
            // .addBinaryBody("image", file.getInputStream(),
            // ContentType.MULTIPART_FORM_DATA, fileName)
            // .addPart("api_key", api_key).addPart("api_secret", api_secret)
            // .addPart("face_image_type",
            // face_image_type).addPart("idcard_name", idcard_name)
            // .addPart("idcard_number",
            // idcard_number).addPart("comparison_type",
            // comparison_type).build();

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            // 根据type不同 传递不同参数 type为meglive，参数为delta，image_best

            StringBody del = new StringBody(delta, ContentType.create("text/plain", Consts.UTF_8));

            builder.addBinaryBody("image_best", file, ContentType.MULTIPART_FORM_DATA, file.getName())
                    .addPart("api_key", api_key).addPart("api_secret", api_secret)
                    .addPart("face_image_type", face_image_type).addPart("idcard_name", idcard_name)
                    .addPart("idcard_number", idcard_number).addPart("comparison_type", comparison_type)
                    .addPart("delta", del);

            HttpEntity reqEntity = builder.build();

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

    /**
     * 人脸识别请求第三方raw_image方法post请求(参数为File)
     *
     * @param url
     * @param file
     * @param apiKey
     * @param apiSecret
     * @param idCardName
     * @param idCardNumber
     * @return
     * @throws Exception
     */
    public static String faceVerifyRawPost(String url, File file, String apiKey, String apiSecret,
                                           String idCardName, String idCardNumber, String qualityThreshold) throws Exception {
        CloseableHttpClient httpClient = HttpTools.createSSLClientDefault();
        String res = "";
        try {
            // 把一个普通参数和文件上传给下面这个地址 是一个servlet
            HttpPost httpPost = new HttpPost(url);
            // 把文件转换成流对象FileBody
            // FileBody bin = new FileBody(file);
            // 普通字段 重新设置了编码方式
            StringBody api_key = new StringBody(apiKey, ContentType.create("text/plain", Consts.UTF_8));
            // StringBody comment = new StringBody("这里是一个评论",
            // ContentType.TEXT_PLAIN);

            StringBody api_secret = new StringBody(apiSecret, ContentType.create("text/plain", Consts.UTF_8));
            // 确定本次比对为“有源比对”或“无源比对”。取值只为“1”或“0”O
            StringBody comparison_type = new StringBody("1", ContentType.create("text/plain", Consts.UTF_8));
            // 确定待比对图片的类型。取值只为“meglive”、“facetoken”、“raw_image”三者之一
            StringBody face_image_type = new StringBody("raw_image", ContentType.create("text/plain", Consts.UTF_8));

            StringBody idcard_name = new StringBody(idCardName, ContentType.create("text/plain", Consts.UTF_8));

            StringBody idcard_number = new StringBody(idCardNumber, ContentType.create("text/plain", Consts.UTF_8));

            StringBody face_quality_threshold = new StringBody(qualityThreshold,
                    ContentType.create("text/plain", Consts.UTF_8));


            // HttpEntity reqEntity = MultipartEntityBuilder.create()
            // // .addPart("image", bin)//相当于<input type="file"
            // // name="media"/>
            // .addBinaryBody("image", file.getInputStream(),
            // ContentType.MULTIPART_FORM_DATA, fileName)
            // .addPart("api_key", api_key).addPart("api_secret", api_secret)
            // .addPart("face_image_type",
            // face_image_type).addPart("idcard_name", idcard_name)
            // .addPart("idcard_number",
            // idcard_number).addPart("comparison_type",
            // comparison_type).build();

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            // 根据type不同 传递不同参数 type为meglive，参数为delta，image_best


            builder.addBinaryBody("image", file, ContentType.MULTIPART_FORM_DATA, file.getName())
                    .addPart("api_key", api_key).addPart("api_secret", api_secret)
                    .addPart("face_image_type", face_image_type).addPart("idcard_name", idcard_name)
                    .addPart("idcard_number", idcard_number).addPart("comparison_type", comparison_type)
                    .addPart("face_quality_threshold", face_quality_threshold);

            HttpEntity reqEntity = builder.build();

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

    /**
     * 人脸识别请求第三方raw_image方法post请求(参数为MultiPartFile)
     *
     * @param url
     * @param file
     * @param apiKey
     * @param apiSecret
     * @param idCardName
     * @param idCardNumber
     * @return
     * @throws Exception
     */
    public static String faceVerifyRawPost(String url, MultipartFile file, String apiKey, String apiSecret,
                                           String idCardName, String idCardNumber, String qualityThreshold) throws Exception {
        CloseableHttpClient httpClient = HttpTools.createSSLClientDefault();
        String res = "";
        try {
            // 把一个普通参数和文件上传给下面这个地址 是一个servlet
            HttpPost httpPost = new HttpPost(url);
            // 把文件转换成流对象FileBody
            // FileBody bin = new FileBody(file);
            // 普通字段 重新设置了编码方式
            StringBody api_key = new StringBody(apiKey, ContentType.create("text/plain", Consts.UTF_8));
            // StringBody comment = new StringBody("这里是一个评论",
            // ContentType.TEXT_PLAIN);

            StringBody api_secret = new StringBody(apiSecret, ContentType.create("text/plain", Consts.UTF_8));
            // 确定本次比对为“有源比对”或“无源比对”。取值只为“1”或“0”O
            StringBody comparison_type = new StringBody("1", ContentType.create("text/plain", Consts.UTF_8));
            // 确定待比对图片的类型。取值只为“meglive”、“facetoken”、“raw_image”三者之一
            StringBody face_image_type = new StringBody("raw_image", ContentType.create("text/plain", Consts.UTF_8));

            StringBody idcard_name = new StringBody(idCardName, ContentType.create("text/plain", Consts.UTF_8));

            StringBody idcard_number = new StringBody(idCardNumber, ContentType.create("text/plain", Consts.UTF_8));

            StringBody face_quality_threshold = new StringBody(qualityThreshold,
                    ContentType.create("text/plain", Consts.UTF_8));

            StringBody return_faces = new StringBody("1", ContentType.create("text/plain", Consts.UTF_8));

            String fileName = file.getOriginalFilename();

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            // 根据type不同 传递不同参数 type为meglive，参数为delta，image_best

            builder.addBinaryBody("image", file.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName)
                    .addPart("api_key", api_key).addPart("api_secret", api_secret)
                    .addPart("face_image_type", face_image_type).addPart("idcard_name", idcard_name)
                    .addPart("idcard_number", idcard_number).addPart("comparison_type", comparison_type)
                    .addPart("face_quality_threshold", face_quality_threshold).addPart("return_faces", return_faces);

            HttpEntity reqEntity = builder.build();

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

    /**
     * 人脸识别raw
     *
     * @param file
     * @param idCardName
     * @param idCardNumber
     * @return
     */
    public static String faceVerifyRaw(File file, String idCardName, String idCardNumber) {
        String response = "201";
        try {

            // 2017-06-19 为了避免low_quality 把face_quality_threshold质量阈值改为25
            response = faceVerifyRawPost(VERIFY_URL, file, OCR_KEY, OCR_SECRET,
                    idCardName, idCardNumber, "0");

            // System.out.println(response);
            System.out.println("身份证号:" + idCardNumber + "的raw方法返回结果：" + response);


            JSONObject obj = JSONObject.parseObject(response);
            String error = obj.getString("error_message");
            if (!(error == null || "".equals(error))) {

                // 请求错误 传201
                response = "201";

            } else {// 正确请求返回信息

                JSONObject result_faceid = obj.getJSONObject("result_faceid");
                Double confidence = result_faceid.getDouble("confidence");
                JSONObject thresholds = result_faceid.getJSONObject("thresholds");
                Double e3 = thresholds.getDouble("1e-3");
                if (confidence > e3) {
                    // 成功返回200
                    response = "200";
                } else {
                    // 认证失败 返回202
                    response = "202";
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            return "201";
        }
        return response;
    }

    /**
     * 人脸识别raw(参数为multipartFile)
     *
     * @param file
     * @param idCardName
     * @param idCardNumber
     * @return
     */
    public static String faceVerifyRaw(MultipartFile file, String idCardName, String idCardNumber) {
        String response = "201";
        try {

            // 2017-06-19 为了避免low_quality 把face_quality_threshold质量阈值改为25
            response = faceVerifyRawPost(VERIFY_URL, file, OCR_KEY, OCR_SECRET, idCardName, idCardNumber, "0");

            System.out.println("身份证号:" + idCardNumber + "的raw方法返回结果：" + response);

            JSONObject obj = JSONObject.parseObject(response);
            String error = obj.getString("error_message");
            if (!(error == null || "".equals(error))) {

                // 请求错误 传201
                return response;

            } else {// 正确请求返回信息

                JSONObject result_faceid = obj.getJSONObject("result_faceid");
                Double confidence = result_faceid.getDouble("confidence");
                JSONObject thresholds = result_faceid.getJSONObject("thresholds");

                Double e3 = thresholds.getDouble("1e-3");
                if (confidence > e3) {
                    // 成功返回200
                    response = "200";
                }
                // else {
                // // 认证失败 返回202
                // response = "202";
                // }

            }

        } catch (Exception e) {
            e.printStackTrace();
            return "201";
        }
        return response;
    }

    /**
     * 人脸识别meg(一张图)
     *

     * @param idCardName
     * @param idCardNumber
     * @return
     */
    public static String faceVerifyMeg(MultipartFile imageBest, String idCardName, String idCardNumber, String delta) {
        String response = "201";
        try {

            response = faceVerifyMegPost(VERIFY_URL, imageBest, OCR_KEY, OCR_SECRET, idCardName, idCardNumber, delta);

            // System.out.println("face++没有env参数的返回报文json:" + response);

            JSONObject obj = JSONObject.parseObject(response);
            String error = obj.getString("error_message");
            if (!(error == null || "".equals(error))) {

                // 请求错误 传201
                response = "201";

            } else {// 正确请求返回信息

                //有源比对时，数据源人脸照片与待验证人脸照的比对结果。此字段只在接口被成功调用时返回。
                JSONObject result_faceid = obj.getJSONObject("result_faceid");

                // 有源对比flag
                boolean flag1 = false;
                Double confidence = result_faceid.getDouble("confidence");
                JSONObject thresholds = result_faceid.getJSONObject("thresholds");
                Double e3 = thresholds.getDouble("1e-3");
                if (confidence > e3) {
                    flag1 = true;
                }

                if (flag1) {
                    return "200";
                } else {
                    return "202";
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            return "201";
        }
        return response;
    }

    /**
     * 人脸识别meg(2张图)
     *

     * @param idCardName
     * @param idCardNumber
     * @return
     */
    public static String faceVerifyMeg(MultipartFile imageBest, MultipartFile imageEnv, String idCardName,
                                       String idCardNumber, String delta) {
        String response = "201";
        try {

            response = faceVerifyMegPost(VERIFY_URL, imageBest, imageEnv, OCR_KEY, OCR_SECRET, idCardName, idCardNumber,
                    delta);

            System.out.println("身份证号:" + idCardNumber + "的meglive方法返回结果：" + response);

            JSONObject obj = JSONObject.parseObject(response);
            String error = obj.getString("error_message");
            if (!(error == null || "".equals(error))) {

                // 请求错误 传201
                response = "201";

            } else {// 正确请求返回信息

                // 有源比对时，数据源人脸照片与待验证人脸照的比对结果。此字段只在接口被成功调用时返回。
                JSONObject result_faceid = obj.getJSONObject("result_faceid");

                // 有源对比flag
                boolean flag1 = false;
                Double confidence = result_faceid.getDouble("confidence");
                JSONObject thresholds = result_faceid.getJSONObject("thresholds");
                // Double e = thresholds.getDouble("1e-3");
                // 2017-06-19更新 改取1e-4
                Double e = thresholds.getDouble("1e-4");
                if (confidence > e) {
                    flag1 = true;
                }

                // 该字段表示待比对的脸的真实性。“真实的人脸”是指待比对的人脸图像是真实人脸的拍摄，而不是戴面具的脸、通过软件人工合成的脸、或是屏幕翻拍回放的脸。本字段返回真实性检查结果以及用作参考的相关阈值。
                JSONObject face_genuineness = obj.getJSONObject("face_genuineness");
                Double synthetic_face_confidence = face_genuineness.getDouble("synthetic_face_confidence");
                Double synthetic_face_threshold = face_genuineness.getDouble("synthetic_face_threshold");

                // 是否软件合成flag
                boolean flag2 = false;

                if (synthetic_face_confidence < synthetic_face_threshold) {
                    // 可以认为人脸不是软件合成脸。
                    flag2 = true;
                }

                Double mask_confidence = face_genuineness.getDouble("mask_confidence");
                Double mask_threshold = face_genuineness.getDouble("mask_threshold");

                // 是否面具flag
                boolean flag3 = false;
                if (mask_confidence < mask_threshold) {
                    // 可以认为人脸不是面具
                    flag3 = true;
                }

                Double screen_replay_confidence = face_genuineness.getDouble("screen_replay_confidence");

                Double screen_replay_threshold = face_genuineness.getDouble("screen_replay_threshold");
                // 风控暂定0.7
                // Double screen_replay_threshold = 0.7;

                // 是否屏幕翻拍flag
                boolean flag4 = false;
                if (screen_replay_confidence < screen_replay_threshold) {
                    // 可以认为人脸不是屏幕翻拍
                    flag4 = true;
                }

                Integer face_replaced = face_genuineness.getInteger("face_replaced");
                // 是否换脸攻击 flag
                boolean flag5 = true;
                // 1表示检测出了换脸攻击
                if (face_replaced != null && face_replaced == 1) {
                    flag5 = false;
                }

                // 所有flag都通过 才通过 否则202
                // System.out.println("flag1:" + flag1);
                // System.out.println("flag2:" + flag2);
                // System.out.println("flag3:" + flag3);
                // System.out.println("flag4:" + flag4);
                // System.out.println("flag5:" + flag5);
                if (flag1 && flag2 && flag3 && flag4 && flag5) {
                    return "200";
                } else {
                    return "202";
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            return "201";
        }
        return response;
    }

    /**
     * face++正面OCR
     *

     * @param cardFile
     * @return
     */
    public static String cardOcr(MultipartFile cardFile) {

        String response = "201";
        try {

            // 1. 头像小图detect 2017-06-19更新 不调detect了

            // if (headFile != null) {
            // String detectResponse = faceOcr(DETECT_URL, headFile, OCR_KEY,
            // OCR_SECRET);
            //
            // JSONObject detect = JSONObject.parseObject(detectResponse);
            // String error = detect.getString("error");
            // if (!(error == null || "".equals(error))) {// 请求错误 传201
            //
            // return response;
            //
            // } else {// 正确请求返回信息
            //
            // JSONArray faces =
            // JSONObject.parseArray(detect.getString("faces"));
            // if (faces.isEmpty()) {
            // return "202";
            // }
            // JSONObject face = faces.getJSONObject(0);
            // String quality = face.getString("quality").substring(0, 3);
            // String quality_threshold = face.getString("quality_threshold");
            // // 如果detect没通过 直接给前台返回照片质量低
            // if (Double.valueOf(quality) < Double.valueOf(quality_threshold))
            // {
            // return "202";
            // }
            // }
            // }

            // 2. 身份证大图ocr

            response = faceOcr(OCR_URL, cardFile, OCR_KEY, OCR_SECRET);

            // System.out.println(response);
            JSONObject obj = JSONObject.parseObject(response);

            String legality = obj.getString("legality");

            JSONObject l = JSONObject.parseObject(legality);

            Double edited = l.getDouble("Edited");
            Double idPhoto = l.getDouble("ID Photo");
            Double Temporary = l.getDouble("Temporary ID Photo");
            Double photocopy = l.getDouble("Photocopy");
            Double screen = l.getDouble("Screen");
            if (idPhoto < 0.80 || edited > 0.1 || Temporary > 0.1 || photocopy > 0.1 || screen > 0.1) {
                /**
                 * 重新拍
                 */
                response = "202";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "201";
        }
        return response;
    }

    /**
     * face++反面OCR
     *
     * @param cardFile
     * @return
     */
    public static String cardOcrF(MultipartFile cardFile) {

        String response = "201";
        try {

            // 2. 身份证反面大图ocr

            response = faceOcr(OCR_URL, cardFile, OCR_KEY, OCR_SECRET);


            JSONObject obj = JSONObject.parseObject(response);

            String legality = obj.getString("legality");

            JSONObject l = JSONObject.parseObject(legality);

            Double edited = l.getDouble("Edited");
            Double idPhoto = l.getDouble("ID Photo");
            Double Temporary = l.getDouble("Temporary ID Photo");
            Double photocopy = l.getDouble("Photocopy");
            Double screen = l.getDouble("Screen");
            if (idPhoto < 0.80 || edited > 0.1 || Temporary > 0.1 || photocopy > 0.1 || screen > 0.1) {
                /**
                 * 重新拍
                 */
                response = "202";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "201";
        }
        return response;
    }

    public static void main(String[] args) throws Exception {

        String res = "{\"id_exceptions\": {\"id_photo_monochrome\": 0, \"id_attacked\": 0}, \"face_genuineness\": {\"mask_confidence\": 0.0, \"screen_replay_confidence\": 0.018, \"mask_threshold\": 0.5, \"synthetic_face_confidence\": 0.639, \"synthetic_face_threshold\": 0.5, \"screen_replay_threshold\": 0.5, \"face_replaced\": 0}, \"request_id\": \"1499080417,e03be82e-3746-4594-9535-e1ca527c62ca\", \"time_used\": 1534, \"result_faceid\": {\"confidence\": 89.075, \"thresholds\": {\"1e-3\": 62.169, \"1e-5\": 74.399, \"1e-4\": 69.315, \"1e-6\": 78.038}}}";



        JSONObject obj = JSONObject.parseObject(res);
        String error = obj.getString("error_message");
        if (!(error == null || "".equals(error))) {

            // 请求错误 传201
            System.out.println("201");

        } else {// 正确请求返回信息

            // 有源比对时，数据源人脸照片与待验证人脸照的比对结果。此字段只在接口被成功调用时返回。
            JSONObject result_faceid = obj.getJSONObject("result_faceid");

            // 有源对比flag
            boolean flag1 = false;
            Double confidence = result_faceid.getDouble("confidence");
            JSONObject thresholds = result_faceid.getJSONObject("thresholds");
            // Double e = thresholds.getDouble("1e-3");
            // 2017-06-19更新 改取1e-4
            Double e = thresholds.getDouble("1e-4");
            if (confidence > e) {
                flag1 = true;
            }

            // 该字段表示待比对的脸的真实性。“真实的人脸”是指待比对的人脸图像是真实人脸的拍摄，而不是戴面具的脸、通过软件人工合成的脸、或是屏幕翻拍回放的脸。本字段返回真实性检查结果以及用作参考的相关阈值。
            JSONObject face_genuineness = obj.getJSONObject("face_genuineness");
            Double synthetic_face_confidence = face_genuineness.getDouble("synthetic_face_confidence");
            Double synthetic_face_threshold = face_genuineness.getDouble("synthetic_face_threshold");

            // 是否软件合成flag
            boolean flag2 = false;

            if (synthetic_face_confidence < synthetic_face_threshold) {
                // 可以认为人脸不是软件合成脸。
                flag2 = true;
            }

            Double mask_confidence = face_genuineness.getDouble("mask_confidence");
            Double mask_threshold = face_genuineness.getDouble("mask_threshold");

            // 是否面具flag
            boolean flag3 = false;
            if (mask_confidence < mask_threshold) {
                // 可以认为人脸不是面具
                flag3 = true;
            }

            Double screen_replay_confidence = face_genuineness.getDouble("screen_replay_confidence");

            Double screen_replay_threshold = face_genuineness.getDouble("screen_replay_threshold");
            // 风控暂定0.7
            // Double screen_replay_threshold = 0.7;

            // 是否屏幕翻拍flag
            boolean flag4 = false;
            if (screen_replay_confidence < screen_replay_threshold) {
                // 可以认为人脸不是屏幕翻拍
                flag4 = true;
            }

            Integer face_replaced = face_genuineness.getInteger("face_replaced");
            // 是否换脸攻击 flag
            boolean flag5 = true;
            // 1表示检测出了换脸攻击
            if (face_replaced != null && face_replaced == 1) {
                flag5 = false;
            }

            // 所有flag都通过 才通过 否则202
            // System.out.println("flag1:" + flag1);
            // System.out.println("flag2:" + flag2);
            // System.out.println("flag3:" + flag3);
            // System.out.println("flag4:" + flag4);
            // System.out.println("flag5:" + flag5);
            if (flag1 && flag2 && flag3 && flag4 && flag5) {
                System.out.println("chenggong");
            }
        }
    }
}