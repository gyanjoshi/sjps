package com.example.projectx.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import com.example.projectx.dao.ArticleDao;
import com.example.projectx.dao.JournalDao;
import com.example.projectx.dto.ArticleDto;
import com.example.projectx.exception.ArticleNotFoundException;
import com.example.projectx.exception.InvalidCategoryException;
import com.example.projectx.exception.UnauthorizedAccessException;
import com.example.projectx.form.ArticleInfoUpdateForm;
import com.example.projectx.form.ArticleUploadForm;
import com.example.projectx.mail.EmailService;
import com.example.projectx.mail.Mail;
import com.example.projectx.model.AppUser;
import com.example.projectx.model.Article;
import com.example.projectx.model.Category;
import com.example.projectx.model.JournalIssue;
import com.example.projectx.model.Person;
import com.example.projectx.repository.ArticleRepository;
import com.example.projectx.repository.CategoryRepository;
import com.example.projectx.repository.JournalIssueRepository;
import com.example.projectx.repository.PersonRepository;

@Service
public class ArticleService {
	
	@Value("${upload.path.article}")
    private String path;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
    private EmailService emailService;
	
	@Autowired
    private ArticleRepository articleRepo;
	
	@Autowired
	CategoryRepository catRepo;
	
	@Autowired
	PersonRepository personRepo;
	
	@Autowired
	ArticleDao articleDao;
	
	
	private static final Logger logger=LoggerFactory.getLogger(ArticleService.class);
	
	private static final String pdfFileMime = "application/pdf";
	
