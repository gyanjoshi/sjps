/**
 * 
 */
package com.example.projectx.dto;

import java.util.Date;

/**
 * @author Gyan Prakash Joshi
 * Mar 25, 2021
 *
 */
public class JournalIssueDto {
	
	private Integer journalId;
	private String JournalTopic;
	private Integer journalIssueId;
	
	private String coverImageFileName;
	
	private String VolumeNum;
	private String IssueNum;
	private String year;
	private String month;
	
	
	private Date createdDate;
	private String createdBy;	
	private Date modifiedDate;	
	private String modifiedBy;	
	private Date publishDate;
	private String publishedBy;

	
	private String status;
	
	private long numItems;
	
	
	public JournalIssueDto(Integer journalId, String journalTopic, Integer journalIssueId, String coverImageFileName,
			String volumeNum, String issueNum, String year, String month, 
			Date createdDate, String createdBy,Date modifiedDate, String modifiedBy,Date publishDate, String publishedBy, String status, long items) {
		super();
		this.journalId = journalId;
		this.journalIssueId = journalIssueId;
		JournalTopic = journalTopic;
		this.coverImageFileName = coverImageFileName;
		VolumeNum = volumeNum;
		IssueNum = issueNum;
		this.year = year;
		this.month = month;
		this.createdDate = createdDate;
		this.createdBy = createdBy;
		this.modifiedDate = modifiedDate;
		this.modifiedBy = modifiedBy;
		this.publishDate = publishDate;
		this.publishedBy = publishedBy;
		this.status = status;
		this.numItems  = items;
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
	
	
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Date getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
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
	
	public String getPublishInfo()
	{
		return " Vol-"+VolumeNum+", Issue-"+IssueNum+" "+month+" "+year;
	}
	public long getNumItems() {
		return numItems;
	}
	public void setNumItems(long numItems) {
		this.numItems = numItems;
	}
	@Override
	public String toString() {
		return "JournalIssueDto [journalId=" + journalId + ", JournalTopic=" + JournalTopic + ", journalIssueId="
				+ journalIssueId + ", coverImageFileName=" + coverImageFileName + ", VolumeNum=" + VolumeNum
				+ ", IssueNum=" + IssueNum + ", year=" + year + ", month=" + month + ", createdDate=" + createdDate
				+ ", createdBy=" + createdBy + ", modifiedDate=" + modifiedDate + ", modifiedBy=" + modifiedBy
				+ ", publishDate=" + publishDate + ", publishedBy=" + publishedBy + ", status=" + status + ", numItems="
				+ numItems + "]";
	}
	
	

}
