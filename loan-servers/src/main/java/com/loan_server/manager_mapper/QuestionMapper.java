package com.loan_server.manager_mapper;

import java.util.List;

import com.loan_entity.manager.Question;
import com.loan_entity.manager_vo.QuestionVo;

public interface QuestionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Question record);

    int insertSelective(Question record);

    Question selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Question record);

    int updateByPrimaryKeyWithBLOBs(Question record);

    int updateByPrimaryKey(Question record);
    
    List<Question> selectAllQuestion();
    
    List<QuestionVo> getAllQuestionList();
}