package com.quest.badminton.activitysender.email;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActivityResultEmail extends BaseEmail {

	public ActivityResultEmail(String basePath, String contentFileName) {
		super(basePath, contentFileName);
	}
}
