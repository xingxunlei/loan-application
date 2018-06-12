package com.loan_server.app_mapper;

import org.apache.ibatis.annotations.Param;

import com.loan_entity.app.Card;

public interface CardMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Card record);

    int insertSelective(Card record);

    Card selectByPrimaryKey(Integer id);
    
    //根据per_id查询身份证
    Card selectByPerId(Integer per_id);
    
    //根据身份证号查询Card
    Card selectByCardNo(String card_num);
 
    int updateByPrimaryKeySelective(Card record);

    int updateByPrimaryKey(Card record);
    
    
    
    
}