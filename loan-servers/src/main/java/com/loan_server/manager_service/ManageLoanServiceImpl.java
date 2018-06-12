package com.loan_server.manager_service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.loan_api.app.LoanService;
import com.loan_entity.app.*;
import com.loan_server.app_mapper.*;
import com.loan_utils.util.*;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.alibaba.fastjson.JSON;
import com.loan_api.app.UserService;
import com.loan_api.manager.ManageLoanService;
import com.loan_entity.loan.PerAccountLog;
import com.loan_entity.manager.Order;
import com.loan_entity.manager.RepaymentPlan;
import com.loan_entity.manager.Review;
import com.loan_entity.manager_vo.BankInfoVo;
import com.loan_entity.manager_vo.CardPicInfoVo;
import com.loan_entity.manager_vo.LoanInfoVo;
import com.loan_entity.manager_vo.PrivateVo;
import com.loan_entity.manager_vo.RepaymentPlanVo;
import com.loan_entity.manager_vo.ReqBackPhoneCheckVo;
import com.loan_entity.manager_vo.ReviewVo;
import com.loan_entity.utils.ManagerResult;
import com.loan_entity.utils.ManagerResultForNet;
import com.loan_server.loan_mapper.PerAccountLogMapper;
import com.loan_server.manager_mapper.ManagerOfLoanMapper;
import com.loan_server.manager_mapper.OrderMapper;
import com.loan_server.manager_mapper.RepaymentPlanMapper;
import com.loan_server.manager_mapper.ReviewMapper;

/**
 * 描述：
 *
 * @version 1.0
 * @author: Wanyan
 * @date： 日期：2016年10月19日 时间：上午11:54:11
 */

public class ManageLoanServiceImpl implements ManageLoanService {
    private static Logger log = Logger.getLogger(ManageInfoServiceImpl.class);
    //图片硬盘地址
    private static final String PIC_DIR = PropertiesReaderUtil.read("third", "picDir");
    @Autowired
    private ManagerOfLoanMapper managerOfLoanMapper;
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private ZhiMaMapper zhiMaMapper;
    //	@Autowired
//    private BpmNodeMapper bpmNodeMapper;
//	@Autowired
//	private DsAppReportMapper dsAppReportMapper;
//	@Autowired
//	ThreadPoolTaskExecutor threadPool;
//	@Autowired
//	SynDateTask synDateTask;
    @Autowired
    private LoanService loanService;

    //	@Autowired
//	private JedisCluster jedisCluster;
    public ManagerResult backPhoneCheckMessage(ReqBackPhoneCheckVo record) {

        return null;
    }

    /**
     * 风控五条规则触碰更新人工审核
     *
     * @param perId
     * @param isManual
     */
//	private void manuallyReview(Integer perId, String isManual, String description){
//		log.info("ManageLoanService.manuallyReview start: per_id = " + perId + "isManual = " + isManual);
//    	Assertion.isPositive(perId, "用户Id不能为空");
//    	Person person = personMapper.selectByPrimaryKey(Integer.valueOf(perId));
//    	if(person != null){
//    		if(Detect.notEmpty(description)){
//    			if(Detect.notEmpty(person.getDescription()) && person.getDescription().length() < 20){
//    				description = person.getDescription() + "#" + description;
//    			}
//    			person.setDescription(description);
//    		}
//        	person.setIsManual(isManual);
//        	personMapper.updateByPrimaryKeySelective(person);
//        	log.info("ManageLoanService.manuallyReview end: person.getId = " + person.getId());
//    	}
//     }

	/*
     * (non-Javadoc)
	 * 
	 * @see
	 * com.loan_api.manager.ManageLoanService#offlineTransfer(java.lang.String)
	 */
    @Autowired
    PerAccountLogMapper perAccountLogMapper;

