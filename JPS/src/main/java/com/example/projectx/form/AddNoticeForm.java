package com.example.projectx.form;

public class AddNoticeForm {
	
	private String NoticeNumber;
	private String NoticeTitle;

	private String noticeText;  //filetext in case if file is not uploaded
	
	
	public String getNoticeNumber() {
		return NoticeNumber;
	}
	public void setNoticeNumber(String noticeNumber) {
		NoticeNumber = noticeNumber;
	}
	public String getNoticeTitle() {
		return NoticeTitle;
	}
	public void setNoticeTitle(String noticeTitle) {
		NoticeTitle = noticeTitle;
	}
	public String getNoticeText() {
		return noticeText;
	}
	public void setNoticeText(String noticeText) {
		this.noticeText = noticeText;
	}
	

}
