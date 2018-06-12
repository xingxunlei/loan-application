package com.loan_server.manager_mapper;

import java.util.List;

import com.loan_entity.manager.CodeType;

public interface CodeTypeMapper {
	List<CodeType> getCodeTypeList();
	
    int deleteByPrimaryKey(Integer id);

    int insert(CodeType record);

    int insertSelective(CodeType record);

    CodeType selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CodeType record);

    int updateByPrimaryKey(CodeType record);
}