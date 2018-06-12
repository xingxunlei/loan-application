package com.loan_server.app_service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jinhuhang.risk.Result;
import com.jinhuhang.risk.dto.yxshare.jsonbean.ReqData;
import com.jinhuhang.risk.service.RiskAPI;
import com.jinhuhang.risk.service.juxinli.JuxinliAPI;
import com.jinhuhang.risk.service.zhima.ZhiMaCreditApi;
import com.loan_api.app.LoanService;
import com.loan_api.loan.YsbpayService;
import com.loan_entity.app.*;
import com.loan_entity.app_vo.ProductApp;
import com.loan_entity.app_vo.RiskContent;
import com.loan_entity.manager.BankList;
import com.loan_entity.manager.Review;
import com.loan_server.app_mapper.*;
import com.loan_server.constant.LoanConstant;
import com.loan_server.loan_mapper.AutoLoanListMapper;
import com.loan_server.loan_service.AutoLoanServiceImpl;
import com.loan_server.manager_mapper.BankListMapper;
import com.loan_server.manager_mapper.CodeValueMapper;
import com.loan_server.manager_mapper.MsgMapper;
import com.loan_server.manager_mapper.ReviewMapper;
import com.loan_utils.util.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisCluster;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

//import com.junhuhang.risk.entity.ReqData;
//import com.junhuhang.risk.entity.Result;

//import scala.util.parsing.combinator.testing.Str;


/**
 * 借款模块接口实现类
 * @author xuepengfei
 *2016年10月9日下午3:40:59
 */
@Service
public class LoanServiceImpl implements LoanService {

    private static final Logger logger = LoggerFactory.getLogger(LoanServiceImpl.class);

    //返回参数及消息设置
    private static final String SUCCESS_CODE = CodeReturn.SUCCESS_CODE;
    private static final String SUCCESS_INFO = "成功";
    private static final String FAIL_CODE = CodeReturn.FAIL_CODE;
    private static final String FAIL_INFO = "失败";
    private static final String NOW_BORROW_CODE = CodeReturn.NOW_BORROW_CODE;
    private static final String NOW_BORROW_INFO = "现在借款";
    private static final String WAIT_SIGN_CODE = CodeReturn.WAIT_SIGN_CODE;
    private static final String WAIT_SIGN_INFO = "现在签约";
    private static final String SIGNED_CODE = CodeReturn.SIGNED_CODE;
    private static final String SIGNED_INFO = "等待放款";
    private static final String TOREPAY_CODE = CodeReturn.TOREPAY_CODE;
    private static final String TOREPAY_INFO = "现在还款";
    private static final String APPLY_CODE = CodeReturn.APPLY_CODE;
    private static final String APPLY_INFO = "继续申请";
    //认证状态常量
    private static final String BPM_UNDO_CODE = CodeReturn.BPM_UNDO_CODE;
    private static final String BPM_ZHIMA_CODE = CodeReturn.BPM_ZHIMA_CODE;
    //未认证
    private static final String BPM_UNDO_INFO = "未认证";
    //已认证
    private static final String BPM_FINISH_CODE = CodeReturn.BPM_FINISH_CODE;
    private static final String BPM_FINISH_INFO = "已认证";
    // 已认证 但重新拉通讯录
    private static final String BPM_PHONE_CODE = CodeReturn.BPM_PHONE_CODE;
    private static final String BPM_PHONE_INFO = "重新获取通讯录";// 已认证 但重新拉通讯录
    // 已认证 但重新进行聚信立 并重新拉通讯录
    private static final String JUXINLI_CODE = CodeReturn.JUXINLI_CODE;
    private static final String JUXINLI_INFO = "已认证，但重新进行聚信立 并重新拉通讯录";
    //风控返回code

    //认证状态编码
    private static final String STATUS_BPM_N = CodeReturn.STATUS_BPM_N;//未认证
    private static final String STATUS_BPM_Y = CodeReturn.STATUS_BPM_Y;//已认证
    private static final String STATUS_BPM_FAIL = CodeReturn.STATUS_BPM_FAIL;//认证失败
    private static final String STATUS_BPM_FAIL_B = CodeReturn.STATUS_BPM_FAIL_B;//认证失败且进黑名单
    private static final String STATUS_BPM_UP = CodeReturn.STATUS_BPM_UP;//已提交,仅手机认证节点有次状态
    //认证过期天数
    private static final int LIMIT_DAY = CodeReturn.LIMIT_DAY;
    //拒绝以后不能再次申请的天数
    private static final int REFUSE_DAY = CodeReturn.REFUSE_DAY;
    //借款状态常量
    private static final String STATUS_APLLY = CodeReturn.STATUS_APLLY;//申请中
    private static final String STATUS_CANCEL = CodeReturn.STATUS_CANCEL;//已取消
    private static final String STATUS_WAIT_SIGN = CodeReturn.STATUS_WAIT_SIGN;//待签约
    private static final String STATUS_SIGNED = CodeReturn.STATUS_SIGNED;//已签约
    private static final String STATUS_TO_REPAY = CodeReturn.STATUS_TO_REPAY;//待还款
    private static final String STATUS_LATE_REPAY = CodeReturn.STATUS_LATE_REPAY;//逾期未还
    private static final String STATUS_PAY_BACK = CodeReturn.STATUS_PAY_BACK;//已还清
    private static final String STATUS_REVIEW_FAIL = CodeReturn.STATUS_REVIEW_FAIL;//审核未通过
    private static final String STATUS_PHONE_REVIEW_FAIL = CodeReturn.STATUS_PHONE_REVIEW_FAIL;//电审未通过
    private static final String STATUS_DELAY_PAYBACK = CodeReturn.STATUS_DELAY_PAYBACK;//逾期还清
    private static final String STATUS_COM_PAYING = CodeReturn.STATUS_COM_PAYING;//放款中
    private static final String STATUS_COM_PAY_FAIL = CodeReturn.STATUS_COM_PAY_FAIL;//放款失败
    private static final String STATUS_PAYING = CodeReturn.STATUS_PAYING;// 还款中

    // banner图硬盘地址
    private static final String BANNER_DIR = PropertiesReaderUtil.read("third", "bannerDir");
    // banner图url
    private static final String BANNER_URL = PropertiesReaderUtil.read("third", "bannerBaseUrl");
    //图片硬盘地址
    private static final String PIC_DIR = PropertiesReaderUtil.read("third", "picDir");
    //风控模型接口URL
    private static final String RISK_URL = PropertiesReaderUtil.read("third", "riskUrl");
    //APP下载地址
    private static final String APP_URL = PropertiesReaderUtil.read("third", "appUrl");
    //是否是测试环境的开关
    private static final String isTest = PropertiesReaderUtil.read("third", "isTest");

    private static final String YM_SERVICECALL = PropertiesReaderUtil.read("third", "ym_servicecall");
    //是否是测试环境的开关
    private static final String YM_SERVICE_WORK_START = PropertiesReaderUtil.read("third", "ym_service_work_start");

    private static final String YM_SERVICE_WORK_END = PropertiesReaderUtil.read("third", "ym_service_work_end");


    //内部接口请求报文常量
    private static final String RISK_COMMAND = CodeReturn.RISK_COMMAND; //个人认证风控命令头
    private static final String PHONERISK_COMMAND = CodeReturn.PHONERISK_COMMAND;
    private static final String REPEAT_COMMAND = CodeReturn.REPEAT_COMMAND;
    private static final String RISK_USER = CodeReturn.RISK_USER;
    private static final String RISK_PASSWORD = CodeReturn.RISK_PASSWORD;//user与Password使用“金互行”分配的帐号与校验码
    private static final String RISK_LANG = CodeReturn.RISK_LANG;

    //Guid没有匹配时，返回的code
    private static final String GUID_WRONG = CodeReturn.GUID_WRONG;//返回系统繁忙,暂时用聚信立的系统繁忙状态码

    private static final String RISK_SUCCESS_CODE = CodeReturn.RISK_SUCCESS_CODE;
    //风控结果属于黑名单
    private static final String[] BLACK_CODE = CodeReturn.BLACK_CODE;
    //风控结果拒绝，不进黑名单
    private static final String[] REFUSE_CODE = CodeReturn.REFUSE_CODE;
    //系统繁忙码 参数错误  都重新再来一遍
    private static final String[] SYSTEM_CODE = CodeReturn.SYSTEM_CODE;
    //芝麻Code
    private static final String ZHIMA_REFUSE_CODE = "203";
    private static final String ZHIMA_TOURL_CODE = "202";
    private static final int ZHIMA_MIN_SROCE = 650;
    // 通讯录存储的base文件名
    private String contactsFileName = "ym_con";

    @Autowired
    private ProductChargeModelMapper productChargeModelMapper;
    @Autowired
    private ProductTermMapper productTermMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private BpmNodeMapper bpmNodeMapper;
    @Autowired
    private CardMapper cardMapper;
    @Autowired
    private PerBpmMapper perBpmMapper;
    @Autowired
    private BankMapper bankMapper;
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private BorrowListMapper borrowListMapper;
    @Autowired
    private PrivateMapper privateMapper;
    @Autowired
    private BpmMapper bpmMapper;
    @Autowired
    private ContactMapper contactMapper;
    @Autowired
    private CityMapper cityMapper;
    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private RiewerMapper riewerMapper;
    @Autowired
    private BankListMapper bankListMapper;
    @Autowired
    private AppVersionMapper appVersionMapper;
    @Autowired
    private MsgMapper msgMapper;
    @Autowired
    private SensetimeMapper sensetimeMapper;
    @Autowired
    private PerCouponMapper perCouponMapper;
    @Autowired
    private ImageMapper imageMapper;
    @Autowired
    private RequestMessageMapper requestMessageMapper;
    @Autowired
    private JedisCluster jedisCluster;
    @Autowired
    private CodeValueMapper codeValueMapper;
    @Autowired
    private JuxinliAPI juxinliAPI;
    @Autowired
    private BorrowManualMapper borrowManualMapper;
    @Autowired
    private ZhiMaCreditApi zhiMaCreditApi;
    @Autowired
    private ZhiMaMapper zhiMaMapper;
    @Autowired
    private AutoLoanServiceImpl autoLoanServiceImpl;
    @Autowired
    private YsbpayService ysbpayService;
    @Autowired
    private RiskAPI riskAPI;
    @Value("${productId}")
    private String productId;

    /**
     * 用户借款状态节点
     * @param per_id 用户ID
     * @return
     */
    public NoteResult getBorrStatus(String per_id) {
        // 构建结果对象NoteResult 默认 code=202 info=现在借款 (包括没有认证流程，没有借款记录，认证流程过期，身份证过期)
        NoteResult result = new NoteResult(NOW_BORROW_CODE, NOW_BORROW_INFO);
        result.setData("");

        Person person = personMapper.selectByPrimaryKey(Integer.valueOf(per_id));

        // 查询当前的借款表
        BorrowList borr = borrowListMapper.selectNow(Integer.valueOf(per_id));
        if (borr == null) {
            return result;
        }
        String borr_status = borr.getBorrStatus();
        // 判断当前的借款状态
        if (STATUS_PAY_BACK.equals(borr_status) || STATUS_CANCEL.equals(borr_status)
                || STATUS_DELAY_PAYBACK.equals(borr_status) || STATUS_PHONE_REVIEW_FAIL.equals(borr_status)) {
            // 已还清或已取消或逾期还清，返回现在借款
            // 检查是否是黑名单用户
            if ("Y".equals(person.getBlacklist())) {
                result.setInfo("黑名单");
                result.setData(8);// 黑名单
            }
            return result;
        }else if (STATUS_APLLY.equals(borr_status) || STATUS_REVIEW_FAIL.equals(borr_status)) {
            // 申请中,或者审核被拒绝
            // 检查是否是黑名单用户
            if ("Y".equals(person.getBlacklist())) {
                result.setCode(APPLY_CODE);
                result.setInfo("黑名单");
                result.setData(8);// 黑名单
                return result;
            }
            //新增人工审核
            BorrowManual borrowManual = borrowManualMapper.selectByBorrId(borr.getId());
            if (STATUS_APLLY.equals(borr_status) && borrowManual != null){
                //申请中，并且有人工审核记录
                result.setCode(APPLY_CODE);
                result.setInfo("人工审核");
                result.setData(9);
                return result;
            }

            // 查询是否认证完成
            NoteResult check = checkBpm(per_id);
            // 认证未完成 --->申请中(206--返回当前未认证节点)
            if (BPM_UNDO_CODE.equals(check.getCode())) {
                if (STATUS_REVIEW_FAIL.equals(borr_status) && !"r".equals(check.getData().toString())){
                    //新增审核拒绝的判断  如果审核拒绝超过了一定期限，可以重新认证了，把原借款改成取消 让用户重新借款
                    logger.info("O(∩_∩)OO(∩_∩)OO(∩_∩)OO(∩_∩)OO(∩_∩)OO(∩_∩)OO(∩_∩)O");
                    borr.setBorrStatus(STATUS_CANCEL);
                    int u = borrowListMapper.updateByPrimaryKey(borr);
                    if (u>0){
                        result.setCode(NOW_BORROW_CODE);
                        result.setInfo(NOW_BORROW_INFO);
                        return result;
                    }
                }
                // 返回当前未认证节点
                result.setCode(APPLY_CODE);
                result.setInfo(APPLY_INFO);
                result.setData(check.getData());// 未认证节点
                return result;
            } else if (BPM_FINISH_CODE.equals(check.getCode())) {

                if (STATUS_REVIEW_FAIL.equals(borr_status)){
                    //如果认证都完成了 借款状态是审核拒绝 那要判断时间是否超过一个月  超过一个月重新申请
                    Date update = borr.getUpdateDate();
                    long now = System.currentTimeMillis();
                    if (now - update.getTime() < 30 * 24 * 60 * 60 * 1000L) {//不够30天
                        // 现在距离被拒绝时间小于30天，返回拒绝黄框
                        result.setCode(APPLY_CODE);
                        result.setInfo(APPLY_CODE);
                        result.setData("r");
                        return result;
                    } else {
                        // 不小于30天 ，取消当前借款 返回现在借款
                        logger.info("^_^^_^^_^^_^^_^^_^^_^^_^^_^^_^^_^^_^^_^");
                        borr.setBorrStatus(STATUS_CANCEL);
                        int u = borrowListMapper.updateByPrimaryKey(borr);
                        if (u>0){
                            result.setCode(NOW_BORROW_CODE);
                            result.setInfo(NOW_BORROW_INFO);
                            return result;
                        }
                    }
                }

                // 如果认证完成 把借款改为待签约 进签约页面  bpmFinish
                NoteResult finish = bpmFinish(per_id);
                if (SUCCESS_CODE.equals(finish.getCode())){
                    //非人工审核
                    result.setCode(WAIT_SIGN_CODE);
                    result.setInfo(WAIT_SIGN_INFO);
                    result.setData(borr.getId());
                    return result;

                }else if (NOW_BORROW_CODE.equals(finish.getCode())){
                    //人工审核
                    result.setCode(APPLY_CODE);
                    result.setInfo("人工审核");
                    result.setData(9);
                    return result;
                }else{
                    //系统错误
                    return new NoteResult(FAIL_CODE, FAIL_INFO);
                }
            }else if (BPM_PHONE_CODE.equals(check.getCode()) || BPM_ZHIMA_CODE.equals(check.getCode())) {
                //通讯录节点 或者芝麻信用节点 直接返回 check结果
                return check;
            }
        } else if (STATUS_WAIT_SIGN.equals(borr_status)) {
            // 待签约
            // 查询认证情况
            NoteResult check = checkBpm(per_id);
            // 有认证过期，将借款状态改为申请中，返回申请中
            if (BPM_UNDO_CODE.equals(check.getCode())) {
                borr.setBorrStatus(STATUS_APLLY);
                borrowListMapper.updateByPrimaryKeySelective(borr);
                // 返回当前未认证节点
                result.setCode(APPLY_CODE);
                result.setInfo(APPLY_INFO);
                result.setData(check.getData());// 未认证节点
                return result;
            } else if (BPM_PHONE_CODE.equals(check.getCode())) {
                return check;
            } else {// 认证没有问题
                result.setCode(WAIT_SIGN_CODE);
                result.setInfo(WAIT_SIGN_INFO);
                result.setData(borr.getId());
                return result;
            }
        } else if (STATUS_SIGNED.equals(borr_status) || STATUS_COM_PAYING.equals(borr_status)
                || STATUS_COM_PAY_FAIL.equals(borr_status)) {
            // 已签约
            result.setCode(SIGNED_CODE);
            result.setInfo(SIGNED_INFO);
            return result;
        } else if (STATUS_TO_REPAY.equals(borr_status) || STATUS_LATE_REPAY.equals(borr_status)) {
            // 待还款、逾期未还
            result.setCode(TOREPAY_CODE);
            result.setInfo(TOREPAY_INFO);
            result.setData(borr.getId());
        } else if (STATUS_PAYING.equals(borr_status)) {
            // 2017.5.9更改 新加还款中状态
            result.setCode(CodeReturn.PAYING_CODE);
            result.setInfo("还款中");
        }
        return result;
    }

    /**
     * 获得产品金额
     * @param per_id 用户ID
     * @return
     */
    public NoteResult getProductsMoney(String per_id) {
        // 获得所有金额

        String redis = jedisCluster.get(RedisConst.PRODUCT_KEY);

        if (StringUtils.isEmpty(redis)) {
            // 缓存没有
            List<Long> data = productTermMapper.findProductMoney();

            // set redis
            getProdSetRedis();

            // 构建返回结果对象NoteResult
            NoteResult result = new NoteResult(FAIL_CODE, FAIL_INFO);
            if (!data.isEmpty()) {
                result.setCode(SUCCESS_CODE);
                result.setInfo(SUCCESS_INFO);
                result.setData(data);
            }
            return result;
        } else {
            // 缓存有

            JSONArray array = JSONArray.parseArray(redis);
            Set<Integer> data = new TreeSet<Integer>();

            for (Object anArray : array) {
                JSONObject obj = (JSONObject) anArray;
                Integer prodMoney = obj.getInteger("prodMoney");
                data.add(prodMoney);
            }
            // 构建返回结果对象NoteResult
            NoteResult result = new NoteResult(FAIL_CODE, FAIL_INFO);
            if (!data.isEmpty()) {
                result.setCode(SUCCESS_CODE);
                result.setInfo(SUCCESS_INFO);
                result.setData(data);
            }
            return result;

        }


    }

