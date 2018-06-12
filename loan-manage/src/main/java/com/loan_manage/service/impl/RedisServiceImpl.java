package com.loan_manage.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.loan_entity.app.*;
import com.loan_entity.loan.CollectorsLevelBack;
import com.loan_entity.manager.BankList;
import com.loan_entity.manager.LoginInfo;
import com.loan_manage.mapper.*;
import com.loan_manage.utils.MD5Util;
import com.loan_manage.utils.UrlReader;
import com.loan_manage.web_service.WebServiceClient;
import com.loan_utils.util.RedisConst;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.JedisCluster;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loan_entity.common.Constants;
import com.loan_entity.loan.CollectorsVo;
import com.loan_entity.manager.ProductMVo;
import com.loan_manage.service.PersonService;
import com.loan_manage.service.ProductService;
import com.loan_manage.service.RedisService;
import com.loan_manage.utils.Detect;
import tk.mybatis.mapper.entity.Condition;

@Service
public class RedisServiceImpl implements RedisService {

    private Logger logger = LoggerFactory.getLogger(RedisServiceImpl.class);
    private static final int FIVE_MINUTES=60*5;
    private WebServiceClient client = WebServiceClient.getInstance();
    @Autowired
    private ProductService productService;
    @Autowired
    private PersonService personService;
    @Autowired
    private CardMapper cardMapper;
    @Autowired
    private BankInfoMapper bankInfoMapper;
    @Autowired
    private CollectorsListMapper collectorsListMapper;
    @Autowired
    private JedisCluster jedisCluster;
    @Autowired
    CollectorsLevelBackMapper collectorsLevelBackMapper;
    @Autowired
    private BankListMapper bankListMapper;

    @Override
    public JSONObject selectProductFromRedis(Integer productId) {
        String key = Constants.YM_ADMIN_SYSTEN_KEY + Constants.PRODUCT_KEY;

        String productStr = jedisCluster.hget(key,String.valueOf(productId));
        JSONObject resultObj = null;
        if(StringUtils.isBlank(productStr)){//从redis里没有取到数据
            List<ProductMVo> products = productService.selectProductInfoToRedis();
            for(ProductMVo product : products){
                JSONObject object = new JSONObject();
                object.put("id",product.getId());
                object.put("prodName",product.getProductName().replace("元",""));
                object.put("prodTem",product.getTemName() + "期");
                object.put("amount",product.getAmount());
                jedisCluster.hset(key,String.valueOf(product.getId()),object.toJSONString());
                if(productId == Integer.valueOf(product.getId())){
                    resultObj = object;
                }
            }
        }else{
            resultObj = JSON.parseObject(productStr);
        }
        return resultObj;
    }

    @Override
    public JSONObject selectPersonFromRedis(Integer customerId) {
        String key = Constants.YM_ADMIN_SYSTEN_KEY + Constants.PERSON_KEY;
        String personStr = jedisCluster.hget(key,String.valueOf(customerId));
        JSONObject resultObj = null;
        if(StringUtils.isBlank(personStr)){
            resultObj = new JSONObject();
            //查询用户电话
            Person person = personService.selectPersonByPersonId(customerId);
            //查询用户信息
            Card queryCard = new Card();
            queryCard.setPerId(customerId);
            Card personCard = cardMapper.selectOne(queryCard);
            if(person != null){
                resultObj.put("phone",person.getPhone());
            }
            if(personCard != null){
                resultObj.put("name",personCard.getName());
                resultObj.put("idCard",personCard.getCardNum());
            }
            jedisCluster.hset(key,String.valueOf(customerId),resultObj.toJSONString());
            jedisCluster.expire(key,14*24*60*60);//14天
        }else{
            resultObj = JSON.parseObject(personStr);
        }
        return resultObj;
    }

    @Override
    public JSONObject selectBankInfoFromRedis(String bankId) {
        String key = Constants.YM_ADMIN_SYSTEN_KEY + Constants.BANK_KEY;
        String bankStr = jedisCluster.hget(key,String.valueOf(bankId));
        JSONObject resultObj = null;
        if(StringUtils.isBlank(bankStr)){
            resultObj = new JSONObject();
            Map<String,Object> queryMap = new HashMap<String,Object>();
            queryMap.put("id",bankId);
            BankVo bank = bankInfoMapper.selectBankInfos(queryMap);
            if(bank != null){
                resultObj.put("bankName",bank.getBankName());
                resultObj.put("bankNum",bank.getBankNum());
                resultObj.put("bankId",bank.getBankId());

                jedisCluster.hset(key,String.valueOf(bank.getId()),resultObj.toJSONString());
            }
        }else{
            resultObj = JSON.parseObject(bankStr);
        }
        return resultObj;
    }

