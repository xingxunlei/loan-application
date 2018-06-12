package com.loan_manage.utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * 
*描述：
*@author: Wanyan
*@date： 日期：2016年11月9日 时间：上午9:34:08
*@version 1.0
 */
public class UrlReader {
	public static String read(String data) {
		Properties props = new Properties();
		InputStream in;
		try {
			in = UrlReader.class.getResourceAsStream("/system.properties");
			props.load(in);
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
		if (props != null) {
			String udata = props.get(data).toString();
			return udata;
		} else {
			return "";
		}

	}
	
	
}
