package com.loan_server.app_service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import com.loan_entity.app.*;
import com.loan_entity.common.Constants;
import com.loan_entity.shell.RegisterVo;
import com.loan_server.manager_mapper.*;
import com.loan_utils.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.loan_api.app.UserService;
import com.loan_entity.loan.PerAccountLog;
import com.loan_entity.manager.Coupon;
import com.loan_entity.manager.Feedback;
import com.loan_entity.manager.Msg;
import com.loan_entity.manager.MsgTemplate;
import com.loan_entity.manager.Order;
import com.loan_entity.manager.Question;
import com.loan_entity.utils.RepaymentDetails;
import com.loan_server.app_mapper.BorrowListMapper;
import com.loan_server.app_mapper.PerCouponMapper;
import com.loan_server.app_mapper.PersonMapper;
import com.loan_server.app_mapper.ProductTermMapper;
import com.loan_server.loan_mapper.PerAccountLogMapper;
import com.loan_utils.entity.BQSEntity;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;
import redis.clients.jedis.JedisCluster;

public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory
            .getLogger(UserServiceImpl.class);

    private static final String WHITE_LIST_KEY = Constants.YM_ADMIN_SYSTEN_KEY + Constants.WHITELIST_PERID;

    private static final String WHITE_LIST_PHONE_KEY = Constants.YM_ADMIN_SYSTEN_KEY + Constants.WHITELIST_PHONE;

    @Autowired
    private PersonMapper personMapper;

    @Autowired
    private FeedbackMapper feedMapper;

    @Autowired
    private QuestionMapper quMapper;

    @Autowired
    private MsgMapper msgMapper;

    @Autowired
    private MsgTemplateMapper msgTemplateMapper;

    @Autowired
    private BorrowListMapper borrowListMapper;

    @Autowired
    private PerAccountLogMapper palMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private PerCouponMapper percouMapper;

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private ProductTermMapper productTermMapper;
    @Autowired
    private JedisCluster jedisCluster;

    @Autowired
    private CodeValueMapper codeValueMapper;

    public String userLogin(String phone, String password) {
        JSONObject obj = new JSONObject();
        JSONObject jj = new JSONObject();
        try {
            Person p = personMapper.getPersonByPhone(phone);
            if (null != p) {
                Person user = new Person();
                user.setPhone(phone);
                user.setPassword(password);
                List<Person> personList = personMapper.userLogin(user);
                if (null != personList && personList.size() > 0) {
                    if (personList.size() > 1) {
                        jj.put("per_id", "");
                        jj.put("token", "");
                        obj.put("code", CodeReturn.fail);
                        obj.put("info", "该手机号有多个账户");
                        obj.put("data", jj);
                    } else {
                        String token = UUID.randomUUID().toString();
                        jj.put("per_id", personList.get(0).getId());
                        jj.put("token", token);
                        obj.put("code", CodeReturn.success);
                        obj.put("info", "登录成功");
                        obj.put("data", jj);
                        Person pp = new Person();
                        pp.setId(personList.get(0).getId());
                        pp.setTokenId(token);
                        personMapper.updateByPrimaryKeySelective(pp);
                    }
                } else {
                    jj.put("per_id", "");
                    jj.put("token", "");
                    obj.put("code", CodeReturn.fail);
                    obj.put("info", "账号密码不匹配");
                    obj.put("data", jj);
                }
            } else {
                jj.put("per_id", "");
                jj.put("token", "");
                obj.put("code", CodeReturn.fail);
                obj.put("info", "该手机号未注册");
                obj.put("data", jj);
            }

        } catch (Exception e) {
            logger.debug(e);
            e.printStackTrace();
            jj.put("per_id", "");
            jj.put("token", "");
            obj.put("code", CodeReturn.fail);
            obj.put("info", "系统错误");
            obj.put("data", jj);
        }
        return obj.toString();
    }

    public String userRegister(Person user) {
        JSONObject obj = new JSONObject();
        try {
            // 幂等性操作 防止重复放款
            if (StringUtils.isEmpty(jedisCluster.get(RedisConst.REGISTER_KEY + user.getPhone()))) {
                String setnx = jedisCluster.set(RedisConst.REGISTER_KEY + user.getPhone(), user.getPhone(), "NX", "EX", 60 * 60 * 24);
                if (!"OK".equals(setnx)) {
                    logger.error("直接返回，注册重复数据phone" + user.getPhone());
                    Person p = personMapper.getPersonByPhone(user.getPhone());
                    obj.put("code", CodeReturn.PHONE_EXIST);
                    obj.put("info", "不可重复注册！");
                    if (p !=null){
                        obj.put("data", p.getSource());
                    }
                    return obj.toString();
                }
            } else {
                logger.error("直接返回，注册重复数据phone" + user.getPhone());
                Person p = personMapper.getPersonByPhone(user.getPhone());
                obj.put("code", CodeReturn.PHONE_EXIST);
                obj.put("info", "不可重复注册！");
                if (p !=null){
                    obj.put("data", p.getSource());
                }
                return obj.toString();
            }

            Person p = personMapper.getPersonByPhone(user.getPhone());
            if (null == p) {
                personMapper.insertSelective(user);
                Coupon cou = couponMapper.selectByName("注册立减5元券");
                if (!cou.getStatus().equals("1")) {
                    obj.put("code", CodeReturn.success);
                    obj.put("info", "注册成功");
                    obj.put("data", user.getId());
                    //回调贝壳钱包
                    shellRegister(user);
                    return obj.toString();
                }
                PerCoupon pc = new PerCoupon();
                pc.setPerId(user.getId());
                pc.setCouponId(cou.getId());
                pc.setCouponName(cou.getCouponName());
                pc.setProductId(cou.getProductId());
                pc.setStartDate(new Date());
                // 算过期日
                Date date = pc.getStartDate();
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(date);
                calendar.add(Calendar.DATE, cou.getDuation());// 把日期往后增加一天.整数往后推,负数往前移动
                date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
                pc.setEndDate(date);

                pc.setAmount(cou.getAmount().toString());
                pc.setStatus("1");
                pc.setCreationUser(user.getId());
                pc.setCreationDate(new Date());
                pc.setUpdateUser(user.getId());
                pc.setUpdateDate(new Date());
                percouMapper.insertSelective(pc);
                obj.put("code", CodeReturn.success);
                obj.put("info", "注册成功");
                obj.put("data", user.getId());
                Coupon cou2 = couponMapper.selectByName("好友注册立减5元券");
                //回调贝壳钱包
                shellRegister(user);
                if (user.getInviter() != 0) {
                    String couponresult = this.userInviceCouponFor(user.getInviter(), cou2);
                }
            } else {
                obj.put("code", CodeReturn.PHONE_EXIST);
                obj.put("info", "注册失败，该手机号已经注册！");
                obj.put("data", p.getSource());
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("code", CodeReturn.fail);
            obj.put("info", "系统错误");
            obj.put("data", "");
        }
        return obj.toString();
    }

    public String userInviceCoupon(int userid, String couponName) {
        JSONObject obj = new JSONObject();
        try {
            Coupon cou = couponMapper.selectByName(couponName);
            PerCoupon pc = new PerCoupon();
            pc.setPerId(userid);
            pc.setCouponId(cou.getId());
            pc.setCouponName(cou.getCouponName());
            pc.setProductId(cou.getProductId());
            pc.setStartDate(new Date());
            // 算过期日
            Date date = pc.getStartDate();
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, cou.getDuation());// 把日期往后增加一天.整数往后推,负数往前移动
            date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
            pc.setEndDate(date);

            pc.setAmount(cou.getAmount().toString());
            pc.setStatus("1");
            pc.setCreationUser(userid);
            pc.setCreationDate(new Date());
            pc.setUpdateUser(userid);
            pc.setUpdateDate(new Date());
            percouMapper.insertSelective(pc);
            obj.put("code", CodeReturn.success);
            obj.put("info", "恭喜获得优惠券一张！");
            obj.put("data", userid);

        } catch (Exception e) {
            e.printStackTrace();
            obj.put("code", CodeReturn.fail);
            obj.put("info", "系统错误");
            obj.put("data", "");
        }
        return obj.toString();
    }

    public String userInviceCouponFor(int userid, Coupon cou) {
        JSONObject obj = new JSONObject();
        try {
            PerCoupon pc = new PerCoupon();
            pc.setPerId(userid);
            pc.setCouponId(cou.getId());
            pc.setCouponName(cou.getCouponName());
            pc.setProductId(cou.getProductId());
            pc.setStartDate(new Date());
            // 算过期日
            Date date = pc.getStartDate();
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, cou.getDuation());// 把日期往后增加一天.整数往后推,负数往前移动
            date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
            pc.setEndDate(date);

            pc.setAmount(cou.getAmount().toString());
            pc.setStatus("1");
            pc.setCreationUser(userid);
            pc.setCreationDate(new Date());
            pc.setUpdateUser(userid);
            pc.setUpdateDate(new Date());
            percouMapper.insertSelective(pc);
            obj.put("code", CodeReturn.success);
            obj.put("info", "恭喜获得优惠券一张！");
            obj.put("data", userid);

        } catch (Exception e) {
            e.printStackTrace();
            obj.put("code", CodeReturn.fail);
            obj.put("info", "系统错误");
            obj.put("data", "");
        }
        return obj.toString();
    }

    public String updatePassword(Person user) {
        JSONObject obj = new JSONObject();
        try {
            int li = personMapper.updatePassword(user);
            // System.out.println(li);
            if (li > 0) {
                obj.put("code", CodeReturn.success);
                obj.put("info", "密码修改成功");
                obj.put("data", "");
            } else {
                obj.put("code", CodeReturn.fail);
                obj.put("info", "密码修改失败");
                obj.put("data", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("code", CodeReturn.fail);
            obj.put("info", "系统错误");
            obj.put("data", "");
        }
        return obj.toString();
    }

    public String getPersonInfo(String userId) {
        JSONObject obj = new JSONObject();
        try {
            PersonMode p = personMapper.getPersonInfo(userId);
            obj.put("code", CodeReturn.success);
            obj.put("info", "查询成功");
            obj.put("data", JSONObject.fromObject(p));
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("code", CodeReturn.fail);
            obj.put("info", "系统错误");
            obj.put("data", new JSONObject());
        }
        return obj.toString();
    }

    public String personUpdatePassword(Person user) {
        JSONObject obj = new JSONObject();
        try {
            Person user1 = personMapper.selectByPrimaryKey(user.getId());
            if (user.getOldPassword().equals(user1.getPassword())) {
                int a = personMapper.personUpdatePassword(user);
                obj.put("code", CodeReturn.success);
                obj.put("info", "密码修改成功");
                obj.put("data", a);
            } else {
                obj.put("code", CodeReturn.fail);
                obj.put("info", "原密码不正确");
                obj.put("data", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("code", CodeReturn.fail);
            obj.put("info", "系统错误");
            obj.put("data", "");
        }
        return obj.toString();
    }

    public String userFeedBack(Feedback feed) {
        JSONObject obj = new JSONObject();
        try {
            feedMapper.insertSelective(feed);
            obj.put("code", CodeReturn.success);
            obj.put("info", "反馈成功");
            obj.put("data", "");
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("code", CodeReturn.fail);
            obj.put("info", "系统错误");
            obj.put("data", "");
        }
        return obj.toString();
    }

    public String getQuestion() {
        JSONObject obj = new JSONObject();

        try {
            List<Question> quList;

            String redis = jedisCluster.get(RedisConst.QUESTION_KEY);
            if (StringUtils.isEmpty(redis)) {
                // redis里没有
                quList = quMapper.selectAllQuestion();
                // redis set
                jedisCluster.set(RedisConst.QUESTION_KEY, com.alibaba.fastjson.JSONObject.toJSONString(quList));

            } else {
                // redis里有
                quList = (List<Question>) com.alibaba.fastjson.JSONObject.parse(redis);
            }

            obj.put("code", CodeReturn.success);
            obj.put("info", "查询成功");
            obj.put("data", JSONArray.fromObject(quList));
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("code", CodeReturn.fail);
            obj.put("info", "系统错误");
            obj.put("data", "");
        }
        return obj.toString();
    }

    public String getMessageByUserId(String userId, int nowPage, int pageSize) {
        JSONObject obj = new JSONObject();
        try {
            int start = (nowPage - 1) * pageSize;
            List<Msg> msgList = msgMapper.getMessageByUserId(userId, start,
                    pageSize);
            JSONArray arr = JSONArray.fromObject(msgList);
            for (int i = 0; i < msgList.size(); i++) {
                if (null == msgList.get(i).getCreate_time()
                        || "".equals(msgList.get(i).getCreate_time())) {
                    arr.getJSONObject(i).put("create_time", "");
                } else {
                    arr.getJSONObject(i).put(
                            "create_time",
                            DateUtil.getDateStringToHHmmss(DateUtil
                                    .getDateHHmmss(msgList.get(i)
                                            .getCreate_time())));
                }
            }
            obj.put("code", CodeReturn.success);
            obj.put("info", "查询成功");
            obj.put("data", arr);
        } catch (Exception e) {
            logger.error(e);
            obj.put("code", CodeReturn.fail);
            obj.put("info", "查询失败");
            obj.put("data", "");
        }

        return obj.toString();
    }

    public String setMessage(String userId, String templateId, String params) {
        JSONObject obj = new JSONObject();
        try {
            String[] cc = params.split(",");
            // 定义最后的消息内容
            String dd = "";
            // 获取消息模版
            MsgTemplate msgTemplate = msgTemplateMapper
                    .selectByPrimaryKey(Integer.parseInt(templateId));
            if (null != msgTemplate) {

                if ("1".equals(msgTemplate.getStatus())) {
                    // 获取模版的内容
                    String ll = msgTemplate.getContent();
                    // 获取模版的标题
                    String title = msgTemplate.getTitle();
                    // 分割模版内容
                    String[] aa = ll.split("\\{");
                    // 将模版内容和参数拼接成最后的消息内容
                    for (int i = 0; i < aa.length; i++) {
                        String[] bb = aa[i].split("}");
                        if (bb.length > 1) {
                            dd += cc[i - 1] + bb[1];
                        } else {
                            dd += aa[i];
                        }
                    }
                    Msg msg = new Msg();
                    msg.setContent(dd);
                    msg.setTitle(title);
                    msg.setPerId(Integer.parseInt(userId));
                    msg.setStatus("n");
                    msg.setType(1);
                    msg.setCreateTime(new Date());
                    msgMapper.insertSelective(msg);

                    obj.put("code", CodeReturn.success);
                    obj.put("info", "消息发送成功");
                    obj.put("data", dd);
                } else {
                    obj.put("code", CodeReturn.fail);
                    obj.put("info", "模版已失效");
                    obj.put("data", "");
                }
            } else {
                obj.put("code", CodeReturn.fail);
                obj.put("info", "模版不存在");
                obj.put("data", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("code", CodeReturn.fail);
            obj.put("info", "消息发送失败,请检查参数和模版是否匹配！");
            obj.put("data", "");
        }
        return obj.toString();
    }

    public String getPersonByPhone(String phone) {
        JSONObject obj = new JSONObject();
        try {
            Person p = personMapper.getPersonByPhone(phone);
            if (null == p) {
                obj.put("code", CodeReturn.success);
                obj.put("info", "手机号不存在");
                obj.put("data", "");
            } else {
                obj.put("code", CodeReturn.fail);
                obj.put("info", "手机号已存在");
                obj.put("data", "");
            }
        } catch (Exception e) {
            obj.put("code", CodeReturn.systemerror);
            obj.put("info", "系统错误");
            obj.put("data", "");
        }
        return obj.toString();
    }

    @Override
    public NoteResult getWithdrawInformation(int userId) {
        NoteResult result = new NoteResult(CodeReturn.FAIL_CODE, "失败");

        try {
            com.alibaba.fastjson.JSONObject dataObject = new com.alibaba.fastjson.JSONObject();
            dataObject.put("isAvailable", false);

            if(isInWhiteList(String.valueOf(userId))){
               Person person = personMapper.selectByPrimaryKey(userId);
               if(person != null && !"Y".equals(person.getBlacklist())){
                   dataObject.put("isAvailable", true);
               }
            }

            result.setCode(CodeReturn.SUCCESS_CODE);
            result.setInfo("成功");
            result.setData(dataObject);
        } catch (Throwable e) {
            logger.error("检测是否可以引导用户下载金狐贷失败", e);
        }

        return result;
    }

    @Override
    public boolean isInWhiteList(String userid) {
        if(Detect.notEmpty(jedisCluster.hget(WHITE_LIST_KEY, userid)) &&
                jedisCluster.hget(WHITE_LIST_KEY, userid).equals("1")){
            return true;
        }
        return false;
    }

    @Override
    public void syncWhiteList() {
        List<Integer> perIds = borrowListMapper.syncWhiteList();
        if(perIds != null){
            //先删除，在保存
            jedisCluster.del(WHITE_LIST_KEY);

            for(Integer perId : perIds){
                if(Detect.isPositive(perId)){
                    jedisCluster.hset(WHITE_LIST_KEY, perId.toString(), "1");
                }
            }
        }
    }

    @Override
    public void syncPhoneWhiteList() {
        List<String> phones = borrowListMapper.syncPhoneWhiteList();
        if(phones != null){
            //先删除，在保存
            jedisCluster.del(WHITE_LIST_PHONE_KEY);

            for(String phone : phones){
                if(phone != null){
                    jedisCluster.hset(WHITE_LIST_PHONE_KEY, phone, "1");
                }
            }
        }
    }

    public String getMyBorrowList(String userId, int nowPage, int pageSize) {
        JSONObject obj = new JSONObject();
        try {
            int start = (nowPage - 1) * pageSize;
            List<BorrowList> borrowLists = borrowListMapper.getMyBorrowList(
                    userId, start, pageSize);
            for (int i = 0; i < borrowLists.size(); i++) {
                //borrowLists.get(i).setBorrAmount(borrowLists.get(i).getBorrAmount().substring(0, borrowLists.get(i).getBorrAmount().indexOf(".")+3));
                //----------------2017.3月更新  借款金额从产品表中取------------------------------
                BorrowList borr = borrowLists.get(i);
                Integer prodId = borr.getProdId();
                ProductTerm term = productTermMapper.selectByProductId(prodId);
                borr.setBorrAmount(String.format("%.2f", term.getMaximumAmount()));
            }

            JsonConfig jsonConfig = new JsonConfig();
            jsonConfig.registerJsonValueProcessor(Date.class,
                    new JsonValueProcessor() {

                        public Object processArrayValue(Object k, JsonConfig jc) {
                            return null;
                        }

                        public Object processObjectValue(String k, Object v,
                                                         JsonConfig arg2) {
                            if (v != null && v instanceof Date) {
                                return new SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss").format(v);
                            }
                            if (v == null || v == "") {
                                return null;
                            } else {
                                return v;
                            }
                        }
                    });
            JSONArray ll = JSONArray.fromObject(borrowLists, jsonConfig);

            obj.put("code", CodeReturn.success);
            obj.put("info", "查询成功");
            obj.put("data", ll);
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("code", CodeReturn.fail);
            obj.put("info", "查询失败");
            obj.put("data", new JSONArray());
        }

        return obj.toString();
    }

    public String updateMessageStatus(String userId, String messageId) {
        JSONObject obj = new JSONObject();
        try {
            int i = msgMapper.updateMessageStatus(messageId);
            if (i > 0) {
                obj.put("code", CodeReturn.success);
                obj.put("info", "修改成功");
                obj.put("data", "");
            } else {
                obj.put("code", CodeReturn.fail);
                obj.put("info", "修改失败");
                obj.put("data", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("code", CodeReturn.fail);
            obj.put("info", "修改失败,系统错误");
            obj.put("data", "");
        }
        return obj.toString();
    }

    public String getProdModeByBorrId(String borrId) {
        JSONObject obj = new JSONObject();
        try {
            List<ProdMode> prodMode = borrowListMapper
                    .getProdModeByBorrId(borrId);
            ProdMode mode = prodMode.get(0);
            //-----------------2017.3月更新  借款金额取产品表中的金额---------------------------
            String prodId = mode.getProdId();
            ProductTerm term = productTermMapper.selectByProductId(Integer.valueOf(prodId));
            String borrAmount = String.format("%.2f", term.getMaximumAmount());
            mode.setBorrAmount(borrAmount);
            String mationAmout = mode.getMationAmout();
            mationAmout = mationAmout
                    .substring(0, mationAmout.indexOf(".") + 3);
            mode.setMationAmout(mationAmout);
            JSONObject mo = JSONObject.fromObject(mode);
            if (null == mode.getPlanrepayDate() || "".equals(mode.getPlanrepayDate())) {
                mo.put("planrepayDate", "");
            } else {
                mo.put("planrepayDate", DateUtil.getDateStringToHHmmss(DateUtil
                        .getDateHHmmss(mode.getPlanrepayDate())));
            }
            if (null == mode.getPayDate() || "".equals(mode.getPayDate())) {
                mo.put("payDate", "");
            } else {
                mo.put("payDate", DateUtil.getDateStringToHHmmss(DateUtil
                        .getDateHHmmss(mode.getPayDate())));
            }
            if (null == mode.getAskborrDate() || "".equals(mode.getAskborrDate())) {
                mo.put("askborrDate", "");
            } else {
                mo.put("askborrDate", DateUtil.getDateStringToHHmmss(DateUtil
                        .getDateHHmmss(mode.getAskborrDate())));
            }
            if (null == mode.getActRepayDate() || "".equals(mode.getActRepayDate())) {
                mo.put("actRepayDate", "");
            } else {
                mo.put("actRepayDate", DateUtil.getDateStringToHHmmss(DateUtil
                        .getDateHHmmss(mode.getActRepayDate())));
            }
            obj.put("code", CodeReturn.success);
            obj.put("info", "查询成功");
            obj.put("data", mo);
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("code", CodeReturn.fail);
            obj.put("info", "查询失败");
            obj.put("data", new JSONObject());
        }

        return obj.toString();
    }

    @Override
    public String getRepaymentDetails(String userId, String borrId) {

        JSONObject obj = new JSONObject();
        DecimalFormat df = new DecimalFormat("######0.00");
        try {
            RepaymentDetails rd = borrowListMapper.getRepaymentDetails(borrId);
            logger.info("还款详情==" + JSONObject.fromObject(rd));
            if (null == rd.getAlsoAmount() || "".equals(rd.getAlsoAmount())) {
                rd.setAlsoAmount(rd.getPlan_repay());
            }
            // 算手续费
            String abc = "2.00";
            //2017.3.27更改支付
//            double ll = Double.parseDouble(rd.getAlsoAmount()) * 0.002;
//            if (ll > 2) {
//                BigDecimal d = new BigDecimal(rd.getAlsoAmount()); // 存款
//                BigDecimal r = new BigDecimal(0.002); // 利息
//                BigDecimal i = d.multiply(r)
//                        .setScale(2, RoundingMode.HALF_EVEN); // 使四舍五入算法
//                abc = String.valueOf(i);
//            } else {
//                abc = "2.00";
//            }

            // abc = df.format(Double.parseDouble(abc));
            Double abcd = Double.parseDouble(rd.getAlsoAmount())
                    + Double.parseDouble(abc);
            // 减去优惠券的金额
            // if (null != rd.getCouAmount()) {
            // abcd = abcd - Double.parseDouble(rd.getCouAmount());
            // }
            rd.setAct_repay_amount(df.format(abcd));
            rd.setCounterFee(abc);
            obj.put("code", CodeReturn.success);
            obj.put("info", "查询成功");
            obj.put("data", JSONObject.fromObject(rd));
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("code", CodeReturn.fail);
            obj.put("info", "查询失败");
            obj.put("data", "");
        }
        return obj.toString();
    }

    @Override
    public String perAccountLog(String userId, int nowPage, int pageSize) {
        JSONObject obj = new JSONObject();
        try {
            int start = (nowPage - 1) * pageSize;
            List<PerAccountLog> pal = palMapper.getPerAccountLog(userId, start,
                    pageSize);
            JSONArray arr = new JSONArray();
            if (null == pal || 0 == pal.size()) {
                obj.put("code", CodeReturn.success);
                obj.put("info", "查询成功");
                obj.put("data", arr);
            } else {
                for (int i = 0; i < pal.size(); i++) {
                    JSONObject obj1 = new JSONObject();
                    obj1.put("id", pal.get(i).getId());
                    obj1.put("perId", pal.get(i).getPerId());
                    obj1.put("operationTypeName", pal.get(i)
                            .getOperationTypeName());
                    obj1.put("operationType", pal.get(i).getOperationType());
                    // 取小数点后两位
                    DecimalFormat df = new DecimalFormat("######0.00");
                    obj1.put("amount", df.format(Double.parseDouble(pal.get(i)
                            .getAmount())));
                    obj1.put("addDateTime", DateUtil
                            .getDateStringToHHmmss(DateUtil.getDateHHmmss(pal
                                    .get(i).getAddDateTime())));
                    arr.add(obj1);
                }
            }
            obj.put("code", CodeReturn.success);
            obj.put("info", "查询成功");
            obj.put("data", arr);
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("code", CodeReturn.fail);
            obj.put("info", "查询失败");
            obj.put("data", "");
        }
        return obj.toString();
    }

    @Override
    public String canOfOrder(String orderId, String token, String info) {
        JSONObject obj = new JSONObject();
        try {
            Order order = orderMapper.selectBySerial(orderId);
            Order order1 = orderMapper.selectByPid(order.getId());
            Person person = new Person();
            person = personMapper.selectByPrimaryKey(order.getPerId());
            logger.info("orderId==" + orderId + ",token==" + token + ",info==" + info);
            if (token.equals(person.getTokenId())) {
                order.setRlState("f");
                order.setRlRemark(info);
                order1.setRlState("f");
                order1.setRlRemark(info);
                orderMapper.updateByPrimaryKeySelective(order);
                orderMapper.updateByPrimaryKeySelective(order1);
                obj.put("code", CodeReturn.success);
                obj.put("info", "订单取消成功");
                obj.put("data", "");
            } else {
                obj.put("code", CodeReturn.TOKEN_WRONG);
                obj.put("info", "该帐号已在别的设备登录，请重新登录");
                obj.put("data", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("code", CodeReturn.fail);
            obj.put("info", "订单取消失败");
            obj.put("data", "");
        }
        return obj.toString();
    }

    @Override
    public String canbaiqishi(String tokenKey, String event, String phone) {
        String riskUrl = PropertiesReaderUtil.read("third", "riskUrl");
        BQSEntity bqs = new BQSEntity();
        bqs.setTokenKey(tokenKey);
        bqs.setEventType(event);
        bqs.setPhone(phone);
        String res = BQSUtil.send(riskUrl, bqs);
        return res;
    }

    @Override
    public String updatePasswordCanbaiqishi(String tokenKey, String event,
                                            String userId) {
        String riskUrl = PropertiesReaderUtil.read("third", "riskUrl");
        PersonMode pm = personMapper.getPersonInfo(userId);

        BQSEntity bqs = new BQSEntity();
        bqs.setTokenKey(tokenKey);
        bqs.setEventType(event);
        bqs.setName(pm.getName());
        bqs.setPhone(pm.getPhone());
        bqs.setIdValue(pm.getCardNum());
        String res = BQSUtil.send(riskUrl, bqs);
        return res;
    }

    @Override
    public String getMyCoupon(String userId, String couponStatus, int nowPage,
                              int pageSize) {
        JSONObject obj = new JSONObject();
        try {
            int start = (nowPage - 1) * pageSize;
            List<PerCoupon2> couList = new ArrayList<PerCoupon2>();

            if ("1".equals(couponStatus)) {
                couList = percouMapper.getMyCoupon(userId, start, pageSize);
            } else {
                couList = percouMapper.getMyCoupon2(userId, start, pageSize);
            }
            obj.put("code", CodeReturn.success);
            obj.put("info", "查询成功");
            obj.put("data", JSONArray.fromObject(couList));
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("code", CodeReturn.fail);
            obj.put("info", "查询失败");
            obj.put("data", "");
        }
        return obj.toString();
    }

    @Override
    public int yanzhengtoken(String userId, String token) {
        Person pp = personMapper.selectByPrimaryKey(Integer.parseInt(userId));
        if (token.equals(pp.getTokenId())) {
            return 200;
        } else {
            return 201;
        }
    }

    /*
    检查该手机号是否在mysql黑名单
     */
    @Override
    public NoteResult checkBlack(String phone) {
        NoteResult result = new NoteResult();
        try {
            int i = personMapper.checkBlack(phone);
            if (i > 0) {
                result.setCode(CodeReturn.NOW_BORROW_CODE);//code 202
                result.setInfo("黑名单");
            } else {
                result.setCode(CodeReturn.SUCCESS_CODE);
                result.setInfo("非黑名单");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new NoteResult(CodeReturn.FAIL_CODE, "查询失败");
        }
        return result;

    }

    @Override
    public String deleteRedis(String per_id) {
        return jedisCluster.del(RedisConst.NODE_KEY + per_id).toString();
    }

    /***
     *  贝壳钱包注册回调
     * @param phone
     */
    private void shellRegister(Person user) {
        //在redis中取出贝壳渠道编号
        String source = jedisCluster.get(ShellRedisConstant.SHELL_SOURCE);
        if (StringUtils.isEmpty(source)) {
            List<String> list = codeValueMapper.getSourceByDesc(ShellConstant.DESCRIPTION);
            if (list != null && list.size() > 0) {
                source = StringUtils.join(list.toArray(), ",");
                jedisCluster.set(ShellRedisConstant.SHELL_SOURCE, source);
            }
        }
        //判断是否为贝壳推送用户
        if (source != null && source.contains(user.getSource())) {
            String register = PropertiesReaderUtil.read("shell", "registerUrl");
            String key = PropertiesReaderUtil.read("shell", "Key");
            RegisterVo vo = new RegisterVo();
            vo.setPhone(user.getPhone());
            vo.setKey(key);
            vo.setRegister_time(DateUtil.getDateStringToHHmmss(new Date()));
            logger.info("---------------贝壳钱包注册通过放款参数register = " + vo);
            IOUtil.postNetReturnUrl(register, MapUtils.setConditionMap(vo));
        }

    }

}