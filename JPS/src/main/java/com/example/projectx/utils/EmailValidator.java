package com.example.projectx.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Java program to validate email in Java
public class EmailValidator
{
	private static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
	
	private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

	private static final Logger logger = LoggerFactory.getLogger(EmailValidator.class);
	
	public static boolean emailValidator(String email) {

		if (email == null) {
			
			logger.error("Email is null");
			return false;
		}
		if(email.length() == 0) {
			logger.error("Email is empty");
			return false;
			
		}		

		Matcher matcher = EMAIL_PATTERN.matcher(email);
		boolean isMatch = matcher.matches();
		
		if (!isMatch) {
			logger.warn("Email {} is not valid.", email);
		}
		else
			logger.info("Email {} is valid.", email);
		
		return isMatch;
	}

	
}
