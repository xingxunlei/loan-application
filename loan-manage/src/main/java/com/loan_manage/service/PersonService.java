package com.loan_manage.service;


import com.alibaba.fastjson.JSONArray;
import com.loan_entity.app.Person;

public interface PersonService {
	public String getPhone(Integer perId) ;

	Person selectPersonByPersonId(Integer personId);
	
	Person getPersonByPhone(String phone);

	void syncOa();

	String getNameByPersonId(Integer personId);

    void syncLevelBack();
}
