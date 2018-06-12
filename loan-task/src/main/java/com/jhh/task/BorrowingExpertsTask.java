package com.jhh.task;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.jhh.service.TimerService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.jhh.model.BorrowingExpertsOrder;
import com.jhh.model.BorrowingExpertsPerson;
import com.jhh.service.BorrowingExpertsService;
import com.jhh.util.Detect;
import com.jhh.util.HttpUtils;
import com.jhh.util.MD5Util;
/**
 * 借款专家接口
 * @author wanzezhong
 * 2017年5月3日 18:18:56
 */
@Component
public class BorrowingExpertsTask {

	private static final Logger log = Logger.getLogger(BorrowingExpertsTask.class);
	@Autowired
	private BorrowingExpertsService borrowingExpertsService;
	@Autowired
	private TimerService timerService;
	/*****分配给应用的唯一标识****/
	@Value("${borrowingExperts.appKey}")
	private String appKey;
	/*****私钥****/
	@Value("${borrowingExperts.appSecret}")
	private String appSecret ;
	/*****API协议版本，默认1.0****/
	@Value("${borrowingExperts.version}")
	private String version;
	@Value("${borrowingExperts.userSource}")
	private String userSource;
	@Value("${borrowingExperts.url}")
	private String url;
	@Value("${borrowingExperts.approve.url}")
	private String approveChildUrl;
	@Value("${borrowingExperts.register.url}")
	private String registerChildUrl;
	/**
	 * 借款专家，用户推送
	 */
//	@Scheduled(cron = "0 */1 * * * ?")
	@Scheduled(cron = "0 0 6 * * ?")
	public void pushUser() {
		log.debug("pushUsertask.start========");
		List<BorrowingExpertsPerson> persons = borrowingExpertsService.pushUser(userSource);
		if(Detect.notEmpty(persons)){
			String timestamp = getSecondTimestampTwo(new Date());
			String param = JSONObject.toJSONString(persons);
			// 获得签名
			String sign = getSign(param, timestamp);
			// 获得最终参数
			String data = getOrderByParam(param, sign, timestamp);
			try {
				String sendUrl = url + registerChildUrl;
				log.info("pushUser post url:" + sendUrl + " AND data:" + data);
				HttpUtils.sendPost(sendUrl, data, "application/json");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		log.debug("pushUsertask.end========");
	}
	
	
	/**
	 * 借款专家，订单推送
	 */
	@Scheduled(cron = "0 0 6 * * ?")
//	@Scheduled(cron = "0 */1 * * * ?")
	public void pushOrder() {
		log.debug("pushOrdertask.start========");
		List<BorrowingExpertsOrder> orders = borrowingExpertsService.pushOrder(userSource);
		if(Detect.notEmpty(orders)){
			String timestamp = getSecondTimestampTwo(new Date());
			String param = JSONObject.toJSONString(orders);
			// 获得签名
			String sign = getSign(param, timestamp);
			// 获得最终参数
			String data = getOrderByParam(param, sign, timestamp);
			try {
				String sendUrl = url + approveChildUrl;
				log.info("pushOrder post url:" + sendUrl + "AND data:" + data);
				HttpUtils.sendPost(sendUrl, data, "application/json");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		log.debug("pushOrdertask.end========");
	}

	/**
	 *  短信提醒用户继续认证
     */
	//@Scheduled(fixedRate = 10*60*1000)
	@Scheduled(cron = "0 0 9 * * ? ")
	public void personSmsRemind(){

		timerService.personSmsRemind();
	}

	/**
	 * 获取签名规则
	 * @return
	 */
	private String getSign(String data, String timestamp){
		String dataMd5 = MD5Util.MD5 (data) ;
		
		String md5 = MD5Util.MD5(appKey + version + timestamp + appSecret + dataMd5);
		return md5;
	}
	/**
	 * 请求参数排序
	 * @param data
	 * @param sign
	 * @return
	 */
	private String getOrderByParam(String data, String sign, String timestamp){
		Map<String, Object> map = new TreeMap<String, Object>();
		map.put("sign", sign);
		map.put("data", JSONObject.parse(data));
		map.put("app_key", appKey);
		map.put("version", version);
		map.put("timestamp", timestamp);
		return JSONObject.toJSONString(map);
	}
	
	/** 
	 * 获取精确到秒的时间戳 
	 * @param date 
	 * @return 
	 */  
	public static String getSecondTimestampTwo(Date date){  
	    if (null == date) {  
	        return "";  
	    }  
	    String timestamp = String.valueOf(date.getTime()/1000);  
	    return timestamp;  
	}  
	
	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getUserSource() {
		return userSource;
	}

	public void setUserSource(String userSource) {
		this.userSource = userSource;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getApproveChildUrl() {
		return approveChildUrl;
	}

	public void setApproveChildUrl(String approveChildUrl) {
		this.approveChildUrl = approveChildUrl;
	}

	public String getRegisterChildUrl() {
		return registerChildUrl;
	}

	public void setRegisterChildUrl(String registerChildUrl) {
		this.registerChildUrl = registerChildUrl;
	}
}
