package com.quest.badminton.activitysender.service;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.quest.badminton.activitysender.TemplateEmail;

@Service
public class BadmintonEmailService {
	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private TemplateEmail email;

	@Scheduled(cron = "0 0 9 * * wed")
	public void send() {
		try {
			MimeMessage msg = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(msg, "UTF-8");
			helper.setFrom(email.getFrom());
			helper.setTo(email.getTo());
			helper.setCc(email.getCc());
			helper.setSubject(email.getSubject());
			msg.setContent(email.getContent(), "text/html;charset=utf-8");
			this.mailSender.send(msg);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
