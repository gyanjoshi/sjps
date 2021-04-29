package com.example.projectx.dto;

public class UserDto {
	
	private String userName;
	private String email;
	private String password;
	
	public UserDto(String username, String email, String password)
	{
		this.userName = username;
		this.email = email;
		this.password = password;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	

}
