package com.loan_manage.utils.arbitration.zcgj;

import com.alibaba.fastjson.JSONObject;
import com.loan_manage.utils.arbitration.util.FileUitl;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UploadZipFile {
//    public static void main(String[] args) {
//        String url = args[0];
//        String token = args[1];
//        String fileDir = args[2];
//
//        System.out.println("系统启动成功...");
//        System.out.println("压缩包所在目录:" + fileDir);
//        System.out.println("接口调用凭证:" + token);
//        System.out.println("正在上传案件压缩包文件...");
//
//        UploadZipFileTask(fileDir, url, token);
//    }

//    public static void UploadZipFileTask(String fileDir, final String url, final String token) {
//        final long timeInterval = 60000L;
//        Runnable runnable = new Runnable() {
//            public void run() {
//                for (; ; ) {
//                    System.out.println("开始查看文件目录...");
//                    try {
//                        int count = UploadZipFile.getDirectory(UploadZipFile.this, url, token);
//                        System.out.println("本次上传" + count + "个压缩包...");
//                    } catch (Exception e1) {
//                        e1.printStackTrace();
//                    }
//                    try {
//                        Thread.sleep(timeInterval);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        Thread thread = new Thread(runnable);
//        thread.start();
//    }

//    private static int getDirectory(String fileDir, String url, String token) {
//        File file = new File(fileDir);
//        File[] flist = file.listFiles();
//        int count = 0;
//        if ((flist != null) && (flist.length != 0)) {
//            File[] arrayOfFile1;
//            int j = (arrayOfFile1 = flist).length;
//            for (int i = 0; i < j; i++) {
//                File f = arrayOfFile1[i];
//                if (f.isDirectory()) {
//                    System.out.println("Dir==>" + f.getAbsolutePath());
//                } else {
//                    if (f.getName().contains("s.")) {
//                        String fileName = f.getName();
//                        String filePath = f.getAbsolutePath();
//                        System.out.println("fileName" + fileName + "filePath" +
//                                filePath);
//                        try {
//                            JSONObject json = JSONObject.parseObject(checkFileUpload(url, filePath, token, fileName));
//                            JSONObject.toJSONString(json, true);
//                            String code = json.get("code").toString();
//                            if (code.equals("1")) {
//                                count++;
//
//                                System.out.println(f.getName());
//                                String oldName = f.getName();
//                                System.out.println();
//                                String fileParent = f.getParent();
//                                SimpleDateFormat sdf = new SimpleDateFormat(
//                                        "yyyyMMddhhmmss");
//                                String newName = oldName.replace("s.",
//                                        "s_delete_" + sdf.format(new Date()) +
//                                                ".");
//                                System.out.println("fileParent" + fileParent);
//                                File newFile = new File(fileParent + "/" +
//                                        newName);
//                                f.renameTo(newFile);
//                            }
//                        } catch (Exception localException1) {
//                        }
//                    }
//                    if (f.getName().contains("m.")) {
//                        String fileName = f.getName();
//                        String filePath = f.getAbsolutePath();
//                        System.out.println("fileName" + fileName + "filePath" +
//                                filePath);
//                        try {
//                            JSONObject json = JSONObject.parseObject(checkFileUpload(url, filePath, token, fileName));
//                            JSONObject.toJSONString(json, true);
//                            String code = json.get("code").toString();
//                            if (code.equals("1")) {
//                                count++;
//                                System.out.println(f.getName());
//                                String oldName = f.getName();
//                                System.out.println();
//                                String fileParent = f.getParent();
//                                SimpleDateFormat sdf = new SimpleDateFormat(
//                                        "yyyyMMddhhmmss");
//                                String newName = oldName.replace("m.",
//                                        "m_delete_" + sdf.format(new Date()) +
//                                                ".");
//                                System.out.println("fileParent" + fileParent);
//                                File newFile = new File(fileParent + "/" +
//                                        newName);
//                                f.renameTo(newFile);
//                            }
//                        } catch (Exception e) {
//                            System.out.println("请求异常!");
//                        }
//                    }
//                }
//            }
//        }
//        return count;
//    }

//    public static String checkFileUpload(String url, String filePath, String token, String fileName) throws Exception {
//        Map<String, String> textMap = new HashMap();
//        String base64Code = FileUitl.encodeBase64File(filePath);
//        textMap.put("fileBody", base64Code);
//        textMap.put("fileName", fileName);
//        textMap.put("token", token);
//        Map<String, String> fileMap = new HashMap();
//        String ret = formUpload(url, textMap);
//        System.out.println(ret);
//        return ret;
//    }

//    public static String formUpload(String urlStr, Map<String, String> textMap) throws Exception {
//        String res = "";
//        HttpURLConnection conn = null;
//        String BOUNDARY = "---------------------------123821742118716";
//
//        URL url = new URL(urlStr);
//        conn = (HttpURLConnection) url.openConnection();
//        conn.setConnectTimeout(5000);
//        conn.setReadTimeout(30000);
//        conn.setDoOutput(true);
//        conn.setDoInput(true);
//        conn.setUseCaches(false);
//        conn.setRequestMethod("POST");
//        conn.setRequestProperty("Connection", "Keep-Alive");
//        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
//        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
//
//        OutputStream out = new DataOutputStream(conn.getOutputStream());
//        if (textMap != null) {
//            StringBuffer strBuf = new StringBuffer();
//            Iterator iter = textMap.entrySet().iterator();
//            while (iter.hasNext()) {
//                Map.Entry entry = (Map.Entry) iter.next();
//                String inputName = (String) entry.getKey();
//                String inputValue = (String) entry.getValue();
//                if (inputValue != null) {
//                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
//                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
//                    strBuf.append(inputValue);
//                }
//            }
//            out.write(strBuf.toString().getBytes());
//        }
//        byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
//        out.write(endData);
//        out.flush();
//        out.close();
//
//        StringBuffer strBuf = new StringBuffer();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        String line = null;
//        while ((line = reader.readLine()) != null) {
//            strBuf.append(line).append("\n");
//        }
//        res = strBuf.toString();
//        reader.close();
//        reader = null;
//
//        return res;
//    }
}
