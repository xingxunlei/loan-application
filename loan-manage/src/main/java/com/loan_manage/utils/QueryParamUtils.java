package com.loan_manage.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 查询参数构建工具,仅支持[ "contains","=", "between"]
 */
public class QueryParamUtils {
    private final static String eq = "=";
    private final static String contains = "contains";
    private final static String startswith = ">=";
    private final static String start = ">";
    private final static String endswith = "<=";
    private final static String end = "<";
    
    public static Map<String,Object> buildRepaymentPlanQueryMap(Map<String,Object> resultMap,String queryParams){
        if(StringUtils.isEmpty(queryParams)){
            return resultMap;
        }
        JSONArray paramArray = JSON.parseArray(queryParams);
        if(queryParams.contains("and")){//多个参数
            for(int i=0;i<paramArray.size();i++){
                Object o  = paramArray.get(i);
                if(o instanceof JSONArray){
                    JSONArray oa = (JSONArray)o;
                    Object subO  = oa.get(0);
                    if(subO instanceof JSONArray){
                        String po = ((JSONArray) subO).toJSONString();
                        buildRepaymentPlanQueryMap(resultMap,po);
                    }else{
                        createParams(resultMap, oa);
                    }
                }
            }
        }else{
            createParams(resultMap, paramArray);
        }
        return resultMap;
    }

    private static void createParams(Map<String, Object> resultMap, JSONArray subArray) {
        String key = (String)subArray.get(0);
        String patten = (String)subArray.get(1);
        String value = (String)subArray.get(2);
        switch (patten){
            case eq :
                resultMap.put(key+"Eq",value);
                break;
            case contains:
                resultMap.put(key+"Contains",value);
                break;
            case startswith:
                resultMap.put(key+"Startswith",value);
                break;
            case start:
                resultMap.put(key+"Start",value);
                break;
            case endswith:
                resultMap.put(key+"Endswith",value);
                break;
            case end:
                resultMap.put(key+"End",value);
                break;
        }
    }

    public static Map getargs(Map<String, String[]> args) {
        Iterator<String> keys = args.keySet().iterator();
        Map arg = new HashMap();
        while (keys.hasNext()) {
            String key = keys.next();
            if ("filter".equals(key)) {
                String[] filter = args.get(key);
                if (filter.length > 0 && StringUtils.isNotEmpty(filter[0])) {
                    String st = filter[0];
                    JSONArray js = JSON.parseArray(st);
                    for (int i = 0; i < js.size(); i++) {
                        if (!"and".equals(js.get(i).toString())) {
                            if (js.get(i) instanceof JSONArray) {
                                JSONArray jss = JSON.parseArray(js.get(i)
                                        .toString());
                                if (jss.get(0) instanceof JSONArray) {
                                    JSONArray jsdate = (JSONArray) jss;
                                    for (int j = 0; j < jsdate.size(); j++) {
                                        setDate(arg, jsdate.get(j));
                                    }
                                } else {
                                    Object o = jss.get(2);
                                    if (o instanceof JSONObject) {
                                        arg.put(jss.get(0),
                                                ((JSONObject) o).get("value"));
                                    } else {
                                        setDate(arg, jss);
                                        arg.put(jss.get(0), jss.get(2));
                                    }
                                }
                            } else {
                                Object o = js.get(2);
                                if (o instanceof JSONObject) {
                                    arg.put(js.get(0),
                                            ((JSONObject) o).get("value"));
                                } else {
                                    arg.put(js.get(0), js.get(2));
                                }
                                break;
                            }
                        }
                    }
                }
            } else if ("sort".equals(key)) {
                String[] sort = args.get(key);
                if (sort.length > 0 && StringUtils.isNotEmpty(sort[0])) {
                    JSONObject jo = JSON.parseArray(sort[0]).getJSONObject(0);
                    arg.put("selector", jo.get("selector"));

                    if(jo.getBoolean("desc")){
                        arg.put("desc", "desc");
                    }else{
                        arg.put("desc", "asc");
                    }
                }
            }
        }
        return arg;
    }

    public static void setDate(Map<String, Object> arg, Object js) {
        if (js instanceof JSONArray) {
            JSONArray jss = (JSONArray) js;
            if (jss.get(1).toString().indexOf(">") > -1) {
                arg.put(jss.getString(0) + "_start", jss.getString(2));
            } else if (jss.get(1).toString().indexOf("<") > -1) {
                arg.put(jss.getString(0) + "_end", jss.getString(2));
            }
        }
    }
    
    /**
     * json值转换
     * @param key
     * @param data
     * @return
     */
	public static String getJsonData(String[] key, String[] data){
		Assertion.notEmpty(key, "key不能为空");
		Assertion.isTrue(key.length != data.length, "key和数据不匹配");
		
		Map<String, String> map = new HashMap<String, String>();
		for(int i = 0 ; i< key.length; i++){
			map.put(key[i], data[i]);
		}
		return JSON.toJSONString(map);
	}
	
	/**
     * json值转换
     * @param key
     * @param data
     * @return
     */
	public static String getJsonData(String key, String data){
		Assertion.notEmpty(key, "key不能为空");
		Map<String, String> map = new HashMap<String, String>();
		map.put(key, data);
		return JSON.toJSONString(map);
	}
    
}
