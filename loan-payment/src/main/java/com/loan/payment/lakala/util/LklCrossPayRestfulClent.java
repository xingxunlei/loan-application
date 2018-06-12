package com.loan.payment.lakala.util;

import com.google.gson.Gson;
import com.loan.payment.lakala.exception.LakalaClientException;
import com.loan_entity.lakala.LakalaCrossPaySuperRequest;
import com.loan_entity.lakala.LakalaCrossPaySuperResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 拉卡拉网络支付restful客户端
 */
@Component
public class LklCrossPayRestfulClent {

    private static final Logger logger = LoggerFactory.getLogger(LklCrossPayRestfulClent.class);


    @Autowired
    @Qualifier("restTemplate")
    private RestTemplate restTemplate;


    /**
     * <p>向拉卡拉跨境支付平台发起post请求</p>
     *
     * @param responseClazz 接受请求的类型，继承自LklCrossPaySuperRes
     * @param orderBill     请求参数
     * @param bizReq        具体业务的url
     * @param <T>           接受请求的类型，继承自LklCrossPaySuperRes
     * @return T
     * @throws LakalaClientException {@link LakalaClientException}
     */
    public <T extends LakalaCrossPaySuperResponse> T doPost(Class<T> responseClazz, LakalaCrossPaySuperRequest orderBill, String bizReq) throws LakalaClientException {
        T result = null;
        Gson json = new Gson();
        String reqUrl = LakalaCrossPayEnv.getEnvConfig().getServer() + ":" + LakalaCrossPayEnv.getEnvConfig().getPort() + bizReq;
        try {
            ResponseEntity<T> respone = restTemplate.postForEntity(reqUrl, orderBill, responseClazz);
            result = respone.getBody();
        }
        //4XX 响应吗
        catch (HttpClientErrorException e) {
            logger.debug("-------------------------------------------------{}", e.getStatusText());
            logger.debug("-------------------------------------------------{}", e.getStatusCode());
            logger.debug("response={}", e.getResponseBodyAsString());
            result = json.fromJson(e.getResponseBodyAsString(), responseClazz);
        }
        //5XX 响应吗
        catch (HttpServerErrorException e) {
            logger.debug("-------------------------------------------------{}", e.getStatusText());
            logger.debug("-------------------------------------------------{}", e.getStatusCode());
            logger.debug("response={}", e.getResponseBodyAsString());
            result = json.fromJson(e.getResponseBodyAsString(), responseClazz);
        } catch (Exception e) {
            throw new LakalaClientException(e);
        }
        return result;
    }

    /**
     * <p>向拉卡拉跨境支付平台发起post请求</p>
     *
     * @param responseClazz
     * @param queryPara
     * @param bizReq
     * @param <T>
     * @return
     * @throws LakalaClientException
     */
    public <T extends LakalaCrossPaySuperResponse> T doGet(Class<T> responseClazz, Map<String, String> queryPara, String bizReq) throws LakalaClientException {
        T result = null;
        Gson json = new Gson();
        String reqUrl = LakalaCrossPayEnv.getEnvConfig().getServer() + ":" + LakalaCrossPayEnv.getEnvConfig().getPort() + bizReq;

        try {
            ResponseEntity<T> respone = restTemplate.getForEntity(reqUrl, responseClazz, queryPara);
            result = respone.getBody();
        }
        //4XX 响应吗
        catch (HttpClientErrorException e) {
            logger.debug("-------------------------------------------------{}", e.getStatusText());
            logger.debug("-------------------------------------------------{}", e.getStatusCode());
            logger.debug("response={}", e.getResponseBodyAsString());
            result = json.fromJson(e.getResponseBodyAsString(), responseClazz);
        }
        //5XX 响应吗
        catch (HttpServerErrorException e) {
            logger.debug("-------------------------------------------------{}", e.getStatusText());
            logger.debug("-------------------------------------------------{}", e.getStatusCode());
            logger.debug("response={}", e.getResponseBodyAsString());
            result = json.fromJson(e.getResponseBodyAsString(), responseClazz);
        } catch (Exception e) {
            throw new LakalaClientException(e);
        }
        return result;
    }

}
