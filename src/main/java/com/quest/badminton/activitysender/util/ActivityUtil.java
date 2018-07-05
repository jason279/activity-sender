package com.quest.badminton.activitysender.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ActivityUtil {
	public static Set<String> getSignupResultEmails() {
		RestTemplate template = new RestTemplate();

		// login
		String userName = "Jason Tian";
		String password = "jason123";
		Map<String, String> params = new HashMap<>();
		params.put("username", userName);
		params.put("password", password);
		ResponseEntity<String> response = template.postForEntity("http://clubsmanager.prod.quest.corp/users/login",
				params, String.class);
		String cookie = response.getHeaders().get("Set-Cookie").get(0);
		cookie = cookie.substring(0, cookie.indexOf(";"));

		// get activity viewId
		if (cookie == null) {
			throw new RuntimeException("login to clubsmanager failed.");
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", cookie);
		HttpEntity<String> entity = new HttpEntity<>(headers);
		response = template.exchange("http://clubsmanager.prod.quest.corp/activities/index/1", HttpMethod.GET, entity,
				String.class);
		String content = response.getBody();
		int startIndex = content.indexOf("/activities/view") + "/activities/view".length() + 1;
		int lastIndex = content.indexOf("\">", startIndex);
		String viewId = content.substring(startIndex, lastIndex);

		// visit activity view
		if (viewId == null) {
			throw new RuntimeException("Could not get the viewId.");
		}
		response = template.exchange("http://clubsmanager.prod.quest.corp/activities/view/" + viewId, HttpMethod.GET,
				entity, String.class);
		content = response.getBody();

		if (content.indexOf("接受报名中") < 0) {
			throw new RuntimeException("the state of activiy for viewId " + viewId + " is not activate.");
		}

		// analyze email
		Set<String> emails = new HashSet<>();
		Pattern p = Pattern.compile("<td>(.+@.+)</td>");
		Matcher matcher = p.matcher(content);
		while (matcher.find()) {
			emails.add(matcher.group().replaceAll("</?td>", ""));
		}

		return emails;
	}
}
