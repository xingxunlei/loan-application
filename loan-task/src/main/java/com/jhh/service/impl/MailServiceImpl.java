package com.jhh.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jhh.service.MailService;
import com.jhh.util.MailSender;

@Component
public class MailServiceImpl implements MailService {
	
	@Value("${mail.wzz.host}")
	private  String host ;
	@Value("${mail.wzz.name}")
	private  String name ;
	@Value("${mail.wzz.pwd}")
	private  String pwd ;
	@Value("${filePath.mailAttachmentRoot}")
	private String robotPath;
	@Value("${mail.wzz.from}")
	private String from;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	
	@Override
	public void sendMail(String from, String[] to, String[] copyto, String filePath, String fileName, String mainTitle, String mailContent){
		MailSender cn = new MailSender();
		// 设置发件人地址、收件人地址和邮件标题
 		cn.setAddress(from, to, copyto, mainTitle, mailContent);
		// 设置要发送附件的位置和标题
		cn.setAffix(filePath, fileName);
		// 设置smtp服务器以及邮箱的帐号和密码
		cn.send(host, name, pwd);
	}

	@Override
	public File annexFile(String subPath, String fileName, Workbook wb) {
		File file = new File(robotPath+subPath+File.separator+fileName);
		//判断目标文件所在的目录是否存在  
        if(!file.getParentFile().exists()) {  
            //如果目标文件所在的目录不存在，则创建父目录  
            if(!file.getParentFile().mkdirs()) { 
            		logger.error("carete dir fail : " + file.getParentFile().getAbsolutePath());
            }
        }
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				logger.error("create file fail : " + file.getAbsolutePath(), e);
			}
		}
		try {
			wb.write(new FileOutputStream(file));
			return file;
		} catch (IOException e) {
			logger.error("write file fail : " + file.getAbsolutePath(), e);
		}
		return null;
	}

	@Override
	public void sendMail(String[] to, String[] copyto, String mainTitle, String mailContent, File attachment) {
		sendMail(from, to, copyto, attachment.getAbsolutePath(), attachment.getName(), mainTitle, mailContent);
	}


}
