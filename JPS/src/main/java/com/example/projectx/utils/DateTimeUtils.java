package com.example.projectx.utils;

import java.time.LocalDateTime;
import java.util.Date;

import org.ocpsoft.prettytime.PrettyTime;

public class DateTimeUtils {

	public static void main(String[] args) {
		PrettyTime p = new PrettyTime();

		// Traditional Date API:
		System.out.println(p.format(new Date()));
		//prints: “moments from now”

		// JDK 8 DateTime API:
		System.out.println(p.format(LocalDateTime.now().minusSeconds(500)));
		//prints: “moments ago”


		System.out.println(p.format(new Date(System.currentTimeMillis() + 1000*60*10)));
		//prints: “10 minutes from now”

	}
	
	public static String getPrettyDate(Date date) {
	    PrettyTime pretty = new PrettyTime();
	    return date != null ? pretty.format(date) : "Unknown Date"; //or whatever implementation
	}

}
