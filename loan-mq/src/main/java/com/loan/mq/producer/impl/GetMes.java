package com.loan.mq.producer.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

@Component("messageReceiver")
public class GetMes implements MessageListener {

	@Override
	public void onMessage(Message message) {

		try {
			System.out.println("消费:"
					+ new ObjectInputStream(new ByteArrayInputStream(
					message.getBody())).readObject());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
