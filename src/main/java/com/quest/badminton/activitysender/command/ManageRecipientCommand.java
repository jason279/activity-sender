package com.quest.badminton.activitysender.command;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.quest.badminton.activitysender.service.ManageRecipientService;

@ShellComponent("email")
public class ManageRecipientCommand {
	@Autowired
	private ManageRecipientService manageRecipientService;

	@ShellMethod(key = "add", value = "add recipient")
	public boolean add(String email) throws IOException {
		return manageRecipientService.add(email);
	}

	@ShellMethod(key = "del", value = "delete recipient")
	public boolean delete(String email) {
		return manageRecipientService.delete(email);
	}
}
