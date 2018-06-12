package com.loan_server.contract_service;

import com.alibaba.fastjson.JSONObject;
import com.loan_api.contract.ElectronicContractService;
import com.loan_entity.app.*;
import com.loan_entity.common.Constants;
import com.loan_entity.contract.Contract;
import com.loan_entity.contract.IdEntity;
import com.loan_server.app_mapper.BorrowListMapper;
import com.loan_server.app_mapper.PerCouponMapper;
import com.loan_server.app_mapper.ProductChargeModelMapper;
import com.loan_server.app_mapper.ProductTermMapper;
import com.loan_server.contract_mapper.ContractMapper;
import com.loan_server.manager_mapper.CodeValueMapper;
import com.loan_utils.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wanzezhong on 2017/11/23.
 * @author carl.wan
 */
public class ElectronicContractServiceImpl implements ElectronicContractService {

    @Autowired
    private BorrowListMapper borrowListMapper;

    @Autowired
    private ProductTermMapper productTermMapper;

    @Autowired
    private PerCouponMapper perCouponMapper;

    @Autowired
    private CodeValueMapper codeValueMapper;

    @Autowired
    private ProductChargeModelMapper productChargeModelMapper;

    @Autowired
    private ContractMapper contractMapper;

    @Value("${contract.url}")
    private String contractUrl;

    @Value("${contract.url.generate}")
    private String generateUrl;

    @Value("${contract.url.preview}")
    private String previewUrl;

    @Value("${contract.url.download}")
    private String downloadUrl;

    @Value("${contract.token}")
    private String token;

    @Value("${contract.productId}")
    private String productId;

    @Override
    public String createElectronicContract(Integer borrId) {
        return createElectronicContract(borrId, productId);
    }

    @Override
    public String createElectronicContract(Integer borrId, String productId) {
        Map data;
        if (this.productId.equals(productId) || "19".equals(productId)) {
            data = getContractDate(borrId);
        }else {
            data = getAuthContractData(borrId);
        }

        return createElectronicContract(borrId, productId, data);
    }

