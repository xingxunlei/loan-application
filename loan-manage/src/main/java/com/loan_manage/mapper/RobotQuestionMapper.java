package com.loan_manage.mapper;

import com.loan_manage.entity.OrderRobot;
import com.loan_manage.entity.RobotQuestion;
import tk.mybatis.mapper.common.Mapper;

public interface RobotQuestionMapper extends Mapper<RobotQuestion> {

    int insertRobotQuestion(RobotQuestion robotQuestion);

}
