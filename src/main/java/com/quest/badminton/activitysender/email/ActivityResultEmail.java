package com.quest.badminton.activitysender.email;

import java.io.File;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActivityResultEmail extends BaseEmail {

	public ActivityResultEmail(String basePath, String contentFileName) {
		super(basePath, contentFileName);
	}

	@Override
	public Object getContent() {
		MimeMultipart msgMultipart = new MimeMultipart("mixed");
		try {
			MimeBodyPart txtContent = new MimeBodyPart();
			txtContent.setContent(this.content, "text/html;charset=utf-8");
			msgMultipart.addBodyPart(txtContent);
			MimeBodyPart imgPart = new MimeBodyPart();
			DataSource ds = new FileDataSource("src/main/resources/money.png");
			imgPart.setDataHandler(new DataHandler(ds));
			imgPart.setHeader("Content-ID", "<money>");
			msgMultipart.addBodyPart(imgPart);
		} catch (MessagingException e) {
			log.error("failed to set result email content");
			return this.content;
		}
		return msgMultipart;
	}
}
