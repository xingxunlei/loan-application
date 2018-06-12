package com.loan_utils.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Map 转换工具类
 */
public class MapUtils {

    private static final Logger logger = LoggerFactory
            .getLogger(MapUtils.class);
    /**
     * 将一个类查询方式加入map（属性值为int型时，0时不加入，
     * 属性值为String型或Long时为null和“”不加入）
     *
     */
    public static Map<String, String> setConditionMap(Object obj) {
        Map<String,String> map = new LinkedHashMap<String, String>();
        if(obj==null){
            return null;
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        for(Field field : fields){
            String fieldName =  field.getName();
            if(getValueByFieldName(fieldName,obj)!=null &&! "".equals(getValueByFieldName(fieldName,obj)))
                map.put(fieldName, String.valueOf(getValueByFieldName(fieldName,obj)));
        }
        logger.info(obj.getClass()+"*************MapUtils转换成Map的结果为********\n"+map);
        return map;

    }

    /**
     * 根据属性名获取该类此属性的值
     * @param fieldName
     * @param object
     * @return
     */
    private static Object getValueByFieldName(String fieldName,Object object){
        String firstLetter=fieldName.substring(0,1).toUpperCase();
        String getter = "get"+firstLetter+fieldName.substring(1);
        try {
            Method method = object.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(object, new Object[] {});
            return value;
        } catch (Exception e) {
            return null;
        }

    }

}