    @Override @Deprecated
    public List<Bank> selectAllBankFromRedis(Integer customerId) {
        //bank info key : ym_ds_admin_view_bankInfo_customerId
        /*String key = Constants.YM_ADMIN_SYSTEN_KEY+Constants.BANK_KEY+"_"+customerId;
        List<Bank> allBanks = null;
        JSONArray bankInfos = jedisCluster.hgetAll(key);
        if(bankInfos != null && bankInfos.size()>0){
            allBanks = JSON.parseArray(bankInfos.toJSONString(),Bank.class);
        }else{
            Condition condition = new Condition(Bank.class);
            condition.createCriteria().andCondition("status in (1,2)");
            List<Bank> banks = bankInfoMapper.selectByExample(condition);
            Bank mainBank = setUserBankToRedis(key, banks);
            allBanks = banks;
        }*/
        return null;
    }

    @Override @Deprecated
    public JSONObject selectMainBankFromRedis(Integer customerId) {
        String key = Constants.YM_ADMIN_SYSTEN_KEY+Constants.BANK_KEY+"_"+customerId;
        String mainBankStr = jedisCluster.hget(key,"9999");
        JSONObject mainBank = null;
        if(StringUtils.isNotEmpty(mainBankStr)){
            mainBank = JSON.parseObject(mainBankStr);
        }else{
            Condition condition = new Condition(Bank.class);
            condition.createCriteria().andCondition("status in (1,2)");
            List<Bank> banks = bankInfoMapper.selectByExample(condition);
            Bank mainBankInfo = setUserBankToRedis(key,banks);
            mainBank = JSON.parseObject(mainBankInfo.toString());
        }
        return mainBank;
    }

    @Override
    public JSONObject selectPhoneFromRedis(String phone) {
        String key = Constants.YM_ADMIN_SYSTEN_KEY + Constants.PERSON_RD_PHONE;
        String personStr = jedisCluster.hget(key,String.valueOf(phone));
        JSONObject resultObj = null;
        if(StringUtils.isBlank(personStr)){
            resultObj = new JSONObject();
            //查询用户电话
            Person person = new Person();
            person.setPhone(phone);
            person = personService.getPersonByPhone(phone);

            //查询用户信息
            if(person != null){
                resultObj.put("perId",person.getId());
            }
            jedisCluster.hset(key,String.valueOf(phone),resultObj.toJSONString());
            jedisCluster.expire(key,14*24*60*60);//14天
        }else{
            resultObj = JSON.parseObject(personStr);
        }
        return resultObj;
    }

    @Override
    public JSONObject selectCardNumFromRedis(String cardNum) {
        String key = Constants.YM_ADMIN_SYSTEN_KEY + Constants.CARD_RD_NUM;
        String personStr = jedisCluster.hget(key,String.valueOf(cardNum));
        JSONObject resultObj = null;
        if(StringUtils.isBlank(personStr)){
            resultObj = new JSONObject();
            //查询用户电话
            Card queryCard = new Card();
            queryCard.setCardNum(cardNum);
            queryCard = cardMapper.selectOne(queryCard);
            if(queryCard != null){
                resultObj.put("perId",queryCard.getPerId());
                resultObj.put("id",queryCard.getId());
            }
            //查询用户信息
            jedisCluster.hset(key,String.valueOf(cardNum),resultObj.toJSONString());
            jedisCluster.expire(key,14*24*60*60);//14天
        }else{
            resultObj = JSON.parseObject(personStr);
        }
        return resultObj;
    }

