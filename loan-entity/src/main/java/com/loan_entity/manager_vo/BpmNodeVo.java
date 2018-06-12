package com.loan_entity.manager_vo;

import com.loan_entity.app.BpmNode;
import com.loan_entity.enums.NodeStatusEnum;

/**
 * Create by Jxl on 2017/9/12
 */
public class BpmNodeVo extends BpmNode {
    public BpmNodeVo(){
        super();
    }

    private String nodeStatus;

    private String nodeStatusStr;

    private String nodeName;

    @Override
    public String getNodeStatus() {
        return nodeStatus;
    }

    @Override
    public void setNodeStatus(String nodeStatus) {
        this.nodeStatus = nodeStatus;
        this.nodeStatusStr = NodeStatusEnum.getDescByCode(nodeStatus);
    }

    public String getNodeStatusStr() {
        return nodeStatusStr;
    }

    public void setNodeStatusStr(String nodeStatusStr) {
        this.nodeStatusStr = nodeStatusStr;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

}
