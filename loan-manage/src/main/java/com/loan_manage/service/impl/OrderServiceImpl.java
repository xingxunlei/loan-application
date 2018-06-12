package com.loan_manage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.loan_entity.app.Card;
import com.loan_entity.common.Constants;
import com.loan_entity.loan.Collectors;
import com.loan_entity.loan.CollectorsLevelBack;
import com.loan_entity.manager.BankList;
import com.loan_manage.entity.OrderVo;
import com.loan_manage.mapper.*;
import com.loan_manage.service.OrderService;
import com.loan_manage.service.RedisService;
import com.loan_manage.utils.Detect;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private CardMapper cardMapper ;
    @Autowired
    private CollectorsLevelBackMapper collectorsLevelBackMapper;
    @Autowired
    private CollectorsMapper collectorsMapper;

    @Override
    public PageInfo<OrderVo> selectOrderVoInfo(Map<String, Object> queryMap, Integer start, Integer size, String userNo) {
        buildQueryCondition(queryMap,userNo);
        logger.info("=================>>>>>>>>>>>>>>>>还款流水查询---起始页：{}；查询参数是:{}",start,queryMap.toString());
        Map<String, Object> map = redisService.getSerchParam(queryMap);
        if(Detect.notEmpty(map)){
            queryMap.putAll(map);
        }

        //模糊名字查询
        if(Detect.notEmpty(queryMap.get("name") + "") ||
                Detect.notEmpty(queryMap.get("createUser") + "") ||
                Detect.notEmpty(queryMap.get("collectionUser") + "")){
            queryMap = likeEntity(queryMap);
        }
        Long totalCount = getTotalCount(queryMap, start, userNo);

        PageHelper.offsetPage(start,size,false);
        List<OrderVo> list = orderMapper.selectOrderVoInfo(queryMap);
        PageInfo<OrderVo> vos = new PageInfo<>(buildOrder(list));
        vos.setTotal(totalCount);

        return vos;
    }

    private Long getTotalCount(Map<String, Object> queryMap, Integer start, String userNo) {
        Long totalCount = null;
        try {
            if (start == 0) {
                //查询有变化,查询符合条件的count
                if (queryMap.isEmpty()) {
                    totalCount = redisService.selectQueryTotalItem(Constants.TYPE_REPAY,"repay");
                } else {
                    totalCount = buildOrderVoInfoCount(queryMap, userNo);
                }
            } else {
                //无变化,直接取得缓存中的总条数
                totalCount = buildOrderVoInfoCount(queryMap, userNo);
            }
        }catch (Exception e){
            //未查询到总条数，按参数查询去查
            totalCount = orderMapper.selectOrderVoInfoItem(queryMap);
        }
        return totalCount;
    }

    public Map<String, Object> likeEntity(Map<String, Object> map){
        if(map != null){
            if(Detect.notEmpty(map.get("name")+ "")){

                Card cd = new Card();
                cd.setName(map.get("name") + "");
                List<Card> cards = cardMapper.select(cd);
                if(Detect.notEmpty(cards)){
                    List perIds = new ArrayList();
                    for (Card card : cards){
                        perIds.add(card.getPerId());
                    }
                    map.put("perIds",perIds);
                }else{
                    map.put("perId", -1);
                }
            }
            if(Detect.notEmpty(map.get("createUser")+ "")){

                CollectorsLevelBack cb = new CollectorsLevelBack();
                cb.setUserName(map.get("createUser") + "");
                List<CollectorsLevelBack> collectorsLevelBacks = collectorsLevelBackMapper.select(cb);
                if(Detect.notEmpty(collectorsLevelBacks)){
                    List createUser = new ArrayList();
                    for (CollectorsLevelBack collectorsLevelBack : collectorsLevelBacks){
                        createUser.add(collectorsLevelBack.getUserSysno());
                    }
                    map.put("createUsers",createUser);
                }else{
                    map.put("perId", -1);
                }
            }
            if(Detect.notEmpty(map.get("collectionUser")+ "")){

                CollectorsLevelBack cb = new CollectorsLevelBack();
                cb.setUserName(map.get("collectionUser") + "");
                List<CollectorsLevelBack> collectorsLevelBacks = collectorsLevelBackMapper.select(cb);
                if(Detect.notEmpty(collectorsLevelBacks)){
                    List collectorsUser = new ArrayList();
                    for (CollectorsLevelBack collectorsLevelBack : collectorsLevelBacks){
                        collectorsUser.add(collectorsLevelBack.getUserSysno());
                    }
                    map.put("collectorsUsers",collectorsUser);
                }else{
                    map.put("perId", -1);
                }
            }
        }
        return map;
    }

    private Long buildOrderVoInfoCount(Map<String, Object> queryMap, String userNo) throws Exception {
        Long totalCount = null;
        if(queryMap.isEmpty()) {
            //查询条件为空
            totalCount = redisService.selectQueryTotalItem(Constants.TYPE_REPAY,"repay_" + userNo);
        }
//        else if(queryMap.size() == 1){
//            //只有一个查询条件
//            if(queryMap.containsKey("rlState")){
//                //还款状态
//                String rlState = (String)queryMap.get("rlState");
//                if("p".equals(rlState)){
//                    totalCount = orderMapper.selectOrderVoInfoItem(queryMap);
//                }else{
//                    String proState = (String)queryMap.get("rlState");
//                    if(StringUtils.isNotEmpty(proState)){
//                        totalCount = redisService.selectQueryTotalItem(Constants.TYPE_REPAY,"repay_state_"+proState);
//                    }else{
//                        totalCount = orderMapper.selectOrderVoInfoItem(queryMap);
//                        redisService.saveQueryTotalItem(Constants.TYPE_LOAN,userNo,totalCount);
//                    }
//                }
//            }else if(queryMap.containsKey("type")){
//                //还款类型
//                String stateString = (String)queryMap.get("type");
//                if(StringUtils.isNotEmpty(stateString)){
//                    totalCount = redisService.selectQueryTotalItem(Constants.TYPE_REPAY,"" +
//                            ""+stateString);
//                }else{
//                    totalCount = orderMapper.selectOrderVoInfoItem(queryMap);
//                    redisService.saveQueryTotalItem(Constants.TYPE_REPAY,userNo,totalCount);
//                }
//            }
//            else{
//                //多个查询条件,先从redis中取得条数
//                totalCount = redisService.selectQueryTotalItem(Constants.TYPE_REPAY,userNo);
//                if(totalCount == null || totalCount == 0){
//                    totalCount = orderMapper.selectOrderVoInfoItem(queryMap);
//                    redisService.saveQueryTotalItem(Constants.TYPE_REPAY,userNo,totalCount);
//                }
//            }
//        }
        else{
            //多个查询条件,先从redis中取得条数
            totalCount = redisService.selectQueryTotalItem(Constants.TYPE_REPAY,userNo);
            if(totalCount == null || totalCount == 0){
                totalCount = orderMapper.selectOrderVoInfoItem(queryMap);
                redisService.saveQueryTotalItem(Constants.TYPE_REPAY,userNo,totalCount);
            }
        }

        if(totalCount == null){
            throw new RuntimeException("总条数查询失败!");
        }
        return totalCount;
    }

    private List<OrderVo> buildOrder(List<OrderVo> vos) {
        List<OrderVo> list = new ArrayList<>();
        for(OrderVo vo : vos){
            String personId = vo.getPerId();
            String type = vo.getType();
            String rlState = vo.getRlState();
            String productId = vo.getProdId();
            String bankId = vo.getBankId();
            String createUserName = vo.getCreateUser();
            String collectionUserName = vo.getCollectionUser();
            String serialNo = vo.getSerialNo();

            if(vo.getBlackList() == null){
                vo.setBlackList("N");
            }else if(vo.getBlackList().equals("Y")){
                vo.setBlackList("Y");
            }else if(vo.getBlackList().equals("N")){
                vo.setBlackList("N");
            }

            //转换状态
            if("10".equals(type)){
                if ("p".equals(rlState)){
                    vo.setRlState("处理中");
                }else if ("s".equals(rlState)){
                    vo.setRlState("成功");
                }else if ("f".equals(rlState)){
                    vo.setRlState("失败");
                } else if("q".equals(rlState)){
                    vo.setRlState("清结算处理中");
                }else if("c".equals(rlState)){
                    vo.setRlState("清结算失败");
                }
            }else{
                if ("p".equals(rlState)){
                    vo.setRlState("处理中,"+serialNo);
                }else if ("s".equals(rlState)){
                    vo.setRlState("成功,"+serialNo);
                }else if ("f".equals(rlState)){
                    vo.setRlState("失败,"+serialNo);
                } else if("q".equals(rlState)){
                    vo.setRlState("清结算处理中,"+serialNo);
                }else if("c".equals(rlState)){
                    vo.setRlState("清结算失败,"+serialNo);
                }
            }


            //取得用户信息
            JSONObject person = redisService.selectPersonFromRedis(Integer.valueOf(personId));
            if(person != null){
                vo.setName(StringUtils.isEmpty(person.getString("name")) ? "" : person.getString("name"));
                vo.setIdCard(StringUtils.isEmpty(person.getString("idCard")) ? "" : person.getString("idCard"));
                vo.setPhone(StringUtils.isEmpty(person.getString("phone")) ? "" : person.getString("phone"));
            }
            //取得银行卡信息
            JSONObject bankInfo = redisService.selectBankInfoFromRedis(bankId);
            if(bankInfo != null){
                vo.setBankNum(StringUtils.isEmpty(bankInfo.getString("bankNum")) ? "" : bankInfo.getString("bankNum"));
                vo.setBankName(StringUtils.isEmpty(bankInfo.getString("bankName")) ? "" : bankInfo.getString("bankName"));
            }
            //获取银行列表
            List<BankList> banks = redisService.getBankList();
            vo.setBanks(banks);
            //取得操作人
            JSONObject createUser = redisService.selectCollertorsUserName(createUserName);
            if(createUser != null){
                vo.setCreateUser(StringUtils.isEmpty(createUser.getString("userName")) ? "" : createUser.getString("userName"));
            }
            //取得催收人
            JSONObject collertorsUser = redisService.selectCollertorsUserName(collectionUserName);
            if(collertorsUser != null){
                vo.setCollectionUser(StringUtils.isEmpty(collertorsUser.getString("userName")) ? "" : collertorsUser.getString("userName"));
            }

            list.add(vo);
        }
        return list;
    }

    private void buildQueryCondition(Map<String, Object> queryMap,String userNo) {
        if(!queryMap.containsKey("selector")){
            queryMap.put("selector","createDate");
            queryMap.put("desc","desc");
        }

        if(StringUtils.isNotEmpty(userNo)){
            Collectors c = new Collectors();
            c.setUserSysno(userNo);
            Collectors collectors = collectorsMapper.selectOne(c);
            queryMap.put("levelType",collectors == null ? "" : collectors.getLevelType());
            queryMap.put("companyId",collectors == null ? "" : collectors.getUserGroupId());
        }
    }
}
