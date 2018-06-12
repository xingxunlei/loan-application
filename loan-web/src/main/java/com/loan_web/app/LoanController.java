package com.loan_web.app;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loan_api.app.LoanService;
import com.loan_api.app.TimerService;
import com.loan_api.app.UserService;
import com.loan_api.loan.BankService;
import com.loan_api.loan.YsbCollectionService;
import com.loan_api.loan.YsbpayService;
import com.loan_entity.app.NoteResult;
import com.loan_entity.loan_vo.BatchCollectEntity;
import com.loan_utils.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

@Controller
@RequestMapping("/loan")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private UserService userService;

    @Autowired
    private BankService bankService;

    @Autowired
    private YsbCollectionService ysbCollectionService;

    @Autowired
    private YsbpayService ysbpayService;

    @Autowired
    private TimerService timerService;

    private static final String SUCCESS_CODE = CodeReturn.SUCCESS_CODE;

    private static final String FAIL_CODE = CodeReturn.FAIL_CODE;

    private static final String TOKEN_WRONG = CodeReturn.TOKEN_WRONG;

    private static final String MD5_WRONG = CodeReturn.MD5_WRONG;

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(LoanController.class);



    /**
     * 用户借款状态节点
     * @param per_id 用户ID
     * @return
     */
    @ResponseBody
    @RequestMapping("/getBorrStatus")
    public String getBorrStatus(String per_id,String md5sign){
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        if(StringUtils.isEmpty(per_id)){
            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id);
        if(flag){
            result = loanService.getBorrStatus(per_id);
        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }
        return JSONObject.toJSONString(result);
    }

    /**
     * 获得产品金额
     * @param per_id 用户ID
     * @return
     */
    @ResponseBody
    @RequestMapping("/getProductsMoney")
    public String getProductsMoney(String per_id,String md5sign){
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        boolean flag = VerifyMD5.Sign(md5sign, per_id);
        if(flag){
            result = loanService.getProductsMoney(per_id);
        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }
        return JSONObject.toJSONString(result);
    }

    /**
     * 获得产品天数
     * @param per_id 用户ID
     * @param money 产品金额
     * @return
     */
    @ResponseBody
    @RequestMapping("/getProductsday")
    public String getProductsday(String per_id,String money,String md5sign){
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        boolean flag = VerifyMD5.Sign(md5sign, per_id,money);
        if(flag){
            result = loanService.getProductsday(per_id,money);
        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }
        return JSONObject.toJSONString(result);
    }

    /**
     * 选择产品金额和天数,获得产品ID及所有费用
     * @param per_id 用户ID
     * @return
     */
    @ResponseBody
    @RequestMapping("/getProductCharge")
    public String getProductCharge(String per_id,String money,String day,String md5sign){
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        boolean flag = VerifyMD5.Sign(md5sign, per_id,money,day);
        if(flag){
            result = loanService.getProductCharge(null, per_id, money, day);
        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }
        return JSONObject.toJSONString(result);
    }

    /**
     * 生成借款记录
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/borrowProduct", method = RequestMethod.POST)
    public String borrowProduct(HttpServletRequest request){
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");

        String per_id = request.getParameter("per_id");
        String product_id = request.getParameter("product_id");
        String plan_repay = request.getParameter("plan_repay");
        String borr_amount = request.getParameter("borr_amount");
        String day = request.getParameter("day");
        String coupon_id = request.getParameter("coupon_id");
        String token = request.getParameter("token");
        String md5sign = request.getParameter("md5sign");

        // logger.info("请求参数："+per_id+product_id+plan_repay+borr_amount+day+coupon_id+token);

        // logger.info("不带版本请求参数："+per_id+ product_id+ plan_repay+
        // borr_amount+day+coupon_id+token);
        boolean flag = VerifyMD5.Sign(md5sign, per_id, product_id, plan_repay, borr_amount,day,coupon_id,token);

        // logger.info("验证md5结果："+flag);
        if(flag){
            String verify = loanService.verifyTokenId(per_id, token);
            if(SUCCESS_CODE.equals(verify)){
            		int pid = Integer.parseInt(product_id);
	        		if((pid == 9 || pid == 10) && !userService.isInWhiteList(per_id)) {
	        			result.setCode(FAIL_CODE);
                    result.setInfo("您没有2000元借款资质");
	        		}else {
                    result = loanService.borrowProduct(per_id, product_id, plan_repay, borr_amount,day,coupon_id);
	        		}
            }else{
                result.setCode(TOKEN_WRONG);
                result.setInfo("您的账号已在另一台设备上登录，请重新登录");
            }
        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }

         return JSONObject.toJSONString(result);
    }

    /**
     * 检查用户是否认证完成
     * @param per_id 用户ID
     * @return
     */
    @ResponseBody
    @RequestMapping("/checkBpm")
    public String checkBpm(String per_id,String md5sign){
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        if(StringUtils.isEmpty(per_id)){
            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id);
        if(flag){
            result = loanService.checkBpm(per_id);

        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }
         return JSONObject.toJSONString(result);
    }

    /**
     * 新建认证流程
     * @param per_id 用户ID
     * @return
     */
    @ResponseBody
    @RequestMapping("/insertPerBpm")
    public String insertPerBpm(String per_id,String token,String md5sign){
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        if(StringUtils.isEmpty(per_id)){
            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id, token);
        if(flag){
            String verify = loanService.verifyTokenId(per_id, token);
            if(SUCCESS_CODE.equals(verify)){
                result = loanService.insertPerBpm(per_id);
            }else{
                result.setCode(TOKEN_WRONG);
                result.setInfo("您的账号已在另一台设备上登录，请重新登录");
            }
        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }

         return JSONObject.toJSONString(result);
    }

    /**
     * 插入身份证正面信息
     * @param per_id 用户ID
     * @param card_byte 身份证正面照片流
     * @return
     */
    @ResponseBody
    @RequestMapping("/insertCardInfoz")
    public String insertCardInfoz(String per_id,String card_num, String name,
                                  String sex, String nation,String birthday,
                                  String address, String card_byte,String head_byte,String description,String token,String md5sign){
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");

        if (StringUtils.isEmpty(per_id) ||StringUtils.isEmpty(card_num) ||StringUtils.isEmpty(name) ||StringUtils.isEmpty(sex)
                ||StringUtils.isEmpty(nation) ||StringUtils.isEmpty(birthday) ||StringUtils.isEmpty(address)){
            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id,card_num,name,sex,nation,
                birthday,address,card_byte,head_byte,description,token);
        if(flag){
            String verify = loanService.verifyTokenId(per_id, token);
            if(SUCCESS_CODE.equals(verify)){
                if (card_num.contains("*")){
                    //身份证号识别有误  重新扫描

                }
                result = loanService.insertCardInfoz(per_id,card_num,name,sex,nation,
                                                            birthday,address,card_byte,head_byte,description);
            }else{
                result.setCode(TOKEN_WRONG);
                result.setInfo("您的账号已在另一台设备上登录，请重新登录");
            }
        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }

         return JSONObject.toJSONString(result);
    }

    /**
     * 插入身份证反面信息
     * @param per_id 用户ID
     * @param card_byte 身份证反面照片流
     * @return
     */
    @ResponseBody
    @RequestMapping("/insertCardInfof")
    public String insertCardInfof(String per_id,String office, String start_date,
            String end_date,String card_byte, String description,String token,String md5sign) {
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        if (StringUtils.isEmpty(per_id) || StringUtils.isEmpty(office) || StringUtils.isEmpty(start_date)){

            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id, office, start_date, end_date, card_byte,description,token);
        if(flag){
            String verify = loanService.verifyTokenId(per_id, token);
            if(SUCCESS_CODE.equals(verify)){
                result = loanService.insertCardInfof(per_id, office, start_date, end_date, card_byte, description);
            }else{
                result.setCode(TOKEN_WRONG);
                result.setInfo("您的账号已在另一台设备上登录，请重新登录");
            }

        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }
         return JSONObject.toJSONString(result);
    }

