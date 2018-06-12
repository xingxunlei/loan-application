package com.loan_manage.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loan_entity.common.Constants;
import com.loan_entity.enums.AutoLoanRulerPropertyEnum;
import com.loan_entity.loan.AutoLoanList;
import com.loan_entity.loan.AutoLoanRuler;
import com.loan_manage.entity.Result;
import com.loan_manage.service.AutoLoanService;
import com.loan_manage.service.CollectorsLevelService;
import com.loan_manage.utils.AuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.loan_entity.enums.AutoLoanRulerPropertyEnum.*;

/**
 * 自动放款Controller
 */
@Controller
@RequestMapping(value = "autoLoan")
public class AutoLoanController {
    private Logger logger = LoggerFactory.getLogger(AutoLoanController.class);

    @Autowired
    private AutoLoanService autoLoanService;

    @Autowired
    private CollectorsLevelService collectorsLevelService;

    /**
     * 查询当前自动放款状态
     *
     * @return
     */
    @RequestMapping(value = "autoLoanStatus", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Result getAutoLoanStatus(HttpServletRequest request) {
        Result result = new Result();
        try {
            String userNo = request.getParameter("userNo");
            String auth = request.getParameter("auth");
            //检测用户权限是否和数据库存储的权限一致
            if (auth == null || !auth.equals(collectorsLevelService.getUserAuth(userNo))) {
                result.setCode(Result.FAIL);
                result.setMessage("权限错误！");
            }
            // 检测用户是否有访问自动放款的权限
            else if (AuthUtil.isPageEnabled(auth, "ym-gd")) {
                //获取自动放款状态
                AutoLoanList autoLoan = autoLoanService.getCurrentStatus();
                result.setCode(Result.SUCCESS);
                result.setMessage("自动放款状态加载成功");
                result.setObject(JSONObject.parse(JSON.toJSONString(autoLoan)));
            } else {
                result.setCode(Result.FAIL);
                result.setMessage("权限不足！");
            }
        } catch (Exception e) {
            logger.error("加载自动放款状态失败", e);
            result.setCode(Result.FAIL);
            result.setMessage("自动放款状态加载失败");
        }
        return result;
    }

    /**
     * 查询当前自动放款规则
     *
     * @return
     */
    @RequestMapping(value = "autoLoanRulers", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Result getAutoLoanRulers(HttpServletRequest request) {
        Result result = new Result();
        try {
            String userNo = request.getParameter("userNo");
            String auth = request.getParameter("auth");
            //检测用户权限是否和数据库存储的权限一致
            if (auth == null || !auth.equals(collectorsLevelService.getUserAuth(userNo))) {
                result.setCode(Result.FAIL);
                result.setMessage("权限错误！");
            }
            // 检测用户是否有访问自动放款的权限
            else if (AuthUtil.isPageEnabled(auth, "ym-gd")) {
                //获取自动放款规则列表
                List<AutoLoanRuler> autoLoanRulers = autoLoanService.getAutoLoanRulers();
                result.setCode(Result.SUCCESS);
                result.setMessage("自动放款规则加载成功");
                result.setObject(autoLoanRulers);
            } else {
                result.setCode(Result.FAIL);
                result.setMessage("权限不足！");
            }
        } catch (Exception e) {
            logger.error("加载自动放款规则失败", e);
            result.setCode(Result.FAIL);
            result.setMessage("自动放款规则加载失败");
        }
        return result;
    }

    /**
     * 更新自动放款规则
     *
     * @return
     */
    @RequestMapping(value = "updateAutoLoanStatus", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Result updateAutoLoanStatus(HttpServletRequest request) {
        Result returnCode = new Result();
        returnCode.setCode(Result.SUCCESS);
        try {
            String userNo = request.getParameter("userNo");
            String auth = request.getParameter("auth");
            //检测用户权限是否和数据库存储的权限一致
            if (auth == null || !auth.equals(collectorsLevelService.getUserAuth(userNo))) {
                returnCode.setCode(Result.FAIL);
                returnCode.setMessage("权限错误！");
            }
            // 检测用户是否有访问自动放款的权限
            else if (AuthUtil.isPageEnabled(auth, "ym-gd")) {

                String autoLoanStatus = request.getParameter("autoLoanStatus");
                if (autoLoanStatus == null) {
                    Result result = new Result();
                    result.setCode(Result.FAIL);
                    result.setMessage("请求参数非法，请设置自动放款状态！");
                }

                boolean hasOpenRuler = false;
                String autoLoanRulers = request.getParameter("autoLoanRulers");
                if (autoLoanRulers != null) {
                    Map rulerMap = JSONObject.parseObject(autoLoanRulers, Map.class);

                    Result result = checkLastOrderSwitch(rulerMap);
                    if (result.getCode() == Result.FAIL) {
                        return result;
                    }

                    result = checkTimeSwitch(rulerMap);
                    if (result.getCode() == Result.FAIL) {
                        return result;
                    }

                    result = checkConcurrencySwitch(rulerMap);
                    if (result.getCode() == Result.FAIL) {
                        return result;
                    }

                    result = checkAmountSwitch(rulerMap);
                    if (result.getCode() == Result.FAIL) {
                        return result;
                    }

                    hasOpenRuler = checkOpenRuler(rulerMap);
                    if (!hasOpenRuler && Constants.SWITCH_ON.equals(autoLoanStatus)) {
                        returnCode.setCode(Result.FAIL);
                        returnCode.setMessage("请求参数非法，请设置自动放款规则！");
                        return returnCode;
                    }

                    List<AutoLoanRuler> rulers = new ArrayList<AutoLoanRuler>();
                    for (int i = 0; i < AutoLoanRulerPropertyEnum.values().length; i++) {
                        String property = AutoLoanRulerPropertyEnum.values()[i].getProperty();
                        if (rulerMap.containsKey(property) && rulerMap.get(property) != null) {
                            String value = rulerMap.get(property).toString();
                            AutoLoanRuler ruler = new AutoLoanRuler();
                            ruler.setProperty(property);
                            ruler.setValue(value);
                            rulers.add(ruler);
                        }
                    }
                    autoLoanService.updateAutoLoanRulers(rulers);
                }

                AutoLoanList currentStatus = autoLoanService.getCurrentStatus();
                if (currentStatus == null) {
                    AutoLoanList newStatus = new AutoLoanList();
                    newStatus.setRulerJson(autoLoanRulers);
                    newStatus.setStatus(autoLoanStatus);
                    newStatus.setCreationUser(userNo);
                    newStatus.setCreationDate(new Date());
                    if (Constants.SWITCH_ON.equals(autoLoanStatus)) {
                        newStatus.setLoanAmount(0d);
                        newStatus.setLoanAmountUpdateDate(new Date());
                        newStatus.setDataType(2);
                    } else {
                        newStatus.setDataType(1);
                    }
                    autoLoanService.insertAutoLoanStatus(newStatus);
                    returnCode.setCode(Result.SUCCESS);
                    returnCode.setMessage("更新自动放款状态成功");
                } else if (!autoLoanStatus.equals(currentStatus.getStatus())
                        || !equalRulers(autoLoanRulers, currentStatus.getRulerJson())) {
                    AutoLoanList newStatus = (AutoLoanList) currentStatus.clone();
                    newStatus.setId(null);
                    newStatus.setCreationDate(new Date());
                    if (autoLoanStatus.equals(currentStatus.getStatus())) {
                        newStatus.setDataType(1);
                    } else if (Constants.SWITCH_ON.equals(currentStatus.getStatus())) {
                        newStatus.setDataType(2);
                    } else {
                        newStatus.setDataType(0);
                    }

                    //开启自动放款
                    if (Constants.SWITCH_OFF.equals(currentStatus.getStatus())
                            && Constants.SWITCH_ON.equals(autoLoanStatus)) {
                        newStatus.setLoanAmount(0d);
                        newStatus.setLoanAmountUpdateDate(new Date());
                    }

                    //未开启，修改规则
                    if (Constants.SWITCH_OFF.equals(currentStatus.getStatus())
                            && Constants.SWITCH_OFF.equals(autoLoanStatus)) {
                        newStatus.setLoanAmount(null);
                        newStatus.setLoanAmountUpdateDate(null);
                    }

                    if (autoLoanRulers != null) {
                        newStatus.setRulerJson(autoLoanRulers);
                    }
                    newStatus.setCreationUser(userNo);
                    newStatus.setStatus(autoLoanStatus);
                    newStatus.setTriggerRulerKey(null);
                    newStatus.setTriggerRulerValue(null);
                    autoLoanService.insertAutoLoanStatus(newStatus);
                    returnCode.setCode(Result.SUCCESS);
                    returnCode.setMessage("更新自动放款状态成功");
                }
            } else {
                returnCode.setCode(Result.FAIL);
                returnCode.setMessage("权限不足！");
            }
        } catch (Exception e) {
            logger.error("请求参数非法!", e);
            returnCode.setCode(Result.FAIL);
            returnCode.setMessage("请求参数非法!");
        }

        return returnCode;
    }

    private boolean checkOpenRuler(Map rulers) {
        if (Constants.SWITCH_ON.equals(rulers.get(AutoLoanRulerPropertyEnum.last_order_switch.getProperty()))
                || Constants.SWITCH_ON.equals(rulers.get(AutoLoanRulerPropertyEnum.amount_switch.getProperty()))
                || Constants.SWITCH_ON.equals(rulers.get(AutoLoanRulerPropertyEnum.time_switch.getProperty()))
                || Constants.SWITCH_ON.equals(rulers.get(AutoLoanRulerPropertyEnum.concurrency_switch.getProperty()))) {
            return true;
        }
        return false;
    }

    private boolean equalRulers(String newRulers, String oldRulers) {
        if (newRulers == null && oldRulers == null) {
            return true;
        } else if (newRulers != null) {
            return newRulers.equals(oldRulers);
        } else {
            return oldRulers.equals(newRulers);
        }
    }

    private Result checkAmountSwitch(Map rulerMap) {
        Result returnCode = new Result();
        returnCode.setCode(Result.SUCCESS);

        if (Constants.SWITCH_ON.equals(rulerMap.get(amount_switch.getProperty()))) {
            String limitString = (String) rulerMap.get(amount_limit.getProperty());
            if (limitString == null) {
                returnCode.setCode(Result.FAIL);
                returnCode.setMessage("请设置放款金额！");
                return returnCode;
            }
            try {
                Float limit = Float.parseFloat(limitString);
                if (limit <= 0) {
                    returnCode.setCode(Result.FAIL);
                    returnCode.setMessage("放款金额小于等于0元，请设置合理的放款金额!");
                    return returnCode;
                }
                if (limit > 100000000L) {
                    returnCode.setCode(Result.FAIL);
                    returnCode.setMessage("放款金额过大，请设置合理的放款金额!");
                    return returnCode;
                }
            } catch (NumberFormatException e) {
                returnCode.setCode(Result.FAIL);
                returnCode.setMessage("放款金额非法!");
                return returnCode;
            }
        }

        return returnCode;
    }

    private Result checkConcurrencySwitch(Map rulerMap) {
        Result returnCode = new Result();
        returnCode.setCode(Result.SUCCESS);

        if (Constants.SWITCH_ON.equals(rulerMap.get(concurrency_switch.getProperty()))) {
            String limitString = (String) rulerMap.get(concurrency_limit.getProperty());
            if (limitString == null) {
                returnCode.setCode(Result.FAIL);
                returnCode.setMessage("请设置并发限制次数！");
                return returnCode;
            }
            try {
                Integer limit = Integer.parseInt(limitString);
                if (limit <= 0) {
                    returnCode.setCode(Result.FAIL);
                    returnCode.setMessage("并发限制次数小于等于0，请设置合理的并发限制次数!");
                    return returnCode;
                }
                if (limit > 999) {
                    returnCode.setCode(Result.FAIL);
                    returnCode.setMessage("并发限制次数过大，请设置合理的并发限制次数!");
                    return returnCode;
                }
            } catch (NumberFormatException e) {
                returnCode.setCode(Result.FAIL);
                returnCode.setMessage("并发限制次数非法!");
                return returnCode;
            }
        }
        return returnCode;
    }

    private Result checkTimeSwitch(Map rulerMap) {
        Result returnCode = new Result();
        returnCode.setCode(Result.SUCCESS);

        if (Constants.SWITCH_ON.equals(rulerMap.get(time_switch.getProperty()))) {
            String startTime = (String) rulerMap.get(start_time.getProperty());
            String endTime = (String) rulerMap.get(end_time.getProperty());

            Date startDate = null;
            Date endDate = null;
            if (startTime != null && startTime.trim().length() > 0) {
                try {
                    startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(startTime.trim());
                } catch (ParseException e) {
                    returnCode.setCode(Result.FAIL);
                    returnCode.setMessage("非法的起始时间！");
                    return returnCode;
                }
            }
            if (endTime.trim() != null && endTime.trim().length() > 0) {
                try {
                    endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(endTime.trim());
                    if (endDate.compareTo(new Date()) <= 0) {
                        returnCode.setCode(Result.FAIL);
                        returnCode.setMessage("结束时间不能小于等于当前时间！");
                        return returnCode;
                    }
                } catch (ParseException e) {
                    returnCode.setCode(Result.FAIL);
                    returnCode.setMessage("非法的结束时间！");
                    return returnCode;
                }
            }
            if (startDate != null && endDate != null && startDate.compareTo(endDate) >= 0) {
                returnCode.setCode(Result.FAIL);
                returnCode.setMessage("起始时间不能大于等于结束时间！");
                return returnCode;
            }

            if (startDate == null && endDate == null) {
                returnCode.setCode(Result.FAIL);
                returnCode.setMessage("请设置起始时间和结束时间！");
                return returnCode;
            }
        }
        return returnCode;
    }

    private Result checkLastOrderSwitch(Map rulerMap) {
        Result returnCode = new Result();
        returnCode.setCode(Result.SUCCESS);

        if (Constants.SWITCH_ON.equals(rulerMap.get(last_order_switch.getProperty()))) {
            if (!Constants.SWITCH_ON.equals(rulerMap.get(normal_order_switch.getProperty()))
                    && !Constants.SWITCH_ON.equals(rulerMap.get(overdue_order_switch.getProperty()))
                    && !Constants.SWITCH_ON.equals(rulerMap.get(none_loan_switch.getProperty()))) {
                returnCode.setCode(Result.FAIL);
                returnCode.setMessage("请设置上单状态！");
                return returnCode;
            }

            if (Constants.SWITCH_ON.equals(rulerMap.get(overdue_order_switch.getProperty()))) {
                String dayString = (String) rulerMap.get(overdue_order_day.getProperty());
                if (dayString == null) {
                    returnCode.setCode(Result.FAIL);
                    returnCode.setMessage("请设置逾期天数！");
                    return returnCode;
                }
                try {
                    Integer day = Integer.parseInt(dayString);
                    if (day <= 0) {
                        returnCode.setCode(Result.FAIL);
                        returnCode.setMessage("逾期天数小于等于0，请设置合理的逾期天数!");
                        return returnCode;
                    }
                    if (day > 9) {
                        returnCode.setCode(Result.FAIL);
                        returnCode.setMessage("逾期天数过大，请设置合理的逾期天数!");
                        return returnCode;
                    }
                } catch (NumberFormatException e) {
                    returnCode.setCode(Result.FAIL);
                    returnCode.setMessage("逾期天数非法!");
                    return returnCode;
                }
            }
        }
        return returnCode;
    }

}
