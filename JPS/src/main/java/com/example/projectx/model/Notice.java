package com.example.projectx.model;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.Table;

import org.hibernate.annotations.Type;



@Entity
@Table(name = "Notices")
public class Notice {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer Id;
	private String  NoticeNumber;
	private String NoticeTitle;
	private String NoticeFileName;
	@Type(type="text")
	private String noticeText;
	private Date uploadedDate;
	public Date getUploadedDate() {
		return uploadedDate;
	}
	public void setUploadedDate(Date uploadedDate) {
		this.uploadedDate = uploadedDate;
	}
	public Integer getId() {
		return Id;
	}
	public void setId(Integer id) {
		Id = id;
	}
	
	public String getNoticeTitle() {
		return NoticeTitle;
	}
	public void setNoticeTitle(String noticeTitle) {
		NoticeTitle = noticeTitle;
	}
	public String getNoticeFileName() {
		return NoticeFileName;
	}
	public void setNoticeFileName(String noticeFileName) {
		NoticeFileName = noticeFileName;
	}
	public String getNoticeText() {
		return noticeText;
	}
	public void setNoticeText(String noticeText) {
		this.noticeText = noticeText;
	}
	public String getNoticeNumber() {
		return NoticeNumber;
	}
	public void setNoticeNumber(String noticeNumber) {
		NoticeNumber = noticeNumber;
	}
	
	
}
