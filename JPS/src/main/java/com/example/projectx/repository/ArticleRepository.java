package com.example.projectx.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.projectx.dto.ArticleDto;
import com.example.projectx.model.Article;

public interface ArticleRepository extends JpaRepository<Article , Integer> {

	
	
	@Query("SELECT new com.example.projectx.dto.ArticleDto(a.Id, a.authorid, a.topic, b.fullName, b.address1, a.tocOrder, a.pageCount, a.status, a.fileName) \r\n" + 
			"FROM Article a\r\n" + 
			"left join AppUser b\r\n" + 
			"on a.authorid=b.userName\r\n" + 
			"where a.status='Approved'"
			+ "AND a.journalissue.Id=:journalId")
	
	public List<ArticleDto> getApprovedArticles(int journalId);
	
	@Query("SELECT new com.example.projectx.dto.ArticleDto(a.Id, a.authorid, a.topic, b.fullName, a.otherAuthors, a.status, a.fileName, a.uploadDate) \r\n" + 
			"FROM Article a\r\n" + 
			"left join AppUser b\r\n" + 
			"on a.authorid=b.userName\r\n" + 
			"where (a.status in ('Submitted','resubmitted','feedback-sent','Re-submitted'))"
			+ "AND a.authorid=:username")
	public List<ArticleDto> getPendingArticles(@Param("username") String username); // Used for list of pending articles in author dashboard

	@Query("SELECT new com.example.projectx.dto.ArticleDto(a.Id, a.authorid, a.topic, b.fullName, a.otherAuthors, a.tocOrder, a.pageCount, a.status, a.fileName) \r\n" + 
			"FROM Article a\r\n" + 
			"left join AppUser b\r\n" + 
			"on a.authorid=b.userName\r\n" + 
			"where a.status='Published'"
			+ " AND a.journalissue.id=:jiid")
	public List<ArticleDto> getPublishedArticles(int jiid);

	@Query("SELECT new com.example.projectx.dto.ArticleDto(a.Id, a.topic, b.fullName, a.pageCount, a.viewCount, a.downloadCount, a.articleAbstract, a.fileName, c.VolumeNum, c.IssueNum, c.year, c.month, a.journalissue.journal.JournalTopic) \r\n" + 
			"FROM Article a\r\n" + 
			"left join AppUser b\r\n" + 
			"on a.authorid=b.userName\r\n" + 
			"left join JournalIssue c "+
			"on a.journalissue = c.Id "+
			"where a.status='Published' AND a.isFavorite=true")
	public List<ArticleDto> getFavoriteArticles();
	
	@Query("SELECT new com.example.projectx.dto.ArticleDto(a.Id, a.topic, b.fullName, a.pageCount, a.viewCount, a.downloadCount, a.articleAbstract, a.fileName, c.VolumeNum, c.IssueNum, c.year, c.month, a.journalissue.journal.JournalTopic) \r\n" + 
			"FROM Article a\r\n" + 
			"left join AppUser b\r\n" + 
			"on a.authorid=b.userName\r\n" + 
			"left join JournalIssue c "+
			"on a.journalissue = c.Id "+
			"where a.status='Published' AND a.Id=:aid")
	public ArticleDto getPublishedArticle(int aid);

	@Query("SELECT new com.example.projectx.dto.ArticleDto(a.Id, a.topic, b.fullName, a.pageCount, a.viewCount, a.downloadCount, a.articleAbstract, a.fileName, c.VolumeNum, c.IssueNum, c.year, c.month, a.journalissue.journal.JournalTopic) \r\n" + 
			"FROM Article a\r\n" + 
			"left join AppUser b\r\n" + 
			"on a.authorid=b.userName\r\n" + 
			"left join JournalIssue c "+
			"on a.journalissue = c.Id "+
			"where a.status='Published' AND a.authorid=:username")
	public List<ArticleDto> getPublishedArticles(String username);

	@Query("SELECT new com.example.projectx.dto.ArticleDto(a.Id, a.authorid, a.topic, b.fullName, a.otherAuthors, a.status, a.fileName, a.uploadDate) \r\n" + 
			"FROM Article a\r\n" + 
			"left join AppUser b\r\n" + 
			"on a.authorid=b.userName\r\n" + 
			"where a.status in ('resubmitted','feedback-sent','Sent to Reviewer','Re-submitted','Updated to PDF','Unapproved')")
	public List<ArticleDto> getUnderReviewArticles(); // Used by the List of Under Review (All Active) submissions in the Editor Dashboard
	
	@Query("SELECT new com.example.projectx.dto.ArticleDto(a.Id, a.authorid, a.topic, b.fullName, a.otherAuthors, a.status, a.fileName, a.uploadDate) \r\n" + 
			"FROM Article a\r\n" + 
			"left join AppUser b\r\n" + 
			"on a.authorid=b.userName\r\n" + 
			"where a.status in ('Submitted')")
	public List<ArticleDto> getUnAssignedArticles(); // Used by the list of unassigned submissions in the editor dashboard.
	
	@Query("SELECT new com.example.projectx.dto.ArticleDto(a.Id, a.authorid, a.topic, b.fullName, a.otherAuthors, a.status, a.fileName, a.uploadDate) \r\n" + 
			"FROM Article a\r\n" + 
			"left join AppUser b\r\n" + 
			"on a.authorid=b.userName\r\n" +
			" LEFT JOIN AppUser u "+
			" ON 1=1 "+
			" INNER JOIN a.reviewers r \r\n"+
			" WHERE r.id  = u.id \r\n"
			+ " AND u.userName=:username \r\n"
			+ " AND a.status in ('resubmitted','feedback-sent','Sent to Reviewer','Re-submitted','Updated to PDF','Unapproved')")
	public List<ArticleDto> getAllAssignedTo(String username); // Used by the list of all articles assigned to the logged in user (editor) in editor dashboard.

	@Query("SELECT new com.example.projectx.dto.ArticleDto(a.Id, a.authorid, a.topic, b.fullName, a.otherAuthors, a.status, a.fileName, a.journalissue.publishDate) \r\n" + 
			"FROM Article a\r\n" + 
			"left join AppUser b\r\n" + 
			"on a.authorid=b.userName\r\n" + 
			"where a.status in ('Published','rejected')")
	public List<ArticleDto> getArchivedArticles(); // Used by the list of all Archive (published or (rejected)) articles	
	
	
}

;