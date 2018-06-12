package com.jhh.service.impl;


import com.jhh.dao.BorrowListMapper;
import com.jhh.dao.PersonMapper;
import com.jhh.model.*;
import com.jhh.service.TimerService;
import com.jhh.service.UserService;
import com.jhh.util.*;
import com.loan_utils.util.EmaySmsUtil;
import com.loan_utils.util.RedisConst;
import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.entity.vo.NormalExcelConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service("timerService")
public class TimerServiceImpl implements TimerService {
	private static final String HSSF  = ".xls";
	private static final String XSSF  = ".xlsx";

//	private static String host = "smtp.sina.com";
//	private static String name = "jinhuhanghlwzx@sina.com";
//	private static String pwd = "jhh123456";
//	private static String from = "jinhuhanghlwzx@sina.com";
//	private static String filePath = "/data/www/youmi/youmiapp/billingNotice/";
	private static final Logger logger = LoggerFactory.getLogger(TimerServiceImpl.class);

	@Value("${system.isTest}")
	private String isTest;
	@Value("${mail.wzz.host}")
	private  String host ;
	@Value("${mail.wzz.name}")
	private  String name ;
	@Value("${mail.wzz.pwd}")
	private  String pwd ;
	@Value("${mail.wzz.from}")
	private  String from ;
	@Value("${mail.wzz.filePath}")
	private  String filePath ;
	@Value("${filePath.moneyManagement}")
	private String moneyManagementFilePath;
	@Value("${sms.message}")
	private String smsMessage;

	private static String[] to = {"xuyaozong@jinhuhang.com.cn","yangyan@jinhuhang.com.cn"};
	private static String[] copyto = {"chenzhen@jinhuhang.com.cn","liuhongming@jinhuhang.com.cn","shuchen@jinhuhang.com.cn","2439357@qq.com","wanzezhong@jinhuhang.com.cn"};

	@Autowired
	private BorrowListMapper borrMapper;

	@Autowired
	private UserService userService;

	@Autowired
	private PersonMapper personMapper;

	@Autowired
	private JedisCluster jedisCluster;

