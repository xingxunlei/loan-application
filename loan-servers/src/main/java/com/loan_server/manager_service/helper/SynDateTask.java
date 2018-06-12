package com.loan_server.manager_service.helper;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.loan_entity.app.BorrowList;
import com.loan_entity.app.DsAppReport;
import com.loan_entity.manager_vo.RepaymentPlanVo;
import com.loan_server.app_mapper.BorrowListMapper;
import com.loan_server.app_mapper.DsAppReportMapper;
import com.loan_server.manager_mapper.RepaymentPlanMapper;

import redis.clients.jedis.JedisCluster;

@Component("synDateTask")
@Scope("prototype")
public class SynDateTask implements Callable<Object>, Serializable {

	private static Logger log = Logger.getLogger(SynDateTask.class);

	private List<DsAppReport> synDatas;
	@Autowired
	private BorrowListMapper borrowListMapper;
	@Autowired
	private RepaymentPlanMapper repaymentPlanMapper;
	@Autowired
	private DsAppReportMapper dsAppReportMapper;
    @Autowired
    private JedisCluster jedisCluster;
	public synchronized void setSynDatas(List<DsAppReport> synDatas) {
		this.synDatas = synDatas;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Object call() throws Exception {
		long starttime = System.currentTimeMillis();
		for (DsAppReport dsBatchVo : synDatas) {
			try {
                if (StringUtils.isNotEmpty(jedisCluster.get(dsBatchVo.getSysno()
						+ ""))) {
					log.error("跑掉重复数据：" + dsBatchVo.getBrrownum());
					continue;
				}
				BorrowList borrowList = new BorrowList();
				borrowList.setBorrNum(dsBatchVo.getBrrownum());
				if (dsBatchVo.getActrepaydate() != null
						&& !("").equals(dsBatchVo.getActrepaydate())) {
					borrowList.setActRepayDate(new Date());
				}
				borrowList.setActRepayAmount(dsBatchVo.getActrepayamount());
				borrowList.setBorrStatus(dsBatchVo.getBorrstatus());
				borrowList.setPlanRepay(dsBatchVo.getPlanrepay());

				RepaymentPlanVo repaymentPlanVo = new RepaymentPlanVo();
				repaymentPlanVo.setBorrNum(dsBatchVo.getBrrownum());
				repaymentPlanVo.setIsSettle(Integer.valueOf(dsBatchVo
						.getIssettle()));
				repaymentPlanVo.setPenalty(new BigDecimal(dsBatchVo
						.getPenalty()));
				repaymentPlanVo.setPenaltyInterest(new BigDecimal(dsBatchVo
						.getPenaltyinterest()));
				repaymentPlanVo.setSurplusQuota(dsBatchVo.getSurplusquota());
				repaymentPlanVo.setSurplusMoney(dsBatchVo.getSurplusmoney());
				repaymentPlanVo.setSurplusInterest(dsBatchVo
						.getSurplusinterest());
				repaymentPlanVo
						.setSurplusPenalty(dsBatchVo.getSurpluspenalty());
				repaymentPlanVo.setSurplusPenaltyInteres(dsBatchVo
						.getSurpluspenaltyinteres());

				int result1 = borrowListMapper.updateByBrroNum(borrowList);
				int result2 = repaymentPlanMapper
						.updateByBrroNum(repaymentPlanVo);
				if (result1 + result2 > 0) {
					// log.error("跑批处理成功+" + dsBatchVo.getBrrownum());
					dsBatchVo.setStatus(1);
					dsAppReportMapper.updateByPrimaryKeySelective(dsBatchVo);
                    jedisCluster.setex(dsBatchVo.getSysno() + "", 24 * 60 * 60,
							dsBatchVo.getSysno() + "");
				} else {
					log.error("借款表及还款计划表更新失败：" + dsBatchVo.getBrrownum());
				}
			} catch (Exception e) {
				log.error(e.getMessage());
				log.error("跑批处理失败+" + dsBatchVo.getBrrownum());
			}

		}
		log.error(Thread.currentThread().getName() + "线程执行结束用时"
				+ (System.currentTimeMillis() - starttime));
		return 1;
	}

}
