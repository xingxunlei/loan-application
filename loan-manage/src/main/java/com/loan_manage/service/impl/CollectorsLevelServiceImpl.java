package com.loan_manage.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.loan_entity.common.Constants;
import com.loan_entity.enums.DclTypeEnum;
import com.loan_entity.loan.Collectors;
import com.loan_entity.loan.CollectorsLevel;
import com.loan_entity.loan.CollectorsLevelBack;
import com.loan_entity.manager.LoanCompany;
import com.loan_manage.entity.Result;
import com.loan_manage.mapper.*;
import com.loan_manage.service.CollectorsLevelService;
import com.loan_manage.utils.AuthUtil;
import com.loan_manage.utils.Detect;
import com.loan_manage.utils.MD5Util;
import com.loan_manage.utils.UrlReader;
import com.loan_utils.util.EmaySmsUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.JedisCluster;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class CollectorsLevelServiceImpl implements CollectorsLevelService {

    @Autowired
    private CollectorsLevelMapper collectorsLevelMapper;
    @Autowired
    private CollectorsLevelBackMapper collectorsLevelBackMapper;
    @Autowired
    private JedisCluster jedisCluster;
    @Autowired
    private LoanCompanyMapper companyMapper;
    @Autowired
    private CollectorsMapper collectorsMapper;
    @Autowired
    private RiewerMapper riewerMapper;

    @Override
    public PageInfo<CollectorsLevel> selectCollectorsLevels(Map<String, Object> queryMap, int offset, int size, String userNo) {

        CollectorsLevel queryCollectorsLevel = new CollectorsLevel();
        queryCollectorsLevel.setUserSysno(userNo);
        CollectorsLevel collectorsLevel = collectorsLevelMapper.selectOne(queryCollectorsLevel);


        Example queryExample = new Example(CollectorsLevel.class);
        Example.Criteria criteria = queryExample.createCriteria();
        //查询当前查询人所属公司,是全局管理员时,可查看所有人,贷后部可以查看除风控部、系统管理、运营管理部所有人员，否则仅能查看自己公司下的人
        if (collectorsLevel.getLevelType() != Integer.valueOf(UrlReader.read("auth.system.super"))) {
            //查询所有的公司
            List<LoanCompany> companies = companyMapper.selectAll();
            Set<Integer> companiesInSet = new HashSet();
            for (LoanCompany company : companies) {
                if ("金互行-客服部".equals(company.getName()) || "金互行-风控部".equals(company.getName()) || "金互行-系统管理".equals(company.getName()) || "金互行-运营管理部".equals(company.getName())) {
                    companiesInSet.add(company.getId());
                }
            }
            if (collectorsLevel.getLevelType() == 1) {
                criteria.andNotIn("userGroupId", companiesInSet);
            } else {
                criteria.andEqualTo("userGroupId", collectorsLevel.getUserGroupId());
            }
        }
        for (String entry : queryMap.keySet()) {
            criteria.andEqualTo(entry, queryMap.get(entry));
        }
        /*if(!queryMap.containsKey("levelType")){
            Set<Integer> inSet = new HashSet();
            inSet.add(1);
            inSet.add(2);
            criteria.andIn("levelType",inSet);
        }*/

        queryExample.setOrderByClause("status asc,create_date desc");
        PageHelper.offsetPage(offset, size);
        List<CollectorsLevel> collectorsLevels = collectorsLevelMapper.selectByExample(queryExample);
        PageInfo<CollectorsLevel> pageInfo = new PageInfo<>(collectorsLevels);

        return pageInfo;
    }

    @Override
    public String generateNewSysno() {
        int userNo = collectorsLevelMapper.selectMaxId() + 1;
        return "DS" + userNo;
    }

    @Override
    public int editCollectorsLevel(CollectorsLevel collectorsLevel) {
        int opCount = 0;

        Example companyExample = new Example(LoanCompany.class);
        companyExample.createCriteria().andEqualTo("name", "金互行-风控部");
        List<LoanCompany> companies = companyMapper.selectByExample(companyExample);

        if (collectorsLevel.getId() == null) {

            collectorsLevel.setPassword(MD5Util.encryptString("123456"));//初始化密码
            collectorsLevel.setCreateDate(new Date());
            collectorsLevel.setCreateUser("系统");
            collectorsLevel.setUpdateDate(new Date());
            opCount = collectorsLevelMapper.insert(collectorsLevel);
            if (opCount > 0) {
                CollectorsLevelBack back = new CollectorsLevelBack();
                back.setUserSysno(collectorsLevel.getUserSysno());
                back.setUserName(collectorsLevel.getUserName());
                collectorsLevelBackMapper.insert(back);
                //如果是风控部人员
                //不再关联拉黑
                /*if (companies != null && companies.size() > 0 && companies.get(0).getId() == collectorsLevel.getUserGroupId()) {
                    Riewer riewer = new Riewer();
                    riewer.setEmplloyeeName(collectorsLevel.getUserName());
                    riewer.setEmployNum(collectorsLevel.getUserSysno());
                    riewer.setStatus("A".equals(collectorsLevel.getStatus()) ? "y" : "n");
                    riewer.setCreationDate(new Date());
                    riewerMapper.insert(riewer);
                }*/
            }
        } else {
            Example example = new Example(CollectorsLevel.class);
            example.createCriteria().andEqualTo("id", collectorsLevel.getId());
            opCount = collectorsLevelMapper.updateByExampleSelective(collectorsLevel, example);
            /*if (opCount > 0) {
                //如果是风控部人员
                if (companies != null && companies.size() > 0 && companies.get(0).getId() == collectorsLevel.getUserGroupId()) {
                    Riewer riewer = new Riewer();
                    riewer.setEmplloyeeName(collectorsLevel.getUserName());
                    riewer.setEmployNum(collectorsLevel.getUserSysno());
                    riewer.setStatus("A".equals(collectorsLevel.getStatus()) ? "y" : "n");
                    riewer.setCreationDate(new Date());

                    Example updateExample = new Example(Riewer.class);
                    updateExample.createCriteria().andEqualTo("employNum", collectorsLevel.getUserSysno());

                    int count = riewerMapper.updateByExample(riewer, updateExample);
                    System.out.println("更新条数:" + count);
                }
            }*/
        }

        return opCount;
    }

    @Override
    public int editCompanyInfo(LoanCompany company) {
        int opCount = 0;
        if (company.getId() == null) {
            opCount = companyMapper.insert(company);
        } else {
            opCount = companyMapper.updateByPrimaryKey(company);
        }
        return opCount;
    }

    @Override
    public Result checkLoginName(String loginname) {

        Result result = new Result();
        try {
            CollectorsLevel queryLevel = new CollectorsLevel();
            queryLevel.setUserSysno(loginname.toUpperCase());
            List<CollectorsLevel> levelList = collectorsLevelMapper.select(queryLevel);

            if (CollectionUtils.isEmpty(levelList)) {
                result.setCode(Result.FAIL);
                result.setMessage("账号校验失败，账户不存在.");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.setCode(Result.SUCCESS);
        result.setMessage("账号校验成功");
        return result;
    }

    @Override
    public Boolean isOutWorker(String userSysNo) {
        if (StringUtils.isBlank(userSysNo)) {
            return null;
        }
        Collectors c = new Collectors();
        c.setUserSysno(userSysNo);
        Collectors collectors = collectorsMapper.selectOne(c);
        return StringUtils.equals(collectors.getLevelType() + "", DclTypeEnum.Ooutter.getCode());
    }

    @Override
    public Result modifyPassword(String userNo, String oldPwd, String newPwd, String newPwdConfirm) {

        Result result = new Result();

        CollectorsLevel queryLevel = new CollectorsLevel();
        queryLevel.setUserSysno(userNo.toUpperCase());
        List<CollectorsLevel> levelList = collectorsLevelMapper.select(queryLevel);

        if (CollectionUtils.isEmpty(levelList)) {
            result.setCode(Result.FAIL);
            result.setMessage("账户不存在.");
            return result;
        }

        CollectorsLevel collectorsLevel = levelList.get(0);

        if (!MD5Util.encryptString(oldPwd).equals(collectorsLevel.getPassword())) {
            result.setCode(Result.FAIL);
            result.setMessage("原始密码不正确");
            return result;
        }

        if (MD5Util.encryptString(newPwd).equals(collectorsLevel.getPassword())) {
            result.setCode(Result.FAIL);
            result.setMessage("新密码和原始密码不能一致");
            return result;
        }

        collectorsLevel.setPassword(MD5Util.encryptString(newPwd));

        Example example = new Example(CollectorsLevel.class);
        example.createCriteria().andEqualTo("id", collectorsLevel.getId());

        int count = collectorsLevelMapper.updateByExample(collectorsLevel, example);
        if (count > 0) {
            result.setCode(Result.SUCCESS);
            result.setMessage("修改成功");
            return result;
        } else {
            result.setCode(Result.FAIL);
            result.setMessage("修改失败");
            return result;
        }
    }

    @Override
    public Result loadUserAuthInfo(String userAuth) {
        Result result = new Result();
        JSONArray authField = AuthUtil.readAuth(userAuth);
        if (authField == null) {
            result.setCode(Result.FAIL);
            result.setMessage("系统参数读取失败,请联系管理员.");
            return result;
        }

        JSONObject outJSON = new JSONObject();
        outJSON.put("auth", authField);

        result.setCode(Result.SUCCESS);
        result.setMessage("账号校验成功");
        result.setObject(outJSON);
        return result;
    }

    @Override
    public Result loadUserRoleInfo(String category) {
        Result result = new Result();
        JSONArray selfRoles = AuthUtil.readRole(category, false);
        JSONArray allRoles = AuthUtil.readRole(category, true);
        if (selfRoles == null) {
            result.setCode(Result.FAIL);
            result.setMessage("系统参数读取失败,请联系管理员.");
            return result;
        }

        JSONObject outJSON = new JSONObject();
        outJSON.put("selfRoles", selfRoles);
        outJSON.put("allRoles", allRoles);

        result.setCode(Result.SUCCESS);
        result.setMessage("账号校验成功");
        result.setObject(outJSON);
        return result;
    }

    @Override
    public Result loadLoginUser(String userName, String password, Integer source, String loginVerifyCode) {

        Result result = new Result();

        String verifyCodeKey = Constants.YM_ADMIN_SYSTEN_KEY + Constants.VERIFY_CODE;
        String userAuthKey = Constants.YM_ADMIN_SYSTEN_KEY + Constants.LOGIN_AUTH_KEY;
        userName = userName.toUpperCase();

        CollectorsLevel queryLevel = new CollectorsLevel();
        queryLevel.setUserSysno(userName);

        if (Detect.isPositive(source) && source == 1) {//有验证码登录

            String code = jedisCluster.get(verifyCodeKey + "_" + userName);
            if (StringUtils.isEmpty(code)) {
                result.setCode(Result.FAIL);
                result.setMessage("登录验证码失效,请重新获取.");
                return result;
            }

            if (!StringUtils.equals(code, loginVerifyCode)) {
                result.setCode(Result.FAIL);
                result.setMessage("登录验证码不匹配,请重新获取.");
                return result;
            }
            jedisCluster.set(verifyCodeKey + "_" + userName, "");//清空redis
        } else {

            queryLevel.setPassword(MD5Util.encryptString(password));
        }

        List<CollectorsLevel> levelList = collectorsLevelMapper.select(queryLevel);

        if (CollectionUtils.isEmpty(levelList)) {
            result.setCode(Result.FAIL);
            result.setMessage("用户名或者密码错误");
            return result;
        }

        CollectorsLevel level = levelList.get(0);

        if (StringUtils.isEmpty(level.getStatus()) || "B".equals(level.getStatus())) {
            result.setCode(Result.FAIL);
            result.setMessage("用户状态不合法");
            return result;
        }

        //获取权限
        String authSuffix = level.getLevelType() + "." + level.getIsManage();

        JSONObject outJSON = new JSONObject();
        outJSON.put("username", level.getUserName());
        outJSON.put("userid", level.getUserSysno());
        outJSON.put("idNumber", level.getUserSysno());
        outJSON.put("auth", MD5Util.encryptStringBySign(authSuffix));
        outJSON.put("category", MD5Util.encryptStringBySign(String.valueOf(level.getLevelType())));

        result.setCode(Result.SUCCESS);
        result.setMessage("账号校验成功");
        result.setObject(outJSON);
        return result;
    }

    @Override
    public boolean isManager(String userName) {

        CollectorsLevel queryLevel = new CollectorsLevel();
        queryLevel.setUserSysno(userName);
        queryLevel.setStatus("A");

        List<CollectorsLevel> levelList = collectorsLevelMapper.select(queryLevel);

        if (levelList != null && levelList.size() > 0) {
            CollectorsLevel level = levelList.get(0);
            return level.getIsManage() >= Constants.MANAGER_LEVEL;
        }

        return false;
    }

    @Override
    public String getUserAuth(String userName) {

        CollectorsLevel queryLevel = new CollectorsLevel();
        queryLevel.setUserSysno(userName);
        queryLevel.setStatus("A");

        List<CollectorsLevel> levelList = collectorsLevelMapper.select(queryLevel);

        if (levelList != null && levelList.size() > 0) {
            CollectorsLevel level = levelList.get(0);
            return MD5Util.encryptStringBySign(level.getLevelType() + "." + level.getIsManage());
        }

        return null;
    }

    @Override
    public Result sendVerifyCode(String userName, String password) {

        String verifyCodeKey = Constants.YM_ADMIN_SYSTEN_KEY + Constants.VERIFY_CODE;

        Result result = new Result();
        CollectorsLevel queryLevel = new CollectorsLevel();
        queryLevel.setUserSysno(userName.toUpperCase());
        CollectorsLevel collectorsLevel = collectorsLevelMapper.selectOne(queryLevel);

        if (collectorsLevel == null) {
            result.setCode(Result.FAIL);
            result.setMessage("用户名错误");
            return result;
        }

        if (!MD5Util.encryptString(password).equals(collectorsLevel.getPassword())) {
            result.setCode(Result.FAIL);
            result.setMessage("密码错误");
            return result;
        }

        if (StringUtils.isEmpty(collectorsLevel.getPhone())) {
            result.setCode(Result.FAIL);
            result.setMessage("电话号码为空,请联系管理员");
            return result;
        }
        //生成6位验证码
        Random random = new Random();
        String radomInt = "";
        for (int i = 0; i < 6; i++) {
            radomInt += random.nextInt(10);
        }
        String message = "【悠米闪借】尊敬的用户，您的登录验证码是：" + radomInt;
        boolean success = EmaySmsUtil.send(message, collectorsLevel.getPhone(), 1);
        if (success) {

            jedisCluster.set(verifyCodeKey + "_" + userName.toUpperCase(), radomInt);
            jedisCluster.expire(verifyCodeKey + "_" + userName.toUpperCase(), 60 * 10);//10min过期
            result.setCode(Result.SUCCESS);
            result.setMessage("验证码发送成功!");
            return result;
        } else {
            result.setCode(Result.FAIL);
            result.setMessage("验证码发送失败,请重新获取!");
            return result;
        }
    }

    @Override
    public List<CollectorsLevel> selectCollectorsLevels() {

        List<LoanCompany> companies = companyMapper.selectAll();
        if (companies != null && companies.size() > 0) {
            Integer companyId = null;
            for (LoanCompany company : companies) {
                if ("金互行-风控部".equals(company.getName())) {
                    companyId = company.getId();
                }
            }

            if (companyId == null) {
                return null;
            }

            Example example = new Example(CollectorsLevel.class);
            example.createCriteria()
                    .andEqualTo("userGroupId", companyId)
                    .andEqualTo("status", "A");

            return collectorsLevelMapper.selectByExample(example);
        }
        return null;
    }
}
