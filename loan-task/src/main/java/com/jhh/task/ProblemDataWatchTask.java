package com.jhh.task;


import com.jhh.service.MailService;
import com.jhh.service.ProblemDataWatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 异常数据监控
 *
 * @author xw
 * @date 2017 - 11 - 28
 */
@Component
public class ProblemDataWatchTask {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MailService mailService;
    //收件人
    @Value("${problemDataMailTo}")
    private String problemDataMailTo;
    //抄送人
    @Value("${problemDataMailCopyTo}")
    private String problemDataMailCopyTo;
    //发件人
    @Value("${mail.wzz.from}")
    private String problemDataMailFrom;
    @Autowired
    private ProblemDataWatchService problemDataWatchService;
    //7-23点每小时执行一次 0 0 7-23 * * ?
    @Scheduled(cron = "0 0 7-23 * * ? ")
    public void excetueTran() {
        logger.info("开始执行异常数据检查-----");
        String title = "【悠米闪借】生产数据异常";
        try {
            String[] mailCopyTo = {};
            if(problemDataMailCopyTo != null && !"".equals(problemDataMailCopyTo)){
                mailCopyTo = mailCopyTo;
            }
            StringBuilder sb = new StringBuilder("");
            List<String> list1 = problemDataWatchService.repaymentNegative(beforeDefiniteDay(1));
            if(list1 != null && list1.size()>0){
                sb.append("还款计划负数：<br/>");
                sb.append("SQL: select id  from ym_repayment_plan where (surplus_quota < 0 or surplus_money<0 or surplus_interest<0 or surplus_penalty<0 or surplus_penalty_Interes<0) and repay_date>date_sub(curdate(),interval 1 day) <br/>");
                sb.append("查询结果: "+list1.toString()+"<br/><br/>");
                logger.info("还款计划是负数:{}",list1.toString());
            }

            List<String> list2 = problemDataWatchService.contractNoDoneRepaymentDone();
            if(list2 != null && list2.size()>0){
                sb.append("合同未结清，还款计划结清： <br/>");
                sb.append("SQL: select a.id from ym_borrow_list a, ym_repayment_plan b where  a.id=b.contract_id and a.borr_status in('BS005','BS004') and ( b.is_settle=1 or (b.surplus_quota=0 and b.surplus_money=0 and b.surplus_interest=0 and  b.surplus_penalty=0 and b.surplus_penalty_Interes=0)) <br/>");
                sb.append("查询结果: "+list2.toString()+"<br/><br/>");
                logger.info("合同未结清，还款计划结清:{}",list2.toString());
            }

            List<String> list3 = problemDataWatchService.contractDoneRepaymentNoDone(beforeDefiniteDay(1));
            if(list3 != null && list3.size()>0){
                sb.append("合同结清，还款计划未结清： <br/>");
                sb.append("SQL: select b.id from ym_borrow_list a, ym_repayment_plan b where a.id=b.contract_id and  a.borr_status in('BS006','BS010') and (b.surplus_quota<>0 or b.surplus_money<>0 or b.surplus_interest<>0 or  b.surplus_penalty<>0" +
                        "or b.surplus_penalty_Interes<>0 or b.is_settle=0) and b.repay_date>date_sub(curdate(),interval 1 day) <br/>");
                sb.append("查询结果: "+list3.toString()+"<br/><br/>");
                logger.info("合同结清，还款计划未结清:{}",list3.toString());
            }

            List<String> list4 = problemDataWatchService.contractWithoutRepayment(beforeDefiniteDay(1));
            if(list4 != null && list4.size()>0){
                sb.append("有合同没还款计划： <br/>");
                sb.append("SQL: select a.id from ym_borrow_list a  where a.borr_status in('BS005','BS004','BS006','BS010') and not exists (select 1 from ym_repayment_plan b  where a.id=b.contract_id ) and a.pay_date>date_sub(curdate(),interval 1 day) <br/>");
                sb.append("查询结果: "+list4.toString()+"<br/><br/>");
                logger.info("有合同没还款计划:{}",list4.toString());
            }

            List<String> list5 = problemDataWatchService.contractNoEstablishWithRepayment();
            if(list5 != null && list5.size()>0){
                sb.append("合同未成立，有还款计划： <br/>");
                sb.append("SQL: select a.id from ym_borrow_list a  where a.borr_status in('BS001','BS002','BS003','BS007','BS008','BS009','BS011','BS012') and  exists (select 1 from ym_repayment_plan b  where a.id=b.contract_id ) <br/>");
                sb.append("查询结果: "+list5.toString()+"<br/><br/>");
                logger.info("合同未成立，有还款计划:{}",list5.toString());
            }

            List<Map> list6 = problemDataWatchService.muitiApplyContract(beforeDefiniteDay(10));
            if(list6 != null && list6.size()>0){
                sb.append("多笔同时申请的合同： <br/>");
                sb.append("SQL: select b.phone,  count(1) from ym_borrow_list a, ym_person b where a.per_id=b.id and borr_status in('BS001','BS002','BS003','BS011','BS004') and a.creation_date>date_sub(curdate(),interval 10 day)  group by per_id having count(1)>1 <br/>");
                sb.append("查询结果: "+list6.toString()+"<br/><br/>");
                logger.info("多笔同时申请的合同:{}",list6.toString());
            }

            List<String> list7 = problemDataWatchService.repetitivePhone();
            if(list7 != null && list7.size()>0){
                sb.append("重复手机号： <br/>");
                sb.append("SQL: select phone from ym_person  GROUP BY phone having count(1)>1 <br/>");
                sb.append("查询结果: "+list7.toString()+"<br/><br/>");
                logger.info("重复手机号:{}",list7.toString());
            }

            List<Map> list8 = problemDataWatchService.repaymentAmountNotrepaymentPlan(beforeDefiniteDay(1));
            if(list8 != null && list8.size()>0){
                sb.append("正常结清和逾期结清中总还款金额不等于应还金额： <br/>");
                sb.append("SQL: select sum(b.act_amount)-a.plan_repay as result, a.update_date  from ym_order b,ym_borrow_list a" +
                        "where b.conctact_id=a.id  and  b.rl_state ='s' and b.type not in (1,3)" +
                        "and a.borr_status in('BS006','BS010') and a.update_date> date_sub(curdate(),interval 1 day)" +
                        "group by b.conctact_id  having result<>0 <br/>");
                sb.append("查询结果: "+list8.toString()+"<br/><br/>");
                logger.info("正常结清和逾期结清中总还款金额不等于应还金额:{}",list8.toString());
            }

            List<Map> list9 = problemDataWatchService.multiRepaymentOfContract(beforeDefiniteDay(1));
            if(list9 != null && list9.size()>0){
                sb.append("一个合同多条还款计划： <br/>");
                sb.append("SQL: select contract_id,count(1),repay_date from ym_repayment_plan group by contract_id  having count(1)>1 and repay_date>date_sub(curdate(),interval 1 day) <br/>");
                sb.append("查询结果: "+list9.toString()+"<br/><br/>");
                logger.info("一个合同多条还款计划:{}",list9.toString());
            }

            List<String> list10 = problemDataWatchService.exceedLimitAndWaitinRepayment(beforeDefiniteDay(3));
            if(list10 != null && list10.size()>0){
                sb.append("逾期的状态还是待还款 ： <br/>");
                sb.append("SQL: select id from ym_borrow_list where planrepay_date<date_sub(curdate(),interval 1 day) and borr_status= 'BS004' <br/>");
                sb.append("查询结果: "+list10.toString()+"<br/><br/>");
                logger.info("逾期的状态还是待还款 :{}",list10.toString());
            }

            List<String> list11 = problemDataWatchService.multiBankNum(beforeDefiniteDay(2));
            if(list11 != null && list11.size()>0){
                sb.append("多张相同的银行卡 ： <br/>");
                sb.append("SQL: select bank_num from ym_bank where status in (1,2)  and update_date>date_sub(curdate(),interval 2 day) group by bank_num having count(bank_num)>1 <br/>");
                sb.append("查询结果: "+list11.toString()+"<br/><br/>");
                logger.info("多张相同的银行卡:{}",list11.toString());
            }

            List<String> list12 = problemDataWatchService.multiOperatorOfContract();
            if(list12 != null && list12.size()>0){
                sb.append("一个合同同时分派给多个人审核 ： <br/>");
                sb.append("SQL: select a.borr_id from ym_review a, ym_borrow_list b where a.borr_id=b.id and b.borr_status = 'BS003' and a.review_type=1 group by a.borr_id having count(1)>1 <br/>");
                sb.append("查询结果: "+list12.toString()+"<br/><br/>");
                logger.info("一个合同同时分派给多个人审核:{}",list12.toString());
            }

            List<String> list13 = problemDataWatchService.multiLoanOfOrder(beforeDefiniteDay(4));
            if(list13 != null && list13.size()>0){
                sb.append("一笔订单放款多次！人工处理不修改数据 ： <br/>");
                sb.append("SQL: select conctact_id from ym_order where rl_state='s' and type=1 and update_date>STR_TO_DATE(#{DATE},'%Y-%m-%d %H:%i:%s') group by conctact_id having count(1)>1 <br/>");
                sb.append("查询结果: "+list13.toString()+"<br/><br/>");
                logger.info("一笔订单放款多次！人工处理不修改数据:{}",list13.toString());
            }

            List<String> list14 = problemDataWatchService.actualExceedLimit(beforeDefiniteDay(3));
            if(list14 != null && list14.size()>0){
                sb.append("正常结清实际上是逾期的 ： <br/>");
                sb.append("SQL: select id from ym_borrow_list where  borr_status='BS006' and  datediff(DATE_FORMAT(act_repay_date,'%Y-%m-%d'),DATE_FORMAT(planrepay_date,'%Y-%m-%d')) >1 and update_date > date_sub(curdate(),interval 1 day) <br/>");
                sb.append("查询结果: "+list14.toString()+"<br/><br/>");
                logger.info("正常结清实际上是逾期的:{}",list14.toString());
            }

            List<String> list15 = problemDataWatchService.multiOrderOfLoanAndRepayment(beforeDefiniteDay(4));
            if(list15 != null && list15.size()>0){
                sb.append("检查一笔放款或还款，有没有在order表中生成2笔相同订单号的流水 ： <br/>");
                sb.append("SQL: select id from ym_order where creation_date>date_sub(curdate(),interval 4 day) group by serial_no having count(1)>1 <br/>");
                sb.append("查询结果: "+list15.toString()+"<br/><br/>");
                logger.info("检查一笔放款或还款，有没有在order表中生成2笔相同订单号的流水:{}",list15.toString());
            }

            List<String> list16 = problemDataWatchService.noContractOrder(beforeDefiniteDay(4));
            if(list16 != null && list16.size()>0){
                sb.append("放款中的合同没有订单流水，要改为放款失败 ： <br/>");
                sb.append("SQL: select id from ym_borrow_list a where a.borr_status in ('BS011','BS012') and not exists (select 1 from ym_order b   where  a.id=b.conctact_id" +
                        "and b.type=1 ) and a.update_date>date_sub(curdate(),interval 4 day) <br/>");
                sb.append("查询结果: "+list16.toString()+"<br/><br/>");
                logger.info("放款中的合同没有订单流水，要改为放款失败:{}",list16.toString());
            }

            List<String> list17 = problemDataWatchService.unsualLoanOrder(beforeDefiniteDay(4));
            if(list17 != null && list17.size()>0){
                sb.append("非已放款、逾期、正常结清、逾期结清的合同是否有放款记录 ： <br/>");
                sb.append("SQL: select id from ym_borrow_list a where a.borr_status in ('BS001','BS002','BS003','BS007','BS008','BS009') and  exists (select 1 from ym_order b   where  a.id=b.conctact_id" +
                        "and b.type=1 and rl_state='s') and a.update_date>date_sub(curdate(),interval 4 day) <br/>");
                sb.append("查询结果: "+list17.toString()+"<br/><br/>");
                logger.info("非已放款、逾期、正常结清、逾期结清的合同是否有放款记录:{}",list17.toString());
            }

            List<String> list18 = problemDataWatchService.multiProfile(beforeDefiniteDay(4));
            if(list18 != null && list18.size()>0){
                sb.append("一个人有重复的个人信息 ： <br/>");
                sb.append("SQL: select per_id from ym_private where update_date>date_sub(curdate(),interval 4 day) group by per_id having count(1)>1  <br/>");
                sb.append("查询结果: "+list18.toString()+"<br/><br/>");
                logger.info("一个人有重复的个人信息:{}",list18.toString());
            }

            List<String> list19 = problemDataWatchService.multiMainCard();
            if(list19 != null && list19.size()>0){
                sb.append("一个人有2张主银行卡 ： <br/>");
                sb.append("SQL: select per_id from ym_bank  where status=1 group by per_id having count(1)>1  <br/>");
                sb.append("查询结果: "+list19.toString()+"<br/><br/>");
                logger.info("一个人有2张主银行卡:{}",list19.toString());
            }

            List<String> list20 = problemDataWatchService.exceedRepamentActualNotExceed(beforeDefiniteDay(3));
            if(list20 != null && list20.size()>0){
                sb.append("逾期结清但实际还款日小于应还款日+1天 ： <br/>");
                sb.append("SQL: select id from  ym_borrow_list where borr_status='BS010' and act_repay_date< DATE_ADD(planrepay_date,INTERVAL 1 day) and update_date > date_sub(curdate(),interval 3 day) <br/>");
                sb.append("查询结果: "+list20.toString()+"<br/><br/>");
                logger.info("逾期结清但实际还款日小于应还款日+1天:{}",list20.toString());
            }

            List<String> list21 = problemDataWatchService.contractLoaningAndActualFailed(beforeDefiniteDay(4));
            if(list21 != null && list21.size()>0){
                sb.append("合同是放款中，实际上是放款失败 ： <br/>");
                sb.append("SQL: select id from ym_borrow_list a where borr_status='BS011' and not EXISTS (select 1 from ym_order b where a.id=b.conctact_id and b.type=1 and b.rl_state<>'f') and a.update_date>date_sub(curdate(),interval 4 day) <br/>");
                sb.append("查询结果: "+list21.toString()+"<br/><br/>");
                logger.info("合同是放款中，实际上是放款失败:{}",list21.toString());
            }

            List<String> list22 = problemDataWatchService.signedContractNoAuditor(beforeDefiniteDay(4));
            if(list22 != null && list22.size()>0){
                sb.append("已签约的合同没有审核人 ： <br/>");
                sb.append("SQL:select b.id from  ym_borrow_list b where b.borr_status = 'BS003' and not EXISTS (select 1 from ym_review a where a.borr_id=b.id  and a.review_type=1) and b.update_date>date_sub(curdate(),interval 4 day) <br/>");
                sb.append("查询结果: "+list22.toString()+"<br/><br/>");
                logger.info("已签约的合同没有审核人:{}",list22.toString());
            }

            List<String> list23 = problemDataWatchService.manualContractNoAuditor(beforeDefiniteDay(4));
            if(list23 != null && list23.size()>0){
                sb.append("人工审核的合同没有审核人 ： <br/>");
                sb.append("SQL: select b.borr_id from  ym_borrow_manual b, ym_borrow_list c where b.borr_id=c.id and c.borr_status<>'BS007' and  not EXISTS (select 1 from ym_review a where a.borr_id=b.borr_id  and a.review_type=1) and b.creation_date>date_sub(curdate(),interval 4 day) <br/>");
                sb.append("查询结果: "+list23.toString()+"<br/><br/>");
                logger.info("人工审核的合同没有审核人:{}",list23.toString());
            }

            List<Map> list24 = problemDataWatchService.exceedRepaymentNoCollectionUpd(beforeDefiniteDay(4));
            if(list24 != null && list24.size()>0){
                sb.append("逾期结清的贷后催收数据没更新 ： <br/>");
                sb.append("SQL: select t.id, t.borr_num, t.borr_status, t.act_repay_date, t1.bedue_name, t1.status" +
                        "from ym_borrow_list t, ds_collectors_list t1 where t.borr_status = 'BS010'" +
                        "and t.borr_num = t1.contract_id and t1.STATUS <> 'B' and t1.is_delete = 2 and t.update_date>date_sub(curdate(),interval 4 day) <br/>");
                sb.append("查询结果: "+list24.toString()+"<br/><br/>");
                logger.info("逾期结清的贷后催收数据没更新:{}",list24.toString());
            }
            //长度大于0有错误数据
            if(sb.toString().length() >0){
                mailService.sendMail(problemDataMailFrom,problemDataMailTo.split(","),mailCopyTo,null,null,title,sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("异常数据监控报错",e);
        }
        logger.info("异常数据检查完毕-----");
    }

    /**
     * 获取指定日期
     * */
    private Map beforeDefiniteDay(int days){
        Calendar cl = new GregorianCalendar();
        cl.set(Calendar.DAY_OF_YEAR,cl.get(Calendar.DAY_OF_YEAR)-days);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map m = new HashMap(16);
        m.put("DATE",sdf.format(cl.getTime()));
        return m;
    }
}