	@Override
	public void smsAlert() {
		logger.info("进入还款提醒的定时任务---------------------------------------");
		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, 2);// 把日期往后增加一天.整数往后推,负数往前移动
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		String dd = DateUtil.getDateString(date);
		String da1 = DateUtil.getDateString(new Date());
		List<BorrowList> blist = borrMapper.getMingtianhuankuanId(dd,da1);
		if (blist.size() > 0) {
			List<BillingNotice> list = new ArrayList<BillingNotice>();
			for (int i = 0; i < blist.size(); i++) {
				try{
					// 合同和人的信息
					BorrPerInfo bpi = new BorrPerInfo();
					bpi = borrMapper.selectByBorrId(blist.get(i).getId());

					// 发送站内信
					logger.info("payDate=="+blist.get(i).getPayDate());
					logger.info("getMaximum_amount=="+bpi.getMaximum_amount());
					logger.info("getTerm_value=="+bpi.getTerm_value());
					logger.info("getRepayDate=="+blist.get(i).getRepayDate());
					DecimalFormat df = new DecimalFormat("######0.00");
					String result = userService.setMessage(
							String.valueOf(blist.get(i).getPerId()),
							"4",
							bpi.getName()
									+ ","
									+ DateUtil.getDateString(blist.get(i)
											.getPayDate())
									+ ","
									+ df.format(bpi.getMaximum_amount())
									+ ","
									+ bpi.getTerm_value()
									+ "天,"
									+ DateUtil.getDateString(DateUtil.getDate(blist
											.get(i).getRepayDate())));
					JSONObject obje = JSONObject.fromObject(result);
					if ("200".equals(obje.get("code").toString())) {
						logger.info("消息发送成功！");
						// 发送短信
						// boolean re =
						// smsService.send("【悠米闪借】"+neirong,bpi.getPhone());

						// 老悠米的短信接口，要加标题模版,还款提醒
						String remessage = SmsUtil.sendSmsHuanKuanTixing(SmsUtil.MGHKTX_CODE,
								bpi.getName(), df.format(Double.parseDouble(bpi
										.getSurplus_quota())), blist.get(i)
										.getRepayDate(), bpi.getPhone());

	                    // 2017.4.19更新 短信send第三个参数 0-悠兔 ，1-悠米，2-吾老板
//	                    if (EmaySmsUtil.send(remessage, bpi.getPhone(), 1)) {
//						if (WuXunSmsUtil.send(remessage, bpi.getPhone(), 1)) { //2017.6.1 更换吾讯运营商
//							logger.info("短信发送成功！");
//						} else {
//							logger.info("短信发送失败！");
//						}

						BillingNotice bn = null;
						if(isTest.equals("off")){
							String resultWuXun = WuXunSmsUtil.sendResult(remessage, bpi.getPhone(), 1);
							bn = new BillingNotice(bpi.getPhone(), resultWuXun);
						}else{
							bn = new BillingNotice(bpi.getPhone(), "000000");
						}
						list.add(bn);
					} else {
						logger.info(obje.get("info").toString());
					}
				}catch(Exception e){
					logger.error(e.getMessage());
				}

			}
			
			//生成Excel
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(NormalExcelConstants.FILE_NAME, DateUtil.getDateStringyyyymmdd(new Date()));
			map.put(NormalExcelConstants.CLASS, BillingNotice.class);
			map.put(NormalExcelConstants.DATA_LIST, list);
//			map.put(NormalExcelConstants.PARAMS, new ExportParams());
			ExportParams exportParams = new ExportParams();
			exportParams.setType(ExcelType.XSSF);
			map.put(NormalExcelConstants.PARAMS, exportParams);

			Workbook workbook = ExcelUtils.getWorkbook(map);
			String fileName = null;
			fileName = map.get(NormalExcelConstants.FILE_NAME) + "";
			//保存Excel
			if (workbook instanceof HSSFWorkbook) {
                fileName += HSSF;
            } else {
                fileName += XSSF;
            }
			logger.info("生产excel");

			if("on".equals(isTest)){
                to = new String[]{from};
                copyto =  new String[]{from};
            }
			ExcelUtils.saveFile(workbook, filePath, fileName);
			//发送邮件
			logger.info("发送邮件");
			this.sendMail(from, to, copyto, filePath + fileName, fileName,"YM_通知还款短信号码" ,"YM_号码推送");
		}
	}
	
	private void sendMail(String from, String[] to, String[] copyto, String filePath, String fileName, String mainTitle, String mailContent){
		MailSender cn = new MailSender();
		// 设置发件人地址、收件人地址和邮件标题
 		cn.setAddress(from, to, copyto, mainTitle, mailContent);
		// 设置要发送附件的位置和标题
		cn.setAffix(filePath, fileName);
		// 设置smtp服务器以及邮箱的帐号和密码
		cn.send(host, name, pwd);
	}

	private void sendMailArray(String from, String[] to, String[] copyto, String[] filePathArray, String[] fileNameArray, String mainTitle, String mailContent){
		MailSender cn = new MailSender();
		// 设置发件人地址、收件人地址和邮件标题
		cn.setAddress(from, to, copyto, mainTitle, mailContent);
		// 设置要发送附件的位置和标题
		cn.setAffixArray(filePathArray, fileNameArray);
		// 设置smtp服务器以及邮箱的帐号和密码
		cn.send(host, name, pwd);
	}


	@Override
	public void sendRobotData(){

		logger.info("进入定时发送机器人数据");
		try {
			List<RobotData> robotData = borrMapper.getRobotData();

			String robotPath = "/data/www/youmi/";
			if ("on".equals(isTest)){
				//测试环境
				robotPath = "C://Users/wanzezhong//Desktop//";
			}
			logger.info("共有数据" + robotData.size());

			// 生成Excel
			String fileName = createExcel(robotData ,robotPath, "JHH-P0-"+DateUtil.getDateString(new Date()),RobotData.class);


			//修改成调用接口发送
			String url = "http://223.111.15.182:8080/worksheetAPI/requestFiles";
			Map<String, String> param = new HashMap<>();
			param.put("file", "file");
			param.put("client_name", "金互行（悠米闪借）");
			param.put("access_token", "youmishanjie20170816");
			File file = new File(robotPath + fileName);
			logger.info("url:" + url + ";param:" + param + ";file:" + file.getPath());
			HttpUtils.postToFile(url, param, file);

//			HttpUtils.postToFile("http://60.191.71.142:7080/worksheetAPI/requestFiles", new File());
		}catch (Exception e) {
			String[] to = {"chailongjie@biocloo.com.cn","m0_support@biocloo.com.cn","xuyaozong@jinhuhang.com.cn","yangyan@jinhuhang.com.cn"};
			String[] copyto = {"luoqian@jinhuhang.com.cn","dinghaifeng@jinhuhang.com.cn","liuhongming@jinhuhang.com.cn","shuchen@jinhuhang.com.cn","chenzhen@jinhuhang.com.cn","wanzezhong@jinhuhang.com.cn"};
			if ("on".equals(isTest)){
				//测试环境只发给自己
				to = new String[]{"wanzezhong@jinhuhang.com.cn"};
				copyto =  new String[]{"wanzezhong@jinhuhang.com.cn"};
			}
			this.sendMail(from, to, copyto, null , null, "YM_百可录逾期提醒异常", e.getMessage());
			e.printStackTrace();
		}

//		String from = "xuepengfei@jinhuhang.com.cn";
//		String[] to = {"wanzezhong@jinhuhang.com.cn","xuepengfei@jinhuhang.com.cn","shuchen@jinhuhang.com.cn","chailongjie@biocloo.com.cn","m0_support@biocloo.com.cn","xuyaozong@jinhuhang.com.cn","yangyan@jinhuhang.com.cn"};
//		String[] copyto = {};
//		String content = "";
//
//		if ("on".equals(isTest)){
//			//测试环境只发给自己
//			to = new String[]{"wanzezhong@jinhuhang.com.cn"};
//			content = "测试环境";
//		}
//		MailSender sender = new MailSender();
//		// 设置发件人地址、收件人地址和邮件标题
//		sender.setAddress(from, to, copyto, "逾期人员数据", content);
//		// 设置要发送附件的位置和标题
//		sender.setAffix(robotPath + fileName, fileName);
//		// 设置smtp服务器以及邮箱的帐号和密码
//		sender.send("192.168.1.18", "xuepengfei", "Xue1988811");
	}

	@Override
	public void sendMoneyManagement() {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);// 把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        String beginDate = DateUtil.getDateString(date);
        String endDate = DateUtil.getDateString(new Date());
        Map map = new HashMap();
        map.put("beginDate", beginDate);
        map.put("endDate", endDate);

		List result = borrMapper.sendMoneyManagement(map);

		List resultSecond = borrMapper.sendMoneyManagementSecond(map);
		//生成Excel
		String fileName = createExcel(result,moneyManagementFilePath,"YM_ZJD" + DateUtil.getDateStringyyyymmdd(new Date()),MoneyManagement.class);

		String fileNameSecond = createExcel(resultSecond,moneyManagementFilePath,"YM_ZJD_SECOND" + DateUtil.getDateStringyyyymmdd(new Date()),MoneyManagementSecond.class);