//    @ResponseBody
//    @RequestMapping("/ocrFront")
//    public String ocrFront(String per_id,String photo,String token,String md5sign){
//        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
//        if(    per_id == null || "".equals(per_id.trim())
//                              ||  
//                photo == null || "".equals(photo.trim())){
//            return JSONObject.toJSONString(result);
//        }
//        String verify = loanService.verifyTokenId(per_id, token);
//        if(SUCCESS_CODE.equals(verify)){
//            result = loanService.ocrFront(per_id, photo);
//        }else{
//            result.setCode(TOKEN_WRONG);
//            result.setInfo("您的账号已在另一台设备上登录，请重新登录");
//        }
//        return JSONObject.toJSONString(result);
//    }
//    
//    
//    @ResponseBody
//    @RequestMapping("/ocrBack")
//    public String ocrBack(String per_id,String photo,String token,String md5sign){
//        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
//        if(    per_id == null || "".equals(per_id.trim())
//                              ||  
//                photo == null || "".equals(photo.trim())){
//            return JSONObject.toJSONString(result);
//        }
//        String verify = loanService.verifyTokenId(per_id, token);
//        if(SUCCESS_CODE.equals(verify)){
//            result = loanService.ocrBack(per_id, photo);
//        }else{
//            result.setCode(TOKEN_WRONG);
//            result.setInfo("您的账号已在另一台设备上登录，请重新登录");
//        }
//        return JSONObject.toJSONString(result);
//    }
//    
//    @ResponseBody
//    @RequestMapping("/compareAll")
//    public String compareAll(String per_id,String photos,String token){
//        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
//        if(    per_id == null || "".equals(per_id.trim())
//                              ||  
//                photos == null || "".equals(photos.trim())){
//            return JSONObject.toJSONString(result);
//        }
//        String verify = loanService.verifyTokenId(per_id, token);
//        if(SUCCESS_CODE.equals(verify)){
//            result = loanService.compareAll(per_id, photos);
//        }else{
//            result.setCode(TOKEN_WRONG);
//            result.setInfo("您的账号已在另一台设备上登录，请重新登录");
//        }
//        return JSONObject.toJSONString(result);
//    }
//    
    /**
     * 插入个人信息
     * @param per_id 用户ID @param qq_num QQ号  @param email 邮箱
     * @param usuallyaddress 常用地址 @param education 学历
     * @param marry 婚姻状况  @param getchild 生育状况
     * @param profession 职业 @param monthlypay 月薪
     * @param business 单位名 @param busi_province 单位所在省
     * @param busi_city 单位所在市 @param busi_address 单位详细地址
     * @param busi_phone 单位电话 @param relatives 亲属关系
     * @param relatives_name 亲属名字 @param rela_phone 亲属联系方式
     * @param society 社会关系 @param soci_phone 社会联系方式
     * @param society_name 社会关系名字
     * @return
     */
    @ResponseBody
    @RequestMapping("/insertPrivateInfo")
    public String insertPrivateInfo(String per_id, String qq_num, String email, String usuallyaddress,
            String education, String marry, String getchild, String profession, String monthlypay, String business,
            String busi_province, String busi_city, String busi_address, String busi_phone, String relatives,
            String relatives_name, String rela_phone, String society, String soci_phone, String society_name,String token,String md5sign){
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        if(    per_id == null || "".equals(per_id.trim())
                              ||
               qq_num == null || "".equals(qq_num.trim())
                              ||
                email == null || "".equals(email.trim())
                              ||
       usuallyaddress == null || "".equals(usuallyaddress.trim())
                              ||
            education == null || "".equals(education.trim())
                              ||
                marry == null || "".equals(marry.trim())
                              ||
             getchild == null || "".equals(getchild.trim())
                              ||
           profession == null || "".equals(profession.trim())
                              ||
           monthlypay == null || "".equals(monthlypay.trim())
                              ||
             business == null || "".equals(business.trim())
                              ||
        busi_province == null || "".equals(busi_province.trim())
                              ||
            busi_city == null || "".equals(busi_city.trim())
                              ||
         busi_address == null || "".equals(busi_address.trim())
                              ||
           busi_phone == null || "".equals(busi_phone.trim())
                              ||
            relatives == null || "".equals(relatives.trim())
                              ||
       relatives_name == null || "".equals(relatives_name.trim())
                              ||
           rela_phone == null || "".equals(rela_phone.trim())
                              ||
              society == null || "".equals(society.trim())
                              ||
           soci_phone == null || "".equals(soci_phone.trim())
                              ||
         society_name == null || "".equals(society_name.trim())){

            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id,qq_num, email, usuallyaddress, education, marry,
                getchild, profession, monthlypay, business, busi_province,
                busi_city, busi_address, busi_phone, relatives, relatives_name,
                rela_phone, society, soci_phone, society_name,token);
        if(flag){
            String verify = loanService.verifyTokenId(per_id, token);
            if(SUCCESS_CODE.equals(verify)){
                result = loanService.insertPrivateInfo(per_id, qq_num, email, usuallyaddress, education, marry,
                                                              getchild, profession, monthlypay, business, busi_province,
                                                              busi_city, busi_address, busi_phone, relatives, relatives_name,
                                                              rela_phone, society, soci_phone, society_name);
            }else{
                result.setCode(TOKEN_WRONG);
                result.setInfo("您的账号已在另一台设备上登录，请重新登录");
            }
        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }

         return JSONObject.toJSONString(result);
    }

    /**
     * 银行卡认证
     * @param per_id 用户ID
     * @return
     */
    @ResponseBody
    @RequestMapping("/insertBankInfo")
    public String insertBankInfo(String per_id,String bank_id, String bank_num, String phone,String status,String tokenKey,String token,String md5sign){
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        if (StringUtils.isEmpty(per_id) ||StringUtils.isEmpty(bank_id) ||StringUtils.isEmpty(bank_num) ||StringUtils.isEmpty(phone) ||StringUtils.isEmpty(status) ){
            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id, bank_id, bank_num, phone, status,tokenKey,token);
        if(flag){
            String verify = loanService.verifyTokenId(per_id, token);
            if(SUCCESS_CODE.equals(verify)){
                if(tokenKey == null){
                    tokenKey = "";
                }
                 logger.info("--------绑卡请求：参数："+per_id+bank_id+ bank_num+ phone+ status,tokenKey);
                 result = bankService.bindingBank(per_id, bank_id, bank_num, phone, status,tokenKey);
            }else{
                result.setCode(TOKEN_WRONG);
                result.setInfo("您的账号已在另一台设备上登录，请重新登录");
            }
        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }

         return JSONObject.toJSONString(result);
    }

    /**
     * 查询银行卡认证（前端不需要）
     * @param per_id 用户ID
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryBankInfo")
    public String queryBankInfo(String per_id,String bank_num){
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        if (StringUtils.isEmpty(per_id) ||StringUtils.isEmpty(bank_num)){
            return JSONObject.toJSONString(result);
        }
         result = bankService.queryContractId(per_id, bank_num);
         return JSONObject.toJSONString(result);
    }

    /**
     * 获取自己的手机号，及运营商展示
     * @param per_id 用户ID
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPhoneInfo")
    public String getPhoneInfo(String per_id,String md5sign) {
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        if(StringUtils.isEmpty(per_id) ){
            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id);
        if(flag){
            result = loanService.getPhoneInfo(per_id);

        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }
         return JSONObject.toJSONString(result);
    }

    /**
     * 获取用户本地的手机通讯录名单
     * @param per_id 用户ID
     * @param phone_list 用户本地的手机通讯录名单
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPhoneList")
    public String getPhoneList(String per_id, String phone_list,String md5sign) {
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        if (StringUtils.isEmpty(per_id) ||StringUtils.isEmpty(phone_list)){

            return JSONObject.toJSONString(result);
        }

         result = loanService.getPhoneList(per_id, phone_list);
         return JSONObject.toJSONString(result);
    }

    /**
     * 认证完成，修改用户、流程、借款状态
     * @param per_id 用户ID
     * @return
     */
    @ResponseBody
    @RequestMapping("/bpmFinish")
    public String bpmFinish(String per_id,String token,String md5sign) {
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        if(StringUtils.isEmpty(per_id)){
            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id,token);
        if(flag){
            String verify = loanService.verifyTokenId(per_id, token);
            if(SUCCESS_CODE.equals(verify)){
                result = loanService.bpmFinish(per_id);
            }else{
                result.setCode(TOKEN_WRONG);
                result.setInfo("您的账号已在另一台设备上登录，请重新登录");
            }
        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }

         return JSONObject.toJSONString(result);
    }

    /**
     * 获取签约界面信息
     * @param per_id
     * @return
     */
    @ResponseBody
    @RequestMapping("/getSignInfo")
    public String getSignInfo(String per_id,String token,String md5sign) {
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");

        if(per_id == null || "".equals(per_id.trim())){
            return JSONObject.toJSONString(result);
        }
        String verify = loanService.verifyTokenId(per_id, token);
        if(SUCCESS_CODE.equals(verify)){
            result = loanService.getSignInfo(per_id);
        }else{
            result.setCode(TOKEN_WRONG);
            result.setInfo("您的账号已在另一台设备上登录，请重新登录");
        }

         return JSONObject.toJSONString(result);
    }

    /**
     * 获取签约界面信息
     *
     * @param per_id
     * @return
     */

    @RequestMapping("/getSignPage")
    public String getSignPage(String per_id, String token, HttpServletRequest request) {
        NoteResult result = new NoteResult();
        String verify = loanService.verifyTokenId(per_id, token);
        if (SUCCESS_CODE.equals(verify)) {
            result = loanService.getSignInfo(per_id);
            request.setAttribute("token",token);
        } else {
            result.setCode(CodeReturn.FAIL_CODE);
            result.setInfo("失败");
        }
        request.setAttribute("data",result);
        return "signPage";
    }

    @RequestMapping("/verifyToken") @ResponseBody
    public NoteResult verifyToken(String per_id, String token){
        NoteResult result = new NoteResult();
        String verify = loanService.verifyTokenId(per_id, token);
        if(SUCCESS_CODE.equals(verify)){
            result.setCode(CodeReturn.SUCCESS_CODE);
        }else{
            result.setCode(CodeReturn.FAIL_CODE);
        }
        return result;
    }

    /**
     * 合同签约，状态改为已签约，添加签约时间
     * @param per_id 用户ID
     * @param borr_id 合同id
     * @return
     */
    @ResponseBody
    @RequestMapping("/signingBorrow") @Transactional()
    public String signingBorrow(String per_id, String borr_id,String name,String money,String day,String token,String md5sign) {
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        String str = JSONObject.toJSONString(result);
        if(StringUtils.isEmpty(per_id) || StringUtils.isEmpty(borr_id) || StringUtils.isEmpty(name) || StringUtils.isEmpty(money) || StringUtils.isEmpty(day)){

            return str;
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id, borr_id, name, money, day,token);
        if(flag){
            String verify = loanService.verifyTokenId(per_id, token);
            if(SUCCESS_CODE.equals(verify)){
                 result = loanService.signingBorrow(per_id, borr_id, name, money, day);
                 if("200".equals(result.getCode())){//签约成功，发站内信
                     String params =name+","+money+"元,"+day+"天";
                     str = userService.setMessage(per_id,"1",params);
                 }else{
                    result.setInfo(result.getInfo());
                     str = JSONObject.toJSONString(result);
                 }
            }else{
                result.setCode(TOKEN_WRONG);
                result.setInfo("您的账号已在另一台设备上登录，请重新登录");
            }
        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }

        return JSONObject.toJSONString(result);
    }

    /**
     * 取消借款申请。判断合同状态，在申请中的合同才能取消借款申请。
     * @param per_id 用户ID
     * @param borr_id 合同id
     * @return
     */
    @ResponseBody
    @RequestMapping("/cancelAskBorrow")
    public String cancelAskBorrow(String per_id, String borr_id,String token,String md5sign) {
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        if(per_id == null || "".equals(per_id.trim())
                          ||
          borr_id == null || "".equals(borr_id.trim())){
            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id, borr_id,token);
        if(flag){
            String verify = loanService.verifyTokenId(per_id, token);
            if(SUCCESS_CODE.equals(verify)){
                result = loanService.cancelAskBorrow(per_id, borr_id);
            }else{
                result.setCode(TOKEN_WRONG);
                result.setInfo("您的账号已在另一台设备上登录，请重新登录");
            }
        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }

         return JSONObject.toJSONString(result);
    }

    /**
     * 根据用户id查询姓名及身份证号
     * @param per_id  用户id
     * @return
     */
    @ResponseBody
    @RequestMapping("/getIDNumber")
    public String getIDNumber(String per_id,String token,String md5sign) {
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        if(per_id == null || "".equals(per_id.trim())){
            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id,token);
        if(flag){
            String verify = loanService.verifyTokenId(per_id, token);
            if(SUCCESS_CODE.equals(verify)){
                result = loanService.getIDNumber(per_id);
            }else{
                result.setCode(TOKEN_WRONG);
                result.setInfo("您的账号已在另一台设备上登录，请重新登录");
            }
        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }


         return JSONObject.toJSONString(result);
    }

    /**
     * 查询所有省市信息
     * @return
     */
    @ResponseBody
    @RequestMapping("/getCity")
    public String getCity() {
        logger.info("***************1.0.1版本");
        NoteResult result = loanService.getCity();
        return JSONObject.toJSONString(result);
    }

    /**
     * 查询银行列表
     * @return
     */
    @ResponseBody
    @RequestMapping("/getBankList")
    public String getBankList() {
        NoteResult result = loanService.getBankList();
        return JSONObject.toJSONString(result);
    }

    /**
     * 获取当前节点的认证状态
     * @param per_id
     * @param node_id
     * @return
     */
    @ResponseBody
    @RequestMapping("/getNodeStatus")
    public String getNodeStatus(String per_id, String node_id,String token,String md5sign) {
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        if(per_id == null || "".equals(per_id.trim())
                          ||
          node_id == null || "".equals(node_id.trim())){
            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id,node_id,token);
        if(flag){
            String verify = loanService.verifyTokenId(per_id, token);
            if(SUCCESS_CODE.equals(verify)){
                result = loanService.getNodeStatus(per_id, node_id);
            }else{
                result.setCode(TOKEN_WRONG);
                result.setInfo("您的账号已在另一台设备上登录，请重新登录");
            }
        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }

         return JSONObject.toJSONString(result);
    }

    /**
     * 人脸识别页面   获取身份证正面照
     * @param per_id
     * @return
     */
    @ResponseBody
    @RequestMapping("/getCardz")
    public String getCardz(String per_id,String md5sign) {
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        if(per_id == null || "".equals(per_id.trim())){
            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id);
        if(flag){
            result = loanService.getCardz(per_id);

        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }
         return JSONObject.toJSONString(result);
    }

    /**
     * 获取用户姓名及手机号
     * @param per_id
     * @return
     */
    @ResponseBody
    @RequestMapping("/getNamePhone")
    public String getNamePhone(String per_id,String token,String md5sign) {
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        if(per_id == null || "".equals(per_id.trim())){
            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id,token);
        if(flag){
            String verify = loanService.verifyTokenId(per_id, token);
            if(SUCCESS_CODE.equals(verify)){
                result = loanService.getNamePhone(per_id);
            }else{
                result.setCode(TOKEN_WRONG);
                result.setInfo("您的账号已在另一台设备上登录，请重新登录");
            }
        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }


         return JSONObject.toJSONString(result);
    }


    /**
     * 后台管理系统代扣接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/askCollect")
    public String askCollect(HttpServletRequest request){
        StringBuffer info = new StringBuffer();
        InputStream is = null;
        String xing="";
        JSONObject jsonobject;
        String response;
        try {
            is = request.getInputStream();
            BufferedInputStream buf = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int iRed;
            while ((iRed=buf.read(buffer))!=-1) {
                info.append(new String(buffer,0,iRed,"UTF-8"))  ;
            }

             xing = URLDecoder.decode(info.toString(), "UTF-8");
            // System.out.println("zzzzzzzzz"+xing);
//            System.out.println("单笔代扣请求参数："+xing);



        //xing = "{\"phone\":\"18501773154\",\"node_status\":\"0000\",\"node_date\":\"2016-11-04T14:48:25.9202007+08:00\",\"description\":\"通过\"}";
        //System.out.println(xing);
//        int str1 = xing.indexOf("{");
//        int str2 = xing.indexOf("}")+1;
//        xing = xing.substring(str1, str2);
//        System.out.println(xing);
         jsonobject = JSONObject.parseObject(xing);
         JSONObject body = JSONObject.parseObject(jsonobject.getString("Body"));
         JSONObject head = JSONObject.parseObject(jsonobject.getString("Head"));
         String reqMd5 = head.getString("Md5");
         String guid = head.getString("Guid");
         String content = body.getString("Content");
         String myMd5 = MD5Util.encodeToMd5(content);
         if(!myMd5.equals(reqMd5)){
             head.put("RespCode", "9997");
             head.put("RespMessage", "MD5签名不正确");
             jsonobject.put("Head", head);
             jsonobject.put("Body", body);
            return JSONObject.toJSONString(jsonobject);
         }

            JSONObject obj = JSONObject.parseObject(content);
            String borrNum = obj.getString("borrNum");
            String name = obj.getString("name");
            String idCardNo = obj.getString("idCardNo");
            String optAmount = obj.getString("optAmount");
            String bankId = obj.getString("bankId");
            String bankNum = obj.getString("bankNum");
            String serNo = obj.getString("serNo");
            String phone = obj.getString("phone");
            String description = obj.getString("description");
            String createUser = "";
            String collectionUser = "";
            if (Detect.notEmpty(obj.getString("createUser"))) {
                createUser  = obj.getString("createUser");
            }
            if (Detect.notEmpty(obj.getString("collectionUser"))) {
                collectionUser = obj.getString("collectionUser");
            }

             NoteResult result = ysbCollectionService.askCollection(guid,borrNum, name, idCardNo,
                                                optAmount, bankId, bankNum, phone, description,serNo,createUser,collectionUser);

             logger.info(JSONObject.toJSONString(result));
         head.put("RespCode", result.getCode());
         head.put("RespMessage", result.getInfo());
         jsonobject.put("Head", head);

         JSONObject resContentJSON = JSONObject.parseObject(content);
         if(result.getData()!=null){
             resContentJSON.put("serNo", result.getData());
         }
         String resContent = JSONObject.toJSONString(resContentJSON);
         body.put("Content", resContent);
         jsonobject.put("Body", body);
         logger.info(JSONObject.toJSONString(jsonobject));
         response = URLEncoder.encode(JSONObject.toJSONString(jsonobject),"utf-8");
        } catch (Exception e1) {

            e1.printStackTrace();
            return "参数错误";
        }

        return response;
    }
    /**
     * .net单独查询订单接口
     */
    @ResponseBody
    @RequestMapping("/netQueryOrder")
    public String netQueryOrder(HttpServletRequest request){
        StringBuffer info = new StringBuffer();
        InputStream is = null;
        String xing="";
        JSONObject jsonobject;
        String response;
        try {
            is = request.getInputStream();
            BufferedInputStream buf = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int iRed;
            while ((iRed=buf.read(buffer))!=-1) {
                info.append(new String(buffer,0,iRed,"UTF-8"))  ;
            }

             xing = URLDecoder.decode(info.toString(), "UTF-8");
            // System.out.println("zzzzzzzzz"+xing);



        //xing = "{\"phone\":\"18501773154\",\"node_status\":\"0000\",\"node_date\":\"2016-11-04T14:48:25.9202007+08:00\",\"description\":\"通过\"}";
        //System.out.println(xing);
//        int str1 = xing.indexOf("{");
//        int str2 = xing.indexOf("}")+1;
//        xing = xing.substring(str1, str2);
//        System.out.println(xing);
         jsonobject = JSONObject.parseObject(xing);
         JSONObject body = JSONObject.parseObject(jsonobject.getString("Body"));
         JSONObject head = JSONObject.parseObject(jsonobject.getString("Head"));
         String content = body.getString("Content");
         JSONObject obj = JSONObject.parseObject(content);
         String serNo = obj.getString("serNo");
         NoteResult result = ysbCollectionService.netQueryOrder(serNo);
         head.put("RespCode", result.getCode());
         head.put("RespMessage", result.getInfo());
         jsonobject.put("Head", head);
         obj.put("status", (String)result.getData());
         obj.put("msg", result.getInfo());
         obj.put("serNo", serNo);
         body.put("Content", JSONObject.toJSONString(obj));
         jsonobject.put("Body", body);
         logger.info(JSONObject.toJSONString(jsonobject));
         response = URLEncoder.encode(JSONObject.toJSONString(jsonobject),"utf-8");
        }catch(Exception e){
            e.printStackTrace();
            return "参数错误";
        }
        return response;
    }

    /**
     * 添加黑名单接口
     */
    @ResponseBody
    @RequestMapping("/goBlackList")
    public String goBlackList(HttpServletRequest request){
        StringBuffer info = new StringBuffer();
        InputStream is = null;
        String xing="";
        JSONObject jsonobject;
        String response;
        try {
            is = request.getInputStream();
            BufferedInputStream buf = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int iRed;
            while ((iRed=buf.read(buffer))!=-1) {
                info.append(new String(buffer,0,iRed,"UTF-8"))  ;
            }

             xing = URLDecoder.decode(info.toString(), "UTF-8");
            // System.out.println("zzzzzzzzz"+xing);



        //xing = "{\"phone\":\"18501773154\",\"node_status\":\"0000\",\"node_date\":\"2016-11-04T14:48:25.9202007+08:00\",\"description\":\"通过\"}";
        //System.out.println(xing);
//        int str1 = xing.indexOf("{");
//        int str2 = xing.indexOf("}")+1;
//        xing = xing.substring(str1, str2);
//        System.out.println(xing);
         jsonobject = JSONObject.parseObject(xing);
         JSONObject body = JSONObject.parseObject(jsonobject.getString("Body"));
         JSONObject head = JSONObject.parseObject(jsonobject.getString("Head"));
         String content = body.getString("Content");
         JSONObject obj = JSONObject.parseObject(content);
         String himid_list = obj.getString("himid_list");
         String blacklist = obj.getString("blacklist");
         NoteResult result = loanService.goBlackList(himid_list, blacklist);
         head.put("RespCode", result.getCode());
         head.put("RespMessage", result.getInfo());
         jsonobject.put("Head", head);
         jsonobject.put("Body", body);
         logger.info(JSONObject.toJSONString(jsonobject));
         response = URLEncoder.encode(JSONObject.toJSONString(jsonobject),"utf-8");
        }catch(Exception e){
            e.printStackTrace();
            return "参数错误";
        }
        return response;
    }


    /**
     * 后台管理系统查询订单接口接口
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryCollectStatus")
    public void queryCollectStatus(){
        ysbCollectionService.queryCollectStatus();
    }

    /**
     * 根据系统名称获取当前最新版本号及是否强制更新
     * @param name 系统名称：ios/andriod
     * @return
     */
    @ResponseBody
    @RequestMapping("/getVersion")
    public String getVersion(String name,String version,String md5sign) {
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        if(name == null || "".equals(name.trim())){
            return JSONObject.toJSONString(result);
        }
        if(version == null){
            version = "";
        }
        boolean flag = VerifyMD5.Sign(md5sign, name,version);
        if(flag){
            result = loanService.getVersion(name,version);

        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }
         return JSONObject.toJSONString(result);
    }

    /**
     * 获取首页滚动信息
     * @param per_id
     * @return
     */
    @ResponseBody
    @RequestMapping("/getRolling")
    public String getRolling(String per_id,String token,String md5sign) {
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        if(per_id == null || "".equals(per_id.trim())){
            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id,token);
        if(flag){
            String verify = loanService.verifyTokenId(per_id, token);
            if(SUCCESS_CODE.equals(verify)){
                result = loanService.getRolling(per_id);
            }else{
                result.setCode(TOKEN_WRONG);
                result.setInfo("您的账号已在另一台设备上登录，请重新登录");
            }
        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }


         return JSONObject.toJSONString(result);
    }

    /**
     * 获取合同信息
     * @return
     */
    @ResponseBody
    @RequestMapping("/getContract")
    public String getContract(HttpServletRequest request) {
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        String per_id = request.getParameter("per_id");
        String borr_id = request.getParameter("borr_id");
        String token = request.getParameter("token");
        String md5sign = request.getParameter("md5sign");
        if(per_id == null || "".equals(per_id.trim())
                          ||
          borr_id == null || "".equals(borr_id.trim())){
            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id,borr_id,token);
        if(flag){
            String verify = loanService.verifyTokenId(per_id, token);
            if(SUCCESS_CODE.equals(verify)){
                result = loanService.getContract(per_id, borr_id);
            }else{
                result.setCode(TOKEN_WRONG);
                result.setInfo("您的账号已在另一台设备上登录，请重新登录");
            }
        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }



         return JSONObject.toJSONString(result);
    }

    /**
     * 获取聚信立协议
     * @return
     */
    @ResponseBody
    @RequestMapping("/juxinliInfo")
    public String juxinliInfo(HttpServletRequest request) {
        NoteResult result = loanService.juxinliInfo();
        return JSONObject.toJSONString(result);
    }

    /**
     * 增加第三方接口调用次数接口
     *
     * @param per_id
     *            用户id
     * @param type
     *            身份证接口1/活体检测接口2
     * @param count
     *            调用接口数
     * @param status
     *            本次认证是否成功s/f
     * @return
     */
    @ResponseBody
    @RequestMapping("/addCount")
    public String addCount(String per_id,String type,String count,String status,String md5sign){
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        if(    per_id == null || "".equals(per_id.trim())
                              ||
                 type == null || "".equals(type.trim())
                              ||
                count == null || "".equals(count.trim())
                              ||
               status == null || "".equals(status.trim())){

            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign,per_id, type, count, status);
        if(flag){
            result = loanService.addCount(per_id, type, count, status);
        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }
//        String verify = loanService.verifyTokenId(per_id, token);
//        if(SUCCESS_CODE.equals(verify)){
//            result = loanService.addCount(per_id, type, count, status);         
//        }else{
//            result.setCode(TOKEN_WRONG);
//            result.setInfo("您的账号已在另一台设备上登录，请重新登录");
//        }  

         return JSONObject.toJSONString(result);
    }

    /**点击使用优惠券
     * @param per_id
     * @return
     */
    @ResponseBody
    @RequestMapping("/useCoupon")
    public String useCoupon(String per_id,String md5sign){
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        if(    per_id == null || "".equals(per_id.trim())){

            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id);
        if(flag){
            result = loanService.useCoupon(per_id);

        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }


         return JSONObject.toJSONString(result);
    }



    /**
     * 还款页面信息接口
     * @param per_id
     * @return
     */
    @ResponseBody
    @RequestMapping("/repayInfo")
    public String repayInfo(String per_id,String token,String md5sign) {
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        if(per_id == null || "".equals(per_id.trim())){
            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id,token);
        if(flag){
            String verify = loanService.verifyTokenId(per_id, token);
            if(SUCCESS_CODE.equals(verify)){
                result = loanService.repayInfo(per_id);
            }else{
                result.setCode(TOKEN_WRONG);
                result.setInfo("您的账号已在另一台设备上登录，请重新登录");
            }

        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }

         return JSONObject.toJSONString(result);
    }


    /**
     * APP提交还款
     * @param per_id
     * @return
     */
    @ResponseBody
    @RequestMapping("/AppRepay")
    public String AppRepay(String per_id,String serial,String amount,String token,String md5sign) {
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        if (per_id == null || "".equals(per_id.trim()) || serial == null || "".equals(serial.trim()) || amount == null
                || "".equals(amount.trim()) || token == null || "".equals(token.trim()) || md5sign == null
                || "".equals(md5sign.trim())) {
            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id,serial,amount,token);
        if(flag){
            String verify = loanService.verifyTokenId(per_id, token);
            if(SUCCESS_CODE.equals(verify)){
                result = ysbpayService.AppRepay(per_id,serial,amount);
            }else{
                result.setCode(TOKEN_WRONG);
                result.setInfo("您的账号已在另一台设备上登录，请重新登录");
            }

        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }

         return JSONObject.toJSONString(result);
    }

    /**
     * 激活优惠券
     *
     * @param per_id
     * @return
     */
    @ResponseBody
    @RequestMapping("/activateCoupon")
    public String activateCoupon(String per_id,String coupon_id,String token,String md5sign) {
        NoteResult result = new NoteResult(FAIL_CODE,"参数错误");
        if (per_id == null || "".equals(per_id.trim()) || coupon_id == null || "".equals(coupon_id.trim())
                || token == null || "".equals(token.trim()) || md5sign == null || "".equals(md5sign.trim())) {
            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id,coupon_id,token);
        if(flag){
            String verify = loanService.verifyTokenId(per_id, token);
            if(SUCCESS_CODE.equals(verify)){
                result = loanService.activateCoupon(per_id, coupon_id);
            }else{
                result.setCode(TOKEN_WRONG);
                result.setInfo("您的账号已在另一台设备上登录，请重新登录");
            }

        }else{
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }

         return JSONObject.toJSONString(result);
    }

    /**
     * 身份证OCR
     *
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/cardOcr", method = RequestMethod.POST)
    public String cardOcr(MultipartHttpServletRequest request) {

        // System.out.println("进入身份证OCR");


        String per_id = request.getParameter("per_id");
        String token = request.getParameter("token");
        String md5sign = request.getParameter("md5sign");
        MultipartFile headFile = request.getFile("head");
        MultipartFile cardFile = request.getFile("card");
        // System.out.println("参数：" + per_id + token + md5sign);
        NoteResult result = new NoteResult(FAIL_CODE, "参数错误");
        if (per_id == null || "".equals(per_id.trim()) || token == null || "".equals(token.trim()) || md5sign == null
                || "".equals(md5sign.trim())) {
            return JSONObject.toJSONString(result);
        }

        boolean flag = VerifyMD5.Sign(md5sign, per_id, token);
        if (flag) {
            String verify = loanService.verifyTokenId(per_id, token);
            if (SUCCESS_CODE.equals(verify)) {
                String response = "";
                if (headFile == null || headFile.isEmpty()) {

                    response = OcrUtil.cardOcrF(cardFile);

                } else {

                    response = OcrUtil.cardOcr(cardFile);
                }
                if ("201".equals(response)) {// 第三方ocr失败
                    result.setInfo("身份证扫描失败");
                } else if ("202".equals(response)) {
                    result.setInfo("拍摄质量低，请重新拍摄");
                } else {
                    result.setCode(SUCCESS_CODE);
                    result.setInfo("成功");
                    result.setData(JSONObject.parseObject(response));

//                    else {
//                        // 2017-07-03 如果失败了 存下失败的图片 及返回报文
//
//                        try {
//                            loanService.saveTemPic(headFile.getBytes(), per_id, rawResponse);
//                            result.setCode(FAIL_CODE);
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//
//                        result.setInfo("请检查身份证是否为真实身份证");
//                    }
                }
            } else {
                result.setCode(TOKEN_WRONG);
                result.setInfo("您的账号已在另一台设备上登录，请重新登录");
            }

        } else {
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }

        // System.out.println(JSONObject.toJSONString(result));
        return JSONObject.toJSONString(result);
    }

    /**
     * faceVerify
     *
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/faceVerify", method = RequestMethod.POST)
    public String faceVerify(MultipartHttpServletRequest request) {

        // System.out.println("进入faceVerify");

        String per_id = request.getParameter("per_id");
        String token = request.getParameter("token");
        String md5sign = request.getParameter("md5sign");
        MultipartFile image_best = request.getFile("image_best");
        MultipartFile image_env = request.getFile("image_env");
        String delta = request.getParameter("delta");
        // System.out.println("image_best:" + image_best.getName());
        // System.out.println("image_env:" + image_env.getName());
        // System.out.println("参数：" + "per_id:" + per_id + ",delta:" + delta +
        // ",token:" + token + ",md5sign:" + md5sign);
        NoteResult result = new NoteResult(FAIL_CODE, "参数错误");
        if (StringUtils.isEmpty(per_id) ||StringUtils.isEmpty(token) ||StringUtils.isEmpty(md5sign)) {
            return JSONObject.toJSONString(result);
        }

        try {
            boolean flag = VerifyMD5.Sign(md5sign, per_id, token);
            if (flag) {
                String verify = loanService.verifyTokenId(per_id, token);
                if (SUCCESS_CODE.equals(verify)) {

                    // 2017-06-16改动 先存人脸识别的照片
                    byte[] bytes = image_env.getBytes();

                    NoteResult save = loanService.saveVerifyPhoto(bytes, per_id);

                    // 如果存图片没成功 直接返回失败
                    if (SUCCESS_CODE.equals(save.getCode())) {
                        logger.info("存图片返回结果：" + JSONObject.toJSONString(save));
                        NoteResult cardInfo = this.loanService.getCardz(per_id);
                        String cardName = cardInfo.getInfo().split(",")[0];
                        String cardNum = cardInfo.getInfo().split(",")[1];
                        File file = (File)cardInfo.getData();
                        String response = OcrUtil.faceVerifyRaw(file, cardName, cardNum);
                        if ("201".equals(response))
                        {
                            result.setInfo("人脸识别发生错误");
                        }
                        else if ("202".equals(response))
                        {
                            NoteResult addCount = loanService.addCount(per_id, "4", "1", "f");
                            if (CodeReturn.VERIFY_FAIL_CODE.equals(addCount.getCode())){
                                //人脸3次失败  无法继续认证
                                return JSONObject.toJSONString(addCount);
                            }
                            result.setInfo("人脸识别失败，请重新认证！");
                        } else {
                            //raw成功
                            // 人脸识别 meglive
                            String megResponse = OcrUtil.faceVerifyMeg(image_best, image_env, cardName, cardNum, delta);
                            if ("200".equals(megResponse)) {
                                // 人脸识别两次全成功 调收费接口
                                // type = 4 人脸识别 count = 2 进行了2次对比 status = s 成功
                                loanService.addCount(per_id, "4", "2", "s");
                                result.setCode(SUCCESS_CODE);
                                result.setInfo("成功");
                            } else if ("202".equals(megResponse)) {
                                // 202 失败 但进行了2次对比
                                // 调收费接口 type = 4 人脸识别 count = 2 进行了2次对比 status
                                // = f 失败
                                NoteResult addCount = loanService.addCount(per_id, "4", "2", "f");
                                if (CodeReturn.VERIFY_FAIL_CODE.equals(addCount.getCode())){
                                    //人脸3次失败  无法继续认证
                                    return JSONObject.toJSONString(addCount);
                                }
                                result.setInfo("人脸识别检测失败，请重新认证！");
                            } else if ("201".equals(megResponse)) {
                                // 201 返回error 没进行对比
                                result.setInfo("人脸识别检测发生错误");
                            }
                        }

                    } else {// 图片没存成功
                        return JSONObject.toJSONString(new NoteResult(FAIL_CODE, "系统错误"));
                    }

                } else {
                    result.setCode(TOKEN_WRONG);
                    result.setInfo("您的账号已在另一台设备上登录，请重新登录");
                }

            } else {
                result.setCode(CodeReturn.MD5_WRONG);
                result.setInfo("系统繁忙");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JSONObject.toJSONString(new NoteResult(FAIL_CODE, "系统错误"));
        }

        // System.out.println(JSONObject.toJSONString(result));
        return JSONObject.toJSONString(result);
    }

    /**
     * 首页banner接口地址
     * @return
     */
    @ResponseBody
    @RequestMapping("/banner")
    public String banner() {

        NoteResult result = loanService.banner();
        Map<String,Object> map= (Map<String, Object>) result.getData();
        List<JSONObject> bannerList = (List<JSONObject>) map.get("bannerList");
        for (JSONObject ject : bannerList){
            if ("http://app.youmishanjie.com/static/appbanner/banner1205.png".equals(ject.get("imageUrl"))){
                ject.put("directUrl","http://app.youmishanjie.com/static/register/activityStaging.html");
            }
        }
        map.put("bannerList",bannerList);
        result.setData(map);
        return JSONObject.toJSONString(result);
    }

    /**
     * 用户银行卡列表
     *
     * @param per_id
     * @return
     */
    @ResponseBody
    @RequestMapping("/personBanks")
    public String personBanks(String per_id, String md5sign) {
        NoteResult result = new NoteResult(FAIL_CODE, "参数错误");
        if (per_id == null || "".equals(per_id.trim())) {

            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id);
        if (flag) {
            result = bankService.personBanks(per_id);
        } else {
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }

        return JSONObject.toJSONString(result);
    }

    /**
     * 用户APP更换主副卡
     *
     * @param per_id
     * @param bank_num
     * @param md5sign
     * @return
     */
    @ResponseBody
    @RequestMapping("/changeBank")
    public String changeBank(String per_id, String bank_num, String md5sign) {
        NoteResult result = new NoteResult(FAIL_CODE, "参数错误");
        if (per_id == null || "".equals(per_id.trim()) || bank_num == null || "".equals(bank_num.trim())) {

            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id, bank_num);
        if (flag) {
            // 更换主副卡
            boolean change = bankService.changeBankStatus(per_id, bank_num);

            if (change) {// 更换成功
                result.setCode(SUCCESS_CODE);
                result.setInfo("更换主副卡成功");
            }

        } else {
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }

        return JSONObject.toJSONString(result);
    }

    @ResponseBody
    @RequestMapping("/canBinding")
    public String canBinding(String per_id, String md5sign) {
        NoteResult result = new NoteResult(FAIL_CODE, "参数错误");
        if (per_id == null || "".equals(per_id.trim())) {

            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id);
        if (flag) {

            result = loanService.canBinding(per_id);
        } else {
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }

        return JSONObject.toJSONString(result);
    }

    /**
     * 获取通讯录
     * @param per_id
     * @param md5sign
     * @return
     */
    @ResponseBody
    @RequestMapping("/getContacts")
    public String getContacts(String per_id, String md5sign) {
        NoteResult result = new NoteResult(FAIL_CODE, "参数错误");
        if (per_id == null || "".equals(per_id.trim())) {

            return JSONObject.toJSONString(result);
        }
        boolean flag = VerifyMD5.Sign(md5sign, per_id);
        if (flag) {

            String data = loanService.getContacts(per_id);
            result.setCode(SUCCESS_CODE);
            result.setInfo("成功");
            result.setData(data);

        } else {
            result.setCode(CodeReturn.MD5_WRONG);
            result.setInfo("系统繁忙");
        }

        return JSONObject.toJSONString(result);
    }

    /**
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/zhima")
    public String zhima(String per_id,String md5sign) {

        NoteResult result = new NoteResult();
        boolean flag = VerifyMD5.Sign(md5sign, per_id);
        if (flag) {
            /**
             * 校验通过
             */
            result = loanService.zhima(per_id);
        }else{
            result.setCode(MD5_WRONG);
            result.setInfo("md5校验不通过");
        }
        return JSONObject.toJSONString(result);
    }
    /**
     * 测试批量代扣接口
     * @return
     */
    @ResponseBody
    @RequestMapping("/testBatch")
    public String getContacts(String param) {
        NoteResult result = new NoteResult(FAIL_CODE, "参数错误");
        JSONArray array = (JSONArray) JSONArray.parse(param);
        List<BatchCollectEntity> list = new ArrayList<BatchCollectEntity>();
        for (int i = 0 ;i<array.size();i++){
            JSONObject obj = (JSONObject) array.get(i);
            BatchCollectEntity batchCollectEntity = JSONObject.toJavaObject(obj, BatchCollectEntity.class);
            list.add(batchCollectEntity);
        }

        result = ysbCollectionService.askCollectionBatch(list);

        return JSONObject.toJSONString(result);
    }

    /*
        测试机器人数据接口
     */
    @ResponseBody
    @RequestMapping("/testRobot")
    public void testRobot() {
        logger.info("测试机器人数据");
        timerService.sendRobotData();
    }

    /*
        单独查询订单状态接口
     */
    @ResponseBody
    @RequestMapping("/orderStatus")
    public String orderStatus(String serialNo) {

        return JSONObject.toJSONString(ysbCollectionService.orderStatus(serialNo));
    }

    /*
        查询所有产品信息
     */
    @ResponseBody
    @RequestMapping("/getProductInfo")
    public String getProductInfo(String per_id) {
        if (StringUtils.isEmpty(per_id)){
            return JSONObject.toJSONString(new NoteResult(FAIL_CODE, "参数为空"));
        }
        return JSONObject.toJSONString(loanService.getProductInfo(per_id));
    }

    /*
        测试贝壳钱包
     */
//    @ResponseBody
//    @RequestMapping("/testBeike")
//    public String testBeike() {
//
//        return JSONObject.toJSONString(ysbpayService.testBeike());
//    }

}
