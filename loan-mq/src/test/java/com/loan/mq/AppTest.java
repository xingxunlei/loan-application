package com.loan.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.loan.mq.domain.RequestDomain;
import com.loan.mq.producer.SendMsgProducer;

/**
 * Unit test for simple App.
 */
public class AppTest {
	private Logger logger = LoggerFactory.getLogger(AppTest.class);

	private ApplicationContext context = null;

	public void setUp() throws Exception {
		context = new ClassPathXmlApplicationContext("application.xml");
	}

	public void should_send_a_amq_message() throws Exception {

		ApplicationContext context = new ClassPathXmlApplicationContext(
				"application.xml");
		SendMsgProducer messageProducer = (SendMsgProducer) context
				.getBean("sendMsgProducer");
		int a = 1;
		while (true) {
			System.out.println(a);
			RequestDomain requestDomain = new RequestDomain(null, a + "", 200);
			messageProducer.send(requestDomain);
			if (a == 11)
				break;
			// try {
			// // 暂停一下，好让消息消费者去取消息打印出来
			// Thread.sleep(1000);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }

		}
		// long start = System.currentTimeMillis();
		// ThreadPoolTaskExecutor threadPool = (ThreadPoolTaskExecutor) context
		// .getBean("threadPool");
		// for (int i = 0; i < 100; i++) {
		// EhrDownloadTask ehrDownloadTask = (EhrDownloadTask) context
		// .getBean("ehrDownloadTask");
		// ehrDownloadTask.setSize(i);
		// threadPool.submit(ehrDownloadTask);
		// }
		// System.out.println(System.currentTimeMillis() - start);
	}

}
