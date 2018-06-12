package com.loan_manage.utils.arbitration.util;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Date;

public class UtilPrint {
    static Logger log = Logger.getLogger(UtilPrint.class);

    UtilPrint() {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "\\arbitration-log4j.properties";
        PropertyConfigurator.configure(path);
    }

    public static void sysLog(Object obj) {
        log4j("系统级别输出:[" + UtilDate.getDate(new Date()) + "]:" + obj);
    }

    public static void log_ini(Object obj) {
        log4j("仲裁系统初始化:[" + UtilDate.getDate(new Date()) + "]:" + obj);
    }

    public static void log_debug(Object obj, Object objs) {
        log4j("仲裁系统-Debug输出:[" + UtilDate.getDate(new Date()) + "]:" + obj + "=" + objs);
    }

    public static void log_nc(Object obj, Object objs) {
        log4j("仲裁系统-内存大小输出:[" + UtilDate.getDate(new Date()) + "]:" + obj + " =" + objs);
    }

    public static void log(Object obj, Object cla) {
        log4j("仲裁系统_AOP日志输出:[" + UtilDate.getDate(new Date()) + "]:" + obj);
    }

    public static void debugLog(Object obj) {
        log4j("仲裁系统_AOP日志输出:[" + UtilDate.getDate(new Date()) + "]:" + obj);
    }

    public static void timerLog(Object obj) {
        log4j("仲裁系统定时扫描任务日志输出:[" + UtilDate.getDate(new Date()) + "]:" + obj);
    }

    public static void timerSendLog(Object obj) {
        log4j("仲裁系统定时发送任务日志输出:[" + UtilDate.getDate(new Date()) + "]:" + obj);
    }

    public static void log4j(Object obj) {
        log.info(obj);
    }
}
