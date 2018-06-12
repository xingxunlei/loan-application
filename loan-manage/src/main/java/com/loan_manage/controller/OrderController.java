package com.loan_manage.controller;

import com.github.pagehelper.PageInfo;
import com.loan_manage.entity.OrderVo;
import com.loan_manage.entity.Result;
import com.loan_manage.service.OrderService;
import com.loan_manage.utils.QueryParamUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 还款流水
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping(value="/queryOrders",produces = "application/json")
    public @ResponseBody Result queryOrders(HttpServletRequest request){
        Result result = new Result();
        int offset =  Integer.valueOf(request.getParameter("skip") == null ? "0" : request.getParameter("skip"));
        int size = Integer.valueOf(request.getParameter("take") == null ? Integer.MAX_VALUE + "" : request.getParameter("take"));
        String userNo = StringUtils.isEmpty(request.getParameter("userNo")) ? "SYSTEM" : request.getParameter("userNo");

        try{
            Map<String,Object> queryMap = QueryParamUtils.getargs(request.getParameterMap());

            PageInfo<OrderVo> info = orderService.selectOrderVoInfo(queryMap,offset,size,userNo);

            result.setCode(Result.SUCCESS);
            result.setMessage("加载成功");
            result.setObject(info);
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("加载失败");
        }

        return result;
    }
}