    @Override
    public ManagerResultForNet offlineTransfer(String record) {
        ManagerResultForNet managerResultForNet = new ManagerResultForNet();
        // System.out.println("字符串=" + record);

        com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(record);
        String body = jsonobj.getString("Body");
        // System.out.println("biody+++++=" + body);
        com.alibaba.fastjson.JSONObject CONbject = JSON.parseObject(body);
        String Content = CONbject.getString("Content");
        String Content2 = CONbject.getString("Content2");

        com.alibaba.fastjson.JSONObject ContentObj = JSON.parseObject(Content);
        com.alibaba.fastjson.JSONObject ContentObj2 = JSON
                .parseObject(Content2);

        BorrowList borrowList = new BorrowList();
        borrowList.setBorrNum(ContentObj.getString("brrowNum"));
        // borrowList.setActRepayDate(ContentObj.getDate("actRepayDate"));
        if (ContentObj.getString("actRepayDate") != null
                && !("").equals(ContentObj.getString("actRepayDate"))) {
            String hh = ContentObj.getString("actRepayDate").replace("T", " ");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date hhobj = format.parse(hh);
                borrowList.setActRepayDate(hhobj);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        borrowList.setActRepayAmount(ContentObj.getString("actRepayAmount"));
        borrowList.setBorrStatus(ContentObj.getString("borrStatus"));
        borrowList.setPlanRepay(ContentObj.getString("planRepay"));

        RepaymentPlanVo repaymentPlanVo = new RepaymentPlanVo();
        repaymentPlanVo.setBorrNum(ContentObj.getString("brrowNum"));
        repaymentPlanVo.setIsSettle(ContentObj.getInteger("isSettle"));
        repaymentPlanVo.setPenalty(ContentObj.getBigDecimal("penalty"));
        repaymentPlanVo.setPenaltyInterest(ContentObj
                .getBigDecimal("penaltyInterest"));
        repaymentPlanVo.setSurplusQuota(ContentObj.getString("surplusQuota"));
        repaymentPlanVo.setSurplusMoney(ContentObj.getString("surplusMoney"));
        repaymentPlanVo.setSurplusInterest(ContentObj
                .getString("surplusInterest"));
        repaymentPlanVo.setSurplusPenalty(ContentObj
                .getString("surplusPenalty"));
        repaymentPlanVo.setSurplusPenaltyInteres(ContentObj
                .getString("surplusPenaltyInteres"));

        // System.out.println("Content2+=" + Content2);

        BorrowList borrowList2 = borrowListMapper.selectNow(ContentObj2
                .getInteger("per_id"));

        Order order = new Order();
        order.setpId(ContentObj2.getInteger("p_id"));
        order.setSerialNo(ContentObj2.getString("serial_no"));
        order.setCompanyId(0);
        order.setPerId(ContentObj2.getInteger("per_id"));
        // order.setBankId(ContentObj2.getInteger("bank_id"));不要了
        order.setConctactId(borrowList2.getId());
        order.setOptAmount(ContentObj2.getString("opt_amount"));
        order.setActAmount(ContentObj2.getString("act_amount"));
        // order.setRlDate(ContentObj2.getTimestamp("rl_date"));
        if (ContentObj.getString("rl_date") != null
                && !("").equals(ContentObj.getString("rl_date"))) {
            String hh = ContentObj.getString("rl_date").replace("T", " ");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date hhobj = format.parse(hh);
                order.setRlDate(hhobj);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        order.setRlRemark(ContentObj2.getString("rl_remark"));
        order.setRlState(ContentObj2.getString("rl_state"));
        order.setType("6");
        order.setReason(ContentObj2.getString("reason"));
        // order.setStatus(ContentObj2.getString("status"));
        order.setStatus("y");
        order.setCreationDate(new Date());
        try {
            int result1 = borrowListMapper.updateByBrroNum(borrowList);
            int result2 = repaymentPlanMapper.updateByBrroNum(repaymentPlanVo);
            int result3 = orderMapper.insert(order);
            if (result1 + result2 + result3 > 0) {
                managerResultForNet.setCode("0000");
                managerResultForNet.setMessage("处理成功！");
            } else {
                managerResultForNet.setCode("2001");
                managerResultForNet.setMessage("处理失败！");
            }
        } catch (Exception e) {
            managerResultForNet.setCode("2002");
            managerResultForNet.setMessage("抛异常！" + e.getMessage());
            log.error(e.getMessage());
        }
        // System.out.println("+++orderkey+++="+order.getId());
        PerAccountLog perAccountLog = new PerAccountLog();
        perAccountLog.setAddtime(new Date());
        perAccountLog.setPerId(ContentObj2.getInteger("per_id"));
        perAccountLog.setOrderId(order.getId());
        perAccountLog.setOperationType("11");
        perAccountLog.setAmount(ContentObj2.getString("opt_amount"));
        perAccountLog.setRemark("线下还款");
        perAccountLog.setDeleted(0);

        int result4 = perAccountLogMapper.insert(perAccountLog);

        return managerResultForNet;
    }

    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private UserService userService;

    public ManagerResult personCheckMessage(String brroid, String status,
                                            String employ_num, String reason) {
        ManagerResult managerResult = new ManagerResult();
        Review review = new Review();
        review.setBorrId(Integer.parseInt(brroid));
        review.setStatus(status);
        review.setEmployNum(employ_num);
        review.setReason(reason);
        try {
            int result = managerOfLoanMapper.personCheckMessage(review);
            managerResult.setCode(result);
            if (result >= 0) {
                managerResult.setMessage("处理成功！");
                BorrowList borrowList = borrowListMapper
                        .selectByPrimaryKey(Integer.parseInt(brroid));

                if ((review.getStatus()).equals("n")
                        && !(borrowList.getBorrStatus()).equals("BS009")) {

//					int resultblack = reviewMapper.blackListStatus(brroid);
//					System.out.println("resultblack=" + resultblack);

                    PersonMode personMode = personMapper
                            .getPersonInfoByBorr(brroid);
                    // 发送消息给用户
                    String messageresult = userService.setMessage(
                            String.valueOf(personMode.getPerId()), "3",
                            personMode.getName());
                    JSONObject obje = JSONObject.fromObject(messageresult);
                    String neirong = obje.get("data").toString();
                    // 短信接口，要加标题模版
                    String remessage = SmsUtil.checkNoSms(SmsUtil.MGFKNO_CODE,
                            personMode.getName(), personMode.getPhone());
                    // 2017.4.19更新 短信send第三个参数 0-悠兔 ，1-悠米，2-吾老板
                    if (EmaySmsUtil.send(remessage, personMode.getPhone(), 1)) {
                        log.info("放款拒绝短信发送成功！");
                    } else {
                        log.info("放款拒绝短信发送失败！");
                    }
                    if ("200".equals(obje.get("code") + "")) {
                        log.info("放款拒绝消息发送成功！");
                    } else {
                        log.info("放款拒绝异常" + obje.get("info").toString());
                    }

                }
            } else {
                managerResult.setMessage("处理失败！");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return managerResult;
    }

    public ManagerResult transferPersonCheck(String brroid_list, String transfer) {
        String[] brroid = brroid_list.split(",");

        ManagerResult managerResult = new ManagerResult();
        for (int i = 0; i < brroid.length; i++) {

            Review review = new Review();
            review.setBorrId(Integer.parseInt(brroid[i]));
            review.setEmployNum(transfer);
            try {
                int result = managerOfLoanMapper.transferPersonCheck(review);
                managerResult.setCode(result);
                if (result > 0) {
                    managerResult.setMessage("处理成功！");
                } else {
                    managerResult.setMessage("处理失败！");
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return managerResult;
    }

    @Autowired
    private BorrowListMapper borrowListMapper;

    public ManagerResult UpdateBorrowList(BorrowList record) {
        ManagerResult managerResult = new ManagerResult();
        try {
            BorrowList borrowList = borrowListMapper.selectByPrimaryKey(record.getId());
            record.setVersion(borrowList.getVersion());
            int result = borrowListMapper.updateByPrimaryKeySelective(record);
            managerResult.setCode(result);
            if (result > 0) {
                managerResult.setMessage("处理成功！");
            } else {
                managerResult.setMessage("处理失败！");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return managerResult;
    }

    @Autowired
    private RepaymentPlanMapper repaymentPlanMapper;

    public ManagerResult insertRepaymentPlan(RepaymentPlan record) {
        ManagerResult managerResult = new ManagerResult();
        try {
            int result = repaymentPlanMapper.insert(record);
            managerResult.setCode(result);
            if (result > 0) {
                managerResult.setMessage("处理成功！");
            } else {
                managerResult.setMessage("处理失败！");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return managerResult;
    }

    public ManagerResult UpdateRepaymentPlan(RepaymentPlan record) {
        ManagerResult managerResult = new ManagerResult();
        try {
            int result = repaymentPlanMapper
                    .updateByPrimaryKeySelective(record);
            managerResult.setCode(result);
            if (result > 0) {
                managerResult.setMessage("处理成功！");
            } else {
                managerResult.setMessage("处理失败！");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return managerResult;
    }

    @Autowired
    private OrderMapper orderMapper;

    public ManagerResult insertOrder(Order record) {
        ManagerResult managerResult = new ManagerResult();
        try {
            int result = orderMapper.insert(record);
            managerResult.setCode(result);
            if (result > 0) {
                managerResult.setMessage("处理成功！");
            } else {
                managerResult.setMessage("处理失败！");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return managerResult;
    }

    @Autowired
    private BankMapper bankMapper;

    public ManagerResult insertBank(Bank record) {
        ManagerResult managerResult = new ManagerResult();
        try {
            int result = bankMapper.insert(record);
            managerResult.setCode(result);
            if (result > 0) {
                managerResult.setMessage("处理成功！");
            } else {
                managerResult.setMessage("处理失败！");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return managerResult;
    }

    public ManagerResult UpdateBank(Bank record) {
        ManagerResult managerResult = new ManagerResult();
        try {
            int result = bankMapper.updateByPrimaryKeySelective(record);
            managerResult.setCode(result);
            if (result > 0) {
                managerResult.setMessage("处理成功！");
            } else {
                managerResult.setMessage("处理失败！");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return managerResult;
    }

    @Autowired
    private ProductMapper productMapper;

    public ManagerResult insertProduct(Product record) {
        ManagerResult managerResult = new ManagerResult();
        try {
            int result = productMapper.insert(record);
            managerResult.setCode(result);
            if (result > 0) {
                managerResult.setMessage("处理成功！");
            } else {
                managerResult.setMessage("处理失败！");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return managerResult;
    }

    public ManagerResult UpdateProduct(Product record) {
        ManagerResult managerResult = new ManagerResult();
        try {
            int result = productMapper.updateByPrimaryKeySelective(record);
            managerResult.setCode(result);
            if (result > 0) {
                managerResult.setMessage("处理成功！");
            } else {
                managerResult.setMessage("处理失败！");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return managerResult;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.loan_api.manager.ManageLoanService#getAllProduct()
     */
    @Override
    public List<Product> getAllProduct() {
        // TODO Auto-generated method stub
        return productMapper.getAllProduct();
    }

    @Autowired
    private ProductTermMapper productTermMapper;

    public ManagerResult insertProductTerm(ProductTerm record) {
        ManagerResult managerResult = new ManagerResult();
        try {
            int result = productTermMapper.insert(record);
            managerResult.setCode(result);
            if (result > 0) {
                managerResult.setMessage("处理成功！");
            } else {
                managerResult.setMessage("处理失败！");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return managerResult;
    }

    public ManagerResult UpdateProductTerm(ProductTerm record) {
        ManagerResult managerResult = new ManagerResult();
        try {
            int result = productTermMapper.updateByPrimaryKeySelective(record);
            managerResult.setCode(result);
            if (result > 0) {
                managerResult.setMessage("处理成功！");
            } else {
                managerResult.setMessage("处理失败！");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return managerResult;
    }

    @Autowired
    private ProductChargeModelMapper productChargeModelMapper;

    public ManagerResult insertProductChargeModel(ProductChargeModel record) {
        ManagerResult managerResult = new ManagerResult();
        try {
            int result = productChargeModelMapper.insert(record);
            managerResult.setCode(result);
            if (result > 0) {
                managerResult.setMessage("处理成功！");
            } else {
                managerResult.setMessage("处理失败！");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return managerResult;
    }

    public ManagerResult UpdateProductChargeModel(ProductChargeModel record) {
        ManagerResult managerResult = new ManagerResult();
        try {
            int result = productChargeModelMapper
                    .updateByPrimaryKeySelective(record);
            managerResult.setCode(result);
            if (result > 0) {
                managerResult.setMessage("处理成功！");
            } else {
                managerResult.setMessage("处理失败！");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return managerResult;
    }

    @Override
    public List<BorrowList> getBorrList(String borrIds, String borrStatus) {
        if (Detect.notEmpty(borrIds) || Detect.notEmpty(borrStatus)) {
            Map<String, Object> map = new HashMap<String, Object>();
            String[] ids = borrIds.split(",");
            map.put("borrIds", ids);
            map.put("borrStatus", borrStatus);
            return borrowListMapper.getBorrList(map);
        }
        return null;
    }

    /*
     * (non-Javadoc)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
     *
     * @see com.loan_api.manager.ManageLoanService#selectRiewerList()
     */
    @Override
    public List<Riewer> selectRiewerList(String status) {
        // TODO Auto-generated method stub
        return managerOfLoanMapper.selectRiewerList(status);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.loan_api.manager.ManageLoanService#selectRiewerListAll()
     */
    @Override
    public List<Riewer> selectRiewerListAll() {
        // TODO Auto-generated method stub
        return managerOfLoanMapper.selectRiewerListAll();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.loan_api.manager.ManageLoanService#selectUserPrivateVo(int)
     */
    @Override
    public PrivateVo selectUserPrivateVo(int perid) {
        // TODO Auto-generated method stub
        return managerOfLoanMapper.selectUserPrivateVo(perid);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.loan_api.manager.ManageLoanService#selectLoanInfoPrivateVo(int)
     */
    @Override
    public List<LoanInfoVo> selectLoanInfoPrivateVo(int himid) {
        // TODO Auto-generated method stub
        return managerOfLoanMapper.selectLoanInfoPrivateVo(himid);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.loan_api.manager.ManageLoanService#selectBankInfoVo(int)
     */
    @Override
    public List<BankInfoVo> selectBankInfoVo(int himid) {
        // TODO Auto-generated method stub
        return managerOfLoanMapper.selectBankInfoVo(himid);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.loan_api.manager.ManageLoanService#managerbymanager(java.lang.String,
     * java.lang.String)
     */
    @Autowired
    private RiewerMapper riewerMapper;

    @Override
    public ManagerResult managerbymanager(String brroid_list, String status) {
        String[] brroid = brroid_list.split(",");

        ManagerResult managerResult = new ManagerResult();
        for (int i = 0; i < brroid.length; i++) {
            Riewer riewer = new Riewer();
            riewer.setId(Integer.parseInt(brroid[i]));
            riewer.setStatus(status);
            riewer.setUpdateDate(new Date());
            try {
                int result = riewerMapper.updateByPrimaryKeySelective(riewer);
                managerResult.setCode(result);
                if (result > 0) {
                    managerResult.setMessage("处理成功！");
                } else {
                    managerResult.setMessage("处理失败！");
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return managerResult;
    }

    //图片转化成base64字符串
    public String GetImageStr(String path) {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        String imgFile = path;//待处理的图片  
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组  
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(data == null){
            return null;
        }
        //对字节数组Base64编码  
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);//返回Base64编码过的字节数组字符串  
    }

    //base64字符串转化成图片  
    public boolean GenerateImage(String imgStr, String path) {   //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空  
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            //Base64解码  
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {//调整异常数据
                    b[i] += 256;
                }
            }
            //生成jpeg图片  
            String imgFilePath = path;//新生成的图片  
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.loan_api.manager.ManageLoanService#getCardPicById(int)
     */
    @Override
    public CardPicInfoVo getCardPicById(int himid) {
        // TODO Auto-generated method stub
        CardPicInfoVo cardPicInfoVo = managerOfLoanMapper.getCardPicById(himid);
        if (cardPicInfoVo != null) {
            if (cardPicInfoVo.getImageZ() == null && cardPicInfoVo.getImage_urlZ() != null) {
                String path = PIC_DIR + cardPicInfoVo.getImage_urlZ();
                if (path != null) {
                    String data = GetImageStr(path);
                    cardPicInfoVo.setImageZ(data);
                }
            }
            if (cardPicInfoVo.getImageF() == null && cardPicInfoVo.getImage_urlF() != null) {
                String path = PIC_DIR + cardPicInfoVo.getImage_urlF();
                if (path != null) {
                    String data = GetImageStr(path);
                    cardPicInfoVo.setImageF(data);
                }
            }
        }
        return cardPicInfoVo;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.loan_api.manager.ManageLoanService#getCardByHimId(int)
     */
    @Autowired
    CardMapper cardMapper;

    @Override
    public ManagerResult getRiskReport(int himid) {
        // TODO Auto-generated method stub
        ManagerResult managerResult = new ManagerResult();
        Card card = cardMapper.selectByPerId(himid);
        String cardnum = card.getCardNum();
        String cardname = card.getName();
        String net = "{\"name\": \"" + cardname + "\",\"idvalue\": \""
                + cardnum + "\"}";

        String url = PropertiesReaderUtil.read("third", "RiskReportUrl");
        String result = HttpTools.post(url, net);
        String result2 = "";
        try {
            //查询芝麻分数
            ZhiMa zhima = zhiMaMapper.selectByPer_Id(himid);
            String source = "未认证";
            if (zhima != null && Detect.notEmpty(zhima.getZmScore())) {
                source = zhima.getZmScore();
            }
            result2 = URLDecoder.decode(result, "UTF-8");
            managerResult.setCode(0);
            if (result2.indexOf("Message\":\"") > 0) {
                String zhimaDtail = "<h3 class='box-title'>芝麻分数：" + source + "</h3>";
                result2 = result2.replaceFirst("Message\":\"", "Message\":\"" + zhimaDtail);
            }
            managerResult.setMessage(result2);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            managerResult.setCode(9);
            managerResult.setMessage(result2);
        }

        return managerResult;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.loan_api.manager.ManageLoanService#DoDsBatchVo()
     */
    @Override
    public ManagerResult DoDsBatchVo() {
//		ManagerResult managerResult = new ManagerResult();
//		// TODO Auto-generated method stub
//        List<DsBatchVo> list = new ArrayList<>();
//		for (int i = 1; i < 10000; i++) {
//            list.clear();
//			try {
//                String net = "{\"pageIndex\": \"" + i + "\",\"pageSize\": \"300\"}";
//                Head head = new Head();
//                Body body = new Body();
//                head.setMd5(MD5Util.encodeToMd5(net.toString()));
//                body.setContent(net);
//                PersonRiskEntity pre = new PersonRiskEntity();
//                pre.setBody(body);
//                pre.setHead(head);
//                // 调用.net的http
//                String url = PropertiesReaderUtil.read("third", "dsBatchUrl");
//                String result = HttpTools.post(url,
//                        com.alibaba.fastjson.JSONObject.toJSONString(pre), 1200000);
//                // System.out.println("结果————dddd=" + result);
//                if (result.equals("") || result == null) {
//                    // System.out.println("总共跑了" + i + "页");
//                    log.error("总共跑了" + i + "页,跑批结束时间：" + System.currentTimeMillis());
//                	break;
//                }
//                list = JSON.parseArray(result, DsBatchVo.class);
//            } catch (Exception e) {
//                e.printStackTrace();
//                log.error("获取.net数据错误！");
//            }
//                // System.out.println("list_长度=" + list.size() + "__"
//                // + list.get(0).getSurplusInterest() + "__"
//                // + list.get(0).getActRepayAmount());
//                for (DsBatchVo dsBatchVo : list) {
//                try {
//                        BorrowList borrowList = new BorrowList();
//                        borrowList.setBorrNum(dsBatchVo.getBrrowNum());
//                        // borrowList.setActRepayDate(dsBatchVo.getActRepayDate());
//                        if (dsBatchVo.getActRepayDate() != null && !("").equals(dsBatchVo.getActRepayDate())) {
//                            borrowList.setActRepayDate(new Date());
//                        }
//                        borrowList.setActRepayAmount(dsBatchVo.getActRepayAmount());
//                        borrowList.setBorrStatus(dsBatchVo.getBorrStatus());
//                        borrowList.setPlanRepay(dsBatchVo.getPlanRepay());
//
//                        RepaymentPlanVo repaymentPlanVo = new RepaymentPlanVo();
//                        repaymentPlanVo.setBorrNum(dsBatchVo.getBrrowNum());
//                        repaymentPlanVo.setIsSettle(dsBatchVo.getIsSettle());
//                        repaymentPlanVo.setPenalty(dsBatchVo.getPenalty());
//                        repaymentPlanVo.setPenaltyInterest(dsBatchVo.getPenaltyInterest());
//                        repaymentPlanVo.setSurplusQuota(dsBatchVo.getSurplusQuota());
//                        repaymentPlanVo.setSurplusMoney(dsBatchVo.getSurplusMoney());
//                        repaymentPlanVo.setSurplusInterest(dsBatchVo.getSurplusInterest());
//                        repaymentPlanVo.setSurplusPenalty(dsBatchVo.getSurplusPenalty());
//                        repaymentPlanVo.setSurplusPenaltyInteres(dsBatchVo.getSurplusPenaltyInteres());
//
//
//                		int result1 = borrowListMapper.updateByBrroNum(borrowList);
//                		int result2 = repaymentPlanMapper
//                				.updateByBrroNum(repaymentPlanVo);
//                		if (result1 + result2 > 0) {
//                			// managerResultForNet.setCode("0000");
//                			// managerResultForNet.setMessage("处理成功！");
//                        // managerResult.setCode(managerResult.getCode() + 1);
//                        log.error("跑批处理成功+" + dsBatchVo.getBrrowNum());
//                		} else {
//                        log.error("借款表及还款计划表更新失败：" + dsBatchVo.getBrrowNum());
//                			// managerResultForNet.setCode("2001");
//                			// managerResultForNet.setMessage("处理失败！");
//                    }
//                } catch (Exception e) {
//                    // managerResultForNet.setCode("2002");
//                    // managerResultForNet.setMessage("抛异常！"+e.getMessage());
//                    log.error(e.getMessage());
//                    log.error("跑批处理失败+" + dsBatchVo.getBrrowNum());
//                }
//
//                }
//
//
//		}
//
//		return managerResult;
        return null;
    }

    /* (non-Javadoc)
     * @see com.loan_api.manager.ManageLoanService#PicBatchVo(int, int)
     */
    @Autowired
    private ImageMapper imageMapper;

    @Override
    public ManagerResult PicBatchVo(int pageIndex, int pageSize) {
//		ManagerResult managerResult = new ManagerResult();
//		// TODO Auto-generated method stub
//		for (int i = 0; i < pageIndex; i++) {
//		try {
//			List<Image> list = managerOfLoanMapper.PicBatchVo(0, pageSize);
//			if(list.size()>0){
//				for (Image image : list) {
//					image.getId();
//					String image_name = image.getId()+""+UUID.randomUUID().toString()+".jpg";
//                        // System.out.println(image_name);
//				    boolean savez = GenerateImage(image.getImage(),PIC_DIR+image_name);
//		            if(savez){
//		            	image.setImageUrl(image_name);
//		            	imageMapper.updateByPrimaryKeySelective(image);
//		            }else{
//		            	managerResult.setMessage("已经成功了"+managerResult.getCode()+"条！");
//		            	return managerResult;
//		            }
//
//					managerResult.setCode(managerResult.getCode() + 1);
//
//
//				}
//			}
//
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//	}
//		managerResult.setMessage("全部成功了"+managerResult.getCode()+"条！");
//		return managerResult;
        return null;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println("ffff");

        // String net = "{\"pageIndex\": \"2\",\"pageSize\": \"350\"}";
        // Head head = new Head();
        // Body body = new Body();
        // head.setMd5(MD5Util.encodeToMd5(net.toString()));
        // body.setContent(net);
        // PersonRiskEntity pre = new PersonRiskEntity();
        // pre.setBody(body);
        // pre.setHead(head);
        // // 调用.net的http
        // String url = PropertiesReaderUtil.read("third", "dsBatchUrl");
        // String result = HttpTools.post(url,
        // com.alibaba.fastjson.JSONObject.toJSONString(pre));
        //
        // System.out.println("结果————dddd=" + result);
        //
        // List<DsBatchVo> list = JSON.parseArray(result, DsBatchVo.class);
        //
        // System.out.println("list_长度=" + list.size() + "__"
        // + list.get(0).getSurplusInterest() + "__"
        // + list.get(0).getActRepayAmount());
        //
        // for (DsBatchVo dsBatchVo : list) {
        // BorrowList borrowList = new BorrowList();
        // borrowList.setBorrNum(dsBatchVo.getBrrowNum());
        // // borrowList.setActRepayDate(dsBatchVo.getActRepayDate());
        // borrowList.setActRepayDate(new Date());
        // borrowList.setActRepayAmount(dsBatchVo.getActRepayAmount());
        // borrowList.setBorrStatus(dsBatchVo.getBorrStatus());
        // borrowList.setPlanRepay(dsBatchVo.getPlanRepay());
        //
        // RepaymentPlanVo repaymentPlanVo = new RepaymentPlanVo();
        // repaymentPlanVo.setBorrNum(dsBatchVo.getBrrowNum());
        // repaymentPlanVo.setIsSettle(dsBatchVo.getIsSettle());
        // repaymentPlanVo.setPenalty(dsBatchVo.getPenalty());
        // repaymentPlanVo.setPenaltyInterest(dsBatchVo.getPenaltyInterest());
        // repaymentPlanVo.setSurplusQuota(dsBatchVo.getSurplusQuota());
        // repaymentPlanVo.setSurplusMoney(dsBatchVo.getSurplusMoney());
        // repaymentPlanVo.setSurplusInterest(dsBatchVo.getSurplusInterest());
        // repaymentPlanVo.setSurplusPenalty(dsBatchVo.getSurplusPenalty());
        // repaymentPlanVo.setSurplusPenaltyInteres(dsBatchVo
        // .getSurplusPenaltyInteres());
        //
        // System.out.println("->>>>>>>>>=" + repaymentPlanVo.getIsSettle()
        // + repaymentPlanVo.getBorrNum());
        //
        // }
//
//		String cardnum = "320611199404071815";
//		String cardname = "张琦";
//
//		String net = "{\"name\": \"" + cardname + "\",\"idvalue\": \""
//				+ cardnum + "\"}";
//
//		String url = PropertiesReaderUtil.read("third", "RiskReportUrl");
//		String result = HttpTools.post(url, net);
//		System.out.println("结果————dddd=" + result);
//		String result2 = URLDecoder.decode(result, "UTF-8");
//		System.out.println("结果————dddd=" + result2);
        String hh = "2016-12-16T11:15:55.037";
        String str = "{\"Success\":true,\"Code\":\"0000\",\"Message\":\"<div class='row'><div class='col";
        if (str.indexOf("Message\":\"") > 0) {
            str = str.replaceFirst("Message\":\"", "Message\":\" " + "<h3 class='box-title'>芝麻分数：" + 740 + "</h3>");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.loan_api.manager.ManageLoanService#goBlackList(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ManagerResult goBlackList(String himid_list, String blacklist) {
        String[] himid = himid_list.split(",");

        ManagerResult managerResult = new ManagerResult();
        for (int i = 0; i < himid.length; i++) {
            Person person = new Person();
            person.setId(Integer.parseInt(himid[i]));
            person.setBlacklist(blacklist);
            person.setUpdateDate(new Date());
            try {
                int result = personMapper.updateByPrimaryKeySelective(person);

                managerResult.setCode(result);
                if (result > 0) {
                    BorrowList borr = borrowListMapper.selectNow(Integer.parseInt(himid[i]));
                    if ("BS001".equals(borr.getBorrStatus()) || "BS002".equals(borr.getBorrStatus())
                            || "BS003".equals(borr.getBorrStatus())) {
                        // 2017-06-01更改 如果拉黑的用户有 1，2，3状态的借款 改为BS008 审核未通过
                        borr.setBorrStatus("BS009");
                        borrowListMapper.updateByPrimaryKeySelective(borr);
                    }
                    managerResult.setMessage("处理成功！");
                } else {
                    managerResult.setMessage("处理失败！");
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return managerResult;
    }

    /* (non-Javadoc)
     * @see com.loan_api.manager.ManageLoanService#goReviewCheck(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ManagerResult goReviewCheck(String himid_list, String blacklist,
                                       String reason, String usernum, String type) {
        String[] himid = himid_list.split(",");
        // TODO Auto-generated method stub

        for (int i = 0; i < himid.length; i++) {
            Review review = new Review();
            review.setBorrId(Integer.parseInt(himid[i]));
            review.setCreateDate(new Date());
            review.setEmployNum(usernum);
            review.setReason(reason);
            review.setReviewType(type);
            review.setStatus(blacklist);

            reviewMapper.insert(review);
        }

        return null;
    }

    /* (non-Javadoc)
     * @see com.loan_api.manager.ManageLoanService#getReviewVoBlackList(int)
     */
    @Override
    public List<ReviewVo> getReviewVoBlackList(int himid) {
        // TODO Auto-generated method stub
        return reviewMapper.getReviewVoBlackList(himid);
    }

    @Override
    public String doDsBatchVoback() {
//		log.error("回调时间" + System.currentTimeMillis());
//		long start = System.currentTimeMillis();
//		List<DsAppReport> reslut = dsAppReportMapper.getGoodBorrows();
//		System.out.println(System.currentTimeMillis() - start);
//		synDateTask.setSynDatas(reslut);
//		threadPool.submit(synDateTask);
//
//		List<DsAppReport> badreslut = dsAppReportMapper.getBadBorrows();
//		if (badreslut != null && badreslut.size() > 0) {
//			List<BadData> list = new ArrayList<BadData>();
//			for (DsAppReport dsAppReport : badreslut) {
//				BadData orig = new BadData();
//				try {
//					BeanUtils.copyProperties(dsAppReport, orig);
//				} catch (Exception e) {
//				}
//				list.add(orig);
//			}
//			// 生成Excel
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put(NormalExcelConstants.FILE_NAME,
//					DateUtil.getDateStringyyyymmdd(new Date()) + "_baddata");
//			map.put(NormalExcelConstants.CLASS, BadData.class);
//			map.put(NormalExcelConstants.DATA_LIST, list);
//			map.put(NormalExcelConstants.PARAMS, new ExportParams());
//			Workbook workbook = ExcelUtils.getWorkbook(map);
//			String fileName = null;
//			fileName = map.get(NormalExcelConstants.FILE_NAME) + "";
//			// 保存Excel
//			if (workbook instanceof HSSFWorkbook) {
//				fileName += ".xls";
//			} else {
//				fileName += ".xlsx";
//			}
//
//			try {
//				ExcelUtils.saveFile(workbook,
//						"/data/www/youmi/youmiapp/billingNotice/", fileName);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			MailSender sender = new MailSender();
//
//			sender.setAddress("ningqi@jinhuhang.com.cn",
// new String[] {
//					"ningqi@jinhuhang.com.cn", "liuhongming@jinhuhang.com.cn",
//					"shuchen@jinhuhang.com.cn", "chenzhen@jinhuhang.com.cn",
//					"zhengjiaolong@jinhuhang.com.cn",
//					"wangge@jinhuhang.com.cn", "yangyunhua@jinhuhang.com.cn" },
//					null,
//					"凌晨同步数据问题数据", "");
//			sender.setAffix("/data/www/youmi/youmiapp/billingNotice/"
//					+ fileName, fileName);
//			sender.send("192.168.1.18", "ningqi", "abc+123");
//		}

        return "0";
    }

    @Override
    public List getOrders(String uid) {
        Map<String, Object> args = new HashMap<String, Object>();
        Integer[] types = new Integer[]{1, 2, 4, 5, 6, 7, 8};
        args.put("perId", uid);
        args.put("type_s", types);
        List oerders = orderMapper.getOrdersByArgs(args);
        return oerders;
    }

}
