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

import com.quest.badminton.activitysender.util.ActivityUtils;
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

	public String getTopActivityViewId(String cookie) {
		String content = visit(ServerConstants.CLUB_WEBSITE_BADMINTON_ACTIVITY_URL, HttpMethod.GET, cookie);

		int startIndex = content.indexOf("/activities/view") + "/activities/view".length() + 1;
		int lastIndex = content.indexOf("\">", startIndex);
		String viewId = content.substring(startIndex, lastIndex);
		if (viewId == null) {
			throw new RuntimeException("Could not get the viewId.");
		}
		return viewId;
	}

	public String getTopActivityViewId() {
		return getTopActivityViewId(loginAsAdmin());
	}

	public String[] getSignupRecipients(String cookie, String viewId) {
		String content = visit(ServerConstants.CLUB_WEBSITE_BADMINTON_VIEW_URL, HttpMethod.GET, cookie, viewId);

		String activityDateStr = ActivityUtils.getActivityDateString("MM/d/yy");
		if (content.indexOf(activityDateStr) < 0) {
			throw new RuntimeException("Not the target activity " + activityDateStr);
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

	public boolean addActivity() {
		String cookie = loginAsAdmin();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", cookie);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> body = getGeneralBody();
		body.add("status", ActivityStatus.starting.name());

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(body, headers);

		ResponseEntity<String> response = restTemplate
				.postForEntity(ServerConstants.CLUB_WEBSITE_BADMINTON_ACTIVITY_ADD_URL, entity, String.class);
		return response.getStatusCodeValue() == 302;
	}

	private MultiValueMap<String, String> getGeneralBody() {
		LocalDate activityDate = ActivityUtils.getActivityDate();
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("club_id", "1");

		body.add("activity_date[year]", Integer.toString(activityDate.getYear()));
		body.add("activity_date[month]", Integer.toString(activityDate.getMonthValue()));
		body.add("activity_date[day]", Integer.toString(activityDate.getDayOfMonth()));
		String title = "周五羽毛球活动(" + activityDate.getMonthValue() + "月" + activityDate.getDayOfMonth() + "日)";
		body.add("tittle", title);
		body.add("description", title);
		body.add("max_amount", "24");
		// body.add("min_amount", "1");
		body.add("allow_sign", "1");
		body.add("multiple_sign", "0");

		body.add("end_date[year]", Integer.toString(activityDate.getYear()));
		body.add("end_date[month]", Integer.toString(activityDate.getMonthValue()));
		body.add("end_date[day]", Integer.toString(activityDate.minusDays(1).getDayOfMonth()));
		body.add("end_date[hour]", "16");
		body.add("end_date[minute]", "00");
		return body;
	}

	public boolean closeActivity(String cookie, String viewId) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", cookie);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> body = getGeneralBody();
		body.add("status", ActivityStatus.end.name());

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(body, headers);

		ResponseEntity<String> response = restTemplate
				.postForEntity(ServerConstants.CLUB_WEBSITE_ACTIVITY_EDIT + viewId, entity, String.class);
		return response.getStatusCodeValue() == 302;
	}

	public boolean closeActivity(String viewId) {
		return closeActivity(loginAsAdmin(), viewId);
	}
}