    @Override
    public JSONObject selectBankNumFromRedis(String bankNum) {
        String key = Constants.YM_ADMIN_SYSTEN_KEY + Constants.BANK_RD_NUM;
        String personStr = jedisCluster.hget(key,String.valueOf(bankNum));
        JSONObject resultObj = null;
        if(StringUtils.isBlank(personStr)){
            resultObj = new JSONObject();
            //查询用户电话
            Bank queryBank = new Bank();
            queryBank.setBankNum(bankNum);
            queryBank = bankInfoMapper.selectOne(queryBank);
            if(queryBank != null){
                resultObj.put("perId",queryBank.getPerId());
                resultObj.put("bankId",queryBank.getId());
            }
            //查询用户信息
            jedisCluster.hset(key,String.valueOf(bankNum),resultObj.toJSONString());
            jedisCluster.expire(key,14*24*60*60);//14天
        }else{
            resultObj = JSON.parseObject(personStr);
        }
        return resultObj;
    }

    @Override
    public JSONObject selectCollertorsFromRedis(String contractID) {
        String key = Constants.YM_ADMIN_SYSTEN_KEY + Constants.COLLECTORS_KEY;
        String collertorsStr = jedisCluster.hget(key,contractID);
        JSONObject resultObj = null;
        if(StringUtils.isBlank(collertorsStr)){
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("contractId",contractID);
            CollectorsVo vo =  collectorsListMapper.selectCollectorsInfo(map);
            if(vo != null){
                String jsonStr = JSON.toJSONString(vo);
                resultObj = JSON.parseObject(jsonStr);
                jedisCluster.hset(key,contractID,jsonStr);
                jedisCluster.expire(key,14*24*60*60);//14天
            }
        }else{
            resultObj = JSON.parseObject(collertorsStr);
        }
        return resultObj;
    }

    @Override
    public void updateCollertorsFromRedis(String contractId,String userId,String username,String updateDate) {
        String key = Constants.YM_ADMIN_SYSTEN_KEY + Constants.COLLECTORS_KEY;
        String collertorsStr = jedisCluster.hget(key,contractId);
        if(StringUtils.isBlank(collertorsStr)){
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("contractId",contractId);
            CollectorsVo vo =  collectorsListMapper.selectCollectorsInfo(map);
            if(vo != null){
                jedisCluster.hset(key,contractId,JSON.toJSONString(vo));
                jedisCluster.expire(key,14*24*60*60);//14天
            }
        }else{
            JSONObject jsonObject = JSON.parseObject(collertorsStr);
            if(StringUtils.isNotEmpty(username)){
                jsonObject.put("bedueName",username);
            }
            if(StringUtils.isNotEmpty(userId)){
                jsonObject.put("bedueName",username);
            }
            if(StringUtils.isNotEmpty(updateDate)){
                jsonObject.put("updateDate",updateDate);
            }
            jedisCluster.hset(key,contractId,jsonObject.toJSONString());
            jedisCluster.expire(key,14*24*60*60);//14天
        }
    }

    @Override
    public JSONObject selectCollertorsUserName(String userId){
        if(!Detect.notEmpty(userId)){
            return null;
        }
        String key = Constants.YM_ADMIN_SYSTEN_KEY + Constants.COLLECTORS_USERID;
        String collertorsStr = jedisCluster.hget(key,userId);
        JSONObject resultObj = null;
        if(collertorsStr == null){
            resultObj = new JSONObject();

            CollectorsLevelBack cl = new CollectorsLevelBack();
            cl.setUserSysno(userId);
            cl = collectorsLevelBackMapper.selectOne(cl);
            if(cl != null){
                resultObj.put("userName",cl.getUserName());
                jedisCluster.hset(key,userId, resultObj.toJSONString());
            }

        }else{
            resultObj = JSON.parseObject(collertorsStr);
        }
        return resultObj;
    }
    public void getDataByRedis(List<Map<String, Object>> result, Map<String, String> param){
    	if(Detect.notEmpty(result) && Detect.notEmpty(result)){
    		for(Map<String, Object> map : result){
    			for(String key : param.keySet()){
    				if(key.equals(Constants.PERSON_KEY)){
    					Integer perId = (Integer) map.get(param.get(key));
    					JSONObject json = selectPersonFromRedis(perId);
        				if(Detect.notEmpty(json)){
        					map.putAll(JSONObject.toJavaObject(json, Map.class));
        				}
    				}else if(key.equals(Constants.BANK_KEY)){
    					Integer bankId = (Integer) map.get(param.get(key));
    					JSONObject json = selectBankInfoFromRedis(bankId + "");
    					if(Detect.notEmpty(json)){
    						map.putAll(JSONObject.toJavaObject(json, Map.class));
        				}
    				}else if(key.equals("createUserName")){
    					String contractId = map.get(param.get(key)) + "";
    					JSONObject json = selectCollertorsUserName(contractId);
    					if(Detect.notEmpty(json)){
    						map.put("createUser",json.get("userName"));
        				}
    				}else if(key.equals("collectionUserName")) {
                        String contractId = map.get(param.get(key)) + "";
                        JSONObject json = selectCollertorsUserName(contractId);
                        if (Detect.notEmpty(json)) {
                            map.put("collectionUser",json.get("userName"));
                        }
                    }else if(key.equals(Constants.PRODUCT_KEY)){
    					Integer productId = Integer.valueOf(map.get(param.get(key)) + "");
    					JSONObject json = selectProductFromRedis(productId);
    					if(Detect.notEmpty(json)){
    						map.putAll(JSONObject.toJavaObject(json, Map.class));
        				}
    				}
    			}

    			//转换状态
                String rlState = (String)map.get("rlState");
                String serialNo = (String)map.get("serialNo");
                if ("p".equals(rlState)){
                    map.put("rlState","处理中,"+serialNo);
                }else if ("s".equals(rlState)){
                    map.put("rlState","成功,"+serialNo);
                }else if ("f".equals(rlState)){
                    map.put("rlState","失败,"+serialNo);
                } else if("q".equals(rlState)){
                    map.put("rlState","清结算处理中,"+serialNo);
                }else if("c".equals(rlState)){
                    map.put("rlState","清结算失败,"+serialNo);
                }
    		}
    	}
    }

