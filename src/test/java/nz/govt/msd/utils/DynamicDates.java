package nz.govt.msd.utils;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.time.FastDateFormat;

public class DynamicDates {
	private static final FastDateFormat DMY_FORMAT = FastDateFormat.getInstance("dd/MM/yyyy");
	static TimeZone tz = TimeZone.getDefault();
	
	private DynamicDates() { }
	
	public static String dynamicDateString(String dateRequired) throws Exception {
		Calendar calendar = Calendar.getInstance(tz);
		String dateToUse;
		
		if (Pattern.matches("(?i)^first[ _-]?(?:|day)[ _-]?of.*", dateRequired)) {
			calendar.add(Calendar.DATE, -(calendar.get(Calendar.DAY_OF_MONTH) - 1));
			dateToUse = DMY_FORMAT.format(calendar.getTime()).toString();
			
		} else if (Pattern.matches("(?i)^day[ _-]?after.*", dateRequired)) {
			calendar.add(Calendar.DATE, 1);
			dateToUse = DMY_FORMAT.format(calendar.getTime()).toString();
			
		} else if (Pattern.matches("(?i)^(?:end|last)[ _-]?(?:|day)[ _-]?of[ _-]?last[ _-]?month$", dateRequired)) {
			calendar.add(Calendar.DATE, -(calendar.get(Calendar.DAY_OF_MONTH)));
			dateToUse = DMY_FORMAT.format(calendar.getTime()).toString();
			
		} else if (Pattern.matches("(?i).*last[ _-]?month$", dateRequired)) {
			calendar.add(Calendar.MONTH, -1);
			dateToUse = DMY_FORMAT.format(calendar.getTime()).toString();
			
		} else if (Pattern.matches("(?i).*this[ _-]?month$", dateRequired)) {
			calendar.add(Calendar.MONTH, 0);
			dateToUse = DMY_FORMAT.format(calendar.getTime()).toString();

		} else if (Pattern.matches("(?i)^(?:end|last)[ _-]?(?:|day)[ _-]?of[ _-]?([0-9]+)[ _-]?month[s]?[ _-]?ago$", dateRequired)) {
			String resultMonths = "0";
			Matcher m2 = Pattern.compile("(?<mnth>[0-9]+)").matcher(dateRequired);
			while (m2.find()) {
				resultMonths = m2.group();
			}
			int monthsAgo = Integer.parseInt(resultMonths) - 1;
			calendar.add(Calendar.DATE, -(calendar.get(Calendar.DAY_OF_MONTH)));
			calendar.add(Calendar.MONTH, -monthsAgo);
			dateToUse = DMY_FORMAT.format(calendar.getTime()).toString();

		} else if (Pattern.matches("(?i).*([0-9]+)[ _-]?month[s]?[ _-]?ago$", dateRequired)) {
			String resultMonths = "0";
			Matcher m1 = Pattern.compile("(?<mnth>[0-9]+)").matcher(dateRequired);
			while (m1.find()) {
				resultMonths = m1.group();
			}
			int monthsAgo = Integer.parseInt(resultMonths);
			calendar.add(Calendar.MONTH, -monthsAgo);
			dateToUse = DMY_FORMAT.format(calendar.getTime()).toString();

			// days ago (5daysAgo)
		} else if (Pattern.matches("([0-9]+)[ _-]?([D|d]ay[s]?)[ _-]?([A|a]go$)", dateRequired)) {
			String resultDays = "0";
			Matcher m1 = Pattern.compile("([0-9]+)[ _-]?[D|d]ay[s]?[ _-]?[A|a]go$").matcher(dateRequired);
			if (m1.find()) {
				resultDays = m1.group(1);
			}
			int daysAgo = Integer.parseInt(resultDays);
			calendar.add(Calendar.DATE, -daysAgo);
			dateToUse = DMY_FORMAT.format(calendar.getTime()).toString();

		} else if (Pattern.matches("(?i)today$", dateRequired)) { 
			dateToUse = DMY_FORMAT.format(calendar.getTime()).toString();
			
		} else if (Pattern.matches("(?i)tomorrow$", dateRequired)) { 
			calendar.add(Calendar.DATE, 1);
			dateToUse = DMY_FORMAT.format(calendar.getTime()).toString();
			
		} else if (Pattern.matches("(?i)yesterday$", dateRequired)) { 
			calendar.add(Calendar.DATE, -1);
			dateToUse = DMY_FORMAT.format(calendar.getTime()).toString();
			
		} else {
			dateToUse = dateRequired;
			
		}
		return dateToUse;
	}
	
	
	public static String date90DaysAgoString() {
		Calendar calendar = Calendar.getInstance(tz);
		calendar.add(Calendar.DATE, -90);
		
		return DMY_FORMAT.format(calendar.getTime()).toString();
	}
	
	public static String date92DaysAgoString() {
		Calendar calendar = Calendar.getInstance(tz);
		calendar.add(Calendar.DATE, -92);
	
		return DMY_FORMAT.format(calendar.getTime()).toString();
	}

	public static String date92DaysAgoString(String pattern) {
		Calendar calendar = Calendar.getInstance(tz);
		calendar.add(Calendar.DATE, -92);
	
		return FastDateFormat.getInstance(pattern).format(calendar.getTime()).toString();
	}

	public static String dateTodayString() {
		Calendar calendar = Calendar.getInstance(tz);
		
		return DMY_FORMAT.format(calendar.getTime()).toString();
	}
	
	public static String dateTodayString(String pattern) {
		Calendar calendar = Calendar.getInstance(tz);
		return FastDateFormat.getInstance(pattern).format(calendar.getTime()).toString();				
	}
	
	public static String dateXXDaysAgoString(int numOfDays) {
		Calendar calendar = Calendar.getInstance(tz);
		calendar.add(Calendar.DATE, -numOfDays);
		
		return DMY_FORMAT.format(calendar.getTime()).toString();
	}
	
	public static String dateXXDaysAheadString(int numOfDays) {
		Calendar calendar = Calendar.getInstance(tz);
		calendar.add(Calendar.DATE, numOfDays);
		
		return DMY_FORMAT.format(calendar.getTime()).toString();
	}	
}
