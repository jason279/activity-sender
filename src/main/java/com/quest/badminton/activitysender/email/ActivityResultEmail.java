package com.quest.badminton.activitysender.email;

import java.util.Set;

import com.quest.badminton.activitysender.util.ActivityUtil;

public class ActivityResultEmail extends BaseEmail {
	public ActivityResultEmail(String basePath) {
		super(basePath);
	}

	@Override
	protected String getContentFileName() {
		return "activityResultContent.txt";
	}

	@Override
	protected String[] initTo() throws Exception {
		Set<String> emails = ActivityUtil.getSignupResultEmails();
		return emails.toArray(new String[emails.size()]);
	}

	@Override
	protected int getDayOffset() {
		return 0;
	}

}