    public Map<String, Object> getSerchParam(Map<String, Object> param){
    	Map<String, Object> result = null ;
    	if(Detect.notEmpty(param)){
    		result = new HashMap<String, Object>();
    		for(String key : param.keySet()){
    			if(key.equals("phone")){
    				JSONObject json = selectPhoneFromRedis(param.get(key) + "");
    				this.putParamBySerch(result, json);
    			}else if(key.equals("idCard")){
    				JSONObject json = selectCardNumFromRedis(param.get(key) + "");
    				this.putParamBySerch(result, json);
    			}else if(key.equals("bankNum")){
                    JSONObject json = selectBankNumFromRedis(param.get(key) + "");
                    this.putParamBySerch(result, json);
                }
    		}
    	}
    	return result;
    }
    @Override @Deprecated
    public void updateRepaymentSum(String contractId, String amount, String repayTime) {
        //待完善
    }

    @Override @Deprecated
    public JSONObject selectRepaymentSum(String contractId) {
        return null;
    }



    private void putParamBySerch(Map<String, Object> result, JSONObject json){
    	if(Detect.notEmpty(json)){
			result.putAll(JSONObject.toJavaObject(json, Map.class));
		}else{
			result.put("borrId", -1);
		}
    }

    private Bank setUserBankToRedis(String key, List<Bank> banks) {
        Bank mainBank = null;
        for(int i=0;i<banks.size();i++){
            Bank bank = banks.get(i);
            String bankStr = JSON.toJSONString(bank);
            if("1".equals(bank.getStatus())){//主卡 field 为9999
                jedisCluster.hset(key,"9999",bankStr);
                mainBank = bank;
            }else if(i != 9){
                String field = i+""+i+""+i+""+i;
                jedisCluster.hset(key,field,bankStr);
            }else{//i=9的情况 设为5个0
                jedisCluster.hset(key,"00000",bankStr);
            }
        }
        return mainBank;
    }


    @Override
    public boolean syanLoginUser() {
        String result = client.getPersonServiceDao().selectAllUser("oa", "1q2w3e");
        if(StringUtils.isNotEmpty(result)){
            String userAuthKey = Constants.YM_ADMIN_SYSTEN_KEY + Constants.LOGIN_AUTH_KEY;
            String authUserids = StringUtils.isEmpty(UrlReader.read("auth.userids")) ? "" : UrlReader.read("auth.userids");
            List<LoginInfo> jsonObj = JSONObject.parseArray(result, LoginInfo.class);
            for(LoginInfo obj : jsonObj){
                String longId = String.valueOf(obj.getLogin_id());
                System.out.println("登录账号:"+longId);
                if(authUserids.contains(longId)){
                    String resultStr = String.valueOf(obj.getResult());
                    String[] auths = resultStr.split(",");
                    String auth = "";
                    for(int i=4;i<auths.length;i++){
                        if(i != auths.length - 1){
                            auth += auths[i] + ",";
                        }else{
                            auth += auths[i];
                        }
                    }
                    jedisCluster.hset(userAuthKey,longId,auth);
                    logger.info("同步OA信息工号：{}；权限：{}",longId,auth);
                }
            }
            logger.info("同步OA权限信息成功!");
            return true;
        }
        return false;
    }


