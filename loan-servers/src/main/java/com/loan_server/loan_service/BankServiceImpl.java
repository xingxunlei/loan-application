package com.loan_server.loan_service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.fastjson.JSONObject;
import com.loan_api.app.LoanService;
import com.loan_api.loan.BankService;
import com.loan_entity.app.Bank;
import com.loan_entity.app.Card;
import com.loan_entity.app.NoteResult;
import com.loan_entity.app.Person;
import com.loan_entity.app.Private;
import com.loan_server.app_mapper.BankMapper;
import com.loan_server.app_mapper.BpmNodeMapper;
import com.loan_server.app_mapper.CardMapper;
import com.loan_server.app_mapper.PersonMapper;
import com.loan_server.app_mapper.PrivateMapper;
import com.loan_utils.entity.BQSEntity;
import com.loan_utils.payment.CollectUtil;
import com.loan_utils.util.BQSUtil;
import com.loan_utils.util.PropertiesReaderUtil;
import redis.clients.jedis.JedisCluster;

/**
 * 绑定银行卡功能实现类
 * @author xuepengfei
 *2016年11月7日上午9:17:08
 */

public class BankServiceImpl implements BankService {
    
    private static final Logger logger = LoggerFactory.getLogger(BankServiceImpl.class);
    
    
    @Autowired
    private CardMapper cardMapper;
    @Autowired
    private BankMapper bankMapper;
    @Autowired
    private PrivateMapper privateMapper;
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private LoanService loanService;
    @Autowired
    private BpmNodeMapper bpmNodeMapper;
    @Autowired
    private JedisCluster jedisCluster;
    
    
    private String end =PropertiesReaderUtil.read("third","endDate");
    private String riskUrl = PropertiesReaderUtil.read("third","riskUrl");
    private String isTest = PropertiesReaderUtil.read("third", "isTest");

