package com.jhh.util;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

/**
 * 发送邮件
 * @author wanzezhong
 * 2017年6月20日 15:59:36
 */
public class MailSender {
	private String host = ""; // smtp服务器
	private String from = ""; // 发件人地址
	private String[] to; // 收件人地址
	private String[] copyto; //抄送地址
	private String affix = ""; // 附件地址
	private String affixName = ""; // 附件名称
	private String[] affixArray = {}; // 附件地址
	private String[] affixNameArray = {}; // 附件名称
	private String user = ""; // 用户名
	private String pwd = ""; // 密码
	private String subject = ""; // 邮件标题
	private String content; //发送内容

	/**
	 * 设置邮件属性
	 * @param from    发送人
	 * @param to      收件人
	 * @param copyto  抄送地址
	 * @param subject 邮件标题
	 * @param content 发送内容
	 */
	public void setAddress(String from, String[] to, String[] copyto, String subject,String content) {
		this.from = from;
		this.to = to;
		this.copyto = copyto;
		this.subject = subject;
		this.content = content;
	}

	/**
	 * 设置邮件附件属性
	 * @param affix 附件地址
	 * @param affixName 附件名称
	 */
	public void setAffix(String affix, String affixName) {
		this.affix = affix;
		this.affixName = affixName;
	}

	/**
	 * 设置邮件附件属性
	 * @param affix 附件地址
	 * @param affixName 附件名称
	 */
	public void setAffixArray(String[] affix, String[] affixName) {
		this.affixArray = affix;
		this.affixNameArray = affixName;
	}


	/**
	 * 发送邮件
	 * @param host smtp服务器
	 * @param user 用户名
	 * @param pwd  第三方登录授权码
	 */
	public void send(String host, String user, String pwd) {
		this.host = host;
		this.user = user;
		this.pwd = pwd;

		Properties props = new Properties();

		// 设置发送邮件的邮件服务器的属性（这里使用网易的smtp服务器）
		props.put("mail.smtp.host", host);
		// 需要经过授权，也就是有户名和密码的校验，这样才能通过验证（一定要有这一条）
//		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.auth", false);

		// 用刚刚设置好的props对象构建一个session
		Session session = Session.getDefaultInstance(props);

		// 有了这句便可以在发送邮件的过程中在console处显示过程信息，供调试使
		// 用（你可以在控制台（console)上看到发送邮件的过程）
		session.setDebug(true);

		// 用session为参数定义消息对象
		MimeMessage message = new MimeMessage(session);
		try {
			// 加载发件人地址
			message.setFrom(new InternetAddress(from));
			// 加载收件人地址
            if (to != null && to.length > 0) {
                String toListStr = getMailList(to);
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toListStr));
            }
            // 加载抄送人地址
            if (copyto != null && copyto.length > 0) {
                String ccListStr = getMailList(copyto);
                message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccListStr)); 
            }
			// 加载标题
			message.setSubject(subject);

			// 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
			Multipart multipart = new MimeMultipart();

			// 设置邮件的文本内容
			BodyPart contentPart = new MimeBodyPart();
			contentPart.setContent(content, "text/html;charset=utf-8");
			multipart.addBodyPart(contentPart);

			setAffixMultipart(multipart);
//			if(affix != null){
//				// 添加附件
//				BodyPart messageBodyPart = new MimeBodyPart();
//				DataSource source = new FileDataSource(affix);
//				// 添加附件的内容
//				messageBodyPart.setDataHandler(new DataHandler(source));
//				// 添加附件的标题
//				// 这里很重要，通过下面的Base64编码的转换可以保证你的中文附件标题名在发送时不会变成乱码
//				sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
//				messageBodyPart.setFileName("=?GBK?B?" + enc.encode(affixName.getBytes()) + "?=");
//				multipart.addBodyPart(messageBodyPart);
//			}

			// 将multipart对象放到message中
			message.setContent(multipart);
			// 保存邮件
			message.saveChanges();
			// 发送邮件
			Transport transport = session.getTransport("smtp");
			// 连接服务器的邮箱
			transport.connect(host, user, pwd);
			// 把邮件发送出去
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setAffixMultipart(Multipart multipart) throws MessagingException {
		if(Detect.notEmpty(affix) && Detect.notEmpty(affixName)){
			// 添加附件
			BodyPart messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(affix);
			// 添加附件的内容
			messageBodyPart.setDataHandler(new DataHandler(source));
			// 添加附件的标题
			// 这里很重要，通过下面的Base64编码的转换可以保证你的中文附件标题名在发送时不会变成乱码
			sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
			messageBodyPart.setFileName("=?GBK?B?" + enc.encode(affixName.getBytes()) + "?=");
			multipart.addBodyPart(messageBodyPart);
		}else if (Detect.notEmpty(affixArray)  && Detect.notEmpty(affixNameArray) && affixArray.length == affixNameArray.length){
			for(int i = 0; i < affixArray.length; i++){
				BodyPart messageBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(affixArray[i]);
				// 添加附件的内容
				messageBodyPart.setDataHandler(new DataHandler(source));
				// 添加附件的标题
				// 这里很重要，通过下面的Base64编码的转换可以保证你的中文附件标题名在发送时不会变成乱码
				sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
				messageBodyPart.setFileName("=?GBK?B?" + enc.encode(affixNameArray[i].getBytes()) + "?=");
				multipart.addBodyPart(messageBodyPart);
			}
		}
	}

	public String getMailList(String[] mailArray) {
        StringBuffer toList = new StringBuffer();
        int length = mailArray.length;
        if (mailArray != null && length < 2) {
            toList.append(mailArray[0]);
        } else {
            for (int i = 0; i < length; i++) {
                toList.append(mailArray[i]);
                if (i != (length - 1)) {
                    toList.append(",");
                }

            }
        }
        return toList.toString();
    }

	public static void main(String[] args) {
		MailSender cn = new MailSender();
	
		String[] to = {"wanzezhong@jinhuhang.com.cn"};
		String[] copyto = {"330402499@qq.com"};
		
		// 设置要发送附件的位置和标题
		cn.setAffix("C:\\Users\\wanzezhong\\Desktop\\billingNotice20170620.xls", "billingNotice20170620.xls");
//		// 设置发件人地址、收件人地址和邮件标题
//		cn.setAddress("carl_wanzezhong@163.com", to, copyto, "通知还款短信号码", "号码推送");
//		// 设置smtp服务器以及邮箱的帐号和密码
//		cn.send("smtp.163.com", "carl_wanzezhong@163.com", "wan123123");
		
//		cn.setAddress("wanzezhong@jinhuhang.com.cn", to, copyto, "通知还款短信号码", "号码推送");
//		cn.send("cas.jhh.com", "wanzezhong", "wan+123");
		
		cn.setAddress("jinhuhanghlwzx@sina.com", to, copyto, "通知还款短信号码", "号码推送");
		cn.send("smtp.sina.com", "jinhuhanghlwzx@sina.com", "jhh123456");
		
	}
}
