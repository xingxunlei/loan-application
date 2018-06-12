package com.loan_manage.service.impl;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.loan_entity.app.BorrowList;
import com.loan_entity.common.Constants;
import com.loan_manage.entity.OrderRobot;
import com.loan_manage.entity.Response;
import com.loan_manage.entity.RobotEntity;
import com.loan_manage.entity.RobotQuestion;
import com.loan_manage.mapper.BorrowListMapper;
import com.loan_manage.mapper.CardMapper;
import com.loan_manage.mapper.OrderRobotMapper;
import com.loan_manage.mapper.RobotQuestionMapper;
import com.loan_manage.service.RobotService;
import com.loan_manage.utils.Assertion;
import com.loan_manage.utils.Detect;
import com.loan_manage.utils.QueryParamUtils;
import com.loan_utils.util.DateUtil;
import com.loan_utils.util.http.AOSHttpClient;
import com.loan_utils.util.http.HttpRequestVO;
import com.loan_utils.util.http.HttpResponseVO;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 机器人实现类
 * @author carl.wan
 *2017年9月11日 15:33:17
 */
@Service("robotService")
public class RobotServiceImpl implements RobotService {

    private static final Logger logger = LoggerFactory.getLogger(RobotServiceImpl.class);

    private static String corpCode = "金互行（悠米闪借）";

    /**
     * 列表分页
     * @param request
     */
    private void buildPage(HttpServletRequest request) {
        int offset =  Integer.valueOf(request.getParameter("skip") == null ? "0" : request.getParameter("skip"));
        int size = Integer.valueOf(request.getParameter("take") == null ? Integer.MAX_VALUE + "" : request.getParameter("take"));
        PageHelper.offsetPage(offset, size);
    }

    private int maxMemSize = 500 * 1024;

    @Value("${ROBOT_ORDER_URl}")
    private String url;
    @Value("${ROBOT_ORDER_ACCESS_TOKEN}")
    private String accessToken;
    @Value("${ROBOT_ORDER_JOB_CODE}")
    private String jobCode;
    @Value("${ROBOT_FILE_URL}")
    private String robotFileUrl;

    @Autowired
    BorrowListMapper borrowListMapper;
    @Autowired
    OrderRobotMapper orderRobotMapper;
    @Autowired
    RobotQuestionMapper robotQuestionMapper;
    @Autowired
    CardMapper cardMapper;

    public Response sendRcOrder(BorrowList borrowList){
        Response response = new Response();
        if(borrowList != null){
            String code = UUID.randomUUID() + "";

            HttpResponseVO httpResponseVO = sendPostRobot(code, borrowList);

            if(httpResponseVO != null && httpResponseVO.getStatus().equals("200")){
                JSONObject json = JSONObject.parseObject(httpResponseVO.getOut());

                if(Detect.notEmpty(json.getString("success"))){
                    JSONObject jsonSucess = JSONObject.parseObject(json.getString("success"));

                    OrderRobot orderRobot = new OrderRobot();
                    orderRobot.setCode(code);
                    orderRobot.setBorrId(borrowList.getId());
                    orderRobot.setState(Constants.OrderRobotState.SEND_ING);
                    orderRobot.setThirdCode(jsonSucess.getString("receipt_id"));
                    orderRobot.setSendParam(httpResponseVO.getSendParam());
                    orderRobot.setRecvParam(httpResponseVO.getOut());
                    orderRobot.setCreateDate(Calendar.getInstance().getTime());
                    orderRobot.setSucessTime(jsonSucess.getString("received_time"));
                    orderRobotMapper.insertSelective(orderRobot);

                    //更新boor电呼状态
                    BorrowList bl = new BorrowList();
                    bl.setId(orderRobot.getBorrId());
                    bl.setBaikeluStatus(orderRobot.getState());
                    borrowListMapper.updateByPrimaryKeySelective(bl);

                    response.setCode(2000);
                    response.setMsg("success");
                }else{
                    response.setData(json.getString("failure"));
                }
                logger.info("sendRcOrder backParam:" + httpResponseVO.getOut());
            }
        }
        return response;
    }

    @Override
    public Response sendRcOrder(Integer borrId) throws Exception {
        Assertion.isPositive(borrId, "合同Id不能为空");
        Response response = new Response();
        response.setCode(5000);
        BorrowList borrowList = borrowListMapper.selectByPrimaryKey(borrId);
        Assertion.notNull(borrowList, "合同不存在");
        Assertion.isTrue(borrowList.getBorrStatus().equals("BS003"),"合同状态必须为已签约");

        //有放过款的不打电话

        List<BorrowList> borrowLists =  borrowListMapper.queryBorrListByPerIdAndStauts(borrowList.getPerId());
        if(Detect.notEmpty(borrowLists)){
            Assertion.isTrue(borrowLists.size() <= 0 ,"放过款的不需重新打电话");
        }
        //合同状态必须为已签约
        response = sendRcOrder(borrowList);

        return response;

    }

