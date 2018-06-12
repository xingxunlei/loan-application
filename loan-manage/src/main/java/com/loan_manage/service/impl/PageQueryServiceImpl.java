package com.loan_manage.service.impl;

import com.loan_entity.common.Constants;
import com.loan_manage.mapper.OrderMapper;
import com.loan_manage.mapper.RepayPlanMapper;
import com.loan_manage.mapper.RiewerMapper;
import com.loan_manage.service.PageQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import java.util.HashMap;

@Service
public class PageQueryServiceImpl implements PageQueryService {

    private Logger logger = LoggerFactory.getLogger(PageQueryServiceImpl.class);

    @Autowired
    private RepayPlanMapper repayPlanMapper;
    @Autowired
    private JedisCluster jedisCluster;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RiewerMapper riewerMapper;

    @Override
    public void initLoanManagementInfoItem() {
        String key = Constants.YM_ADMIN_SYSTEN_KEY + Constants.MANAGE_LOAN_COUNT;
        try{
            /**
             * 查询无参数
             */
            Long totalCount = repayPlanMapper.selectLoanManagementInfoItems(new HashMap<String, Object>());
            if(totalCount != null){

                logger.info("=============>>>>>>>>>>>>>>>>>贷后管理无参数的总条数查询到条数{}<<<<<<<<<<<=============",totalCount);
                jedisCluster.hset(key,"loan",String.valueOf(totalCount));
                logger.info("=============>>>>>>>>>>>>>>>>>贷后管理无参数的总条数查询到条数放入redis<<<<<<<<<<<=============");
            }

            /*String[] proIds = new String[]{"1","2","3","4"};
            for(String proId : proIds){
                Map<String,Object> proMap = new HashMap<String,Object>();
                proMap.put("productName",proId);
                Long proTotalCount = repayPlanMapper.selectLoanManagementInfoItems(proMap);
                if(proTotalCount != null){

                    logger.info("=============>>>>>>>>>>>>>>>>>贷后管理产品类型{}总条数查询到条数{}<<<<<<<<<<<=============",proId,proTotalCount);
                    jedisCluster.hset(key,"loan_pro_"+proId,String.valueOf(proTotalCount));
                    logger.info("=============>>>>>>>>>>>>>>>>>贷后管理产品类型{}总条数查询到条数放入redis<<<<<<<<<<<=============",proId);
                }
            }
            String[] states = new String[]{"BS004","BS005","BS006","BS010"};
            for(String state : states){
                Map<String,Object> stateMap = new HashMap<>();
                stateMap.put("stateString",state);
                Long stateTotalCount = repayPlanMapper.selectLoanManagementInfoItems(stateMap);
                if(stateTotalCount != null){

                    logger.info("=============>>>>>>>>>>>>>>>>>贷后管理借款状态{}总条数查询到条数{}<<<<<<<<<<<=============",state,stateTotalCount);
                    jedisCluster.hset(key,"loan_state_"+state,String.valueOf(stateTotalCount));
                    logger.info("=============>>>>>>>>>>>>>>>>>贷后管理借款状态{}总条数查询到条数放入redis<<<<<<<<<<<=============",state);
                }
            }*/
            //设置key过期时间半小时
            jedisCluster.expire(key,60*30);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void initRepaymentInfoItem() {
        String key = Constants.YM_ADMIN_SYSTEN_KEY + Constants.MANAGE_REPAY_COUNT;
        try{
            //查询无参数
            Long totalItem = orderMapper.selectOrderVoInfoItem(new HashMap<String, Object>());
            if(totalItem != null){
                logger.info("=============>>>>>>>>>>>>>>>>>还款流水无参数的总条数查询到条数{}<<<<<<<<<<<=============",totalItem);
                jedisCluster.hset(key,"repay",String.valueOf(totalItem));
                logger.info("=============>>>>>>>>>>>>>>>>>还款流水无参数的总条数查询到条数放入redis<<<<<<<<<<<=============");
            }

            /*String[] repayTypes = new String[]{"2","4","5","6","7","8"};
            for(String repayType : repayTypes){
                Map<String,Object> typeMap = new HashMap<>();
                typeMap.put("type",repayType);

                Long typeTotalItem = orderMapper.selectOrderVoInfoItem(typeMap);
                if(typeTotalItem != null){
                    logger.info("=============>>>>>>>>>>>>>>>>>还款流水类型{}的总条数查询到条数{}<<<<<<<<<<<=============",repayType,typeTotalItem);
                    jedisCluster.hset(key,"repay_type_"+repayType,String.valueOf(typeTotalItem));
                    logger.info("=============>>>>>>>>>>>>>>>>>还款流水类型{}的总条数查询到条数放入redis中field{}<<<<<<<<<<<=============",repayType,"repay_type_"+repayType);
                }
            }

            String[] repayStates = new String[]{"s","f"};
            for(String repayState : repayStates){
                Map<String,Object> stateMap = new HashMap<>();
                stateMap.put("rlState",repayState);

                Long stateTotalItem = orderMapper.selectOrderVoInfoItem(stateMap);
                if(stateTotalItem != null){
                    logger.info("=============>>>>>>>>>>>>>>>>>还款流水状态{}的总条数查询到条数{}<<<<<<<<<<<=============",repayState,stateTotalItem);
                    jedisCluster.hset(key,"repay_state_"+repayState,String.valueOf(stateTotalItem));
                    logger.info("=============>>>>>>>>>>>>>>>>>还款流水状态{}的总条数查询到条数放入redis中field{}<<<<<<<<<<<=============",repayState,"repay_state_"+repayState);
                }
            }*/
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void initRepaymentPlanInfoItem() {
        String key = Constants.YM_ADMIN_SYSTEN_KEY + Constants.MANAGE_REPAY_PLAN_COUNT;
        try{
            //查询无参数
            Long totalItem = repayPlanMapper.selectRepaymentPlanItem(new HashMap<String, Object>());
            if(totalItem != null){
                logger.info("=============>>>>>>>>>>>>>>>>>还款计划无参数的总条数查询到条数{}<<<<<<<<<<<=============",totalItem);
                jedisCluster.hset(key,"rpl",String.valueOf(totalItem));
                logger.info("=============>>>>>>>>>>>>>>>>>还款计划无参数的总条数查询到条数放入redis<<<<<<<<<<<=============");
            }

            /*String[] proIds = new String[]{"1","2","3","4"};
            for(String proId : proIds){
                Map<String,Object> proMap = new HashMap<String,Object>();
                proMap.put("productName",proId);
                Long proTotalCount = repayPlanMapper.selectRepaymentPlanItem(proMap);
                if(proTotalCount != null){

                    logger.info("=============>>>>>>>>>>>>>>>>>还款计划产品类型{}总条数查询到条数{}<<<<<<<<<<<=============",proId,proTotalCount);
                    jedisCluster.hset(key,"rpl_pro_"+proId,String.valueOf(proTotalCount));
                    logger.info("=============>>>>>>>>>>>>>>>>>还款计划产品类型{}总条数查询到条数放入redis<<<<<<<<<<<=============",proId);
                }
            }

            String[] states = new String[]{"BS004","BS005","BS006","BS010"};
            for(String state : states){
                Map<String,Object> stateMap = new HashMap<>();
                stateMap.put("state",state);
                Long stateTotalCount = repayPlanMapper.selectRepaymentPlanItem(stateMap);
                if(stateTotalCount != null){

                    logger.info("=============>>>>>>>>>>>>>>>>>还款计划借款状态{}总条数查询到条数{}<<<<<<<<<<<=============",state,stateTotalCount);
                    jedisCluster.hset(key,"rpl_state_"+state,String.valueOf(stateTotalCount));
                    logger.info("=============>>>>>>>>>>>>>>>>>还款计划借款状态{}总条数查询到条数放入redis<<<<<<<<<<<=============",state);
                }
            }*/
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void initCollectorsInfoItem() {
        String key = Constants.YM_ADMIN_SYSTEN_KEY + Constants.TYPE_COLL_INFO_COUNT;
        try{
            //查询无参数
            Long totalItem = repayPlanMapper.selectCollectorsInfoItem(new HashMap<String, Object>());
            if(totalItem != null){
                logger.info("=============>>>>>>>>>>>>>>>>>催收信息无参数的总条数查询到条数{}<<<<<<<<<<<=============",totalItem);
                jedisCluster.hset(key,"coll",String.valueOf(totalItem));
                logger.info("=============>>>>>>>>>>>>>>>>>催收信息无参数的总条数查询到条数放入redis<<<<<<<<<<<=============");
            }

            /*String[] proIds = new String[]{"1","2","3","4"};
            for(String proId : proIds){
                Map<String,Object> proMap = new HashMap<String,Object>();
                proMap.put("productName",proId);
                Long proTotalCount = repayPlanMapper.selectCollectorsInfoItem(proMap);
                if(proTotalCount != null){
                    logger.info("=============>>>>>>>>>>>>>>>>>催收信息产品类型{}总条数查询到条数{}<<<<<<<<<<<=============",proId,proTotalCount);
                    jedisCluster.hset(key,"coll_pro_"+proId,String.valueOf(proTotalCount));
                    logger.info("=============>>>>>>>>>>>>>>>>>催收信息产品类型{}总条数查询到条数放入redis<<<<<<<<<<<=============",proId);
                }
            }*/
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void initAuthManageInfoItem() {
        String key = Constants.YM_ADMIN_SYSTEN_KEY + Constants.TYPE_AUTH_INFO_COUNT;
        try{
            //查询无参数
            Long totalItem = riewerMapper.selectAuditsCount(new HashMap<String, Object>());
            if(totalItem != null){
                logger.info("=============>>>>>>>>>>>>>>>>>审核管理无参数的总条数查询到条数{}<<<<<<<<<<<=============",totalItem);
                jedisCluster.hset(key,"total_item",String.valueOf(totalItem));
                logger.info("=============>>>>>>>>>>>>>>>>>审核管理无参数的总条数查询到条数放入redis<<<<<<<<<<<=============");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
