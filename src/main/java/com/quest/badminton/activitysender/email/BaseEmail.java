package com.quest.badminton.activitysender.email;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BaseEmail {
	protected String from = "Jason.Tian@quest.com";
	protected String[] to;
	protected String cc = "Mark.Zhu@quest.com";
	protected String subject;
	protected String content;

	protected String basePath;

	protected abstract String[] initTo() throws Exception;

	protected abstract String getContentFileName();

	protected abstract int getDayOffset();

	public BaseEmail(String basePath) {
		this.basePath = basePath;
		SimpleDateFormat sdf = new SimpleDateFormat("周五羽毛球活动正常进行(MM月dd日)");
		this.subject = sdf.format(new Date(System.currentTimeMillis() + getDayOffset() * 24 * 60 * 60 * 1000));
		try {
			this.to = initTo();
			initContent();
		} catch (Exception e) {
			this.to = new String[] { "Jason.Tian@quest.com" };
			this.cc = null;
			this.content = "Exception thrown by badminton email sender:" + e.getMessage();
		}
	}

	protected void initContent() throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(this.basePath + getContentFileName()), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			this.content = sb.toString();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String[] getTo() {
		return to;
	}

	public void setTo(String[] to) {
		this.to = to;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
