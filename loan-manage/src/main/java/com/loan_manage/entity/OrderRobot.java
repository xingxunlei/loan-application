package com.loan_manage.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ym_order_robot")
public class OrderRobot implements Serializable{
	private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

    private String code;

    private String thirdCode;

    private Integer state;

    private Integer borrId;

    private String sendParam;

    private String recvParam;

    private String audio;

    private Date createDate;

    private String createUser;

    private Date updateDate;

    private String updateUser;

    private String sucessTime;

    private String phone;
    private String duration;
    private Integer score;
    private Integer gender;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSucessTime() {
        return sucessTime;
    }

    public void setSucessTime(String sucessTime) {
        this.sucessTime = sucessTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getThirdCode() {
        return thirdCode;
    }

    public void setThirdCode(String thirdCode) {
        this.thirdCode = thirdCode;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getBorrId() {
        return borrId;
    }

    public void setBorrId(Integer borrId) {
        this.borrId = borrId;
    }

    public String getSendParam() {
        return sendParam;
    }

    public void setSendParam(String sendParam) {
        this.sendParam = sendParam;
    }

    public String getRecvParam() {
        return recvParam;
    }

    public void setRecvParam(String recvParam) {
        this.recvParam = recvParam;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
}