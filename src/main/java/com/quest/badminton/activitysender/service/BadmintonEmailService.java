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
import com.quest.badminton.activitysender.email.BaseEmail;

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

	@Scheduled(cron = "0 30 9 * * wed")
	public void sendAcitvitySignupEmailTask() {
		createActivity();
		sendEmail(new ActivitySignupEmail(basePath));
	}

	@Scheduled(cron = "0 0 12 * * thu")
	public void sendAcitvityResultEmailTask() {
		String cookie = webSiteService.loginAsAdmin();
		String viewId = webSiteService.getTopActivityViewId(cookie);
		closeActivity(cookie, viewId);
		sendEmail(createSignupResultEmail(cookie, viewId));
	}

	public void createActivity() {
		webSiteService.addActivity();
		log.info("successfully add new activity.");
	}

	public void closeActivity(String cookie, String viewId) {
		webSiteService.closeActivity(cookie, viewId);
		log.info("successfully close activity {}.", viewId);
	}

	public BaseEmail createSignupResultEmail(String cookie, String viewId) {
		String[] recipients = webSiteService.getSignupRecipients(cookie, viewId);
		boolean shouldCancel = recipients.length > 6;
		ActivityResultEmail email = new ActivityResultEmail(basePath,
				shouldCancel ? "activityCancelContent.txt" : "activityResultContent.txt");
		email.setContent(email.getContent().replace("${peopleNum}", String.valueOf(recipients.length)));
		email.setTo(recipients);
		return email;
	}

	public void sendEmail(BaseEmail email) {
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
			log.info("successfully send email {}.", email);
		} catch (Exception e) {
			log.error("send email {} failed.", email, e);
		}
	}

}
