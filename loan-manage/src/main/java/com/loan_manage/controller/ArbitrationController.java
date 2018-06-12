package com.loan_manage.controller;

import com.loan_manage.entity.Result;
import com.loan_manage.service.ArbitrationService;
import com.loan_manage.utils.arbitration.main.MainRun;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 仲裁项目接口
 * @author xingmin 
 */
@Slf4j
@Controller
@RequestMapping(value = "/arbitration")
public class ArbitrationController {

    @Autowired
    private ArbitrationService arbitrationService;

    /**
     * 生成仲裁需要的文件
     * @param perId
     * @param borrId
     * @note 若参数不传，则自动取orderMap文件中的数据生成文件
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/submit", method = RequestMethod.POST, produces = "application/json")
    public Result submitArbitration(HttpServletRequest request) {
        Result result = new Result();
        try {
            List<Map<String, String>> data = arbitrationService.transferArbitration(request.getParameter("perId"), request.getParameter("borrId"));

            result.setCode(Result.SUCCESS);
            result.setMessage("任务执行成功");
            result.setObject(data);
            new Thread(new FileTaskRunnable(result)).start();
            return result;
        }catch (Exception e){
            log.error("任务执行失败.......");
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("任务执行失败");
            return result;
        }
    }

    /**
     * 开启定时器传输文件（每5秒扫描一次）
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/startTask", method = RequestMethod.POST, produces = "application/json")
    public Result startArbitrationTask() {
        Result result = new Result();
        try {
            MainRun.startTask();
            result.setCode(Result.SUCCESS);
            result.setMessage("上传工具开启成功....");
            return result;
        }catch (Exception e){
            log.error("上传工具开启失败.......");
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("上传工具开启失败");
            return result;
        }
    }

    /**
     * 开启传输任务，只运行1次
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/start", method = RequestMethod.POST, produces = "application/json")
    public Result startArbitration() {
        Result result = new Result();
        try {
            MainRun.start();
            result.setCode(Result.SUCCESS);
            result.setMessage("上传工具开启成功....");
            return result;
        }catch (Exception e){
            log.error("上传工具开启失败.......");
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("上传工具开启失败");
            return result;
        }
    }

}

class FileTaskRunnable implements Runnable {

    private Result result;

    public FileTaskRunnable(Result result) {
        this.result = result;
    }

    @Override
    public void run() {
        List<Map<String, String>> list = (List<Map<String, String>>) result.getObject();
        try {
            FileWriter writer = new FileWriter("D:/result.txt",true);
            list.forEach(map -> {
                try {
                    if (!"SUCCESS".equals(map.get("code").toUpperCase())) {
                        return;
                    }
                    writer.write(String.format("%s, %s, %s, %s \n", map.get("borrId"), map.get("perId"), map.get("code"), map.get("message")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
