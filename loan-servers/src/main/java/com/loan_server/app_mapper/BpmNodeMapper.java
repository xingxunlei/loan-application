package com.loan_server.app_mapper;

import java.util.List;

import com.loan_entity.app.BpmNode;

public interface BpmNodeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BpmNode record);

    int insertSelective(BpmNode record);

    BpmNode selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BpmNode record);

    int updateByPrimaryKey(BpmNode record);
    //查询当前流程下已认证，最大的node_id
    Integer getNodeId(Integer bpm_id);
    
    //查询当前流程下，当前node_id对应的最新明细
    BpmNode selectByNode_id(Integer bpm_id,Integer node_id);
    
    //查询当前person 当前节点 BpmNode
    BpmNode selectByPerNode(Integer per_id,Integer node_id);
    
    //删除当前流程下某一节点
    int deleteByBpmNode(Integer bpm_id,Integer node_id);

    List<String> selectMaxNodeId(String idType, Integer idValue);

    List<BpmNode> selectAllNodes(List<String> nodeIds);

}