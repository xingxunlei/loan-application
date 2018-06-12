package com.loan_manage.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ym_robot_question")
public class RobotQuestion implements Serializable{
	private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

    private Integer robotOrderId;

    private String questionId;

    private String question;

    private String userInput;

    private String rightAnswers;

    private String answerResults;

    private String interactiveWay;

    private String duration;

    private Date createDate;
;
    private Date updateDate;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRobotOrderId() {
        return robotOrderId;
    }

    public void setRobotOrderId(Integer robotOrderId) {
        this.robotOrderId = robotOrderId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public String getRightAnswers() {
        return rightAnswers;
    }

    public void setRightAnswers(String rightAnswers) {
        this.rightAnswers = rightAnswers;
    }

    public String getAnswerResults() {
        return answerResults;
    }

    public void setAnswerResults(String answerResults) {
        this.answerResults = answerResults;
    }

    public String getInteractiveWay() {
        return interactiveWay;
    }

    public void setInteractiveWay(String interactiveWay) {
        this.interactiveWay = interactiveWay;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

}