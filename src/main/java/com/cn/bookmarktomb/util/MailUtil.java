package com.cn.bookmarktomb.util;

import com.cn.bookmarktomb.model.cache.ConfigCache;
import com.cn.bookmarktomb.model.constant.EmailConstant;
import com.cn.bookmarktomb.model.entity.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author fallen-angle
 * This is used to send email.
 */
@Slf4j
@Component
public class MailUtil {

	public void sendTestEmail(Email email) {
		JavaMailSender mailSender = generateMailSender(email);
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper mineHelper = new MimeMessageHelper(message, true);
			mineHelper.setFrom(email.getUsername());
			mineHelper.setTo(email.getUsername());
			mineHelper.setSubject("Bookmark Tomb Test Email");
			mineHelper.setText(EmailConstant.TEST_EMAIL_HTML, true);
			mailSender.send(message);
		} catch (MessagingException e) {
			log.error("Send message to " + email.getUsername() + " error!");
		}

	}

	public void sendVerifyCodeEmail(String to, String code) {
		JavaMailSender mailSender = getConfigMailSender();
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper mineHelper = new MimeMessageHelper(message, true);
			mineHelper.setFrom(((Email) ConfigCache.get(ConfigCache.EMAIL)).getUsername());
			mineHelper.setTo(to);
			mineHelper.setSubject("Bookmark Tomb Verify Code Email");
			mineHelper.setText(String.format(EmailConstant.VERIFY_CODE_EMAIL_HTML, code), true);
			mailSender.send(message);
		} catch (MessagingException e) {
			log.error("Send message to " + to + " error!");
		}

	}

	private JavaMailSender getConfigMailSender(){
		return generateMailSender((Email) ConfigCache.get(ConfigCache.EMAIL));
	}

	private JavaMailSender generateMailSender(Email email) {
		JavaMailSenderImpl jms = new JavaMailSenderImpl();
		jms.setHost(email.getHost());
		jms.setPort(email.getPort());
		jms.setUsername(email.getUsername());
		jms.setPassword(email.getPassword());
		jms.setDefaultEncoding("Utf-8");
		Properties p = new Properties();
		p.setProperty("mail.smtp.auth", "true");
		jms.setJavaMailProperties(p);
		return jms;
	}
}
