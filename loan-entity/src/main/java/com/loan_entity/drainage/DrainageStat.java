package com.loan_entity.drainage;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by zhangqi on 17-11-20.
 */
@Setter
@Getter
public class DrainageStat implements Serializable {
    public static final int EVENT_TYPE_CLICK = 1;
    public static final int EVENT_TYPE_SUCCESS = 2;
    private int id;
    /**
     * 此事件用户的ID
     */
    private int per_id;
    /**
     * 此事件引流产品的id
     */
    private int drainage_id;
    /**
     * 此事件的事件类型
     */
    private String event_type;
    /**
     * 触发事件的时间
     */
    private Date event_time;

}
