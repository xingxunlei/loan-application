package com.loan_manage.controller;

import com.github.pagehelper.PageInfo;
import com.loan_entity.loan.CollectorsLevel;
import com.loan_entity.manager.LoanCompany;
import com.loan_manage.entity.Result;
import com.loan_manage.service.CollectorsLevelService;
import com.loan_manage.service.LoanCompanyService;
import com.loan_manage.service.RedisService;
import com.loan_manage.utils.QueryParamUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "auth")
public class AuthController {

    @Autowired
    private CollectorsLevelService collectorsLevelService;
    @Autowired
    private LoanCompanyService loanCompanyService;
    @Resource
    private RedisService redisService;
    /**
     * 查询所有公司
     * @return
     */
    @RequestMapping(value = "loanCompanys",method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody Result loanCompanys(){
        Result result = new Result();
        try {
            List<LoanCompany> companies = loanCompanyService.selectAllLoanCompanys();
            result.setCode(Result.SUCCESS);
            result.setMessage("数据加载成功");
            result.setObject(companies);
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("数据加载失败");
        }
        return result;
    }

    /**
     * 分页加载公司
     * @return
     */
    @RequestMapping(value = "loanCompanysByPage",method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody Result loanCompanysByPage(){
        Result result = new Result();
        try {
            List<LoanCompany> companies = loanCompanyService.selectAllLoanCompanys();
            result.setCode(Result.SUCCESS);
            result.setMessage("数据加载成功");
            result.setObject(new PageInfo<LoanCompany>(companies));
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("数据加载失败");
        }
        return result;
    }

    /**
     * 编辑系统用户
     * @param company
     * @return
     */
    @RequestMapping(value = "editCompanyInfo",method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody Result editCompanyInfo(LoanCompany company){
        Result result = new Result();
        try{
            int editCount = collectorsLevelService.editCompanyInfo(company);
            if(editCount > 0){
                result.setCode(Result.SUCCESS);
                result.setMessage("操作成功");
            }else{
                result.setCode(Result.FAIL);
                result.setMessage("操作失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("操作失败");
        }
        return result;
    }

    /**
     * 获取工号
     * @return
     */
    @RequestMapping(value = "loadNewUsersysno",method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody Result loadNewUsersysno(){
        Result result = new Result();
        try{
            result.setCode(Result.SUCCESS);
            result.setMessage("数据加载成功");
            result.setObject(collectorsLevelService.generateNewSysno());
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("数据加载失败");
        }
        return result;
    }

    /**
     * 系统人员管理
     * @param request
     * @return
     */
    @RequestMapping(value = "loadCollectorsLevels",method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody Result loadCollectorsLevels(HttpServletRequest request){
        Result result = new Result();
        int offset =  Integer.valueOf(request.getParameter("skip") == null ? "0" : request.getParameter("skip"));
        int size = Integer.valueOf(request.getParameter("take") == null ? Integer.MAX_VALUE + "" : request.getParameter("take"));
        String userNo = StringUtils.isEmpty(request.getParameter("userNo")) ? "SYSTEM" : request.getParameter("userNo");
        try{
            Map<String,Object> queryMap = QueryParamUtils.getargs(request.getParameterMap());
            PageInfo<CollectorsLevel> infos = collectorsLevelService.selectCollectorsLevels(queryMap,offset,size,userNo);
            result.setCode(Result.SUCCESS);
            result.setMessage("数据加载成功");
            result.setObject(infos);
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("数据加载失败");
        }
        return result;
    }

    /**
     * 编辑系统用户
     * @param collectorsLevel
     * @return
     */
    @RequestMapping(value = "editCollectorsLevel",method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody Result editCollectorsLevel(CollectorsLevel collectorsLevel){
        Result result = new Result();
        try{
            int editCount = collectorsLevelService.editCollectorsLevel(collectorsLevel);
            if(editCount > 0){
                result.setCode(Result.SUCCESS);
                result.setMessage("操作成功");
            }else{
                result.setCode(Result.FAIL);
                result.setMessage("操作失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("操作失败");
        }
        return result;
    }

    /**
     * 同步OA权限信息
     * @return
     */
    @RequestMapping(value = "doSyncOaUser",method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody Result doSyncOaUser(){
        Result result = new Result();
        try{
            boolean flag = redisService.syanLoginUser();
            if(flag){
                result.setCode(Result.SUCCESS);
                result.setMessage("同步成功");
            }else{
                result.setCode(Result.FAIL);
                result.setMessage("同步失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("同步发生异常");
        }
        return result;
    }

    /**
     * 发送验证码
     * @return
     */
    @RequestMapping(value = "sendVerifyCode",method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody Result sendVerifyCode(String userName,String password){
        Result result = new Result();
        try{
            if(StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)){
                result.setCode(Result.FAIL);
                result.setMessage("校验参数异常!");
                return result;
            }
            return collectorsLevelService.sendVerifyCode(userName,password);
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("验证码发送发生异常");
            return result;
        }
    }

    /**
     * 修改密码
     * @param userNo
     * @param oldPwd
     * @param newPwd
     * @param newPwdConfirm
     * @return
     */
    @RequestMapping(value = "modifyPassword",method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody Result modifyPassword(String userNo,String oldPwd,String newPwd,String newPwdConfirm){
        Result result = new Result();
        try{
            result = collectorsLevelService.modifyPassword(userNo,oldPwd,newPwd,newPwdConfirm);
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("修改密码发生异常");

        }
        return result;
    }

    /**
     * 获取远程ID
     * @param request
     * @return
     */
    @RequestMapping(value = "loadRemoteAddr",method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody Result loadRemoteAddr(HttpServletRequest request){
        Result result = new Result();
        try{
            result.setCode(Result.SUCCESS);
            result.setMessage("IP获取成功");
            result.setObject(getIpAddr(request));
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage("IP获取发生异常");

        }
        return result;
    }

    public static String loadRemoteIp(HttpServletRequest request){
        if (request.getHeader("x-forwarded-for") == null){
            return request.getRemoteAddr();
        }
        return request.getHeader("x-forwarded-for");
    }

    public static String getRealIp() throws SocketException {

        String localip = null;// 本地IP，如果没有配置外网IP则返回它
        Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;
        boolean finded = false;// 是否找到外网IP

        while (netInterfaces.hasMoreElements() && !finded) {
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 内网IP
                    localip = ip.getHostAddress();
                    System.out.println("netip："+localip);
                    break;
                }
            }
        }
        return localip;
    }


    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if(ip != null && ip.contains(",")){
            ip=ip.split(",")[0];
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


    /* public static String getIpAddr(final HttpServletRequest request)
           throws Exception {
        if (request == null) {
            throw (new Exception("getIpAddr method HttpServletRequest Object is null"));
        }
        String ipString = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getRemoteAddr();
        }

        // 多个路由时，取第一个非unknown的ip
        final String[] arr = ipString.split(",");
        for (final String str : arr) {
            if (!"unknown".equalsIgnoreCase(str)) {
                ipString = str;
                break;
            }
        }

        return ipString;
    }*/

    public static String getLocalIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String forwarded = request.getHeader("X-Forwarded-For");
        String realIp = request.getHeader("X-Real-IP");

        String ip = null;
        if (realIp == null) {
            if (forwarded == null) {
                ip = remoteAddr;
            } else {
                ip = remoteAddr + "/" + forwarded.split(",")[0];
            }
        } else {
            if (realIp.equals(forwarded)) {
                ip = realIp;
            } else {
                if(forwarded != null){
                    forwarded = forwarded.split(",")[0];
                }
                ip = realIp + "/" + forwarded;
            }
        }
        return ip;
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static void main(String[] args) throws Exception {
        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()) {
            NetworkInterface ni = en.nextElement();
            printParameter(ni);

        }
    }

    public static void printParameter(NetworkInterface ni) throws SocketException {
        List<InterfaceAddress> list = ni.getInterfaceAddresses();
        Iterator<InterfaceAddress> it = list.iterator();

        while (it.hasNext()) {
            InterfaceAddress ia = it.next();
            System.out.println(" Address = " + ia.getAddress());
            System.out.println(" Broadcast = " + ia.getBroadcast());
            System.out.println(" Network prefix length = " + ia.getNetworkPrefixLength());
            System.out.println("");
        }
    }
}
