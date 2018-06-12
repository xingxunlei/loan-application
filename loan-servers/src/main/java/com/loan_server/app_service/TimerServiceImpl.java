package com.loan_server.app_service;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.loan_entity.loan_vo.RobotData;
import com.loan_utils.util.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.entity.vo.NormalExcelConstants;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.loan_api.app.TimerService;
import com.loan_api.app.UserService;
import com.loan_entity.app.BorrowList;
import com.loan_entity.loan.BillingNotice;
import com.loan_entity.utils.BorrPerInfo;
import com.loan_server.app_mapper.BorrowListMapper;

import net.sf.json.JSONObject;


public class TimerServiceImpl implements TimerService {
	private static final String HSSF  = ".xls";
	private static final String XSSF  = ".xlsx";

//	private static String host = "smtp.sina.com";
//	private static String name = "jinhuhanghlwzx@sina.com";
//	private static String pwd = "jhh123456";
//	private static String from = "jinhuhanghlwzx@sina.com";
//	private static String filePath = "/data/www/youmi/youmiapp/billingNotice/";

	private static String host = "192.168.1.18";
	private static String name = "wanzezhong";
	private static String pwd = "wan+123";
	private static String from = "wanzezhong@jinhuhang.com.cn";
	private static String filePath = "/data/www/youmi/youmiapp/billingNotice/";
	//	private static String filePath = "C:\\Users\\wanzezhong\\Desktop\\";
//	private static String robotPath = PropertiesReaderUtil.read("third", "bannerDir");


	private static String[] to = {"xuyaozong@jinhuhang.com.cn"};
	private static String[] copyto = {"chenzhen@jinhuhang.com.cn","2439357@qq.com","wanzezhong@jinhuhang.com.cn"};

	private static final Logger logger = LoggerFactory
			.getLogger(LoanServiceImpl.class);

	private String isTest = PropertiesReaderUtil.read("third", "isTest");


	@Autowired
	private BorrowListMapper borrMapper;

	@Autowired
	private UserService userService;

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
						String resultWuXun = WuXunSmsUtil.sendResult(remessage, bpi.getPhone(), 1);
						BillingNotice bn = new BillingNotice(bpi.getPhone(), resultWuXun);
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
			map.put(NormalExcelConstants.PARAMS, new ExportParams());
			Workbook workbook = ExcelUtils.getWorkbook(map);
			String fileName = null;
			try {
				fileName = map.get(NormalExcelConstants.FILE_NAME) + "";
				//保存Excel
				if (workbook instanceof HSSFWorkbook) {
					fileName += HSSF;
				} else {
					fileName += XSSF;
				}
				logger.info("生产excel");
				ExcelUtils.saveFile(workbook, filePath, fileName);
				//发送邮件
				logger.info("发送邮件");
				TimerServiceImpl.sendMail(from, to, copyto, filePath + fileName, fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void sendMail(String from, String[] to, String[] copyto, String filePath, String fileName){
		MailSender cn = new MailSender();
		// 设置发件人地址、收件人地址和邮件标题
 		cn.setAddress(from, to, copyto, "通知还款短信号码", "号码推送");
		// 设置要发送附件的位置和标题
		cn.setAffix(filePath, fileName);
		// 设置smtp服务器以及邮箱的帐号和密码
		cn.send(host, name, pwd);
	}


	public void sendRobotData(){

//		logger.info("进入定时发送机器人数据");
//
//		List<RobotData> robotData = borrMapper.getRobotData();
//
//		String robotPath = "D://ps//";
////		String robotPath = "/data/www/youmi/";
//
//		logger.info("共有数据"+robotData.size());
//
//		// 生成Excel
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put(NormalExcelConstants.FILE_NAME,
//				"JHH-M0-"+DateUtil.getDateString(new Date()));
//		map.put(NormalExcelConstants.CLASS, RobotData.class);
//		map.put(NormalExcelConstants.DATA_LIST, robotData);
//		ExportParams exportParams = new ExportParams();
//		exportParams.setType(ExcelType.XSSF);
//		map.put(NormalExcelConstants.PARAMS, exportParams);
//		Workbook workbook = ExcelUtils.getWorkbook(map);
//		String fileName = null;
//		fileName = map.get(NormalExcelConstants.FILE_NAME) + "";
//		// 保存Excel
//		fileName += ".xlsx";
//
//
//		try {
//			ExcelUtils.saveFile(workbook, robotPath, fileName);
//			//修改成调用接口发送
//			HttpUtils.postToFile("http://60.191.71.142:7080/worksheetAPI/requestFiles",
//					new File(robotPath + fileName));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		String from = "xuepengfei@jinhuhang.com.cn";
//		String[] to = {"xuepengfei@jinhuhang.com.cn","shuchen@jinhuhang.com.cn","chailongjie@biocloo.com.cn","m0_support@biocloo.com.cn","xuyaozong@jinhuhang.com.cn","yangyan@jinhuhang.com.cn"};
//		String[] copyto = {};
//		String content = "";
//
//		if ("on".equals(isTest)){
//			//测试环境只发给自己
//			to = new String[]{"xuepengfei@jinhuhang.com.cn"};
//			content = "测试环境";
//		}
//
//		MailSender sender = new MailSender();
//		// 设置发件人地址、收件人地址和邮件标题
//		sender.setAddress(from, to, copyto, "逾期人员数据", content);
//		// 设置要发送附件的位置和标题
//		sender.setAffix(robotPath + fileName, fileName);
//		// 设置smtp服务器以及邮箱的帐号和密码
//		sender.send("192.168.1.18", "xuepengfei", "Xue1988811");
	}

	public static void main(String[] args) {
//		Date date = new Date();
//		Calendar calendar = new GregorianCalendar();
//		calendar.setTime(date);
//		calendar.add(Calendar.DATE, 2);// 把日期往后增加一天.整数往后推,负数往前移动
//		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
//		System.out.println(DateUtil.getDateString(date));

		Map<String, Object> map = new HashMap<String, Object>();
		List<BillingNotice> list = new ArrayList<BillingNotice>();
		BillingNotice a1 = new BillingNotice("111","111");
		BillingNotice a2 = new BillingNotice("222","222");
		BillingNotice a3 = new BillingNotice("333","333");
		list.add(a1);
		list.add(a2);
		list.add(a3);
		map.put(NormalExcelConstants.FILE_NAME, "billingNotice" + DateUtil.getDateStringyyyymmdd(new Date()));
		map.put(NormalExcelConstants.CLASS, BillingNotice.class);
		map.put(NormalExcelConstants.DATA_LIST, list);
		map.put(NormalExcelConstants.PARAMS, new ExportParams());

		Workbook workbook = ExcelUtils.getWorkbook(map);
		String fileName = null;
		try {
			fileName = map.get(NormalExcelConstants.FILE_NAME) + "";
			//保存Excel
			if (workbook instanceof HSSFWorkbook) {
				fileName += HSSF;
			} else {
				fileName += XSSF;
			}
			ExcelUtils.saveFile(workbook, filePath, fileName);
			//发送邮件
			TimerServiceImpl.sendMail(from, to, copyto, filePath + fileName, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	@Override
	public void rejectAudit() {
		logger.info("rejectAudit.start");
		int count = borrMapper.rejectManualReview();
		logger.info("rejectAudit.end" + "count :" + count);
	}

}
