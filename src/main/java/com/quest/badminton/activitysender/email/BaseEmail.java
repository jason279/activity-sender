package com.quest.badminton.activitysender.email;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.quest.badminton.activitysender.util.ActivityUtils;

public abstract class BaseEmail {
	protected String from = "Jason.Tian@quest.com";
	protected String[] to;
	// protected String[] cc = new String[] { "Mark.Zhu@quest.com", "Jason.Tian@quest.com" };
	protected String[] cc = new String[] { "Jason.Tian@quest.com" };
	protected String subject;
	protected String content;
	private String contentFileName;

	protected String basePath;

	protected String[] initTo() throws Exception {
		// empty by default
		return new String[0];
	}

	public BaseEmail(String basePath, String contentFileName) {
		this.basePath = basePath;
		this.contentFileName = contentFileName;
		this.subject = ActivityUtils.getActivityDateString("周五羽毛球活动正常进行(MM月dd日)");
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
		BufferedReader reader = Files.newBufferedReader(Paths.get(this.basePath, this.contentFileName));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		this.content = sb.toString();
		reader.close();
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

	public String[] getCc() {
		return cc;
	}

	public void setCc(String[] cc) {
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
