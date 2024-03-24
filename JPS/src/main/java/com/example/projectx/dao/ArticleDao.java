package com.example.projectx.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.projectx.dto.ArticleDto;
import com.example.projectx.model.AppUser;
import com.example.projectx.model.Article;
import com.example.projectx.model.Person;

@Repository
@Transactional
public class ArticleDao {
	
	@PersistenceContext
    private EntityManager entityManager;
	
	private static final Logger logger=LoggerFactory.getLogger(ArticleDao.class);
	

	public List<Person> getReviewers(int aid) {
		List<Person> reviewers = new ArrayList<Person>();
		
		String sql = "SELECT p From " + Person.class.getName() + " p \r\n"+
					 " WHERE EXISTS (select 1 from "+ Article.class.getName()+" a INNER JOIN a.reviewers r WHERE p.id = r.id AND a.Id=:aid )";
		
		TypedQuery<Person> query = entityManager.createQuery(sql,Person.class);
		
		query.setParameter("aid", aid);


        reviewers = query.getResultList();
		
		return reviewers;
	}
	
	public List<ArticleDto> getApprovedArticles() {
		List<ArticleDto> articles = new ArrayList<ArticleDto>();
		
		String sql = "SELECT new com.example.projectx.dto.ArticleDto(a.Id, a.authorid, a.topic, b.fullName, a.otherAuthors, a.status, a.fileName, a.uploadDate) \r\n"
				+ " FROM "+Article.class.getName()+" a \r\n" + 
						"left join "+AppUser.class.getName()+" b\r\n" + 
						"on a.authorid=b.userName\r\n" +
					 "where a.status in ('Approved','Unscheduled')";
		
		TypedQuery<ArticleDto> query = entityManager.createQuery(sql,ArticleDto.class);
		
		articles = query.getResultList();
		
		return articles;
		
		
	}

	public List<ArticleDto> getScheduledArticles() 
	{
		
		List<ArticleDto> articles = new ArrayList<ArticleDto>();
		
		String sql = "SELECT new com.example.projectx.dto.ArticleDto(a.Id, a.authorid, a.topic, b.fullName, a.otherAuthors, a.status, a.fileName, a.uploadDate,"
				+ " a.journalissue.VolumeNum,a.journalissue.IssueNum,a.journalissue.year, a.journalissue.month, a.journalsection.sectionName,"
				+ "a.journalissue.journal.JournalTopic, a.tocOrder) \r\n"
				+ " FROM "+Article.class.getName()+" a \r\n" + 
					" left join "+AppUser.class.getName()+" b\r\n" + 
					" on a.authorid=b.userName\r\n" +
					" where a.status in ('Scheduled','Updated to PDF','Unpublished')";
		
		TypedQuery<ArticleDto> query = entityManager.createQuery(sql,ArticleDto.class);
		
		articles = query.getResultList();
		
		return articles;
	}
	
	public List<ArticleDto> getScheduledArticlesBySection(Integer issueId, Long sectionId) 
	{
		
		List<ArticleDto> articles = new ArrayList<ArticleDto>();
		
		String sql = "SELECT new com.example.projectx.dto.ArticleDto(a.Id, a.authorid, a.topic, b.fullName, a.otherAuthors, a.status, a.fileName, a.uploadDate,"
				+ " a.journalissue.VolumeNum,a.journalissue.IssueNum,a.journalissue.year, a.journalissue.month, a.journalsection.sectionName,"
				+ "a.journalissue.journal.JournalTopic, a.tocOrder, a.pageCount) \r\n"
				+ " FROM "+Article.class.getName()+" a \r\n" + 
					" left join "+AppUser.class.getName()+" b\r\n" + 
					" on a.authorid=b.userName\r\n" +
					" where a.status in ('Scheduled','Updated to PDF')"
					+ "AND a.journalissue.Id=:issue AND a.journalsection.Id=:section";
		
		TypedQuery<ArticleDto> query = entityManager.createQuery(sql,ArticleDto.class);
		
		query.setParameter("issue", issueId);
		query.setParameter("section", sectionId);
		
		articles = query.getResultList();
		
		return articles;
	}

}
