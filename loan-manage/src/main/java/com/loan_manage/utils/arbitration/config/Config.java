package com.loan_manage.utils.arbitration.config;

import com.loan_manage.utils.arbitration.util.FileUitl;
import com.loan_manage.utils.arbitration.util.UtilPrint;
import com.loan_manage.utils.arbitration.util.Util_properties;
import org.apache.log4j.PropertyConfigurator;

public class Config {
    private static String url;
    private static String token;
    private static String path;
    private static String pathTmp;
    private static int countList = 10;

    public static String getUrl() {
        return url;
    }

    public static String getToken() {
        return token;
    }

    public static String getPath() {
        return path;
    }

    public static String getPathTmp() {
        return pathTmp;
    }

    public static int getCountList() {
        return countList;
    }

    public void init() throws Exception {
        try {
            init_log4j();
            Util_properties pro = new Util_properties();

            url = pro.getConfig("url");
            token = pro.getConfig("tocken");
            path = pro.getConfig("dirpath");
            pathTmp = pro.getConfig("dirpath") + "/UploadSuccessTmp/";
            countList = pro.getConfigKeyInt("countList");
            if (url == null) {
                throw new Exception("上传的URL不能为空");
            }
            if (token == null) {
                throw new Exception("上传的token不能为空");

            }
            if (path == null) {
                throw new Exception("上传的path不能为空");
            }
            log();
            FileUitl.createDir(pathTmp);
        } catch (Exception e) {
            throw new Exception("启动的三个参数有问题:" + e.getMessage());
        }
    }

    public static void init_log4j() {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "\\arbitration-log4j.properties";
        PropertyConfigurator.configure(path);
    }

    public static void log() {
        UtilPrint.log_ini("---------------上传工具参数初始化----------------------");
        UtilPrint.log_debug("url", url);
        UtilPrint.log_debug("token", token);
        UtilPrint.log_debug("path", path);
        UtilPrint.log_debug("countList", Integer.valueOf(countList));
    }

}
