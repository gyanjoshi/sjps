package com.example.projectx.form;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Email;

import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

public class UserForm {
	

	@NotNull(message = "Full Name can not be null!!")
    @NotEmpty(message = "Full Name can not be empty!!")	
	private String fullName;
	
	private String address1;
	private String address2;
	private String city;
	private String state;


	@Email(message = "Please enter valid email")
	private String email;
	
	@NotNull
    @Size(max = 15, min = 10, message = "Mobile number should be between 10 and 15 digits")
//    @Pattern(regexp = "[7-9][0-9]{9}", message = "Mobile number is invalid!!")
    
	private String phone;
	
	private String country;
	private String title;
	private String profession;
	private String qualification;

	private String userName;

	private String password;

	private String confirmpassword;

	@Email(message = "Please enter valid email")
	private String confirmEmail;
	
	private MultipartFile profilePicture;
	
	public MultipartFile getProfilePicture() {
		return profilePicture;
	}
	public void setProfilePicture(MultipartFile profilePicture) {
		this.profilePicture = profilePicture;
	}
	
	private String role;
	
	
	public String getConfirmEmail() {
		return confirmEmail;
	}
	public void setConfirmEmail(String confirmEmail) {
		this.confirmEmail = confirmEmail;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
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
	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}
	public String getQualification() {
		return qualification;
	}
	public void setQualification(String qualification) {
		this.qualification = qualification;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getConfirmpassword() {
		return confirmpassword;
	}
	public void setConfirmpassword(String confirmpassword) {
		this.confirmpassword = confirmpassword;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	public void validate(BindingResult result)
	{
		if(!email.equals(confirmEmail))
		{
			result.rejectValue("email", "email", "Emails do not match");
			result.rejectValue("confirmEmail", "confirmEmail", "Emails do not match");
			
		}
		if(!password.equals(confirmpassword))
		{
			result.rejectValue("password", "password", "Password do not match");
			result.rejectValue("confirmpassword", "confirmpassword", "Passwords do not match");
		}
			
	}
	

}
