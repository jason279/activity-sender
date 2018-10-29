package com.quest.badminton.activitysender.email;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ActivitySignupEmail extends BaseEmail {

	public ActivitySignupEmail(String basePath) {
		super(basePath, "activitySignupContent.txt");
	}

	@Override
	protected String[] initTo() throws Exception {
		List<String> to = new ArrayList<>();
		BufferedReader reader = Files.newBufferedReader(Paths.get(this.basePath, "activitySignupTo.txt"));
		String line = null;
		while ((line = reader.readLine()) != null) {
			to.add(line);
		}
		reader.close();
		return to.toArray(new String[] {});
	}

}
