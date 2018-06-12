package com.jhh.dao;

import java.util.List;

import com.jhh.model.BorrowingExpertsPerson;
import com.jhh.model.Person;

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
    //修改密码
    int personUpdatePassword(Person person);
    
    Person getPersonByPhone(String phone);
    //获取用户当前tokenId
    String getTokenId(String per_id);
    //通过用户来源获取用户手机和创建日期
    List<BorrowingExpertsPerson> getPhoneAndCreateDate(String source);

    //注册时间2天之内的并且合同为申请中的用户信息
    List<Person> getPersonAndBorrByDate();
}