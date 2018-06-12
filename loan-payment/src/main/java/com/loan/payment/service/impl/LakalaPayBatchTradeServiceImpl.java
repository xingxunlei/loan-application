package com.loan.payment.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loan.payment.lakala.exception.LakalaClientException;
import com.loan.payment.lakala.exception.LakalaCommonException;
import com.loan.payment.lakala.util.DigestUtil;
import com.loan.payment.lakala.util.LakalaCrossPayEnv;
import com.loan.payment.lakala.util.LakalaMsgUtil;
import com.loan.payment.service.LakalaPayBatchTradeService;
import com.loan_entity.lakala.LakalaCrossPayEncryptRequest;
import com.loan_entity.lakala.LakalaCrossPayEncryptResponse;
import com.loan_entity.lakala.batchTrade.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

@Service
public class LakalaPayBatchTradeServiceImpl implements LakalaPayBatchTradeService {

    private static final Logger logger = LoggerFactory.getLogger(LakalaPayBatchTradeServiceImpl.class);

    @Autowired
    private LakalaCrossPayRestfulService payRestfulService;

    public ApplyTradeTokenResponse applyTradeToken(ApplyTradeTokenRequest order, LakalaCrossPayEncryptRequest dataHead) {
        ApplyTradeTokenResponse res = null;
        LakalaCrossPayEncryptRequest req = LakalaMsgUtil.encryptMsg(order, dataHead);

        try {
            LakalaCrossPayEncryptResponse encryptRes = payRestfulService.doPost(LakalaCrossPayEncryptResponse.class, req, "/gate/applyBatchToken");
            if ("0000".equals(encryptRes.getRetCode())) {
                res = LakalaMsgUtil.decrypt(encryptRes, ApplyTradeTokenResponse.class);
            } else {
                res = new ApplyTradeTokenResponse();
            }
            res.setRetCode(encryptRes.getRetCode());
            res.setRetMsg(encryptRes.getRetMsg());
            res.setVer(encryptRes.getVer());
            res.setPayTypeId(encryptRes.getPayTypeId());
            res.setReqType(encryptRes.getReqType());
            res.setTs(encryptRes.getTs());
            res.setMerId(encryptRes.getMerId());
        } catch (Exception e) {
            logger.error("批量交易-申请批量交易令牌异常,secCode=" + order.getSecCode(), e);
            throw new LakalaClientException(e.getMessage(), e);
        }
        return res;
    }

    public BatUploadFileResponse uploadFile(BatUploadFileRequest order) {
        logger.info("start uplading batch file to lakala,file={},bizToken={}", new String[]{order.getFileName(), order.getBizToken()});
        BatUploadFileResponse res = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        BufferedReader bufferedReader = null;
        String filePath = order.getFilePath();
        String fileName = order.getFileName();

        try {
            File file = new File(filePath + fileName);
            FileEntity fileEntity = new FileEntity(file);
            fileEntity.setContentEncoding("UTF-8");
            fileEntity.setChunked(false);
            fileEntity.setContentType("binary/octet-stream");
            String reqUrl = LakalaCrossPayEnv.getEnvConfig().getServer() + ":" + LakalaCrossPayEnv.getEnvConfig().getPort() + "/gate/batchBizFileUpload/" + order.getMerchantId() + "/" + order.getFileName() + "/" + order.getBizToken() + "/" + order.getSign();
            HttpPost httpPost = new HttpPost(reqUrl);
            httpPost.setEntity(fileEntity);


            CloseableHttpResponse response = httpClient.execute(httpPost);
            logger.info("uploaded file to url={},and filePath={}", new String[]{reqUrl, filePath});
            int httpStatus = response.getStatusLine().getStatusCode();

            HttpEntity entity = response.getEntity();
            long contentLen = entity.getContentLength();
            StringBuffer stringBuffer = null;
            if (null != entity) {
                bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));
                String text;
                stringBuffer = new StringBuffer();
                while ((text = bufferedReader.readLine()) != null) {
                    stringBuffer.append(text).append("\r\n");
                }
            }
            logger.info("response from lakala for upload batch file is {},", new Object[]{stringBuffer.toString()});

