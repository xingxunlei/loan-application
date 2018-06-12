package com.loan_manage.controller;

import com.loan_manage.entity.Result;
import com.loan_manage.service.RiskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


/**
 * 审核管理
 * Create by Jxl on 2017/9/11
 */
@Controller
@RequestMapping("/risk")
public class RiskController {

    @Autowired
    private RiskService RiskService;

    /**
     * 取消借款
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/cancel")
    public Result cancel(HttpServletRequest request){
        Result result = new Result();
        String borrowId = request.getParameter("borrowId");
        String userId = request.getParameter("userId");
        try {
            result = RiskService.cancelLoan(borrowId,userId);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage(e.getMessage());
            return result;
        }
    }



}
