package com.loan_manage.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.loan_entity.app.Product;
import com.loan_entity.manager_vo.DebitInfoVo;
import com.loan_manage.entity.Result;
import com.loan_manage.service.ProductService;
import com.loan_manage.service.RepaymentPlanService;
import com.loan_manage.utils.QueryParamUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("repaymentPlan")
public class RepaymentPlanController {

    private Logger logger =  Logger.getLogger(RepaymentPlanController.class);

    @Autowired
    private RepaymentPlanService repaymentPlanService;
    @Autowired
    private ProductService productService;

    /**
     * 获取所有产品信息
     * @return
     */
    @RequestMapping("/queryAllProduct") @ResponseBody
    public Result queryAllProduct(){
        Result result = new Result();
        try {
            List<Product> products = productService.selectAllProduct();
            JSONArray array = new JSONArray();
            for(Product product : products){
                JSONObject object = new JSONObject();
                object.put("value",product.getId());
                object.put("format",product.getProductName().replace("元",""));
                array.add(object);
            }
            result.setCode(Result.SUCCESS);
            result.setMessage("加载成功");
            result.setObject(array);
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("加载失败");
        }
        return result;
    }

    /**
     * 分页查询还款计划
     * @param request
     * @return
     */
    @RequestMapping("/queryRepaymentPlan") @ResponseBody
    public Result queryRepaymentPlan(HttpServletRequest request){
        int offset =  Integer.valueOf(request.getParameter("skip") == null ? "0" : request.getParameter("skip"));
        int size = Integer.valueOf(request.getParameter("take") == null ? Integer.MAX_VALUE + "" : request.getParameter("take"));
        String userNo = StringUtils.isEmpty(request.getParameter("userNo")) ? "SYSTEM" : request.getParameter("userNo");

        Result result = new Result();
        try{
            Map<String,Object> queryMap = QueryParamUtils.getargs(request.getParameterMap());
            PageInfo info = repaymentPlanService.selectRepaymentPlan(queryMap,offset,size,userNo);
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

    @RequestMapping("/queryDebitInfo") @ResponseBody
    public Result queryDebitInfo(HttpServletRequest request){
        logger.info("============>进入扣款信息查询");
        int offset =  Integer.valueOf(request.getParameter("skip") == null ? "0" : request.getParameter("skip"));
        int size = Integer.valueOf(request.getParameter("take") == null ? Integer.MAX_VALUE + "" : request.getParameter("take"));
        Result result = new Result();
        try{
            Map<String,Object> queryMap = QueryParamUtils.getargs(request.getParameterMap());
            PageHelper.offsetPage(offset,size);
            PageInfo<DebitInfoVo> info = repaymentPlanService.selectDebitInfo(queryMap);
            logger.info("============>扣款信息查询成功");
            result.setCode(Result.SUCCESS);
            result.setMessage("加载成功");
            result.setObject(info);
        }catch (Exception e){
            logger.info("============>扣款信息查询失败："+e.getMessage());
            result.setCode(Result.FAIL);
            result.setMessage("加载失败");
        }
        return result;
    }
}