//		//发送邮件
		logger.info("发送邮件");
		String[] to = {"chenzhen@jinhuhang.com.cn","qixintong@jinhuhang.com.cn"};

		String[] fileNames = {fileName , fileNameSecond};
		String[] filePaths = {moneyManagementFilePath + fileName , moneyManagementFilePath + fileNameSecond};
		if("on".equals(isTest)){
			to = new String[]{from};
			copyto =  new String[]{"chenzhen@jinhuhang.com.cn","wangge@jinhuhang.com.cn"};
		}
		String[] copyto =  new String[]{"qixintong@jinhuhang.com.cn","wanzezhong@jinhuhang.com.cn"};
		this.sendMailArray(from, to, copyto, filePaths, fileNames, "YM_放款数据", "YM_放款数据");
	}

	private String createExcel(List result,String path, String fileName, Class excilClass){
		Map<String, Object> excilMap = new HashMap<String, Object>();
		excilMap.put(NormalExcelConstants.FILE_NAME, fileName);
//		if(Detect.notEmpty(result)){
//			excilMap.put(NormalExcelConstants.CLASS, result.get(0).getClass());
//		}else {
//			excilMap.put(NormalExcelConstants.CLASS, Object.class);
//		}
		excilMap.put(NormalExcelConstants.CLASS, excilClass);

		excilMap.put(NormalExcelConstants.DATA_LIST, result);
		ExportParams exportParams = new ExportParams();
		exportParams.setType(ExcelType.XSSF);
		excilMap.put(NormalExcelConstants.PARAMS, exportParams);
		Workbook workbook = ExcelUtils.getWorkbook(excilMap);

		//保存Excel
		if (workbook instanceof HSSFWorkbook) {
			fileName += HSSF;
		} else {
			fileName += XSSF;
		}
		logger.info("生成excel");
		ExcelUtils.saveFile(workbook, path, fileName);
		return fileName;
	}

	@Override
	public void personSmsRemind() {
		//获取两天之内注册并且合同为申请中的用户信息
		List<Person> pid = personMapper.getPersonAndBorrByDate();
		pid.stream().filter(p -> {
			//从redis中获取用户节点信息
			Map<String, String> nodeMap = jedisCluster.hgetAll(RedisConst.NODE_KEY + p.getId());
			//节点不存在直接返回
			if (nodeMap == null || nodeMap.size() == 0) {
				logger.info("-----------用户节点不存在 phone = "+p.getPhone());
				return true;
			} else if (nodeMap.size() == 8) {  //对节点全时判断状态
				boolean temp = false;
				for (Map.Entry<String, String> v : nodeMap.entrySet()) {
					BpmNode bmp = (BpmNode) JSONObject.toBean(JSONObject.fromObject(v.getValue()), BpmNode.class);
					String status = bmp.getNodeStatus();
					long date = DateUtil.getTimeDifference(new Date(), bmp.getUpdateDate());
					if (CodeReturn.STATUS_BPM_FAIL.equals(status) || CodeReturn.STATUS_BPM_UP.equals(status)) {
						logger.info("-----------用户出现最终态 phone = "+p.getPhone());
						temp = false;
						break;
					} else if (CodeReturn.STATUS_BPM_N.equals(status) && date > 24 * 60) {
						temp = true;
					}
				}
				return temp;

			} else {
				boolean temp = true;
				for (Map.Entry<String, String> v : nodeMap.entrySet()) {
					BpmNode bmp = (BpmNode) JSONObject.toBean(JSONObject.fromObject(v.getValue()), BpmNode.class);
					String status = bmp.getNodeStatus();
					if (CodeReturn.STATUS_BPM_FAIL.equals(status) || CodeReturn.STATUS_BPM_UP.equals(status)) {
						logger.info("-----------用户节点不足八个并有最终态 不发送 phone = "+p.getPhone());
						temp = false;
						break;
					}

				}
				return temp;
			}
		}).forEach(id ->{
			logger.info("需要发送短信的用户手机号为:"+id.getPhone());
			EmaySmsUtil.send(smsMessage,id.getPhone(),1);
				}
		);

	}

	@Override
	public void sendHaierOrder() {
		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);// 把日期往后增加一天.整数往后推,负数往前移动
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		String beginDate = DateUtil.getDateString(date);
		String endDate = DateUtil.getDateString(new Date());
		Map map = new HashMap();
		map.put("beginDate", beginDate);
		map.put("endDate", endDate);

		List result = borrMapper.sendMoneyToHaier(map);

		//生成Excel
		String fileName = createExcel(result,moneyManagementFilePath,"YM_HAIER" + DateUtil.getDateString(new Date()), MoneyHaier.class);

