/**
 * 
 */
package com.example.projectx.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Gyan Prakash Joshi
 * Mar 21, 2021
 * Represents Article Approve form for validation purpose
 *
 */
public class ApproveArticleForm {
	
	private MultipartFile approvalCertificate;
    
	@NotNull(message = "Email message can not be null!!")
	@NotEmpty(message = "Email message can not be empty!!")
    private String emailMessage;
    
    private Integer article_id;
    
    private String topic;

	public MultipartFile getApprovalCertificate() {
		return approvalCertificate;
	}

	public void setApprovalCertificate(MultipartFile approvalCertificate) {
		this.approvalCertificate = approvalCertificate;
	}

	public String getEmailMessage() {
		return emailMessage;
	}

	public void setEmailMessage(String emailMessage) {
		this.emailMessage = emailMessage;
	}

	public Integer getArticle_id() {
		return article_id;
	}

	public void setArticle_id(Integer article_id) {
		this.article_id = article_id;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
    
	public void validate(Errors errors) {
		
		if (approvalCertificate == null) {
			errors.rejectValue("approvalCertificate", "approvalCertificate", "Approval Certificate should be uploaded");
			
		}
		else {
			if (approvalCertificate.isEmpty())
				errors.rejectValue("approvalCertificate","approvalCertificate", "Approval Certificate should be uploaded");
			    
				
		}		
	}

}
