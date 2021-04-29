package com.example.projectx.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class EditorProfileForm {
	
	@NotNull(message = "Full Name can not be null!!")
    @NotEmpty(message = "Full Name can not be empty!!")	
	private String fullName;
	
	@NotNull(message = "Affiliation should be entered !!")
    @NotEmpty(message = "Affiliation can not be empty!!")	
	private String affiliation;	
	
	private String address;
	
	@NotNull(message="Phone number should be provided")
	@NotEmpty(message="Phone number should not be empty")
    @Size(max = 15, min = 10, message = "Mobile number should be between 10 and 15 digits") 
	private String phone;
	
	private String city;
	private String state;	
	private String country;
	
	@Size(max = 500, message = "About me should be less than 500 characters") 
	private String aboutme;
	
	private String title;
	private String qualification;
	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public String getAffiliation() {
		return affiliation;
	}
	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getQualification() {
		return qualification;
	}
	public void setQualification(String qualification) {
		this.qualification = qualification;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAboutme() {
		return aboutme;
	}
	public void setAboutme(String aboutme) {
		this.aboutme = aboutme;
	}

}
