package com.example.projectx.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity

@Table(name = "Editors", 
uniqueConstraints = {       
        @UniqueConstraint(name = "EMAIL_UK", columnNames = "email")})


public class Editor extends Person implements Serializable, Reviewer  {
	
	private Boolean active;
	
	public Editor(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public Editor() {
		// TODO Auto-generated constructor stub
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	@Column(name="profile_picture")
	private String profilePicture;
	
	public Long getId() {
		return super.id;
	}

	public void setId(Long id) {
		super.id = id;
	}

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	@Override
	public boolean isAppUser() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Reviewer getReviewer() {
		// TODO Auto-generated method stub
		return this;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

}
