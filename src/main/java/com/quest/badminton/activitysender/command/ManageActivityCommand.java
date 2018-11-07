package com.quest.badminton.activitysender.command;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.quest.badminton.activitysender.service.BadmintonEmailService;
import com.quest.badminton.activitysender.service.ClubWebSiteService;

@ShellComponent("activity")
public class ManageActivityCommand {
	@Autowired
	private BadmintonEmailService emailService;
	@Autowired
	private ClubWebSiteService webSiteService;

	@ShellMethod(key = "end", value = "close activity and send result email")
	public void endActivity() {
		emailService.sendAcitvityResultEmailTask();
	}

	@ShellMethod(key = "list", value = "list signup recipients")
	public String getSignupRecipients() {
		String cookie = webSiteService.loginAsAdmin();
		String viewId = webSiteService.getTopActivityViewId(cookie);
		String[] recipients = webSiteService.getSignupRecipients(cookie, viewId);
		return Arrays.toString(recipients);
	}

	@ShellMethod(key = "start", value = "create activity and send signup email")
	public void startActivity() {
		emailService.sendAcitvitySignupEmailTask();
	}
}
