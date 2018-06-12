package com.loan_manage.service;

/**
 * Create by Jxl on 2017/9/25
 */
public interface ExportConcurrentService {
    /**
     * 获取导出锁
     * @return
     */
    boolean getExportToken();

    /**
     * 生产环境状态调整,导出令牌容器大小
     * @param volume
     */
    String setFillVolume(int volume);

    /**
     * 生产环境状态调整,导出令牌容器填充间隔
     * @param interval
     */
    String setFillInterval(long interval);
}