            if (200 == httpStatus) {
                Gson json = new GsonBuilder().create();
                BatUploadFileResponse uploadFileRes = json.fromJson(stringBuffer.toString(), BatUploadFileResponse.class);

                String retCode = uploadFileRes.getRetCode();
                String rerMsg = uploadFileRes.getRetMsg();
                String sign = uploadFileRes.getSign();
                String secCode = order.getSecCode();
                //验证返回参数签名
                String localSign = DigestUtil.Encrypt(secCode + retCode + rerMsg, "MD5", "utf-8");
                if (!localSign.equals(sign)) {
                    logger.warn("批量交易-上传批量文件拉卡拉响应签名验证不通过");
                    throw new LakalaClientException("批量交易-上传批量文件拉卡拉响应签名验证不通过,bizToken=" + order.getBizToken() + ",file=" + fileName + "http status=" + httpStatus + ",responseTxt=" + stringBuffer.toString());
                }

            } else {
                logger.warn("response status from lakala is {},and responseTxt is {}", new Object[]{httpStatus, stringBuffer.toString()});
                throw new LakalaClientException("批量交易-上传批量文件异常,bizToken=" + order.getBizToken() + ",file=" + fileName + "http status=" + httpStatus + ",responseTxt=" + stringBuffer.toString());
            }
        } catch (Exception e) {
            logger.error("批量交易-上传批量文件异常,bizToken=" + order.getBizToken() + ",file=" + fileName, e);
            throw new LakalaClientException("批量交易-上传批量文件异常,bizToken=" + order.getBizToken() + ",file=" + fileName, e);
        } finally {
            try {
                httpClient.close();
                if (null != bufferedReader)
                    bufferedReader.close();
            } catch (IOException e) {
                logger.error("批量交易-上传批量文件异常,bizToken=" + order.getBizToken() + ",file=" + fileName, e);
                throw new LakalaClientException("批量交易-上传批量文件异常,bizToken=" + order.getBizToken() + ",file=" + fileName, e);
            }
        }
        return res;
    }

    public void downLoadLakalaResFile(DownLoadFileRequest req) {
        logger.info("开始下载文件,req={}", req.toString());
        String downLoadUrl = LakalaCrossPayEnv.getEnvConfig().getServer() + ":" + LakalaCrossPayEnv.getEnvConfig().getPort();
        //回盘
        if (req.isResFile()) {
            downLoadUrl += "/fileService/downLoadBckFile/" + req.getDownLoadToken();
        } else {
            //补回盘
            downLoadUrl += "/fileService/downLoadPlusBck/" + req.getDownLoadToken();
        }

        String secCode = req.getSecCode();
        String fileName = req.getFileName();
        String filePath = req.getLocalFilePath();
        String lklDigest = req.getLklDigest();

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet();
        BufferedInputStream bin = null;
        FileWriter fw = null;
        FileOutputStream fo = null;
        try {

            httpGet.setURI(new URI(downLoadUrl));
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int httpStatus = response.getStatusLine().getStatusCode();

            if (null != response.getHeaders("retCode") && response.getHeaders("retCode").length > 0) {
                String retCode = response.getHeaders("retCode")[0].getValue();
                String retMsg = URLEncodedUtils.parse(response.getHeaders("retMsg")[0].getValue(), Charset.forName("utf-8")).toString();
                if (!"0000".equals(retCode)) {
                    logger.warn("下载拉卡拉回盘文件失败,downLoadUrl=" + downLoadUrl + ",文件名为:" + fileName + ",拉卡拉服务响应=" + retMsg + ",retCode=" + retCode);
                    throw new LakalaCommonException("下载拉卡拉回盘文件失败,downLoadUrl=" + downLoadUrl + ",文件名为:" + fileName + ",拉卡拉服务响应=" + retMsg + ",retCode=" + retCode);
                }
            }

            if (200 != httpStatus) {
                throw new LakalaClientException("下载拉卡拉回盘文件异常,downLoadUrl=" + downLoadUrl + ",文件名为:" + fileName + ",拉卡拉服务响应=" + httpStatus);
            }

            bin = new BufferedInputStream(response.getEntity().getContent());
            fo = new FileOutputStream(filePath + fileName);
            byte[] contents = new byte[1024];
            int byteRead = 0;
            while ((byteRead = bin.read(contents)) != -1) {
                fo.write(contents, 0, byteRead);

            }
            //计算下载文件的摘要
            String localDigest = DigestUtil.fileDigestWithSalt(secCode, filePath + fileName);
            //比较文件摘要是否与拉卡拉消息中一致

            // ------------------------此处商户可以自行修改，是否对摘要进行判定。建议进行判定！！！
            if (!localDigest.equals(lklDigest)) {
                logger.warn("digest of {} is not correct", fileName);
                logger.debug("localDigest of {} is {}", new String[]{fileName, localDigest});
                logger.debug("lakalaDigest of {} is {}", new String[]{fileName, lklDigest});
                throw new LakalaClientException("回盘文件 " + fileName + "摘要验证失败");
            }
            // ------------------------此处商户可以自行修改，是否对摘要进行判定。建议进行判定！！！

            logger.info("{} download completed", fileName);
        } catch (LakalaCommonException e) {
            logger.error("下载拉卡拉回盘文件异常,downLoadUrl=" + downLoadUrl + ",文件名为:" + fileName, e);
            throw new LakalaClientException(e);
        } catch (ClientProtocolException e) {
            logger.error("下载拉卡拉回盘文件异常,downLoadUrl=" + downLoadUrl + ",文件名为:" + fileName, e);
            throw new LakalaClientException("下载拉卡拉回盘文件异常,downLoadUrl=" + downLoadUrl + ",文件名为:" + fileName, e);
        } catch (IOException e) {
            logger.error("下载拉卡拉回盘文件异常,downLoadUrl=" + downLoadUrl + ",文件名为:" + fileName, e);
            throw new LakalaClientException("下载拉卡拉回盘文件异常,downLoadUrl=" + downLoadUrl + ",文件名为:" + fileName, e);
        } catch (URISyntaxException e) {
            logger.error("下载拉卡拉回盘文件异常,downLoadUrl=" + downLoadUrl + ",文件名为:" + fileName, e);
            throw new LakalaClientException("下载拉卡拉回盘文件异常,downLoadUrl=" + downLoadUrl + ",文件名为:" + fileName, e);
        } catch (Exception e) {
            logger.error("下载拉卡拉回盘文件异常,downLoadUrl=" + downLoadUrl + ",文件名为:" + fileName, e);
            throw new LakalaClientException("下载拉卡拉回盘文件异常,downLoadUrl=" + downLoadUrl + ",文件名为:" + fileName, e);
        } finally {
            try {
                httpClient.close();
                if (null != fo)
                    fo.close();
                if (null != bin)
                    bin.close();
            } catch (IOException e) {
                logger.error("下载拉卡拉回盘文件异常,downLoadUrl=" + downLoadUrl + ",文件名为:" + fileName, e);
                throw new LakalaClientException("下载拉卡拉回盘文件异常,downLoadUrl=" + downLoadUrl + ",文件名为:" + fileName, e);
            }
        }
    }

    public BatchBizQueryResponse batchBizQuery(BatchBizQueryRequest order, LakalaCrossPayEncryptRequest dataHead) throws LakalaClientException {
        BatchBizQueryResponse res = null;
        LakalaCrossPayEncryptRequest req = LakalaMsgUtil.encryptMsg(order, dataHead);
        try {
            LakalaCrossPayEncryptResponse encryptRes = payRestfulService.doPost(LakalaCrossPayEncryptResponse.class, req, "/gate/batchBizQuery");
            if ("0000".equals(encryptRes.getRetCode())) {
                res = LakalaMsgUtil.decrypt(encryptRes, BatchBizQueryResponse.class);
            } else {
                res = new BatchBizQueryResponse();
            }
            res.setRetCode(encryptRes.getRetCode());
            res.setRetMsg(encryptRes.getRetMsg());
            res.setVer(encryptRes.getVer());
            res.setPayTypeId(encryptRes.getPayTypeId());
            res.setReqType(encryptRes.getReqType());
            res.setTs(encryptRes.getTs());
            res.setMerId(encryptRes.getMerId());
        } catch (Exception e) {
            logger.error("批量交易-批量交易查询异常,bizToken=" + order.getBizToekn(), e);
            throw new LakalaClientException(e.getMessage(), e);
        }
        return res;
    }
}
