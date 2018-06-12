package com.loan_server.loan_service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.loan_api.loan.DelayQueueService;
import com.loan_api.loan.YsbpayService;
import com.loan_entity.loan_vo.NotExistOrder;
import com.loan_utils.payment.CollectUtil;
import com.loan_utils.payment.PaymentUtil;

import com.loan_utils.util.PropertiesReaderUtil;
import com.loan_utils.util.RedisConst;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.*;

/**
 * @author xuepengfei
 */
public class DelayQueueServiceImpl implements DelayQueueService {

    private static final Logger logger = LoggerFactory.getLogger(DelayQueueServiceImpl.class);


    @Autowired
    private YsbpayService ysbpayService;

    @Autowired
    private JedisCluster jedisCluster;

    private static final String QUEUE_NAME = RedisConst.DELAY_QUEUE;

    private static final String QUEUE_ELEMENT_EXIST = RedisConst.QUEUE_ELEMENT_EXIST;

    //延时时间
    private static final String DELAYSECOND = PropertiesReaderUtil.read("third", "delaySecond");


    @Override
    public void addToDeplayQueue(String serialNo, int type, int times) {
//        //检查队列中是否有该笔订单了 已有就不再处理
        if (StringUtils.isEmpty(jedisCluster.get(QUEUE_ELEMENT_EXIST + serialNo))) {
            String set = jedisCluster.set(QUEUE_ELEMENT_EXIST + serialNo, serialNo, "NX", "EX", 7*24*60*60);
            if ("OK".equals(set)){
                NotExistOrder task = new NotExistOrder();
                task.setSerialNo(serialNo);
                task.setType(type);
                task.setTimes(times);
                logger.info(task.getSerialNo() + "第一次进入队列，剩余次数" + task.getTimes());
                Long lpush = jedisCluster.lpush(QUEUE_NAME, JSONObject.toJSONString(task));
                logger.info("lpush结果："+lpush);
            }
        }
    }

    private final static ScheduledThreadPoolExecutor poll = new ScheduledThreadPoolExecutor(1 );
    private void addAgain(final String serialNo, final int type, final int times) {
        poll.schedule(new Runnable() {
            @Override
            public void run() {
                NotExistOrder task = new NotExistOrder();
                task.setSerialNo(serialNo);
                task.setType(type);
                task.setTimes(times);
                logger.info(task.getSerialNo() + "重新进入队列，剩余次数" + task.getTimes());
                Long lpush = jedisCluster.lpush(QUEUE_NAME, JSONObject.toJSONString(task));
                logger.info("lpush结果："+lpush);
            }
        }, Integer.valueOf(DELAYSECOND), TimeUnit.SECONDS);
    }

    @Override
    public void transferFromDelayQueue() {
        while (true) {
            try {
                List<String> brpop = jedisCluster.brpop(0, QUEUE_NAME);
                if (brpop.size() == 2) {
                    NotExistOrder task = JSONObject.toJavaObject(JSONObject.parseObject(brpop.get(1)), NotExistOrder.class);

                    logger.info("队列中弹出元素：" + JSONObject.toJSONString(task));

                    int times = task.getTimes();
                    if (times > 0) {
                        //还有执行次数  执行
                        //查询银生宝 没结果（交易不存在） times -1  延时重置 放回队列
                        if (!query(task)) {

                            logger.info(task.getSerialNo() + "重新进入队列，剩余次数" + task.getTimes());
                            addAgain(task.getSerialNo(), task.getType(), times - 1);
                        }
                    } else {
                        //没有执行次数了 确定订单失败
                        logger.info(task.getSerialNo() + "已无次数，最终结果为失败");
                        orderFail(task);

                    }
                }else {
                    logger.error("队列异常："+JSONObject.toJSONString(brpop));
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

        }
    }


    public void test() throws InterruptedException {
        long now = System.currentTimeMillis();

        NotExistOrder task = new NotExistOrder();
        task.setSerialNo("1111111111");
        task.setTimes(3);
        task.setType(1);
        System.out.println(task.getSerialNo() + "第一次进入队列，剩余次数" + task.getTimes());
        addToDeplayQueue(task.getSerialNo(), task.getType(), task.getTimes());
        transferFromDelayQueue();

    }

    /*
        查询第三方订单是否有结果  有结果 处理 返回true   没结果 返回false
     */
    private boolean query(NotExistOrder task) {
        if (task.getType() == 1) {
            //放款订单
            String response = PaymentUtil.queryOrderStatus(task.getSerialNo());
            String code = JSONObject.parseObject(response).getString("result_code");
//            String code = "200";
            if ("2002".equals(code)) {
                //订单不存在
                return false;
            } else {
                return true;
            }
        } else {
            //还款订单
            String response = CollectUtil.queryOrder(task.getSerialNo());
            String code = JSONObject.parseObject(response).getString("result_code");
            if ("2010".equals(code)) {
                //订单不存在
                return false;
            } else {
                return true;
            }

        }
    }

    private void orderFail(NotExistOrder task) {
        if (task.getType() == 1) {
            //放款订单失败
            ysbpayService.fileCaozuo(task.getSerialNo(), "最终结果：交易不存在");
            logger.info(task.getSerialNo()+"订单最终处理失败");
        } else {
            //扣款订单失败
            ysbpayService.paymentFail(task.getSerialNo(), "最终结果：交易不存在");
            logger.info(task.getSerialNo()+"订单最终处理失败");
        }
    }

}

