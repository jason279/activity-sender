package com.quest.badminton.activitysender.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ManageRecipientService {
	private static final Pattern QUEST_EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@quest.com$");

	@Value("${badminton.app.base.path}")
	private String basePath;

	private Path recipientFile;
	private Path tempFile;

	@PostConstruct
	private void initPaths() {
		this.recipientFile = FileSystems.getDefault().getPath(basePath, "activitySignupTo.txt");
		this.tempFile = FileSystems.getDefault().getPath(basePath, "tempSignupTo.txt");
	}

	public boolean add(String email) {
		if (!QUEST_EMAIL_PATTERN.matcher(email).matches()) {
			log.warn("wrong format of quest email {}.", email);
			return false;
		}
		try {
			BufferedWriter writer = Files.newBufferedWriter(recipientFile, StandardOpenOption.APPEND);
			writer.newLine();
			writer.write(email);
			writer.close();
			log.info("successfully add {} to recipient file.", email);
			return true;
		} catch (IOException e) {
			log.error("failed add {} to recipient file.", email, e);
			return false;
		}
	}

	public boolean delete(String email) {
		try {
			BufferedReader reader = Files.newBufferedReader(recipientFile);
			BufferedWriter writer = Files.newBufferedWriter(tempFile);
			boolean isExist = false;
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!email.equalsIgnoreCase(line)) {
					writer.write(line + System.lineSeparator());
				} else {
					isExist = true;
				}
			}
			reader.close();
			writer.close();

			if (isExist) {
				Files.copy(tempFile, recipientFile, StandardCopyOption.REPLACE_EXISTING);
				log.info("successfully remove email {}.", email);
			} else {
				log.info("email {} is not exist.", email);
			}
			return true;
		} catch (IOException e) {
			log.error("failed remove email {} .", email, e);
			return false;
		}
	}

}
