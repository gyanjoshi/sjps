package com.example.projectx.model;

import java.sql.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "Journal_Issues")

public class JournalIssue {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="journal_seq_gen")
	@SequenceGenerator(name="journal_seq_gen", sequenceName="journal_seq")
	
	@Column(name="id")
	private Integer Id;
	
	@Column(name="volume")
	private String VolumeNum;
	@Column(name="issue")
	private String IssueNum;
	@Column(name="year")
	private String year;
	@Column(name="month")
	private String month;
	
	@Column(name="coverimagefile")
	private String coverImageFileName;
	
	@Column(name="createddate")
	private Date createdDate;
	
	@Column(name="createdby")
	private String createdBy;
	
	@Column(name="modifieddate")
	private Date modifiedDate;
	
	@Column(name="modifiedby")
	private String modifiedBy;
	
	@Column(name="publishdate")
	private Date publishDate;
	@Column(name="publishedby")
	private String publishedBy;
	
	@Column(name="status")
	private String status;
	
	@ManyToOne
    @JoinColumn
    private Journal journal;
	
	@OneToMany(mappedBy = "journalissue", cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    private Set<Article> articles;
	
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
	
	
	public String getCoverImageFileName() {
		return coverImageFileName;
	}
	public void setCoverImageFileName(String coverImageFileName) {
		this.coverImageFileName = coverImageFileName;
	}
	
	public Set<Article> getArticles() {
		return articles;
	}
	public void setArticles(Set<Article> articles) {
		this.articles = articles;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public void addArticle(Article a)
	{
		articles.add(a);
	}
	
	public boolean removeArticle(Article a)
	{
		return articles.remove(a);
	}
	
	public void removeAllArticles()
	{
		articles.forEach(a -> {
			articles.remove(a);
		});
	}
	
	public Integer getId() {
		return Id;
	}
	public void setId(Integer id) {
		Id = id;
	}
	public Journal getJournal() {
		return journal;
	}
	public void setJournal(Journal journal) {
		this.journal = journal;
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
	
	public Integer getJournalId()
	{
		return this.journal.getId();
	}
	
	

}
