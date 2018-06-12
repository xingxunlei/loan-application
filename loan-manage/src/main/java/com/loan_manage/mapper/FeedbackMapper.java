package com.loan_manage.mapper;

import com.loan_entity.manager.Feedback;
import com.loan_entity.manager_vo.FeedbackVo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface FeedbackMapper extends Mapper<Feedback> {

    List<FeedbackVo> getFeedbackList();
}