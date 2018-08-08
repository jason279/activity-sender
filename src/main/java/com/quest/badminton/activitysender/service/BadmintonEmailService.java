package com.quest.badminton.activitysender.service;

import java.time.LocalDate;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.quest.badminton.activitysender.email.ActivityResultEmail;
import com.quest.badminton.activitysender.email.ActivitySignupEmail;
import com.quest.badminton.activitysender.util.ServerConstants;

@Service
public class BadmintonEmailService {
	private static final Logger logger = LoggerFactory.getLogger(BadmintonEmailService.class);
	@Autowired
	private JavaMailSender mailSender;
	@Value("${badminton.app.base.path}")
	private String basePath;
	@Autowired
	private ClubWebSiteService webSiteService;

	@Scheduled(cron = "0 0 9 * * wed")
	public void sendAcitvitySignupEmail() {
		// create activity link
		String cookie = webSiteService.loginAsAdmin();
		webSiteService.addActivity(cookie, LocalDate.now().plusDays(2));
		logger.info("successfully add new activity."); 

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
			logger.info("successfully send activity sign up email.");
		} catch (Exception e) {
			logger.error("send activity sign up email failed.", e);
		}
	}

	@Scheduled(cron = "0 40 10 * * fri")
	public void sendAcitvityResultEmail() {
		ActivityResultEmail email = new ActivityResultEmail(basePath);
		updateEmailTo(email);
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
			logger.info("successfully send activity result email.");
		} catch (Exception e) {
			logger.error("send activity result email failed.", e);
		}
	}

	private void updateEmailTo(ActivityResultEmail email) {
		String cookie = webSiteService.login("Admin", "admin123");
		String content = webSiteService.visit(ServerConstants.CLUB_WEBSITE_BADMINTON_ACTIVITY_URL, HttpMethod.GET,
				cookie);
		String viewId = webSiteService.getViewId(content);
		content = webSiteService.visit(ServerConstants.CLUB_WEBSITE_BADMINTON_VIEW_URL, HttpMethod.GET, cookie, viewId);
		email.setTo(webSiteService.getSignupResultEmails(content));

	}
}
