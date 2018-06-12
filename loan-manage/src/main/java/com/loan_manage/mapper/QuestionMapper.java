package com.loan_manage.mapper;

import com.loan_entity.manager.Question;
import com.loan_entity.manager_vo.QuestionVo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface QuestionMapper extends Mapper<Question> {

    List<QuestionVo> getAllQuestionList();
}