package com.loan_manage.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.dubbo.remoting.TimeoutException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loan_entity.app.BankVo;
import com.loan_entity.app.Product;
import com.loan_entity.common.Constants;
import com.loan_entity.loan.CollectorsLevel;
import com.loan_entity.manager.BankList;
import com.loan_entity.manager.CollectorsRemark;
import com.loan_entity.manager.Download;
import com.loan_manage.entity.AskCollection;
import com.loan_manage.entity.LoansRemarkOutVo;
import com.loan_manage.entity.LoansRemarkVo;
import com.loan_manage.exception.RedisException;
import com.loan_manage.service.*;
import com.loan_utils.util.DateUtil;
import com.loan_utils.util.ExcelUtils;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.vo.NormalExcelConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.loan_entity.loan.ReceiptUsers;
import com.loan_entity.manager.CollectorsListVo;
import com.loan_entity.manager_vo.LoanManagementVo;
import com.loan_manage.entity.Result;
import com.loan_manage.utils.QueryParamUtils;

//贷后管理
@Controller
@RequestMapping("/loanManagement")
public class LoanManagementController {

    @Autowired
    private LoanManagementService loanManagementService;
    @Autowired
    private CollectorsLevelService collectorsLevelService;
    @Autowired
    private ExportConcurrentService exportConcurrentService;
    @Autowired
    private AutoTranService autoTranService;
    /**
     * 查询还款计划
     * @param request
     * @return
     */
    @RequestMapping(value = "/queryLoans",produces = "application/json") @ResponseBody
    public Result queryLoans(HttpServletRequest request){
        Result result = new Result();
        int offset =  Integer.valueOf(request.getParameter("skip") == null ? "0" : request.getParameter("skip"));
        int size = Integer.valueOf(request.getParameter("take") == null ? Integer.MAX_VALUE + "" : request.getParameter("take"));
        String userNo = StringUtils.isEmpty(request.getParameter("userNo")) ? "SYSTEM" : request.getParameter("userNo");
        //PageHelper.offsetPage(offset,size);
        try{
            Map<String,Object> queryMap = QueryParamUtils.getargs(request.getParameterMap());
            PageInfo<LoanManagementVo> info = loanManagementService.selectLoanManagementInfo(queryMap,offset,size,userNo);
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

    @RequestMapping(value = "/queryBatchReduce",produces = "application/json") @ResponseBody
    public Result queryBatchReduce(HttpServletRequest request){
        Result result = new Result();
        int offset =  Integer.valueOf(request.getParameter("skip") == null ? "0" : request.getParameter("skip"));
        int size = Integer.valueOf(request.getParameter("take") == null ? Integer.MAX_VALUE + "" : request.getParameter("take"));
        String userNo = StringUtils.isEmpty(request.getParameter("userNo")) ? "SYSTEM" : request.getParameter("userNo");
        //PageHelper.offsetPage(offset,size);
        try{
            Map<String,Object> queryMap = QueryParamUtils.getargs(request.getParameterMap());
            PageInfo<LoanManagementVo> info = loanManagementService.selectBatchReduceInfo(queryMap,offset,size,userNo);
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

    /**
     * 查询催收人员
     * @param request
     * @return
     */
    @RequestMapping(value="/queryReceiptUsers",produces = "application/json") @ResponseBody
    public Result queryReceiptUsers(HttpServletRequest request){
        Result result = new Result();
        int offset =  Integer.valueOf(request.getParameter("skip") == null ? "0" : request.getParameter("skip"));
        int size = Integer.valueOf(request.getParameter("take") == null ? Integer.MAX_VALUE + "" : request.getParameter("take"));
        try{
            Map<String,Object> queryMap = QueryParamUtils.getargs(request.getParameterMap());


            PageInfo<ReceiptUsers> info = loanManagementService.selectReceiptUsers(queryMap , offset , size);
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

    /**
     * 查询催收人员
     * @param request
     * @return
     */
    @RequestMapping(value="/queryReceiptUsersByUser",produces = "application/json") @ResponseBody
    public Result queryReceiptUsersByUser(HttpServletRequest request,String userNo,Integer type){
        Result result = new Result();
        int offset =  Integer.valueOf(request.getParameter("skip") == null ? "0" : request.getParameter("skip"));
        int size = Integer.valueOf(request.getParameter("take") == null ? Integer.MAX_VALUE + "" : request.getParameter("take"));
        Map<String,Object> queryParams = QueryParamUtils.getargs(request.getParameterMap());
        try{
            PageInfo<CollectorsLevel> info = loanManagementService.selectReceiptUsers(queryParams,userNo,type,offset,size);
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

    /**
     * 申请减免/线下还款
     * @param contractId 合同号
     * @param reduce 减免金额/还款金额
     * @return
     */
    @RequestMapping(value="/reduceLoan",produces = "application/json") @ResponseBody
    public Result reduceLoan(String contractId,String reduce,String remark,String type,String userName){
        Result result = new Result();
        if(StringUtils.isEmpty(contractId)){
            result.setCode(Result.FAIL);
            result.setMessage("合同号为空");
        }else if(StringUtils.isEmpty(reduce)){
            result.setCode(Result.FAIL);
            result.setMessage("金额为空");
        }else{
            try{
                result = loanManagementService.reduceLoan(contractId,reduce,remark,type,userName);
                if(result.getCode() == Result.SUCCESS){
                    result.setCode(Result.SUCCESS);
                    result.setMessage("操作成功");
                }else{
                    result.setCode(Result.FAIL);
                    result.setMessage(result.getMessage());
                }
            }catch (Exception e){
                e.printStackTrace();
                result.setCode(Result.FAIL);
                result.setMessage("操作失败");
            }
        }
        return result;
    }

    /**
     * 转件
     * @return
     */
    @RequestMapping(value="/transferLoan",produces = "application/json") @ResponseBody
    public Result transferLoan(String contractIds,String userId,String opUserId){
        Result result = new Result();
        try{
            Map<String,Object> map = loanManagementService.transferLoan(contractIds,userId,opUserId);
            int state = (Integer) map.get("state");
            String msg = (String)map.get("msg");
            result.setCode(state);
            result.setMessage(msg);
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("转件失败");
        }
        return result;
    }

    /**
     * 拉黑/洗白
     * @param contractId
     * @return
     */
    @RequestMapping(value="/whiteBlackList",produces = "application/json") @ResponseBody
    public Result whiteBlackList(Integer contractId,String userId,String reason,String type){
        Result result = new Result();
        try{
            int state = loanManagementService.whiteBlackList(contractId,userId,reason,type);
            if(state == Result.SUCCESS){
                result.setCode(Result.SUCCESS);
                result.setMessage("拉黑成功");
            }else{
                result.setCode(Result.FAIL);
                result.setMessage("拉黑失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    /**
     * 查询催收队列
     */
    @RequestMapping(value="/queryCollectors",produces = "application/json") @ResponseBody
    public Result queryCollectors(HttpServletRequest request,String userId){
        Result result = new Result();
        int offset =  Integer.valueOf(request.getParameter("skip") == null ? "0" : request.getParameter("skip"));
        int size = Integer.valueOf(request.getParameter("take") == null ? Integer.MAX_VALUE + "" : request.getParameter("take"));
        try{
            Map<String,Object> queryMap = QueryParamUtils.getargs(request.getParameterMap());
            PageHelper.offsetPage(offset,size);
            queryMap.put("userId",userId);
            PageInfo<CollectorsListVo> info = loanManagementService.selectCollectorsListVo(queryMap);
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

    /**
     * 提交催收备注
     * @param remark
     * @return
     */
    @RequestMapping(value="/collectionRemark",produces = "application/json") @ResponseBody
    public Result collectionRemark(HttpServletRequest request,@ModelAttribute("reamrk")CollectorsRemark remark){
        Result result = new Result();
        try{
            remark.setCreateDate(new Date());
            int state = loanManagementService.addCollectionRemark(remark);
            if(state > 0){
                result.setCode(Result.SUCCESS);
                result.setMessage("催收备注提交成功");
            }else{
                result.setCode(Result.FAIL);
                result.setMessage("备注提交失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("备注提交失败");
        }
        return result;
    }

    /**
     * 查询绑定银行卡主卡
     * @param userId 用户ID
     * @return
     */
    @RequestMapping(value="/queryMainBankInfo",produces = "application/json") @ResponseBody
    public Result queryMainBankInfo(Integer userId){
        Result result = new Result();
        try{
            BankVo bankVo = loanManagementService.selectMainBankByUserId(userId);
            List<BankList> banks = loanManagementService.selectBankList();
            JSONObject object = new JSONObject();
            object.put("bankInfo",bankVo);
            object.put("banks",banks);
            result.setCode(Result.SUCCESS);
            result.setMessage("查询成功");
            result.setObject(object);
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("查询失败");
        }
        return result;
    }

    /**
     * 扣款
     * @param askCollection
     * @return
     */
    @RequestMapping(value="/askCollection",produces = "application/json") @ResponseBody
    public Result askCollection(@ModelAttribute("askCollection") AskCollection askCollection){
        Result result = new Result();
        try{
            return loanManagementService.askCollection(askCollection);
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("提交扣款失败!");
        }
        return result;
    }

    /**
     * 拉卡拉代扣
     * @param askCollection
     * @return
     */
    @RequestMapping(value="/lakalaAskCollection",produces = "application/json") @ResponseBody
    public Result lakalaAskCollection(@ModelAttribute("askCollection") AskCollection askCollection){
        Result result = new Result();
        try{
            return loanManagementService.lakalaAskCollection(askCollection);
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("提交扣款失败!");
        }
        return result;
    }
    /**
     * 查询催收信息
     * @param request
     * @return
     */
    @RequestMapping(value="/queryCollectorsInfo",produces = "application/json") @ResponseBody
    public Result queryCollectorsInfo(HttpServletRequest request){
        Result result = new Result();
        int offset =  Integer.valueOf(request.getParameter("skip") == null ? "0" : request.getParameter("skip"));
        int size = Integer.valueOf(request.getParameter("take") == null ? Integer.MAX_VALUE + "" : request.getParameter("take"));
        String userNo = StringUtils.isEmpty(request.getParameter("userNo")) ? "SYSTEM" : request.getParameter("userNo");

        try{
            Map<String,Object> queryMap = QueryParamUtils.getargs(request.getParameterMap());

            PageInfo<CollectorsListVo> info = loanManagementService.selectCollectorsInfo(queryMap,offset,size,userNo);
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

    @RequestMapping(value="/queryExportCount",produces = "application/json") @ResponseBody
    public Result queryExportCount(HttpServletRequest request){
        Result result = new Result();
        try{
            Map<String,Object> queryMap = QueryParamUtils.getargs(request.getParameterMap());
            int count = loanManagementService.queryExportCount(queryMap);
            result.setCode(Result.SUCCESS);
            result.setMessage("加载成功");
            result.setObject(count);
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("加载失败");
        }
        return result;
    }

    @RequestMapping(value="/exportLoans")
    public void exportLoans(HttpServletRequest request, HttpServletResponse response,Integer count,String userNo){
        try{
            if( Constants.DOWNLOAD_MAX_ITEMS <= count.intValue()){
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write("<!DOCTYPE html><head><script> if(confirm('一次性下数据量多于5W条!')){window.location.href = document.referrer;}else{window.location.href = document.referrer;} </script></head>");
                return;
            }
            if(!exportConcurrentService.getExportToken()){
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write("<!DOCTYPE html><head><script> if(confirm('下载人数过多,请稍后重试!')){window.location.href = document.referrer;}else{window.location.href = document.referrer;} </script></head>");
                return;
            }
            Map<String,Object> queryMap = QueryParamUtils.getargs(request.getParameterMap());
            List<LoanManagementVo> loanManagements = loanManagementService.selectExportData(queryMap,count,userNo);
            Map<String, Object> map = new HashMap<>();
            map.put(NormalExcelConstants.FILE_NAME, "贷后管理" + new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date()));
            map.put(NormalExcelConstants.CLASS, LoanManagementVo.class);
            map.put(NormalExcelConstants.DATA_LIST, loanManagements);
            map.put(NormalExcelConstants.PARAMS, new ExportParams());

            ExcelUtils.jeecgSingleExcel(map,request,response);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequestMapping(value="/queryExportRemarkCount",produces = "application/json") @ResponseBody
    public Result queryExportRemarkCount(HttpServletRequest request,String userNo){
        Result result = new Result();
        try{
            Map<String,Object> queryMap = QueryParamUtils.getargs(request.getParameterMap());
            int count = loanManagementService.queryExportRemarkCount(queryMap,userNo);
            result.setCode(Result.SUCCESS);
            result.setMessage("加载成功");
            result.setObject(count);
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("加载失败");
        }
        return result;
    }

    @RequestMapping(value="/exportLoansRemark")
    public void exportLoansRemark(HttpServletRequest request, HttpServletResponse response,String userNo,Integer count){
        try{
            if( Constants.DOWNLOAD_MAX_ITEMS <= count.intValue()){
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write("<!DOCTYPE html><head><script> if(confirm('一次性下数据量多于5W条!')){window.location.href = document.referrer;}else{window.location.href = document.referrer;} </script></head>");
                return;
            }
            if(!exportConcurrentService.getExportToken()){
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write("<!DOCTYPE html><head><script> if(confirm('下载人数过多,请稍后重试!')){window.location.href = document.referrer;}else{window.location.href = document.referrer;} </script></head>");
                return;
            }
            Map<String,Object> queryMap = QueryParamUtils.getargs(request.getParameterMap());
            Map<String, Object> map = new HashMap();
            if(collectorsLevelService.isOutWorker(userNo)){
                List<LoansRemarkOutVo> loansRemarkOutVoList = loanManagementService.selectExportLoansRemarkForOutWorkers(queryMap, userNo);
                map.put(NormalExcelConstants.CLASS, LoansRemarkOutVo.class);
                map.put(NormalExcelConstants.DATA_LIST, loansRemarkOutVoList);
            }else{
                List<LoansRemarkVo> loansRemark = loanManagementService.selectExportLoansRemarkVo(queryMap,userNo);
                map.put(NormalExcelConstants.CLASS, LoansRemarkVo.class);
                map.put(NormalExcelConstants.DATA_LIST, loansRemark);
            }
            map.put(NormalExcelConstants.FILE_NAME, "催收备注" + new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date()));
            map.put(NormalExcelConstants.PARAMS, new ExportParams());
            ExcelUtils.jeecgSingleExcel(map,request,response);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/batchCollection",produces = "application/json")
    @ResponseBody
    public Result batchCollection(String reduceData,String reduceMoney,String createUser, String deductionsType, String collectType){
        Result result = new Result();
        try{
            if(StringUtils.isNotEmpty(reduceData)){
                List<LoanManagementVo> askCollections = JSON.parseArray(reduceData,LoanManagementVo.class);
                result = loanManagementService.batchCollection(askCollections,reduceMoney,createUser,deductionsType, collectType);
            }else{
                result.setCode(Result.FAIL);
                result.setMessage("代扣信息为空");
            }
        }catch (RedisException e){
            e.printStackTrace();
            result.setCode(Result.SUCCESS);
            result.setMessage("代扣请求已经发送!");
        }catch (TimeoutException e1){
            e1.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("代扣请求超时,请稍后查看流水!");
        }
        return result;
    }

    /**
     * 拉卡拉批量代扣
     * @param reduceData
     * @param reduceMoney
     * @param createUser
     * @param deductionsType
     * @return
     */
    @RequestMapping(value = "/lakalaBatchCollection",produces = "application/json")
    @ResponseBody
    public Result lakalaBatchCollection(String reduceData,String reduceMoney,String createUser, String deductionsType){
        Result result = new Result();
        try{
            if(StringUtils.isNotEmpty(reduceData)){
                List<LoanManagementVo> askCollections = JSON.parseArray(reduceData,LoanManagementVo.class);
                result = loanManagementService.lakalaBatchCollection(askCollections,reduceMoney,createUser,deductionsType);
            }else{
                result.setCode(Result.FAIL);
                result.setMessage("代扣信息为空");
            }
        }catch (RedisException e){
            e.printStackTrace();
            result.setCode(Result.SUCCESS);
            result.setMessage("代扣请求已经发送!");
        }
        return result;
    }


    @RequestMapping(value = "/modifyPersonBlackList",produces = "application/json")  @ResponseBody
    public Result modifyPersonBlackList(){
        Result result = new Result();
        try{
            List<String> excute = loanManagementService.modifyPersonBlackList();
            result.setCode(Result.SUCCESS);
            result.setMessage("执行成功");
            result.setObject(excute);
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("代扣失败");
        }
        return result;
    }

    /**
     * 检查是否可以下载及检查最大下载条数
     * @return
     */
    @RequestMapping(value = "/checkCanDownload",produces = "application/json")  @ResponseBody
    public Result checkCanDownload(){
        Result result = new Result();
        try{
            Download download = loanManagementService.checkCanDownload();
            result.setCode(Result.SUCCESS);
            result.setMessage("检查成功");
            result.setObject(download);
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("检查失败");
        }
        return result;
    }

    @RequestMapping(value = "/loadProducts",produces = "application/json")  @ResponseBody
    public Result loadProducts(){
        Result result = new Result();
        try{
            List<Product> products = loanManagementService.selectProducts();
            result.setCode(Result.SUCCESS);
            result.setMessage("查询成功");
            result.setObject(products);
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("查询失败");
        }
        return result;
    }

    @RequestMapping("/resetTokenQueue")
    @ResponseBody
    public String resetTokenQueue(HttpServletRequest request, HttpServletResponse response,long interval,int volume){
        try {
            String interval1 = "";
            String volume1 = "";
            if(interval > 0){
                 interval1 = exportConcurrentService.setFillInterval(interval);
            }
            if(volume > 0){
                 volume1 = exportConcurrentService.setFillVolume(volume);
            }
            return "set tokenQ`s volume:"+volume1+",interval period:"+interval1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "设置tokenQ失败!";
    }

    @RequestMapping("/autoTran")
    @ResponseBody
    public Result autoTran() {
        Result result = new Result();
        try {
            autoTranService.autoTran();
            result.setCode(Result.SUCCESS);
            result.setMessage("自动分担成功");
        } catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("自动分担失败："+e.getMessage());
        }
        return result;
    }
}
