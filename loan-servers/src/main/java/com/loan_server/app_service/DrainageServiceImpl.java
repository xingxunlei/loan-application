package com.loan_server.app_service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.fastjson.JSONObject;
import com.loan_api.app.DrainageService;
import com.loan_entity.app.NoteResult;
import com.loan_entity.drainage.Drainage;
import com.loan_entity.drainage.DrainageStat;
import com.loan_server.loan_mapper.DrainageMapper;
import com.loan_utils.util.RedisConst;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangqi on 17-11-20.
 */
@Service
public class DrainageServiceImpl implements DrainageService{

    private static final Logger mLogger = LoggerFactory
            .getLogger(DrainageServiceImpl.class);

    /**
     * where条件 当status为1时 为启用状态
     */
    private static final String DRAINAGE_STATUS = "1";

    @Autowired
    private DrainageMapper mDrainageMapper;

    @Autowired
    private JedisCluster mJedisCluster;

    /**
     * 拿到所有引流渠道要展示的产品
     * @param per_id
     * @return
     */
    @Override
    public NoteResult getDrainage(String per_id) {
        NoteResult noteResult = new NoteResult("201","系统繁忙");
        try {
            if(TextUtils.isEmpty(per_id)){
                //TODO
            }

            List<Drainage> datas = mDrainageMapper.selectDrainage(DRAINAGE_STATUS);
            noteResult.setData(datas);
            noteResult.setCode("200");
            noteResult.setInfo("请求数据成功");
        }catch (Exception e){
            mLogger.error("抓取引流产品error: "+e.toString());
        }
        return noteResult;
    }

    /**
     * 记录一次 引流事件
     */
//    @Async
    @Override
    public String drainageEvent(DrainageStat drainageStat) {
//        insert(drainageStat,1);

        /**
         * redis存入引流日志
         */
        mJedisCluster.lpush(RedisConst.DRAINAGE_STAT, JSONObject.toJSONString(drainageStat));
        return "success";
    }


    @Override
    public void syncDrainage() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                task();
            }
        });
        thread.start();
    }

    /**
     * 从redis中 取出日志
     */
    private void task() {

        try {
            List<DrainageStat> drainageStatList = new ArrayList<>();
            for(;;){
                String rpop = mJedisCluster.rpop(RedisConst.DRAINAGE_STAT);

                if(TextUtils.isEmpty(rpop)){
                    break;
                }

                DrainageStat drainageStat = JSONObject.parseObject(rpop, DrainageStat.class);
                drainageStatList.add(drainageStat);
            }

            /**
             * 一次性只插入5000条数据 分组
             */
            List<List<DrainageStat>> disperse = disperse(drainageStatList,5000);

            if(disperse == null || disperse.size() == 0){
                mLogger.info("没有入库或者异常");
                return;
            }

            for (int i = 0; i < disperse.size(); i++) {
                mLogger.info("引流: 日志第"+i+"组入库");
                mDrainageMapper.insertDrainageStat(disperse.get(i));
            }

        }catch (Exception e){
            mLogger.info("引流定时任务异常:"+e.toString());
        }finally {
            mLogger.info("引流定时任务结束");
        }
    }



    /**
     * 将集合根据数量分成若干小姐
     * @param list
     * @param quantity
     * @return
     */
    public <T> List<List<T>> disperse(List<T> list, int quantity){
        if(list == null || list.size() == 0){
            return null;
        }

        if(quantity <= 0){
            return null;
        }

        List<List<T>> tempList = new ArrayList<>();
        int count = 0;
        while (count < list.size()) {
            tempList.add(new ArrayList(list.subList(count, (count + quantity) > list.size() ? list.size() : count + quantity)));
            count += quantity;
        }
        return tempList;
    }
}
