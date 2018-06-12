package com.loan_server.loan_service;

import com.alibaba.fastjson.JSONObject;
import com.loan_entity.app.BorrowList;
import com.loan_entity.common.Constants;
import com.loan_entity.enums.AutoLoanRulerPropertyEnum;
import com.loan_entity.loan.AutoLoanList;
import com.loan_server.app_mapper.BorrowListMapper;
import com.loan_server.constant.LoanConstant;
import com.loan_server.loan_mapper.AutoLoanListMapper;
import com.loan_utils.radis.ScriptCaller;
import com.loan_utils.util.CodeReturn;
import com.loan_utils.util.DateUtil;
import com.loan_utils.util.MailSender;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.util.*;

import static com.loan_entity.enums.AutoLoanRulerPropertyEnum.*;

/**
 * 放款相关操作
 */
@Service
public class AutoLoanServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(AutoLoanServiceImpl.class);

    @Value("${mail.mailTitle}")
    private String mailTitle;

    @Value("${mail.host}")
    private String host;

    @Value("${mail.name}")
    private String name;

    @Value("${mail.pwd}")
    private String pwd;

    @Value("${mail.from}")
    private String from;

    @Value("${mail.to}")
    private String mailTo;
    @Value("${mail.copyTo}")
    private String copyTo;

    @Autowired
    private AutoLoanListMapper autoLoanListMapper;

    @Autowired
    private BorrowListMapper borrowListMapper;

    @Autowired
    private JedisCluster jedisCluster;

    private static String AUTO_LOAN_SCRIPT;

    static {
        try {
            AUTO_LOAN_SCRIPT = IOUtils.toString(AutoLoanServiceImpl.class.getResourceAsStream(
                    "/lua/autoLoan.lua"), "utf-8");
            logger.error("加载自动放款脚本：" + AUTO_LOAN_SCRIPT);
        } catch (IOException e) {
            logger.error("不能读取自动放款Lua脚本", e);
        }
    }

    /**
     * 查询用户是否满足自动放款条件
     *
     * @param perId 用户id
     * @return true false
     */
    public boolean getAutomaticLoan(Integer perId) {
        try {
            if (AUTO_LOAN_SCRIPT != null && Constants.SWITCH_ON.equals(jedisCluster.get(Constants.AUTO_LOAN_TOTAL_SWITCH))) {
                if (Constants.SWITCH_ON.equals(jedisCluster.getClusterNodes().get(Constants.AUTO_LOAN_LAST_ORDER_SWITCH))) {
                    if (perId != null) {
                        Map<String, String> rulerMap = new HashMap<>();
                        rulerMap.put(AutoLoanRulerPropertyEnum.last_order_switch.getProperty(), Constants.SWITCH_ON);
                        rulerMap.put(AutoLoanRulerPropertyEnum.none_loan_switch.getProperty(), jedisCluster.get(Constants.AUTO_LOAN_NONELOAN_SWITCH));
                        rulerMap.put(AutoLoanRulerPropertyEnum.normal_order_switch.getProperty(), jedisCluster.get(Constants.AUTO_LOAN_NORMALORDER_SWITCH));
                        rulerMap.put(AutoLoanRulerPropertyEnum.overdue_order_switch.getProperty(), jedisCluster.get(Constants.AUTO_LOAN_OVERDUEORDER_SWITCH));
                        rulerMap.put(AutoLoanRulerPropertyEnum.overdue_order_day.getProperty(), jedisCluster.get(Constants.AUTO_LOAN_OVERDUEORDER_DAY));
                        if (!validLastStatus(rulerMap, perId)) {
                            return false;
                        }
                    }
                }

                String borrAmount = "0";
                BorrowList lastBorr = borrowListMapper.selectNow(perId);
                if (lastBorr != null) {
                    borrAmount = lastBorr.getBorrAmount();
                }
                String currentTime = String.valueOf(System.currentTimeMillis());

                Object result = ScriptCaller.getInstance(AUTO_LOAN_SCRIPT).call(jedisCluster, Arrays.asList(Constants.HASHTAG_AUTO_LOAN), Arrays.asList(currentTime, borrAmount));
                if (result == null) {
                    logger.error("自动放款返回脚本为空：" + result + ", 放款人ID为" + perId);
                    return false;
                } else {
                    JSONObject json = JSONObject.parseObject(result.toString());
                    if (json != null) {
                        if (json.containsKey("trigger_ruler_key")) {
                            logger.error("自动放款失败：" + result + ", 借款人ID为" + perId);
                            AutoLoanList loan = autoLoanListMapper.selectByMax();
                            try {
                                loan.setLoanAmount(Double.parseDouble(jedisCluster.get(Constants.AUTO_LOAN_AMOUNT)));
                            } catch (NumberFormatException e) {
                                logger.error("获取自动放款金额失败：", e);
                            }
                            loan.setLoanAmountUpdateDate(new Date());
                            assemblyParam(loan, json.getString("trigger_ruler_key"), String.valueOf(json.get("trigger_ruler_value")));
                            return false;
                        } else {
                            if (Integer.valueOf(1).equals(json.get("result"))) {
                                logger.error("自动放款成功：" + result + ", 借款人ID为" + perId);
                                return true;
                            } else {
                                logger.error("自动放款失败：" + result + ", 借款人ID为" + perId);
                                return false;
                            }
                        }
                    } else {
                        logger.error("自动放款返回脚本解析失败：" + result + ", 放款人ID为" + perId);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("--------------自动放款出现异常，原因为-------------", e);
            jedisCluster.set(Constants.AUTO_LOAN_TOTAL_SWITCH, Constants.SWITCH_OFF);
            AutoLoanList loan = autoLoanListMapper.selectByMax();
            assemblyParam(loan, LoanConstant.ERR_EXCEPTION, LoanConstant.ERR_EXCEPTION_VALUE);
            return false;
        }
        return false;
    }

    /**
     * 判断保证金是否充足并改写当前最大放款金额
     *
     * @param code    code
     * @param massage 错误信息
     */
    public void addAutoAmount(String code, String massage) {
        //获取最新规则
        AutoLoanList loan = autoLoanListMapper.selectByMax();
        logger.info("------------------自动放款回调接受参数 massage = " + massage +
                "\n自动放款回调最新规则为 AutoLoanList =" + loan);
        if (loan != null) {
            //获取当前规则
            try {
                loan.setLoanAmount(Double.parseDouble(jedisCluster.get(Constants.AUTO_LOAN_AMOUNT)));
            } catch (NumberFormatException e) {
                logger.error("获取自动放款金额失败：", e);
            }
            loan.setLoanAmountUpdateDate(new Date());
            //总开关是否开启
            if (LoanConstant.STATUS_OPEN.equals(jedisCluster.get(Constants.AUTO_LOAN_TOTAL_SWITCH))) {
                //判断保证金是否充足
                if (LoanConstant.UNDERMARGIN_CODE.equals(code)) {
                    jedisCluster.set(Constants.AUTO_LOAN_TOTAL_SWITCH, Constants.SWITCH_OFF);
                    assemblyParam(loan, LoanConstant.UNDERMARGIN, massage);
                }
            }
        }
    }

    /**
     * 判断上单状态
     *
     * @return true false
     */
    private boolean validLastStatus(Map<String, String> map, Integer perId) {
        //判断上单状态总开关
        if (LoanConstant.STATUS_OPEN.equals(getPropertyValue(map, last_order_switch))) {
            //获取用户上一单数据
            BorrowList borr = borrowListMapper.lastOrder(perId);
            logger.info("--------当前用户上一单状态为perId=" + perId + "upperBorr=" + borr);
            boolean status = false;
            //正常结清
            if (LoanConstant.STATUS_OPEN.equals(getPropertyValue(map, normal_order_switch))) {
                if (borr != null && CodeReturn.STATUS_PAY_BACK.equals(borr.getBorrStatus())) {
                    status = true;
                }
            }
            //逾期还清
            if (LoanConstant.STATUS_OPEN.equals(getPropertyValue(map, overdue_order_switch))) {
                //是否逾期
                if (borr != null && CodeReturn.STATUS_DELAY_PAYBACK.equals(borr.getBorrStatus())) {
                    //上笔订单逾期天数
                    long overdueTime = DateUtil.getTimeDifference(borr.getActRepayDate(), borr.getPlanrepayDate());
                    String ruleTime = getPropertyValue(map, overdue_order_day);
                    //逾期时间是否满足条件
                    if (ruleTime != null && overdueTime < Long.parseLong(ruleTime) * 24 * 60) {
                        status = true;
                    }
                }
            }
            //有无借款
            if (LoanConstant.STATUS_OPEN.equals(getPropertyValue(map, none_loan_switch))) {
                List<BorrowList> borrowLists = borrowListMapper.selectBorrowPay(perId);
                if (borrowLists == null) {
                    status = true;
                }
            }
            return status;
        }

        return true;
    }

    /**
     * 发送邮件
     */
    private void sendEmail(String type, String value) {
        //发送邮件
        logger.info("发送邮件");
        String[] to = mailTo.split(",");
        String[] copyto = {copyTo};
        String mailContent = "";
        switch (type) {
            case LoanConstant.END_TIME:
                mailContent = LoanConstant.EMAIL_END_TIME;
                break;
            case LoanConstant.CONCURRENCY_LIMIT:
                mailContent = LoanConstant.EMAIL_CONCURRENCY_LIMIT;
                break;
            case LoanConstant.AMOUNT_LIMIT:
                mailContent = LoanConstant.EMAIL_AMOUNT_LIMIT;
                break;
            case LoanConstant.UNDERMARGIN:
                mailContent = LoanConstant.EMAIL_UNDERMARGIN;
                break;
            case LoanConstant.SWITCH_CODE:
                mailContent = LoanConstant.EMAIL_SWITCH_CODE;
                break;
            default:
                mailContent = LoanConstant.EMAIL_ERR_EXCEPTION;
        }
        this.sendMail(from, to, copyto, mailTitle, mailContent);
    }

    private void sendMail(String from, String[] to, String[] copyto, String mainTitle, String mailContent) {
        MailSender cn = new MailSender();
        // 设置发件人地址、收件人地址和邮件标题
        cn.setAddress(from, to, copyto, mainTitle, mailContent);
        // 设置smtp服务器以及邮箱的帐号和密码
        cn.send(host, name, pwd);
    }

    /**
     * 组装参数 发邮件
     *
     * @param loan  规则表
     * @param key   TriggerRulerKey
     * @param value TriggerRulerValue
     */
    private void assemblyParam(AutoLoanList loan, String key, String value) {
        if (loan == null) {
            return;
        }
        loan.setStatus(LoanConstant.STATUS_CLOSE);
        loan.setCreationUser(LoanConstant.AUTO_REVIEWER);
        loan.setDataType(LoanConstant.DATA_TYPE_CLOSE);
        loan.setTriggerRulerKey(key);
        loan.setTriggerRulerValue(String.valueOf(value));
        autoLoanListMapper.saveAutoLoanList(loan);
        //发送邮件
        sendEmail(key, value);
    }


    private String getPropertyValue(Map<String, String> s, AutoLoanRulerPropertyEnum property) {
        return s.get(property.getProperty());
    }

}
