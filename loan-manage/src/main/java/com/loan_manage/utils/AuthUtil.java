package com.loan_manage.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loan_manage.pojo.auth.Category;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;

/**
 * Created by chenchao on 2017/10/13.
 */
public class AuthUtil {

    public static void main(String[] args) {
        System.out.println(readAuth(MD5Util.encryptStringBySign("1.1")).toString());
    }


    public static JSONArray readAuth(String auth) {
        try {
            InputStream in = UrlReader.class.getResourceAsStream("/auth.json");
            StringWriter writer = new StringWriter();
            IOUtils.copy(in, writer, "UTF-8");
            String jsonString = writer.toString();
            JSONArray array = JSONArray.parseArray(jsonString);
            checkRole(auth, array);
            return array;
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return null;
    }

    public static JSONArray readRole(String category, boolean allRoles) {
        try {
            InputStream in = UrlReader.class.getResourceAsStream("/role.json");
            StringWriter writer = new StringWriter();
            IOUtils.copy(in, writer, "UTF-8");
            String jsonString = writer.toString();
            JSONArray array = JSONArray.parseArray(jsonString);
            if (!allRoles) {
                checkCategory(category, array);
            }
            return array;
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return null;
    }

    private static void checkCategory(String category, JSONArray array) {
        try {
            InputStream in = UrlReader.class.getResourceAsStream("/privilege.json");
            StringWriter writer = new StringWriter();
            IOUtils.copy(in, writer, "UTF-8");
            String jsonString = writer.toString();
            JSONArray relations = JSONArray.parseArray(jsonString);

            for (int i = 0; i < relations.size(); i++) {
                JSONObject relation = relations.getJSONObject(i);
                String categoryType = relation.getString("category");
                if (MD5Util.encryptStringBySign(categoryType).equals(category)) {
                    JSONArray privileges = relation.getJSONArray("privileges");
                    Iterator iter = array.iterator();
                    while (iter.hasNext()) {
                        JSONObject object = (JSONObject) iter.next();
                        if (!privileges.contains(object.getString("type"))) {
                            iter.remove();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    private static void checkRole(String auth, JSONArray array) {
        Iterator iter = array.iterator();
        while (iter.hasNext()) {
            JSONObject object = (JSONObject) iter.next();
            Iterator keyIter = object.keySet().iterator();
            while (keyIter.hasNext()) {
                String key = (String) keyIter.next();
                if ("roles".equals(key)) {
                    JSONArray roles = object.getJSONArray("roles");
                    for (int i = 0; i < roles.size(); i++) {
                        roles.set(i, MD5Util.encryptStringBySign(roles.get(i).toString()));
                    }
                    if (!roles.contains(auth)) {
                        iter.remove();
                        break;
                    }
                    keyIter.remove();
                    object.remove(key);
                    continue;
                }
                if ("desc".equals(key)) {
                    keyIter.remove();
                    object.remove(key);
                }
                if ("subDirs".equals(key)) {
                    keyIter.remove();
                    object.remove(key);
                }
                if ("pageUrl".equals(key)) {
                    keyIter.remove();
                    object.remove(key);
                }
                if (object.get(key) instanceof JSONArray) {
                    JSONArray children = object.getJSONArray(key);
                    checkRole(auth, children);
                }
            }
        }
    }

    public static boolean isPageEnabled(String auth, String page) {
        JSONArray authInfo = readAuth(auth);
        for (int i = 0; i < authInfo.size(); i++) {
            JSONObject object = authInfo.getJSONObject(i);
            if (object.containsKey("pages")) {
                JSONArray pages = object.getJSONArray("pages");
                for (int j = 0; j < pages.size(); j++) {
                    JSONObject pageObject = pages.getJSONObject(j);
                    if (page.equals(pageObject.getString("id"))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
