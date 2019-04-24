package com.quest.badminton.activitysender.email;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.quest.badminton.activitysender.util.ActivityUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseEmail {
	private static final String EMAIL_JASON = "Jason.Tian@quest.com";
	private static final String EMAIL_SCOTT = "Scott.He@quest.com";
	private static final String EMAIL_JEFFREY = "Jeffrey.Wu@quest.com";
	private static final String[] EMAIL_ORGANIZER = new String[] { EMAIL_SCOTT, EMAIL_JASON, EMAIL_JEFFREY };

	protected String from;
	protected String[] to;
	protected String[] cc;
	protected String subject = ActivityUtils.getActivityDateString("周五羽毛球活动正常进行(MM月dd日)");
	protected String content;

	private String contentFileName;
	protected String basePath;

	protected String[] initTo() throws Exception {
		// empty by default
		return new String[0];
	}

	public BaseEmail(String from, String[] to, String[] cc, String content) {
		this.from = from;
		this.to = to;
		this.cc = cc;
		this.content = content;
	}

	public BaseEmail() {
		this.from = EMAIL_JASON;
		this.cc = EMAIL_ORGANIZER;
	}

	public BaseEmail(String content) {
		this(EMAIL_JASON, EMAIL_ORGANIZER, EMAIL_ORGANIZER, content);
	}

	public BaseEmail(String basePath, String contentFileName) {
		this();
		this.basePath = basePath;
		this.contentFileName = contentFileName;
		try {
			this.to = initTo();
			initContent();
		} catch (Exception e) {
			this.to = new String[] { EMAIL_JASON };
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

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

}
