package com.example.projectx.dto;

import java.util.Date;


public class ArticleDto {
	
	private Integer articleId;
	private String authorId;
	private String topic;
	private String authorName;
	private String address;
	private String city;
	private String email;
	private String status;
	
	private String articleAbstract;
	private String fileName;
	
	private Integer tocorder;
	private Integer pageCount;
	private String pageFromTo;
	
	private Integer viewCount;
	private Integer downloadCount;
	private String issueNum;
	private String volumeNum;
	private String year;
	private String month;
	
	private String journalTitle;
	
	private String categories;
	private Date uploadDate;
	private String otherAuthors;
	
	private String reviewers;
	
	private String sectionName;
	
	private String allAuthorsName;
	
		
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public Integer getViewCount() {
		return viewCount;
	}
	public void setViewCount(Integer viewCount) {
		this.viewCount = viewCount;
	}
	public Integer getDownloadCount() {
		return downloadCount;
	}
	public void setDownloadCount(Integer downloadCount) {
		this.downloadCount = downloadCount;
	}
	public String getIssueNum() {
		return issueNum;
	}
	public void setIssueNum(String issueNum) {
		this.issueNum = issueNum;
	}
	public String getVolumeNum() {
		return volumeNum;
	}
	public void setVolumeNum(String volumeNum) {
		this.volumeNum = volumeNum;
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


	
	public ArticleDto(int id, String authorid, String topic, String fullName, String address1, String city, String email, String status, String articleAbstract, String fileName) 
	
	{
		this.articleId = id;
		this.authorId = authorid;
		this.topic = topic;
		this.authorName = fullName;
		this.address = address1;
		this.city = city;
		this.email = email;
		this.status = status;
		this.articleAbstract = articleAbstract;
		this.fileName  = fileName;
		
		if (fullName != null && !fullName.isEmpty())
			this.allAuthorsName = fullName;
		
		if (otherAuthors != null && !otherAuthors.isEmpty())		
			if(this.allAuthorsName != null && !this.allAuthorsName.isEmpty())
				this.allAuthorsName += ", " + otherAuthors;
	}
// Used for getPendingArticles(@Param("username") String username); // Used for list of pending articles in author-dashboard
// a.Id, a.authorid, a.topic, b.fullName, a.otherAuthors, a.status, a.fileName, a.uploadDate,a.categories
	
	public ArticleDto(Integer aid, String authorId, String topic, String fullName, String otherAuthors, String status, String fileName, Date uploadDate)
	{
		this.articleId = aid;
		this.authorId = authorId;
		this.authorName = fullName;
		this.topic = topic;
		this.otherAuthors = otherAuthors;
		this.status = status;
				
		this.fileName  = fileName;
		this.uploadDate = uploadDate;
		
		if (fullName != null && !fullName.isEmpty())
			this.allAuthorsName = fullName;
		
		if (otherAuthors != null && !otherAuthors.isEmpty())		
			if(this.allAuthorsName != null && !this.allAuthorsName.isEmpty())
				this.allAuthorsName += ", " + otherAuthors;
	}
//	com.example.projectx.dto.ArticleDto(a.Id, a.authorid, a.topic, b.fullName, a.otherAuthors, a.status, a.fileName, a.uploadDate,"
//			+ " a.journalissue.VolumeNum,a.journalissue.VolumeNum,a.journalIssue.IssueNum,a.journalIssue.year, a.journalIssue.month, a.journalsection.sectionName)
//	a.Id, a.authorid, a.topic, b.fullName, a.otherAuthors, a.status, a.fileName, a.uploadDate,"
//			+ " a.journalissue.VolumeNum,a.journalissue.VolumeNum,a.journalissue.IssueNum,a.journalissue.year, a.journalissue.month, a.journalsection.sectionName,"
//			+ "a.journalissue.journal.JournalTopic
//
//	a.Id, a.authorid, a.topic, b.fullName, a.otherAuthors, a.status, a.fileName, a.uploadDate,"
//			+ " a.journalissue.VolumeNum,a.journalissue.IssueNum,a.journalissue.year, a.journalissue.month, a.journalsection.sectionName,"
//			+ "a.journalissue.journal.JournalTopic, a.tocOrder"
	public ArticleDto(Integer aid, String authorId, String topic, String fullName, String otherAuthors, String status, String fileName, 
			Date uploadDate,String volumeNum, String IssueNum, String year, String month,String sectionName, String journaltitle, Integer toc)
	{
		this.articleId = aid;
		this.authorId = authorId;
		this.authorName = fullName;
		this.topic = topic;
		this.otherAuthors = otherAuthors;
		this.status = status;
				
		this.fileName  = fileName;
		this.uploadDate = uploadDate;
		this.volumeNum = volumeNum;
		this.issueNum = IssueNum;
		this.year = year;
		this.month = month;
		this.journalTitle = journaltitle;
		this.sectionName = sectionName;
		
		this.tocorder = toc;
		
		
		
		
		if (fullName != null && !fullName.isEmpty())
			this.allAuthorsName = fullName;
		
		if (otherAuthors != null && !otherAuthors.isEmpty())		
			if(this.allAuthorsName != null && !this.allAuthorsName.isEmpty())
				this.allAuthorsName += ", " + otherAuthors;
		
	}		
	public ArticleDto(Integer aid, String authorId, String topic, String fullName, String otherAuthors, String status, String fileName, 
			Date uploadDate,String volumeNum, String IssueNum, String year, String month,String sectionName, String journaltitle, Integer toc, Integer pageCount)
	{
		this.articleId = aid;
		this.authorId = authorId;
		this.authorName = fullName;
		this.topic = topic;
		this.otherAuthors = otherAuthors;
		this.status = status;
				
		this.fileName  = fileName;
		this.uploadDate = uploadDate;
		this.volumeNum = volumeNum;
		this.issueNum = IssueNum;
		this.year = year;
		this.month = month;
		this.journalTitle = journaltitle;
		this.sectionName = sectionName;
		
		this.tocorder = toc;
		this.pageCount  = pageCount;
		
		
		
		if (fullName != null && !fullName.isEmpty())
			this.allAuthorsName = fullName;
		
		if (otherAuthors != null && !otherAuthors.isEmpty())		
			if(this.allAuthorsName != null && !this.allAuthorsName.isEmpty())
				this.allAuthorsName += ", " + otherAuthors;
		
	}
//	a.Id, a.topic, b.fullName, a.pageCount, a.viewCount, a.downloadCount, a.articleAbstract, a.fileName, c.VolumeNum, c.IssueNum, c.year, c.month, a.journalissue.journal.JournalTopic
// Used for Editor pick articles and article detail page

	public ArticleDto(Integer aid, String topic, String fullName, Integer pageCount, Integer viewCount, Integer downloadCount,String articleAbstract, String fileName, String VolumeNum, String IssueNum, String year, String month, String journaltitle)
	{
		this.articleId = aid;
		this.topic = topic;
		this.authorName = fullName;
		this.pageCount = pageCount;
		this.viewCount = viewCount;
		this.downloadCount = downloadCount;
		this.articleAbstract = articleAbstract;
		this.fileName = fileName;
		this.volumeNum = VolumeNum;
		this.issueNum = IssueNum;
		this.year = year;
		this.month = month;
		this.journalTitle = journaltitle;
		
		if (fullName != null && !fullName.isEmpty())
			this.allAuthorsName = fullName;
		
		if (otherAuthors != null && !otherAuthors.isEmpty())		
			if(this.allAuthorsName != null && !this.allAuthorsName.isEmpty())
				this.allAuthorsName += ", " + otherAuthors;
	}
	
	
//	a.Id, a.authorid, a.topic, b.fullName, b.address1, a.tocOrder, a.pageCount, a.status, a.fileName
// Used in List<ArticleDto> getPublishedArticles(int jiid) in articleService.
	public ArticleDto(int id, String authorid, String topic, String fullName,String otherAuthors, Integer tocorder, Integer pageCount, String status,String fileName) 
	
	{
		this.articleId = id;
		this.authorId = authorid;
		this.topic = topic;
		this.authorName = fullName;
		this.otherAuthors = otherAuthors;
		this.tocorder = tocorder;
		this.pageCount = pageCount;
		
		this.status = status;
		this.fileName = fileName;
		
		if (fullName != null && !fullName.isEmpty())
			this.allAuthorsName = fullName;
		else
			this.allAuthorsName = authorid;
		
		if (otherAuthors != null && !otherAuthors.isEmpty())		
			if(this.allAuthorsName != null && !this.allAuthorsName.isEmpty())
				this.allAuthorsName += ", " + otherAuthors;	
		
//		System.out.println("All authors = "+this.allAuthorsName);
			
	}
	
	public Integer getArticleId() {
		return articleId;
	}
	public void setArticleId(Integer articleId) {
		this.articleId = articleId;
	}
	public String getAuthorId() {
		return authorId;
	}
	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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

	public Integer getTocorder() {
		return tocorder;
	}

	public void setTocorder(Integer tocorder) {
		this.tocorder = tocorder;
	}

	public Integer getPageCount() {
		return pageCount;
	}

	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}