    @Override
    public String getLoginUser(String userName, String pwd) {
        String key = Constants.YM_ADMIN_SYSTEN_KEY + Constants.LOGIN_USER_KEY;
        String userInfo = jedisCluster.hget(key,String.valueOf(userName).toLowerCase());
        if(!Detect.notEmpty(userInfo)){
            userInfo = jedisCluster.hget(key,String.valueOf(userName).toUpperCase());
        }
        if(!Detect.notEmpty(pwd)){
            //密码为空，验证帐号是否存在
            if(Detect.notEmpty(userInfo)){
                JSONObject json = JSONObject.parseObject(userInfo);
                userInfo = "{'code':0,'message':'账号校验成功','result':{'person_id':'" + json.getString("userid") + "'}}";
            }else {
                userInfo = "{'code':13,'message':'账号校验失败，账户不存在.','result':{'person_id':''}}";
            }
        }else {
            //密码存在为登录
            if (Detect.notEmpty(userInfo)) {
                JSONObject json = JSONObject.parseObject(userInfo);
                // 比对密码
                if (!json.get("password").equals(
                        MD5Util.encryptString(pwd))) {
                    userInfo = "{\"code\":10,\"message\":\"登陆失败,账号密码不对应.\",\"result \":{\"person_id\":\"\"}}";
                } else {
                    // 密码验证成功，OA权限显示字段组装
                    String returnStr = "userid_" + json.get("userid")
                            + ",username_" + json.get("username")
                            + ",idNumber_" + json.get("idNumber") + ",pwd_"
                            + pwd + "," + json.get("result");
                    json.put("result", returnStr);
                    userInfo = json.toString();
                }
            } else {
                userInfo = "{'code':13,'message':'账号校验失败，账户不存在.','result':{'person_id':''}}";
            }
        }

        return userInfo;

    }

    @Override
    public String getRedistByKey(String key){
        return jedisCluster.get(key);
    }

    @Override
    public long selectQueryTotalItem(String type,String field) {
        String key = getRegion(type);
        logger.info("redis key : {}" , key);
        String totalCountStr = jedisCluster.hget(key,field);
        logger.info("redis result : {}" , totalCountStr);
        if(!StringUtils.isEmpty(totalCountStr) && !StringUtils.endsWith(totalCountStr,"nil")){
            return Long.valueOf(totalCountStr);
        }
        return 0;
    }

//    @Override
//    public long selectQueryTotalItemWithCAndP(String type,String condition, String permission) {
//        String key = getConditionAndPermissionKey(condition, permission);
//        String region = getRegion(type);
//        String totalCountStr=jedisCluster.get(region+key);
//        if(!StringUtils.isEmpty(totalCountStr) && !StringUtils.endsWith(totalCountStr,"nil")){
//            return Long.valueOf(totalCountStr);
//        }
//        return 0;
//    }

    @Override
    public void saveQueryTotalItem(String type, String keySuffix, long totalCount) {
        String key = Constants.YM_ADMIN_SYSTEN_KEY,field = "";
        if(Constants.TYPE_LOAN.equals(type)){//贷后管理
            key += Constants.MANAGE_LOAN_COUNT;
            field = "loan_";
        }else if(Constants.TYPE_REPAY.equals(type)){//还款流水
            key += Constants.MANAGE_REPAY_COUNT;
            field = "repay_";
        }else if(Constants.TYPE_REPAY_PLAN.equals(type)){
            key += Constants.MANAGE_REPAY_PLAN_COUNT;
            field = "rpl_";
        }else if(Constants.TYPE_COLL_INFO.equals(type)){
            key += Constants.TYPE_COLL_INFO_COUNT;
            field = "coll_";
        }
        logger.info("save key is : {}" , key);
        jedisCluster.hset(key,field+keySuffix,String.valueOf(totalCount));
    }

//    @Override
//    public void saveQueryTotalItemWithCAndP(String type, String condition, String permission,long totalCount) {
//        String region = getRegion(type);
//        String key=getConditionAndPermissionKey(condition,permission);
//        jedisCluster.setex(region+key,FIVE_MINUTES,String.valueOf(totalCount));
//    }

