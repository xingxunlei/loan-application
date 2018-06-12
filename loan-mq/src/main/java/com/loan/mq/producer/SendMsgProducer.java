package com.loan.mq.producer;

import com.loan.mq.domain.RequestDomain;

public interface SendMsgProducer {

	public void send(RequestDomain requestDomain) throws Exception;

}
