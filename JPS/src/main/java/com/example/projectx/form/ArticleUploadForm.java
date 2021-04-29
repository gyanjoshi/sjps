package com.example.projectx.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

//import javax.validation.constraints.

import org.springframework.web.multipart.MultipartFile;

public class ArticleUploadForm {
	
	@NotNull(message = "Topic can not be null!!")
    @NotEmpty(message = "Topic can not be empty!!")	
	private String topic;
	
	@NotNull(message = "Article abstract can not be null!!")
	@NotEmpty(message = "Article abstract can not be empty!!")
	private String articleAbstract; 

	
    private MultipartFile fileData;
    
	
    private Long[] categories;
    
    private String otherauthors;
    
    private boolean isUpdate = false;
    
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getArticleAbstract() {
		return articleAbstract;
	}
	public void setArticleAbstract(String articleAbstract) {
		this.articleAbstract = articleAbstract;
	}
	public MultipartFile getFileData() {
		return fileData;
	}
	public void setFileData(MultipartFile file) {
		this.fileData = file;
	}
	public Long[] getCategories() {
		return categories;
	}
	public void setCategories(Long[] categories) {
		this.categories = categories;
	}
	public String getOtherauthors() {
		return otherauthors;
	}
	public void setOtherauthors(String otherauthors) {
		this.otherauthors = otherauthors;
	}
	public boolean isUpdate() {
		return isUpdate;
	}
	public void setUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}
	
	
}
