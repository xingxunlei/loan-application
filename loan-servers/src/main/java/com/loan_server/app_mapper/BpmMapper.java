package com.loan_server.app_mapper;

import org.apache.ibatis.annotations.Param;

import com.loan_entity.app.Bpm;

public interface BpmMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Bpm record);

    int insertSelective(Bpm record);

    Bpm selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Bpm record);

    int updateByPrimaryKey(Bpm record);
    
    
    /**
     * 根据父节点id查询
     * @param parent_nodeid
     * @return
     */
    Bpm selectByParent(@Param("parent_nodeid")Integer parent_nodeid);
    
    
}