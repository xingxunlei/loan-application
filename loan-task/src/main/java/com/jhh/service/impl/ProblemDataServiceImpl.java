package com.jhh.service.impl;

import com.jhh.dao.ProblemDataMapper;
import com.jhh.service.ProblemDataWatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Create by xw on 2017/11/28
 */
@Service
public class ProblemDataServiceImpl implements ProblemDataWatchService {

    @Autowired
    private ProblemDataMapper problemDataMapper;

    @Override
    public List<String> repaymentNegative(Map m) {
        return problemDataMapper.repaymentNegative(m);
    }

    @Override
    public List<String> contractNoDoneRepaymentDone() {
        return problemDataMapper.contractNoDoneRepaymentDone();
    }

    @Override
    public List<String> contractDoneRepaymentNoDone(Map m) {
        return problemDataMapper.contractDoneRepaymentNoDone(m);
    }

    @Override
    public List<String> contractWithoutRepayment(Map m) {
        return problemDataMapper.contractWithoutRepayment( m);
    }

    @Override
    public List<String> contractNoEstablishWithRepayment() {
        return problemDataMapper.contractNoEstablishWithRepayment( );
    }

    @Override
    public List<Map> muitiApplyContract(Map m) {
        return problemDataMapper.muitiApplyContract( m);
    }

    @Override
    public List<String> repetitivePhone() {
        return problemDataMapper.repetitivePhone();
    }

    @Override
    public List<Map> repaymentAmountNotrepaymentPlan(Map m) {
        return problemDataMapper.repaymentAmountNotrepaymentPlan( m);
    }

    @Override
    public List<Map> multiRepaymentOfContract(Map m) {
        return problemDataMapper.multiRepaymentOfContract( m);
    }

    @Override
    public List<String> exceedLimitAndWaitinRepayment(Map m) {
        return problemDataMapper.exceedLimitAndWaitinRepayment( m);
    }

    @Override
    public List<String> multiBankNum(Map m) {
        return problemDataMapper.multiBankNum( m);
    }

    @Override
    public List<String> multiOperatorOfContract() {
        return problemDataMapper.multiOperatorOfContract();
    }

    @Override
    public List<String> multiLoanOfOrder(Map m) {
        return problemDataMapper.multiLoanOfOrder( m);
    }

    @Override
    public List<String> actualExceedLimit(Map m) {
        return problemDataMapper.actualExceedLimit( m);
    }

    @Override
    public List<String> multiOrderOfLoanAndRepayment(Map m) {
        return problemDataMapper.multiOrderOfLoanAndRepayment( m);
    }

    @Override
    public List<String> noContractOrder(Map m) {
        return problemDataMapper.noContractOrder( m);
    }

    @Override
    public List<String> unsualLoanOrder(Map m) {
        return problemDataMapper.unsualLoanOrder( m);
    }

    @Override
    public List<String> multiProfile(Map m) {
        return problemDataMapper.multiProfile( m);
    }

    @Override
    public List<String> multiMainCard() {
        return problemDataMapper.multiMainCard();
    }

    @Override
    public List<String> exceedRepamentActualNotExceed(Map m) {
        return problemDataMapper.exceedRepamentActualNotExceed( m);
    }

    @Override
    public List<String> contractLoaningAndActualFailed(Map m) {
        return problemDataMapper.contractLoaningAndActualFailed(m);
    }

    @Override
    public List<String> signedContractNoAuditor(Map m) {
        return problemDataMapper.signedContractNoAuditor( m);
    }

    @Override
    public List<String> manualContractNoAuditor(Map m) {
        return problemDataMapper.manualContractNoAuditor( m);
    }

    @Override
    public List<Map> exceedRepaymentNoCollectionUpd(Map m) {
        return problemDataMapper.exceedRepaymentNoCollectionUpd(m);
    }
}
