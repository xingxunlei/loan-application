package com.jhh.task;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jhh.service.ExcelService;
import com.jhh.service.MailService;
import com.jhh.util.DateUtil;

@Component
public class MailTask {
	
	@Autowired
	private ExcelService excelService;
	
	@Autowired
	private MailService mailService;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
//	@Scheduled(cron = "0 0 09 * * ?")
	public void mailLiuHongWei() {
		String[] to  = {"liuhongwei@jinhuhang.com.cn"};
//		String[] to  = {"wangge@jinhuhang.com.cn"};
		String[] cc  = {"wufang@jinhuhang.com.cn","wangge@jinhuhang.com.cn","chenzhen@jinhuhang.com.cn"};
		String sql = "select count(1) as '数量' from ym_borrow_list where borr_status = 'BS002' and date(update_date) = date_sub(curdate(),interval 2 day)";
		
		Workbook wb = excelService.sqlToExcel(sql);
		String today = DateUtil.getDateString(new Date());
		File attachment = mailService.annexFile("liuhongwei",today+".xlsx", wb);
		if(attachment != null) {
			mailService.sendMail(to, cc, "每日邮件报表", "", attachment);
		}
	}
	
//	@Scheduled(cron = "0 0 09 * * ?")
	public void mailGuozhen() {
		String[] to  = {"guozhen@jinhuhang.com.cn"};
//		String[] to  = {"wangge@jinhuhang.com.cn"};
		String[] cc  = {"wufang@jinhuhang.com.cn","wangge@jinhuhang.com.cn","chenzhen@jinhuhang.com.cn"};
		Calendar c = Calendar.getInstance();
		String today = DateUtil.getDateString(c.getTime());
		c.add(Calendar.DATE, -1);
		String yesterday = DateUtil.getDateString(c.getTime());
		String sql = "select  t1.`name` AS `客户姓名`,\n" + 
				"  t1.card_num AS `客户身份证号`,\n" + 
				"  t1.phone AS `手机号码`,\n" + 
				"  t1.borr_amount AS `放款金额`,\n" + 
				"  t1.pay_date AS `放款日期`,\n" + 
				"  t1.product_name AS `产品类型`,\n" + 
				"  t1.meaning AS `渠道` from\n" + 
				"(SELECT\n" + 
				"  DISTINCT ym_card.`name`,\n" + 
				"  ym_card.card_num ,\n" + 
				"  ym_person.phone,\n" + 
				"  y.borr_amount,\n" + 
				"  y.pay_date,\n" + 
				"  ym_product.product_name,\n" + 
				"  ym_code_value.meaning,\n" + 
				"  y.per_id as per_id\n" + 
				"FROM\n" + 
				"  ym_borrow_list AS y\n" + 
				"INNER JOIN ym_card ON\n" + 
				"  y.per_id = ym_card.per_id  AND  y.pay_date >= '"+yesterday+"' AND y.pay_date < '"+today+"' \n" + 
				"INNER JOIN ym_product ON\n" + 
				"  y.prod_id = ym_product.id\n" + 
				"INNER JOIN ym_person ON\n" + 
				"  y.per_id = ym_person.id\n" + 
				"INNER JOIN ym_product_term ON\n" + 
				"  ym_product.id = ym_product_term.product_id\n" + 
				"INNER JOIN ym_order ON\n" + 
				"  y.id = ym_order.conctact_id\n" + 
				"  AND ym_order.type = '1'\n" + 
				"  AND ym_order.rl_state = 's' LEFT join ym_code_value on\n" + 
				"  ym_code_value.code_code = ym_person.source\n" + 
				"  AND code_type = 'register_source' \n" + 
				" GROUP by ym_card.`name` ) t1, ym_borrow_list t2\n" + 
				"where t1.per_id = t2.per_id group by t1.per_id having count(t1.per_id) = 1";
		if(logger.isDebugEnabled()) {
			logger.debug(sql);
		}
		Workbook wb = excelService.sqlToExcel(sql);
		File attachment = mailService.annexFile("guozhen",today+".xlsx", wb);
		if(attachment != null) {
			mailService.sendMail(to, cc, "每日邮件报表", "", attachment);
		}
	}

}
