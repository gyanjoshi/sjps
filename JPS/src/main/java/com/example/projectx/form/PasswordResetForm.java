/**
 * 
 */
package com.example.projectx.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Gyan Prakash Joshi
 * 
 * Class represents password reset form in the view. It will be used for validation of the form
 *
 */
public class PasswordResetForm {

	@NotNull(message = "email can not be null!!")
    @NotEmpty(message = "email can not be empty!!")
	@Email(message = "email should be in valid format")
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
