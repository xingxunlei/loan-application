package com.loan_manage.utils.arbitration.timer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loan_manage.utils.arbitration.config.Config;
import com.loan_manage.utils.arbitration.dao.ReqBean;
import com.loan_manage.utils.arbitration.util.FileUitl;
import com.loan_manage.utils.arbitration.util.UtilPrint;
import com.loan_manage.utils.arbitration.zcgj.UploadZipFileService;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class RpcExecutor {
    private static int THREAD_COUNT_BASE = Runtime.getRuntime().availableProcessors();
    private static ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT_BASE * 3);
    private static CompletionService completionService = new ExecutorCompletionService(executorService);

    private static class MakeDocTask implements Callable<String> {
        private String filePath;
        private String fileName;
        private UploadZipFileService uploadZipFileService = new UploadZipFileService();

        private MakeDocTask(File file) {
            this.filePath = file.getAbsolutePath();
            this.fileName = file.getName();
        }

        public String call() {
            long start = System.currentTimeMillis();
            ReqBean reqDao = uploadZipFileService.checkFileUpload(this.filePath, this.fileName);
            UtilPrint.log_debug("单个案件推送耗时：", System.currentTimeMillis() - start + "ms");
            return JSONObject.toJSONString(reqDao);
        }
    }

    public void run() throws InterruptedException, ExecutionException {
        UtilPrint.log_debug("开始推送数据...查看cpu", THREAD_COUNT_BASE);
        long startTotal = System.currentTimeMillis();
        List<File> docList = new LinkedList();
        try {
            docList = FileUitl.getDirectory(Config.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        UtilPrint.log_debug("查到的数据列表", "【" + docList.size() + "】");
        int count = 0;
        int countAdd = 0;
        for (File o : docList) {
            UtilPrint.debugLog("fileName:" + o.getName() + "-[加入队列]-[" + countAdd + "]");
            completionService.submit(new MakeDocTask(o));
            countAdd++;
        }
        UtilPrint.debugLog("---请等待上传返回...");
        for (File o : docList) {
            Object obj = completionService.take().get();

            ReqBean reqbean = JSONObject.toJavaObject(JSON.parseObject(obj.toString()), ReqBean.class);
            if (reqbean.getCode().equals("1")) {
                UtilPrint.debugLog("fileName:" + o.getName() + "-[处理结果ok]-[" + count + "]");
                FileUitl.MoveFile(o, Config.getPathTmp());
            } else {
                UtilPrint.debugLog("fileName:" + o.getName() + "-[处理结果error]-[" + count + "]");
            }
            count++;
        }
        getCurrentThreads(startTotal, docList);
    }

    private static void getCurrentThreads(long startTotal, List<File> docList) {
        long end = (System.currentTimeMillis() - startTotal) / 1000L;
        long freeMemory = Runtime.getRuntime().freeMemory() / 1024L / 1024L;
        long totalMemory = Runtime.getRuntime().totalMemory() / 1024L / 1024L;
        int cpu = Runtime.getRuntime().availableProcessors();
        UtilPrint.log_debug("本次推送数据列表...", docList.size());
        UtilPrint.log_debug("查看cpu.......", cpu);
        UtilPrint.log_debug("本次推送总耗时:%毫秒 ", System.currentTimeMillis() - startTotal);
        if (docList.size() != 0) {
            UtilPrint.log_debug("平均耗时:%s ", end / docList.size());
        }
        UtilPrint.log_debug("可用内存:%sm ", freeMemory);
        UtilPrint.log_debug("可用总内存:%sm ", totalMemory);
        UtilPrint.log_debug("**************************************", "********************************");
    }
}
