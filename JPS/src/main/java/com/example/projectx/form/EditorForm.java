package com.example.projectx.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.validation.Errors;

public class EditorForm extends EditorProfileForm {
	
	@NotNull(message = "email can not be null!!")
    @NotEmpty(message = "email can not be empty!!")
	@Email(message = "email should be in valid format")
	private String email;
	
	@NotNull(message = "email can not be null!!")
    @NotEmpty(message = "email can not be empty!!")
	@Email(message = "email should be in valid format")
	private String confirmEmail;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getConfirmEmail() {
		return confirmEmail;
	}
	public void setConfirmEmail(String confirmEmail) {
		this.confirmEmail = confirmEmail;
	}
	
	public void validate(Errors errors) 
	{
		if (!email.equals(confirmEmail))
		{
			errors.rejectValue("confirmEmail", "confirmEmail", "Email do not match");
			errors.rejectValue("email", "email", "Email do not match");
		}	
		
	}
	
	
	
	

}
