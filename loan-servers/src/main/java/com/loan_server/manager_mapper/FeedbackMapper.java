package com.loan_server.manager_mapper;

import java.util.List;

import com.loan_entity.manager.Feedback;
import com.loan_entity.manager_vo.FeedbackVo;

public interface FeedbackMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Feedback record);

    int insertSelective(Feedback record);

    Feedback selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Feedback record);

    int updateByPrimaryKey(Feedback record);
    
    List<FeedbackVo> getFeedbackList();
}