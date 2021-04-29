package com.example.projectx.dao;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.projectx.dto.JournalIssueDto;
import com.example.projectx.dto.PublishedJournalDto;
import com.example.projectx.model.Journal;
import com.example.projectx.model.JournalIssue;

@Repository
@Transactional
public class JournalDao {
	
	@PersistenceContext
    private EntityManager entityManager;
	
	private static final Logger logger=LoggerFactory.getLogger(JournalDao.class);
	
	public List<JournalIssue> getAllJournalIssues(int jid)
    {
    	
    	List<JournalIssue> journals = new ArrayList<JournalIssue>();
    	
    	javax.persistence.Query query = entityManager.createQuery("SELECT u FROM "+JournalIssue.class.getName()+" u where u.journal.Id=:jid");
    	
    	query.setParameter("jid", jid);
    	
    	logger.info("Getting all journal issues defined for the journal id: {}",jid);
    	
		List<?> list = query.getResultList();
		
		if(!list.isEmpty()) 
		{
			for(Object journal: list)
			{
				JournalIssue j = (JournalIssue)journal;
				journals.add(j);
			}
			
			logger.info("Found {} journal issues defined for journal id: {}", journals.size(), jid);
			return journals;
		}
    	
		else
		{
			logger.warn("Could not find any issues for journal id: {}",jid);
			return null;
		}
			
    	
    }
	
	public List<JournalIssue> getAllPreparedJournalIssues(int jid)
    {
    	
    	List<JournalIssue> journals = new ArrayList<JournalIssue>();
    	
    	javax.persistence.Query query = entityManager.createQuery("SELECT u FROM "+JournalIssue.class.getName()+" u where u.journal.Id=:jid and status ='Prepared'");
    	query.setParameter("jid", jid);
    	
    	logger.info("Getting all prepared journal issues defined for the journal id: {}",jid);
    	
    	List<?> list = query.getResultList();
		
		if(!list.isEmpty()) 
		{
			for(Object journal: list)
			{
				JournalIssue j = (JournalIssue)journal;
				journals.add(j);
			}
			logger.info("Found {} prepared issues defined for the journal id: {}",journals.size(), jid);
			return journals;
		}
    	
		else
		{
			logger.info("Could not find any prepared issues defined for the journal id: {}",jid);
			return null;
		}
		
			
    	
    }
	
	public List<JournalIssue> getAllPublishedJournalIssues(int jid)
    {
    	
    	List<JournalIssue> journals = new ArrayList<JournalIssue>();
    	javax.persistence.Query query  = entityManager.createQuery("SELECT u FROM "+JournalIssue.class.getName()+" u where u.journal.Id=:jid and status ='Published'");
    	query.setParameter("jid", jid);
    	
    	logger.info("Getting all published journal issues defined for the journal id: {}",jid);
    	
		List<?> list = query.getResultList();
		
		
		
		if(!list.isEmpty()) 
		{
			for(Object journal: list)
			{
				JournalIssue j = (JournalIssue)journal;
				journals.add(j);
			}
			logger.info("Found {} published issues defined for the journal id: {}",jid);
			return journals;
		}
    	
		else
		{
			logger.info("Could not find any published issues defined for the journal id: {}",jid);
			return null;
		}
			
    	
    }

	public List<Journal> getAllJournals() {
		
		List<Journal> journals = new ArrayList<Journal>();
    	
    	javax.persistence.Query query = entityManager.createQuery("SELECT u FROM "+Journal.class.getName()+" u ");
    	
    	    	
    	logger.info("Getting list of all journals..");
		List<?> list = query.getResultList();
		
		if(!list.isEmpty()) 
		{
			for(Object journal: list)
			{
				Journal j = (Journal)journal;
				journals.add(j);
			}
			logger.info("Found {} journals defined in the system.", journals.size());
			return journals;
		}
    	
		else
		{
			logger.warn("Could not find any journals defined in the system");
			return null;
		}
			
	}

	public List<JournalIssue> getDraftJournalIssues(int jid) {
		List<JournalIssue> journals = new ArrayList<JournalIssue>();
    	javax.persistence.Query query  = entityManager.createQuery("SELECT u FROM "+JournalIssue.class.getName()+" u where u.journal.Id=:jid and status not in ('Published')");
    	query.setParameter("jid", jid);
    	
    	logger.info("Getting list of all draft version (not published) issues for journal id: {}",jid);
    	
		List<?> list = query.getResultList();		
		
		
		if(!list.isEmpty()) 
		{
			for(Object journal: list)
			{
				JournalIssue j = (JournalIssue)journal;
				journals.add(j);
			}
			logger.info("Found {} draft issues for journal id: {}", journals.size(), jid);
			return journals;
		}
    	
		else
		{
			logger.info("Could not find any draft issues for journal id: {}",jid);
			return null;
		}
			
	}

