package com.loan_manage.web_service;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(targetNamespace = "http://webservice.oper.jinhuhang.com/")
public interface OaViewPersonWebService {

   public String getPersonByPage(
            @WebParam(name = "pageindex", targetNamespace = "") Integer pageindex,
            @WebParam(name = "pagesize", targetNamespace = "") Integer pagesize
    );
}
