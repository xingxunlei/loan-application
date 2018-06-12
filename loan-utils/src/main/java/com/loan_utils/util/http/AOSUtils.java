package com.loan_utils.util.http;


import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;




/**
 * <b>辅助工具类</b>
 * 
 * @author chailongjie
 * @since 1.0
 * @date 2009-1-22
 */
@SuppressWarnings("all")
public class AOSUtils {


	/**
	 * 判断对象是否Empty(null或元素为0)<br>
	 * 实用于对如下对象做判断:String Collection及其子类 Map及其子类
	 * 
	 * @param pObj
	 *            待检查对象
	 * @return boolean 返回的布尔值
	 */
	public static boolean isEmpty(Object pObj) {
		if (pObj == null)
			return true;
		if (pObj == "")
			return true;
		if (pObj instanceof String) {
			if (((String) pObj).length() == 0) {
				return true;
			}
		} else if (pObj instanceof Collection) {
			if (((Collection) pObj).size() == 0) {
				return true;
			}
		} else if (pObj instanceof Map) {
			if (((Map) pObj).size() == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断对象是否为NotEmpty(!null或元素>0)<br>
	 * 实用于对如下对象做判断:String Collection及其子类 Map及其子类
	 * 
	 * @param pObj
	 *            待检查对象
	 * @return boolean 返回的布尔值
	 */
	public static boolean isNotEmpty(Object pObj) {
		if (pObj == null)
			return false;
		if (pObj == "")
			return false;
		if (pObj instanceof String) {
			if (((String) pObj).length() == 0) {
				return false;
			}
		} else if (pObj instanceof Collection) {
			if (((Collection) pObj).size() == 0) {
				return false;
			}
		} else if (pObj instanceof Map) {
			if (((Map) pObj).size() == 0) {
				return false;
			}
		}
		return true;
	}



	/**
	 * 将字符串型日期转换为日期型
	 * 
	 * @param strDate
	 * @return
	 */
	public static Date stringToDate(String strDate) {
		Date tmpDate = (new SimpleDateFormat(AOSCons.DATATIME)).parse(strDate, new ParsePosition(0));
		if (tmpDate == null) {
			tmpDate = (new SimpleDateFormat(AOSCons.DATA)).parse(strDate, new ParsePosition(0));
		}
		return tmpDate;
	}
}