//		//发送邮件
		logger.info("发送邮件");


		String[] to = {"qixintong@jinhuhang.com.cn","liqiujie@jinhuhang.com.cn"};
		String[] copyto = {"shuchen@jinhuhang.com.cn","wanzezhong@jinhuhang.com.cn"};
		if("on".equals(isTest)){
			to = new String[]{"wanzezhong@jinhuhang.com.cn"};
			copyto =  new String[]{""};
		}

		this.sendMail(from, to, copyto, moneyManagementFilePath + fileName, fileName,
				"【悠米闪借】" + DateUtil.getDateString(new Date()) + "海尔还款流水", "");
	}

	public String getIsTest() {
		return isTest;
	}

	public void setIsTest(String isTest) {
		this.isTest = isTest;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public static String[] getTo() {
		return to;
	}

	public static void setTo(String[] to) {
		TimerServiceImpl.to = to;
	}

	public static String[] getCopyto() {
		return copyto;
	}

	public static void setCopyto(String[] copyto) {
		TimerServiceImpl.copyto = copyto;
	}

	public String getMoneyManagementFilePath() {
		return moneyManagementFilePath;
	}

	public void setMoneyManagementFilePath(String moneyManagementFilePath) {
		this.moneyManagementFilePath = moneyManagementFilePath;
	}

	public void setSmsMessage(String smsMessage) {
		this.smsMessage = smsMessage;
	}

	public String getSmsMessage() {
		return smsMessage;
	}

	public static void main(String[] arge){
	}
}
