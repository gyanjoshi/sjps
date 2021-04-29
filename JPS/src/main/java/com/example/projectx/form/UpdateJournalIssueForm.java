/**
 * 
 */
package com.example.projectx.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Gyan Prakash Joshi
 * Mar 30, 2021
 *
 */
public class UpdateJournalIssueForm {
	
	@NotNull(message="Journal Issue should not be null")
	
	private Integer journalIssueId;
	
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

	public Integer getJournalIssueId() {
		return journalIssueId;
	}

	public void setJournalIssueId(Integer journalIssueId) {
		this.journalIssueId = journalIssueId;
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
	
	

}
