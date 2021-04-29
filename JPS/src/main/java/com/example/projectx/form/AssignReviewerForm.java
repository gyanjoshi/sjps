package com.example.projectx.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

public class AssignReviewerForm {
	
	
	private MultipartFile evaluationSheet;
    
	@NotNull(message = "Reviewer should be selected. !!")
	@NotEmpty(message = "Reviewer should be selected. !!")
    private Long[] reviewers;
    
    @NotNull(message = "Email message can not be null!!")
	@NotEmpty(message = "Email message can not be empty!!")
    private String emailMessage;
    
    private Integer article_id;
    
    private String topic;
    
    private String authorUserName;
    
    public MultipartFile getEvaluationSheet() {
		return evaluationSheet;
	}

	public void setEvaluationSheet(MultipartFile evaluationSheet) {
		this.evaluationSheet = evaluationSheet;
	}

	public Long[] getReviewers() {
		return reviewers;
	}

	public void setReviewers(Long[] reviewers2) {
		this.reviewers = reviewers2;
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

	public String getAuthorUserName() {
		return authorUserName;
	}

	public void setAuthorUserName(String authorUserName) {
		this.authorUserName = authorUserName;
	}

	public void validate(Errors errors) {
		
		if (evaluationSheet == null) {
			errors.rejectValue("evaluationSheet", "evaluationSheet", "Evaluation sheet should be uploaded");
			
		}
		else {
			if (evaluationSheet.isEmpty())
				errors.rejectValue("evaluationSheet","evaluationSheet", "Evaluation sheet should be uploaded");
			    
				
		}		
	}
    

}
