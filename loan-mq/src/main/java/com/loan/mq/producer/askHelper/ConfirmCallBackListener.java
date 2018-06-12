package com.loan.mq.producer.askHelper;

import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Service;

@Service("confirmCallBackListener")
public class ConfirmCallBackListener implements ConfirmCallback {

	@Override
	public void confirm(CorrelationData arg0, boolean arg1, String arg2) {
		System.out.println("confirm--:correlationData:" + arg0 + ",ack:" + arg1
				+ ",cause:" + arg2);
	}

}
