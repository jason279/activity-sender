package com.quest.badminton.activitysender.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.quest.badminton.activitysender.util.ServerConstants;

@Service
public class ClubWebSiteService {

	@Autowired
	private RestTemplate restTemplate;

	public String loginAsAdmin() {
		return login("Admin", "admin123");
	}

	public String login(String userName, String password) {
		Map<String, String> params = new HashMap<>();
		params.put("username", userName);
		params.put("password", password);
		ResponseEntity<String> response = restTemplate.postForEntity(ServerConstants.CLUB_WEBSITE_LOGIN_URL, params,
				String.class);
		List<String> cookieHeaders = response.getHeaders().get("Set-Cookie");
		if (cookieHeaders == null || cookieHeaders.size() == 0) {
			throw new RuntimeException("login to clubsmanager failed, no cookie header can be found.");
		}

		String cookie = cookieHeaders.get(0);
		// format is:CAKEPHP=pflu62j9491ao3g70to6grr6p6; path=/; HttpOnly
		cookie = cookie.substring(0, cookie.indexOf(";"));
		if (cookie == null) {
			throw new RuntimeException("login to clubsmanager failed, no cookie can be found.");
		}
		return cookie;
	}

	public String visit(String url, HttpMethod method, String cookie, Object... uriVariables) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", cookie);
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url, method, entity, String.class, uriVariables);
		return response.getBody();
	}

	public String getViewId(String content) {
		int startIndex = content.indexOf("/activities/view") + "/activities/view".length() + 1;
		int lastIndex = content.indexOf("\">", startIndex);
		String viewId = content.substring(startIndex, lastIndex);
		if (viewId == null) {
			throw new RuntimeException("Could not get the viewId.");
		}
		return viewId;
	}

	public String[] getSignupResultEmails(String content) {
		if (content.indexOf("接受报名中") < 0) {
			throw new RuntimeException("the state of activiy is not activate.");
		}

		// analyze email
		Set<String> emails = new HashSet<>();
		Pattern p = Pattern.compile("<td>(.+@.+)</td>");
		Matcher matcher = p.matcher(content);
		while (matcher.find()) {
			emails.add(matcher.group().replaceAll("</?td>", ""));
		}

		return emails.toArray(new String[emails.size()]);
	}

	public boolean addActivity(String cookie, LocalDate acitivityDate) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", cookie);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("club_id", "1");

		body.add("activity_date[year]", Integer.toString(acitivityDate.getYear()));
		body.add("activity_date[month]", Integer.toString(acitivityDate.getMonthValue()));
		body.add("activity_date[day]", Integer.toString(acitivityDate.getDayOfMonth()));
		String title = "周五羽毛球活动(" + acitivityDate.getMonthValue() + "月" + acitivityDate.getDayOfMonth() + "日)";
		body.add("tittle", title);
		body.add("description", title);
		body.add("max_amount", "24");
		// body.add("min_amount", "1");
		body.add("allow_sign", "1");
		body.add("multiple_sign", "0");
		body.add("status", "starting");
		body.add("end_date[year]", Integer.toString(acitivityDate.getYear()));
		body.add("end_date[month]", Integer.toString(acitivityDate.getMonthValue()));
		body.add("end_date[day]", Integer.toString(acitivityDate.getDayOfMonth()));
		body.add("end_date[hour]", "10");
		body.add("end_date[minute]", "30");

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(body, headers);

		ResponseEntity<String> response = restTemplate
				.postForEntity(ServerConstants.CLUB_WEBSITE_BADMINTON_ACTIVITY_ADD_URL, entity, String.class);
		return response.getStatusCodeValue() == 302;
	}
}