    /**
     *  csv_bank_digit_4   银行卡后四位
     *  csv_phone_num 电话号码
     *  csv_dob_yyyymmdd 出生年月日
     * csv_digit_4  身份证后四位
     * csv_borrow_amt 借款金额
     * csv_borrow_period 分期天数
     * @return
     */
    private String getWorkData(Integer borrowId){
        Assertion.isPositive(borrowId,"合同ID不能为空");
        Map<String, Object> map = cardMapper.queryRobot(borrowId);
        if(map != null ){
            String bankNum = map.get("bankNum").toString();
            String cardNum = map.get("cardNum").toString();
            String birthday = DateUtil.getDateStringyyyymmdd((Date) map.get("birthday")) ;
            JSONObject json = new JSONObject();
            json.put("csv_bank_digit_4", bankNum.substring(bankNum.length() - 4, bankNum.length()));
            json.put("csv_phone_num", map.get("phone"));
            json.put("csv_dob_yyyymmdd", birthday);
            json.put("csv_digit_4", cardNum.substring(cardNum.length() - 4, cardNum.length()));
            json.put("csv_borrow_amt", ((BigDecimal)map.get("borrAmount")).intValue() + "");
            json.put("csv_borrow_period", map.get("termValue") + "");
            return json.toString();
        }
        return null;
    }

