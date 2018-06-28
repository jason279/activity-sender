package com.quest.badminton.activitysender;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TemplateEmail {
	private String from = "Jason.Tian@quest.com";
	private String[] to;
	private String cc = "Mark.Zhu@quest.com";
	private String subject;
	private String content;

	public TemplateEmail() {
		SimpleDateFormat sdf = new SimpleDateFormat("周五羽毛球活动正常进行(MM月dd日)");
		this.subject = sdf.format(new Date(System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000));
		try {
			initTo();
			initContent();
		} catch (Exception e) {
			this.to = new String[] { "Jason.Tian@quest.com" };
			this.content = "Exception thrown by badminton email sender:" + e.getMessage();
		}
	}

	private void initTo() throws IOException {
		List<String> to = new ArrayList<>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("src/main/resources/to.txt"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				to.add(line);
			}
			this.to = to.toArray(new String[] {});
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	private void initContent() throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("src/main/resources/content.txt"));
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
