package com.quest.badminton.activitysender.email;

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
		return new String[] { "Jason.Tian@quest.com" };
	}

	@Override
	protected int getDayOffset() {
		return 0;
	}

}
