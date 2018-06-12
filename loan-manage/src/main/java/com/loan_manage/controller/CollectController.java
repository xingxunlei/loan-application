package com.loan_manage.controller;

import com.loan_entity.common.Constants;
import com.loan_manage.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.JedisCluster;

import javax.servlet.http.HttpServletRequest;


/**
 * 审核管理
 * Create by Jxl on 2017/9/11
 */
@Controller
@RequestMapping("/collect")
public class CollectController {

    @Autowired
    private JedisCluster jedisCluster;

    /**
     * 切换代扣到海尔金融
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/switchCollectToHaier")
    public Result switchCollectToHaier(HttpServletRequest request){
        Result result = new Result();
        try {
            String status = jedisCluster.set("YM_a_collectType", Constants.HAIER_PLATFORM);
            result.setCode(Result.SUCCESS);
            result.setMessage(status);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage(e.getMessage());
            return result;
        }
    }

    /**
     * 切换代扣到银生宝
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/switchCollectToYsb")
    public Result switchCollectToYsb(HttpServletRequest request){
        Result result = new Result();
        try {
            String status = jedisCluster.set("YM_a_collectType", Constants.YSB_PLATFORM);
            result.setCode(Result.SUCCESS);
            result.setMessage(status);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage(e.getMessage());
            return result;
        }
    }

    /**
     * 切换APP主动还款到海尔金融
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/switchAppToHaier")
    public Result switchAppToHaier(HttpServletRequest request){
        Result result = new Result();
        try {
            String status = jedisCluster.set("YM_a_appRepayType", Constants.HAIER_PLATFORM);
            result.setCode(Result.SUCCESS);
            result.setMessage(status);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage(e.getMessage());
            return result;
        }
    }

    /**
     * 切换APP主动还款到银生宝
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/switchAppToYsb")
    public Result switchAppToYsb(HttpServletRequest request){
        Result result = new Result();
        try {
            String status = jedisCluster.set("YM_a_appRepayType", Constants.YSB_PLATFORM);
            result.setCode(Result.SUCCESS);
            result.setMessage(status);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage(e.getMessage());
            return result;
        }
    }
}
