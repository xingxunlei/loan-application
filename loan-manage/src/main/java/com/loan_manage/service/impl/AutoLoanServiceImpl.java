package com.loan_manage.service.impl;

import com.loan_entity.common.Constants;
import com.loan_entity.enums.AutoLoanRulerPropertyEnum;
import com.loan_entity.loan.AutoLoanList;
import com.loan_entity.loan.AutoLoanRuler;
import com.loan_manage.mapper.AutoLoanListMapper;
import com.loan_manage.mapper.AutoLoanRulerMapper;
import com.loan_manage.service.AutoLoanService;
import com.loan_utils.radis.ScriptCaller;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import javax.mail.internet.ParseException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @see AutoLoanService
 * Created by chenchao on 2017/11/1.
 */
@Service("autoLoanService")
public class AutoLoanServiceImpl implements AutoLoanService {

    private final static Logger logger = LoggerFactory.getLogger(AutoLoanServiceImpl.class);

    @Autowired
    private AutoLoanListMapper autoLoanListMapper;

    @Autowired
    private JedisCluster jedisCluster;

    private static String AUTO_LOAN_SCRIPT;

    static {
        try {
            AUTO_LOAN_SCRIPT = IOUtils.toString(AutoLoanServiceImpl.class.getResourceAsStream(
                    "/lua/switchAutoLoan.lua"), "utf-8");
        } catch (IOException e) {
            logger.error("不能读取自动放款Lua脚本", e);
        }
    }

    @Override
    public AutoLoanList getCurrentStatus() {
        AutoLoanList loan = autoLoanListMapper.selectByMax();
        if (loan != null) {
            String status = jedisCluster.get(Constants.AUTO_LOAN_TOTAL_SWITCH);
            if (status != null) {
                loan.setStatus(status);
            }

            String amount = jedisCluster.get(Constants.AUTO_LOAN_AMOUNT);
            if (amount != null) {
                loan.setLoanAmount(Double.parseDouble(amount));
            }
        }
        return loan;
    }

