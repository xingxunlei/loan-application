package com.loan.mq.producer.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.loan.mq.domain.RequestDomain;
import com.loan.mq.producer.SendMsgProducer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

@Component("sendMsgProducer")
public class SendMsgProducerImpl implements SendMsgProducer {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	private RabbitTemplate rabbitTemplate;


	@Override
	public void send(RequestDomain requestDomain) throws Exception {
		final String request = JSON.toJSONString(requestDomain);

		final String exchange = rabbitTemplate.getExchange();
		final String routingKey = rabbitTemplate.getRoutingKey();
		System.out.println(exchange + routingKey);
		//@SuppressWarnings("unchecked")
		/*boolean iscommit = rabbitTemplate.execute(new ChannelCallback() {
			@Override
			public Object doInRabbit(Channel channel) throws Exception {
				channel.basicPublish(exchange, routingKey, true,
						MessageProperties.PERSISTENT_BASIC,
						(request + "").getBytes());
				return channel.waitForConfirms();
			}
		});*/
	}

}
