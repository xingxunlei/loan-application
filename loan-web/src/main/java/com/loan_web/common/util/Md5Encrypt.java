package com.loan_web.common.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Encrypt {

	public static String md5(String text) throws UnsupportedEncodingException {
		MessageDigest msgDigest = null;
		try {
			msgDigest = MessageDigest.getInstance("MD5");
			msgDigest.update(text.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("System doesn't support MD5 algorithm.");
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedEncodingException("encode error");
		}
		
		byte[] bytes = msgDigest.digest();
		byte tb;
		char low;
		char high;
		char tmpChar;
		String md5Str = new String();

		for (int i = 0; i < bytes.length; i++) {
			tb = bytes[i];
			tmpChar = (char) ((tb >>> 4) & 0x000f);
			if (tmpChar >= 10) {
				high = (char) (('a' + tmpChar) - 10);
			} else {
				high = (char) ('0' + tmpChar);
			}

			md5Str += high;
			tmpChar = (char) (tb & 0x000f);

			if (tmpChar >= 10) {
				low = (char) (('a' + tmpChar) - 10);
			} else {
				low = (char) ('0' + tmpChar);
			}

			md5Str += low;
		}
		return md5Str;
	}
	
	public static void main(String[] args){
		try {
			String digest=md5("merchantId=2120140827163221001&userType=P&orderId=20140828296132&orderTime=20140828190405&name=李杨&idNum=420621199103126313&mobilePhoneNum=15221203805&merchantKey=tyjr20140827!@#");
			System.out.println(digest);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
