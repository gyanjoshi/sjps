package com.example.projectx.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;

public class PasswordChangeForm {
	
	@NotNull(message = "Current Password should be provided!!")
    @NotEmpty(message = "Current Password should be provided!!")	
	private String currentPassword;
	
	@NotNull(message = "Please type new password")
	@NotEmpty(message= "Please type new password")
	private String password; 
	
	@NotNull(message = "Please retype new password")
	@NotEmpty(message = "Please retype new password")
	private String confirmPassword;
	
	private static final Logger logger=LoggerFactory.getLogger(PasswordChangeForm.class);

	
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	
	public void validate(Errors errors) {
		if (!password.equals(confirmPassword))	
			errors.reject("pwd", "Passwords do not match");		
		
	}
	
	
	

}
