package com.loan_manage.utils;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.StringWriter;

/**
 * @author wuhanhong
 * @date 2017 - 11 - 14
 */
public class AutoTranUtil {
    public static JSONArray readAutoTran() {
        try {
            InputStream in = UrlReader.class.getResourceAsStream("/autoTran.json");
            StringWriter writer = new StringWriter();
            IOUtils.copy(in, writer, "UTF-8");
            String jsonString = writer.toString();
            JSONArray array = JSONArray.parseArray(jsonString);
            return array;
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return null;
    }
}
