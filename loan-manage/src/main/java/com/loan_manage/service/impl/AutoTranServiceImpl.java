package com.loan_manage.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loan_entity.loan.CollectorsList;
import com.loan_entity.manager.CollectorsRecord;
import com.loan_manage.mapper.CollectorsListMapper;
import com.loan_manage.service.AutoTranService;
import com.loan_manage.utils.AutoTranUtil;
import com.loan_utils.util.DateUtil;
import com.loan_utils.util.PropertiesReaderUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author wuhanhong
 * @date 2017 - 11 - 14
 */
@Service
public class AutoTranServiceImpl implements AutoTranService {

    private Logger logger = LoggerFactory.getLogger(AutoTranServiceImpl.class);

    @Autowired
    private CollectorsListMapper collectorsListMapper;
    @Autowired
    private DubboTranService dubboTranService;

    @Override
    public void autoTran() throws Exception {
        logger.info("================>进入自动分单");
        JSONArray tranUsers = AutoTranUtil.readAutoTran();

        if (tranUsers == null) {
            logger.error("================>配置文件读取失败");
        } else {
            logger.info("================>配置文件读取:{}",tranUsers.toJSONString());
            String today = DateUtil.getDateString(new Date());
            Map<String,Object> params = new HashMap<>();
            params.put("today",today);
            List<CollectorsList> collectorsLists = collectorsListMapper.selectSystemCollectorsLists(params);
            logger.info("================>查询到单子条数:{}", collectorsLists.size());

            if (collectorsLists != null && collectorsLists.size() > 0) {
                logger.info("================>进入开始分单");
                int tranPeopleSize = tranUsers.size();
                //切割list
                List<List<CollectorsList>>  col = averageAssign(collectorsLists,tranPeopleSize);
                List<CollectorsList> updateCollectorsList = new ArrayList<>();
                List<CollectorsRecord> collectorsRecords = new ArrayList<>();
                for (int i = 0; i < tranPeopleSize; i++) {
                    JSONObject userInfo = (JSONObject)tranUsers.get(i);
                    String username = (String)userInfo.get("username");
                    String userno = (String)userInfo.get("userno");
                    List<CollectorsList> subs = col.get(i);
                    for (CollectorsList sub : subs) {
                        sub.setBedueName(username);
                        sub.setBedueUserSysno(userno);

                        //记录转件记录
                        CollectorsRecord collectorsRecord = new CollectorsRecord();
                        collectorsRecord.setBedueUser(userno);
                        collectorsRecord.setContractId(sub.getContractId());
                        collectorsRecord.setCreateUser("AUTO");
                        collectorsRecord.setCreateTime(new Date());

                        collectorsRecords.add(collectorsRecord);
                        updateCollectorsList.add(sub);
                    }
                }
                dubboTranService.doTransferLoan(
                        updateCollectorsList,
                        null,
                        collectorsRecords,
                        null,
                        null);
            }
        }
    }

    public static <T> List<List<T>> averageAssign(List<T> source, int n) {
        List<List<T>> result = new ArrayList<List<T>>();
        int remaider = source.size() % n;
        int number = source.size() / n;
        int offset = 0;
        for (int i = 0; i < n; i++) {
            List<T> value = null;
            if (remaider > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remaider--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }
}