    @Override
    public void saveBatchCollectionTags(String tagName) {
        String prefix = "bc_tag_";
        jedisCluster.set(prefix+tagName,"off");
        jedisCluster.expire(prefix+tagName,60*10);//2小时后过期
    }

    @Override
    public String selectBatchCollectionTags(String key) {
        if(StringUtils.isEmpty(key)){
            return null;
        }
        String prefix = "bc_tag_";
        try {
            return jedisCluster.get(prefix + key);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 查询条件和相关权限生成key
     * @Author Jxl
     * @Date 2017/9/7 18:53
     * @param queryCondition  查询条件
     * @param permission 相关权限
     * @return 32位key
     */
    private  String  getConditionAndPermissionKey(String queryCondition,String permission){
        return MD5Util.encryptString(queryCondition + permission);
    }

    private String getRegion(String type) {
        String key = Constants.YM_ADMIN_SYSTEN_KEY ;
        if(Constants.TYPE_LOAN.equals(type)){//贷后管理
            key += Constants.MANAGE_LOAN_COUNT;
        }else if(Constants.TYPE_REPAY.equals(type)){//还款流水
            key += Constants.MANAGE_REPAY_COUNT;
        }else if(Constants.TYPE_REPAY_PLAN.equals(type)){
            key += Constants.MANAGE_REPAY_PLAN_COUNT;
        }else if(Constants.TYPE_COLL_INFO.equals(type)){
            key += Constants.TYPE_COLL_INFO_COUNT;
        }else if(Constants.TYPE_AUTH_INFO.equals(type)){
            key += Constants.TYPE_AUTH_INFO_COUNT;
        }
        return key;
    }

    /**
     * 获取银行卡列表
     * @return
     */
    @Override
    public List<BankList> getBankList() {

        String redis = jedisCluster.get(Constants.YM_ADMIN_SYSTEN_KEY +"srt_bank_list");

        List<BankList> list;
        if (StringUtils.isEmpty(redis)) {
            // redis里没有 从数据库拿
            list = bankListMapper.selectAll();
            // redis set
            jedisCluster.set(Constants.YM_ADMIN_SYSTEN_KEY +"srt_bank_list", JSONArray.toJSONString(list));
        } else {
            // redis里有
            list = (List<BankList>) JSONArray.parse(redis);

        }
        return list;
    }

    @Override
    public void saveDownloadCount(int count) {
        String key = Constants.YM_ADMIN_SYSTEN_KEY + Constants.DOWNLOAD_COUNT;
        String countValue = jedisCluster.get(key);
        Integer downloadCount = StringUtils.isEmpty(countValue) ? 0 : Integer.valueOf(countValue);
        downloadCount = downloadCount + count;
        jedisCluster.set(key,String.valueOf(downloadCount));
    }

    @Override
    public int selectDownloadCount() {
        String key = Constants.YM_ADMIN_SYSTEN_KEY + Constants.DOWNLOAD_COUNT;
        String countValue = jedisCluster.get(key);
        return StringUtils.isEmpty(countValue) ? 0 : Integer.valueOf(countValue);
    }

    @Override
    public boolean checkLock(Integer borrId) {
        String key = Constants.YM_ADMIN_SYSTEN_KEY + Constants.TYPE_PAYBACK_LOCK + "_" + borrId;

        String checkLockKey = jedisCluster.get(key);
        if (StringUtils.isEmpty(checkLockKey)) {
            String result = jedisCluster.setex(key,60,String.valueOf(borrId));
            if ("OK".equals(result)){
                return true;
            }else{
                //set不成功说明此key存在 不允许任何还款操作
                return false;
            }
        }else{
            return false;
        }
    }

    @Override
    public boolean selectCanReduce(String contractId) {
        String key = Constants.YM_ADMIN_SYSTEN_KEY + Constants.TYPE_REDUCE_LOCK + "_" + contractId;
        Long setnx = jedisCluster.setnx(key, contractId);
        logger.info("线下还款、申请减免锁setnx="+setnx);
        if (setnx == 1){
            //设置过去时间5min
            jedisCluster.expire(key, 60);
            return true;
        }else{
            //set不成功说明此key存在
            return false;
        }
    }

}
