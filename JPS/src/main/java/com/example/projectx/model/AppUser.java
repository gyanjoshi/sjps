package com.example.projectx.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
 
@Entity
@Table(name = "App_Users", //
        uniqueConstraints = { //
                @UniqueConstraint(name = "USERNAME_UK", columnNames = "username"),
                @UniqueConstraint(name = "EMAIL_UK", columnNames = "email")})

public class AppUser extends Person implements Reviewer, Serializable 
{
	
	public AppUser() {
		super();
	}
	public AppUser(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;

	@Column(name="username")
	private String userName;
	@Column(name="password")
	private String password;
	@Column(name="role")	
	private String role;

	
	@Column(name="profile_picture")
	private String profilePicture;
	
	@Column(name="enabled")	
	private short enabled;
	
	public String getProfilePicture() {
		return profilePicture;
	}
	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
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
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public short getEnabled() {
		return enabled;
	}
	public void setEnabled(short enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean isAppUser() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public Reviewer getReviewer() {
		// TODO Auto-generated method stub
		return this;
	}
	
	
	
}
