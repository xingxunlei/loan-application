package com.loan.mq.producer.askHelper;

import java.io.UnsupportedEncodingException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.stereotype.Service;

@Service("returnCallBackListener")
public class ReturnCallBackListener implements ReturnCallback {

	@Override
	public void returnedMessage(Message arg0, int arg1, String arg2,
			String arg3, String arg4) {
		try {
			System.out.println("return--message:"
					+ new String(arg0.getBody(), "UTF-8") + ",replyCode:"
					+ arg1 + ",replyText:" + arg2 + ",exchange:" + arg3
					+ ",routingKey:" + arg4);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