    private String createElectronicContract(Integer borrId, String productId, Map data) {
        Integer contractStatus = Constants.ContractStatus.EXCEPTION;
        String result = "";
        try {
            Map param = new HashMap();
            param.put("token", token);
            param.put("productId", productId);
            param.put("contractNo", data.get("#borrNum#"));
            param.put("cardNo", data.get("#cardId#"));
            param.put("name", data.get("#name#"));
            param.put("mobile", data.get("#phone#"));
            param.put("data", JSONObject.toJSONString(data));

            result = HttpUtils.sendPost(contractUrl + generateUrl, HttpUtils.toParam(param));
            if (Detect.notEmpty(result)){
                JSONObject jsonResult = JSONObject.parseObject(result);
                JSONObject jsonData = jsonResult.getJSONObject("data");
                if(jsonData.getString("code").equals("10000")){
                    contractStatus = Constants.ContractStatus.CREATEING;
                }
            }
            //创建合同
            creatContract(borrId, productId, contractStatus, result);
        } catch (Exception e) {
            creatContract(borrId, productId, contractStatus, e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String queryElectronicContract(Integer borrId) {
        String result = "";
        Map data = getContractDate(borrId);
        Map param = new HashMap();
        param.put("token",token);
        param.put("productId",productId);
        param.put("serialNo",borrId);
        param.put("data",JSONObject.toJSONString(data));

        try {
            result = HttpUtils.sendPost(contractUrl + previewUrl, HttpUtils.toParam(param));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String downElectronicContract(String borrNum) {
        return downElectronicContract(borrNum, productId);
    }

    @Override
    public String downElectronicContract(String borrNum, String productId) {
        Assertion.notEmpty(borrNum,"合同号不能为空");
        NoteResult noteResult = new NoteResult();
        noteResult.setCode(CodeReturn.FAIL_CODE);
        noteResult.setInfo("合同下载文件正在生成中，请两分钟后再试...");
        noteResult.setData("合同下载文件正在生成中，请两分钟后再试...");
        //查找本地合同是否存在
        Contract contract = new Contract();
        contract.setBorrNum(borrNum);
        contract.setProductId(productId);
        contract = contractMapper.selectOne(contract);
        if(contract != null ){
            if(contract.getStatus().equals(Constants.ContractStatus.SUCESS)){
                noteResult.setCode(CodeReturn.SUCCESS_CODE);
                noteResult.setData(contract.getContractUrl());
                noteResult.setInfo("");
                return JSONObject.toJSONString(noteResult);
            }else {
                Map param = new HashMap();
                param.put("contractNo", borrNum);
                try {
                    String result = HttpUtils.sendPost(contractUrl + downloadUrl, HttpUtils.toParam(param));
                    //正常生成合同查看
                    if (Detect.notEmpty(result)) {
                        JSONObject jsonResult = JSONObject.parseObject(result);
                        JSONObject jsonData = jsonResult.getJSONObject("data");
                        if (jsonData.getString("code").equals("10000")) {
                            noteResult.setCode(CodeReturn.SUCCESS_CODE);
                            noteResult.setData(jsonData.getString("data"));
                            noteResult.setInfo("成功");
                            saveContract(contract.getBorrId(), productId, Constants.ContractStatus.SUCESS, "", jsonData.getString("data"));
                            return JSONObject.toJSONString(noteResult);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //未生成正常合同
        BorrowList bl = borrowListMapper.selectByBorrNum(borrNum);
        if(bl != null){
            createElectronicContract(bl.getId());
        }

        return JSONObject.toJSONString(noteResult);
    }

    @Override
    public String callBack(String code, String url, String borrNum) {
        return callBack(code, url, borrNum, productId);
    }

    @Override
    public String callBack(String code, String url, String borrNum, String productId) {
        //查找本地合同是否存在
        Example example = new Example(Contract.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("borrNum", borrNum);
        criteria.andEqualTo("productId", productId);

        List<Contract> contractList = contractMapper.selectByExample(example);
        if (null == contractList || contractList.size() < 1) {
            return null;
        }

        Contract contract = contractList.get(0);
        if (!Detect.isPositive(contract.getBorrId())) {
            return null;
        }

        int status = Constants.ContractStatus.FAIL;
        if(code.equals("10000")){
            status = Constants.ContractStatus.SUCESS;
        }
        saveContract(contract.getBorrId(), contract.getProductId(), status, code, url);
        return null;
    }

    @Override
    public void disposeExceptionContract() {
        Example example = new Example(Contract.class);
        Set<Integer> inSet = new HashSet();
        inSet.add(Constants.ContractStatus.SUCESS);
        example.createCriteria().andNotIn("status", inSet);
        //查询失败异常合同
        List<Contract> contracts = contractMapper.selectByExample(example);
        if(Detect.notEmpty(contracts)){
            //调用下载接口
            for (Contract constant : contracts){
                downElectronicContract(constant.getBorrNum());
            }
        }
    }

    private Map<String, String> getAuthContractData(Integer borrId) {
        Assertion.isPositive(borrId,"合同Id不能为空");
        Map<String, String> person = borrowListMapper.selectPersonByBorrId(borrId);
        if ( person == null || person.isEmpty()) {
            return null;
        }

        Map<String, String> map = new HashMap();
        map.put("#CardID#", person.get("cardNum"));
        map.put("#cardId#", person.get("cardNum"));
        map.put("#name#", person.get("name"));
        map.put("#client#", person.get("name"));
        map.put("#year1#", person.get("regDate").substring(0, 4));
        map.put("#month1#", person.get("regDate").substring(5, 7));
        map.put("#day1#", person.get("regDate").substring(8, person.get("regDate").length()));
        map.put("#year2#", person.get("regDate").substring(0, 4));
        map.put("#month2#", person.get("regDate").substring(5, 7));
        map.put("#day2#", person.get("regDate").substring(8, person.get("regDate").length()));
        map.put("#borrNum#", person.get("borrNum"));
        map.put("#phone#", person.get("phone"));
        return map;
    }

    private Map getContractDate(Integer borrId){
        Assertion.isPositive(borrId,"合同Id不能为空");

        IdEntity idEntity = borrowListMapper.queryIdentityById(borrId);
        Integer prodId = idEntity.getProdId();
        ProductTerm term = productTermMapper.selectByProductId(prodId);
        //1.借款金额      2017.3月更新  借款金额从产品表中读取
        String money = String.format("%.2f", term.getMaximumAmount());

        //3.借款天数
        String day = String.valueOf(term.getTermValue());
        // logger.debug(money + "," + day);
        NoteResult charge = getProductCharge(prodId, idEntity.getPerId() + "", money.substring(0,money.lastIndexOf(".")), day);
        JSONObject fee = (JSONObject)charge.getData();
        // logger.debug(JSONObject.toJSONString(fee));
        //4.利息
        String interest = fee.getString("interest");
        //5.信审费
        String review = fee.getString("review");
        //6.平台费
        String plat = fee.getString("plat");


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        long now = DateUtil.dateToStamp(idEntity.getBorrDate());
//        long planRepayTime = now + term.getTermValue()*24*60*60*1000L;
        //11.签订日期 、借款日期（今天）
        String borrowDate = sdf.format(new Date(now));
        //16.签订年
        String signYear = borrowDate.substring(0,4);
        //17.签订月
        String signMonth = borrowDate.substring(5,7);
        //18.签订日
        String signDay = borrowDate.substring(8,borrowDate.length()-1);
        //12.预计还款日期
//        String planDate = sdf.format(new Date(planRepayTime));

        String couponId = idEntity.getPerCouponId();
        //13.优惠券金额
        String couponMoney = "0.00";
        if(!"0".equals(couponId)){//使用了优惠券
            PerCoupon coupon = perCouponMapper.selectByPrimaryKey(Integer.valueOf(couponId));
            couponMoney = String.format("%.2f", Double.valueOf(coupon.getAmount()));
        }
        //2.还款金额
        Double planRepay = Double.valueOf(money) + Double.valueOf(interest) + Double.valueOf(review) + Double.valueOf(plat) - Double.valueOf(couponMoney);
        //14.手续费
        String thirdFee = codeValueMapper.getMeaningByTypeCode("payment_fee", "1");
        Map<String, String> map = new HashMap();
        map.put("#bankCardNo#",idEntity.getCardNum());
        map.put("#bankName#", idEntity.getBankName());
        map.put("#bankNumber#",idEntity.getBankNum());
        map.put("#bankPhone#",idEntity.getBankPhone());
        map.put("#signDate#",DateUtil.getDateString(DateUtil.getDate(idEntity.getBorrDate())));
        map.put("#day#", day);
        map.put("#interest#",interest);
        map.put("#money#",money);
        map.put("#email#",idEntity.getEmail());
        map.put("#plat#",plat);
        map.put("#review#",review);
        map.put("#bankNum#",idEntity.getBankNum());
        map.put("#cardId#", idEntity.getCardNum());
        map.put("#name#",idEntity.getName());
        map.put("#borrNum#", idEntity.getBorrNum());
        map.put("#phone#",idEntity.getPhone());
        map.put("#borrowDate#", DateUtil.getDateString(DateUtil.getDate(idEntity.getPayDate())));
        map.put("#couponMoney#",couponMoney);
        map.put("#planRepay#",planRepay + "");
        map.put("#signDay#",signDay);
        map.put("#signMonth#",signMonth);
        map.put("#signYear#",signYear);
        map.put("#thirdFee#",thirdFee);
        map.put("#planDate#",idEntity.getPlanRepayDate());
        map.put("#loanName#","方伟");
        map.put("#loanCardNum#","321322198706184814");

        return map;
    }

    /**
     * 选择产品金额和天数,获得产品所有费用
     * @param per_id 用户ID
     * @return
     */
    public NoteResult getProductCharge(Integer productId, String per_id,String money,String day){
        //构建返回结果对象NoteResult
        NoteResult result = new NoteResult(CodeReturn.FAIL_CODE,"fail");
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
            List<ProductChargeModel> charges = productChargeModelMapper.selectByProductId(Integer.valueOf(product_id));

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
                result.setCode(CodeReturn.SUCCESS_CODE);
                result.setInfo("success");
                result.setData(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new NoteResult(CodeReturn.FAIL_CODE,"fail");
        }
        return result;
    }

    /**
     * 创建电子合同
     * @param borrId
     * @param status
     */
    private void creatContract(Integer borrId, String productId, Integer status, String msg){
        if (!Detect.isPositive(borrId)) {
            return;
        }

        if (!Detect.notEmpty(productId)) {
            return;
        }

        BorrowList bl = borrowListMapper.selectByPrimaryKey(borrId);
        if (null == bl) {
            return;
        }

        Contract contract = new Contract();
        contract.setBorrNum(bl.getBorrNum());
        contract.setBorrId(borrId);
        contract.setProductId(productId);
        contract.setCreateDate(Calendar.getInstance().getTime());
        contract.setStatus(status);
        contract.setResultJson(msg);
        contractMapper.insertContract(contract);
    }

    /**
     * 保存电子合同
     * @param borrId
     * @param status
     */
    private void saveContract(Integer borrId, String productId, Integer status, String msg, String url){
        if(Detect.isPositive(borrId) && Detect.isPositive(status)){
            Contract contract = new Contract();
            contract.setBorrId(borrId);
            contract.setProductId(productId);
            contract = contractMapper.selectOne(contract);

            if(contract != null){
                contract.setStatus(status);
                if(Detect.notEmpty(msg)){
                    contract.setResultJson(msg);
                }
                contract.setContractUrl(url);
                contractMapper.updateByPrimaryKeySelective(contract);
            }
        }
    }

    public String getContractUrl() {
        return contractUrl;
    }

    public void setContractUrl(String contractUrl) {
        this.contractUrl = contractUrl;
    }

    public String getGenerateUrl() {
        return generateUrl;
    }

    public void setGenerateUrl(String generateUrl) {
        this.generateUrl = generateUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