    @Override
    public List<AutoLoanRuler> getAutoLoanRulers() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<AutoLoanRuler> rulers = new ArrayList<>();
        if (rulers != null) {
            for (int i = 0; i < AutoLoanRulerPropertyEnum.values().length; i++) {
                AutoLoanRulerPropertyEnum rulerProperty = AutoLoanRulerPropertyEnum.values()[i];
                AutoLoanRuler ruler = new AutoLoanRuler();
                ruler.setProperty(rulerProperty.getProperty());
                rulers.add(ruler);
                switch (rulerProperty) {
                    case amount_switch:
                        ruler.setValue(jedisCluster.get(Constants.AUTO_LOAN_AMOUNT_SWITCH));
                        break;
                    case time_switch:
                        ruler.setValue(jedisCluster.get(Constants.AUTO_LOAN_TIME_SWITCH));
                        break;
                    case concurrency_switch:
                        ruler.setValue(jedisCluster.get(Constants.AUTO_LOAN_CONCURRENCY_SWITCH));
                        break;
                    case last_order_switch:
                        ruler.setValue(jedisCluster.get(Constants.AUTO_LOAN_LAST_ORDER_SWITCH));
                        break;
                    case start_time:
                        ruler.setValue(jedisCluster.get(Constants.AUTO_LOAN_STARTTIME));
                        try {
                            if (ruler.getValue() != null) {
                                ruler.setValue(sdf.format(new Date(Long.parseLong(ruler.getValue()))));
                            }
                        } catch (NumberFormatException e) {
                            logger.error("解析自动放款开始时间失败：" + ruler.getValue(), e);
                        }
                        break;
                    case end_time:
                        ruler.setValue(jedisCluster.get(Constants.AUTO_LOAN_ENDTIME));
                        try {
                            if (ruler.getValue() != null) {
                                ruler.setValue(sdf.format(new Date(Long.parseLong(ruler.getValue()))));
                            }
                        } catch (NumberFormatException e) {
                            logger.error("解析自动放款结束时间失败：" + ruler.getValue(), e);
                        }
                        break;
                    case concurrency_limit:
                        ruler.setValue(jedisCluster.get(Constants.AUTO_LOAN_CONCURRENCY_LIMIT));
                        break;
                    case amount_limit:
                        ruler.setValue(jedisCluster.get(Constants.AUTO_LOAN_AMOUNT_LIMIT));
                        break;
                    case normal_order_switch:
                        ruler.setValue(jedisCluster.get(Constants.AUTO_LOAN_NORMALORDER_SWITCH));
                        break;
                    case overdue_order_switch:
                        ruler.setValue(jedisCluster.get(Constants.AUTO_LOAN_OVERDUEORDER_SWITCH));
                        break;
                    case none_loan_switch:
                        ruler.setValue(jedisCluster.get(Constants.AUTO_LOAN_NONELOAN_SWITCH));
                        break;
                    case overdue_order_day:
                        ruler.setValue(jedisCluster.get(Constants.AUTO_LOAN_OVERDUEORDER_DAY));
                        break;
                    default:
                        break;
                }
            }
        }
        return rulers;
    }

    @Override
    public int insertAutoLoanStatus(AutoLoanList autoLoanStatus) {
        if (AUTO_LOAN_SCRIPT != null) {
            ScriptCaller.getInstance(AUTO_LOAN_SCRIPT).call(jedisCluster, Arrays.asList(Constants.HASHTAG_AUTO_LOAN), Arrays.asList(autoLoanStatus.getStatus()));
            return autoLoanListMapper.insertSelective(autoLoanStatus);
        }else{
            return 0;
        }
    }

    @Override
    public void updateAutoLoanRulers(List<AutoLoanRuler> rulers) {
        if (rulers != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            for (int i = 0; i < rulers.size(); i++) {
                AutoLoanRuler ruler = rulers.get(i);
                String property = ruler.getProperty();
                if (!AutoLoanRulerPropertyEnum.contains(property)) {
                    continue;
                }
                switch (AutoLoanRulerPropertyEnum.valueOf(property)) {
                    case amount_switch:
                        jedisCluster.set(Constants.AUTO_LOAN_AMOUNT_SWITCH, ruler.getValue());
                        break;
                    case time_switch:
                        jedisCluster.set(Constants.AUTO_LOAN_TIME_SWITCH, ruler.getValue());
                        break;
                    case concurrency_switch:
                        jedisCluster.set(Constants.AUTO_LOAN_CONCURRENCY_SWITCH, ruler.getValue());
                        break;
                    case last_order_switch:
                        jedisCluster.set(Constants.AUTO_LOAN_LAST_ORDER_SWITCH, ruler.getValue());
                        break;
                    case start_time:
                        try {
                            if (ruler.getValue() != null && ruler.getValue().trim().length() > 0) {
                                jedisCluster.set(Constants.AUTO_LOAN_STARTTIME, String.valueOf(sdf.parse(ruler.getValue()).getTime()));
                            }
                        } catch (java.text.ParseException e) {
                            logger.error("解析自动放款开始时间失败：" + ruler.getValue(), e);
                        }
                        break;
                    case end_time:
                        try {
                            if (ruler.getValue() != null && ruler.getValue().trim().length() > 0) {
                                jedisCluster.set(Constants.AUTO_LOAN_ENDTIME, String.valueOf(sdf.parse(ruler.getValue()).getTime()));
                            }
                        } catch (java.text.ParseException e) {
                            logger.error("解析自动放款结束时间失败：" + ruler.getValue(), e);
                        }
                        break;
                    case concurrency_limit:
                        jedisCluster.set(Constants.AUTO_LOAN_CONCURRENCY_LIMIT, ruler.getValue());
                        break;
                    case amount_limit:
                        jedisCluster.set(Constants.AUTO_LOAN_AMOUNT_LIMIT, ruler.getValue());
                        break;
                    case normal_order_switch:
                        jedisCluster.set(Constants.AUTO_LOAN_NORMALORDER_SWITCH, ruler.getValue());
                        break;
                    case overdue_order_switch:
                        jedisCluster.set(Constants.AUTO_LOAN_OVERDUEORDER_SWITCH, ruler.getValue());
                        break;
                    case none_loan_switch:
                        jedisCluster.set(Constants.AUTO_LOAN_NONELOAN_SWITCH, ruler.getValue());
                        break;
                    case overdue_order_day:
                        jedisCluster.set(Constants.AUTO_LOAN_OVERDUEORDER_DAY, ruler.getValue());
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
