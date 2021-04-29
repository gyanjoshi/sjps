package com.example.projectx.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


public class AuthorRegistrationForm 
{
	
	@NotNull(message = "email can not be null!!")
    @NotEmpty(message = "email can not be empty!!")
	@Email(message = "email should be in valid format")
	private String email;
	
	@NotNull(message = "Full name can not be null!!")
    @NotEmpty(message = "Full name can not be empty!!")
	private String fullName;
	
	@NotNull(message = "username can not be null!!")
    @NotEmpty(message = "username can not be empty!!")	
	private String username;
	
	@NotNull(message = "password can not be null!!")
    @NotEmpty(message = "password can not be empty!!")	
	private String password;
	
	@NotNull(message = "repeat password can not be null!!")
    @NotEmpty(message = "repeat password can not be empty!!")	
	private String cpassword;

	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCpassword() {
		return cpassword;
	}

	public void setCpassword(String cpassword) {
		this.cpassword = cpassword;
	}
}
