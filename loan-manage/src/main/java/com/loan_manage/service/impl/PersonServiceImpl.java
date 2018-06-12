package com.loan_manage.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loan_entity.app.Card;
import com.loan_entity.common.Constants;
import com.loan_entity.loan.Collectors;
import com.loan_entity.loan.CollectorsLevelBack;
import com.loan_manage.mapper.CardMapper;
import com.loan_manage.mapper.CollectorsLevelBackMapper;
import com.loan_manage.mapper.CollectorsMapper;
import com.loan_manage.task.UserTask;
import com.loan_manage.utils.Detect;
import com.loan_manage.utils.UrlReader;
import com.loan_manage.web_service.OaViewPersonWebService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loan_entity.app.Person;
import com.loan_manage.mapper.PersonMapper;
import com.loan_manage.service.PersonService;
import com.loan_manage.utils.Assertion;
import com.loan_manage.utils.QueryParamUtils;
import redis.clients.jedis.JedisCluster;

import java.util.*;

@Service("personService")
public class PersonServiceImpl implements PersonService {

    private Logger logger = LoggerFactory.getLogger(UserTask.class);
    @Autowired
    PersonMapper PersonMapper;
    @Autowired
    CollectorsMapper collectorsMapper;
    @Autowired
    CollectorsLevelBackMapper collectorsLevelBackMapper;
    @Autowired
    private JedisCluster jedisCluster;
    @Autowired
    private CardMapper cardMapper;

    @Override
    public String getPhone(Integer perId) {
        Assertion.isPositive(perId, "id不能为空");
        Person person = PersonMapper.selectByPrimaryKey(perId);
        return QueryParamUtils.getJsonData("phone", person.getPhone());
    }

    @Override
    public Person selectPersonByPersonId(Integer personId) {
        return PersonMapper.selectByPrimaryKey(personId);
    }

    @Override
    public Person getPersonByPhone(String phone) {
        return PersonMapper.getPersonByPhone(phone);
    }


    @Override
    public void syncLevelBack() {
        JaxWsProxyFactoryBean svr = new JaxWsProxyFactoryBean();
        svr.setServiceClass(OaViewPersonWebService.class);

        String urlinfo = UrlReader.read("oa.url.oper");
        svr.setAddress(urlinfo + "/OAOperationCenter/oaview");
        OaViewPersonWebService service = (OaViewPersonWebService) svr.create();
        String result = service.getPersonByPage(1, 10000);
        JSONArray list = JSONObject.parseArray(result);

        String key = Constants.YM_ADMIN_SYSTEN_KEY + Constants.COLLECTORS_USERID;

        List<BackVo> backVos = new ArrayList<>();
        Map<String, String> oaUsers = new HashMap<>();
        Iterator<Object> it = list.iterator();
        String sql = "INSERT INTO ds_collectors_level_back (user_sysno, user_name) VALUES ";
        while (it.hasNext()) {
            JSONObject jsonOa = (JSONObject) it.next();
            String employee_num = jsonOa.getString("employee_num");
            String name = jsonOa.getString("name");

            BackVo backVo = new BackVo(employee_num, name);
            backVos.add(backVo);
            JSONObject resultObj = new JSONObject();
            resultObj.put("userName", name);
            oaUsers.put(employee_num, resultObj.toJSONString());
            if(StringUtils.isNotEmpty(employee_num) && StringUtils.isNotEmpty(name)){
                sql += " ('"+employee_num+"','"+name+"'), ";
            }
        }
        System.out.println(sql);
        if (!backVos.isEmpty()) {
            jedisCluster.hmset(key, oaUsers);
        }
    }

    @Override
    public void syncOa() {

        JaxWsProxyFactoryBean svr = new JaxWsProxyFactoryBean();
        svr.setServiceClass(OaViewPersonWebService.class);

        String urlinfo = UrlReader.read("oa.url.oper");
        svr.setAddress(urlinfo + "/OAOperationCenter/oaview");
        OaViewPersonWebService service = (OaViewPersonWebService) svr.create();
        String result = service.getPersonByPage(1, 10000);
        JSONArray list = JSONObject.parseArray(result);

        String key = Constants.YM_ADMIN_SYSTEN_KEY + Constants.COLLECTORS_USERID;
        Iterator<Object> it = list.iterator();
        int success = 0, fail = 0;
        String msg = "";
        Map<String, String> oaUsers = new HashMap<>();
        JSONObject resultObj = new JSONObject();
        List<BackVo> backVos = new ArrayList<>();
        while (it.hasNext()) {
            JSONObject jsonOa = (JSONObject) it.next();
            String department_name = jsonOa.getString("department_name");
            String employee_num = jsonOa.getString("employee_num");
            String name = jsonOa.getString("name");

            BackVo backVo = new BackVo(employee_num,name);
            backVos.add(backVo);

            resultObj.put("userName", name);
            oaUsers.put(employee_num, resultObj.toJSONString());
            if (StringUtils.isNotEmpty(department_name) && ("贷后部".equals(department_name) || "风控部".equals(department_name) || "运营管理部".equals(department_name))) {
                int count = 0;
                String person_type = jsonOa.getString("person_type");
                Collectors query = new Collectors();
                query.setUserSysno(employee_num);
                query.setUserName(name);
                if(department_name.equals("贷后部")){
                    query.setUserGroupId(1);
                    query.setLevelType(1);
                    query.setIsManage(0);
                }else if(department_name.equals("风控部")){
                    query.setUserGroupId(4);
                    query.setLevelType(5);
                    query.setIsManage(0);
                }else if(department_name.equals("运营管理部")){
                    query.setUserGroupId(4);
                    query.setLevelType(2);
                    query.setIsManage(0);
                }
                Collectors collectors = collectorsMapper.selectOne(query);
                if (collectors == null) {
                    if ("在职".equals(person_type)) {
                        query.setStatus("A");
                    }
                    count = collectorsMapper.insert(query);
                } else {
                    if (!"在职".equals(person_type)) {
                        collectors.setStatus("B");
                        count = collectorsMapper.updateByPrimaryKeySelective(collectors);
                    }
                }
                if (count > 0) {
                    success++;
                } else {
                    fail++;
                    msg += employee_num + " = " + name + ",";
                }
            }
        }
        if (!oaUsers.isEmpty()) {
            jedisCluster.hmset(key, oaUsers);
            collectorsLevelBackMapper.delAll();
            collectorsLevelBackMapper.insertList(backVos);
            logger.info("OA全量用户已同步到REDIS");
        }
        logger.info("同步结束时间：" + System.currentTimeMillis());
        logger.info("成功" + success + "个OA人员,失败" + fail + "个OA人员,失败信息:" + msg);
    }

    @Override
    public String getNameByPersonId(Integer personId) {
        if (Detect.isPositive(personId)) {
            Card c = new Card();
            c.setPerId(personId);
            Card card = cardMapper.selectOne(c);
            return card != null ? card.getName() : "";
        }
        return "";
    }

    @Getter @Setter
    class BackVo{
        private String employee_num;
        private String name;

        public BackVo(String employee_num, String name) {
            this.employee_num = employee_num;
            this.name = name;
        }
        public BackVo(){}
    }
}
