package com.example.projectx.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "Articles")
public class Article {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="article_seq_gen")
	@SequenceGenerator(name="article_seq_gen", sequenceName="article_seq")
	
	@Column(name="article_id")
	private Integer Id;
	@Column(name="topic")
	private String topic;
	@Column(name="status")
	private String status;
	
	@Lob
	@Column(name="abstract")	
	private String articleAbstract;
	
	@Column(name="pageCount")
	private Integer pageCount;
	
	@Column(name="tocOrder")
	private Integer tocOrder;
	
	@Column(name="authorid")
	private String authorid;// matches with username of AppUser
	
	@Column(name="filename")
	private String fileName;
	
	@Column(name="fileUploadDate")
	private Date uploadDate;
	
	@Column(name="uploadedBy")
	private String uploadedBy;
	
	@ManyToOne
    @JoinColumn
    private JournalIssue journalissue;
	
	@Column(name="downloadcount")
	private Integer downloadCount;
	
	@Column(name="viewcount")
	private Integer viewCount;
	

	@Column(name="isfavorite", nullable = true)
	private Boolean isFavorite;
	
	@Column(name="otherAuthors")
	private String otherAuthors;
	
	@ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
        name = "Article_Categories", 
        joinColumns = { @JoinColumn(name = "article_id") }, 
        inverseJoinColumns = { @JoinColumn(name = "cat_id") }
    )
	
	
	
    private Set<Category> categories = new HashSet<>();
	
	
	@ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinTable(
        name = "Article_Reviewers", 
        joinColumns = { @JoinColumn(name = "article_id") }, 
        inverseJoinColumns = { @JoinColumn(name = "Id") }
    )
	
	
	
    private Set<Person> reviewers = new HashSet<>();
	
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	private JournalSection journalsection;
	
	
	
//	public JournalIssue getJournal() {
//		return journalissue;
//	}
//	public void setJournal(JournalIssue journal) {
//		this.journalissue = journal;
//	}
	
	public Integer getJournalIssueId()
	{
		return journalissue.getId();
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getId() {
		return Id;
	}
	public void setId(Integer id) {
		Id = id;
	}
	public String getArticleAbstract() {
		return articleAbstract;
	}
	public void setArticleAbstract(String articleAbstract) {
		this.articleAbstract = articleAbstract;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}
	public String getAuthorid() {
		return authorid;
	}
	public void setAuthorid(String authorid) {
		this.authorid = authorid;
	}
	public Integer getPageCount() {
		return pageCount;
	}
	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}
	public Integer getTocOrder() {
		return tocOrder;
	}
	public void setTocOrder(Integer tocOrder) {
		this.tocOrder = tocOrder;
	}
	public Integer getDownloadCount() {
		return downloadCount;
	}
	public void setDownloadCount(Integer downloadCount) {
		this.downloadCount = downloadCount;
	}
	public Integer getViewCount() {
		return viewCount;
	}
	public void setViewCount(Integer viewCount) {
		this.viewCount = viewCount;
	}
	
	public JournalIssue getJournalissue() {
		return journalissue;
	}
	public void setJournalissue(JournalIssue journalissue) {
		this.journalissue = journalissue;
	}
	public Boolean getIsFavorite() {
		return isFavorite;
	}
	public void setIsFavorite(Boolean isFavorite) {
		this.isFavorite = isFavorite;
	}
	public String getOtherAuthors() {
		return otherAuthors;
	}
	public void setOtherAuthors(String otherAuthors) {
		this.otherAuthors = otherAuthors;
	}
	public Set<Category> getCategories() {
		return categories;
	}
	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}
	public Set<Person> getReviewers() {
		return reviewers;
	}
	public void setReviewers(Set<Person> reviewers) {
		this.reviewers = reviewers;
	}
	
	public void addReviewer(Person p) {
		this.reviewers.add(p);
		p.assignArticle(this);
	}
	
	public void removeReviewer(Person p)
	{
		this.reviewers.remove(p);
		p.revokeArticle(this);
	}
	
	public void removeAllReviewer()
	{
		Set<Person> toRemoveList = new HashSet<Person>();
		
				
		this.reviewers.forEach( p -> {
			p.revokeArticle(this);
			toRemoveList.add(p);
			
			
		});
		this.reviewers.removeAll(toRemoveList);
		
	}
	public JournalSection getJournalsection() {
		return journalsection;
	}
	public void setJournalsection(JournalSection journalsection) {
		this.journalsection = journalsection;
	}
	
	public boolean isPDFUploaded() {
		
		if(this.status.equalsIgnoreCase("Updated to PDF"))
			return true;
		if(this.fileName != null && this.fileName.endsWith(".pdf"))
			return true;
		else
			return false;
	}
	
	public void removeCategory(Category c)
	{
		this.categories.remove(c);
	}
	
	public void removeAllCategories()
	{
		Set<Category> toRemoveList = new HashSet<Category>();
		
		categories.forEach(c -> {
			c.removeArticle(this);	
			toRemoveList.add(c);
			
		});	
		this.categories.removeAll(toRemoveList);
		
	}

}
