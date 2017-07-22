package com.gk.erp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TimeUtils {
	
	private static SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public static String convert2String(long time){
		return sFormat.format(new Date(time));
	}
	
	public static long convert2Long(String time){
		long ans = 0;
		try {
			ans = sFormat.parse(time).getTime();
		} catch (ParseException e) {
		}
		return ans;
	}

	public static long convert2LongWithError(String time) throws ParseException {
		return sFormat.parse(time).getTime();
	}
	
}
