package com.example.projectx.form;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class AuthorValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		
		return AuthorRegistrationForm.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		
		AuthorRegistrationForm arf = (AuthorRegistrationForm) target;
		
		String pwd = arf.getPassword();
		String cpwd = arf.getCpassword();
		
		if(!pwd.equals(cpwd))
			errors.reject("pwd", "Passwords do not match");
		
		
	}

}
