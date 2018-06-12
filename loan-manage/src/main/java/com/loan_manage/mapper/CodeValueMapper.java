package com.loan_manage.mapper;

import com.loan_entity.manager.CodeValue;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CodeValueMapper extends Mapper<CodeValue> {
	
	List<CodeValue> getCodeValueListByCode(String code_type);

    //根据type和code查询meaning
    String getMeaningByTypeCode(String code_type, String code_code);
}