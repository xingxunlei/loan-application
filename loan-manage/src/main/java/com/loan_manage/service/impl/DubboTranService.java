package com.loan_manage.service.impl;

import com.jhh.settlementsoa.model.request.GuestRepaySettlementRequest;
import com.jhh.settlementsoa.model.response.GuestRepaySettlementResponse;
import com.jhh.settlementsoa.rmi.RMISettlementService;
import com.jhh.settlementsoa.util.ResponseUtil;
import com.loan_entity.app.BorrowList;
import com.loan_entity.common.Constants;
import com.loan_entity.loan.Collectors;
import com.loan_entity.loan.CollectorsList;
import com.loan_entity.manager.CollectorsRecord;
import com.loan_entity.manager.LoanCompanyBorrow;
import com.loan_entity.manager.LoanCompanyOrder;
import com.loan_entity.manager.Order;
import com.loan_manage.entity.Result;

import com.loan_manage.mapper.*;
import com.loan_utils.util.BorrNum_util;
import com.loan_utils.util.Detect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class DubboTranService {
    private Logger logger = LoggerFactory.getLogger(LoanManagementServiceImpl.class);
    @Autowired
    private CollectorsListMapper collectorsListMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RMISettlementService rmiSettlementService;
    @Autowired
    private CollectorsRecordMapper collectorsRecordMapper;
    @Autowired
    private LoanCompanyBorrowMapper companyBorrowMapper;
    @Autowired
    private CompanyOrderMapper companyOrderMapper;
    @Autowired
    private CollectorsMapper collectorsMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
    public Result callDubbo(BorrowList borrowList, String reduce, String remark, String type, String userName){
        logger.info("进入dubbo调用,参数是："+borrowList.getId()+"->"+reduce+"->"+remark+"->"+type + "->" + userName);
        Result result = new Result();
        CollectorsList collectorsList = new CollectorsList();
        collectorsList.setContractId(borrowList.getBorrNum());
        collectorsList = collectorsListMapper.selectOne(collectorsList);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSS");
        String serNo = "YM" + sdf.format(new Date()) + BorrNum_util.getStringRandom(10);
        //生成减免订单
        Order order = new Order();
        order.setPerId(0);
        order.setSerialNo(serNo);
        order.setCompanyId(0);
        order.setConctactId(borrowList.getId());
        order.setPerId(borrowList.getPerId());
        order.setOptAmount(reduce);
        order.setActAmount(reduce);
        order.setRlState("p");
        order.setType(type);
        order.setStatus("y");
        order.setCreationDate(new Date());
        order.setCreateUser(userName);
        if(collectorsList != null && Detect.notEmpty(collectorsList.getBedueUserSysno())){
            order.setCollectionUser(collectorsList.getBedueUserSysno());
        }

        int count = orderMapper.insertSelective(order);

        if(count > 0){
            logger.info("订单生成成功"+order.toString());

            //取得订单号
            int orderId = order.getId();

            try{
                //生成公司流水记录
                Collectors c = new Collectors();
                c.setUserSysno(userName);
                Collectors collectors = collectorsMapper.selectOne(c);
                if(collectors != null && collectors.getLevelType() == Constants.COLLECTORS_OUT){
                    LoanCompanyOrder companyOrder = new LoanCompanyOrder();
                    companyOrder.setOrderId(orderId);
                    companyOrder.setCompanyId(collectors.getUserGroupId());
                    companyOrder.setCreateUser(userName);
                    companyOrder.setCreateDate(new Date());
                    companyOrderMapper.insert(companyOrder);
                }
            }catch (Exception e){
                logger.info("查询创建人失败。。。。。。。。");
            }
            GuestRepaySettlementRequest request = new GuestRepaySettlementRequest();
            request.setBorrNum(String.valueOf(borrowList.getId()));
            request.setOrderId(order.getSerialNo());
            request.setStatus("s");
            request.setMsg(remark);
            logger.info("开始调dubbo");

            ResponseUtil<GuestRepaySettlementResponse> response = null;
            try {
                response = rmiSettlementService.guestRepaySettlement(request);
            }catch (Exception e){
                logger.error(e.getMessage());
            }

            if(response == null){
                logger.info("dubbo调用失败");
                result.setCode(Result.FAIL);
                result.setMessage("清结算调用失败");

                order.setRlState("f");
                order.setReason(result.getMessage());
                orderMapper.updateByPrimaryKeySelective(order);
                return result;
            }

            if(response.getStatus() != 1){
                logger.info("dubbo调用失败");
                result.setCode(Result.FAIL);
                result.setMessage("清结算处理失败");

                order.setRlState("f");
                order.setReason(result.getMessage());
                orderMapper.updateByPrimaryKeySelective(order);
                return result;
            }
            logger.info("dubbo调用成功");

            result.setCode(Result.SUCCESS);
            result.setMessage("操作成功");
            return result;
        }else{
            logger.info("订单生成失败");
            result.setCode(Result.FAIL);
            result.setMessage("订单生成失败");
            return result;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
    public void doTransferLoan(
            List<CollectorsList> updateCollectorsList,
            List<CollectorsList> insertCollectorsList,
            List<CollectorsRecord> collectorsRecords,
            List<LoanCompanyBorrow> updateCompanyBorrow,
            List<LoanCompanyBorrow> insertCompanyBorrow) throws Exception {
        int i = 0, j = 0, k = 0, m = 0, n = 0;
        if(updateCollectorsList != null && updateCollectorsList.size() > 0){
            i = collectorsListMapper.batchUpdateCollectorsList(updateCollectorsList);

            logger.info("催收记录更新条数:{},实际更新条数:{}",updateCollectorsList.size(),i);
            if(i != updateCollectorsList.size()){//有数据更新失败,所有数据回滚
                throw new RuntimeException("催收记录更新失败,需要更新条数：" + updateCollectorsList.size() + ",实际更新条数:"+i);
            }
        }

        if(insertCollectorsList != null && insertCollectorsList.size() > 0){
            j = collectorsListMapper.batchInsertCollectorsList(insertCollectorsList);
            logger.info("催收记录插入条数:{},实际更新条数:{}",insertCollectorsList.size(),j);
            if(j != insertCollectorsList.size()){//有数据插入失败,所有数据回滚
                throw new RuntimeException("催收记录插入失败,需要插入条数：" + insertCollectorsList.size() + ",实际插入条数:"+j);
            }
        }

        if(collectorsRecords != null && collectorsRecords.size() > 0){
            k = collectorsRecordMapper.batchInsertCollectorsRecord(collectorsRecords);
            logger.info("转件记录插入条数:{},实际插入条数:{}",collectorsRecords.size(),k);
        }

        if(updateCompanyBorrow != null && updateCompanyBorrow.size() > 0){
            m = companyBorrowMapper.batchUpdate(updateCompanyBorrow);
            logger.info("公司催收记录更新条数:{},实际更新条数:{}",updateCompanyBorrow.size(),m);
            if(m != updateCompanyBorrow.size()){
                throw new RuntimeException("公司催收记录更新失败,需要更新条数：" + updateCompanyBorrow.size() + ",实际更新条数:"+j);
            }
        }

        if(insertCompanyBorrow != null && insertCompanyBorrow.size() > 0){
            n = companyBorrowMapper.batchInsert(insertCompanyBorrow);
            logger.info("公司催收记录插入条数:{},实际插入条数:{}",insertCompanyBorrow.size(),n);
            if(n != insertCompanyBorrow.size()){
                throw new RuntimeException("公司催收记录插入失败,需要插入条数：" + insertCompanyBorrow.size() + ",实际插入条数:"+j);
            }
        }
    }
}
