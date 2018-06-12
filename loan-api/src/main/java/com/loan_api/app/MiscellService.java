package com.loan_api.app;

import com.loan_entity.app.YmFeedback;


public interface MiscellService {
	
	public String getMessageByUserId(String userId);
	
	public String getMyBorrowList(String userId);
	
	public String commonProblem(String userId);
	
	public String feedback(YmFeedback feed);
	
	public String getPersonInfo(String userId);
}
