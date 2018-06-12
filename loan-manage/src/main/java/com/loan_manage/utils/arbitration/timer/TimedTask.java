package com.loan_manage.utils.arbitration.timer;

import com.loan_manage.utils.arbitration.util.UtilPrint;

import java.util.concurrent.ExecutionException;

public class TimedTask {
    public static int startInt = 5;
    public static int sleepInt = 5000;

    public void runOne() {
        TaskLog();
        Runnable runnable = () -> {
            UtilPrint.timerLog("线程激活-开始工作...");
            try {
                new RpcExecutor().run();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            } catch (ExecutionException e1) {
                e1.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void runTask() {
        TimedTaskLog();
        Runnable runnable = () -> {
            UtilPrint.timerLog("线程激活-开始工作...");
            for (; ; ) {
                try {
                    new RpcExecutor().run();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } catch (ExecutionException e1) {
                    e1.printStackTrace();
                }
                try {
                    Thread.sleep(TimedTask.sleepInt);
                } catch (InterruptedException localInterruptedException1) {
                }
                UtilPrint.timerLog("线程激活-重新开始工作...");
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public static void TaskLog() {
        UtilPrint.timerLog("开启执行传输任务.....");
    }

    public static void TimedTaskLog () {
        UtilPrint.timerLog("开启定时器.....");
        UtilPrint.timerLog("开启定时器.....n秒后开启:" + TimedTask.startInt + "s");
    }

}
