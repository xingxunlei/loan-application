package com.loan_manage.service.impl;

import com.loan_manage.service.MasterService;
import com.netflix.curator.RetryPolicy;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.Assert;
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

    @Value("${dubbo.registry.address}")
    private String hosts;
    @Value("${zookeeper.retry.times}")
    private Integer retry;
    @Value("${zookeeper.connection.timeout}")
    private Integer connectTimeout;
    @Value(("${zookeeper.session.timeout}"))
    private Integer sessionTimeout;
    @Value("${zookeeper.nodes.path}")
    private String root;

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

    public Integer getRetry() {
        return retry;
    }

    public void setRetry(Integer retry) {
        this.retry = retry;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(Integer sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root + "/master";
    }


    public static void main(String[] args){
        MasterServiceImpl masterService = new MasterServiceImpl();
        masterService.setHosts("192.168.1.61:5181,192.168.1.61:5182,192.168.1.61:5182");
        masterService.setRetry(3);
        masterService.setConnectTimeout(30);
        masterService.setSessionTimeout(300);
        masterService.setRoot("/youmi/nodes");

        try {
            /*new Thread(){
                public void run(){
                    try {

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();*/
            masterService.startZookeeper();
            Thread.sleep(1000);
            System.out.println(masterService.isMaster());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
