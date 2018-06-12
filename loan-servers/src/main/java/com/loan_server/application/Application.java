package com.loan_server.application;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Application {

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext(new String[]{ "classpath:spring/applicationContext.xml", "classpath:spring/provider.xml" });
        context.start();
        System.out.println("系统启动成功");
        System.in.read();
    }

}
