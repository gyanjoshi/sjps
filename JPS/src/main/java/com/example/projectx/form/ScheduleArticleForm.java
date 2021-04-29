/**
 * 
 */
package com.example.projectx.form;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Gyan Prakash Joshi
 * Mar 22, 2021
 *
 */
public class ScheduleArticleForm {
	
	@NotNull(message = "Journal ID can not be null!!")
	
	private Integer journalId;
	
	@NotNull(message = "Journal Issue can not be null!!")
	
	private Integer journalIssueId;
	
	@NotNull(message = "Journal Section can not be null!!")
	
	private Long journalSectionId;
	
	@NotNull(message = "Article ID can not be null!!")
	
	private Integer articleId;
	
	@NotNull(message = "Article topic can not be null")
	@NotEmpty(message = "Article topic can not be empty")
	private String topic;
	
	@Min(value=1, message="Toc order should be starting from 1")
	@Digits(integer=2, fraction=0, message = "Toc order should be between 1 and 99. Decimal values are not accepted.")
	private int tocOrder;
	
	
	private boolean isFavorite;
	
	
	public Integer getArticleId() {
		return articleId;
	}
	public void setArticleId(Integer articleId) {
		this.articleId = articleId;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
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
	public  Long getJournalSectionId() {
		return journalSectionId;
	}
	public void setJournalSectionId(Long journalSectionId) {
		this.journalSectionId = journalSectionId;
	}
	public int getTocOrder() {
		return tocOrder;
	}
	public void setTocOrder(int tocOrder) {
		this.tocOrder = tocOrder;
	}
	public boolean getIsFavorite() {
		return isFavorite;
	}
	public void setIsFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}
	
	

}
