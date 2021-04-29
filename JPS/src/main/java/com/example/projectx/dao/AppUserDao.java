package com.example.projectx.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.example.projectx.model.AppRole;
import com.example.projectx.model.AppUser;
import com.example.projectx.model.Person;
import com.example.projectx.model.Qualification;
import com.example.projectx.model.Title;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
 
@Repository
@Transactional
public class AppUserDao {
 
	@PersistenceContext
    private EntityManager entityManager;
 
	private static final Logger logger=LoggerFactory.getLogger(AppUserDao.class);
	
	public AppUser getActiveUser(String userName) {
		AppUser activeUserInfo = new AppUser();
		short enabled = 1;
		List<?> list = entityManager.createQuery("SELECT u FROM "+AppUser.class.getName()+" u WHERE userName=:userName and enabled=:enabled")
				.setParameter("userName", userName).setParameter("enabled", enabled).getResultList();
		if(!list.isEmpty()) {
			if(list.size() > 1)
				logger.warn("More than 1 entry of user {} is found..",userName);
			
			activeUserInfo = (AppUser)list.get(0);
			logger.info("Active User found with username {}",userName);
			
			return activeUserInfo;
		}
		
		else {
			
			logger.info("Active User is not found with username {}",userName);
			return null;
		}
		
	}
	
    public AppUser findUserAccount(String userName) {
        
    	try {
            String sql = "Select e from " + AppUser.class.getName() + " e " //
                    + " Where e.userName = :userName ";
 
            
            Query query = entityManager.createQuery(sql, AppUser.class);
            query.setParameter("userName", userName);
 
            logger.info("Searching for AppUser with userName {} ..",userName);
            
            return (AppUser) query.getSingleResult();
        } 
    	catch (NoResultException e) {
        	logger.warn("AppUser with userName {} is not found",userName);
            return null;
        }
    }
    
    public AppUser findUserAccountByEmail(String email) {
    	
    	String sql = "Select e from " + AppUser.class.getName() + " e " //
                + " Where e.email = :email ";

        
        Query query = entityManager.createQuery(sql, AppUser.class);
        query.setParameter("email", email);

        logger.info("Searching for AppUser with email {} ..",email);
        
    	try {
            
            
            AppUser user = (AppUser) query.getSingleResult();
            
            
            return user;
        } 
    	catch (NoResultException e) {
        	logger.warn("AppUser with email {} is not found",email);
            return null;
        }
    	catch (NonUniqueResultException e) {
    		logger.warn("More than one AppUser with email {} is found",email);
            return (AppUser) query.getResultList().get(0);
    	}
    }
    
    public List<AppUser> getAllEditors()
    {
    	
    	short enabled = 1;
    	String role="ROLE_EDITOR";
    	
    	List<AppUser> users = new ArrayList<AppUser>();
    	
		List<?> list = entityManager.createQuery("SELECT u FROM "+AppUser.class.getName()+" u WHERE enabled=:enabled AND role=:role")
				.setParameter("role", role).setParameter("enabled", enabled).getResultList();
		
		logger.info("Getting list of all active editors..");
		
		if(!list.isEmpty()) 
		{
			for(Object user: list)
			{
				AppUser au = (AppUser)user;
				users.add(au);
			}
			
			logger.info("Found {} active editors.", users.size());
			return users;
		}
    	
		else
		{
			logger.error("No active editor found..");
			return null;
		}
			
    	
    }

	public List<AppUser> getAllAuthors() {
		short enabled = 1;
    	String role="ROLE_AUTHOR";
    	
    	List<AppUser> users = new ArrayList<AppUser>();
    	
		List<?> list = entityManager.createQuery("SELECT u FROM "+AppUser.class.getName()+" u WHERE enabled=:enabled AND role=:role ")
				.setParameter("role", role).setParameter("enabled", enabled).getResultList();
		
		logger.info("Getting list of all active authors..");
		
		if(!list.isEmpty()) 
		{
			for(Object user: list)
			{
				AppUser au = (AppUser)user;
				users.add(au);
			}
			logger.info("Found {} active authors.", users.size());
			
			return users;
		}
    	
		else
		{
			logger.warn("No active author found..");
			return null;
		}
			
	}
    
	public List<AppRole> getAllRoles()
	{
		List<AppRole> roles = new ArrayList<AppRole>();

			String sql = "select r from "+AppRole.class.getName()+" r";
			Query query = entityManager.createQuery(sql, AppRole.class); 
			
			logger.info("Getting list of all defined roles..");
 
            List<?> list =  query.getResultList();
            if(!list.isEmpty()) 
    		{
    			for(Object r: list)
    			{
    				AppRole ar = (AppRole)r;
    				roles.add(ar);
    			}
    			
    			logger.info("Found {} defined roles..",roles.size());
    			return roles;
    		}
        	
    		else
    		{
    			logger.error("Could not find any roles..");
    			return null;
    		}
    			
	}
	public List<Title> getAllTitles()
	{
		List<Title> titles = new ArrayList<Title>();

			String sql = "select r from "+Title.class.getName()+" r";
			Query query = entityManager.createQuery(sql, Title.class);            
 
			logger.info("Getting list of all titles..");
			
            List<?> list =  query.getResultList();
            if(!list.isEmpty()) 
    		{
    			for(Object r: list)
    			{
    				Title ar = (Title)r;
    				titles.add(ar);
    			}    			
    			
    			return titles;
    		}
        	
    		else
    		{
    			logger.warn("Could not find any title.");
    			return null;
    		}
    			
	}
	public List<Qualification> getAllQualifications()
	{
		logger.info("Getting list of all defined qualifications..");
		
		String sql = "select r from "+Qualification.class.getName()+" r";
		TypedQuery<Qualification> query = entityManager.createQuery(sql, Qualification.class);            
 
		List<Qualification> qualifications =  query.getResultList();
        
		if (qualifications.size() > 0)
			logger.info("Found {} qualifications in the system",qualifications.size());
		else
			logger.info("Could not find any qualification in the system");
		
		return qualifications;
		
		
    			
	}

	public List<AppUser> getAllUsers() {
		
		logger.info("Getting list of all users");
		
		String sql = "From "+AppUser.class.getName();
		
		TypedQuery<AppUser> query = entityManager.createQuery(sql,AppUser.class);
		
		List<AppUser> users = query.getResultList();
		
		if (users.size() > 0)
			logger.info("Found {} users in the system",users.size());
		else
			logger.info("Could not find any users in the system");
		
		return users;
			
	}

	
}
