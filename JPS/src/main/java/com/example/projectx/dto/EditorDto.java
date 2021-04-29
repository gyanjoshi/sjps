package com.example.projectx.dto;

public class EditorDto {
	
	private String email;
	private String phone;
	private String fullName;
	private Boolean active;
	
	public EditorDto(String email, String phone, String name, Boolean active)
	{
		this.email = email;
		this.phone = phone;
		this.fullName = name;
		this.setActive(active);
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	

}