    /**
     * 1.白骑士  2查询本地及第三方子协议 3绑卡
     */
    @Override
    public NoteResult bindingBank(String per_id,String bank_id, String bank_num, String phone,String status,String tokenKey) {
        NoteResult result = new NoteResult("201","系统繁忙");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date now = new Date();
        try{
            //根据per_id查询用户信息 : 姓名，身份号
            Card card = cardMapper.selectByPerId(Integer.valueOf(per_id));
            String card_num = card.getCardNum();
            String name = card.getName();

            if(tokenKey != null && !"".equals(tokenKey)){//tokenKey不为空，验证白骑士
            String bqsVarify = verifyBQS(per_id, name, card_num, bank_num, phone, tokenKey);
                logger.info("###################白骑士认证结果："+bqsVarify);
                if("5001".equals(bqsVarify)){//没通过
                    loanService.createBpmNode(per_id, "6", "NS003", "");                    
                    result.setCode("300");
                    result.setInfo("白骑士认证建议拒绝");                       
                    return result;
                    
                } else if ("9999".equals(bqsVarify)) {// 个人信息不全
                    result.setCode("201");
                    result.setInfo("个人信息不完全，请先进行个人认证");
                    return result;
                } else if ("5003".equals(bqsVarify)) {// 繁忙
                    result.setCode("201");
                    result.setInfo("白骑士系统繁忙");
                    return result;

                }else{
                    String bqsBinding = bindingBQS(per_id, name, card_num, bank_num, phone, tokenKey); 
                    logger.info("###################白骑士绑卡结果："+bqsBinding);
                    if("5001".equals(bqsBinding)){//没通过
                        loanService.createBpmNode(per_id, "6", "NS003", ""); 
                        result.setCode("300");
                        result.setInfo("白骑士绑卡建议拒绝");
                        return result;
                    }else if("5003".equals(bqsVarify)){//繁忙
                        result.setCode("201");
                        result.setInfo("白骑士系统繁忙");
                        return result;
                    }
                }
            }
            
            //先查询银行卡表中是否有该银行卡
            Bank exist = bankMapper.selectByBankNum(bank_num);
            if(exist != null && exist.getSubContractNum()!=null){
                //表中的银行卡是否是该per_id下的 
                if(per_id.equals(exist.getPerId().toString())){
                    //是该per_id下的银行卡，新增节点完成
                    NoteResult create2 = loanService.createBpmNode(per_id, "6", "NS002", "");
                    if("200".equals(create2.getCode())){
                        result.setCode("200");
                        result.setInfo("操作成功");
                        return result;
                    } 
                }else{
                    //不是该per_id下的银行卡，返回绑定失败
                    return new NoteResult("201","同一张银行卡不可多人绑定");                   
                }
            }
//            else{//表中没有，去第三方查               
//                NoteResult query = queryContractId(per_id, bank_num);
//                if("200".equals(query.getCode())){//本地没有子协议，但是绑定了第三方(数据库中丢失了用户的主卡记录)
//                  //获取子协议号
//                    String subContractId = (String)query.getData();
//                    if("1".equals(status)){
//                        //如果本次绑定是主卡，把用户原来主卡状态改为副卡
//                      
//                        bankMapper.updateBankStatus("2", Integer.valueOf(per_id),"1");
//                        logger.info("*************更换主卡成功");
//                    } 
//                  //数据库中插入一条银行卡数据
//                    Bank bank = new Bank();
//                    bank.setPerId(Integer.valueOf(per_id));
//                    bank.setBankId(bank_id);
//                    bank.setBankNum(bank_num);        
//                    bank.setPhone(phone);
//                    bank.setStatus(status);
//                    bank.setStartDate(now);
//                    bank.setEndDate(sdf.parse(end));
//                    bank.setResultCode("0000");
//                    bank.setSubContractNum(subContractId);
//                    bank.setCreationDate(now);
//                    bank.setUpdateDate(now);
//                    int i = bankMapper.insert(bank);
//                    if(i>0){
//                        NoteResult create2 = loanService.createBpmNode(per_id, "6", "NS002", "");
//                                          
//                        if("200".equals(create2.getCode())){
//                            result.setCode("200");
//                            result.setInfo("操作成功");
//                            return result;
//                        }                           
//                    }                                                       
//                }
//            }
            
            //如果本地没有记录，该卡未绑定过，去绑定
           
            //数据库中插入一条银行卡数据
            Bank bank = new Bank();
            bank.setPerId(Integer.valueOf(per_id));
            bank.setBankId(bank_id);
            bank.setBankNum(bank_num);        
            bank.setPhone(phone);
            bank.setStatus("0");
            
            
            
            int i = bankMapper.insertSelective(bank);
            if(i>0){//插入数据成功       
            
            //给第三方发送请求绑卡
            String response = CollectUtil.requestBind(bank_num, name, card_num, phone);          
            JSONObject res = JSONObject.parseObject(response);
            String result_code = res.getString("result_code");
            String result_msg = res.getString("result_msg");
            logger.info("第三方返回结果："+response);
            if("0000".equals(result_code)){//第三方受理成功
                //获取子协议号
                String subContractId = res.getString("subContractId");
                logger.info("**********第三方绑卡成功");
                if("1".equals(status)){
                    //如果本次绑定是主卡，把用户原来主卡状态改为副卡
                    
                    bankMapper.updateBankStatus("2", Integer.valueOf(per_id),"1");
                    logger.info("*************更换主卡成功");
                } 
                bank.setStatus(status);
                bank.setStartDate(now);              
                Date endDate = sdf.parse(end);
                bank.setEndDate(endDate);
                bank.setResultCode(result_code);
                bank.setResultMsg(result_msg);
                bank.setSubContractNum(subContractId);
                int k = bankMapper.updateByPrimaryKeySelective(bank);
                NoteResult create = loanService.createBpmNode(per_id, "6", "NS002", "");
                if(k>0 && "200".equals(create.getCode())){
                    // 数据更改 删除缓存
                    jedisCluster.del("YM_b_bank_"+per_id);

                    result.setCode("200");
                    result.setData(subContractId);
                    result.setInfo(result_msg);
                    return result;
                }                   
                                   
            }else{//第三方受理失败
                bank.setResultCode(result_code);
                bank.setResultMsg(result_msg);
                int k = bankMapper.updateByPrimaryKeySelective(bank);
                if(k>0){
                    result.setCode("201");
                    result.setData(result_code);
                    result.setInfo(result_msg);
                    return result;
                }
            }
            }
            //数据有更改  删除缓存



        }catch(Exception e){
            e.printStackTrace();
            result.setCode("201");
            result.setInfo("系统繁忙");
            return result;
        }
        return result;
    }

    @Override
    public NoteResult queryContractId(String per_id,String bank_num) {
        NoteResult result = new NoteResult("201","系统繁忙");
        try{
           Card card = cardMapper.selectByPerId(Integer.valueOf(per_id));
           String idCardNo = card.getCardNum();
           String name = card.getName();
           
           //---------------真实请求第三方查询------------------
           String response = CollectUtil.requestQuery(bank_num, name, idCardNo);
           
           JSONObject res = JSONObject.parseObject(response);
           String result_code = res.getString("result_code");
           String result_msg = res.getString("result_msg");

           //---------------------模拟----------------------------
//            if ("on".equals(isTest)) {
//                result_code = "0000";
//                result_msg = "模拟第三方";
//            }
           //-----------------------------------------------------
           
           if("0000".equals(result_code)){//查询成功
               //---------------------获取子协议号---------------
               String subContractId = res.getString("subContractId");
               //---------------------模拟---------------------
//                if ("on".equals(isTest)) {
//                    subContractId = "111111";
//                }
               //----------------------------------------------
               result.setCode("200");
               result.setData(subContractId);
               result.setInfo(result_msg);
               
           }else{
               result.setCode(result_code);
               result.setInfo(result_msg);
               
           }  
        }catch(Exception e){
            e.printStackTrace();
            result.setCode("201");
            result.setInfo("系统繁忙");
            return result;
        }
        
        return result;
    }
    public static void main(String[] args) {
        
    }