    private HttpResponseVO sendPostRobot(String code, BorrowList borrowList){
        Map map = new HashMap();
        map.put("work_id", code);
        map.put("corp_code", corpCode);
        map.put("access_token",accessToken);
        map.put("job_code", jobCode);
//        String work_data = "{'csv_bank_digit_4':'1234','csv_phone_num':'13636569813','csv_dob_yyyymmdd':'19911111','csv_digit_4':'2137','csv_borrow_amt':'1000','csv_borrow_period':'14'}";
        String work_data = getWorkData(borrowList.getId());

        Map dtt=null;
        try {
            dtt = JSONObject.parseObject(work_data, HashMap.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> mdata = Maps.newHashMap();
        map.put("work_data", dtt);
        mdata.put("mdata", JSONObject.toJSONString(map));
        HttpRequestVO httpRequestVO = new HttpRequestVO(url, mdata);
        HttpResponseVO httpResponseVO = AOSHttpClient.upload(httpRequestVO);
        httpResponseVO.setSendParam(JSONObject.toJSONString(mdata));
        return httpResponseVO;
    }

    public static void main(String[] arge){


    }

    @Override
    @Transactional
    public Response callBackRc(HttpServletRequest request) throws Exception {
        Response response = new Response().code(4001).msg("file");
        // Check that we have a file upload request
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // maximum size that will be stored in memory
        factory.setSizeThreshold(maxMemSize);
        // Location to save data that is larger than maxMemSize.
//		factory.setRepository(new File("/tmp"));
        factory.setRepository(new File(robotFileUrl));
        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
        // Check if request is multi-part
        if (!isMultipart) {
            throw new Exception("3:Request should be multi-part.");
        }

        List<FileItem> fileItems = upload.parseRequest(request);
        RobotEntity robotEntity = getRobotEntity(fileItems);
        if(Detect.notEmpty(robotEntity.getWork_id())){
            //获取订单
            String robotOrderId = robotEntity.getWork_id();
            OrderRobot orderRobot = new OrderRobot();
            orderRobot.setCode(robotOrderId);
            orderRobot = orderRobotMapper.selectOne(orderRobot);

            saveOrderRobot(robotEntity, orderRobot);

            saveRobotQuestion(robotEntity, orderRobot);

            //成功报文组装
            JSONObject json = new JSONObject();
            json.put("receipt_id",orderRobot.getThirdCode());
            json.put("received_time", DateUtil.getDateTime_String(Calendar.getInstance().getTime()));
            JSONObject data = new JSONObject();
            data.put("success",json);
            response.setCode(2000);
            response.setData(data);

        }

        return response;
    }

    /**
     * 保存订单详情
     * @param robotEntity
     * @param orderRobot
     */
    public void saveRobotQuestion(RobotEntity robotEntity, OrderRobot orderRobot){
        JSONObject robotResult =  JSONObject.parseObject(robotEntity.getWork_result());
        //订单明细
        String resultDetails = robotResult.getString("result_details");

        List<Map> array = JSONObject.parseArray(resultDetails, Map.class);
        String work_data = getWorkData(orderRobot.getBorrId());
        JSONObject json = null;
        if(Detect.notEmpty(work_data)){
            json = JSONObject.parseObject(work_data);
        }
        for(Map map : array){
            //详情输入
            RobotQuestion robotQuestion = new RobotQuestion();
            robotQuestion.setRobotOrderId(orderRobot.getId());
            robotQuestion.setCreateDate(Calendar.getInstance().getTime());
            robotQuestion.setDuration(map.get("duration") + "");
            robotQuestion.setQuestion(map.get("Q_TXT") + "");
            robotQuestion.setAnswerResults(map.get("Q_ANS") + "");
            robotQuestion.setQuestionId(map.get("Q_ID") + "");
            robotQuestion.setUserInput(map.get("Q_VAL") + "");
            robotQuestion.setInteractiveWay(map.get("Q_TYPE") + "");
            //问题详情正确答案
            if(Detect.notEmpty(json)){
                if(map.get("Q_ID").equals("Q1_1")){
                    robotQuestion.setRightAnswers(json.getString("csv_digit_4"));
                }else if (map.get("Q_ID").equals("Q1_2")){
                    robotQuestion.setRightAnswers(json.getString("csv_dob_yyyymmdd"));
                }else if (map.get("Q_ID").equals("Q1_3")){
                    robotQuestion.setRightAnswers(json.getString("csv_borrow_period"));
                }else if (map.get("Q_ID").equals("Q1_4")){
                    robotQuestion.setRightAnswers(json.getString("csv_borrow_amt"));
                }else if (map.get("Q_ID").equals("Q1_5")){
                    robotQuestion.setRightAnswers(json.getString("csv_bank_digit_4"));
                }
            }

            Example queryQuestion = new Example(RobotQuestion.class);
            queryQuestion.createCriteria().andEqualTo("questionId",robotQuestion.getQuestionId())
                    .andEqualTo("robotOrderId",robotQuestion.getRobotOrderId());
            List<RobotQuestion> questions = robotQuestionMapper.selectByExample(queryQuestion);

            //没有查询到插入数据
            if(CollectionUtils.isEmpty(questions)){
                robotQuestionMapper.insertSelective(robotQuestion);
            }
        }
    }

    /**
     * 保存机器人订单
     * @param robotEntity
     * @param orderRobot
     * @return
     */
    public JSONObject saveOrderRobot(RobotEntity robotEntity, OrderRobot orderRobot){
        JSONObject robotResult =  JSONObject.parseObject(robotEntity.getWork_result());
        String resultCode = robotResult.getString("result_code");
        if(Detect.notEmpty(resultCode)){
            if(resultCode.equals("通过")){
                orderRobot.setState(Constants.OrderRobotState.PASS);
            }else if(resultCode.equals("拒绝")){
                orderRobot.setState(Constants.OrderRobotState.UN_PASS);
            }else if(resultCode.equals("未接通")){
                orderRobot.setState(Constants.OrderRobotState.BUSY);
            }else if(resultCode.equals("非本人")){
                orderRobot.setState(Constants.OrderRobotState.NOT_ONESELF);
            }else {
                orderRobot.setState(Constants.OrderRobotState.UNKNOWN);
            }
        }
        String gender = robotResult.getString("gender");
        if(Detect.notEmpty(gender)){
            if(gender.equals("男")){
                orderRobot.setGender(Constants.Gender.MAN);
            }else if (gender.equals("女")){
                orderRobot.setGender(Constants.Gender.WOMAN);
            }
        }
        orderRobot.setUpdateDate(Calendar.getInstance().getTime());
        orderRobot.setAudio(robotEntity.getAudio());
        orderRobot.setPhone(robotResult.getString("phone_num"));
        orderRobot.setScore(robotResult.getInteger("score"));
        orderRobot.setDuration(robotResult.getString("duration"));
        orderRobotMapper.updateByPrimaryKeySelective(orderRobot);

        //更新boor电呼状态
        BorrowList bl = new BorrowList();
        bl.setId(orderRobot.getBorrId());
        bl.setBaikeluStatus(orderRobot.getState());
        borrowListMapper.updateByPrimaryKeySelective(bl);

        return robotResult;
    }

    @Override
    public Response robotOrderByBorrNum(String borrNum, HttpServletRequest request) {
        Assertion.notEmpty(borrNum, "合同号不能为空");
        Response response = new Response().code(4001);
        Map<String, Object> paramMap = QueryParamUtils.getargs(request.getParameterMap());
        BorrowList bl = new BorrowList();
        bl.setBorrNum(borrNum);
        bl = borrowListMapper.selectOne(bl);

        if(bl != null ){
            this.buildPage(request);
            Example example = new Example(OrderRobot.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("borrId",bl.getId());
            if(Detect.notEmpty(paramMap.get("state") + ""))
                criteria.andEqualTo("state", paramMap.get("state"));
            if(Detect.notEmpty(paramMap.get("score") + ""))
                criteria.andEqualTo("score", paramMap.get("score"));
            if(Detect.notEmpty(paramMap.get("gender") + ""))
                criteria.andEqualTo("gender", paramMap.get("gender"));
            if(Detect.notEmpty(paramMap.get("phone") + ""))
                criteria.andEqualTo("phone", paramMap.get("phone"));
            if(Detect.notEmpty(paramMap.get("creatDate") + "")) {
                if (Detect.notEmpty(paramMap.get("creatDate_end") + "")) {
                    criteria.andBetween("createDate", paramMap.get("creatDate_start"), paramMap.get("creatDate_end"));
                }
                criteria.andGreaterThanOrEqualTo("createDate", paramMap.get("creatDate_start"));
            }


            example.setOrderByClause("create_date desc");
            List<OrderRobot> orderRobots = orderRobotMapper.selectByExample(example);

            List<Map<String,String>> restlt = new ArrayList();
            if(Detect.notEmpty(orderRobots)){
                for(OrderRobot orderRobot : orderRobots){
                    Map map = new HashMap();
                    map.put("creatDate", orderRobot.getCreateDate());
                    map.put("phone", orderRobot.getPhone());
                    map.put("state", orderRobot.getState());
                    map.put("duration", orderRobot.getDuration());
                    map.put("gender", orderRobot.getGender());
                    map.put("score", orderRobot.getScore());
                    RobotQuestion robotQuestion = new RobotQuestion();
                    robotQuestion.setRobotOrderId(orderRobot.getId());
                    List<RobotQuestion> robotQuestions = robotQuestionMapper.select(robotQuestion);
                    if(Detect.notEmpty(robotQuestions)){
                        int i = 1;
                        for (RobotQuestion rq : robotQuestions){
                            map.put("quest" + i, rq.getQuestion());
                            map.put("answer" + i,rq.getAnswerResults());
                            i ++;
                        }
                    }
                    restlt.add(map);
                }
            }
            response.setCode(2000);
            PageInfo page = new PageInfo(restlt);
            page.setTotal(new PageInfo(orderRobots).getTotal());
            response.setData(page);
        }


        return response;
    }

    /**
     * 解析参数
     * @param fileItems
     * @return
     * @throws IOException
     */
    private RobotEntity getRobotEntity(List<FileItem> fileItems ) throws IOException {

        RobotEntity robotEntity = new RobotEntity();
        String temp = null;
        byte[] audioByteArray = null;
        String audioFileName = null;

        for (FileItem fi : fileItems) {
            if (fi.isFormField() && fi.getFieldName().equals("work_result")) {

                temp = fi.getString("UTF-8");
                robotEntity.setWork_result(temp);
                System.out.println("work_result string: " + temp);
            }
            if (fi.isFormField() && fi.getFieldName().equals("token")) {

                temp = fi.getString("UTF-8");
                robotEntity.setToken(temp);
                System.out.println("token string: " + temp);
            }
            if (fi.isFormField() && fi.getFieldName().equals("work_data")) {

                temp = fi.getString("UTF-8");
//                robotEntity.setw(temp);
                System.out.println("workdata string: " + temp);
            }
            if (fi.isFormField() && fi.getFieldName().equals("work_id")) {
                temp = fi.getString("UTF-8");
                robotEntity.setWork_id(temp);
                System.out.println("work_id string: " + temp);
            }

            if (!fi.isFormField() && fi.getFieldName().equals("audio")) {
                audioFileName = fi.getName();
                audioByteArray = fi.get();
                temp = robotFileUrl + audioFileName;
//                createFile("/home/biocloo/tmp/" + audioFileName, audioByteArray);
                createFile(temp, audioByteArray);
                robotEntity.setAudio(temp);
            }
        }
        return robotEntity;
    }

    public static void createFile(String path, byte[] content) throws IOException {
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(content);
        fos.close();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCorpCode() {
        return corpCode;
    }

    public void setCorpCode(String corpCode) {
        this.corpCode = corpCode;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getJobCode() {
        return jobCode;
    }

    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }

    public String getRobotFileUrl() {
        return robotFileUrl;
    }

    public void setRobotFileUrl(String robotFileUrl) {
        this.robotFileUrl = robotFileUrl;
    }
}
