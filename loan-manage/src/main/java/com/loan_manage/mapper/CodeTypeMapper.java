package com.loan_manage.mapper;

import com.loan_entity.manager.CodeType;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CodeTypeMapper extends Mapper<CodeType> {

	List<CodeType> getCodeTypeList();
}