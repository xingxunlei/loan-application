package com.loan_server.app_mapper;

import java.util.List;

import com.loan_entity.app.Contact;
import org.apache.ibatis.annotations.Param;

public interface ContactMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Contact record);

    int insertSelective(Contact record);

    Contact selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Contact record);

    int updateByPrimaryKey(Contact record);
    
    //获取用户通讯录
    List<Contact> selectAllByPerId(Integer per_id);
    
    //删除用户通讯录
    int deleteByPerId(Integer per_id);

    Contact selectOne(String per_id);

    void updateStatusByPerId(@Param("status") int status,@Param("perId") String perId);
}