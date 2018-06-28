package com.quest.badminton.activitysender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ActivitySenderApplication {

	@Bean
	public TemplateEmail email() {
		return new TemplateEmail();
	}

	public static void main(String[] args) {
		SpringApplication.run(ActivitySenderApplication.class, args);
	}
}
