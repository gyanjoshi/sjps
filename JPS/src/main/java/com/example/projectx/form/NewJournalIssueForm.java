package com.example.projectx.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

public class NewJournalIssueForm {	
	
	@NotNull(message="Journal should be selected")
	
	private Integer journalId;
	
	@NotNull(message="Issue can not be null")
	@NotEmpty(message="issue can not be empty")
	private String issue;
	
	@NotNull(message="Volume can not be null")
	@NotEmpty(message="Volume can not be empty")
	private String volume;
	
	@NotNull(message="Year can not be null")
	@NotEmpty(message="Year can not be empty")
	private String year;
	
	@NotNull(message="Month can not be null")
	@NotEmpty(message="Month can not be empty")
	private String month;
	
		
	private MultipartFile coverPage;
	
	private Boolean uploadCover;

	public NewJournalIssueForm()
	{
		super();
		this.uploadCover = false;
	}
	
	public String getIssue() {
		return issue;
	}
	public void setIssue(String issue) {
		this.issue = issue;
	}
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	
	public Integer getJournalId() {
		return journalId;
	}
	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}
	public MultipartFile getCoverPage() {
		return coverPage;
	}
	public void setCoverPage(MultipartFile coverPage) {
		this.coverPage = coverPage;
	}
	
	
	
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	
	
	public Boolean getUploadCover() {
		return uploadCover;
	}
	public void setUploadCover(Boolean uploadCover) {
		this.uploadCover = uploadCover;
	}
	public void validate(Errors errors) 
	{
		
	if(this.uploadCover.booleanValue())
	{
		
	
		if (coverPage == null) {
			errors.rejectValue("coverPage", "coverPage", "Cover Page should be uploaded");
			
		}
		else {
			if (coverPage.isEmpty())
				errors.rejectValue("coverPage","coverPage", "Cover Page should be uploaded");
			    
				
		}
	}
	}

}
