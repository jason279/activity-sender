package com.quest.badminton.activitysender.email;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ActivitySignupEmail extends BaseEmail {

	public ActivitySignupEmail(String basePath) {
		super(basePath);
	}

	@Override
	protected String[] initTo() throws Exception {
		List<String> to = new ArrayList<>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(this.basePath + "activitySignupTo.txt"), "UTF-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				to.add(line);
			}
			return to.toArray(new String[] {});
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	@Override
	protected String getContentFileName() {
		return "activitySignupContent.txt";
	}

	@Override
	protected int getDayOffset() {
		return 2;
	}

}