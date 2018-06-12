package com.loan_utils.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class JSONConvert {
	
	/****
	 * 转成List对象
	 * 
	 * @param in
	 * @param clsT
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> getObjectsFromJson(String in, Class<T> clsT){
		JSONArray ja = JSON.parseArray(in);
		List<T> list = new ArrayList<T>(ja.size());
		for (int i = 0; i < ja.size(); ++i) {
			T t = convertEp(ja.getString(i), clsT);
			list.add(t);
		}
		return list;
	}
	
	/**
	 * json转成对象
	 * @param jsonStr
	 * @param c
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T convertEp(String jsonStr, Class<T> c) {
		return JSON.parseObject(jsonStr,c);
	}
}