    @Override
    public String verifyBQS(String per_id, String name, String card_num, String bank_num, String bank_phone,
            String tokenKey) {
        Private p = privateMapper.selectByPerId(Integer.valueOf(per_id));
        Person person= personMapper.selectByPrimaryKey(Integer.valueOf(per_id));
        BQSEntity bqs = new BQSEntity();

        try {
            // 姓名
            bqs.setName(name);
            // 身份证号
            bqs.setIdValue(card_num);
            // 手机号
            bqs.setPhone(person.getPhone());
            // 邮箱
            bqs.setMail(p.getEmail());
            // 学历
            bqs.setEducation(p.getEducation());
            // 常住地址
            bqs.setAddress(p.getUsuallyaddress());
            // 是否已婚
            bqs.setMarriage(p.getMarry());
            // 子女数
            bqs.setLoc_childrenNum(p.getGetchild());
            // 职业
            bqs.setLoc_occupation(p.getProfession());
            // 用户工作单位名称
            bqs.setOrganization(p.getBusiness());
            // 用户工作单位地址
            bqs.setOrganizationAddress(p.getBusiAddress());
            // 单位电话
            bqs.setOrganizationPhone(p.getBusiPhone());
            // 月收入(单位千)
            bqs.setLoc_income(p.getMonthlypay());
            // 用户联系人姓名1
            bqs.setContactsName(p.getRelativesName());
            // 用户联系人手机号1
            bqs.setContactsMobile(p.getRelaPhone());
            // 用户联系人姓名2
            bqs.setContactsNameSec(p.getSocietyName());
            // 用户联系人手机号2
            bqs.setContactsMobileSec(p.getSociPhone());
            // 银行卡号
            bqs.setBankCardNo(bank_num);
            // 预留手机号
            bqs.setBankCardMobile(bank_phone);
            // 设备指纹会话标识
            bqs.setTokenKey(tokenKey);
            // 事件类型
            bqs.setEventType("verify");
        } catch (Exception e) {

            e.printStackTrace();
            return "9999";// 个人信息不全 返回9999
        }
             
        String respCode = BQSUtil.send(riskUrl, bqs);
        return respCode;
    }

    @Override
    public String bindingBQS(String per_id, String name, String card_num, String bank_num, String bank_phone,
            String tokenKey) {
        Person person= personMapper.selectByPrimaryKey(Integer.valueOf(per_id));
        BQSEntity bqs = new BQSEntity();
        //      姓名        
        bqs.setName(name);
        //        身份证号
        bqs.setIdValue(card_num);
        //        手机号
        bqs.setPhone(person.getPhone());
        //      银行卡号
        bqs.setBankCardNo(bank_num);
        //        预留手机号
        bqs.setBankCardMobile(bank_phone);
        //      设备指纹会话标识
        bqs.setTokenKey(tokenKey);
        //        事件类型
        bqs.setEventType("binding");
              
        String respCode = BQSUtil.send(riskUrl, bqs);
        return respCode;
    }

    @Override
    public NoteResult personBanks(String per_id) {
        NoteResult result = new NoteResult("201", "系统繁忙");
        try {
            List<Bank> banks = bankMapper.selectAllBanks(per_id);
            result.setCode("200");
            result.setInfo("成功");
            result.setData(banks);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode("201");
            result.setInfo("系统繁忙");
        }

        return result;
    }

    @Override
    public boolean changeBankStatus(String per_id, String bank_num) {

        try {
            Bank bank = bankMapper.selectByBankNum(bank_num);
            String oldStatus = bank.getStatus();
            if ("1".equals(oldStatus)) {
                // 如果此银行卡已经是主卡 不允许更改状态
                return false;
            }else{
                // 此银行卡不是主卡， 把原来的主卡 改为副卡
                bankMapper.updateBankStatus("2", Integer.valueOf(per_id), "1");
                // 把此卡转成主卡
                bank.setStatus("1");
                bankMapper.updateByPrimaryKeySelective(bank);

                logger.info("*************用户主动更换主卡成功");

                // 数据更改 删除缓存
                jedisCluster.del("YM_b_bank_"+per_id);
                return true;

            }





        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
   
       
   
}
