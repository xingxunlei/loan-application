package com.loan_manage.entity;

import java.io.File;
import java.io.Serializable;

/**
 * Created by wanzezhong on 2017/9/13.
 */
public class RobotEntity implements Serializable{
   private String work_id;
   private String token;
   private String audio;
   private String work_result;

    public String getWork_id() {
        return work_id;
    }

    public void setWork_id(String work_id) {
        this.work_id = work_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getWork_result() {
        return work_result;
    }

    public void setWork_result(String work_result) {
        this.work_result = work_result;
    }
}