    /**
     * 获得产品天数
     * @param per_id 用户ID
     * @param money 借款金额
     * @return
     */
    public NoteResult getProductsday(String per_id,String money){
        //获得所有天数
        String redis = jedisCluster.get(RedisConst.PRODUCT_KEY);

        if (StringUtils.isEmpty(redis)) {
            // 缓存没有
            List<Integer> data = productTermMapper.findProductDay(Long.valueOf(money));

            getProdSetRedis();

            // 构建返回结果对象NoteResult
            NoteResult result = new NoteResult(FAIL_CODE, FAIL_INFO);
            if (!data.isEmpty()) {
                result.setCode(SUCCESS_CODE);
                result.setInfo(SUCCESS_INFO);
                result.setData(data);
            }
            return result;
        } else {
            // 缓存有

            JSONArray array = JSONArray.parseArray(redis);
            Set<Integer> data = new HashSet<Integer>();

            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = (JSONObject) array.get(i);
                Integer prodDay = obj.getInteger("prodDay");
                data.add(prodDay);
            }
            // 构建返回结果对象NoteResult
            NoteResult result = new NoteResult(FAIL_CODE, FAIL_INFO);
            if (!data.isEmpty()) {
                result.setCode(SUCCESS_CODE);
                result.setInfo(SUCCESS_INFO);
                result.setData(data);
            }
            return result;

        }

    }

    /**
     * 选择产品金额和天数,获得产品所有费用
     * @param per_id 用户ID
     * @return
     */
    public NoteResult getProductCharge(Integer productId, String per_id,String money,String day){
        //构建返回结果对象NoteResult
        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);
        try {
            Integer product_id;
            if(Detect.isPositive(productId)){
                //如果有值直接赋值
                product_id = productId;
            }else {
                //根据金额和天数查询产品ID
                product_id = productTermMapper.findProductId(money, day);
            }
            //根据产品ID查询产品期数表，获得产品金额，天数，利息(利率*金额*天数)
            ProductTerm term = productTermMapper.selectByProductId(product_id);
            //借款金额
            BigDecimal amount = term.getMaximumAmount();
            //应还总额
            double total = amount.doubleValue();
            //利率
            BigDecimal rate = term.getMonthlyRate();
            //利息=借款金额*利率
            double mul = amount.multiply(rate).doubleValue();
            //总金额加上利息
            total += mul;
            String interest = String.format("%.2f", mul);
            JSONObject data = new JSONObject();
            data.put("product_id", String.valueOf(product_id));
            data.put("interest",interest);
            //根据产品ID查询产品收费表，获得每个产品所有收费名称及收费金额
            List<ProductChargeModel> charges =
                    productChargeModelMapper.selectByProductId(Integer.valueOf(product_id));

            for(ProductChargeModel charge : charges){
                String chargeName = charge.getChargeName();
                BigDecimal chargeAmount = charge.getAmount();
                //总金额加上各种费用
                //-----------2017.3月改动，总金额=金额+利息 --------------
                //total += chargeAmount.doubleValue();
                String chargeFee = String.format("%.2f",chargeAmount.doubleValue());
                if("letter".equals(chargeName)){
                    data.put("review",chargeFee);
                }
                if ("managecost".equals(chargeName)){
                    data.put("plat",chargeFee);
                }

            }

            //费用后置 ：应还金额=本金+利息+快速信审费+平台管理费-优惠券金额
            if(product_id > 4 ){
                total = amount.doubleValue() + mul + data.getDoubleValue("review") + data.getDoubleValue("plat");
            }
            data.put("total", String.format("%.2f", total));
            List<JSONObject> coupons = new ArrayList<JSONObject>();
            if(per_id!=null && !"".equals(per_id)){
                //根据per_id,product_id,使用状态查询用户可用的优惠券
                List<PerCoupon> perCoupons = perCouponMapper.selectByPerProductStatus(Integer.valueOf(per_id), productId, "1");

                for(PerCoupon coupon:perCoupons){
                    JSONObject couponJSON = new JSONObject();
                    double couponAmount = Double.valueOf(coupon.getAmount());
                    couponJSON.put("id", coupon.getId());
                    couponJSON.put("name", coupon.getCouponName());
                    couponJSON.put("amount", String.format("%.2f",couponAmount));
                    coupons.add(couponJSON);
                }

            }
            data.put("coupons", coupons);
            if(!data.isEmpty()){
                result.setCode(SUCCESS_CODE);
                result.setInfo(SUCCESS_INFO);
                result.setData(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new NoteResult(FAIL_CODE,FAIL_INFO);
        }
        return result;
    }

    /**
     * 根据金额和天数获得产品id
     * @param per_id 用户ID
     * @param money 产品金额
     * @param day 产品天数
     * @return
     */
    public NoteResult getProductId(String per_id,String money,String day){

        // 构建返回结果对象NoteResult
        NoteResult result = new NoteResult(FAIL_CODE, FAIL_INFO);

        //先从缓存中获取产品信息
        String redis = jedisCluster.get(RedisConst.PRODUCT_KEY);

        if (StringUtils.isEmpty(redis)) {
            //缓存中没有产品信息
            Integer productId = productTermMapper.findProductId(money, day);

            getProdSetRedis();

            if (productId != null) {
                result.setCode(SUCCESS_CODE);
                result.setInfo(SUCCESS_INFO);
                result.setData(String.valueOf(productId));
            }
            return result;

        } else {
            // redis里有
            JSONArray array = JSONArray.parseArray(redis);

            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = (JSONObject)array.get(i);
                double objMoney = obj.getDoubleValue("prodMoney");
                int objDay = obj.getIntValue("prodDay");
                if (objMoney == Double.valueOf(money) && objDay == Double.valueOf(day)) {
                    // 命中 返回
                    result.setCode(SUCCESS_CODE);
                    result.setInfo(SUCCESS_INFO);
                    result.setData(obj.getString("id"));
                }

            }

            return result;
        }

    }

    /**
     * 生成借款记录
     * @param per_id 用户ID
     * @param product_id 产品ID
     * @return
     */
    public NoteResult borrowProduct(String per_id, String product_id, String plan_repay, String borr_amount,String day,String coupon_id) {
        // 幂等操作
        if (StringUtils.isEmpty(jedisCluster.get(RedisConst.BP_KEY + per_id))){
            String setnx = jedisCluster.set(RedisConst.BP_KEY + per_id, per_id, "NX", "EX", 60 );
            if(!"OK".equals(setnx)) {
                NoteResult result = new NoteResult(FAIL_CODE, FAIL_INFO);

                result.setCode(FAIL_CODE);
                result.setInfo("1分钟内无法重复借款，请稍后");
                logger.error("borrowProduct直接返回，重复数据per_id" + per_id);
                return result;
            }
        }else{
            NoteResult result = new NoteResult(FAIL_CODE, FAIL_INFO);

            result.setCode(FAIL_CODE);
            result.setInfo("1分钟内无法重复借款，请稍后");
            logger.error("borrowProduct直接返回，重复数据per_id" + per_id);
            return result;
        }
        //构建结果对象 默认 201 失败
        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);
        //检查金额天数是否和id匹配
        NoteResult checkPro = getProductId(per_id, borr_amount, day);
        String checkProId = (String)checkPro.getData();
        if(!checkProId.equals(product_id)){//不匹配
            result.setCode("202");
            result.setInfo("产品金额与id不匹配");
            return result;
        }
        //新建borr时先检查是否有申请中的借款
        Integer haveBorrowing = borrowListMapper.selectDoing(Integer.valueOf(per_id));
        if(haveBorrowing>0){//有电审未通过，已结清，已取消，已逾期结清之外的借款
            result.setCode(FAIL_CODE);
            result.setInfo("有正在处理中的借款，不允许重复借款！");
            return result;
        }

        //新建BorrowList对象
        BorrowList newBorr = new BorrowList();
        newBorr.setPerId(Integer.valueOf(per_id));
        newBorr.setProdId(Integer.valueOf(product_id));
        newBorr.setAskborrDate(new Date());
        //--------------2017.3月更改  最原始的500  1000 不从前台取，从产品表中取
        ProductTerm term = productTermMapper.selectByProductId(Integer.valueOf(product_id));
        borr_amount = String.valueOf(term.getMaximumAmount());

        newBorr.setBorrStatus(STATUS_APLLY);

        //----------------2017.3月改动   borr_amount 为金额-平台-信审+优惠券
        //根据产品ID查询产品收费表，获得每个产品所有收费名称及收费金额
        List<ProductChargeModel> charges =
                productChargeModelMapper.selectByProductId(Integer.valueOf(product_id));
        double review = 0.0;
        double plat = 0.0;
        for(ProductChargeModel charge : charges){
            String chargeName = charge.getChargeName();
            BigDecimal chargeAmount = charge.getAmount();
            //总金额加上各种费用
            //-----------2017.3月改动，总金额=金额+利息   不再包括各种费用 --------------
            //total += chargeAmount.doubleValue();
            double chargeFee = chargeAmount.doubleValue();
            if("letter".equals(chargeName)){
                review = chargeFee;
            }
            if ("managecost".equals(chargeName)){
                plat = chargeFee;
            }

        }
        double amount = Double.valueOf(borr_amount);
        amount = amount - review - plat ;
        //利率
        BigDecimal rate = term.getMonthlyRate();
        //利息=产品金额*利率
        double mul = term.getMaximumAmount().multiply(rate).doubleValue();
        //2017.3月更新  plan_repay不取前端数据，自己算
        double plan = term.getMaximumAmount().doubleValue()+mul;

        //费用后置修改 2017年6月12日
        //1、实际放款金额= 本金；
        //2、应还金额=本金+利息+快速信审费+平台管理费-优惠券金额
        //3、违约金，罚息收取分别由本金作为基数计算
        if(Integer.valueOf(product_id) > 4){
            amount = term.getMaximumAmount().doubleValue();
            plan = term.getMaximumAmount().doubleValue() + mul + review + plat;
        }

        if(!"0".equals(coupon_id)){//使用了优惠券
            PerCoupon coupon = perCouponMapper.selectByPrimaryKey(Integer.valueOf(coupon_id));
            coupon.setStatus("2");
            perCouponMapper.updateByPrimaryKeySelective(coupon);
            //--------------2017.3月更改  如果使用有优惠券  borr_amount要加上优惠券的金额 相当于放款金额增加了优惠券金额
            borr_amount = String.valueOf(Double.valueOf(amount) + Double.valueOf(coupon.getAmount()));
            //减去优惠券价钱，费用后置
            if(Integer.valueOf(product_id) > 4){
                plan = plan - Double.valueOf(coupon.getAmount());
            } else {
                amount = Double.valueOf(borr_amount);
            }

        }
        newBorr.setPerCouponId(coupon_id);

        //费用后置修改
        newBorr.setPlanRepay(String.format("%.2f", plan));
        newBorr.setBorrAmount(String.valueOf(amount));
        newBorr.setBorrNum(BorrNum_util.createBorrNum());
        int i = borrowListMapper.insertSelective(newBorr);
        //新建借款记录成功
        if(i>0){
            result.setCode(SUCCESS_CODE);
            result.setInfo(SUCCESS_INFO);
        }

        return result;
    }

    /**
     * 检查用户是否认证完成
     * @param per_id 用户ID
     * @return
     */
    public NoteResult checkBpm(String per_id) {
        // 构建结果对象
        NoteResult result = new NoteResult();

        // 所有节点
        int[] nodeIds = {1, 2, 3, 4, 5, 6};
        // 设定一个开关 用来标记是否有状态为提交中的节点 目前暂时针对聚信立
        boolean haveNS004 = false;

        // 时间参数
        long now = System.currentTimeMillis();
        long limit30 = 30 * 24 * 60 * 60 * 1000L;// 30天有效期
        long limit60 = 60 * 24 * 60 * 60 * 1000L;// 60天有效期

        // 先查出当前person
        Person p = personMapper.selectByPrimaryKey(Integer.valueOf(per_id));

        // 1.判断黑名单
        if ("Y".equals(p.getBlacklist())) {
            result.setCode(BPM_UNDO_CODE);
            result.setInfo("黑名单");
            result.setData("8");// 黑名单
            return result;
        }

        // 不为黑名单 查出用户所有节点
        Map<String, String> nodeMap = initNode(p);
        // 遍历所有节点
        for (String key : nodeMap.keySet()) {

            BpmNode node = JSONObject.toJavaObject(JSONObject.parseObject(nodeMap.get(key)), BpmNode.class);

            //节点一
            if ("1".equals(key)) {
                if (STATUS_BPM_N.equals(node.getNodeStatus())) {
                    // 没有或者节点状态为未认证，则用户没有上传身份证正面信息，返回认证身份证正面code
                    result.setCode(BPM_UNDO_CODE);
                    result.setInfo(BPM_UNDO_INFO);
                    result.setData("1");
                    return result;
                }
                // 没返回 从数组中移除该节点
                int position = Arrays.binarySearch(nodeIds, 1);
                nodeIds = ArrayUtils.remove(nodeIds, position);
                // System.out.println("当前数组元素:" + Arrays.toString(nodeIds));
                continue;
            }

            //节点二
            if ("2".equals(key)) {
                if (STATUS_BPM_N.equals(node.getNodeStatus())) {
                    // 没有或者节点状态为未认证，则用户没有上传身份证反面信息，返回认证身份证反面code
                    result.setCode(BPM_UNDO_CODE);
                    result.setInfo(BPM_UNDO_INFO);
                    result.setData("2");
                    return result;
                }


                Card c = cardMapper.selectByPerId(Integer.valueOf(per_id));
                if ((c.getEndDate() != null) && (now- 24*60*60*1000 - c.getEndDate().getTime() > 0L)) {
                    node.setNodeStatus("NS001");
                    node.setUpdateDate(new Date());
                    BpmNode node1 = findNode(Integer.valueOf(per_id), 1);
                    node1.setNodeStatus("NS001");
                    node1.setUpdateDate(new Date());
                    this.bpmNodeMapper.updateByPrimaryKeySelective(node);
                    this.bpmNodeMapper.updateByPrimaryKeySelective(node1);
                    jedisHset(per_id,RedisConst.NODE_KEY + per_id, "1",JSONObject.toJSONString(node1));
                    jedisHset(per_id,RedisConst.NODE_KEY + per_id, "2",JSONObject.toJSONString(node));
                    result.setCode("207");
                    result.setInfo("未认证");
                    result.setData("1");
                    return result;
                }

                // 没返回 从数组中移除该节点
                int position2 = Arrays.binarySearch(nodeIds, 2);
                nodeIds = ArrayUtils.remove(nodeIds, position2);
                // System.out.println("当前数组元素:" + Arrays.toString(nodeIds));
                continue;
            }

            //节点2.5
            if ("2.5".equals(key)) {

                if (STATUS_BPM_Y.equals(node.getNodeStatus()) && now - node.getUpdateDate().getTime()<limit30) {//认证状态为成功  并且更新时间少于1个月

                    continue;
                }else{
                    result.setCode(BPM_ZHIMA_CODE);
                    result.setInfo(BPM_UNDO_INFO);
                    return result;
                }
            }


            //节点三
            if ("3".equals(key)) {
                if (STATUS_BPM_N.equals(node.getNodeStatus())) {
                    // 没有或者节点状态为未认证，则用户没有个人认证，返回个人认证code
                    result.setCode(BPM_UNDO_CODE);
                    result.setInfo(BPM_UNDO_INFO);
                    result.setData("3");
                    return result;
                } else if (STATUS_BPM_FAIL.equals(node.getNodeStatus())) {
                    // 节点状态为拒绝，检查认证时间
                    Date checkDate = node.getUpdateDate();
                    if (now - checkDate.getTime() < limit30) {
                        // 现在距离被拒绝时间小于30天，返回拒绝黄框
                        result.setCode(BPM_UNDO_CODE);
                        result.setInfo(BPM_UNDO_INFO);
                        result.setData("r");
                        return result;
                    } else {
                        // 不小于30天 ，返回个人认证code
                        result.setCode(BPM_UNDO_CODE);
                        result.setInfo(BPM_UNDO_INFO);
                        result.setData("3");
                        return result;
                    }

                }

                // 没返回 从数组中移除该节点
                int position3 = Arrays.binarySearch(nodeIds, 3);
                nodeIds = ArrayUtils.remove(nodeIds, position3);
                // System.out.println("当前数组元素:" + Arrays.toString(nodeIds));
                continue;
            }

            //节点3.5
            if ("3.5".equals(key)) {
                if (STATUS_BPM_Y.equals(node.getNodeStatus())) {
                    //通讯录节点 如果过期 返回通讯录过期code
                    Date checkDate = node.getUpdateDate();
                    if (now - checkDate.getTime() > limit30) {
                        result.setCode(BPM_PHONE_CODE);
                        result.setInfo(BPM_PHONE_INFO);
                        return result;
                    }

                }else if (STATUS_BPM_FAIL.equals(node.getNodeStatus())){
                    Date checkDate = node.getUpdateDate();
                    if (now - checkDate.getTime() < limit30) {
                        // 现在距离被拒绝时间小于30天，返回拒绝黄框
                        result.setCode(BPM_UNDO_CODE);
                        result.setInfo(BPM_UNDO_INFO);
                        result.setData("r");
                        return result;
                    }else{
                        result.setCode(BPM_PHONE_CODE);
                        result.setInfo(BPM_PHONE_INFO);
                        return result;
                    }
                }
                continue;
            }

            //节点4
            if ("4".equals(key)) {
                if (STATUS_BPM_N.equals(node.getNodeStatus())) {
                    // 没有或者节点状态为未认证，则用户没有个人认证，返回人脸识别认证code
                    result.setCode(BPM_UNDO_CODE);
                    result.setInfo(BPM_UNDO_INFO);
                    result.setData("4");
                    return result;
                } else if (STATUS_BPM_FAIL.equals(node.getNodeStatus())) {
                    // 节点状态为拒绝，检查认证时间
                    Date checkDate = node.getUpdateDate();
                    if (now - checkDate.getTime() < limit30) {
                        // 现在距离被拒绝时间小于30天，返回拒绝黄框
                        result.setCode(BPM_UNDO_CODE);
                        result.setInfo(BPM_UNDO_INFO);
                        result.setData("r");
                        return result;
                    } else {
                        // 不小于30天 ，返回个人认证code
                        result.setCode(BPM_UNDO_CODE);
                        result.setInfo(BPM_UNDO_INFO);
                        result.setData("4");
                        return result;
                    }

                }
                // 没返回 从数组中移除该节点
                int position4 = Arrays.binarySearch(nodeIds, 4);
                nodeIds = ArrayUtils.remove(nodeIds, position4);
                // System.out.println("当前数组元素:" + Arrays.toString(nodeIds));
                continue;
            }

            //节点5
            if ("5".equals(key)) {
                if (STATUS_BPM_N.equals(node.getNodeStatus())) {
                    // 没有或者节点状态为未认证，则用户没有手机认证，返回手机认证code
                    result.setCode(BPM_UNDO_CODE);
                    result.setInfo(BPM_UNDO_INFO);
                    result.setData("5");
                    return result;
                } else if (STATUS_BPM_FAIL.equals(node.getNodeStatus())
                        || STATUS_BPM_FAIL_B.equals(node.getNodeStatus())) {
                    // 节点状态为拒绝或黑名单 检查认证时间 小于一个月返回黄框 超过一个月可重新认证
                    Date checkDate = node.getUpdateDate();
                    if (now - checkDate.getTime() < limit30) {
                        result.setCode(BPM_UNDO_CODE);
                        result.setInfo(BPM_UNDO_INFO);
                        result.setData("r");
                        return result;
                    } else {
                        result.setCode(BPM_UNDO_CODE);
                        result.setInfo(BPM_UNDO_INFO);
                        result.setData("5");
                        return result;
                    }
                } else if (STATUS_BPM_Y.equals(node.getNodeStatus())) {
                    // 节点状态为通过 检查认证时间 超过两个月要重新认证
                    Date checkDate = node.getUpdateDate();
                    if (now - checkDate.getTime() > limit60) {
                        result.setCode(BPM_UNDO_CODE);
                        result.setInfo(BPM_UNDO_INFO);
                        result.setData("5");
                        return result;
                    }
                } else if (STATUS_BPM_UP.equals(node.getNodeStatus())) {
                    // 节点状态为提交中 还没拿到聚信立结果 将开关改为true
                    haveNS004 = true;
                }
                // 没返回 从数组中移除该节点
                int position5 = Arrays.binarySearch(nodeIds, 5);
                nodeIds = ArrayUtils.remove(nodeIds, position5);
                // System.out.println("当前数组元素:" + Arrays.toString(nodeIds));
                continue;
            }

            //节点6
            if ("6".equals(key)) {
                if (STATUS_BPM_N.equals(node.getNodeStatus())) {
                    // 没有或者节点状态为未认证，则用户没有手机认证，返回手机认证code
                    result.setCode(BPM_UNDO_CODE);
                    result.setInfo(BPM_UNDO_INFO);
                    result.setData("6");
                    return result;
                } else if (STATUS_BPM_FAIL.equals(node.getNodeStatus())) {
                    // 节点状态为拒绝，检查认证时间
                    Date checkDate = node.getUpdateDate();
                    if (now - checkDate.getTime() < limit30) {
                        // 现在距离被拒绝时间小于30天，返回拒绝黄框
                        result.setCode(BPM_UNDO_CODE);
                        result.setInfo(BPM_UNDO_INFO);
                        result.setData("r");
                        return result;
                    } else {
                        // 不小于30天 ，返回个人认证code
                        result.setCode(BPM_UNDO_CODE);
                        result.setInfo(BPM_UNDO_INFO);
                        result.setData("6");
                        return result;
                    }

                }
                // 没返回 从数组中移除该节点
                int position6 = Arrays.binarySearch(nodeIds, 6);
                nodeIds = ArrayUtils.remove(nodeIds, position6);
                // System.out.println("当前数组元素:" + Arrays.toString(nodeIds));
                continue;
            }
        }

        // 没有返回 检查数组中元素个数
        int lenth = nodeIds.length;
        logger.error("当前per_id:" + per_id + "，当前数组元素:" + Arrays.toString(nodeIds));
        if (lenth == 0) {

            //此为芝麻信用临时解决办法 具体逻辑 再详细考虑考虑  如果
            if (!nodeMap.containsKey("2.5")){
                result.setCode(BPM_ZHIMA_CODE);
                result.setInfo(BPM_UNDO_INFO);
                return result;
            }

            // 分为两种情况  聚信立有结果/没有结果
            // 第一种：没有结果 节点5为提交 其余为通过
            if (haveNS004) {
                result.setCode(BPM_UNDO_CODE);
                result.setInfo(BPM_UNDO_INFO);
                result.setData("0");
                return result;
            }
            // 第二种：聚信立有结果，所有节点通过，从数组中移除
            result.setCode(BPM_FINISH_CODE);
            result.setInfo(BPM_FINISH_INFO);

            return result;
        } else {
            // 有节点未移除 还有未认证完成的节点 取第一个元素

            result.setCode(BPM_UNDO_CODE);
            result.setInfo(BPM_UNDO_INFO);
            result.setData(nodeIds[0]);
            //临时打个补丁   如果 返回的是3  却没有2.5节点（芝麻信用） 则要返回认证芝麻信用的code
            //此为临时解决办法 具体逻辑 再详细考虑考虑
            if (nodeIds[0]>2 && !nodeMap.containsKey("2.5")){
                result.setCode(BPM_ZHIMA_CODE);
                result.setInfo(BPM_UNDO_INFO);
            }
            return result;
        }
    }

    /**
     * 新建认证流程  2017.09.18 不再调用此方法
     */
    public NoteResult insertPerBpm(String per_id){
        // 构建结果对象，默认 201 失败
        NoteResult result = new NoteResult(FAIL_CODE, FAIL_INFO);
        // 查出父节点为0的节点
        Bpm bpm = bpmMapper.selectByParent(0);
        // 新增流程明细
        BpmNode bpmNode = new BpmNode();
        bpmNode.setPerId(Integer.valueOf(per_id));
        bpmNode.setNodeId(bpm.getId());
        bpmNode.setNodeStatus(STATUS_BPM_N);
        bpmNode.setDescription("当前流程的第一个认证记录");
        int j = bpmNodeMapper.insertSelective(bpmNode);
        if (j > 0) {
            result.setCode(SUCCESS_CODE);
            result.setInfo(SUCCESS_INFO);
        }

        return result;
    }

    /**
     * 插入身份证正面信息
     * @return
     */
    public NoteResult insertCardInfoz(String per_id,String card_num, String name,
                                      String sex, String nation,String birthday,
                                      String address, String card_byte,String head_byte,String description) {
        // 构建结果对象，默认201 失败
        NoteResult result = new NoteResult(FAIL_CODE, FAIL_INFO);
        long time = System.currentTimeMillis();
        try {
            // 验证用户是否已经上传过身份证
            Card perCard = cardMapper.selectByPerId(Integer.valueOf(per_id));
            if (perCard != null && !card_num.equals(perCard.getCardNum())) {
                // 如果用户已经上传 并且跟当前上传的身份证号不一致 返回失败
                result.setInfo("请上传本人身份证");
                return result;
            }
            // 验证库里身份证是否对应该per_id
            Card c = cardMapper.selectByCardNo(card_num);
            if (c != null) {
                boolean samePerson = c.getPerId().equals(Integer.valueOf(per_id));
                // System.out.println("是否同一个人" + samePerson);
                if (!samePerson) {
                    // 此身份证库中已有，但不是这个per_id下的
                    result.setInfo("该身份证已认证，无法重复认证");
                    return result;
                } else {
                    // 库中已有，是这个per_id下的
                    // image表中插入正面数据
                    Image cardz = new Image();
                    cardz.setImageType("1");
                    cardz.setImageFormat("jpg");
                    // 2017.4.7更新 存硬盘
                    // cardz.setImage(card_byte);
                    String pathz = time + "id" + card_num + "z.jpg";
                    boolean savez = GenerateImage(card_byte, PIC_DIR + pathz);
                    if (!savez) {// 如果存图片失败 直接返回错误
                        result.setCode(FAIL_CODE);
                        result.setInfo(FAIL_INFO);
                        return result;
                    }
                    cardz.setImageUrl(pathz);

                    int i = imageMapper.insert(cardz);
                    // image表中插入头像数据
                    Image head = new Image();
                    head.setImageType("3");// 头像照
                    head.setImageFormat("jpg");
                    // 2017.4.7更新 存硬盘
                    // head.setImage(head_byte);
                    String pathd = time + "id" + card_num + "d.jpg";
                    boolean saved = GenerateImage(head_byte, PIC_DIR + pathd);
                    if (!saved) {// 如果存图片失败 直接返回错误
                        result.setCode(FAIL_CODE);
                        result.setInfo(FAIL_INFO);
                        return result;
                    }
                    head.setImageUrl(pathd);
                    int j = imageMapper.insert(head);
                    if (i > 0 && j > 0) {// 插入image成功
                        // 更改原来的正面身份证信息
                        c.setPerId(Integer.valueOf(per_id));
                        c.setCardNum(card_num);
                        c.setName(name);
                        c.setSex(sex);
                        c.setNation(nation);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = sdf.parse(birthday);
                        c.setBirthday(date);
                        c.setAddress(address);
                        c.setCardPhotoz(cardz.getId().toString());
                        c.setCardPhotod(head.getId().toString());

                        int k = cardMapper.updateByPrimaryKeySelective(c);
                        if (k > 0) {// 插入身份证正面信息成功

                            // 新增认证流程节点明细
                            result = createBpmNode(per_id, "1", STATUS_BPM_Y, "");

                        }
                    }
                }
            } else {
                // image表中插入正面数据
                Image cardz = new Image();
                cardz.setImageType("1");
                cardz.setImageFormat("jpg");
                // 2017.4.7更新 存硬盘
                // cardz.setImage(card_byte);
                String pathz = time + "id" + card_num + "z.jpg";
                boolean savez = GenerateImage(card_byte, PIC_DIR + pathz);
                if (!savez) {// 如果存图片失败 直接返回错误
                    result.setCode(FAIL_CODE);
                    result.setInfo(FAIL_INFO);
                    return result;
                }
                cardz.setImageUrl(pathz);

                int i = imageMapper.insert(cardz);
                // image表中插入头像数据
                Image head = new Image();
                head.setImageType("3");// 头像照
                head.setImageFormat("jpg");
                // 2017.4.7更新 存硬盘
                // head.setImage(head_byte);
                String pathd = time + "id" + card_num + "d.jpg";
                boolean saved = GenerateImage(head_byte, PIC_DIR + pathd);
                if (!saved) {// 如果存图片失败 直接返回错误
                    result.setCode(FAIL_CODE);
                    result.setInfo(FAIL_INFO);
                    return result;
                }
                head.setImageUrl(pathd);
                int j = imageMapper.insert(head);

                if (i > 0 && j > 0) {// 插入image成功
                    // card表中添加正面身份证信息
                    Card card = new Card();
                    card.setPerId(Integer.valueOf(per_id));
                    card.setCardNum(card_num);
                    card.setName(name);
                    card.setSex(sex);
                    card.setNation(nation);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = sdf.parse(birthday);
                    card.setBirthday(date);
                    card.setAddress(address);
                    card.setCardPhotoz(cardz.getId().toString());
                    card.setCardPhotod(head.getId().toString());

                    int k = cardMapper.insertSelective(card);
                    if (k > 0) {// 插入身份证正面信息成功
                        // 新增认证流程节点明细
                        result = createBpmNode(per_id, "1", STATUS_BPM_Y, "");
                    }
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
            result.setCode(FAIL_CODE);
            result.setInfo(FAIL_INFO);
            return result;
        }
        return result;
    }

    /**
     * 插入身份证反面信息
     * @return
     */
    public NoteResult insertCardInfof(String per_id,String office, String start_date,
                                      String end_date,String card_byte, String description) {
        //构建结果对象，默认 201 失败
        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);
        long time = System.currentTimeMillis();
        try {
            //card表中添加反面身份证信息
            Card card = cardMapper.selectByPerId(Integer.valueOf(per_id));
            String card_num = card.getCardNum();

            //image表中插入数据
            Image cardf = new Image();
            cardf.setImageType("2");
            cardf.setImageFormat("jpg");
            //2017.4.7更新  存硬盘
            //cardf.setImage(card_byte);
            String path = time+"id"+card_num+"f.jpg";
            boolean save = GenerateImage(card_byte,PIC_DIR+path);
            if(!save){//如果存图片失败  直接返回错误
                result.setCode(FAIL_CODE);
                result.setInfo(FAIL_INFO);
                return result;
            }
            cardf.setImageUrl(path);

            int i = imageMapper.insert(cardf);
            if(i>0){
                card.setOffice(office);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                card.setStartDate(sdf.parse(start_date));
                if(end_date != null && !"".equals(end_date)){
                    // 2017.3月 如果身份证过期，节点不通过
                    Date now = new Date();
                    // 如果是永久身份证 存
                    if ("永久".equals(end_date.trim()) || "长期".equals(end_date.trim())) {
                        card.setEndDate(sdf.parse("2099-12-12"));
                    } else if (now.getTime() - 24*60*60*1000 > sdf.parse(end_date).getTime()) {// 已经过期
                        result.setCode(FAIL_CODE);
                        result.setInfo("该身份证已过期");
                        return result;
                    } else {
                        card.setEndDate(sdf.parse(end_date));
                    }
                }
                card.setCardPhotof(cardf.getId().toString());

                int j = cardMapper.updateByPrimaryKeySelective(card);

                if(j>0){//插入身份证反面信息成功
                    //新增认证流程节点明细
                    result = createBpmNode(per_id, "2",STATUS_BPM_Y,"");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(FAIL_CODE);
            result.setInfo(FAIL_INFO);
            return result;
        }

        return result;
    }

    /**
     * 插入个人信息
     * @param per_id 用户ID @param qq_num QQ号  @param email 邮箱
     * @param usuallyaddress 常用地址 @param education 学历
     * @param marry 婚姻状况  @param getchild 生育状况
     * @param profession 职业 @param monthlypay 月薪
     * @param business 单位名 @param busi_province 单位所在省
     * @param busi_city 单位所在市 @param busi_address 单位详细地址
     * @param busi_phone 单位电话 @param relatives 亲属关系
     * @param relatives_name 亲属名字 @param rela_phone 亲属联系方式
     * @param society 社会关系 @param soci_phone 社会联系方式
     * @param society_name 社会关系名字
     * @return
     */
    public NoteResult insertPrivateInfo(String per_id, String qq_num, String email, String usuallyaddress,
                                        String education, String marry, String getchild, String profession, String monthlypay, String business,
                                        String busi_province, String busi_city, String busi_address, String busi_phone, String relatives,
                                        String relatives_name, String rela_phone, String society, String soci_phone, String society_name) {

        //构建结果对象，默认 201 失败
        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);

        // 幂等操作
        if (StringUtils.isEmpty(jedisCluster.get(RedisConst.PR_KEY + per_id))){
            String setnx = jedisCluster.set(RedisConst.PR_KEY + per_id, per_id, "NX", "EX", 60 * 2);
            if(!"OK".equals(setnx)) {
                logger.error("直接返回，重复数据private表");
                result.setInfo("数据重复！");
                return result;
            }
        }else{
            logger.error("直接返回，重复数据private表");
            result.setInfo("数据重复！");
            return result;
        }

        // 2017-07-03 选择联系人超长 从后面截取10位
        if (relatives_name.length() > 10) {
            relatives_name = relatives_name.substring(relatives_name.length() - 10, relatives_name.length() - 1);
        }

        if (society_name.length() > 10) {
            society_name = society_name.substring(society_name.length() - 10, society_name.length() - 1);
        }



        //检查该per_id下是否有个人信息
        Private p = privateMapper.selectByPerId(Integer.valueOf(per_id));
        int i;
        if(p == null){
            //插入个人信息
            p = new Private();
            p.setPerId(Integer.valueOf(per_id));
            p.setQqNum(qq_num);
            p.setEmail(email);
            p.setUsuallyaddress(usuallyaddress);
            p.setEducation(education);
            p.setMarry(marry);
            p.setGetchild(getchild);
            p.setProfession(profession);
            p.setMonthlypay(monthlypay);
            p.setBusiness(business);
            p.setBusiProvince(busi_province);
            p.setBusiCity(busi_city);
            p.setBusiAddress(busi_address);
            p.setBusiPhone(busi_phone);
            p.setRelatives(relatives);

            p.setRelativesName(relatives_name);
            p.setRelaPhone(rela_phone);
            p.setSociety(society);
            p.setSociPhone(soci_phone);

            p.setSocietyName(society_name);

            i = privateMapper.insertSelective(p);
        }else{

            p.setPerId(Integer.valueOf(per_id));
            p.setQqNum(qq_num);
            p.setEmail(email);
            p.setUsuallyaddress(usuallyaddress);
            p.setEducation(education);
            p.setMarry(marry);
            p.setGetchild(getchild);
            p.setProfession(profession);
            p.setMonthlypay(monthlypay);
            p.setBusiness(business);
            p.setBusiProvince(busi_province);
            p.setBusiCity(busi_city);
            p.setBusiAddress(busi_address);
            p.setBusiPhone(busi_phone);
            p.setRelatives(relatives);
            p.setRelativesName(relatives_name);
            p.setRelaPhone(rela_phone);
            p.setSociety(society);
            p.setSociPhone(soci_phone);
            p.setSocietyName(society_name);
            p.setUpdateDate(new Date());

            i = privateMapper.updateByPrimaryKey(p);
        }

        if(i>0){//插入个人信息成功    走风控 模型
//-----------------------------正式部分--------------------------------------------------------------
//           List<Object> ContactList = new ArrayList<Object>();
//           List<Contact> list = contactMapper.selectAllByPerId(Integer.valueOf(per_id));
//           for(Contact c : list){
//               JSONObject one = new JSONObject();
//               one.put("name", c.getContactName());
//               one.put("phone", c.getContactNum());
//               ContactList.add(one);
//           }
            JSONArray ContactList = JSONArray.parseArray(getContacts(per_id));
            result = javaCheckRisk(per_id,email);
//-----------------------------测试，不走风控----------------------------------------------------------
//            result = createBpmNode(per_id, "3", STATUS_BPM_Y,"");
//-------------------------------------------------------------------------------------------------
        }
        return result;
    }

    /**
     * 获取自己的手机号，及运营商展示
     * @param per_id 用户ID
     * @return
     */
    public NoteResult getPhoneInfo(String per_id) {
        //构建结果对象，默认 201 失败
        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);
        Person person = personMapper.selectByPrimaryKey(Integer.valueOf(per_id));
        if(person!=null){
            result.setCode(SUCCESS_CODE);
            result.setInfo(SUCCESS_INFO);
            result.setData(person.getPhone());
        }
        return result;
    }

    /**
     * 获取用户本地的手机通讯录名单
     * @param per_id 用户ID
     * @param phone_list 用户本地的手机通讯录名单
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public NoteResult getPhoneList(String per_id, String phone_list)  {
        // 构建结果对象，默认 201 失败
            NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);
            String newStr = phone_list;
           int size =  newStr.length();
            logger.info("newStr:"+newStr);

            JSONArray newList = JSONArray.parseArray(newStr);
            //小于风控设置的阈值 进人工审核
            String contacts = getContacts(per_id);
            List<Contact> list = contactMapper.selectAllByPerId(Integer.valueOf(per_id));
            String path;
            if (StringUtils.isEmpty(contacts)) {
                logger.info("getPhoneList---第一次上传");
                // 文件存储方案
                String fileName = contactsFileName;
                path = saveContacts(newStr, per_id,fileName);

                int i = deleteAndInsert(per_id, path);

                if (i > 0) {
                    result.setCode(SUCCESS_CODE);
                    result.setInfo(SUCCESS_INFO);
                }else{
                    return result;
                }

            } else {

                logger.info("getPhoneList---重新上传");

                // 原来有联系人 合并 + 去重
                JSONArray oldList = JSONArray.parseArray(contacts);

                JSONArray finalList = AddJsonarray.delRepeatIndexidJ(JSONArray.parseArray((AddJsonarray.joinJSONArray(newList, oldList))));

                // 文件存储方案
                String fileName = contactsFileName;

                path= saveContacts(JSONObject.toJSONString(finalList), per_id,fileName);

                int i = deleteAndInsert(per_id, path);

                if (i > 0) {
                    //先保证数据库删除插入成功，再删除文件
                    if (list.size() == 1) {
                        Contact c = list.get(0);
                        if ("1".equals(c.getSync())) {
                            //文件存储  删除原来的文件
                            String oldPath = c.getContactName();
                            int lenth = PropertiesReaderUtil.read("third", "trackServer").length();
                            oldPath = oldPath.substring(lenth,oldPath.length());
                            FDFSNewUtil.deleteFile(oldPath);
                        }
                    }
                    result.setCode(SUCCESS_CODE);
                    result.setInfo(SUCCESS_INFO);
                }else{
                    return result;
                }
            }
            //调用风控通讯录
            PersonMode person = personMapper.getPersonInfo(per_id);
            Map<String,String> contactsMap = new HashMap<>();
            contactsMap.put("name",person.getName());
            contactsMap.put("idCard",person.getCardNum());
            contactsMap.put("phone",person.getPhone());
            contactsMap.put("currentContacts",JSONObject.toJSONString(newList));
            contactsMap.put("contactPath",path);
            Person p = personMapper.selectByPrimaryKey(Integer.parseInt(per_id));
            Map<String,String> whiteTag = new HashMap<>();
            if ("Y".equals(p.getBlacklist())){
                whiteTag.put("whiteTag","0");
                whiteTag.put("org","cts");
            }else {
                whiteTag.put("whiteTag","1");
                whiteTag.put("org","cts");
            }
            logger.info("风控个人认证通讯录请求参数contactsMap="+contactsMap+"\nwhiteTag="+whiteTag);
            String response;
            try {
                response = riskAPI.risk(Integer.parseInt(productId), JSONObject.toJSONString(contactsMap), JSONObject.toJSONString(whiteTag));
            }catch (Exception e){
                result.setData(GUID_WRONG);
                logger.error("-------风控个人认证通讯录抛出异常\n"+e);
                return result;
            }
            logger.info("风控个人认证通讯录返回结果为----------------------response"+response);
            if (response == null){
                result.setData(GUID_WRONG);
                logger.error("-------风控个人认证通讯录dubbo服务调用失败"+response);
                return result;
            }
            JSONObject obj = JSONObject.parseObject(response);
            //返回的报文中
            String code = obj.getString("code");
            String message = obj.getString("msg");

        //每次上传通讯录成功  更新缓存里 节点为7的节点
        BpmNode node = new BpmNode();
        node.setPerId(Integer.valueOf(per_id));
        node.setNodeId(7);
        node.setUpdateDate(new Date());
            if(RISK_SUCCESS_CODE.equals(code)){
                node.setNodeStatus(STATUS_BPM_Y);
                contactMapper.updateStatusByPerId(1,per_id);
                manuallyReview(per_id, 1, 2, "");
            }else if(CodeReturn.JuLiXinCode.RC_RULE.equals(code)){
                node.setNodeStatus(STATUS_BPM_Y);
                contactMapper.updateStatusByPerId(1,per_id);
                manuallyReview(per_id, 1, 2, "");
            } else if(CodeReturn.JuLiXinCode.RISK_FAIL_CODE.equals(code)){//风控结果为拒绝
                node.setNodeStatus(STATUS_BPM_FAIL);
                contactMapper.updateStatusByPerId(0,per_id);
                manuallyReview(per_id, 1,1, code+message);
                result.setCode(BPM_UNDO_CODE);
                result.setInfo(BPM_UNDO_INFO);
                result.setData("r");
            }else {
             throw new DataAccessException("风控远程接口出现异常"){
                   private static final long serialVersionUID = 4693491283309191722L;
               };
            }
        jedisHset(per_id,RedisConst.NODE_KEY + per_id, "3.5", JSONObject.toJSONString(node));
        //为了人工审核 重新上传通讯录时，给用户设置一个key 存储本次上传的通讯录个数
        jedisCluster.set(RedisConst.CONTACTS_NUM_KEY + per_id, String.valueOf(size),"NX", "EX", 60 * 60 * 24 * 30);
        return result;
    }

    /**
     * 认证完成，修改用户、流程、借款状态,触发人工审核，分配审核人等操作
     * @param per_id 用户ID
     * @return
     */
    public NoteResult bpmFinish(String per_id){
        logger.info("bpmFinish:"+"进入finish");
        // 结果对象
        NoteResult result = new NoteResult(FAIL_CODE, FAIL_INFO);
        try {
            //人工审核新改动，改成待签约之前先判断person的状态
            Person person = personMapper.selectByPrimaryKey(Integer.valueOf(per_id));
            logger.info("person:"+JSONObject.toJSONString(person));

            BorrowList borr = borrowListMapper.selectNow(Integer.valueOf(per_id));


            int j = setReviewer(borr.getId(),"");

            if (j<1){
                return result;
            }

            if (StringUtils.isEmpty(person.getIsManual()) || "4".equals(person.getIsManual())){
                //null或者4  不是非人工审核  按原来流程走
                logger.info("bpmFinish："+"非人工审核");
                // 修改借款表的状态，把申请中改为待签约
                borr.setBorrStatus(STATUS_WAIT_SIGN);
                int k = borrowListMapper.updateByPrimaryKeySelective(borr);

                if (k > 0) {
                    result.setCode(SUCCESS_CODE);
                    result.setInfo(SUCCESS_INFO);
                }

            }else{
                //此用户为人工审核用户  插入一条人工审核表的记录  借款状态不更改
                logger.info("bpmFinish："+"人工审核");
                String contactValue = jedisCluster.get(RedisConst.CONTACTS_NUM_KEY + per_id);
                //从缓存中取本次上传的通讯录数量 存入manual表 风控根据通讯录数量进行排序
                // 缓存没有或者挂了  存该用户所有通讯录的数量
                Integer contactNum = StringUtils.isEmpty(contactValue) ? contactNum(per_id) : Integer.valueOf(contactValue);

                BorrowManual borrowManual = new BorrowManual(borr.getId(),person.getDescription(),contactNum,Integer.valueOf(person.getIsManual()),new Date());
                int i = borrowManualMapper.insertSelective(borrowManual);
                if (i>0){
                    result.setCode(NOW_BORROW_CODE);//202
                    result.setInfo("人工审核");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new NoteResult(FAIL_CODE, FAIL_INFO);
        }
        return result;
    }

    /**
     * 流程节点改变，并更新缓存(此节点没有则新增，此节点有则更新)
     * @param per_id 用户id
     * @param node_id 节点id
     * @param node_status 节点状态
     * @param description 节点描述
     * @return
     */
    @Override
    public NoteResult createBpmNode(String per_id,String node_id,String node_status,String description){
        NoteResult result = new NoteResult(FAIL_CODE, FAIL_INFO);
        try {

            // 新增流程明细 如果有该明细 update
            BpmNode oldNode = findNode(Integer.valueOf(per_id), Integer.valueOf(node_id));
            BpmNode node = oldNode==null ? new BpmNode() : oldNode;

            // 老用户加节点 要set bpm_id这个字段
            PerBpm bpm = perBpmMapper.selectByPerId(Integer.valueOf(per_id));

            if (bpm != null) {
                node.setBpmId(bpm.getId());
            }

            node.setPerId(Integer.valueOf(per_id));
            node.setNodeId(Integer.valueOf(node_id));
            node.setNodeStatus(node_status);
            node.setDescription(description);
            node.setUpdateDate(new Date());


            logger.info("更新的节点："+JSONObject.toJSONString(node));

            int m = oldNode==null ? bpmNodeMapper.insertSelective(node) : bpmNodeMapper.updateByPrimaryKeySelective(node);

            if (m > 0) {
                //修改节点之后  都set一遍redis
                node.setUpdateDate(new Date());
                jedisHset(per_id,RedisConst.NODE_KEY + per_id,node_id,JSONObject.toJSONString(node));
                result.setCode(SUCCESS_CODE);
                result.setInfo(SUCCESS_INFO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new NoteResult(FAIL_CODE, FAIL_INFO);
        }
        return result;
    }

    /**
     * 合同签约，状态改为已签约，添加签约时间
     * @param per_id 用户ID
     * @param borr_id 合同id
     * @return
     */
    @Override
    public NoteResult signingBorrow(String per_id, String borr_id,String name,String money,String day) {
        //构建结果对象，默认201 失败
        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);
        try{
            //borr_id查询到用户当前的借款表
            BorrowList borrowList = borrowListMapper.selectByPrimaryKey(Integer.valueOf(borr_id));

            int[] nodes = { 3, 4, 5, 6 };// 有可能为拒绝的节点
            for (int i : nodes) {
                // 遍历有可能为拒绝的节点
                BpmNode n = findNode(Integer.valueOf(per_id), i);
                if (n != null && !STATUS_BPM_Y.equals(n.getNodeStatus())) {

                    result.setCode(FAIL_CODE);
                    result.setInfo("信用等级过低,无法签约");
                    return result;
                }
            }

            //把当前借款表的借款状态改为已签约,添加签约时间
            if(!STATUS_WAIT_SIGN.equals(borrowList.getBorrStatus())){
                //如果借款表的状态不为待签约，不能签约
                result.setCode(FAIL_CODE);
                result.setInfo(FAIL_INFO);
                return result;
            }
            borrowList.setBorrStatus(STATUS_SIGNED);
            long time = System.currentTimeMillis();
            Date date =new Date(time);
            borrowList.setMakeborrDate(date);
            borrowList.setUpdateDate(date);
            int i = borrowListMapper.updateByPrimaryKeySelective(borrowList);
            if( i > 0){
                //更改成功
                //判断该笔订单是否可以自动放款
                if (autoLoanServiceImpl.getAutomaticLoan(Integer.parseInt(per_id))){
                    //set审核人为9999  自动放款
//                    setReviewer(Integer.valueOf(borr_id),LoanConstant.AUTO_REVIEWER);
                    Review review = reviewMapper.selectByBorrId(Integer.valueOf(borr_id));
                    if (review == null){
                        //没审核  insert
                        review = new Review();
                        review.setBorrId(Integer.parseInt(per_id));
                        review.setReviewType("1");
                        //set审核人为9999  自动放款
                        review.setEmployNum(LoanConstant.AUTO_REVIEWER);
                        int isReview = reviewMapper.insertSelective(review);
                        logger.info("自动放款borrId:"+borr_id+"insert审核人结果："+isReview);
                    }else {
                        //有审核  update
                        review.setEmployNum(LoanConstant.AUTO_REVIEWER);
                        int isReview = reviewMapper.updateByPrimaryKeySelective(review);
                        logger.info("自动放款borrId:"+borr_id+"update审核人结果："+isReview);
                    }

                    //去放款
                    String response = ysbpayService.payCont(per_id,borr_id);
                    logger.info("---------自动放款银生宝响应参数-----response="+response);
//                    Map<String, String> resultMap = JSONObject.parseObject(response, Map.class);
                    JSONObject resultMap = JSONObject.parseObject(response);
                    if (resultMap != null){
                        result.setCode(resultMap.getString("code"));
                        result.setInfo(resultMap.getString("info"));
                    }else {
                        result.setCode(FAIL_CODE);
                        result.setInfo(FAIL_INFO);
                    }
                    return result;
                }
                //不自动放款 还按照原来流程走
                setReviewer(Integer.valueOf(borr_id),"");
                //签约成功以后调机器人审核
                signRobot(borr_id);
                result.setCode(SUCCESS_CODE);
                result.setInfo(SUCCESS_INFO);
            }

        }catch(Exception e){
            e.printStackTrace();
            result.setCode(FAIL_CODE);
            result.setInfo(FAIL_INFO);
            return result;
        }
        return result;
    }

    /**
     * 取消借款申请。判断合同状态，在申请中的合同才能取消借款申请。
     * @param per_id 用户ID
     * @param borr_id 合同id
     * @return
     */
    public NoteResult cancelAskBorrow(String per_id, String borr_id) {
        //构建结果对象，默认201 失败
        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);
        try {
            //根据borr_id获取借款表
            BorrowList borrowList =
                    borrowListMapper.selectByPrimaryKey(Integer.valueOf(borr_id));
            String status = borrowList.getBorrStatus();
            if(STATUS_APLLY.equals(status) || STATUS_WAIT_SIGN.equals(status)){
                borrowList.setBorrStatus(STATUS_CANCEL);
                String couponId = borrowList.getPerCouponId();
                if(!"0".equals(couponId)){//使用了优惠券
                    PerCoupon coupon = perCouponMapper.selectByPrimaryKey(Integer.valueOf(couponId));
                    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date endDate = coupon.getEndDate();
                    long end = endDate.getTime()+24*60*60*1000L;

                    long now = System.currentTimeMillis();

                    if(now<end){//优惠券未过期

                        coupon.setStatus("1");//取消订单后把优惠券状态改为未使用
                    }else{
                        coupon.setStatus("3");//取消订单后把优惠券状态改为过期
                    }
                    perCouponMapper.updateByPrimaryKeySelective(coupon);
                }
                long time = System.currentTimeMillis();
                Date date =new Date(time);
                borrowList.setUpdateDate(date);
                int i = borrowListMapper.updateByPrimaryKeySelective(borrowList);
                if(i>0){//取消借款申请成功
                    result.setCode(SUCCESS_CODE);
                    result.setInfo(SUCCESS_INFO);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new NoteResult(FAIL_CODE,FAIL_INFO);
        }
        return result;
    }

    /**
     * 插入个人信息后，走风控模型 java接口
     * @param per_id 用户ID @param Name 姓名  @param IdValue 身份证号 //TODO
     */
    public NoteResult javaCheckRisk(String per_id,String email) {
        NoteResult result = new NoteResult(FAIL_CODE,"系统繁忙");
        //调用java个人认证参数
        Card card = cardMapper.selectByPerId(Integer.valueOf(per_id));
        Person person = personMapper.selectByPrimaryKey(Integer.valueOf(per_id));
        if(card == null || person == null){
            result.setInfo("用户信息不存在或不完整");
            return result;
        }
        String phone = person.getPhone();
        String name = card.getName();
        String idValue = card.getCardNum();
        String requestId = String.valueOf(new Date().getTime());
        //风控参数拼接
        Map<String,String> riskMap = new HashMap<>();
        Map<String,String> whiteTag = new HashMap<>();
        //判断是否为黑名单
        if ("Y".equals(person.getBlacklist())){
            whiteTag.put("whiteTag","0");
            whiteTag.put("org","basic,yxshare,cis,tongdun");
        }else {
            whiteTag.put("whiteTag","1");
            whiteTag.put("org","basic,yxshare,cis,tongdun");
        }
        riskMap.put("phone",phone);
        riskMap.put("idCard",idValue);
        riskMap.put("requestId",requestId);
        riskMap.put("name",name);
        riskMap.put("email",email);
        //请求风控系统
        logger.info("聚信立个人认证请求参数为----------------------riskMap"+riskMap);
        String response;
        try {
            response = riskAPI.risk(Integer.parseInt(productId), JSONObject.toJSONString(riskMap), JSONObject.toJSONString(whiteTag));
        }catch (Exception e){
            result.setData(GUID_WRONG);
            logger.error("-------聚信立个人认证抛出异常\n"+e);
            return result;
        }
        logger.info("聚信立个人认证返回结果为----------------------response"+response);
        if (response == null){
            result.setData(GUID_WRONG);
            logger.error("-------聚信立dubbo服务调用失败"+response);
            return result;
        }
        JSONObject obj = JSONObject.parseObject(response);
        //返回的报文中
        String code = obj.getString("code");
        String message = obj.getString("msg");
        if(RISK_SUCCESS_CODE.equals(code)){
            //风控结果成功，新增成功认证流程明细
            NoteResult create = createBpmNode(per_id, "3", STATUS_BPM_Y,"");
            if(SUCCESS_CODE.equals(create.getCode())){
                result.setCode(SUCCESS_CODE);
                result.setInfo("风控通过");
            }else{
                result.setCode(FAIL_CODE);
                result.setInfo("系统错误");
            }
        }else if(CodeReturn.JuLiXinCode.RC_RULE.equals(code)){
            //触碰聚信立5条风控规则 ，增加人工审核.相当于通过
            //             isManual 1审核 2解除   type 1通讯录 2聚信立
            this.manuallyReview(per_id, 2,1, message);
            //更新节点描述
            this.updateNodeDescription(per_id, CodeReturn.NodeId.GENERAL_REVIEW, message);

            result.setCode(SUCCESS_CODE);
            result.setInfo(message);
            return result;
        } else if(CodeReturn.JuLiXinCode.RISK_FAIL_CODE.equals(code)){//风控结果为拒绝
                //新增认证失败流程明细
                NoteResult create = createBpmNode(per_id, "3", STATUS_BPM_FAIL,code+message);
                //更改借款状态为审核未通过
                BorrowList borr = borrowListMapper.selectNow((Integer.valueOf(per_id)));
                borr.setBorrStatus(STATUS_REVIEW_FAIL);
                int j = borrowListMapper.updateByPrimaryKeySelective(borr);
                //更改借款状态成功，且新增认证失败流程明细成功
                if(j>0 && SUCCESS_CODE.equals(create.getCode())){
                    result.setCode("220");
                    // result.setInfo("拒绝:"+message);
                    return result;
                }
            }else {
            result.setCode(code);
            result.setInfo(message);
        }
        return result;
    }

    /**
     * 风控五条规则触碰更新人工审核
     * @param perId
     * @param isManual 触碰的节点
     */

    public boolean manuallyReview(String perId, int type,int isManual, String description){
        logger.info("manuallyReview start: per_id = " + perId + "isManual = " + isManual);
        Person person = personMapper.selectByPrimaryKey(Integer.valueOf(perId));

        if (person == null) {
            return false;
        }

        //查出person原状态  1，通讯录人工审核 2，聚信立审核  3，都有 4 都没有
        String status = person.getIsManual();
        if (StringUtils.isEmpty(status)){
            status = "4";//没有默认为4
        }

        try {
            //先判断isManual  审核还是解除  1审核，2解除
            if (isManual == 1){//审核,判断是通讯录审核还是聚信立审核
                if(type == 1){//通讯录审核
                    if ("1".equals(status)){//原状态为1，状态不变，desc覆盖
                        person.setDescription(description);
                        int i = personMapper.updateByPrimaryKeySelective(person);
                        if(i>0){
                            logger.info("isManual=1,type=1,status=1,更新desc");
                            return true;
                        }else{
                            return false;
                        }
                    }else if ("2".equals(status)){//原状态为2，改状态为3，desc前插
                        person.setIsManual("3");
                        String desc = person.getDescription();
                        desc = description+desc;
                        person.setDescription(desc);
                        int i = personMapper.updateByPrimaryKeySelective(person);
                        if(i>0){
                            logger.info("isManual=1,type=1,status=2,更新状态及desc");
                            return true;
                        }else{
                            return false;
                        }
                    }else if ("3".equals(status)){//原状态为3，状态不变，desc前覆盖
                        String desc = person.getDescription();
                        desc = description+"&"+desc.split("&")[1];
                        person.setDescription(desc);
                        int i = personMapper.updateByPrimaryKeySelective(person);
                        if(i>0){
                            logger.info("isManual=1,type=1,status=3,更新desc");
                            return true;
                        }else{
                            return false;
                        }
                    }else{//原状态为4，改状态为1，desc新增
                        person.setIsManual("1");
                        person.setDescription(description);
                        int i = personMapper.updateByPrimaryKeySelective(person);
                        if(i>0){
                            logger.info("isManual=1,type=1,status=4,更新desc");
                            return true;
                        }else{
                            return false;
                        }
                    }
                }else{//聚信立审核
                    if ("1".equals(status)){//原状态为1，改状态为3，desc追加
                        person.setIsManual("3");
                        String desc = person.getDescription();
                        person.setDescription(desc+"&"+description);
                        int i = personMapper.updateByPrimaryKeySelective(person);
                        if(i>0){
                            logger.info("isManual=1,type=2,status=1,更新status及desc");
                            return true;
                        }else{
                            return false;
                        }
                    }else if ("2".equals(status)){//desc覆盖
                        person.setDescription("&"+description);
                        int i = personMapper.updateByPrimaryKeySelective(person);
                        if(i>0){
                            logger.info("isManual=1,type=2,status=2,更新desc");
                            return true;
                        }else{
                            return false;
                        }
                    }else if ("3".equals(status)){//desc后覆盖
                        person.setDescription("&"+description);
                        int i = personMapper.updateByPrimaryKeySelective(person);
                        if(i>0){
                            logger.info("isManual=1,type=2,status=3,更新desc");
                            return true;
                        }else{
                            return false;
                        }
                    }else{//原状态为4，改状态为2，desc新增
                        person.setIsManual("2");
                        person.setDescription("&"+description);
                        int i = personMapper.updateByPrimaryKeySelective(person);
                        if(i>0){
                            logger.info("isManual=1,type=2,status=4,更新status及desc");
                            return true;
                        }else{
                            return false;
                        }
                    }

                }

            }else{//解除，判断是通讯录解除审核还是聚信立解除
                if (type == 1){//通讯录解除
                    if ("1".equals(status)){//原状态为1，改为4，desc清空
                        person.setIsManual("4");
                        person.setDescription("");
                        int i = personMapper.updateByPrimaryKeySelective(person);
                        if(i>0){
                            logger.info("isManual=2,type=1,status=1,更新status及desc");
                            return true;
                        }else{
                            return false;
                        }

                    }else if ("3".equals(status)) {//原状态为3，改为2，desc前清空
                        person.setIsManual("2");
                        String desc = person.getDescription();
                        person.setDescription("&"+desc.split("&")[1]);
                        int i = personMapper.updateByPrimaryKeySelective(person);
                        if(i>0){
                            logger.info("isManual=2,type=1,status=3,更新status及desc");
                            return true;
                        }else{
                            return false;
                        }
                    }

                }else{//聚信立解除
                    if ("2".equals(status)){//原状态为2，改为4，desc清空
                        person.setIsManual("4");
                        person.setDescription("");
                        int i = personMapper.updateByPrimaryKeySelective(person);
                        if(i>0){
                            logger.info("isManual=2,type=2,status=2,更新status及desc");
                            return true;
                        }else{
                            return false;
                        }

                    }else if ("3".equals(status)) {//原状态为3，改为1，desc后清空
                        person.setIsManual("1");
                        String desc = person.getDescription();
                        person.setDescription(desc.split("&")[0]);
                        int i = personMapper.updateByPrimaryKeySelective(person);
                        if(i>0){
                            logger.info("isManual=2,type=2,status=3,更新status及desc");
                            return true;
                        }else{
                            return false;
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 流程节点描述更新
     * @param perId
     */
    private void updateNodeDescription(String perId, Integer nodeId, String message){
        logger.info("updateNodeDescription start: per_id = " + perId + "message = " + message);
        Assertion.notEmpty(perId, "用户Id不能为空");
        Assertion.notEmpty(message, "审核结果不能为空");
        Assertion.isPositive(nodeId, "节点Id不能为空");
        createBpmNode(perId, String.valueOf(nodeId), STATUS_BPM_Y, message);
    }


    /**
     * 根据用户id查询姓名及身份证号
     * @param per_id  用户id
     * @return
     */
    public NoteResult getIDNumber(String per_id){
        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);
        Card card =cardMapper.selectByPerId(Integer.valueOf(per_id));
        Person person = personMapper.selectByPrimaryKey(Integer.valueOf(per_id));
        JSONObject data = new JSONObject();
        if(card != null && person != null){
            data.put("name", card.getName());
            data.put("number", card.getCardNum());
            data.put("phone", person.getPhone());
        }
        if(!data.isEmpty()){
            result.setCode(SUCCESS_CODE);
            result.setInfo(SUCCESS_INFO);
            result.setData(data);
        }
        return result;
    }

    /**
     * 查询所有省市信息
     * @return
     */
    public NoteResult getCity() {
        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);

        // logger.info("********1.0.1版本service");
        try {
            String city = jedisCluster.get(RedisConst.CITY_KEY);
            if (StringUtils.isEmpty(city)) {

                logger.info("redis里没有");
                // 放在data里面的结果集
                List<JSONObject> list = new ArrayList<JSONObject>();
                // 所有省
                List<City> province = cityMapper.findByPid(0);
                // 遍历每个省
                for (City c : province) {
                    // 该省下面的所有市
                    List<City> cities = cityMapper.findByPid(c.getId());

                    // 把所有的市放入这个省对象
                    JSONObject pro = new JSONObject();
                    pro.put("name", c.getName());
                    pro.put("id", c.getId());
                    pro.put("child", cities);

                    // 将对象加到结果集
                    list.add(pro);
                }

                if (!list.isEmpty()) {

                    String cityForSet = JSONObject.toJSONString(list);
                    jedisCluster.set(RedisConst.CITY_KEY, cityForSet);
                    result.setCode(SUCCESS_CODE);
                    result.setInfo(SUCCESS_INFO);
                    result.setData(list);
                }
            } else {
                logger.info("redis里有");
                result.setCode(SUCCESS_CODE);
                result.setInfo(SUCCESS_INFO);
                List<JSONObject> list2 = (List<JSONObject>) JSONObject.parse(city);
                result.setData(list2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new NoteResult(FAIL_CODE,FAIL_INFO);
        }
        return result;
    }

    /**
     * 获取当前节点的认证状态
     * @param per_id
     * @param node_id
     * @return
     */
    public NoteResult getNodeStatus(String per_id, String node_id) {

        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);
        //查询当前流程
        PerBpm bpm = perBpmMapper.selectByPerId(Integer.valueOf(per_id));
        //查询当前流程当前 node_id 下所有明细
        if(bpm != null){
            BpmNode node =
                    bpmNodeMapper.selectByNode_id(bpm.getId(), Integer.valueOf(node_id));
            if(node != null){//如果有node
                if(STATUS_BPM_UP.equals(node.getNodeStatus())){
                    result.setCode("207");
                    result.setInfo("未认证");
                    return result;
                }
                if(STATUS_BPM_Y.equals(node.getNodeStatus())){
                    result.setCode("208");
                    result.setInfo("已认证");
                    return result;
                }
                if(STATUS_BPM_FAIL.equals(node.getNodeStatus())){
                    result.setCode("209");
                    result.setInfo("认证失败");
                    return result;
                }
                if(STATUS_BPM_FAIL_B.equals(node.getNodeStatus())){
                    result.setCode("210");
                    result.setInfo("认证失败，且为黑名单");
                    return result;
                }
            }else{
                //node为空  表示超时已经删除手机和银行节点，返回211
                result.setCode("211");
                result.setInfo("由于系统原因，需要重新进行手机认证！");
                return result;
            }
        }else{
            // 新用户 没有bpm_id
            BpmNode node = findNode(Integer.valueOf(per_id), Integer.valueOf(node_id));
            if (node != null) {// 如果有node
                if (STATUS_BPM_UP.equals(node.getNodeStatus())) {
                    result.setCode("207");
                    result.setInfo("未认证");
                    return result;
                }
                if (STATUS_BPM_Y.equals(node.getNodeStatus())) {
                    result.setCode("208");
                    result.setInfo("已认证");
                    return result;
                }
                if (STATUS_BPM_FAIL.equals(node.getNodeStatus())) {
                    result.setCode("209");
                    result.setInfo("认证失败");
                    return result;
                }
                if (STATUS_BPM_FAIL_B.equals(node.getNodeStatus())) {
                    result.setCode("210");
                    result.setInfo("认证失败，且为黑名单");
                    return result;
                }
            } else {
                // node为空 表示超时已经删除手机和银行节点，返回211
                result.setCode("211");
                result.setInfo("由于系统原因，需要重新进行手机认证！");
                return result;
            }
        }
        return result;
    }

    /**
     * 获取签约界面信息
     * @param per_id
     * @return
     */
    public NoteResult getSignInfo(String per_id) {
        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);
        BorrowList borr = borrowListMapper.selectNow(Integer.valueOf(per_id));
        Bank bank = bankMapper.getSignInfo(Integer.valueOf(per_id));
        try {
            String borr_id = String.valueOf(borr.getId());
            //--------------2017.3月更新 签约页面借款金额从产品表中读取
            ProductTerm term = productTermMapper.selectByProductId(borr.getProdId());
            String amount = String.format("%.2f", term.getMaximumAmount());
            //  String money = amount.substring(0, amount.indexOf("."));
            String day = String.valueOf(term.getTermValue());
            String planRepay = String.valueOf(borr.getPlanRepay());
//          String couponId = borr.getPerCouponId();
//            if(!"0".equals(couponId)){//使用了优惠券
//                PerCoupon coupon = perCouponMapper.selectByPrimaryKey(Integer.valueOf(couponId));
//                String couponAmount = coupon.getAmount();
//                //预计还款金额=原预计还款金额-优惠金额
//                planRepay =String.valueOf(Double.valueOf(planRepay)-Double.valueOf(couponAmount));
//            }

            String name = bank.getName();
            String bankNum = bank.getBankNum();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            long now = System.currentTimeMillis();
            long pay = now+24*60*60*1000*Integer.parseInt(day);
            Date borrDate = new Date(now);
            Date repayDate = new Date(pay);
            String borrDay = sdf.format(borrDate);
            String repayDay = sdf.format(repayDate);
            JSONObject data = new JSONObject();
            NoteResult charge = getProductCharge(borr.getProdId(),per_id,amount,day);
            JSONObject fee = (JSONObject)charge.getData();
            //4.利息
            String interest = fee.getString("interest");
            //5.信审费
            String review = fee.getString("review");
            //6.平台费
            String plat = fee.getString("plat");
            //13.优惠券金额
            String couponId = borr.getPerCouponId();
            String couponMoney = "0.00";
            if(!"0".equals(couponId)){//使用了优惠券
                PerCoupon coupon = perCouponMapper.selectByPrimaryKey(Integer.valueOf(couponId));
                couponMoney = String.format("%.2f", Double.valueOf(coupon.getAmount()));
            }
            data.put("review",review);
            data.put("plat",plat);
            data.put("interest",interest);
            data.put("couponMoney",couponMoney);
            data.put("per_id", per_id);
            data.put("borr_id", borr_id);
            data.put("name", name);
            data.put("money", amount);//2017.1.20更改， 借款金额（500）直接写成应还金额（525）
            data.put("day", day);
            data.put("bankNum", bankNum);
            data.put("borrDay", borrDay);
            data.put("planRepay", planRepay);
            data.put("repayDay", repayDay);
            result.setCode(SUCCESS_CODE);
            result.setInfo(SUCCESS_INFO);
            result.setData(data);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(FAIL_CODE);
            result.setInfo(FAIL_INFO);
            return result;
        }
        return result;
    }

    /**
     * 人脸识别页面   获取头像照片或身份证正面照
     * @param per_id
     * @return
     */
    public NoteResult getCardz(String per_id){
        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);
        try {
            Card card = cardMapper.selectByPerId(Integer.valueOf(per_id));
            String cardName = card.getName();
            String cardNum = card.getCardNum();
            String imageId = card.getCardPhotod();
            if(imageId==null || "".equals(imageId)){//如果没有头像图片，那就传身份证正面照片
                imageId = card.getCardPhotoz();
            }
            Image image = imageMapper.selectByPrimaryKey(Integer.valueOf(imageId));
            //2017.4.7更新 取出存在硬盘的图片
            String path = PIC_DIR+image.getImageUrl();

            File data = new File(path);
            result.setCode(SUCCESS_CODE);
            // 2017.04.25更改 info换成姓名+身份证
            result.setInfo(cardName + "," + cardNum);
            result.setData(data);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            result.setCode(FAIL_CODE);
            result.setInfo(FAIL_INFO);
            return result;
        }

        return result;
    }

    /**
     * 获取姓名及手机号
     * @param per_id
     * @return
     */
    public NoteResult getNamePhone(String per_id) {
        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);
        try{
            Person person = personMapper.selectByPrimaryKey(Integer.valueOf(per_id));
            Card card = cardMapper.selectByPerId(Integer.valueOf(per_id));
            int unread = msgMapper.selectUnread(per_id);
            JSONObject obj = new JSONObject();
            if(unread>0){
                obj.put("msg", "y");
            }else{
                obj.put("msg", "n");
            }
            obj.put("phone", person.getPhone());
            if(card == null){
                obj.put("name", "未知");
            }else{
                obj.put("name", card.getName());
            }
            result.setCode(SUCCESS_CODE);
            result.setInfo(SUCCESS_INFO);
            result.setData(obj);
        }catch(Exception e){
            e.printStackTrace();
            result.setCode(FAIL_CODE);
            result.setInfo(FAIL_INFO);
            return result;
        }
        return result;
    }

    /**
     * 获取可以代扣及支付的银行卡列表
     * @return
     */
    @Override
    public NoteResult getBankList() {
        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);

        String redis = jedisCluster.get(RedisConst.BANKLIST_KEY);

        List<BankList> list;
        if (StringUtils.isEmpty(redis)) {
            // redis里没有 从数据库拿
            list = bankListMapper.selectBySupport("1");
            // redis set
            jedisCluster.set(RedisConst.BANKLIST_KEY, JSONArray.toJSONString(list));
        } else {
            // redis里有
            list = (List<BankList>) JSONArray.parse(redis);

        }

        if (!list.isEmpty()) {

            result.setCode(SUCCESS_CODE);
            result.setInfo(SUCCESS_INFO);
            result.setData(list);
        }


        return result;
    }

    /**
     * 根据系统名称获取当前最新版本号及是否强制更新
     * @param name 系统名称：ios/andriod
     * @return
     */
    @Override
    public NoteResult getVersion(String name,String version) {
        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);
        // 2017-07-04 初始化为服务器的下载地址 如果缓存挂了从服务器下载
        String download = APP_URL;

        if (StringUtils.isEmpty(jedisCluster.get(RedisConst.DOWNLOAD_KEY))) {

            jedisCluster.set(RedisConst.DOWNLOAD_KEY, download);
        } else {
            download = jedisCluster.get(RedisConst.DOWNLOAD_KEY);
        }

        AppVersion v = new AppVersion();
        if("".equals(version)){
            v = appVersionMapper.selectByAppName(name);
        }else{
            v = appVersionMapper.selectByAppNameVersion(name, version);
        }
        if(v != null){
            result.setCode(SUCCESS_CODE);
            JSONObject obj = new JSONObject();
            obj.put("version",v.getVersion());
            obj.put("code", v.getCode());
            obj.put("forcedUpdate",v.getForcedUpdate());
            obj.put("add", download);
            result.setData(obj);
            result.setInfo(SUCCESS_INFO);
        }else{
            result.setCode(SUCCESS_CODE);
            JSONObject obj = new JSONObject();
            obj.put("version","");
            obj.put("code", "1000000");
            obj.put("forcedUpdate","y");
            obj.put("add", download);
            result.setData(obj);
            result.setInfo(SUCCESS_INFO);
        }
        return result;
    }

    /**
     * 获取首页滚动条显示内容
     * @param per_id
     * @return
     */
    @Override
    public NoteResult getRolling(String per_id) {
        NoteResult result = new NoteResult(SUCCESS_CODE,SUCCESS_INFO);
        BorrowList borr = borrowListMapper.selectNow(Integer.valueOf(per_id));
        List<String> data = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            // long end = sdf.parse("2017-03-28 00:00:00").getTime();
            // long nowTime = System.currentTimeMillis();
            // if(nowTime<end){
            // data.add("悠米闪借将于3月23日18点到3月27日0点进行服务器升级，届时可能影响放款及还款等功能。注册、登录、认证及签约功能及微信公众号客服不受影响。");
            // }

            if ("on".equals(isTest)){
                data.add("欢迎来到悠米闪借！");
            }
            if (borr != null) {
                if (STATUS_LATE_REPAY.equals(borr.getBorrStatus()) || STATUS_TO_REPAY.equals(borr.getBorrStatus())) {

                    Date plan = borr.getPlanrepayDate();
                    Date now = new Date();
                    Calendar cp = Calendar.getInstance();
                    cp.setTime(plan);
                    Calendar cn = Calendar.getInstance();
                    cn.setTime(now);
                    long p = plan.getTime();
                    long n = now.getTime();
                    int day;
                    if (p > n) {// 未逾期
                        day = cp.get(Calendar.DAY_OF_YEAR) - cn.get(Calendar.DAY_OF_YEAR);
                        data.add("您距离还款日还有" + day + "天！");

                    } else {
                        day = cn.get(Calendar.DAY_OF_YEAR) - cp.get(Calendar.DAY_OF_YEAR);
                        data.add("您已经逾期" + day + "天！");
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        result.setData(data);
        return result;
    }

    /**
     * 获取合同信息
     * @param per_id
     * @param borr_id
     */
    @Override
    public NoteResult getContract(String per_id, String borr_id) {
        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);
        try{
            BorrowList borr = borrowListMapper.selectByPrimaryKey(Integer.valueOf(borr_id));
            Integer prodId = borr.getProdId();
            ProductTerm term = productTermMapper.selectByProductId(prodId);
            //1.借款金额      2017.3月更新  借款金额从产品表中读取
            String money = String.format("%.2f", term.getMaximumAmount());
            //2.还款金额
            String planRepay = borr.getPlanRepay();
            //3.借款天数
            String day = String.valueOf(term.getTermValue());
            // logger.debug(money + "," + day);
            NoteResult charge = getProductCharge(prodId,per_id,money.substring(0,money.lastIndexOf(".")),day);
            JSONObject fee = (JSONObject)charge.getData();
            // logger.debug(JSONObject.toJSONString(fee));
            //4.利息
            String interest = fee.getString("interest");
            //5.信审费
            String review = fee.getString("review");
            //6.平台费
            String plat = fee.getString("plat");
            //7.合同编号
            String borrNum = borr.getBorrNum();
            NoteResult info = getIDNumber(per_id);
            JSONObject infoJSON = (JSONObject)info.getData();
            //8.借款人姓名
            String name = infoJSON.getString("name");
            //9.借款人身份证号
            String cardId = infoJSON.getString("number");
            //10.借款人手机号
            String phone = infoJSON.getString("phone");

            Bank bank = bankMapper.getSignInfo(Integer.valueOf(per_id));
            //15.银行卡号
            String bankNum = bank.getBankNum();
            //19.银行卡预留手机号
            String bankPhone = bank.getPhone();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
            long now = System.currentTimeMillis();
            long planRepayTime = now + term.getTermValue()*24*60*60*1000L;
            //11.签订日期 、借款日期（今天）
            String borrowDate = sdf.format(new Date(now));
            //16.签订年
            String signYear = borrowDate.substring(0,4);
            //17.签订月
            String signMonth = borrowDate.substring(5,7);
            //18.签订日
            String signDay = borrowDate.substring(8,borrowDate.length()-1);
            //12.预计还款日期
            String planDate = sdf.format(new Date(planRepayTime));

            String couponId = borr.getPerCouponId();
            //13.优惠券金额
            String couponMoney = "0.00";
            if(!"0".equals(couponId)){//使用了优惠券
                PerCoupon coupon = perCouponMapper.selectByPrimaryKey(Integer.valueOf(couponId));
                couponMoney = String.format("%.2f", Double.valueOf(coupon.getAmount()));
            }
            //14.手续费
            String thirdFee = codeValueMapper.getMeaningByTypeCode("payment_fee", "1");
//            String thirdFee = "2.00";
//            double ll = Double.parseDouble(planRepay) * 0.002;
//            if (ll > 2) {
//                thirdFee = String.valueOf(ll);
//            } else {
//                thirdFee = "2.00";
//            }
//            String[] as = thirdFee.split("\\.");
//            if (as[1].length() > 2) {
//                thirdFee = thirdFee.substring(0, thirdFee.indexOf(".") + 3);
//            }
//            JSONObject data = new JSONObject();
//            data.put("borrNum", borrNum);
//            data.put("cardId", cardId);
//            data.put("money", money);
//            data.put("day", day);
//            data.put("planRepay", planRepay);
//            data.put("name", name);
//            data.put("borrowDate", borrowDate);
//            data.put("planDate", planDate);
//            data.put("phone", phone);
//            data.put("thirdFee", thirdFee);
//            data.put("interest", interest);
//            data.put("review", review);
//            data.put("plat", plat);
//            data.put("bankNum",bankNum);
//            data.put("signYear", signYear);
//            data.put("signMonth", signMonth);
//            data.put("signDay", signDay);
//            data.put("couponMoney", couponMoney);
            InputStream is = getClass().getResourceAsStream("/../../contract.html");
            BufferedReader bis = new BufferedReader(new InputStreamReader(is));
            String fileString="";
            String szTemp;

            while ( (szTemp = bis.readLine()) != null) {
                fileString+=szTemp;
            }
            bis.close();

            fileString = fileString.replaceAll("#borrNum", borrNum);
            fileString = fileString.replaceAll("#cardId", cardId);
            fileString = fileString.replaceAll("#money", money);
            fileString = fileString.replaceAll("#day", day);
            fileString = fileString.replaceAll("#planRepay", planRepay);
            fileString = fileString.replaceAll("#name", name);
            fileString = fileString.replaceAll("#borrowDate", borrowDate);
            fileString = fileString.replaceAll("#planDate", planDate);
            fileString = fileString.replaceAll("#phone", phone);
            fileString = fileString.replaceAll("#thirdFee", thirdFee);
            fileString = fileString.replaceAll("#interest", interest);
            fileString = fileString.replaceAll("#review", review);
            fileString = fileString.replaceAll("#plat", plat);
            fileString = fileString.replaceAll("#bankNum", bankNum);
            fileString = fileString.replaceAll("#signYear", signYear);
            fileString = fileString.replaceAll("#signMonth", signMonth);
            fileString = fileString.replaceAll("#signDay", signDay);
            fileString = fileString.replaceAll("#couponMoney", couponMoney);
            fileString = fileString.replaceAll("#bankPhone", bankPhone);

            result.setCode(SUCCESS_CODE);
            result.setData(fileString);
            result.setInfo(SUCCESS_INFO);

        }catch(Exception e){
            e.printStackTrace();
            result.setInfo("系统异常或信息不存在");
            return result;
        }

        return result;
    }

    /**
     * 增加调用第三方接口次数
     * @param per_id
     * @param type
     * @param count
     * @param status
     * @return
     */
    @Override
    public NoteResult addCount(String per_id, String type, String count,String status) {
        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);
        try {
            //插入数据
            Sensetime st = new Sensetime();
            st.setPerId(Integer.valueOf(per_id));
            st.setType(type);
            st.setCount(Integer.valueOf(count));
            st.setCreateTime(new Date());

            int i = sensetimeMapper.insert(st);
            if(i>0){//插入成功
                if("2".equals(type)||"4".equals(type)){//插入数据类型是人脸识别
                    if("s".equals(status)){//人脸识别结果为成功
                        //新增人脸识别认证成功接口
                        result = createBpmNode(per_id, "4", STATUS_BPM_Y, "");
                        return result;
                    }
                    //人脸识别结果为失败
                    //查询该用户人脸识别的次数
                    int times = sensetimeMapper.selectTimes(per_id);
                    //3次，并且本次为失败，提交人脸识别拒绝节点
                    if(times>2 && "f".equals(status)){
                        NoteResult create = createBpmNode(per_id,"4",STATUS_BPM_FAIL,"人脸识别3次未成功");
                        if(SUCCESS_CODE.equals(create.getCode())){//插入人脸识别拒绝节点成功
                            result.setCode(CodeReturn.VERIFY_FAIL_CODE);
                            result.setInfo("人脸识别失败超过3次，无法继续认证");
                        }
                    }else{
                        result.setCode(SUCCESS_CODE);
                        result.setInfo(SUCCESS_INFO);
                    }
                }else{//插入的数据不是人脸识别
                    result.setCode(SUCCESS_CODE);
                    result.setInfo(SUCCESS_INFO);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setInfo("系统错误");
            return result;
        }

        return result;
    }

    @Override
    public NoteResult useCoupon(String per_id) {
        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);
        try {
            BorrowList borr = borrowListMapper.selectNow(Integer.valueOf(per_id));
            //String status = borr.getBorrStatus();
            if(borr == null || STATUS_CANCEL.equals(borr.getBorrStatus())
                    || STATUS_PAY_BACK.equals(borr.getBorrStatus())
                    || STATUS_DELAY_PAYBACK.equals(borr.getBorrStatus())){
                //没借过款，或者已还清，或者已取消，或者逾期结清
                result.setCode(SUCCESS_CODE);
                result.setData("1");
                result.setInfo(SUCCESS_INFO);
            }else{
                result.setCode(SUCCESS_CODE);
                result.setData("0");
                result.setInfo(SUCCESS_INFO);
            }
        }catch(Exception e){
            e.printStackTrace();
            result.setInfo("系统错误");
            return result;
        }

        return result;
    }

    @Override
    public String verifyTokenId(String per_id, String token) {
        try{
            String loginTokenId = personMapper.getTokenId(per_id);
            if(token.equals(loginTokenId)){
                return SUCCESS_CODE;
            }else{
                return FAIL_CODE;
            }
        }catch(Exception e){
            return FAIL_CODE;
        }

    }

//    @Override
//    public NoteResult ocrFront(String per_id, String photo) {
//        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);
//        try {
//            //把照片传到第三方
//            JSONObject object = new JSONObject();
//            object.put("front_photo", photo);
//            object.put("head_option", 0);
//            String res = OcrServiceDemo.ocr(OCR_URL, object, OCR_KEY, OCR_SECRET);
//            JSONObject resJSON = JSONObject.parseObject(res);
//            int code = resJSON.getInteger("code");
//            if(code == 0){//解析成功
//                logger.info("=========身份证正面识别成功");
//                JSONObject data = resJSON.getJSONObject("data");
//                //增加次数  type 安卓 身份证 3  次数1
//                NoteResult add = addCount(per_id, "3", "1", "s");
//                if(SUCCESS_CODE.equals(add.getCode())){
//                    result.setCode(SUCCESS_CODE);
//                    result.setData(data);
//                    result.setInfo(SUCCESS_INFO);
//                }
//                return result;
//            }else{
//                logger.info("=========身份证正面识别失败code:"+code);
//                //增加次数  type 安卓 身份证 3  次数1
//                NoteResult add = addCount(per_id, "3", "1", "f");
//                if(SUCCESS_CODE.equals(add.getCode())){
//                    result.setCode("202");
//                    result.setInfo("OCR失败");
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new NoteResult(FAIL_CODE,FAIL_INFO);
//        }
//        return result;
//    }
//
//    @Override
//    public NoteResult ocrBack(String per_id, String photo) {
//        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);
//        try {
//            //把照片传到第三方
//            JSONObject object = new JSONObject();
//            object.put("back_photo", photo);
//            object.put("head_option", 0);
//            String res = OcrServiceDemo.ocr(OCR_URL, object, OCR_KEY, OCR_SECRET);
//            JSONObject resJSON = JSONObject.parseObject(res);
//            int code = resJSON.getInteger("code");
//            if(code == 0){//解析成功
//                logger.info("=========身份证反面识别成功");
//                JSONObject data = resJSON.getJSONObject("data");
//                //增加次数  type 安卓 身份证 3  次数1
//                NoteResult add = addCount(per_id, "3", "1", "s");
//                if(SUCCESS_CODE.equals(add.getCode())){
//                    result.setCode(SUCCESS_CODE);
//                    result.setData(data);
//                    result.setInfo(SUCCESS_INFO);
//                }
//            }else{
//                logger.info("=========身份证反面识别失败code:"+code);
//                NoteResult add = addCount(per_id, "3", "1", "f");
//                //增加次数  type 安卓 身份证 3  次数1
//                if(SUCCESS_CODE.equals(add.getCode())){
//                    result.setCode("202");
//                    result.setInfo("OCR失败");
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new NoteResult(FAIL_CODE,FAIL_INFO);
//        }
//        return result;
//
//    }
//
//    @Override
//    public NoteResult compareAll(String per_id, String photos) {
//        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);
//        try {
//            //根据per_id查询姓名及身份证号
//            Card card = cardMapper.selectByPerId(Integer.valueOf(per_id));
//            String name = card.getName();
//            String number = card.getCardNum();
//            Integer imageId = Integer.valueOf(card.getCardPhotoz());
//            Image cardz = imageMapper.selectByPrimaryKey(imageId);
//            String IDphoto = cardz.getImage();
//            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//            String res;
//            if(card.getCardPhotod()==null || "".equals(card.getCardPhotod())){//无底图，对比三次
//                 //请求第三方
//                 res = CompareAllDemo.compareAll(COMPARE_URL, name, number, IDphoto, photos, time,
//                        COMPARE3_KEY1, COMPARE3_SECRET1, COMPARE3_KEY2,
//                        COMPARE3_SECRET2, COMPARE3_KEY3, COMPARE3_SECRET3);
//                 JSONObject response = JSONObject.parseObject(res);
//                 //添加底图
//                 JSONObject r1 = response.getJSONObject("response_l1");
//                 JSONObject r2 = response.getJSONObject("response_l2");
//                 JSONObject r3 = response.getJSONObject("response_l3");
//
//                 JSONObject data1 = r1.getJSONObject("data");
//                 JSONObject data2 = r2.getJSONObject("data");
//                 JSONObject data3 = r3.getJSONObject("data");
//
//                 //3次比对的相似度
//                 String sim1 = data1.getString("similarity");
//                 String sim2 = data2.getString("similarity");
//                 String sim3 = data3.getString("similarity");
//
//                 int code1 = r1.getInteger("code");
//                 int code2 = r2.getInteger("code");
//                 int code3 = r3.getInteger("code");
//                 if(code1+code2+code3==0){//全成功
//                     logger.info("=========人脸识别成功（3次比对）");
//
//                     //库照String
//                     String photo_id = data1.getString("photo_id");
//
//                     //存入image表
//                     Image cardd = new Image();
//                     cardd.setImageType("3");
//                     cardd.setImageFormat("jpg");
//                     cardd.setImage(photo_id);
//                     int i = imageMapper.insert(cardd);
//                     if(i>0){
//                        card.setCardPhotod(Integer.valueOf(cardd.getId()).toString());
//                        cardMapper.updateByPrimaryKeySelective(card);
//                        //新增节点完成  节点为4 人脸识别
//                        NoteResult create = createBpmNode(per_id, "4", STATUS_BPM_Y, "库照与身份证拍照对比相似度"+sim1+",自拍与抓拍对比相似度"+sim2+",身份证拍照与自拍对比相似度"+sim3);
//                        //增加次数，type 安卓人脸  4,调用count 3,状态成功s
//                        NoteResult add = addCount(per_id, "4", "3", "s");
//                        if(SUCCESS_CODE.equals(add.getCode())&&SUCCESS_CODE.equals(create.getCode())){
//                            result.setCode(SUCCESS_CODE);
//                            result.setInfo(SUCCESS_INFO);
//                        }
//                     }
//
//                 }else{//人脸识别结果为失败
//                     logger.info("=========人脸识别失败（3次比对）");
//                     //新增节点未完成  节点为4 人脸识别（新增未完成只是为了看相似度）
//                     NoteResult create = createBpmNode(per_id, "4", STATUS_BPM_N, "库照与身份证拍照对比相似度"+sim1+",自拍与抓拍对比相似度"+sim2+",身份证拍照与自拍对比相似度"+sim3);
//                     //增加次数，type 4,调用count 3,状态成功s
//                     NoteResult add = addCount(per_id, "4", "3", "f");
//                     if(SUCCESS_CODE.equals(create.getCode())){
//                         if("202".equals(add.getCode())){//人脸识别超过3次
//                             result.setCode("203");
//                             result.setInfo("人脸识别超过3次");
//                         }else if(SUCCESS_CODE.equals(add.getCode())){
//                             result.setCode("202");
//                             result.setInfo("人脸识别认证失败");
//                         }
//                     }
//                 }
//
//            //增加调用次数  type3  count3
//            }else{//有底图，对比两次
//                 //请求第三方
//                 res = CompareAllDemo.compareAll(COMPARE_URL, name, number, IDphoto, photos, time,
//                        "", "", COMPARE2_KEY2,
//                        COMPARE2_SECRET2, COMPARE2_KEY3, COMPARE2_SECRET3);
//                 JSONObject response = JSONObject.parseObject(res);
//
//                 JSONObject r2 = response.getJSONObject("response_l2");
//                 JSONObject r3 = response.getJSONObject("response_l3");
//
//                 JSONObject data2 = r2.getJSONObject("data");
//                 JSONObject data3 = r3.getJSONObject("data");
//                 //两次对比的相似度
//                 String sim2 = data2.getString("similarity");
//                 String sim3 = data3.getString("similarity");
//
//                 int code2 = r2.getInteger("code");
//                 int code3 = r3.getInteger("code");
//                 if(code2+code3==0){//全成功
//                     logger.info("=========人脸识别成功（2次比对）");
//                     //新增节点完成  节点为4 人脸识别
//                     NoteResult create = createBpmNode(per_id, "4", STATUS_BPM_Y, "自拍与抓拍对比相似度"+sim2+",身份证拍照与自拍对比相似度"+sim3);
//                     //增加次数，type 4,调用count 3,状态成功s
//                     NoteResult add = addCount(per_id, "4", "3", "s");
//                     if(SUCCESS_CODE.equals(add.getCode())&&SUCCESS_CODE.equals(create.getCode())){
//                         result.setCode(SUCCESS_CODE);
//                         result.setInfo(SUCCESS_INFO);
//                     }
//                 }else{//人脸识别结果为失败
//                     logger.info("=========人脸识别失败（2次比对）");
//                     //新增节点未完成  节点为4 人脸识别（新增未完成只是为了看相似度）
//                     NoteResult create = createBpmNode(per_id, "4", STATUS_BPM_N, "自拍与抓拍对比相似度"+sim2+",身份证拍照与自拍对比相似度"+sim3);
//                     //增加次数，type 4,调用count 3,状态成功s
//                     NoteResult add = addCount(per_id, "4", "3", "f");
//                     if(SUCCESS_CODE.equals(create.getCode())){
//                         if("202".equals(add.getCode())){//人脸识别超过3次
//                             result.setCode("203");
//                             result.setInfo("人脸识别超过3次");
//                         }else if(SUCCESS_CODE.equals(add.getCode())){
//                             result.setCode("202");
//                             result.setInfo("人脸识别认证失败");
//                         }
//                     }
//                 }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new NoteResult(FAIL_CODE,FAIL_INFO);
//        }
//
//
//        return result;
//    }

    @Override
    public NoteResult goBlackList(String himid_list, String blacklist) {
        String[] himid = himid_list.split(",");

        NoteResult noteResult = new NoteResult();

        int result = 0;
        try {
            for (int i = 0; i < himid.length; i++) {
                Card card = cardMapper.selectByCardNo(himid[i]);
                Person person = new Person();
                person.setId(card.getPerId());
                person.setBlacklist(blacklist);
                person.setUpdateDate(new Date());

                result += personMapper.updateByPrimaryKeySelective(person);

            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        noteResult.setCode(String.valueOf(result));
        if (result > 0) {
            noteResult.setInfo("处理成功！");
        } else {
            noteResult.setInfo("处理失败！");
        }
        return noteResult;
    }

    //2017.03.27---APP还款页面信息
    @Override
    public NoteResult repayInfo(String per_id) {
        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);

        try {
            //生成订单号
            String serial = SerialNumUtil.createByType("05");
            String company = "上海米缸企业信用征信有限公司";
            PersonMode mode = personMapper.getPersonInfo(per_id);
            JSONObject obj = (JSONObject)JSONObject.toJSON(mode);
            obj.put("serial", serial);
            obj.put("company", company);
            result.setCode(SUCCESS_CODE);
            result.setInfo(SUCCESS_INFO);
            result.setData(obj);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(FAIL_CODE);
            result.setInfo("系统错误");
            return result;
        }
        return result;
    }

//    @Override
//    public NoteResult recognition(String per_id) {
//
//        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);
//        try {
//            //新增认证流程节点明细(人脸识别节点为4)
//            result = createBpmNode(per_id, "4",STATUS_BPM_Y,"");
//            //增加调用第三方次数(人脸识别type 2)
//            NoteResult add = addCount(per_id, "2", "3", "s");
//            if(SUCCESS_CODE.equals(result.getCode()) && SUCCESS_CODE.equals(add.getCode())){
//                result.setCode(SUCCESS_CODE);
//                result.setInfo(SUCCESS_INFO);
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//            return new NoteResult(FAIL_CODE,FAIL_INFO);
//        }
//        return result;
//    }

    //图片转化成base64字符串
    public String GetImageStr(String path)
    {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        String imgFile = path;//待处理的图片
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try
        {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);//返回Base64编码过的字节数组字符串
    }


    //base64字符串转化成图片
    public boolean GenerateImage(String imgStr,String path)
    {   //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try
        {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {//调整异常数据
                    b[i]+=256;
                }
            }
            //生成jpeg图片
            String imgFilePath = path;//新生成的图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    // base64字符串转化成图片
    public boolean GenerateImage(byte[] b, String path) { // 对字节数组字符串进行Base64解码并生成图片
        if (b.length == 0) // 图像数据为空
            return false;
        try {

            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            // 生成jpeg图片
            String imgFilePath = path;// 新生成的图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public NoteResult activateCoupon(String per_id, String coupon_id) {

        NoteResult result = new NoteResult(FAIL_CODE,FAIL_INFO);

        try {
            PerCoupon pc = perCouponMapper.selectByPrimaryKey(Integer.valueOf(coupon_id));
            if("4".equals(pc.getStatus())){//如果该优惠券状态为未激活  改为未使用
                pc.setStatus("1");
            }else{
                result.setInfo("该优惠券不能激活");
                return result;
            }
            int i = perCouponMapper.updateByPrimaryKeySelective(pc);
            if(i>0){//更新成功
                result.setCode(SUCCESS_CODE);
                result.setInfo(SUCCESS_INFO);
                PerCoupon2 cou2 = perCouponMapper.getCoupon2ById(coupon_id);
                result.setData(JSONObject.toJSON(cou2));
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(FAIL_CODE);
            result.setInfo("系统错误");
            return result;
        }
        return result;
    }

    @Override
    public NoteResult banner() {
        NoteResult result = new NoteResult(FAIL_CODE, FAIL_INFO);

//        JSONObject obj = new JSONObject();
//        List<JSONObject> bannerList = new ArrayList<JSONObject>();
//
//        try {
//
//            File dir = new File(BANNER_DIR);
//            String[] filelist = dir.list();
//            // 遍历文件夹下
//            if(Detect.notEmpty(filelist)){
//                for (int i = 0; i < filelist.length; i++) {
//                    // 获取文件名
//                    String fileName = filelist[i];
//                    // 拼URL
//                    String url = BANNER_URL + fileName;
//                    JSONObject banner = new JSONObject();
//                    banner.put("imageUrl", url);
//                    if ("banner1106.png".equals(fileName)){
//                        banner.put("directUrl", "https://mp.weixin.qq.com/s?__biz=MzUxOTI3OTYxOQ==&mid=2247483735&idx=1&sn=4e7af5967f419c54796d092828101ee5&chksm=f9fd443bce8acd2d76ac7e78eb9cc570b80c7121d219ddede3caf85baf51578a645df39ac144#rd");
//                    }else {
//                        banner.put("directUrl", "");
//                    }
//                    bannerList.add(banner);
//                }
//            }
//
//            obj.put("bannerList", bannerList);
//
//            result.setCode(SUCCESS_CODE);
//            result.setInfo(SUCCESS_INFO);
//            result.setData(obj);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            result.setCode(FAIL_CODE);
//            result.setInfo("系统错误");
//
//        }
        return result;
    }

    @Override
    public NoteResult getBasicInfo() {
        NoteResult result = new NoteResult(FAIL_CODE, FAIL_INFO);

        JSONObject obj = new JSONObject();
        List<JSONObject> bannerList = new ArrayList<JSONObject>();
        try {

            File dir = new File(BANNER_DIR);
            String[] filelist = dir.list();
            // 遍历文件夹下
            if(Detect.notEmpty(filelist)){
                for (int i = 0; i < filelist.length; i++) {
                    // 获取文件名
                    String fileName = filelist[i];
                    // 拼URL
                    String url = BANNER_URL + fileName;
                    JSONObject banner = new JSONObject();
                    banner.put("imageUrl", url);
                    if ("banner1106.png".equals(fileName)){
                        banner.put("directUrl", "https://mp.weixin.qq.com/s?__biz=MzUxOTI3OTYxOQ==&mid=2247483735&idx=1&sn=4e7af5967f419c54796d092828101ee5&chksm=f9fd443bce8acd2d76ac7e78eb9cc570b80c7121d219ddede3caf85baf51578a645df39ac144#rd");
                    }else {
                        banner.put("directUrl", "");
                    }
                    bannerList.add(banner);
                }
            }
            JSONObject service = new JSONObject();
            service.put("service_call",YM_SERVICECALL);
            service.put("service_workStart",YM_SERVICE_WORK_START);
            service.put("service_workEnd",YM_SERVICE_WORK_END);
            obj.put("bannerList", bannerList);
            obj.put("service",service);

            result.setCode(SUCCESS_CODE);
            result.setInfo(SUCCESS_INFO);
            result.setData(obj);

        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(FAIL_CODE);
            result.setInfo("系统错误");

        }
        return result;
    }

    /**
     * 替换四个字节的字符 \xF0\x9F\x98\x84\xF0\x9F的解决方案
     *
     */
    public static String removeFourChar(String content) {
        byte[] conbyte = content.getBytes();
        for (int i = 0; i < conbyte.length; i++) {
            if ((conbyte[i] & 0xF8) == 0xF0) {
                for (int j = 0; j < 4; j++) {
                    conbyte[i + j] = 0x30;
                }
                i += 3;
            }
        }
        content = new String(conbyte);
        return content.replaceAll("0000", "");
    }

    /*
     * 是否可以绑卡
     *
     * @see com.loan_api.app.LoanService#canBinding(java.lang.String)
     */
    @Override
    public NoteResult canBinding(String per_id) {
        NoteResult result = new NoteResult(FAIL_CODE, "请先完成认证再绑定银行卡");

        BpmNode node = findNode(Integer.valueOf(per_id), 3);

        if (node != null) {
            result.setCode(SUCCESS_CODE);
            result.setInfo(SUCCESS_INFO);
        }

        return result;
    }

    @Override
    public NoteResult saveVerifyPhoto(byte[] bytes,String per_id) {
        NoteResult result = new NoteResult(FAIL_CODE, FAIL_INFO);
        long time = System.currentTimeMillis();
        try {
            Card card = cardMapper.selectByPerId(Integer.valueOf(per_id));
            String card_num = card.getCardNum();

            // image表中插入活体图片数据
            Image image = new Image();
            image.setImageType("4");
            image.setImageFormat("jpg");
            // 2017.4.7更新 存硬盘
            // cardz.setImage(card_byte);
            String pathV = time + "id" + card_num + "v.jpg";
            boolean saveV = GenerateImage(bytes, PIC_DIR + pathV);
            if (!saveV) {// 如果存图片失败 直接返回错误
                result.setCode(FAIL_CODE);
                result.setInfo(FAIL_INFO);
                return result;
            }
            image.setImageUrl(pathV);

            int i = imageMapper.insert(image);
            card.setCardPhotov(image.getId().toString());

            int k = cardMapper.updateByPrimaryKeySelective(card);

            if (i + k > 1) {
                result.setCode(SUCCESS_CODE);
                result.setInfo(SUCCESS_INFO);
            }
        } catch (Exception e) {

            e.printStackTrace();
            result.setCode(FAIL_CODE);
            result.setInfo("系统错误");
            return result;
        }
        return result;
    }

    @Override
    public NoteResult juxinliInfo() {

        NoteResult result = new NoteResult(FAIL_CODE, FAIL_INFO);

        try {
            InputStream is = getClass().getResourceAsStream("/../../userInfo.html");
            BufferedReader bis = new BufferedReader(new InputStreamReader(is));
            String fileString = "";
            String szTemp;

            while ((szTemp = bis.readLine()) != null) {
                fileString += szTemp;
            }
            bis.close();

            result.setCode(SUCCESS_CODE);
            result.setData(fileString);
            result.setInfo(SUCCESS_INFO);
        } catch (IOException e) {
            result.setInfo("系统异常或信息不存在");
            return result;
        }

        return result;
    }

    public BpmNode findNode(Integer per_id, Integer node_id) {
        PerBpm perBpm = perBpmMapper.selectByPerId(per_id);

        BpmNode node;

        if (perBpm == null) {
            node = bpmNodeMapper.selectByPerNode(per_id, node_id);

        } else {
            Integer bpmId = perBpm.getId();
            node = bpmNodeMapper.selectByNode_id(bpmId, node_id);
        }

        return node;
    }

    public boolean isPhoneListOK(String per_id) {

        boolean result = false;
        Contact c = contactMapper.selectOne(per_id);
        if (c != null) {
            Date date = c.getUpdateDate();
            result = new Date().before(DateUtils.addDays(date, 30));
        }

        return result;

    }

    @Override
    public NoteResult saveTemPic(byte[] bytes, String per_id, String response) {
        NoteResult result = new NoteResult(FAIL_CODE, FAIL_INFO);

        try {

            RequestMessage rm = new RequestMessage();

            String pathV = System.currentTimeMillis() + "id" + per_id + "head.jpg";

            rm.setCreationDate(new Date());
            rm.setPerId(Integer.valueOf(per_id));
            rm.setResponse(response);
            rm.setDescription(pathV);
            boolean saveV = GenerateImage(bytes, PIC_DIR + pathV);
            if (!saveV) {// 如果存图片失败 直接返回错误
                result.setCode(FAIL_CODE);
                result.setInfo(FAIL_INFO);

            } else {
                int i = requestMessageMapper.insert(rm);
                if (i > 0) {
                    result.setCode(SUCCESS_CODE);
                    result.setInfo(SUCCESS_INFO);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new NoteResult(FAIL_CODE, FAIL_INFO);
        }

        return result;
    }

    // public String getPhone(String per_id) {
    // // 先从redis里取
    // String redis = jedisCluster.get("phonelist_" + per_id);
    //
    // logger.info("---getPhone---phonelist---get:" + redis);
    //
    // if (StringUtils.isEmpty(redis)) {
    // // 缓存中没有 从数据库中取
    // List<Object> ContactList = new ArrayList<Object>();
    // List<Contact> list =
    // contactMapper.selectAllByPerId(Integer.valueOf(per_id));
    // for (Contact c : list) {
    // JSONObject one = new JSONObject();
    // one.put("name", c.getContactName());
    // one.put("phone", c.getContactNum());
    // ContactList.add(one);
    // }
    //
    // // 存到redis里的字符串
    // String newStr = JSONObject.toJSONString(ContactList);
    // jedisCluster.setex("phonelist_" + per_id,
    // JSONObject.toJSONString(newStr));
    // logger.info("---getPhone---phonelist---set:" + newStr);
    // return newStr;
    // } else {
    // return redis;
    // }
    //
    // }

    private void getProdSetRedis() {
        // 构建产品信息json串
        List<ProductApp> products = productMapper.getAllProductAvailable();
//        JSONArray array = new JSONArray();
        Set<JSONObject> array = new TreeSet<>(new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                return o1.getIntValue("prodMoney")*10000+1000+o1.getIntValue("prodDay")-o2.getIntValue("prodMoney")*10000+1000+o2.getIntValue("prodDay");
            }
        });
        for (ProductApp p : products) {
            JSONObject obj = new JSONObject();

            Integer id = p.getId();
            String prodName = p.getProductName();

            String prodDay = p.getDay().toString();
            String prodMoney = p.getMoney().toString();

            //根据产品ID查询产品期数表，获得产品金额，天数，利息(利率*金额*天数)
            ProductTerm term = productTermMapper.selectByProductId(id);
            //借款金额
            BigDecimal amount = term.getMaximumAmount();
            //利率
            BigDecimal rate = term.getMonthlyRate();
            //利息=借款金额*利率
            double mul = amount.multiply(rate).doubleValue();
            String interest = String.format("%.2f", mul);
            List<ProductChargeModel> charges = productChargeModelMapper.selectByProductId(id);
            for (ProductChargeModel charge : charges) {
                String chargeName = charge.getChargeName();
                String chargeAmount = charge.getAmount().toString();
                obj.put(chargeName, chargeAmount);
            }

            obj.put("id", id.toString());
            obj.put("prodDay", prodDay);
            obj.put("prodMoney", prodMoney);
            obj.put("prodName", prodName);
            obj.put("interest", interest);

            array.add(obj);

        }

        // 将最终的产品信息字符串放入redis
        jedisCluster.set(RedisConst.PRODUCT_KEY, JSONArray.toJSONString(array));

    }

    public String saveContacts(String contacts, String per_id,String fileName) {
        logger.info("saveContacts----contacts:" + contacts);
        byte[] bytes = new byte[0];
        try {
            bytes = contacts.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
//            logger.info("fileName:"+fileName);
            FDFSNewUtil.getInstance();
            String path = FDFSNewUtil.uploadFile(bytes, fileName);

            if(StringUtils.isEmpty(path)){
                return "";
            }

            return path;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    public String getContacts(String per_id) {

        String re = "";

        // 判断是文件存储还是数据库存储
        List<Contact> contacts = contactMapper.selectAllByPerId(Integer.valueOf(per_id));

        if (contacts.isEmpty()) {
            // 新用户 没有上传过通讯录
            return "";
        } else if (contacts.size() == 1) {

            // 只有一条记录 且这条记录sync字段为1 那么是文件存储的
            Contact c = contacts.get(0);
            if ("1".equals(c.getSync())) {

                String fileName = c.getContactName();

                if (!"http".equals(fileName.substring(0,4))){
                    fileName = PropertiesReaderUtil.read("third","trackServer") + fileName;
                }

                re = FDFSNewUtil.downloadString(fileName);

            }else{//老用户，且只有一条通讯录（有一次删除过2000W条老数据，每个人只留了一条）
                logger.info("老用户以前删除过通讯录");

                JSONArray array = new JSONArray();

                JSONObject obj = new JSONObject();
                obj.put("name", c.getContactName());
                obj.put("phone", c.getContactNum());
                array.add(obj);

                re = JSONObject.toJSONString(array);
            }
        } else {
            // 多条记录的 非文件存储的老用户
            // 转成 {name:xxx phone:xxx}格式
            JSONArray array = new JSONArray();
            for(Contact c:contacts){
                JSONObject obj = new JSONObject();
                obj.put("name", c.getContactName());
                obj.put("phone", c.getContactNum());
                array.add(obj);
            }

            re = JSONObject.toJSONString(array);
        }

//        logger.info("getContacts----contacts:" + contacts);

        return re;

    }

    /**
     * 初始化用户节点到缓存
     * @return 用户所有节点信息
     */
    private Map<String, String>  initNode(Person p){
        Map<String, String> nodeMap = jedisCluster.hgetAll(RedisConst.NODE_KEY + p.getId());


        if (nodeMap.isEmpty()) {
            // 缓存中没有用户信息 查出用户所有节点
            logger.info("缓存中没有用户信息 查出用户所有节点");
            List<BpmNode> nodes = null;

            if (p.getBpmId() == null || "".equals(p.getBpmId())) {
                // 新流程用户，用per_id来查询
                List<String> ids = bpmNodeMapper.selectMaxNodeId("per_id", p.getId());
                if(ids!=null && !ids.isEmpty()) {
                    nodes = bpmNodeMapper.selectAllNodes(ids);
                    logger.info("新用户,节点数为：" + nodes.size());
                }
            } else {
                // 老流程用户，bpm_id字段不为空 用bpm_id来查询
                List<String> ids = bpmNodeMapper.selectMaxNodeId("bpm_id", p.getBpmId());
                if(ids!=null && !ids.isEmpty()) {
                    nodes = bpmNodeMapper.selectAllNodes(ids);
                    logger.info("老用户,节点数为：" + nodes.size());
                }
            }

            if(nodes!=null) {
                //查出节点后生成map 存入缓存
                for (BpmNode n : nodes) {
                    nodeMap.put(n.getNodeId().toString(), JSONObject.toJSONString(n));
                }
            }

            ZhiMa zhiMa = zhiMaMapper.selectByPer_Id(p.getId());
            if (zhiMa !=null){
                BpmNode zhimaNode =  new BpmNode();
                zhimaNode.setPerId(p.getId());
                zhimaNode.setNodeId(8);//芝麻信用节点编号定为8
                zhimaNode.setUpdateDate(zhiMa.getUpdateDate());
                zhimaNode.setNodeStatus(STATUS_BPM_Y);
                nodeMap.put("2.5", JSONObject.toJSONString(zhimaNode));
            }
//            else{
//                BpmNode zhimaNode =  new BpmNode();
//                zhimaNode.setPerId(p.getId());
//                zhimaNode.setNodeId(8);//芝麻信用节点编号定为8
//                zhimaNode.setNodeStatus(STATUS_BPM_N);
//                nodeMap.put("2.5", JSONObject.toJSONString(zhimaNode));
//            }

            //如果有通讯录 增加通讯录节点
            Contact contact = contactMapper.selectOne(p.getId().toString());
            if (contact !=null){
                BpmNode contactNode =  new BpmNode();
                contactNode.setPerId(p.getId());
                contactNode.setNodeId(7);//通讯录节点编号定为7
                contactNode.setUpdateDate(contact.getUpdateDate());
                contactNode.setNodeStatus(STATUS_BPM_Y);
                nodeMap.put("3.5", JSONObject.toJSONString(contactNode));
            }
            if (!nodeMap.isEmpty()) {
                jedisCluster.hmset(RedisConst.NODE_KEY + p.getId(), nodeMap);
                jedisCluster.expire(RedisConst.NODE_KEY + p.getId(), 7 * 24 * 60 * 60);
            }


        }

        TreeMap<String, String> treeMap = new TreeMap<String, String>(nodeMap);
        return treeMap;
    }

    /*
        根据per_id查询用户的通讯录数量
     */
    private Integer contactNum(String per_id){
        String contacts = getContacts(per_id);
        if (StringUtils.isNotEmpty(contacts)){
            JSONArray contactsArray = JSONObject.parseArray(contacts);
            return contactsArray.size();
        }else {
            return 0;
        }
    }

    @Override
    public NoteResult zhima(String per_id) {
        NoteResult noteResult = new NoteResult(CodeReturn.FAIL_CODE,"系统繁忙,请稍后再试!");
        logger.info("开始进行芝麻:"+per_id+"的芝麻信用授权信息");
        /**
         * zhiMaCreditApi成功
         */
        String success = "1";
        String error = "0";
        try{
            /**
             * 查出身份证和姓名
             */
            logger.info("开始查询用户:"+per_id+"的身份证信息");
            Card card = cardMapper.selectByPerId(Integer.valueOf(per_id));
            if(card == null){
                noteResult.setCode("222");
                noteResult.setInfo("没有查询到身份证信息");
                return noteResult;
            }
            logger.info("开始查询用户:"+per_id+"的身份证信息,查询完毕");
            ReqData reqData = new ReqData();
            reqData.setName(card.getName());
            reqData.setIdNo(card.getCardNum());
            logger.info("开始查询用户:"+per_id+"的芝麻信用授权信息");
            Result auth = zhiMaCreditApi.zhimaAuthInfoAuthquery(reqData);
            logger.info("返回的授权信息:"+JSONObject.toJSONString(auth));
            switch (auth.getCode()){
                case "0":
                    logger.info(per_id+"系统繁忙"+auth.getCode());
                    noteResult.setCode(CodeReturn.FAIL_CODE);
                    noteResult.setInfo("系统繁忙,请稍后重试!");
                    break;
                case "1":
                    String openId = auth.getResult().toString();
                    /**
                     * 已经授权过了 去查询分数
                     */
                    ZhiMa zhiMa = zhiMaMapper.selectByPer_Id(Integer.valueOf(per_id));
                    int sroce = 0;
                    if(zhiMa != null){
                        Date updateDate = zhiMa.getUpdateDate();
                        Date date = new Date();
                        long diffTime = date.getTime() - updateDate.getTime();
                        /**
                         * 芝麻分超过30天 则重新获取
                         */
                        long month = 2592000000L;
                        if(diffTime > month){
                            Result zhiMaSroce = getZhiMaSroce(openId);
                            if(error.equals(zhiMaSroce.getResult())){
                                return noteResult;
                            }
                            String sroceNow = zhiMaSroce.getResult().toString();
                            if(insertZhiMaSroce(per_id,sroceNow,auth.getMessage()) > 0){
                                sroce = Integer.valueOf(sroceNow);
                            }else{
                                return noteResult;
                            }
                        }else{
                            sroce = Integer.valueOf(zhiMa.getZmScore());
                        }
                    }else{
                        logger.info(per_id+"db没数据,开始查询芝麻分数");
                        /**
                         * db没数据 查询一次
                         */
                        Result scoreGet = zhiMaCreditApi.zhimaCreditScoreGet(openId);
                        logger.info(per_id+"查询结果:"+JSONObject.toJSONString(scoreGet));
                        if(success.equals(scoreGet.getCode())){
                            if(insertZhiMaSroce(per_id,scoreGet.getResult().toString(),auth.getMessage()) > 0){
                                sroce = Integer.valueOf(scoreGet.getResult().toString());
                            }else{
                                return noteResult;
                            }
                        }
                    }

                    /**
                     * 分数大于 xxx即可通过
                     */
                    logger.info(per_id+"查询结果:通过");
                    createZhimaPass(per_id);
                    noteResult.setCode(CodeReturn.SUCCESS_CODE);
                    noteResult.setInfo("下一节点");
                    break;
                case "2":
                    logger.info(per_id+"给用户生成授权URL");
                    Result result = zhiMaCreditApi.getZhiMaAuthorize(reqData, "M_APPSDK", "app", "2");
                    logger.info(per_id+"生成结果:"+JSONObject.toJSONString(result));
                    if(error.equals(result.getCode())){
                        noteResult.setCode(CodeReturn.FAIL_CODE);
                        noteResult.setInfo("系统繁忙,请稍后重试!");
                        break;
                    }else if(success.equals(result.getCode())){
                        noteResult.setCode(ZHIMA_TOURL_CODE);
                        noteResult.setData(result.getResult()); //URL
                        noteResult.setInfo("提示用户去授权芝麻信用");
                        break;
                    }
                    break;
                case "3":
                    /**
                     * 直接拒绝
                     */
                    logger.info(per_id+"直接拒绝");
                    noteResult.setCode(ZHIMA_REFUSE_CODE);
                    break;
                case "4":
                    logger.info(per_id+"-该用户没有支付宝账户 或别的情况直接通过");
                    if(insertZhiMaSroce(per_id,"",auth.getMessage()) > 0){
                        createZhimaPass(per_id);
                        noteResult.setCode(CodeReturn.SUCCESS_CODE);
                        noteResult.setInfo("下一节点");
                    }else{
                        return noteResult;
                    }
                    break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return noteResult;
    }

    private void createZhimaPass(String per_id) {
        BpmNode zhimaNode = new BpmNode();
        zhimaNode.setNodeId(8);
        zhimaNode.setNodeStatus(STATUS_BPM_Y);
        zhimaNode.setUpdateDate(new Date());
        zhimaNode.setPerId(Integer.valueOf(per_id));
        jedisHset(per_id, RedisConst.NODE_KEY + per_id,"2.5", JSONObject.toJSONString(zhimaNode));
    }

    /**
     * 获取芝麻分的方法
     * @param openId
     * @return
     */
    private Result getZhiMaSroce(String openId){
        return zhiMaCreditApi.zhimaCreditScoreGet(openId);
    }

    private int insertZhiMaSroce(String per_id,String sroce,String description){
        ZhiMa zhiMa = new ZhiMa();
        zhiMa.setCreationDate(new Date());
        zhiMa.setPerId(Integer.valueOf(per_id));
        zhiMa.setZmScore(sroce);
        zhiMa.setUpdateDate(new Date());
        zhiMa.setDescription(description);
        //缓存也要更新
        BpmNode zhimaNode =  new BpmNode();
        zhimaNode.setPerId(Integer.valueOf(per_id));
        zhimaNode.setNodeId(8);//芝麻信用节点编号定为8
        zhimaNode.setUpdateDate(zhimaNode.getUpdateDate());
        zhimaNode.setNodeStatus(STATUS_BPM_Y);
        jedisHset(per_id,RedisConst.NODE_KEY + per_id,"2.5",JSONObject.toJSONString(zhimaNode));

        return zhiMaMapper.insert(zhiMa);
    }

    /*
      统一的hset方法   hset之前要判断key是否存在，如果key不存在会有问题。
     */
    public void jedisHset(String per_id,String key,String field,String value){
        if(!jedisCluster.exists(key)){
            Person p = personMapper.selectByPrimaryKey(Integer.valueOf(per_id));
            initNode(p);
        }
        jedisCluster.hset(key, field, value);
    }

    /*
    分配审核人方法
     */
    private int setReviewer(Integer borrId,String emp_num){
        //幂等操作
        if (StringUtils.isEmpty(jedisCluster.get(RedisConst.REVIEW_KEY + borrId))){
            String setnx = jedisCluster.set(RedisConst.REVIEW_KEY + borrId, borrId.toString(), "NX", "EX", 30 * 60);
            if(!"OK".equals(setnx) ) {
                logger.error("直接返回，重复数据审核分配" + borrId);
                return 0;
            }
        }else{
            logger.error("直接返回，重复数据审核分配" + borrId);
            return 0;
        }

        //分配审核人之前先看有没有审核人 如果有 直接返回1
        if (reviewMapper.selectReview(borrId) > 0){
            return 1;
        }
        //新增审核表记录
        Integer sum = reviewMapper.reviewSum();
        if(sum == null){
            sum = 0;
        }
        //获得所有审核人的员工编号
        List<String> reviewerList = riewerMapper.selectEmployNum();
        //给该borrowList设置审核人
        int turn = sum%reviewerList.size();

        Review review = new Review();
        review.setBorrId(borrId);
        review.setReviewType("1");
        //如果员工编号传空，自动分配
        if (StringUtils.isEmpty(emp_num)) {
            review.setEmployNum(reviewerList.get(turn));
        }else{
            review.setEmployNum(emp_num);
        }
        return reviewMapper.insertSelective(review);
    }

    public void signRobot(String borrId){
        Date da = new Date();
         logger.info("当前时间为：" + da.getHours() + "时");
        boolean flag1 = da.getHours() >= 9;
        boolean flag2 = da.getHours() < 17;
        logger.info("flag1:"+flag1+"---------flag2:"+flag2);
        if (flag1 && flag2) {
            logger.info("进入机器人审核");
            String url = PropertiesReaderUtil.read("third","robotUrl");
            Map<String,String> map = new HashMap<>();
            map.put("borrId",borrId);
            HttpUrlPost.sendPost(url, map);
        }
    }

    public int deleteAndInsert(String per_id, String path){
        if (StringUtils.isEmpty(path)){
            //没存成功就传""
            return 0;
        }
        // 存储成功  删除老通讯录
        contactMapper.deleteByPerId(Integer.valueOf(per_id));

        // 数据库contact表加一条数据
        Contact contact = new Contact();
        contact.setPerId(Integer.valueOf(per_id));
        // 联系人姓名存为fileName 作为以后查找依据
        contact.setContactName(path);
        // sync作为标识字段 空则为老数据 1为文件存储
        contact.setSync("1");
        logger.info("getPhoneList----contact:" + JSONObject.toJSONString(contact));

        int i = contactMapper.insertSelective(contact);

        return i;
    }

    @Override
    public NoteResult getProductInfo(String per_id) {
        NoteResult result = new NoteResult(FAIL_CODE, FAIL_INFO);
        try {
            if (StringUtils.isEmpty(jedisCluster.get(RedisConst.PRODUCT_KEY))){
                getProdSetRedis();
            }
            JSONObject data = new JSONObject();
            List<JSONObject> coupons = new ArrayList<JSONObject>();
            if(per_id!=null && !"".equals(per_id)){
                //根据per_id,product_id,使用状态查询用户可用的优惠券
                List<PerCoupon> perCoupons = perCouponMapper.selectByPerIdStatus(per_id,"1");

                for(PerCoupon coupon:perCoupons){
                    JSONObject couponJSON = new JSONObject();
                    double couponAmount = Double.valueOf(coupon.getAmount());
                    couponJSON.put("id", coupon.getId());
                    couponJSON.put("name", coupon.getCouponName());
                    couponJSON.put("amount", String.format("%.2f",couponAmount));
                    couponJSON.put("productId", coupon.getProductId().toString());
                    coupons.add(couponJSON);
                }

            }
            data.put("product", JSONArray.parse(jedisCluster.get(RedisConst.PRODUCT_KEY)));
            data.put("coupons", coupons);
            result.setData(data);
            result.setCode(SUCCESS_CODE);
            result.setInfo(SUCCESS_INFO);
        }catch (Exception e){
            e.printStackTrace();
            return new NoteResult(FAIL_CODE, FAIL_INFO);
        }
        return result;
    }



    public static void main(String[] args) {
      String a = HttpClientUtil.getInstance().doGetRequest("http://192.168.1.63/group1/M01/00/3F/wKgBP1ob6aGAdOi4AAAPOIszrg0.ym_con");
        String aa="";
        try {
            aa = new String(a.getBytes("GBK"),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.print(aa);
    }

}
