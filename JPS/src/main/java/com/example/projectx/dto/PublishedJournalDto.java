package com.example.projectx.dto;

import java.util.Date;

public class PublishedJournalDto {
	
	private Integer journalId;
	private Integer journalIssueId;
	private String JournalTopic;
	private String coverImageFileName;
	private String VolumeNum;
	private String IssueNum;
	private String year;
	private String month;
	
	private Date publishDate;
	private String publishedBy;
	private String status;
	
	public PublishedJournalDto()
	{
		
	}
	public PublishedJournalDto(int journalId, int journalIssueId, String journalTopic,
			String coverImageFileName, String volumeNum, String issueNum, String year, String month,
			java.util.Date publishDate, String publishedBy, String status) {

		this.journalId = journalId;
		this.journalIssueId = journalIssueId;
		JournalTopic = journalTopic;
		this.coverImageFileName = coverImageFileName;
		VolumeNum = volumeNum;
		IssueNum = issueNum;
		this.year = year;
		this.month = month;
		
		this.publishDate = publishDate;
		this.publishedBy = publishedBy;
		this.status = status;
	}
	public Integer getJournalId() {
		return journalId;
	}
	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}
	public Integer getJournalIssueId() {
		return journalIssueId;
	}
	public void setJournalIssueId(Integer journalIssueId) {
		this.journalIssueId = journalIssueId;
	}
	public String getJournalTopic() {
		return JournalTopic;
	}
	public void setJournalTopic(String journalTopic) {
		JournalTopic = journalTopic;
	}
	public String getCoverImageFileName() {
		return coverImageFileName;
	}
	public void setCoverImageFileName(String coverImageFileName) {
		this.coverImageFileName = coverImageFileName;
	}
	public String getVolumeNum() {
		return VolumeNum;
	}
	public void setVolumeNum(String volumeNum) {
		VolumeNum = volumeNum;
	}
	public String getIssueNum() {
		return IssueNum;
	}
	public void setIssueNum(String issueNum) {
		IssueNum = issueNum;
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
	
	
	public Date getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}
	public String getPublishedBy() {
		return publishedBy;
	}
	public void setPublishedBy(String publishedBy) {
		this.publishedBy = publishedBy;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
