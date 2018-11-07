package com.quest.badminton.activitysender.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ActivityUtils {
	public static LocalDate getActivityDate() {
		return LocalDate.now().with(DayOfWeek.FRIDAY);
	}
	
	public static String getActivityDateString(String pattern) {
		LocalDate date = getActivityDate();
		return date.format(DateTimeFormatter.ofPattern(pattern));
	}
}