	public List<JournalIssueDto> AllDraftIssues() 
	{
		
		List<JournalIssueDto> journals = new ArrayList<JournalIssueDto>();
		
		String sql = "SELECT new com.example.projectx.dto.JournalIssueDto(a.journal.Id, a.journal.JournalTopic,a.Id,a.coverImageFileName, a.VolumeNum,a.IssueNum,a.year,a.month,a.createdDate,a.createdBy,"
				+ "a.modifiedDate,a.modifiedBy,a.publishDate,a.publishedBy,a.status, count(b) as numItems)"
				+ " FROM "+JournalIssue.class.getName()+" a "
						+ " LEFT JOIN a.articles b "
						+ " WHERE a.status not in ('Published')"
						+ " GROUP BY a.journal.Id, a.journal.JournalTopic,a.Id,a.coverImageFileName, a.VolumeNum,a.IssueNum,a.year,a.month,a.createdDate,a.createdBy,\r\n"
						+ " a.modifiedDate,a.modifiedBy,a.publishDate,a.publishedBy,a.status";
		
		TypedQuery<JournalIssueDto> query = entityManager.createQuery(sql, JournalIssueDto.class);
    	
		journals = query.getResultList();	
		
		return journals;
	}
	
	public List<JournalIssueDto> AllBackIssues() 
	{
		
		List<JournalIssueDto> journals = new ArrayList<JournalIssueDto>();
		
		String sql = "SELECT new com.example.projectx.dto.JournalIssueDto(a.journal.Id, a.journal.JournalTopic,a.Id,a.coverImageFileName, a.VolumeNum,a.IssueNum,a.year,a.month,a.createdDate,a.createdBy,"
				+ "a.modifiedDate,a.modifiedBy,a.publishDate,a.publishedBy,a.status, count(b) as numItems)"
				+ " FROM "+JournalIssue.class.getName()+" a "
						+ " LEFT JOIN a.articles b "
						+ " WHERE a.status in ('Published')"
						+ "GROUP BY a.journal.Id, a.journal.JournalTopic,a.Id,a.coverImageFileName, a.VolumeNum,a.IssueNum,a.year,a.month,a.createdDate,a.createdBy,\r\n"  
						+ " a.modifiedDate,a.modifiedBy,a.publishDate,a.publishedBy,a.status";
		
		TypedQuery<JournalIssueDto> query = entityManager.createQuery(sql, JournalIssueDto.class);
    	
		journals = query.getResultList();	
		
		return journals;
	}

	public List<PublishedJournalDto> getPublishedJournals() {
		
		List<PublishedJournalDto> journals = new ArrayList<PublishedJournalDto>();
		
		String sql = "SELECT new com.example.projectx.dto.PublishedJournalDto(a.journal.Id, a.Id,a.journal.JournalTopic, a.coverImageFileName, "
				+ "a.VolumeNum,a.IssueNum,a.year,a.month,"
				+ "a.publishDate,a.publishedBy, a.status)"
				+ " FROM "+JournalIssue.class.getName()+" a "
				+ " WHERE a.status in ('Published')"
				+ " ORDER BY a.publishDate DESC";
		
		TypedQuery<PublishedJournalDto> query = entityManager.createQuery(sql, PublishedJournalDto.class);
    	
		journals = query.getResultList();	
		
		return journals;
	}
	
	public String getJournalIssueCoverPageFileName(int jid) {
		
		String sql = "SELECT distinct coalesce(a.coverImageFileName, a.journal.coverImageFileName) as filename from "+JournalIssue.class.getName()+" a WHERE a.Id=:jid";
		
		Query query = entityManager.createQuery(sql);
		
		query.setParameter("jid", jid);
		
		String filename = (String) query.getSingleResult();
		
		return filename;
		
	}
	
	public String getJournalCoverPageFileName(int jid) {
		
		String sql = "SELECT distinct a.coverImageFileName as filename from "+Journal.class.getName()+" a WHERE a.Id=:jid";
		
		Query query = entityManager.createQuery(sql);
		
		query.setParameter("jid", jid);
		
		String filename = (String) query.getSingleResult();
		
		return filename;
		
	}
	
}
