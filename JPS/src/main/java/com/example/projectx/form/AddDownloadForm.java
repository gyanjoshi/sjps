package com.example.projectx.form;

import org.springframework.web.multipart.MultipartFile;

public class AddDownloadForm {
	
	private String downloadTopic;
	private MultipartFile DownloadFilePath;
	
	
	public String getDownloadTopic() {
		return downloadTopic;
	}
	public void setDownloadTopic(String downloadTopic) {
		this.downloadTopic = downloadTopic;
	}
	public MultipartFile getDownloadFilePath() {
		return DownloadFilePath;
	}
	public void setDownloadFilePath(MultipartFile downloadFilePath) {
		DownloadFilePath = downloadFilePath;
	}
	
	
	
	

}