	public void saveArticle(String topic, String articleAbstract, MultipartFile file,String uploadedBy)
	{

		logger.info("Trying to save new article titled {} submitted by author {}.", topic, uploadedBy);
		
		if (file == null || file.isEmpty())
		{
			logger.error("Article file is not uploaded. returning without submitting article");
			return;
		}
		
        // Article Object
        Article article = new Article();
        article.setTopic(topic);
        article.setArticleAbstract(articleAbstract);
        article.setStatus("Submitted");
        article.setAuthorid(uploadedBy);
        article.setUploadedBy(uploadedBy);
       // article.setFileName(fileName);
        article.setUploadDate(new java.sql.Date(System.currentTimeMillis()));
        
        logger.trace("Article object prepared to save at database.");
        
        Article a = articleRepo.save(article);
        
        int id = a.getId();
        
        logger.trace("Article object saved to database with id: {}", id);
        
        String articleFileName = "Article_"+id+"."+FilenameUtils.getExtension(file.getOriginalFilename());
        
        logger.info("Article will be saved at {} with file name {}", path, articleFileName);
        
        FileStorageService.uploadFile(path, articleFileName, file);
        
        logger.info("Article saved at {} with file name {}", path, articleFileName);
        
        logger.trace("Updating article file name in database..");
        
        article.setFileName(articleFileName);
        
        articleRepo.save(article);
        
        logger.trace("Article filename is updated in database.");  
        
        // pull the list of all executive editors from user table.  Executive editors are those, who
//        have been registered in user table with role EDITOR.
        
        List<AppUser> editors = userDetailsService.getAllEditors();
        
        AppUser author = userDetailsService.getUserByUsername(uploadedBy);
        
        logger.info("Preparing email to notify all executive editors..");
        
        Mail mail = new Mail();
        
        editors.forEach(u -> mail.addTo(u.getEmail()));
        
        mail.setSubject("Article submitted: "+a.getTopic());
        
        mail.setContent("Hi Editors,"+
       		 		 author.getFullName()+" has uploaded new article titled "+a.getTopic()+
       		 		 ". Please review this article.");
        
        try {
 			emailService.sendHtmlMessage(mail,FileStorageService.multipartToFile(file,file.getOriginalFilename()));
 			logger.info("E-mail sent to all editors notifying article upload..");
 			
 		} catch (MessagingException e) {
 			logger.error("E-mail Could not be sent to {}", mail.getTo());
 			
 		} catch (Exception e) {
 			logger.error("Error in sending email.");
			e.printStackTrace();
		}
        
	}
	

/*
 * ***************************** re-factored for getPendingArticles () method 
 * *** 4 new methods added: getAllAssignedTo(User user), getUnAssignedArticles(), getUnderReview(), getArchived()
 */
	public List<ArticleDto> getAllAssignedTo(String username)
	{
		List<ArticleDto> articles = articleRepo.getAllAssignedTo(username);
		
		// For each article, get categories from repository and join the category list into single string object.
		
		articles.forEach(a -> {
			StringJoiner joiner = new StringJoiner(",");
			catRepo.getAllCategories(a.getArticleId())
					.forEach(c -> {
			
									joiner.add(c.getCategory());
					});
			String cats = joiner.toString();
			a.setCategories(cats);
		});			
			
			
		return articles;

	}
	public List<ArticleDto> getUnAssignedArticles()
	{
		List<ArticleDto> articles = articleRepo.getUnAssignedArticles();
		
		// For each article, get categories from repository and join the category list into single string object.
		
		articles.forEach(a -> {
			StringJoiner joiner = new StringJoiner(",");
			catRepo.getAllCategories(a.getArticleId())
					.forEach(c -> {
			
									joiner.add(c.getCategory());
					});
			String cats = joiner.toString();
			a.setCategories(cats);
		});			
			
			
		return articles;

	}
	public List<ArticleDto> getUnderReviewArticles()
	{
		List<ArticleDto> articles = articleRepo.getUnderReviewArticles();
		
		// For each article, get categories from repository and join the category list into single string object.
		
		articles.forEach(a -> {
			StringJoiner joiner = new StringJoiner(",");
			catRepo.getAllCategories(a.getArticleId())
					.forEach(c -> {
			
									joiner.add(c.getCategory());
					});
			String cats = joiner.toString();
			a.setCategories(cats);
		});	
		
		// For each article, get reviewers from repository and join the reviewer list into single string object.
		
				articles.forEach(a -> {
					StringJoiner joiner = new StringJoiner(",");
					articleDao.getReviewers(a.getArticleId()).forEach(r -> {
						joiner.add(r.getFullName());
						
					});
					
					String reviewers = joiner.toString();
					a.setReviewers(reviewers);
				});	
			
			
		return articles;

	}
	public List<ArticleDto> getApprovedArticles() {
		List<ArticleDto> articles = articleDao.getApprovedArticles();
		
		// For each article, get categories from repository and join the category list into single string object.
		
		articles.forEach(a -> {
			StringJoiner joiner = new StringJoiner(",");
			catRepo.getAllCategories(a.getArticleId())
					.forEach(c -> {
			
									joiner.add(c.getCategory());
					});
			String cats = joiner.toString();
			a.setCategories(cats);
		});	
		
		// For each article, get reviewers from repository and join the reviewer list into single string object.
		
				articles.forEach(a -> {
					StringJoiner joiner = new StringJoiner(",");
					articleDao.getReviewers(a.getArticleId()).forEach(r -> {
						joiner.add(r.getFullName());
						
					});
					
					String reviewers = joiner.toString();
					a.setReviewers(reviewers);
				});	
			
			
		return articles;
	}
	// Get list of all Scheduled articles (scheduled to publish in some journal issues)
	public List<ArticleDto> getScheduledArticles() 
	{
		List<ArticleDto> articles = articleDao.getScheduledArticles();
		
		// For each article, get categories from repository and join the category list into single string object.
		
		articles.forEach(a -> {
			StringJoiner joiner = new StringJoiner(",");
			catRepo.getAllCategories(a.getArticleId())
					.forEach(c -> {
			
									joiner.add(c.getCategory());
					});
			String cats = joiner.toString();
			a.setCategories(cats);
		});	
		
		// For each article, get reviewers from repository and join the reviewer list into single string object.
		
				articles.forEach(a -> {
					StringJoiner joiner = new StringJoiner(",");
					articleDao.getReviewers(a.getArticleId()).forEach(r -> {
						joiner.add(r.getFullName());
						
					});
					
					String reviewers = joiner.toString();
					a.setReviewers(reviewers);
				});	
			
			
		return articles;
	}
	// Get list of all published or rejected Articles articles
	public List<ArticleDto> getArchivedArticles() 
	{
			// TODO Auto-generated method stub
		List<ArticleDto> articles =  articleRepo.getArchivedArticles();
		
		// For each article, get categories from repository and join the category list into single string object.
		
				articles.forEach(a -> {
					StringJoiner joiner = new StringJoiner(",");
					catRepo.getAllCategories(a.getArticleId())
							.forEach(c -> {
					
											joiner.add(c.getCategory());
							});
					String cats = joiner.toString();
					a.setCategories(cats);
				});	
				
				// For each article, get reviewers from repository and join the reviewer list into single string object.
				
						articles.forEach(a -> {
							StringJoiner joiner = new StringJoiner(",");
							articleDao.getReviewers(a.getArticleId()).forEach(r -> {
								joiner.add(r.getFullName());
								
							});
							
							String reviewers = joiner.toString();
							a.setReviewers(reviewers);
						});	
					
					
				return articles;
		
	}

/*
 * **************** end of submission view methods *********************	
 */
	public Article getArticleById(int id)
	{
		return articleRepo.findById(id).orElse(null);
	}
	public void sendFeedback(int article,String message) throws Exception
	{
		
		Article a = articleRepo.findById(article).orElse(null);
		
		if(a==null) {
			logger.error("Could not find article with given id {}",article);
			throw new Exception("Article with given id "+article+" not found.");
		}
		
		String authorid = a.getAuthorid();
		
		logger.info("Trying to send feedback to the author {} for article id: {}", authorid, article);
		
		
		
		
		AppUser user = userDetailsService.getUserByUsername(authorid);
		
		
		logger.info("Preparing email to notify author for feedbacks..");
		
		Mail mail = new Mail();

        
        mail.addTo(user.getEmail());
        mail.setSubject("Review feedback for your article "+a.getTopic());
        mail.setContent(message);
        
        try {
 			emailService.sendHtmlMessage(mail);
 			logger.info("E-mail sent to author {} for feedbacks..", user.getEmail());
 		} catch (MessagingException e) {
 			logger.error("E-mail Could not be sent to author {}", mail.getTo());
 			e.printStackTrace();
 		} catch (Exception e) {
 			logger.error("Error in sending email to author {}", mail.getTo());
			e.printStackTrace();
		}
        
        a.setStatus("feedback-sent");
        
        articleRepo.save(a);
        
        logger.info("Status of article updated in databse..");
	}
	public List<ArticleDto> getApprovedArticles(int journalId)
	{
		return articleRepo.getApprovedArticles(journalId);
	}
	public void updateArticle(int id, String topic, String articleAbstract, MultipartFile file,String uploadedBy)
	{
		
		logger.info("Trying to update article {}", id);

		Article article = getArticleById(id);
		String afilename = article.getFileName();
		
		article.setTopic(topic);
        article.setArticleAbstract(articleAbstract);
        article.setStatus("Re-submitted");
        article.setUploadDate(new java.sql.Date(System.currentTimeMillis())); 
        
                
        articleRepo.save(article);
        
        logger.info("Updated the article id {} information..", id);
        
		
		if(file != null && !file.isEmpty())
		{
			logger.info("Updating existing article id {} to new one uploaded by {}", id, uploadedBy);
			FileStorageService.deleteFile(path, afilename);
	        
	        FileStorageService.uploadFile(path, afilename, file);
	        
	        logger.info("Updated existing article id {} to new one uploaded by {}", id, uploadedBy);
	        
	        logger.info("Preparing to send email to all editors about article update..");
	        
	        
	        List<AppUser> editors = userDetailsService.getAllEditors();
	        
	        AppUser author = userDetailsService.getUserByUsername(uploadedBy);
	        
	        Mail mail = new Mail();
	        
	        editors.forEach(e -> mail.addTo(e.getEmail()));
	        
	        mail.setSubject("Article re-submited: "+topic);
	        mail.setContent("Hi Editor(s),"+
	       		 		 author.getFullName()+" has re-submitted new article titled "+topic+
	       		 		 ". Please review this article.");
	        
	        
	          try 
	          {
	 			emailService.sendHtmlMessage(mail,FileStorageService.multipartToFile(file,file.getOriginalFilename()));
	 			logger.info("Email sent to all editors");
	          } 
	          catch (MessagingException e) 
	          {
	     			logger.error("Could not send email to editors");
	     			e.printStackTrace();
	     		} 
	          catch (Exception e) 
	          {
	        	  logger.error("Error in sending email to editors");
					e.printStackTrace();
				}
			
		}
		
		else
		{
			logger.warn("No file is uploaded..Continuing without updating article file.");
		} 
        
        
        
	}
	public List<ArticleDto> getPendingArticles(String username) {
		
		List<ArticleDto> articles = articleRepo.getPendingArticles(username);
		
		// For each article, get categories from repository and join the category list into single string object.
		
		articles.forEach(a -> {
			StringJoiner joiner = new StringJoiner(",");
			catRepo.getAllCategories(a.getArticleId())
					.forEach(c -> {
			
									joiner.add(c.getCategory());
					});
			String cats = joiner.toString();
			a.setCategories(cats);
		});			
			
			
		return articles;
	}
	public void deleteArticle(int id, String  username) throws ArticleNotFoundException, UnauthorizedAccessException {
		// TODO Auto-generated method stub
		
		AppUser user = userDetailsService.getUserByUsername(username);
		
		
		
		if (user == null) 
		{
			logger.error("Unauthorized delete access detected for article by username {}",username);
			throw new UnauthorizedAccessException("Unauthorized delete access");			
		}
		
		String role = user.getRole();
		
		Article article = getArticleById(id);
		
		if (article == null) {
			logger.error("Invalid article id {} given to delete", id);
			// throw new exception
			throw new ArticleNotFoundException("Invalid article.");
			
		}
		
		if(role.equalsIgnoreCase("ROLE_AUTHOR"))
		{
			if(!article.getAuthorid().equals(username)) {
				logger.error("This user can not delete this article ");
				// throw new exception
				throw new UnauthorizedAccessException("Unauthorized access. You have no authority to delete article.");
			}
			
			if(article.getStatus().equalsIgnoreCase("Published")) {
				logger.error("Published article can not be deleted by author.");
				// throw new exception
				throw new UnauthorizedAccessException("Unauthorized access. You can not delete published article.");
			}
		}
		
		
				
		
		logger.info("Preparing to delete article id: {}", id);
		
		
		String fileName = article.getFileName();		
		FileStorageService.deleteFile(path, fileName);	
		
		article.removeAllCategories();
		article.removeAllReviewer();
		
		articleRepo.deleteById(id);
		
		logger.info("Successfully deleted the article id {}", id);		
		
	}
	public void uploadPDFArticle(Integer id, MultipartFile file) throws Exception {
		// TODO Auto-generated method stub
		logger.info("Preparing to upload PDF version of article id {}",id);
		
		Article article = getArticleById(id);
		
		if(article == null)
		{
			logger.error("Could not find article id {}", id);
			throw new Exception("Invalid Article ID");
			
		}
		
		if (file == null)
		{
			logger.error("Uploaded file is empty or null");
			throw new Exception("PDF file should be uploaded");
			
		}
		
		String fileMime = file.getContentType();
		// Validate PDF Content type here
		if(!fileMime.equalsIgnoreCase(pdfFileMime))
		{
			logger.error("Uploaded file should be valid PDF document");
			throw new Exception("Uploaded file should be valid PDF document");
		}
		
		
		String afilename = article.getFileName();		
		String articleFileName = "Article_"+id+"."+FilenameUtils.getExtension(file.getOriginalFilename()); 
		
		// If article is not yet published then change its status. Other wise retain status as published.
		if(!article.getStatus().equalsIgnoreCase("Published"))
			article.setStatus("Updated to PDF");
        
        article.setFileName(articleFileName);

        article.setUploadDate(new java.sql.Date(System.currentTimeMillis()));
        
        FileStorageService.deleteFile(path, afilename);
        
        FileStorageService.uploadFile(path, articleFileName, file);
        
        logger.info("Updated article {} saved in file system path {}", articleFileName, path);
        
        article.setPageCount(FileStorageService.getPageCount(path, articleFileName));
        
        articleRepo.save(article);
        
        logger.info("Updated article info saved.");      
		
	}
	public void reject(int article,String message) throws Exception {
		
		Article a = articleRepo.findById(article).orElse(null);
		
		if(a==null) {
			logger.error("Could not find article with given id {}",article);
			throw new Exception("Article with given id "+article+" not found.");
		}
		
		String authorid = a.getAuthorid();
		
		logger.info("Trying to send reject message to the author {} for article id: {}", authorid, article);
		
		
		
		
		AppUser user = userDetailsService.getUserByUsername(authorid);
		
		
				
		logger.info("Preparing to reject the article id {}", article);
		
		
		
		if (user == null)
		{
			logger.error("Could not find author id {}", authorid);
			throw new Exception("Author could not be found");
		}
		
		logger.info("Preparing to send email to author {}", user.getEmail());
		
		Mail mail = new Mail();        
        mail.addTo(user.getEmail());
        mail.setSubject("Your article has been returned: "+a.getTopic());
        mail.setContent(message);
        
        try {
 			emailService.sendHtmlMessage(mail);
 			logger.info("Email sent to author {} about article {} rejection.", user.getEmail(), a.getTopic());
 		} catch (MessagingException e) {
 			logger.error("Could not send email to author {} for article rejection.", user.getEmail());
 			e.printStackTrace();
 		} catch (Exception e) {
			logger.error("Error in sending email to author {} for article rejection.", user.getEmail());
			e.printStackTrace();
		}
        
        a.setStatus("rejected");
        
        articleRepo.save(a);
        
        logger.info("Article {} is rejected", a.getTopic());
		
	}
	// Get list of all published articles in given journal issue 
	public List<ArticleDto> getPublishedArticles(int jiid) {
		// TODO Auto-generated method stub
		return articleRepo.getPublishedArticles(jiid);
	}
	public List<ArticleDto> getEditorPickArticles() {
		
		return articleRepo.getFavoriteArticles();
		
	}
	public ArticleDto getPublishedArticleById(Integer id) throws ArticleNotFoundException
	{
		ArticleDto adto = articleRepo.getPublishedArticle(id);
		
		if (adto == null)
		{
			logger.error("Article with id: {} is not found",id);
			throw new ArticleNotFoundException("Article is not available");
		}		
		
		return adto;
		
	}
	public void incrementViewCounter(int aid) {
		Article a = articleRepo.getOne(aid);
		
		if(a==null)
		{
			logger.error("Article entity with id {} is not found",aid);
			return;
		}
		Integer v = a.getViewCount();
		
		if (v == null)
			v=0;
		
		a.setViewCount(v+1);
		
		articleRepo.saveAndFlush(a);
		
		logger.info("View count for {} is incremented",aid);
		
	}
	public void incrementDownloadCounter(int aid) {
		Article a = articleRepo.getOne(aid);
		
		a.setDownloadCount(a.getDownloadCount()+1);
		
		articleRepo.saveAndFlush(a);
		
		logger.info("Download count for {} is incremented",aid);
		
	}
	public List<Category> getAllCategories() {
		
		return catRepo.findAll();
		
	}
	public void saveArticle(ArticleUploadForm articleUploadForm, String username) throws InvalidCategoryException {
		// Service method to save article submitted by author
		
		String topic = articleUploadForm.getTopic();
		String articleAbstract = articleUploadForm.getArticleAbstract();
		String uploadedBy = username;
		MultipartFile file = articleUploadForm.getFileData();
		String otherAuthors = articleUploadForm.getOtherauthors();
		
		Long[] cats = articleUploadForm.getCategories();
		
		List<Category> catlist = catRepo.findAllById(Arrays.asList(cats));
		
		if(catlist == null || catlist.isEmpty())
		{
			logger.error("Category list is not provided");
			throw new InvalidCategoryException("Invalid category list is provided..");
		}
			
		
		Set<Category> catset = new HashSet<>(catlist);
		
		logger.info("Trying to save new article titled {} submitted by author {}.", topic, uploadedBy);
		
		logger.info("Categories selected "+cats);
		
		
		
        // Article Object
        Article article = new Article();
        article.setTopic(topic);
        article.setArticleAbstract(articleAbstract);
        article.setStatus("Submitted");
        article.setAuthorid(uploadedBy);
        article.setUploadedBy(uploadedBy);
        article.setCategories(catset);
       // article.setFileName(fileName);
        article.setOtherAuthors(otherAuthors);
        article.setUploadDate(new java.sql.Date(System.currentTimeMillis()));
        article.setIsFavorite(false); // By default, all submitted articles are non-favorite.
        
        
        logger.trace("Article object prepared to save at database.");
        
        Article a = articleRepo.save(article);
        
        int id = a.getId();
        
        logger.trace("Article object saved to database with id: {}", id);
        
        String articleFileName = "Article_"+id+"."+FilenameUtils.getExtension(file.getOriginalFilename());
        
        logger.info("Article will be saved at {} with file name {}", path, articleFileName);
        
        FileStorageService.uploadFile(path, articleFileName, file);
        
        logger.info("Article saved at {} with file name {}", path, articleFileName);
        
        logger.trace("Updating article file name in database..");
        
        article.setFileName(articleFileName);
        
        articleRepo.save(article);
        
        logger.trace("Article filename is updated in database.");  
        
        // pull the list of all executive editors from user table.  Executive editors are those, who
//        have been registered in user table with role EDITOR.
        
        List<AppUser> editors = userDetailsService.getAllEditors();
        
        AppUser author = userDetailsService.getUserByUsername(uploadedBy);
        
        logger.info("Preparing email to notify all executive editors..");
        
        Mail mail = new Mail();
        
        editors.forEach(u -> mail.addTo(u.getEmail()));
        
        mail.setSubject("Article submitted: "+a.getTopic());
        
        mail.setContent("Hi Editors,"+
       		 		 author.getFullName()+" has uploaded new article titled "+a.getTopic()+
       		 		 ". Please review this article.");
        
        try {
 			emailService.sendHtmlMessage(mail,FileStorageService.multipartToFile(file,file.getOriginalFilename()));
 			logger.info("E-mail sent to all editors notifying article upload..");
 			
 		} catch (MessagingException e) {
 			logger.error("E-mail Could not be sent to {}", mail.getTo());
 			
 		} catch (Exception e) {
 			logger.error("Error in sending email.");
			e.printStackTrace();
		}
		
	}
	public void updateArticle(int id, ArticleUploadForm articleUploadForm, String username) throws ArticleNotFoundException, UnauthorizedAccessException, InvalidCategoryException {
		// Update the given article
		
		Article article = getArticleById(id);
		
		AppUser user = userDetailsService.getUserByUsername(username);
		
		if (user == null) {
			logger.error("Unauthorized delete access detected for article by username {}",username);
			// throw new exception
			throw new UnauthorizedAccessException("Unauthorized access. You have no authority to update.");
		}
		
		if (article == null) {
			logger.error("Invalid article id {} given to delete", id);
			// throw new exception
			throw new ArticleNotFoundException("Invalid article.");
			
		}
		
		if(!article.getAuthorid().equals(username)) {
			logger.error("This user can not delete this article ");
			// throw new exception
			throw new UnauthorizedAccessException("Unauthorized access. You have no authority to update this article.");
		}
		
		if(article.getStatus().equalsIgnoreCase("Published")) {
			logger.error("Published article can not be deleted by author.");
			// throw new exception
			throw new UnauthorizedAccessException("Unauthorized access. You can not update this article.");
		}
		
		// prepare fields for update
		
		String topic = articleUploadForm.getTopic();
		String articleAbstract = articleUploadForm.getArticleAbstract();
		String uploadedBy = username;
		MultipartFile file = articleUploadForm.getFileData();
		String otherAuthors = articleUploadForm.getOtherauthors();
		
		Long[] cats = articleUploadForm.getCategories();
		
		List<Category> catlist = catRepo.findAllById(Arrays.asList(cats));
		
		if(catlist == null || catlist.isEmpty())
		{
			logger.error("Category list is not valid");
			throw new InvalidCategoryException("Invalid category list is provided..");
		}
			
		
		Set<Category> catset = new HashSet<>(catlist);
		
		logger.info("Trying to save new article titled {} submitted by author {}.", topic, uploadedBy);
		
		logger.info("Categories selected "+cats);
		
		logger.info("Trying to update article {}", id);

		// update the article object with new information
		
		String afilename = article.getFileName();
		
		article.setTopic(topic);
        article.setArticleAbstract(articleAbstract);
        article.setStatus("Re-submitted");
        article.setUploadDate(new java.sql.Date(System.currentTimeMillis())); 
        article.setCategories(catset);
        article.setOtherAuthors(otherAuthors);
        article.setUploadedBy(uploadedBy);        
                
        articleRepo.save(article);
        
        logger.info("Updated the article id {} information..", id);
        
		
		if(!file.isEmpty())
		{
			logger.info("Updating existing article id {} to new one uploaded by {}", id, uploadedBy);
			FileStorageService.deleteFile(path, afilename);
	        
	        FileStorageService.uploadFile(path, afilename, file);
	        
	        logger.info("Updated existing article id {} to new one uploaded by {}", id, uploadedBy);
	        
	        logger.info("Preparing to send email to all editors about article update..");
	        
	        
	        List<AppUser> editors = userDetailsService.getAllEditors();
	        
	        AppUser author = userDetailsService.getUserByUsername(uploadedBy);
	        
	        Mail mail = new Mail();
	        
	        editors.forEach(e -> mail.addTo(e.getEmail()));
	        
	        mail.setSubject("Article re-submited: "+topic);
	        mail.setContent("Hi Editor(s),"+
	       		 		 author.getFullName()+" has re-submitted new article titled "+topic+
	       		 		 ". Please review this article.");
	        
	        
	          try 
	          {
	 			emailService.sendHtmlMessage(mail,FileStorageService.multipartToFile(file,file.getOriginalFilename()));
	 			logger.info("Email sent to all editors");
	          } 
	          catch (MessagingException e) 
	          {
	     			logger.error("Could not send email to editors");
	     			e.printStackTrace();
	     		} 
	          catch (Exception e) 
	          {
	        	  logger.error("Error in sending email to editors");
					e.printStackTrace();
				}
			
		}
		
		else
		{
			logger.warn("No file is uploaded..Continuing without updating article file.");
		}		
		
	}
	public List<ArticleDto> getPublishedArticleByUser(String username) {
		// TODO Gets all published articles by the username
		
		List<ArticleDto> articles = articleRepo.getPublishedArticles(username);
		
		// For each article, get categories from repository and join the category list into single string object.
		
		articles.forEach(a -> {
			StringJoiner joiner = new StringJoiner(",");
			catRepo.getAllCategories(a.getArticleId())
					.forEach(c -> {
			
									joiner.add(c.getCategory());
					});
			String cats = joiner.toString();
			a.setCategories(cats);
		});			
			
			
		return articles;
	}


	
	/**
	 * This method will find all articles on given sections such as : Editorial, Articles, Experiments, etc, which are ready to
	 * publish
	 * @param id
	 * @return All the ready to publish articles on given section (section id)
	 */
	public List<ArticleDto> getAllScheduledArticles(Integer issueid, Long sectionid) 
	{
		return articleDao.getScheduledArticlesBySection(issueid, sectionid);
	}


