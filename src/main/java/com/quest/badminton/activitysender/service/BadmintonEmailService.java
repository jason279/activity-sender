package com.quest.badminton.activitysender.service;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.quest.badminton.activitysender.email.ActivityResultEmail;
import com.quest.badminton.activitysender.email.ActivitySignupEmail;

@Service
public class BadmintonEmailService {
	@Autowired
	private JavaMailSender mailSender;

	@Scheduled(cron = "0 30 12 * * wed")
	public void sendAcitvitySignupEmail() {
		ActivitySignupEmail email = new ActivitySignupEmail();
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

//	@Scheduled(cron = "0 48 16 * * fri")
	public void sendAcitvityResultEmail() {
		ActivityResultEmail email = new ActivityResultEmail();
		try {
			MimeMessage msg = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(msg, "UTF-8");
			helper.setFrom(email.getFrom());
			helper.setTo(email.getTo());
			// helper.setCc(email.getCc());
			helper.setSubject(email.getSubject());
			msg.setContent(email.getContent(), "text/html;charset=utf-8");
			this.mailSender.send(msg);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
