package com.loan_web.app;

import com.alibaba.fastjson.JSONObject;
import com.loan_api.app.DrainageService;
import com.loan_entity.app.NoteResult;
import com.loan_entity.drainage.DrainageStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * Created by zhangqi on 17-11-20.
 */

@Controller
@RequestMapping("/drainage")
public class DrainageController {

    private static final Logger mLogger = LoggerFactory.getLogger(DrainageController.class);

    @Autowired
    DrainageService mDrainageService;

    @ResponseBody
    @RequestMapping(value = "/getDrainage",method = RequestMethod.POST)
    public String getDrainage(String per_id) {
        return JSONObject.toJSONString(mDrainageService.getDrainage(per_id));
    }

    /**
     * event_type  1 点击
     *             2 成功回调
     * @param per_id
     * @param drainage_id
     * @param event_type
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/drainageEvent",method = RequestMethod.POST)
    public String getDrainage(int per_id, int drainage_id, String event_type) {
        DrainageStat drainageStat = new DrainageStat();
        drainageStat.setDrainage_id(drainage_id);
        drainageStat.setPer_id(per_id);
        drainageStat.setEvent_type(event_type);
        drainageStat.setEvent_time(new Date());
        mDrainageService.drainageEvent(drainageStat);
        return JSONObject.toJSONString(new NoteResult("200","事件插入成功"));
    }
}
