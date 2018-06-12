package com.loan_manage.service.impl;

import com.alibaba.dubbo.remoting.TimeoutException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.loan_api.loan.YsbCollectionService;
import com.loan_api.loan.YsbpayService;
import com.loan_entity.app.*;
import com.loan_entity.common.Constants;
import com.loan_entity.loan.*;
import com.loan_entity.loan_vo.BatchCollectEntity;
import com.loan_entity.manager.*;
import com.loan_entity.manager_vo.LoanInfoVo;
import com.loan_entity.manager_vo.LoanManagementVo;
import com.loan_entity.payment.Gather;
import com.loan_manage.entity.AskCollection;
import com.loan_manage.entity.LoansRemarkOutVo;
import com.loan_manage.entity.LoansRemarkVo;
import com.loan_manage.entity.Result;
import com.loan_manage.exception.RedisException;
import com.loan_manage.mapper.*;
import com.loan_manage.service.LoanManagementService;
import com.loan_manage.service.RedisService;
import com.loan_manage.utils.UrlReader;
import com.loan_utils.util.BorrNum_util;
import com.loan_utils.util.DateUtil;
import com.loan_utils.util.Detect;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class LoanManagementServiceImpl implements LoanManagementService {
    private Logger logger = LoggerFactory.getLogger(LoanManagementServiceImpl.class);
    @Autowired
    private RepayPlanMapper repayPlanMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private BorrowListMapper borrowListMapper;
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private CollectorsListMapper collectorsListMapper;
    @Autowired
    private CollectorsMapper collectorsMapper;
    @Autowired
    private CollectorsRemarkMapper collectorsRemarkMapper;
    @Autowired
    private BankInfoMapper bankInfoMapper;
    @Autowired
    private YsbCollectionService ysbCollectionService;
    @Autowired
    private BankListMapper bankListMapper;
    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private DubboTranService dubboTranService;
    /*@Autowired
    private PaymentService paymentService;*/
    @Autowired
    private YsbpayService ysbpayService;
    @Autowired
    private ManagerOfLoanMapper managerOfLoanMapper;
    @Autowired
    private LoanCompanyMapper companyMapper;
    @Autowired
    private CompanyOrderMapper companyOrderMapper;
    @Autowired
    private CollectorsLevelMapper collectorsLevelMapper;

    @Override
    public PageInfo<LoanManagementVo> selectLoanManagementInfo(
            Map<String, Object> queryMap,
            Integer start,
            Integer size,
            String userNo
    ) {
        buildLoanManageQueryCondition(queryMap,userNo);
        logger.info("=================>>>>>>>>>>>>>>>>贷后管理查询---起始页：{}；查询参数是:{}",start,queryMap.toString());
        Long totalCount = getTotalCount(queryMap, start, userNo);
        logger.info("=================>>>>>>>>>>>>>>>>贷后管理查询总条数:{}",totalCount);
        PageHelper.offsetPage(start,size,false);
        List<LoanManagementVo> loanList = repayPlanMapper.selectLoanManagementInfoBySize(queryMap);

        PageInfo<LoanManagementVo> vos = new PageInfo<>(buildLoanManagementVo(loanList));
        vos.setTotal(totalCount);
        if(vos.getTotal() == 0){
            totalCount = repayPlanMapper.selectLoanManagementInfoItems(queryMap);
            vos.setTotal(totalCount);
        }
        return vos;
    }

    @Override
    public PageInfo<LoanManagementVo> selectBatchReduceInfo(
            Map<String, Object> queryMap,
            Integer start,
            Integer size,
            String userNo
    ) {
        buildLoanManageQueryCondition(queryMap,userNo);
        logger.info("=================>>>>>>>>>>>>>>>>批量代扣专用查询---起始页：{}；查询参数是:{}",start,queryMap.toString());
        Long totalCount = getBatchReduceTotalCount(queryMap, start, userNo);
        logger.info("=================>>>>>>>>>>>>>>>>批量代扣专用查询查询总条数:{}",totalCount);
        PageHelper.offsetPage(start,size,false);
        List<LoanManagementVo> loanList = repayPlanMapper.selectBatchReduceInfoBySize(queryMap);

        PageInfo<LoanManagementVo> vos = new PageInfo<>(buildLoanManagementVo(loanList));
        vos.setTotal(totalCount);
        if(vos.getTotal() == 0){
            totalCount = repayPlanMapper.selectBatchReduceInfoItems(queryMap);
            vos.setTotal(totalCount);
        }
        return vos;
    }

    private Long getBatchReduceTotalCount(Map<String, Object> queryMap, Integer start, String userNo) {
        Long totalCount = null;
        try {
            if (start == 0) {//查询条件变化及初始化,做一次count,为后续查询条件不变化准备
                totalCount = repayPlanMapper.selectBatchReduceInfoItems(queryMap);
                redisService.saveQueryTotalItem("m_batch_reduce_count",userNo,totalCount);
            } else {//查询条件不变化,先从redis里取值,取不到值,做一次count,并放入redis中
                totalCount = redisService.selectQueryTotalItem("m_batch_reduce_count","loan_" + userNo);
            }
        }catch (Exception e){
            //未查询到总条数，按参数查询去查
            totalCount = repayPlanMapper.selectBatchReduceInfoItems(queryMap);
        }

        //检查总条数,条数为空或者为0,做一次count
        if(totalCount == null || totalCount == 0){
            totalCount = repayPlanMapper.selectBatchReduceInfoItems(queryMap);
            redisService.saveQueryTotalItem("m_batch_reduce_count",userNo,totalCount);
        }
        return totalCount;
    }

    private Long getTotalCount(Map<String, Object> queryMap, Integer start, String userNo) {
        Long totalCount = null;
        try {
            if (start == 0) {//查询条件变化及初始化,做一次count,为后续查询条件不变化准备
                totalCount = repayPlanMapper.selectLoanManagementInfoItems(queryMap);
                redisService.saveQueryTotalItem(Constants.TYPE_LOAN,userNo,totalCount);
            } else {//查询条件不变化,先从redis里取值,取不到值,做一次count,并放入redis中
                totalCount = redisService.selectQueryTotalItem(Constants.TYPE_LOAN,"loan_" + userNo);
            }
        }catch (Exception e){
            //未查询到总条数，按参数查询去查
            totalCount = repayPlanMapper.selectLoanManagementInfoItems(queryMap);
        }

        //检查总条数,条数为空或者为0,做一次count
        if(totalCount == null || totalCount == 0){
            totalCount = repayPlanMapper.selectLoanManagementInfoItems(queryMap);
            redisService.saveQueryTotalItem(Constants.TYPE_LOAN,userNo,totalCount);
        }
        return totalCount;
    }

    @Override
    public PageInfo<ReceiptUsers> selectReceiptUsers(Map<String, Object> queryMap,int offset,int size) {
        //查询贷后及外包的人员
        List<LoanCompany> companies = companyMapper.selectAll();
        String companyIds = "";
        List<String> companyNames = new ArrayList<>();
        companyNames.add("金互行-风控部");
        companyNames.add("金互行-系统管理");
        companyNames.add("金互行-运营管理部");

        for(LoanCompany company : companies){
            if(!companyNames.contains(company.getName())){
                companyIds += company.getId() + ",";
            }
        }
        companyIds = companyIds.substring(0,companyIds.length() - 1);
        queryMap.put("companyIds",companyIds);
        logger.info("selectReceiptUsers查询参数是:"+queryMap.toString());
        PageHelper.offsetPage(offset,size);
        List<ReceiptUsers> receiptUsers = repayPlanMapper.selectReceiptUsersNew(queryMap);
        return new PageInfo<ReceiptUsers>(receiptUsers);
    }

    @Override
    public Result reduceLoan(String contractId, String reduce, String remark, String type, String userName) {

        // 清结算时间不能做还款操作
        Result result = getSettlementSwitchO();
        if(result.getCode() == Result.FAIL){
            return result;
        }

        //根据合同号查询是否可以减免或者线下还款
        /*boolean canReduce = redisService.selectCanReduce(contractId);
        if(!canReduce){
            result.setCode(Result.FAIL);
            result.setMessage("操作过于频繁,请稍后重试!");
            return result;
        }*/

        //根据合同号查询个人
        BorrowList b = new BorrowList();
        b.setBorrNum(contractId);
        BorrowList borrowList = borrowListMapper.selectOne(b);
        if(borrowList == null){
            result.setCode(Result.FAIL);
            result.setMessage("合同不存在");
            return result;
        }
        result = reduceLoanCalculate(borrowList.getId(), reduce, type);
        if(result.getCode() == Result.FAIL){
            return result;
        }
        return dubboTranService.callDubbo(borrowList,reduce,remark,type, userName);
    }

    private Result reduceLoanCalculate(int borrId, String money, String type){
        Result result = new Result();
        result.setCode(Result.SUCCESS);
        if(!Detect.isPositive(borrId)){
            result.setCode(Result.FAIL);
            result.setMessage("合同号为空");
        }else if(!Detect.notEmpty(money)){
            result.setCode(Result.FAIL);
            result.setMessage("金额为空");
        }else if(!Detect.notEmpty(type)){
            result.setCode(Result.FAIL);
            result.setMessage("操作类型为空");
        }

        Map<String,Double> map = getSurplusMoney(borrId,type);
        Double surplusMoney = map.get("surplusMoney");
        if (surplusMoney == 0) {
            result.setCode(Result.FAIL);
            result.setMessage("减免金额与数据库金额不匹配，可减免金额为："+surplusMoney);
        } else {
            logger.info("----borrId:" + borrId + "----surplusMoney:" + surplusMoney + "----money" + Double.valueOf(money));
            //减免判断
            if (type.equals(Constants.OrderType.MITIGATE_PUNISHMENT)){
                Double surplusPenalty = map.get("surplusPenalty");
                if(surplusPenalty-Double.valueOf(money) < 0 ){
                    result.setCode(Result.FAIL);
                    result.setMessage("减免金额与数据库金额不匹配，可减免金额为："+(surplusPenalty == null ? surplusPenalty : surplusPenalty));

                }
            }
            //扣款金额判断
            if(surplusMoney-Double.valueOf(money) < 0 ){
                result.setCode(Result.FAIL);
                result.setMessage("输入金额与数据库金额不匹配，实际金额为："+(surplusMoney == null ? surplusMoney : surplusMoney));
            }
        }

        return result;
    }

    /**
     *  剩余能操作的金额和处理中的金额
     * @param borrId 合同id
     * @param type 类型
     * @return 金额
     */
    private Map<String,Double> getSurplusMoney(int borrId,String type){
        Map<String,Double> map = new HashMap<>();
        RepaymentPlan rp = new RepaymentPlan();
        rp.setContractId(Integer.valueOf(borrId));
        rp = repayPlanMapper.selectOne(rp);
        logger.info("-----" + JSONObject.toJSONString(rp));
        if(type.equals(Constants.OrderType.MITIGATE_PUNISHMENT)){
            //减免

            //已减免金额
            double reduceMoney = getMoney(borrId, Constants.OrderStatus.SUCESS, Constants.OrderType.MITIGATE_PUNISHMENT);
            //所有应还违约金罚息
            BigDecimal totalPenalty = rp.getPenalty().add(rp.getPenaltyInterest());
            //剩余能减免的金额
            BigDecimal surplusPenalty = totalPenalty.subtract(BigDecimal.valueOf(reduceMoney));

            //剩余能操作的减免金额
            map.put("surplusPenalty",surplusPenalty.doubleValue());
        }
        //处理中的金额
        double reduceMoney = getMoney(borrId, Constants.OrderStatus.WAIT, null);
        //剩余还款总额
        double totalPenalty = Double.valueOf(rp.getSurplusMoney()) + Double.valueOf(rp.getSurplusInterest()) +
                Double.valueOf(rp.getSurplusPenalty()) + Double.valueOf(rp.getSurplusPenaltyInteres());
        //剩余能操作的金额
        map.put("surplusMoney",totalPenalty - reduceMoney);

        return map;
    }


    private Double getMoney(int borrId, String status, String type){
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("type",type);
        map.put("status",status);
        map.put("borrId",borrId);
        logger.info("getMoney ----type:" + type + "----status :" + status + "-----borrId:" + borrId);
        return  orderMapper.getOrderMoney(map);
    }


    @Override
    public Map<String,Object> transferLoan(String contractIds, String userId,String opUserId) {
        logger.info("transferLoan参数contractIds:{} -->催收人ID:{}  -->操作人ID",contractIds,userId,opUserId);
        //加入催收队列
        Map<String,Object> map = new HashMap<String,Object>();
        String[] cIds = null;
        if(contractIds.contains(",")){
            cIds = contractIds.split(",");
        }else{
            cIds = new String[]{contractIds};
        }
        Collectors c = new Collectors();
        c.setUserSysno(userId);
        Collectors collectors = collectorsMapper.selectOne(c);
        if(collectors != null){

            if(cIds != null){
                //需要更新催收人
                List<CollectorsList> updateCollectorsList = new ArrayList<>();
                //需要新建催收人
                List<CollectorsList> insertCollectorsList = new ArrayList<>();
                //转件记录
                List<CollectorsRecord> collectorsRecords = new ArrayList<>();
                //更新公司催收记录
                List<LoanCompanyBorrow> updateCompanyBorrow = new ArrayList<>();
                //插入公司催收记录
                List<LoanCompanyBorrow> insertCompanyBorrow = new ArrayList<>();

                for(String cId : cIds){
                    String[] contractInfos = cId.split("-");

                    /*CollectorsList cs  = new CollectorsList();
                    cs.setContractSysno(contractInfos[1]);
                    cs = collectorsListMapper.selectOne(cs);*/
                    Map<String,Object> queryMap = new HashMap<>();
                    queryMap.put("contractSysno",contractInfos[1]);
                    CollectorsCompanyVo companyVo = collectorsListMapper.selectCollectorsCompanyVo(queryMap);

                    if(companyVo != null && companyVo.getId() != null) {
                        //update
                        CollectorsList collectorsList = new CollectorsList();
                        collectorsList.setBedueUserSysno(collectors.getUserSysno());
                        collectorsList.setBedueName(collectors.getUserName());
                        collectorsList.setContractId(contractInfos[0]);
                        collectorsList.setContractSysno(contractInfos[1]);
                        collectorsList.setAcquMode("B");
                        collectorsList.setUpdateDate(new Date());

                        updateCollectorsList.add(collectorsList);
                    }

                    //记录转件记录
                    CollectorsRecord collectorsRecord = new CollectorsRecord();
                    collectorsRecord.setBedueUser(collectors.getUserSysno());
                    collectorsRecord.setContractId(cId);
                    collectorsRecord.setCreateUser(opUserId);
                    collectorsRecord.setCreateTime(new Date());

                    collectorsRecords.add(collectorsRecord);

                    //转给公司
                    if(collectors.getLevelType() == Constants.COLLECTORS_OUT){
                        if(companyVo != null && companyVo.getCompanyId() != null){//更新

                            LoanCompanyBorrow update = new LoanCompanyBorrow();
                            update.setCompanyId(collectors.getUserGroupId());
                            update.setBorrId(Integer.valueOf(contractInfos[1]));
                            update.setUpdateUser(opUserId);
                            update.setUpdateDate(new Date());

                            updateCompanyBorrow.add(update);

                        }else{//插入

                            LoanCompanyBorrow insert = new LoanCompanyBorrow();
                            insert.setCompanyId(collectors.getUserGroupId());
                            insert.setBorrId(Integer.valueOf(contractInfos[1]));
                            insert.setCreateUser(opUserId);
                            insert.setCreateDate(new Date());

                            insertCompanyBorrow.add(insert);
                        }
                    }
                }

                try {
                    dubboTranService.doTransferLoan(
                            updateCollectorsList,
                            insertCollectorsList,
                            collectorsRecords,
                            updateCompanyBorrow,
                            insertCompanyBorrow);
                    //没有发生异常，判断催收人是否是外包人员,如果是外包,则要插入公司记录

                    map.put("state",1);
                    map.put("msg","转件操作成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    map.put("state",0);
                    map.put("msg","转件操作失败");
                }
            }
        }else{
            map.put("state",0);
            map.put("msg","催收人查询失败");
        }
        return map;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
    public int whiteBlackList(Integer conId,String userId,String reason,String type) {
        logger.info("人员ID:"+userId);

        Review review = new Review();
        review.setBorrId(conId);
        review.setCreateDate(new Date());
        review.setEmployNum(userId);
        review.setReason(reason);
        review.setReviewType("1".equals(type) ? "3" : "2");
        review.setStatus("1".equals(type) ? "n" : "y");

        reviewMapper.insert(review);

        //拉黑后BS003状态订单变为BS009
        if(review.getReviewType().equals(Constants.ReviewType.BLACK)){
            BorrowList bl = new BorrowList();
            bl.setPerId(conId);
            bl.setBorrStatus("BS003");
            bl = borrowListMapper.selectOne(bl);
            if(bl != null && Detect.isPositive(bl.getId())){
                bl.setBorrStatus("BS009");
                borrowListMapper.updateByPrimaryKeySelective(bl);
            }
        }

        Person qPerson = personMapper.selectPersonById(conId);
        if(qPerson == null){
            throw new RuntimeException("员工查询失败");
        }
        qPerson.setBlacklist("1".equals(type) ? "Y" : "N");
        int count = personMapper.updateByPrimaryKeySelective(qPerson);
        return count > 0 ? Result.SUCCESS : Result.FAIL;
    }

    @Deprecated
    public int whiteList(Integer conId,String userId,String reason) {
        logger.info("黑名单,人员ID:"+userId);

        Review review = new Review();
        review.setBorrId(conId);
        review.setCreateDate(new Date());
        review.setEmployNum(userId);
        review.setReason(reason);
        review.setReviewType("1");
        review.setStatus("N");

        reviewMapper.insert(review);

        Person qPerson = personMapper.selectPersonById(Integer.valueOf(userId));
        if(qPerson == null){
            return Result.FAIL;
        }
        qPerson.setBlacklist("N");
        int count = personMapper.updateByPrimaryKeySelective(qPerson);
        return count > 0 ? Result.SUCCESS : Result.FAIL;
    }

    @Override
    public PageInfo<CollectorsListVo> selectCollectorsListVo(Map<String, Object> queryMap) {
        buildQueryCondition(queryMap);
        logger.info("selectCollectorsListVo参数:"+queryMap.toString());
        List<CollectorsListVo> collectors = repayPlanMapper.selectCollectorsListInfo(queryMap);
        PageInfo<CollectorsListVo> pageInfo = new PageInfo<>(collectors);
        pageInfo.setList(buildCollectorsListVo(pageInfo.getList()));
        return pageInfo;
    }

    @Override
    public int addCollectionRemark(CollectorsRemark remark) {
        if(remark == null){
            return 0;
        }
        Integer contractSysno = remark.getContractSysno();

        remark.setUpdateUser(remark.getCreateUser());

        //查询催收人并将原有操作人赋值给UpdateUser
        CollectorsList collectorsList = new CollectorsList();
        collectorsList.setContractSysno(String.valueOf(contractSysno));
        collectorsList = collectorsListMapper.selectOne(collectorsList);
        if(collectorsList == null){
            logger.info("添加催收备注:催收人查询失败");
            //return 0;
        }else{
            remark.setCreateUser(collectorsList.getBedueUserSysno());
        }

        logger.info("添加催收备注:"+remark.toString());
        return collectorsRemarkMapper.insert(remark);
    }

    @Override
    public BankVo selectMainBankByUserId(Integer userId) {
        logger.info("查询个人主卡:"+userId);
        return bankInfoMapper.selectMainBankByUserId(userId);
    }

    @Override
    public Result askCollection(AskCollection askCollection) {
        // 清结算时间不能做还款操作
        Result result = getSettlementSwitchO();
        if(result.getCode() == Result.FAIL){
            return result;
        }
        //查询催收人
        CollectorsList collectorsList = new CollectorsList();
        collectorsList.setContractSysno(askCollection.getBorrId());
        collectorsList = collectorsListMapper.selectOne(collectorsList);
        String collectionUser = null;

        if(collectorsList != null && Detect.notEmpty(collectorsList.getBedueUserSysno())){
            collectionUser = collectorsList.getBedueUserSysno();
        }

        String guid = UUID.randomUUID().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSS");
        String serNo = "YM" + sdf.format(new Date()) + BorrNum_util.getStringRandom(10);
        logger.info("请求参数:guid:{};borrId:{};name:{};IdCardNo:{};OptAmount:{};" +
                        "BankId:{};BankNum:{};Phone:{};Description:{};serNo:{};CreateUser:{};collectionUser:{}",
                guid,
                askCollection.getBorrId(),
                askCollection.getName(),
                askCollection.getIdCardNo(),
                askCollection.getOptAmount(),
                askCollection.getBankId(),
                askCollection.getBankNum(),
                askCollection.getPhone(),
                askCollection.getDescription(),
                serNo,
                askCollection.getCreateUser(),
                collectionUser
        );
        NoteResult noteResult = ysbCollectionService.askCollection(
                guid,
                askCollection.getBorrId(),
                askCollection.getName(),
                askCollection.getIdCardNo(),
                askCollection.getOptAmount(),
                askCollection.getBankId(),
                askCollection.getBankNum(),
                askCollection.getPhone(),
                askCollection.getDescription(),
                serNo,
                askCollection.getCreateUser(),
                collectionUser
        );

        if("0000".equals(noteResult.getCode())){
            String orderSer = (String)noteResult.getData();
            if(StringUtils.isNotEmpty(askCollection.getCreateUser())){

                //根据创建人查询所属公司
                CollectorsLevel queryLevel = new CollectorsLevel();
                queryLevel.setUserSysno(askCollection.getCreateUser());
                CollectorsLevel level = collectorsLevelMapper.selectOne(queryLevel);
                if (level != null){
                    //查询受理的订单
                    Order insertOrder = orderMapper.selectBySerial(orderSer);
                    if( insertOrder != null ){
                        LoanCompanyOrder companyOrder = new LoanCompanyOrder();
                        companyOrder.setCompanyId(level.getUserGroupId());
                        companyOrder.setOrderId(insertOrder.getId());
                        companyOrder.setCreateUser(askCollection.getCreateUser());
                        companyOrder.setCreateDate(new Date());

                        int count = companyOrderMapper.insertSelective(companyOrder);
                        logger.info("插入公司流水条数{}",count);
                    }
                }

            }
            result.setCode(Result.SUCCESS);
            result.setMessage(noteResult.getInfo());
            result.setObject(noteResult.getData());
        }else{
            result.setCode(Result.FAIL);
            result.setMessage(noteResult.getInfo());
        }
        return result;
    }

    @Override
    public Result lakalaAskCollection(AskCollection askCollection) {
        // 清结算时间不能做还款操作
        Result result = getSettlementSwitchO();
        if(result.getCode() == Result.FAIL){
            return result;
        }
        //查询催收人
        CollectorsList collectorsList = new CollectorsList();
        collectorsList.setContractSysno(askCollection.getBorrId());
        collectorsList = collectorsListMapper.selectOne(collectorsList);
        String collectionUser = null;

        if(collectorsList != null && Detect.notEmpty(collectorsList.getBedueUserSysno())){
            collectionUser = collectorsList.getBedueUserSysno();
        }

        String guid = UUID.randomUUID().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSS");
        String serNo = "YM" + sdf.format(new Date()) + BorrNum_util.getStringRandom(10);

        Gather gather = new Gather();
        gather.setGuid(guid);
        gather.setBankId(askCollection.getBankId());
        gather.setBankInfoId(askCollection.getBankInfoId());
        gather.setBankNum(askCollection.getBankNum());
        gather.setName(askCollection.getName());
        gather.setPhone(askCollection.getPhone());
        gather.setIdCardNo(askCollection.getIdCardNo());
        gather.setPerId(askCollection.getPerId());
        gather.setBorrNum(askCollection.getBorrNum());
        gather.setBorrId(askCollection.getBorrId());
        gather.setOptAmount(askCollection.getOptAmount());
        gather.setDescription(StringUtils.isEmpty(askCollection.getDescription()) ? "拉卡拉代扣" : askCollection.getDescription());
        gather.setSerNo(serNo);
        gather.setCreateUser(askCollection.getCreateUser());
        gather.setCollectionUser(collectionUser);

        logger.info("请求参数：{}",gather.toString());

//        NoteResult noteResult = paymentService.sigleGatherByLakala(gather);

//        if("0000".equals(noteResult.getCode())){
//            result.setCode(Result.SUCCESS);
//            result.setMessage(noteResult.getInfo());
//            result.setObject(noteResult.getData());
//        }else{
//            result.setCode(Result.FAIL);
//            result.setMessage(noteResult.getInfo());
//        }
        return result;
    }

    @Override
    public List<BankList> selectBankList() {
        return bankListMapper.selectAll();
    }

    @Override
    public PageInfo<CollectorsListVo> selectCollectorsInfo(Map<String, Object> queryMap) {
        buildQueryCondition(queryMap);
        List<CollectorsListVo> listVos = repayPlanMapper.selectCollectorsInfo(queryMap);
        PageInfo<CollectorsListVo> pageInfo = new PageInfo<CollectorsListVo>(listVos);
        pageInfo.setList(buildCollectorsInfo(pageInfo.getList()));
        return pageInfo;
    }

    @Override
    public PageInfo<CollectorsListVo> selectCollectorsInfo(Map<String, Object> queryMap, int offset, int size, String userNo) {
        buildQueryCondition(queryMap);
        logger.info("=================>>>>>>>>>>>>>>>>催收信息查询---起始页：{}；查询参数是:{}",offset,queryMap.toString());
        Long totalCount = null;
        try {
            if (offset == 0) {
                //查询有变化,查询符合条件的count//查询参数为
                if (queryMap.isEmpty()) {
                    totalCount = redisService.selectQueryTotalItem(Constants.TYPE_COLL_INFO,"coll");
                } else {
                    totalCount = buildCollectorsInfoCount(queryMap, userNo);
                }
            } else {
                //无变化,直接取得缓存中的总条数
                totalCount = buildCollectorsInfoCount(queryMap, userNo);
            }
        }catch (Exception e){
            //未查询到总条数，按参数查询去查
            totalCount = repayPlanMapper.selectCollectorsInfoItem(queryMap);
        }

        queryMap.put("startItem",offset);
        queryMap.put("pageSize",size);

        List<CollectorsListVo> collList = repayPlanMapper.selectCollectorsInfo(queryMap);
        PageInfo<CollectorsListVo> vos = new PageInfo<>(buildCollectorsInfo(collList));
        vos.setTotal(totalCount);

        return vos;
    }


    private Long buildCollectorsInfoCount(Map<String, Object> queryMap, String userNo) {
        Long totalCount = null;
        if(queryMap.isEmpty()) {
            totalCount = redisService.selectQueryTotalItem(Constants.TYPE_COLL_INFO,"coll");
        }else if(queryMap.size() == 1){
            //只有一个查询条件
            if(queryMap.containsKey("productName")){
                //按产品类型查询
                String proId = (String)queryMap.get("productName");
                if(StringUtils.isNotEmpty(proId)){
                    totalCount = redisService.selectQueryTotalItem(Constants.TYPE_COLL_INFO,"coll_pro_"+proId);
                }else{
                    totalCount = repayPlanMapper.selectCollectorsInfoItem(queryMap);
                    redisService.saveQueryTotalItem(Constants.TYPE_COLL_INFO,userNo,totalCount);
                }
            }else{
                totalCount = redisService.selectQueryTotalItem(Constants.TYPE_COLL_INFO,userNo);
                if(totalCount == null  || totalCount == 0){
                    totalCount = repayPlanMapper.selectCollectorsInfoItem(queryMap);
                    redisService.saveQueryTotalItem(Constants.TYPE_COLL_INFO,userNo,totalCount);
                }
            }
        }else{
            //多个查询条件,先从redis中取得条数
            totalCount = redisService.selectQueryTotalItem(Constants.TYPE_COLL_INFO,userNo);
            if(totalCount == null  || totalCount == 0){
                totalCount = repayPlanMapper.selectCollectorsInfoItem(queryMap);
                redisService.saveQueryTotalItem(Constants.TYPE_COLL_INFO,userNo,totalCount);
            }
        }

        if(totalCount == null){
            throw new RuntimeException("总条数查询失败!");
        }

        return totalCount;
    }

    @Override
    public int queryExportCount(Map<String, Object> queryMap) {
        buildQueryCondition(queryMap);
        return repayPlanMapper.queryExportCount(queryMap);
    }

    @Override
    public List<LoanManagementVo> selectExportData(Map<String, Object> queryMap,Integer count,String userNo) {
        buildQueryCondition(queryMap);
        queryMap.put("startItem",0);
        queryMap.put("pageSize",count == 0 ? Integer.MAX_VALUE : count);
        addAuthLevel2queryMap(queryMap,userNo);
        List<LoanManagementVo> loanManagementVoList = repayPlanMapper.selectExportData(queryMap);
//        return buildLoanManagementVo(loanManagementVoList);
        return loanManagementVoList;
    }

    @Override
    public List<LoansRemarkVo> selectExportLoansRemarkVo(Map<String, Object> queryMap,String userNo) {
        buildQueryCondition(queryMap);
//        List<LoansRemarkVo> loansRemarks = repayPlanMapper.selectLoansRemarkVo(queryMap);
//        return buildLoansRemarkVo(loansRemarks);
        addAuthLevel2queryMap(queryMap,userNo);
        List<LoansRemarkVo> remarkVos = repayPlanMapper.selectExportLoansRemarkVo(queryMap);
        return  remarkVos;
    }

    @Override
    public List<LoansRemarkOutVo> selectExportLoansRemarkForOutWorkers(Map<String, Object> queryMap, String userNo) {
        buildQueryCondition(queryMap);
        addAuthLevel2queryMap(queryMap,userNo);
        List<LoansRemarkOutVo> outVoList = repayPlanMapper.selectExportDataForOutWorkers(queryMap);
        return outVoList;
    }

    @Override
    public int queryExportRemarkCount(Map<String, Object> queryMap,String userNo) {
        buildQueryCondition(queryMap);
        addAuthLevel2queryMap(queryMap,userNo);
        return repayPlanMapper.selectLoansRemarkVoCount(queryMap);
    }

    @Override
    public Result batchCollection(List<LoanManagementVo> askCollections, String reduceMoney, String createUser, String deductionsType, String t) throws RedisException,TimeoutException {
        // 清结算时间不能做还款操作
        Result result = getSettlementSwitchO();
        if(result.getCode() == Result.FAIL){
            return result;
        }
        logger.error("time start:" + DateUtil.getDateStringToHHmmss(new Date()));
        List<BatchCollectEntity> batchCollects = new ArrayList<>();
        //不合法订单号
        StringBuilder sb = new StringBuilder();
        for(LoanManagementVo vo : askCollections){
            if("逾期未还".equals(vo.getStateString())){

                //feature-3.5.0 jira353 消除重复扣款  前台不做查询
                BatchCollectEntity batchCollect = new BatchCollectEntity();

                BankVo bankVo = bankInfoMapper.selectMainBankByUserId(Integer.valueOf(vo.getCustomerId()));

                batchCollect.setBankId(String.valueOf(bankVo.getBankId()));
                batchCollect.setBankNum(bankVo.getBankNum());
                batchCollect.setBorrId(vo.getContractKey());
                batchCollect.setDescription("批量代扣");
                batchCollect.setGuid(UUID.randomUUID().toString());
                batchCollect.setIdCardNo(vo.getCustomerIdValue());
                batchCollect.setName(vo.getCustomerName());
                //reduce money 如果为全额扣款  传0
                batchCollect.setOptAmount(reduceMoney);
                batchCollect.setPerId(vo.getCustomerId());
                batchCollect.setPhone(vo.getCustomerMobile());

                CollectorsList collectorsList = new CollectorsList();
                collectorsList.setContractSysno(vo.getContractKey());
                collectorsList = collectorsListMapper.selectOne(collectorsList);

                //新增订单操作人和催收入
                batchCollect.setCreateUser(createUser);
                if(collectorsList != null && Detect.notEmpty(collectorsList.getBedueUserSysno())){
                    batchCollect.setCollectionUser(collectorsList.getBedueUserSysno());
                }
                batchCollect.setDeductionsType(deductionsType);

                batchCollects.add(batchCollect);
            }
        }
        NoteResult noteResult = null;
        logger.error("time end:" + DateUtil.getDateStringToHHmmss(new Date()));
        if(batchCollects.size() > 0){
            noteResult = ysbCollectionService.askCollectionBatch(batchCollects);
        }

        if(noteResult == null || "200".equals(noteResult.getCode())){
            if(!StringUtils.isEmpty(sb.toString())){
                sb.insert(0,"处理失败的订单:\n");
            }
            sb.append("成功处理订单条数为：" + batchCollects.size() + "条，请稍后查看扣款结果。");
            result.setCode(Result.SUCCESS);
            result.setMessage(sb.toString());
            if(noteResult != null){
                result.setObject(noteResult.getData());
            }
        }else{
            result.setCode(Result.FAIL);
            result.setMessage(noteResult.getInfo());
        }
        return result;
    }

    @Override
    public Result lakalaBatchCollection(List<LoanManagementVo> askCollections, String reduceMoney, String createUser, String deductionsType) {
        // 清结算时间不能做还款操作
        Result result = getSettlementSwitchO();
        if(result.getCode() == Result.FAIL){
            return result;
        }
        System.out.print( "time start:" + DateUtil.getDateTimeString(new Date()));
        List<BatchCollectEntity> batchCollects = new ArrayList<>();
        //不合法订单号
        StringBuilder sb = new StringBuilder();
        for(LoanManagementVo vo : askCollections){
            if("逾期未还".equals(vo.getStateString())){
                //从redis里取到该单是否处理过，
                String bcTag = redisService.selectBatchCollectionTags(vo.getContractKey());
                if(bcTag != null && !bcTag.equals("nil")){//不为空,跳过不处理.
                    sb.append(vo.getCustomerName() + ":" + vo.getContractID() + "\n");
                    continue;
                }

                //判断是否有p状态的订单15012096987734373
                String optAmount = "0".equals(reduceMoney) ? vo.getSurplusTotalAmount() : reduceMoney;
                result =  reduceLoanCalculate(Integer.valueOf(vo.getContractKey()), optAmount, Constants.OrderType.BATCH_COLLECTION);
                if(result.getCode() == Result.FAIL){
                    sb.append(vo.getCustomerName() + ":" + vo.getContractID() + "\n");
                    continue;
                }

                //将单子放入redis
                redisService.saveBatchCollectionTags(vo.getContractKey());

                BatchCollectEntity batchCollect = new BatchCollectEntity();

                BankVo bankVo = bankInfoMapper.selectMainBankByUserId(Integer.valueOf(vo.getCustomerId()));

                batchCollect.setBankId(String.valueOf(bankVo.getBankId()));
                batchCollect.setBankNum(bankVo.getBankNum());
                batchCollect.setBorrId(vo.getContractKey());
                batchCollect.setBankInfoId(String.valueOf(bankVo.getId()));
                batchCollect.setDescription("批量代扣");
                batchCollect.setGuid(UUID.randomUUID().toString());
                batchCollect.setIdCardNo(vo.getCustomerIdValue());
                batchCollect.setName(vo.getCustomerName());
                batchCollect.setOptAmount(optAmount);
                batchCollect.setPerId(vo.getCustomerId());
                batchCollect.setPhone(vo.getCustomerMobile());

                CollectorsList collectorsList = new CollectorsList();
                collectorsList.setContractSysno(vo.getContractKey());
                collectorsList = collectorsListMapper.selectOne(collectorsList);

                //新增订单操作人和催收入
                batchCollect.setCreateUser(createUser);
                if(collectorsList != null && Detect.notEmpty(collectorsList.getBedueUserSysno())){
                    batchCollect.setCollectionUser(collectorsList.getBedueUserSysno());
                }
                batchCollect.setDeductionsType(deductionsType);

                batchCollects.add(batchCollect);
            }
        }
        NoteResult noteResult = null;
        System.out.print( "time end:" + DateUtil.getDateTimeString(new Date()));
        if(batchCollects.size() > 0){
//            noteResult = paymentService.batchGatherByLakala(batchCollects);
        }

        if(noteResult == null || "200".equals(noteResult.getCode())){
            if(!StringUtils.isEmpty(sb.toString())){
                sb.append("有正在处理的订单。");
            }
            sb.append("成功处理订单条数为：" + batchCollects.size() + "条，请稍后查看扣款结果。");
            result.setCode(Result.SUCCESS);
//            result.setMessage("批量扣款请求已发送，请稍后查看扣款结果");
            result.setMessage(sb.toString());
            if(noteResult != null){
                result.setObject(noteResult.getData());
            }


        }else{
            result.setCode(Result.FAIL);
            result.setMessage(noteResult.getInfo());
        }
        return result;
    }

    @Override
    public List<String> modifyPersonBlackList() {

        List<String> failList = new ArrayList<>();

        int allCount = personMapper.selectPersonByBlackListNullCount();
        int page = allCount / 1000 == 0 ? allCount / 1000 : allCount / 1000 + 1;
        for(int i = 0; i < page ; i++){
            //计算当前页
            int start = i * 1000;
            PageHelper.offsetPage(start,1000);
            List<Person> persons = personMapper.selectPersonByBlackListNull();

            List<Integer> updateIds = new ArrayList<>();

            for (Person person : persons) {
                if(person == null){
                    continue;
                }
                if(!StringUtils.isEmpty(person.getBlacklist())){
                    failList.add(person.getId()+"");
                }

                updateIds.add(person.getId());
            }
            logger.info("更新列表：{}",updateIds);
            personMapper.updatePersonBlackList(updateIds);
        }
        return failList;
    }

    private List<LoansRemarkVo> buildLoansRemarkVo(List<LoansRemarkVo> loansRemarks) {
        List<LoansRemarkVo> vos = new ArrayList<LoansRemarkVo>();
        for(LoansRemarkVo vo : loansRemarks){
//            if("BS005".equals(vo.getStateString())){
//                vo.setStateString("逾期未还");
//            }else if("BS010".equals(vo.getStateString())){
//                vo.setStateString("逾期结清");
//            }else if("BS004".equals(vo.getStateString())){
//                vo.setStateString("代还款");
//            }
            //取出产品类型
            JSONObject product = redisService.selectProductFromRedis(Integer.valueOf(vo.getProductId()));
            if(product != null){
                vo.setProductName(StringUtils.isEmpty(product.getString("prodName")) ? "" : product.getString("prodName"));
                vo.setAmount(StringUtils.isEmpty(product.getString("amount")) ? "" : product.getString("amount"));
            }
            //取得用户信息
            JSONObject person = redisService.selectPersonFromRedis(Integer.valueOf(vo.getCustomerId()));
            if(person != null){
                vo.setCustomerName(StringUtils.isEmpty(person.getString("name")) ? "" : person.getString("name"));
                vo.setCustomerIdValue(StringUtils.isEmpty(person.getString("idCard")) ? "" : person.getString("idCard"));
                vo.setCustomerMobile(StringUtils.isEmpty(person.getString("phone")) ? "" : person.getString("phone"));
            }
            //取得贷后人信息
            JSONObject callNameJson = redisService.selectCollertorsUserName(vo.getCallName());
            if(callNameJson != null){
                vo.setCallName(StringUtils.isEmpty(callNameJson.getString("userName")) ? "" : callNameJson.getString("userName"));
            }
            vos.add(vo);
        }
        return vos;
    }

    private List<CollectorsListVo> buildCollectorsInfo(List<CollectorsListVo> list) {
        List<CollectorsListVo> vos = new ArrayList<CollectorsListVo>();
        for(CollectorsListVo vo : list){
            //取得用户信息
            JSONObject person = redisService.selectPersonFromRedis(Integer.valueOf(vo.getCustomerId()));
            if(person != null){
                vo.setCustomerName(StringUtils.isEmpty(person.getString("name")) ? "" : person.getString("name"));
                vo.setCustomerIdValue(StringUtils.isEmpty(person.getString("idCard")) ? "" : person.getString("idCard"));
                vo.setCustomerMobile(StringUtils.isEmpty(person.getString("phone")) ? "" : person.getString("phone"));
            }
            //取出产品信息
            JSONObject product = redisService.selectProductFromRedis(Integer.valueOf(vo.getProductId()));
            if(product != null){
                vo.setProductName(StringUtils.isEmpty(product.getString("prodName")) ? "" : product.getString("prodName"));
                vo.setAmount(StringUtils.isEmpty(product.getString("amount")) ? "" : product.getString("amount"));
            }
            vos.add(vo);
        }
        return vos;
    }

    private List<CollectorsListVo> buildCollectorsListVo(List<CollectorsListVo> list) {
        List<CollectorsListVo> vos = new ArrayList<CollectorsListVo>();
        for(CollectorsListVo vo : list){
            if(vo.getBlackList() == null){
                vo.setBlackList("No");
            }else if(vo.getBlackList().equals("Y")){
                vo.setBlackList("Yes");
            }else if(vo.getBlackList().equals("N")){
                vo.setBlackList("No");
            }
            //取得用户信息
            JSONObject person = redisService.selectPersonFromRedis(Integer.valueOf(vo.getCustomerId()));
            if(person != null){
                vo.setCustomerName(StringUtils.isEmpty(person.getString("name")) ? "" : person.getString("name"));
                vo.setCustomerIdValue(StringUtils.isEmpty(person.getString("idCard")) ? "" : person.getString("idCard"));
                vo.setCustomerMobile(StringUtils.isEmpty(person.getString("phone")) ? "" : person.getString("phone"));
            }
            //取出产品信息
            JSONObject product = redisService.selectProductFromRedis(Integer.valueOf(vo.getProductId()));
            if(product != null){
                vo.setProductName(StringUtils.isEmpty(product.getString("prodName")) ? "" : product.getString("prodName"));
                vo.setAmount(StringUtils.isEmpty(product.getString("amount")) ? "" : product.getString("amount"));
            }

            vos.add(vo);
        }
        return vos;
    }

    private void buildLoanManageQueryCondition(Map<String, Object> queryMap,String userNo){

        if(queryMap != null){
            String customerName = (String)queryMap.get("customerName");
            String customerIdValue = (String)queryMap.get("customerIdValue");
            String customerMobile = (String)queryMap.get("customerMobile");
            Map<String,Object> map = new HashMap<String,Object>();
            if (StringUtils.isNotEmpty(customerName))map.put("name",customerName);
            if (StringUtils.isNotEmpty(customerIdValue))map.put("idCard",customerIdValue);
            if (StringUtils.isNotEmpty(customerMobile))map.put("phone",customerMobile);

            if(!map.isEmpty()){
                PageHelper.offsetPage(0,100000);
                List<Integer> perIds = personMapper.selectPersonId(map);
                if(perIds != null && perIds.size() > 0){
                    String id = "";
                    for(int i=0;i<perIds.size();i++){
                        if(i == perIds.size()-1){
                            id += perIds.get(i)+"";
                        }else{
                            id += perIds.get(i)+",";
                        }
                    }
                    queryMap.put("customerId",id);
                }else{
                    queryMap.put("customerId","-1");
                }
            }
        }

        addAuthLevel2queryMap(queryMap, userNo);

        if(!queryMap.containsKey("selector")){
            queryMap.put("selector","endDateString");
            queryMap.put("desc","desc");
        }
    }

    private void addAuthLevel2queryMap(Map<String, Object> queryMap, String userNo) {
        Collectors c = new Collectors();
        c.setUserSysno(userNo);
        Collectors collectors = collectorsMapper.selectOne(c);
        queryMap.put("levelType",collectors == null ? "" : collectors.getLevelType());
        queryMap.put("companyId",collectors == null ? "" : collectors.getUserGroupId());
    }

    private void buildQueryCondition(Map<String, Object> queryMap) {

        if(queryMap != null){
            String customerName = (String)queryMap.get("customerName");
            String customerIdValue = (String)queryMap.get("customerIdValue");
            String customerMobile = (String)queryMap.get("customerMobile");
            Map<String,Object> map = new HashMap<String,Object>();
            if (StringUtils.isNotEmpty(customerName))map.put("name",customerName);
            if (StringUtils.isNotEmpty(customerIdValue))map.put("idCard",customerIdValue);
            if (StringUtils.isNotEmpty(customerMobile))map.put("phone",customerMobile);

            if(!map.isEmpty()){
                PageHelper.offsetPage(0,100000);
                List<Integer> perIds = personMapper.selectPersonId(map);
                if(perIds != null && perIds.size() > 0){
                    String id = "";
                    for(int i=0;i<perIds.size();i++){
                        if(i == perIds.size()-1){
                            id += perIds.get(i)+"";
                        }else{
                            id += perIds.get(i)+",";
                        }
                    }
                    queryMap.put("customerId",id);
                }else{
                    queryMap.put("customerId","-1");
                }
            }
        }
    }

    private List<LoanManagementVo> buildLoanManagementVo(List<LoanManagementVo> list) {
        List<LoanManagementVo> vos = new ArrayList<LoanManagementVo>();
        for(LoanManagementVo vo : list){
//            if("BS005".equals(vo.getStateString())){
//                vo.setStateString("逾期未还");
//            }else if("BS010".equals(vo.getStateString())){
//                vo.setStateString("逾期结清");
//            }else if("BS004".equals(vo.getStateString())){
//                vo.setStateString("待还款");
//            }else if("BS006".equals(vo.getStateString())){
//                vo.setStateString("正常结清");
//                //TODO 将逾期一天但是结清的单子设置为逾期一天而非0
//            }
            if(vo.getBlackList() == null){
                vo.setBlackList("No");
            }else if(vo.getBlackList().equals("Y")){
                vo.setBlackList("Yes");
            }else if(vo.getBlackList().equals("N")){
                vo.setBlackList("No");
            }
            //取出产品类型
            JSONObject product = redisService.selectProductFromRedis(Integer.valueOf(vo.getProductId()));
            if(product != null){
                vo.setProductName(StringUtils.isEmpty(product.getString("prodName")) ? "" : product.getString("prodName"));
                vo.setAmount(StringUtils.isEmpty(product.getString("amount")) ? "" : product.getString("amount"));
            }
            //取得用户信息
            JSONObject person = redisService.selectPersonFromRedis(Integer.valueOf(vo.getCustomerId()));
            if(person != null){
                vo.setCustomerName(StringUtils.isEmpty(person.getString("name")) ? "" : person.getString("name"));
                vo.setCustomerIdValue(StringUtils.isEmpty(person.getString("idCard")) ? "" : person.getString("idCard"));
                vo.setCustomerMobile(StringUtils.isEmpty(person.getString("phone")) ? "" : person.getString("phone"));
            }
            vos.add(vo);
        }
        return vos;
    }

    protected Result getSettlementSwitchO(){
        Result result = new Result();
        result.setCode(Result.SUCCESS);
        String key = redisService.getRedistByKey(Constants.SETTLEMENT_SWITCH);
        if(Detect.notEmpty(key)){
            if(key.equals("off")){
                result.setCode(Result.FAIL);
                result.setMessage("清结算期间不允许扣款一类操作");
            }
        }
        return result;
    }

    @Override
    public Download checkCanDownload() {
        int totalCount = redisService.selectDownloadCount();
        String downloadCount =  UrlReader.read("max.download");
        int downloadCountInt = StringUtils.isNotEmpty(downloadCount) ? Integer.valueOf(downloadCount) : 0;
        Download download = new Download();

        download.setTotalCount(totalCount);
        download.setDownloadCount(downloadCountInt);

        return download;
    }

    @Override
    public List<Product> selectProducts() {
        return productMapper.selectAll();
    }

    @Override
    public List<LoanInfoVo> selectLoanInfoPrivateVo(int perId) {
        List<LoanInfoVo> loanInfoVos = managerOfLoanMapper.selectLoanInfoPrivateVoForOperator(perId);
        return loanInfoVos;
    }

    @Override
    public PageInfo<CollectorsLevel> selectReceiptUsers(Map<String, Object> queryParams,String userNo, Integer type,int offset,int size) {

        Example example = new Example(CollectorsLevel.class);
        example.createCriteria().andEqualTo("userSysno",userNo);
        List<CollectorsLevel> collectorsLevels = collectorsLevelMapper.selectByExample(example);
        if(collectorsLevels == null){
            return null;
        }

        CollectorsLevel collectorsLevel = collectorsLevels.get(0);

        if(collectorsLevel == null){
            return null;
        }

        queryParams.put("levelType",collectorsLevel.getLevelType());
        queryParams.put("companyId",collectorsLevel.getUserGroupId());
        queryParams.put("type",type);
        PageHelper.offsetPage(offset,size);
        return new PageInfo<CollectorsLevel>(collectorsLevelMapper.selectDsUsers(queryParams));
    }
}
