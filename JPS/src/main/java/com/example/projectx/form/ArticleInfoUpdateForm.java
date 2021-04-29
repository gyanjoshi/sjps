/**
 * 
 */
package com.example.projectx.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Gyan Prakash Joshi
 * Apr 4, 2021
 *
 */
public class ArticleInfoUpdateForm {

	private int articleId;
	
	@NotNull(message = "Topic can not be null!!")
    @NotEmpty(message = "Topic can not be empty!!")	
	private String topic;
	
	@NotNull(message = "Categories can not be null!!")
    @NotEmpty(message = "Category can not be empty!!")	
	private Long[] categories;
	
	private String otherauthors;
	 
	@NotNull(message = "Reviewer should be selected. !!")
	@NotEmpty(message = "Reviewer should be selected. !!")
    private Long[] reviewers;
	
	

	public int getArticleId() {
		return articleId;
	}

	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
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

	public Long[] getReviewers() {
		return reviewers;
	}

	public void setReviewers(Long[] reviewers) {
		this.reviewers = reviewers;
	} 
	
	
}
