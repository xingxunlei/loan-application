package com.loan.payment.lakala.util;

import com.loan_entity.enums.LakalaEnv;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 拉卡拉跨境支付环境信息
 */
public class LakalaCrossPayEnv implements InitializingBean, ApplicationContextAware {

    private static final Log logger = LogFactory.getLog(LakalaCrossPayEnv.class);

    private ApplicationContext appCtx;
    private String server;
    private String port;
    private String privateKey;
    private String publicKey;
    private String merId;
    private String env;
    private static final Map<String, LakalaCrossPayClientConfig> ENV_CONFIG = new ConcurrentHashMap<String, LakalaCrossPayClientConfig>();

    @PostConstruct
    public void afterPropertiesSet() throws Exception {

        logger.info("...............初始化拉卡拉支付环境开始...............");

        LakalaCrossPayClientConfig config = new LakalaCrossPayClientConfig();
        config.setPort(port);
        config.setPrivateKey(privateKey);
        config.setPublicKey(publicKey);
        config.setServer(server);
        config.setMerId(merId);
        ENV_CONFIG.put(env, config);

        logger.info("...............初始化拉卡拉支付环境结束...............");
    }


    /**
     * 获取当前环境的配置信息
     *
     * @return
     */
    public static LakalaCrossPayClientConfig getEnvConfig() {
        Iterator<Map.Entry<String, LakalaCrossPayClientConfig>> it = ENV_CONFIG.entrySet().iterator();
        LakalaCrossPayClientConfig config = null;
        if (it.hasNext()) {
            config = it.next().getValue();
        }
        return config;

    }
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appCtx = appCtx;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setMerId(String merId) {
        this.merId = merId;
    }

    public void setEnv(String env) {
        this.env = env;
    }
}
