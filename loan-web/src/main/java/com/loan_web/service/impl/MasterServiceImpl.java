package com.loan_web.service.impl;

import com.loan_utils.util.PropertiesReaderUtil;
import com.loan_web.service.MasterService;
import com.netflix.curator.RetryPolicy;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

/**
 * 是否主节点实现
 * @author wuhanhong
 * @date
 */
@Service
public class MasterServiceImpl implements MasterService {

    private final static Logger logger = LoggerFactory.getLogger(MasterServiceImpl.class);
    private CuratorFramework zk;


    private String hosts = PropertiesReaderUtil.read("dubbo","dubbo.registry.address");
    private String retry = PropertiesReaderUtil.read("dubbo","zookeeper.retry.times");
    private String connectTimeout = PropertiesReaderUtil.read("dubbo","zookeeper.connection.timeout");
    private String sessionTimeout = PropertiesReaderUtil.read("dubbo","zookeeper.session.timeout");
    private String root = PropertiesReaderUtil.read("dubbo","zookeeper.nodes.path");

    @PostConstruct
    public void startZookeeper() throws Exception {
        logger.debug("start zookeeper ...");
        RetryPolicy retryPolicy = new RetryUntilElapsed(1000,1000);
        zk = CuratorFrameworkFactory.newClient(hosts, retryPolicy);
        zk.start();
    }

    @PreDestroy
    private void stopZookeeper() throws Exception {
        if(zk != null) {
            logger.debug("stop zookeeper ...");
            zk.close();
        }
    }

    @Override
    public boolean isMaster() {
        try {
            if(zk == null || zk.getZookeeperClient() == null || zk.getZookeeperClient().getZooKeeper() == null ){
                return false;
            }

            Long sessionId = zk.getZookeeperClient().getZooKeeper().getSessionId();
            List<String> children = null;

            if(zk.checkExists().forPath(root) != null){
                children = zk.getChildren().forPath(root);
            }
            if(children == null || children.isEmpty()){
                zk.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(root+"/"+sessionId,"".getBytes());
                return true;
            }else{
                String child = children.get(0);
                if(sessionId.toString().equals(child)){
                    return true;
                }
            }
        } catch (Exception e) {
            logger.warn("create node failed....",e);
        }
        return false;
    }


    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public String getRetry() {
        return retry;
    }

    public void setRetry(String retry) {
        this.retry = retry;
    }

    public String getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(String connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(String sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root + "/master";
    }


    public static void main(String[] args){
//        MasterServiceImpl masterService = new MasterServiceImpl();
//        masterService.setHosts("192.168.1.61:5181,192.168.1.61:5182,192.168.1.61:5182");
//        masterService.setRetry(3);
//        masterService.setConnectTimeout(30);
//        masterService.setSessionTimeout(300);
//        masterService.setRoot("/youmi/nodes");
//
//        try {
//            /*new Thread(){
//                public void run(){
//                    try {
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }.start();*/
//            masterService.startZookeeper();
//            Thread.sleep(1000);
//            System.out.println(masterService.isMaster());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
