package com.loan_server.app_mapper;

import java.util.List;

import com.loan_entity.app.Person;
import com.loan_entity.app.PersonMode;

public interface PersonMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Person record);

    int insertSelective(Person record);

    Person selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Person record);

    int updateByPrimaryKey(Person record);
    //登录
    List<Person> userLogin(Person person);
    //忘记密码
    int updatePassword(Person person);
    //查询个人资料
    PersonMode getPersonInfo(String userId);
    //修改密码
    int personUpdatePassword(Person person);
    
    Person getPersonByPhone(String phone);
    //获取用户当前tokenId
    String getTokenId(String per_id);
    
  //查询个人资料
    PersonMode getPersonInfoByBorr(String brroid);
    //检查黑名单
    int checkBlack(String phone);

    int getPersonAvailableBorrowCount(int userId);
}