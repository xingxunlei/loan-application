package com.loan_utils.util;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class AddJsonarray {
    /**
     * 返回两个JsonArray的合并后的字符串
     * 
     * @param mData
     * @param array
     * @return
     */
    public static String joinJSONArray(JSONArray mData, JSONArray array) {
        StringBuffer buffer = new StringBuffer();
        try {
            int len = mData.size();
            for (int i = 0; i < len; i++) {
                JSONObject obj1 = (JSONObject) mData.get(i);
                if (i == len - 1)
                    buffer.append(obj1.toString());
                else
                    buffer.append(obj1.toString()).append(",");
            }
            len = array.size();
            if (len > 0)
                buffer.append(",");
            for (int i = 0; i < len; i++) {
                JSONObject obj1 = (JSONObject) array.get(i);
                if (i == len - 1)
                    buffer.append(obj1.toString());
                else
                    buffer.append(obj1.toString()).append(",");
            }
            buffer.insert(0, "[").append("]");
            return buffer.toString();
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 去重复index_id项合并value值
     *
     */
    public static String delRepeatIndexid(JSONArray array) {

        JSONArray arrayTemp = new JSONArray();

        int num = 0;
        for (int i = 0; i < array.size(); i++) {
            if (num == 0) {
                arrayTemp.add(array.get(i));
            } else {
                int numJ = 0;
                for (int j = 0; j < arrayTemp.size(); j++) {
                    JSONObject newJsonObjectI = (JSONObject) array.get(i);
                    JSONObject newJsonObjectJ = (JSONObject) arrayTemp.get(j);
                    String index_idI = newJsonObjectI.get("name").toString();
                    String valueI = newJsonObjectI.get("phone").toString();
                    // long timeI = newJsonObjectI.getLong("time");

                    String index_idJ = newJsonObjectJ.get("name").toString();
                    String valueJ = newJsonObjectJ.get("phone").toString();
                    // long timeJ = newJsonObjectJ.getLong("time");

                    if (index_idI.equals(index_idJ) && valueI.equals(valueJ)) {

                        arrayTemp.remove(j);
                        JSONObject newObject = new JSONObject();
                        newObject.put("name", index_idI);
                        newObject.put("phone", valueJ);
                        // newObject.put("time", timeI);
                        arrayTemp.add(newObject);

                        break;
                    }
                    numJ++;
                }
                if (numJ - 1 == arrayTemp.size() - 1) {
                    arrayTemp.add(array.get(i));
                }
            }

            num++;
        }
        return JSONArray.toJSONString(arrayTemp);
    }

    /**
     * 去重复index_id项合并value值
     *
     */
    public static JSONArray delRepeatIndexidJ(JSONArray array) {

        JSONArray arrayTemp = new JSONArray();

        int num = 0;
        for (int i = 0; i < array.size(); i++) {
            if (num == 0) {
                arrayTemp.add(array.get(i));
            } else {
                int numJ = 0;
                for (int j = 0; j < arrayTemp.size(); j++) {
                    JSONObject newJsonObjectI = (JSONObject) array.get(i);
                    JSONObject newJsonObjectJ = (JSONObject) arrayTemp.get(j);
                    String index_idI = newJsonObjectI.getString("name");
                    if (StringUtils.isEmpty(index_idI)){
                        index_idI = "联系人姓名为空";
                    }
                    if (StringUtils.isEmpty(newJsonObjectI.getString("phone"))){
                        continue;
                    }
                    String valueI = newJsonObjectI.get("phone").toString();
                    // long timeI = newJsonObjectI.getLong("time");

                    String index_idJ = newJsonObjectJ.getString("name");
                    if (StringUtils.isEmpty(index_idJ)){
                        index_idJ = "联系人姓名为空";
                    }
                    String valueJ = newJsonObjectJ.get("phone").toString();
                    // long timeJ = newJsonObjectJ.getLong("time");

                    if (index_idI.equals(index_idJ) && valueI.equals(valueJ)) {

                        arrayTemp.remove(j);
                        JSONObject newObject = new JSONObject();
                        newObject.put("name", index_idI);
                        newObject.put("phone", valueJ);
                        // newObject.put("time", timeI);
                        arrayTemp.add(newObject);

                        break;
                    }
                    numJ++;
                }
                if (numJ - 1 == arrayTemp.size() - 1) {
                    arrayTemp.add(array.get(i));
                }
            }

            num++;
        }
        return arrayTemp;
    }

    public static void main(String[] args) {
        JSONObject obj1 = new JSONObject();
        obj1.put("name", "tom");
        obj1.put("phone", "133");
        obj1.put("time", System.currentTimeMillis() - 1000);
        JSONObject obj2 = new JSONObject();
        obj2.put("name", "jack");
        obj2.put("phone", "134");
        obj2.put("time", System.currentTimeMillis() - 500);

        JSONArray array1 = new JSONArray();
        array1.add(obj1);
        array1.add(obj2);

        JSONObject obj3 = new JSONObject();
        obj3.put("name", "tom");
        obj3.put("phone", "135");
        obj3.put("time", System.currentTimeMillis());
        JSONObject obj4 = new JSONObject();
        obj4.put("name", "rose");
        obj4.put("phone", "136");
        obj4.put("time", System.currentTimeMillis() + 1000);

        JSONArray array2 = new JSONArray();
        array2.add(obj3);
        array2.add(obj4);

        String str = AddJsonarray.joinJSONArray(array1, array2);
        System.out.println(str);

        String newA = AddJsonarray.delRepeatIndexid(JSONArray.parseArray(str));

        System.out.println(newA);

    }
}