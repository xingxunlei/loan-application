package com.loan_manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.loan_entity.common.Constants;
import com.loan_entity.manager_vo.DebitInfoVo;
import com.loan_entity.manager_vo.RepayPlanVo;
import com.loan_manage.mapper.PersonMapper;
import com.loan_manage.mapper.RepayPlanMapper;
import com.loan_manage.service.RedisService;
import com.loan_manage.service.RepaymentPlanService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RepaymentPlanServiceImpl implements RepaymentPlanService {
    private Logger logger = LoggerFactory.getLogger(RepaymentPlanServiceImpl.class);
    @Autowired
    private RepayPlanMapper repayPlanMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private PersonMapper personMapper;

    @Override
    public PageInfo<RepayPlanVo> selectRepaymentPlan(Map<String, Object> args) {
        buildQueryCondition(args);

        //1.从redis里取出查询数据数据
        List<RepayPlanVo> repayPlanVoList = repayPlanMapper.selectRepaymentPlan(args);
        PageInfo<RepayPlanVo> info = new PageInfo<RepayPlanVo>(repayPlanVoList);
        List<RepayPlanVo> voList = buildShowRepay(info.getList());
        info.setList(voList);
        return info;
    }

    @Override
    public PageInfo selectRepaymentPlan(Map<String, Object> queryMap, int offset, int size, String userNo) {
        buildQueryCondition(queryMap);
        logger.info("=================>>>>>>>>>>>>>>>>还款计划查询---起始页：{}；查询参数是:{}",offset,queryMap.toString());
        Long totalCount = null;
        try {
            if (offset == 0) {//查询有变化,查询符合条件的count//查询参数为
                if (queryMap.isEmpty()) {
                    totalCount = redisService.selectQueryTotalItem(Constants.TYPE_REPAY_PLAN,"rpl");
                } else {
                    totalCount = buildRepaymentPlanInfoCount(queryMap, userNo);
                }
            } else {//无变化,直接取得缓存中的总条数
                totalCount = buildRepaymentPlanInfoCount(queryMap, userNo);
            }
        }catch (Exception e){
            //未查询到总条数，按参数查询去查
            totalCount = repayPlanMapper.selectRepaymentPlanItem(queryMap);
        }
        queryMap.put("startItem",offset);
        queryMap.put("pageSize",size);

        List<RepayPlanVo> planList = repayPlanMapper.selectRepaymentPlan(queryMap);
        PageInfo<RepayPlanVo> vos = new PageInfo(buildShowRepay(planList));
        vos.setTotal(totalCount);

        return vos;
    }

    private Long buildRepaymentPlanInfoCount(Map<String, Object> queryMap, String userNo) throws Exception {
        Long totalCount = null;
        if(queryMap.isEmpty()) {
            totalCount = redisService.selectQueryTotalItem(Constants.TYPE_REPAY_PLAN,"rpl");
        }
//        else if(queryMap.size() == 1){//只有一个查询条件
//            if(queryMap.containsKey("productName")){//按产品类型查询
//                String proId = (String)queryMap.get("productName");
//                if(StringUtils.isNotEmpty(proId)){
//                    totalCount = redisService.selectQueryTotalItem(Constants.TYPE_REPAY_PLAN,"rpl_pro_"+proId);
//                }else{
//                    totalCount = repayPlanMapper.selectRepaymentPlanItem(queryMap);
//                    redisService.saveQueryTotalItem(Constants.TYPE_REPAY_PLAN,userNo,totalCount);
//                }
//            }else if(queryMap.containsKey("stateString")){//按借款状态查询
//                String stateString = (String)queryMap.get("stateString");
//                if(StringUtils.isNotEmpty(stateString)){
//                    totalCount = redisService.selectQueryTotalItem(Constants.TYPE_REPAY_PLAN,"rpl_state_"+stateString);
//                }else{
//                    totalCount = repayPlanMapper.selectRepaymentPlanItem(queryMap);
//                    redisService.saveQueryTotalItem(Constants.TYPE_REPAY_PLAN,userNo,totalCount);
//                }
//            }else{
//                totalCount = redisService.selectQueryTotalItem(Constants.TYPE_REPAY_PLAN,userNo);
//                if(totalCount == null || totalCount == 0){
//                    totalCount = repayPlanMapper.selectRepaymentPlanItem(queryMap);
//                    redisService.saveQueryTotalItem(Constants.TYPE_REPAY_PLAN,userNo,totalCount);
//                }
//            }
//        }
        else{
            //多个查询条件,先从redis中取得条数
            totalCount = redisService.selectQueryTotalItem(Constants.TYPE_REPAY_PLAN,userNo);
            String condition = JSON.toJSONString(queryMap);
            if(totalCount == null || totalCount == 0){
                totalCount = repayPlanMapper.selectRepaymentPlanItem(queryMap);
                redisService.saveQueryTotalItem(Constants.TYPE_REPAY_PLAN,userNo,totalCount);
            }
        }

        if(totalCount == null){
            throw new RuntimeException("总条数查询失败!");
        }
        return totalCount;
    }

    @Override
    public PageInfo<DebitInfoVo> selectDebitInfo(Map<String, Object> queryMap) {
        logger.info("selectDebitInfo查询参数是:"+queryMap.toString());
        List<DebitInfoVo> debitInfos = repayPlanMapper.selectDebitInfo(queryMap);
        PageInfo<DebitInfoVo> infos = new PageInfo<DebitInfoVo>(debitInfos);
        List<DebitInfoVo> resultList = buildDebitInfo(debitInfos);
        infos.setList(resultList);
        return infos;
    }

    private List<DebitInfoVo> buildDebitInfo(List<DebitInfoVo> debitInfos) {
        List<DebitInfoVo> vos = new ArrayList<DebitInfoVo>();
        for(DebitInfoVo debitInfo : debitInfos){
            //取出银行卡信息
            JSONObject bank = redisService.selectBankInfoFromRedis(debitInfo.getBankId());
            if(bank != null){
                debitInfo.setBankName(StringUtils.isEmpty(bank.getString("bankName")) ? "" : bank.getString("bankName"));
                debitInfo.setBankNum(StringUtils.isEmpty(bank.getString("bankNum")) ? "" : bank.getString("bankNum"));
            }
            //取出个人信息
            JSONObject person = redisService.selectPersonFromRedis(Integer.valueOf(debitInfo.getPerId()));
            if(person != null){
                debitInfo.setMobile(StringUtils.isEmpty(person.getString("phone")) ? "" : person.getString("phone"));
                debitInfo.setUsername(StringUtils.isEmpty(person.getString("name"))? "" : person.getString("name"));
                debitInfo.setIdCard(StringUtils.isEmpty(person.getString("idCard")) ? "" : person.getString("idCard"));
                debitInfo.setPhone(StringUtils.isEmpty(person.getString("phone")) ? "" : person.getString("phone"));
            }
            //TODO 取出操作人信息
            vos.add(debitInfo);
        }
        return vos;
    }

    private List<RepayPlanVo> buildShowRepay(List<RepayPlanVo> voList) {
        List<RepayPlanVo> vos = new ArrayList<RepayPlanVo>();
        for(RepayPlanVo repayPlan : voList){
            //取出产品类型
            JSONObject product = redisService.selectProductFromRedis(repayPlan.getProductId());
            if(product != null){
                repayPlan.setProductName(product.getString("prodName"));
            }
            //取出个人信息
            JSONObject person = redisService.selectPersonFromRedis(repayPlan.getCustomerId());
            if(person != null){
                repayPlan.setCustomerName(person.getString("name"));
                repayPlan.setCustomerIdValue(person.getString("idCard"));
                repayPlan.setCustomerMobile(person.getString("phone"));
            }
            vos.add(repayPlan);
        }
        return vos;
    }

    private void buildQueryCondition(Map<String, Object> queryMap) {
        if(queryMap != null){
            String customerName = (String)queryMap.get("customerName");
            String customerIdValue = (String)queryMap.get("customerIdValue");
            String customerMobile = (String)queryMap.get("customerMobile");
            Map<String,Object> map = new HashMap<String,Object>();
            if (StringUtils.isNotEmpty(customerName))map.put("name",customerName);
            if (StringUtils.isNotEmpty(customerIdValue))map.put("idCard",customerIdValue);
            if (StringUtils.isNotEmpty(customerMobile))map.put("phone",customerMobile);

            if(!map.isEmpty()){
                List<Integer> perIds = personMapper.selectPersonId(map);
                if(perIds != null){
                    String id = "";
                    for(int i=0;i<perIds.size();i++){
                        if(i == perIds.size()-1){
                            id += perIds.get(i)+"";
                        }else{
                            id += perIds.get(i)+",";
                        }
                    }
                    queryMap.put("customerId",id);
                }else{
                    queryMap.put("customerId","-1");
                }
            }
        }
    }
}
