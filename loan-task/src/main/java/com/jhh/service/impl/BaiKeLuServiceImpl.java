package com.jhh.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jhh.dao.BaiKeLuReportMapper;
import com.jhh.model.BaiKeLuReport;
import com.jhh.service.BaiKeLuService;
import com.jhh.util.DateUtil;
import com.jhh.util.MailSender;

@Component
public class BaiKeLuServiceImpl implements BaiKeLuService {
	
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
	@Value("${filePath.baikelu}")
	private String robotPath;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String[] to = {"wufang@jinhuhang.com.cn","lujiawen@jinhuhang.com.cn","wangge@jinhuhang.com.cn", "chenzhen@jinhuhang.com.cn"};
//	private static final String[] to = {"wufang@jinhuhang.com.cn","wangge@jinhuhang.com.cn", "chenzhen@jinhuhang.com.cn"};
	
	@Autowired
	private BaiKeLuReportMapper baiKeLuReportMapper;
	
	private void sendMail(String from, String[] to, String[] copyto, String filePath, String fileName, String mainTitle, String mailContent){
		MailSender cn = new MailSender();
		// 设置发件人地址、收件人地址和邮件标题
 		cn.setAddress(from, to, copyto, mainTitle, mailContent);
		// 设置要发送附件的位置和标题
		cn.setAffix(filePath, fileName);
		// 设置smtp服务器以及邮箱的帐号和密码
		cn.send(host, name, pwd);
	}

	@Override
	public void sendReport() {
		try {
			File file = toExcel(getData());
			this.sendMail(from, to, new String[0], file.getAbsolutePath(), file.getName(), "百可录统计报表", "");
		} catch (IOException e) {
			logger.error("生成报表文件失败", e);
		}
	}
	
	private File toExcel(SortedMap<String, BaiKeLuReport> data) throws IOException {
		String filePath = robotPath + "BaiKeLu-" + DateUtil.getDateString(Calendar.getInstance().getTime()) + ".xlsx";
		logger.info(filePath);
		File file = new File(filePath);
		if(!file.exists() && !file.createNewFile()) {
			throw new IOException("创建文件失败 : " + file.getAbsolutePath());
		}
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet();
		Row firstRow = sheet.createRow(0);
		firstRow.createCell(1).setCellValue("审核");
		firstRow.createCell(2).setCellValue("提示");
		for(String date : data.keySet()) {
			Row row = sheet.createRow(sheet.getLastRowNum()+1);
			row.createCell(0).setCellValue(date);
			row.createCell(1).setCellValue(data.get(date).getShenHe());
			row.createCell(2).setCellValue(data.get(date).getTiShi());
		}
		wb.write(new FileOutputStream(file));
		return file;
	}
	
	@SuppressWarnings("unused")
	@Deprecated
	private String toHtml(SortedMap<Date, BaiKeLuReport> data) {
		StringBuilder builder = new StringBuilder();
		builder.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" + 
				"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
				"<head>\n" + 
				"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" + 
				"<title>百可录统计报表</title>\n" + 
				"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" + 
				"</head>\n");
		builder.append("<table align=\"center\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"border-collapse: collapse;\">\n" + 
				"<tr>\n" + 
				"<td>日期</td>\n" + 
				"<td>审核</td>\n" + 
				"<td>提示</td>\n" + 
				"</tr>\n");
		for(Date d: data.keySet()) {
			BaiKeLuReport br = data.get(d);
			builder.append("<tr>\n" + 
				"<td>"+ br.getDay() +"</td>\n" + 
				"<td>"+ br.getShenHe() +"</td>\n" + 
				"<td>"+ br.getTiShi() +"</td>\n" + 
				"</tr>\n");
		}
		builder.append("</table>\n"
				+ "</html>");
		return builder.toString();
	}
	
	private SortedMap<String, BaiKeLuReport> getData() {
		int days = 7;
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -days);
		List<BaiKeLuReport> shenHeList = baiKeLuReportMapper.getShenHe(0, DateUtil.getDateString(c.getTime()));
		List<BaiKeLuReport> tiShiList = new ArrayList<>();
		if ("on".equals(isTest)){
			//测试环境
			robotPath = "/Users/frank/tmp/";
		}
		for(int i = 0;i < days;i ++) {
			Calendar today = Calendar.getInstance();
			today.add(Calendar.DATE, -i);
			String day = DateUtil.getDateString(today.getTime());
			String readFilePath = robotPath + "JHH-M0-"+day + ".xlsx";
			BaiKeLuReport br = new BaiKeLuReport();
			br.setDay(new Date(today.getTimeInMillis()));
			try {
				Workbook wb = new XSSFWorkbook(new FileInputStream(new File(readFilePath)));
				br.setCount(wb.getSheetAt(0).getLastRowNum());
			} catch (IOException e) {
				logger.warn("Read Excel File '" + readFilePath + "' error, return 0", e);
				br.setCount(0);
			}
			tiShiList.add(br);
		}
		SortedMap<String, BaiKeLuReport> map = new TreeMap<>();
		for(BaiKeLuReport br : tiShiList) {
			br.setType(BaiKeLuReport.TI_SHI);
			map.put(DateUtil.getDateString(br.getDay()), br);
		}
		for(BaiKeLuReport br : shenHeList) {
			br.setType(BaiKeLuReport.SHEN_HE);
			String key = DateUtil.getDateString(br.getDay());
			if(map.containsKey(key)) {
				map.get(key).join(br);
			}else {
				map.put(key, br);
			}
		}
		return map;
	}
	
}
