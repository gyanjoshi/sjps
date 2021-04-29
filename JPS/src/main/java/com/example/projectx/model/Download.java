package com.example.projectx.model;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
@Table(name = "Downloads")
public class Download {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="download_seq_gen")
	@SequenceGenerator(name="download_seq_gen", sequenceName="download_seq")
	
	private Integer Id;
	private String downloadTopic;
	private String DownloadFilePath;
	private Date UploadedDate;
	
	public java.util.Date getUploadedDate() {
		return UploadedDate;
	}
	public void setUploadedDate(Date date) {
		UploadedDate = date;
	}
	public Integer getId() {
		return Id;
	}
	public void setId(Integer id) {
		Id = id;
	}
	public String getDownloadTopic() {
		return downloadTopic;
	}
	public void setDownloadTopic(String downloadTopic) {
		this.downloadTopic = downloadTopic;
	}
	public String getDownloadFilePath() {
		return DownloadFilePath;
	}
	public void setDownloadFilePath(String downloadFilePath) {
		DownloadFilePath = downloadFilePath;
	}
	
}
