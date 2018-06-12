package com.loan_manage.web_service;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.loan_manage.utils.UrlReader;



public class WebServiceClient {
	private static String urlinfo = "";
	private static final WebServiceClient webServiceClient= new WebServiceClient();
	private WebServiceClient(){}
	public static WebServiceClient getInstance(){
		urlinfo = UrlReader.read("oa.Url");
		//urlinfo="http://172.16.11.37:8080/";
		return webServiceClient;
	}
	
	public static void main(String[] args) {
		
		urlinfo = UrlReader.read("oa.Url");
		//urlinfo="http://172.16.11.37:8080/";
		System.out.println(urlinfo+"/OADataCenter/person/service");
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

		factory.setServiceClass(PersonServiceDao.class);

		factory.setAddress(urlinfo+"/OADataCenter/person/service");
		
		
		//factory.setAddress("http://172.16.11.130:8080/OADataCenter/person/service");
		
		PersonServiceDao service = (PersonServiceDao) factory.create();
		String result = service.checklogin_id("oa",
 "1q2w3e", "SH2092");
		
		// String result1 = service.login("oa", "1q2w3e", "SH2092", "123456");
		String result1 = service.selectAllUser("oa", "1q2w3e");
		System.out.println(result1 + ">>>>>>>>>>>>>>>>>>>>>>>");

	}
	
	
	public PersonServiceDao getPersonServiceDao() {

		// 调用WebService

		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

		factory.setServiceClass(PersonServiceDao.class);

		factory.setAddress(urlinfo+"/OADataCenter/person/service");
		System.out.println(urlinfo+"/OADataCenter/person/service");
		
		//factory.setAddress("http://172.16.11.130:8080/OADataCenter/person/service");
		
		PersonServiceDao service = (PersonServiceDao) factory.create();

		return service;

	}
	public OaViewPersonWebService getOaViewPersonWebService(){
		String oaUrlOper = UrlReader.read("oa.url.oper");
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(OaViewPersonWebService.class);
		factory.setAddress("http://192.168.1.44:8080/OAOperationCenter/oaview");

		OaViewPersonWebService service = (OaViewPersonWebService)factory.create();
		return service;
	}
}
