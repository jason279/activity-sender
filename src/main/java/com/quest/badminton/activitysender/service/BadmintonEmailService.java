package com.quest.badminton.activitysender.service;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.quest.badminton.activitysender.email.ActivityResultEmail;
import com.quest.badminton.activitysender.email.ActivitySignupEmail;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BadmintonEmailService {
	@Autowired
	private JavaMailSender mailSender;
	@Value("${badminton.app.base.path}")
	private String basePath;
	@Autowired
	private ClubWebSiteService webSiteService;

	@Scheduled(cron = "0 0 10 * * wed")
	public void sendAcitvitySignupEmail() {
		// create activity link
		webSiteService.addActivity();
		log.info("successfully add new activity.");

		// send email
		ActivitySignupEmail email = new ActivitySignupEmail(basePath);
		try {
			MimeMessage msg = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(msg, "UTF-8");
			helper.setFrom(email.getFrom());
			helper.setTo(email.getTo());
			String[] cc = email.getCc();
			if (cc != null) {
				helper.setCc(email.getCc());
			}
			helper.setSubject(email.getSubject());
			msg.setContent(email.getContent(), "text/html;charset=utf-8");
			this.mailSender.send(msg);
			log.info("successfully send activity sign up email.");
		} catch (Exception e) {
			log.error("send activity sign up email failed.", e);
		}
	}

	@Scheduled(cron = "0 0 16 * * thu")
	public void sendAcitvityResultEmail() {
		String cookie = webSiteService.loginAsAdmin();
		String viewId = webSiteService.getTopActivityViewId(cookie);
		webSiteService.closeActivity(cookie, viewId);
		log.info("successfully close activity {}.", viewId);

		String[] recipients = webSiteService.getSignupResultEmails(cookie, viewId);
		boolean shouldCancel = (recipients.length < 4);
		ActivityResultEmail email = new ActivityResultEmail(basePath,
				shouldCancel ? "activityCancelContent.txt" : "activityResultContent.txt");
		email.setTo(recipients);
		try {
			MimeMessage msg = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(msg, "UTF-8");
			helper.setFrom(email.getFrom());
			helper.setTo(email.getTo());
			String[] cc = email.getCc();
			if (cc != null) {
				helper.setCc(email.getCc());
			}
			helper.setSubject(email.getSubject());
			msg.setContent(email.getContent(), "text/html;charset=utf-8");
			this.mailSender.send(msg);
			log.info("successfully send activity result email.");
		} catch (Exception e) {
			log.error("send activity result email failed.", e);
		}
	}
}
