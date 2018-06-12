package com.loan_manage.controller;

import com.loan_manage.entity.Result;
import com.loan_manage.service.AgreementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 电子协议
 * @author xingmin
 */
@Slf4j
@Controller
@RequestMapping(value = "/agreement")
public class AgreementController {

    @Autowired
    private AgreementService agreementService;

    /**
     * 生成需要的文件
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
            List<Map<String, String>> data = agreementService.generateFiles(request.getParameter("perId"), request.getParameter("borrId"));

            result.setCode(Result.SUCCESS);
            result.setMessage("任务执行成功");
            result.setObject(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            log.error("任务执行失败.......");
            result.setCode(Result.FAIL);
            result.setMessage("任务执行失败");
            return result;
        }
    }

}
