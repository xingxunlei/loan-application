package com.jhh.service;

import java.io.File;

import org.apache.poi.ss.usermodel.Workbook;

public interface MailService {
	
	File annexFile(String subPath, String fileName, Workbook wb);
	
	void sendMail(String from, String[] to, String[] copyto, String filePath, String fileName, String mainTitle, String mailContent);

	void sendMail(String[] to, String[] copyto, String mainTitle, String mailContent, File attachment);


}
