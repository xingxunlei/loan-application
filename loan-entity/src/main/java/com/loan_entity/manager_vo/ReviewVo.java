package com.loan_entity.manager_vo;

import java.io.Serializable;
import java.util.Date;

import com.loan_entity.manager.Review;

public class ReviewVo extends Review implements Serializable{

    private String emplloyeeName;

    private String meaning;

	/**
	 * @return the emplloyeeName
	 */
	public String getEmplloyeeName() {
		return emplloyeeName;
	}

	/**
	 * @param emplloyeeName the emplloyeeName to set
	 */
	public void setEmplloyeeName(String emplloyeeName) {
		this.emplloyeeName = emplloyeeName;
	}

	/**
	 * @return the meaning
	 */
	public String getMeaning() {
		return meaning;
	}

	/**
	 * @param meaning the meaning to set
	 */
	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}
    
    

}