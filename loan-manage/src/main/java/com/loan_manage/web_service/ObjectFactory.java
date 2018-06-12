
package com.loan_manage.web_service;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    private final static QName _GetPersonByPage_QNAME = new QName("http://webservice.oper.jinhuhang.com/", "getPersonByPage");
    private final static QName _GetPersonByPageResponse_QNAME = new QName("http://webservice.oper.jinhuhang.com/", "getPersonByPageResponse");

    public ObjectFactory() {
    }

    public GetPersonByPage createGetPersonByPage() {
        return new GetPersonByPage();
    }

    public GetPersonByPageResponse createGetPersonByPageResponse() {
        return new GetPersonByPageResponse();
    }

    @XmlElementDecl(namespace = "http://webservice.oper.jinhuhang.com/", name = "getPersonByPage")
    public JAXBElement<GetPersonByPage> createGetPersonByPage(GetPersonByPage value) {
        return new JAXBElement<GetPersonByPage>(_GetPersonByPage_QNAME, GetPersonByPage.class, null, value);
    }

    @XmlElementDecl(namespace = "http://webservice.oper.jinhuhang.com/", name = "getPersonByPageResponse")
    public JAXBElement<GetPersonByPageResponse> createGetPersonByPageResponse(GetPersonByPageResponse value) {
        return new JAXBElement<GetPersonByPageResponse>(_GetPersonByPageResponse_QNAME, GetPersonByPageResponse.class, null, value);
    }

}