	public String getPageFromTo() {
		return pageFromTo;
	}

	public void setPageFromTo(String pageFromTo) {
		this.pageFromTo = pageFromTo;
	}
	
	public String getPublishInfo()
	{
		return this.journalTitle+" ,"+ " Vol-"+volumeNum+", Issue-"+issueNum+" "+month+" "+year;
	}
	public String getJournalTitle() {
		return journalTitle;
	}
	public void setJournalTitle(String journalTitle) {
		this.journalTitle = journalTitle;
	}
	public String getCategories() {
		return categories;
	}
	public void setCategories(String categories) {
		this.categories = categories;
	}
	public Date getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}
	public String getOtherAuthors() {
		return otherAuthors;
	}
	public void setOtherAuthors(String otherAuthors) {
		this.otherAuthors = otherAuthors;
	}
	public String getReviewers() {
		return reviewers;
	}
	public void setReviewers(String reviewers) {
		this.reviewers = reviewers;
	}
	public String getAllAuthorsName() {
		return allAuthorsName;
	}
	public void setAllAuthorsName(String allAuthorsName) {
		this.allAuthorsName = allAuthorsName;
	}
	public boolean isPDFUploaded() {
		
		if(this.status.equalsIgnoreCase("Updated to PDF"))
			return true;
		if(this.fileName != null && this.fileName.endsWith(".pdf"))
			return true;
		else
			return false;
	}
	
	
	

}
