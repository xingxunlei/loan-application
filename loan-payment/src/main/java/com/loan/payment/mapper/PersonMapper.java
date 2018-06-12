package com.loan.payment.mapper;

import com.loan_entity.app.Person;
import com.loan_entity.app.PersonMode;
import com.loan_entity.manager_vo.CardPicInfoVo;
import com.loan_entity.manager_vo.PrivateVo;
import com.loan_entity.manager_vo.ReviewVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface PersonMapper extends Mapper<Person>{

}