	public void updateArticleInfo(int article, ArticleInfoUpdateForm articleInfoUpdateForm) throws Exception 
	{
		Article a = getArticleById(article);
		
				
		if (a == null) {
			logger.error("Invalid article id {} given to update", article);
			// throw new exception
			throw new Exception("Invalid article.");
			
		}
		
		Long[] cats = articleInfoUpdateForm.getCategories();
		
		List<Category> catlist = catRepo.findAllById(Arrays.asList(cats));
		
		if(catlist == null || catlist.isEmpty())
		{
			logger.error("Category list is not valid");
			throw new Exception("Invalid category list is provided..");
		}
		
		Long[] reviewer = articleInfoUpdateForm.getReviewers();
		
		List<Person> reviewerlist = personRepo.findAllById(Arrays.asList(reviewer));
		
		if(reviewerlist == null || reviewerlist.isEmpty())
		{
			logger.error("Reviewer list is not valid");
			throw new Exception("Invalid Reviewer list is provided..");
		}
			
		
		Set<Category> catset = new HashSet<>(catlist);
		Set<Person> reviewerSet = new HashSet<>(reviewerlist);
		
		a.setTopic(articleInfoUpdateForm.getTopic());
		a.setCategories(catset);
		a.setReviewers(reviewerSet);
		a.setOtherAuthors(articleInfoUpdateForm.getOtherauthors());
		
		articleRepo.save(a);
		
		logger.info("Article Info updated..");
		
	}


	public void unapproveArticle(int article) throws Exception 
	{
		Article a = articleRepo.findById(article).orElse(null);
		
		if (a == null) 
		{
			
			logger.error("Article not found for id {}",article);
			throw new Exception("Article not found for given id "+article);
		}
		
		
		a.setStatus("Unapproved");
		
		articleRepo.save(a);
		
	}


	public void setFavorite(int article) throws Exception 
	{
		
		Article a = articleRepo.findById(article).orElse(null);
		
		if (a == null) 
		{
			
			logger.error("Article not found for id {}",article);
			throw new Exception("Article not found for given id "+article);
		}
		
		a.setIsFavorite(true);
		
		articleRepo.save(a);
	}
	
	public void unpublishArticle(int article) throws Exception 
	{
		Article a = articleRepo.findById(article).orElse(null);
		
		if (a == null) 
		{
			
			logger.error("Article not found for id {}",article);
			throw new Exception("Article not found for given id "+article);
		}
		
		
		a.setStatus("Unpublished");
		
		articleRepo.save(a);
		
	}

}
