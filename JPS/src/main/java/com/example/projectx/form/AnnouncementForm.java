/**
 * 
 */
package com.example.projectx.form;

import java.util.Date;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author Gyan Prakash Joshi
 * Apr 9, 2021
 *
 */
public class AnnouncementForm {
	
	@NotNull(message="Title can not be null")
	@NotEmpty(message="Title can not be empty")
	private String title;
	
	@NotNull(message="Short Description can not be null")
	@NotEmpty(message="Short Description can not be empty")
	private String shortDescription;
	
	@NotNull(message="Full Text Description can not be null")
	@NotEmpty(message="Full Text Description can not be empty")
	private String fullDescription;
	
	@DateTimeFormat(pattern = "MM/dd/yyyy")
	@Future(message="You must specify future date")
	private Date expiryDate;
	
	@NotNull(message="Announce Type should be provided")
	private Long announcementTypeid;
	
	private Boolean hasExpiryDate;
	
	private Boolean sendEmail;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getFullDescription() {
		return fullDescription;
	}

	public void setFullDescription(String fullDescription) {
		this.fullDescription = fullDescription;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Long getAnnouncementTypeid() {
		return announcementTypeid;
	}

	public void setAnnouncementTypeid(Long announcementTypeid) {
		this.announcementTypeid = announcementTypeid;
	}

	public Boolean getHasExpiryDate() {
		return hasExpiryDate;
	}

	public void setHasExpiryDate(Boolean hasExpiryDate) {
		this.hasExpiryDate = hasExpiryDate;
	}

	public Boolean getSendEmail() {
		return sendEmail;
	}

	public void setSendEmail(Boolean sendEmail) {
		this.sendEmail = sendEmail;
	}
	
	
	
	

}
