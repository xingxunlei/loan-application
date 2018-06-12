package com.loan_manage.utils.arbitration.main;

import com.loan_manage.utils.arbitration.config.Config;
import com.loan_manage.utils.arbitration.timer.TimedTask;
import com.loan_manage.utils.arbitration.util.UtilPrint;

public class MainRun {

    public static void startTask() {
        try {
            UtilPrint.log_ini("上传工具初始化.....:");
            Config config = new Config();
            config.init();
            UtilPrint.log_ini("上传工具初始化.....");
            new TimedTask().runTask();
        } catch (Exception e) {
            e.printStackTrace();
            UtilPrint.log_ini("上传工具初始化.....error=" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void start() {
        try {
            UtilPrint.log_ini("上传工具初始化.....:");
            Config config = new Config();
            config.init();
            UtilPrint.log_ini("上传工具初始化.....");
            new TimedTask().runOne();
        } catch (Exception e) {
            e.printStackTrace();
            UtilPrint.log_ini("上传工具初始化.....error=" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
