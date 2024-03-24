package com.example.projectx.service;

import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.projectx.dao.IGenericDao;
import com.example.projectx.dao.JournalDao;
import com.example.projectx.dto.JournalDropDownDto;
import com.example.projectx.dto.JournalIssueDto;
import com.example.projectx.dto.PreparedJournalDto;
import com.example.projectx.dto.PublishedJournalDto;
import com.example.projectx.form.JournalForm;
import com.example.projectx.form.NewJournalIssueForm;
import com.example.projectx.form.UpdateJournalIssueForm;
import com.example.projectx.mail.EmailService;
import com.example.projectx.mail.Mail;
import com.example.projectx.model.AppUser;
import com.example.projectx.model.Article;
import com.example.projectx.model.Journal;
import com.example.projectx.model.JournalIssue;
import com.example.projectx.model.JournalSection;
import com.example.projectx.repository.ArticleRepository;
import com.example.projectx.repository.JournalIssueRepository;
import com.example.projectx.repository.JournalRepository;

@Service
public class JournalService {
	
	@Value("${upload.path.journal}")
    private String path;
	
	@Value("${upload.path.coverimage}")
	private String coverpage;
	
	@Value("${upload.path.article}")
	private String articlePath;
	
	@Autowired
    private EmailService emailService;
	
	@Autowired
	private JournalIssueRepository journalIssueRepository;
	
	@Autowired
	private JournalRepository journalRepo;
	
	@Autowired
	private ArticleRepository articleRepo;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	JournalDao journalDao;
	
	
	private IGenericDao<JournalSection> dao;

   @Autowired
   public void setDao(IGenericDao<JournalSection> daoToSet) {
	   
	   logger.info("*****************Section Dao constructor called ******************");
	   dao = daoToSet;
	   
   }
	
	private static final Logger logger = LoggerFactory.getLogger(JournalService.class);
	
	private static final List<String> journalCoverContentTypes = Arrays.asList("image/png", "image/jpeg", "image/gif");
	
	private static final String pdfFileMime = "application/pdf";
	
	
	
	public List<JournalIssue> getAllJournalIssues(int jid)
	{
		return journalDao.getAllJournalIssues(jid);
	}
	
	public List<JournalIssue> getAllPublishedJournalIssues(int jid)
	{
		return journalDao.getAllPublishedJournalIssues(jid);
	}
	
	public List<JournalIssue> getDraftJournalIssues(int jid)
	{
		return journalDao.getDraftJournalIssues(jid);
	}
	
	public Map<Integer,byte[]> getAllJournalCoverImage()
	{
		Map<Integer,byte[]> coverPageMap = new HashMap<Integer,byte[]>();
		
		List<Journal> journals = journalDao.getAllJournals();
		
		if(journals != null)
		{
			logger.trace("Journals found. Getting profile pictures of all Journals.");
			journals.forEach(j -> coverPageMap.put(j.getId(), getJournalCoverPage(j.getId())));
			
		}
		else
		{
			logger.error("Journals not found");
		}
		
		
		return coverPageMap;
	}
	
	public Map<Integer,byte[]> getAllJournalIssueCoverImage()
	{
		Map<Integer,byte[]> coverPageMap = new HashMap<Integer,byte[]>();
		
		List<Journal> journals = journalDao.getAllJournals();;
		
		if(journals != null)
		{
			logger.trace("Journals found. Getting Cover Image of all Journals issues.");
			journals.forEach( j -> {
				List<JournalIssue> issues = journalDao.getAllJournalIssues(j.getId());
				if(issues == null || issues.size()==0)
					logger.warn("No Issues found for journal id : {}", j.getId());
				else
				{
					issues.forEach( i -> {
						coverPageMap.put(i.getId(), getJournalIssueCoverPage(i.getId()));
					});
				}
				
			});			
			
		}
		else
		{
			logger.error("Journals not found");
		}
		
		
		return coverPageMap;
	}

	
	private byte[] getJournalCoverPage(int jid)
	{
		String fileName=journalDao.getJournalCoverPageFileName(jid);
		String absoultePath = coverpage + fileName;
		
		byte[] bytes=null;
		
		File f = new File(absoultePath);
		
		if(f.exists())
		{
			
			try {
				bytes =  Files.readAllBytes(f.toPath());
			} catch (IOException e) {
				logger.error("Could not read bytes from cover image {}", absoultePath);
				e.printStackTrace();
			}
		}
		else {
			logger.error("Cover Page File {} does not exist", absoultePath);
		}
		
		return bytes;
	}
	
	private byte[] getJournalIssueCoverPage(int jid)
	{
		String fileName=journalDao.getJournalIssueCoverPageFileName(jid);
		String absoultePath = coverpage + fileName;
		
		byte[] bytes=null;
		
		File f = new File(absoultePath);
		
		if(f.exists())
		{
			
			try {
				bytes =  Files.readAllBytes(f.toPath());
			} catch (IOException e) {
				logger.error("Could not read bytes from cover image {}", absoultePath);
				e.printStackTrace();
			}
		}
		else {
			logger.error("Cover Page File {} does not exist", absoultePath);
		}
		
		return bytes;
	}
	
	
	
	public String getCoverPageLocation()
	{
		return coverpage;
		
	}
	
	public void updateJournalCoverPage(int jid, MultipartFile file) throws Exception
	{
		logger.info("Trying to update cover Page of journal id {}",jid);
		
		if (file == null || file.isEmpty())
		{
			logger.error("Cover Page is not uploaded. returning without cover page update");
			throw new Exception("Cover page should be uploaded!!");
		}
		
		Journal journal = null;
		
		try {
			journal = journalRepo.getOne(jid);	
			
		}
		catch (EntityNotFoundException e) {
			logger.error("Invalid Journal");
			throw new Exception("Invalid Journal!!");
		}
		catch (Exception e) {
			logger.error("Invalid Journal");
			throw new Exception("Invalid Journal!!");
		}
		
		if (journal == null) {
			logger.error("Invalid Journal");
			throw new Exception("Invalid Journal!!");
		}
		
		String fileContentType = file.getContentType();
		
		if (journalCoverContentTypes.contains(fileContentType))
		{
			String coverPage = journal.getCoverImageFileName();		
			String fileName = "Journal_Cover_"+journal.getId()+"."+FilenameUtils.getExtension(file.getOriginalFilename());
			
			logger.info("Trying to store new cover page replacing old one");
			
			if (coverPage != null)
				FileStorageService.deleteFile(coverpage, coverPage);
			
			FileStorageService.uploadFile(coverpage,fileName, file);
			
			logger.info("Successfully replaced cover image");
			
			journal.setCoverImageFileName(fileName);
			
			journalRepo.save(journal);
			
			logger.info("Journal Entity saved");
		}
		else {
			logger.error("Journal Cover picture content type is not acceptable.");
			throw new Exception("Journal Cover picture content type is not acceptable.");
		}
		
		
	}
		
	public List<JournalDropDownDto> getJournalIssueDropdown(int jid)
	{
		
		List<JournalIssue> journals = journalDao.getAllJournalIssues(jid);
		
		List<JournalDropDownDto> list = new ArrayList<JournalDropDownDto>();
		
		if(journals != null)
		{
			journals.forEach(j -> {
				JournalDropDownDto jdto = new JournalDropDownDto();
				jdto.setJournalId(j.getId());
				jdto.setJournalText(j.getMonth()+"-"+j.getYear()+"(Volume# "+j.getVolumeNum()+", Issue# "+j.getIssueNum()+")");
				list.add(jdto);
			});
			
			logger.info("Journal Issue drop down list prepared");
		}
		else
		{
			logger.error("No Journal issue found for journal id: {}", jid);
		}
		
		
		return list;
		
	}
	
	public List<PreparedJournalDto> getPreparedJournalIssues(int jid)
	{
		List<JournalIssue> journals = journalDao.getAllPreparedJournalIssues(jid);
		
		List<PreparedJournalDto> list = new ArrayList<PreparedJournalDto>();
		
		if(journals != null)
		{
			journals.forEach(j -> {
				PreparedJournalDto jdto = new PreparedJournalDto();
				jdto.setJournalId(j.getId());
				jdto.setTitle(j.getJournal().getJournalTopic());
				jdto.setIssue(j.getIssueNum());
				jdto.setVolume(j.getVolumeNum());
				jdto.setYear(j.getYear());
				jdto.setMonth(j.getMonth());
				//jdto.setFileName(j.getJournalFileName());
				list.add(jdto);
			});
			logger.info("All Prepared Journal Issue drop down list prepared.");
		}
		else
		{
			logger.error("No Prepared Journal issue found for journal id: {}", jid);
		}
		
		
		return list;
		
	}
	public void addArticle(JournalIssue j, Article a)
	{
		j.addArticle(a);
		logger.info("Article {} is added to journal issue {}",a.getTopic(), j.getId());
	}

	public void publish(int issueid) throws Exception {
		
		logger.info("Trying to publish journal issue {}",issueid);
		
		JournalIssue issue = journalIssueRepository.findById(issueid).orElse(null);
		
		// Validation steps
		
		// 1. Check if issue id is valid (it is valid if it is in database)
		if(issue == null)
		{
			logger.error("Journal issue {} is not found", issueid);
			throw new Exception ("Invalid Journal Issue");
		}
		
		//2. Check the status of this issue. If it is already published, throw error message
		
		if(issue.getStatus().equalsIgnoreCase("published"))
		{
			logger.error("This issue {} is already published");
			throw new Exception ("This issue is already in published status");
		}
		
		Set<Article> sa  = issue.getArticles();
		
		//3. Check if there are articles assigned to this issue
		if(sa == null || sa.isEmpty())
		{
			logger.error("There are no articles assigned to this issue");
			throw new Exception("There are no articles assigned to this issue");
		}
		
		//4. Check if every articles in the issue are converted to PDF
		
		long count = sa.stream()
					  .filter(a->!(a.isPDFUploaded()))
					  .count();
		
		if(count>0)
		{
			logger.error("There are {} articles which are not updated to PDF", count);
			throw new Exception("There are "+count+" articles, which are not updated to PDF.");
		}
		
		//5. Check if there is at least 1 Editorial section
		
		
		Article editorial = sa.stream().filter(a -> a.getJournalsection().getSectionName().toLowerCase().contains("editorial"))
					.findFirst().orElse(null);
		
		if(editorial == null)
		{
			logger.error("There is no article assigned to Editorial section");
			throw new Exception("There is no article assigned to Editorial section");
		}
		
		//6. Check TOC order of each article
		
		
		long tocFailCount = sa.stream()
				  .filter(a-> a.getTocOrder() == null || a.getTocOrder() == 0)
				  .count();
		
		if(tocFailCount>0)
		{
			logger.error("There are {} articles for which TOC order is not assigned", tocFailCount);
			throw new Exception("There are "+tocFailCount+" article for which TOC order is not assigned.");
		}
		
		//7. Check for uniqueness of Toc order for each item
		
		List<Integer> orders = sa.stream()
								 .map(t -> t.getTocOrder())
								 .collect(Collectors.toList());
		
		int totalArticles = sa.size();
		long totaldistinctToc = orders.stream().distinct().count();
		
		if(totalArticles > totaldistinctToc)
		{
			logger.error("Toc order is not unique for every items");
			throw new Exception("Toc order is not unique for every items");
		}
		
		//8. Check if each article is populated with number of pages
		long numPage = sa.stream()
				  .filter(a-> a.getPageCount() == null || a.getPageCount() == 0)
				  .count();
		
		if(numPage>0)
		{
			logger.error("There are {} articles for which Number of page is null.", numPage);
			throw new Exception("There are "+numPage+" for which Number of page is null.");
		}
		
		// Validation completed
		
		
		issue.setStatus("Published");
		issue.setPublishDate(new java.sql.Date(System.currentTimeMillis()));
		
		journalIssueRepository.save(issue);
		
		logger.info("Status of Journal issue is set as 'Published'");
		
		
		
		
		
		for(Article a: sa)
		{
			a.setStatus("Published");
			// save article
            articleRepo.save(a);
            
            logger.info("Status of article {} set as 'Published'", a.getId());
            
			AppUser user = userDetailsService.getUserByUsername(a.getAuthorid());
			
			logger.info("Trying to send email to all the article authors");
			
			// send email to the author
			
			Mail mail = new Mail();

            
            mail.addTo(user.getEmail());
            mail.setSubject("Article "+a.getTopic()+" is published");
            mail.setContent("Dear "+user.getFullName()+",<br>"
            		+ "Congratulations!! Your article has been published. "
            		+ " Regards,<br>"
            		+ "Editorial Borad<br>"
            		+ issue.getJournal().getJournalTopic());
            
            try {
     			emailService.sendHtmlMessage(mail);
     			logger.info("Email sent to the author {} about article publish.", user.getEmail());
     		} catch (MessagingException e) {
     			logger.error("Could not send email to author {} about article publish.", user.getEmail());
     			e.printStackTrace();
     		} catch (Exception e) {
				logger.error("Error in sending email to author {}", user.getEmail());
				e.printStackTrace();
			}         
            
			
		}		
		
	}


	public void prepare(int journalId, MultipartFile editorial) {
		
		logger.info("Preparing journal issue {} for publishing.", journalId);
		
		JournalIssue journal = journalIssueRepository.getOne(journalId);
		
		if (editorial == null || editorial.isEmpty())
		{
			logger.error("Editorial file is not uploaded or empty. Returing without preparing.");
			return;
		}
		
		String editorialFileName = "Editorial_"+journal.getId()+"."+FilenameUtils.getExtension(editorial.getOriginalFilename());
		
//		journal.setEditorialFileName(editorialFileName);
		
		FileStorageService.uploadFile(path, editorialFileName,editorial);
		
//		journal.setEditorialpageCount(FileStorageService.getPageCount(path, editorialFileName));

		journal.setStatus("Prepared");
		
		journalIssueRepository.save(journal);
		
		
		logger.info("Journal Issue {} is prepared for publishing.", journalId);
		
		
	}

	public Journal getJournalById(int id) {
		// TODO Auto-generated method stub
		Journal journal = journalRepo.getOne(id);
		
		
		return journal;
	}
	
	/**
	 * 
	 * @param id
	 * @return journalIssue object
	 * If entity with id @param id is not found, it returns null
	 */
	public JournalIssue getJournalIssueById(int id) {
		
		JournalIssue journal = journalIssueRepository.findById(id).orElse(null);		
		return journal;
	}

	public List<Journal> getAllJournals() {
		return journalDao.getAllJournals();
	}

	public void createJournal(String title, MultipartFile coverPage) 
	{
		logger.info("Trying to create new journal with title: {}", title);
		Journal journal = new Journal();
		
		journal.setJournalTopic(title);
		
		Journal j = journalRepo.save(journal);
		String fileName = "Journal_Cover_"+journal.getId()+"."+FilenameUtils.getExtension(coverPage.getOriginalFilename());
		
		if (coverPage == null || coverPage.isEmpty()) {
			logger.error("No cover page uploaded. Continuing without cover page.");
		}
		else
		{
			FileStorageService.uploadFile(coverpage,fileName, coverPage);
			
			j.setCoverImageFileName(fileName);
		}		
		
		journalRepo.save(j);
		
		logger.info("Created new journal with title: {}", title);
	}

	

	public byte[] getJournalCoverImage(int id) {
		byte[] bytes = getJournalCoverPage(id);
		return bytes;
	
	}
	public byte[] getJournalIssueCoverImage(int id) {
		byte[] bytes = getJournalIssueCoverPage(id);
		return bytes;
	}

	public List<PublishedJournalDto> getAllPublishedJournals() {
		
		List<PublishedJournalDto> list = journalDao.getPublishedJournals();

		if(list == null)
		{
			logger.info("No published journals");
		}
		else
		{
			logger.info("Total Published journals {}",list.size());
		}
		
		return list;
	}

	public JournalIssue getCurrentJournalIssue(int jid) {
		List<JournalIssue> issues = journalDao.getAllPublishedJournalIssues(jid);
		if(issues != null)
		{
			issues.sort(Comparator.comparing(JournalIssue::getPublishDate).reversed());
			return issues.get(0);
		}
		else {
			logger.error("No Published journal issue exists");
			return null;
		}		
		
	}

	public void updateJournal(JournalForm journalForm) throws Exception {
			Integer id = journalForm.getId();
			
			logger.info("Trying to update topic of journal id: {}", id);
			Journal journal = journalRepo.findById(id).orElse(null);
			
			if (journal == null) {
				logger.error("Journal with id {} is not found!",id);
				throw new Exception("Invalid Journal");
			}
			
			journal.setJournalTopic(journalForm.getJournalTopic());
			journal.setPublisher(journalForm.getPublisher());
			journal.setOnlineissn(journalForm.getOnlineissn());
			journal.setPrintissn(journalForm.getPrintissn());
					
			journalRepo.save(journal);
			
			logger.info("Journal Info with id {} is updated", id);
	}

	public List<JournalSection> getAllSections() {
		
		return dao.findAll(JournalSection.class);
	}

	public List<JournalIssueDto> getAllFutureIssues() {
		
		return journalDao.AllDraftIssues();
		
	}
	public List<JournalIssueDto> getAllBackIssues() {
		return journalDao.AllBackIssues();
	}

	public void createJournalIssue(NewJournalIssueForm newJournalIssueForm) throws Exception 
	{
		String volume = newJournalIssueForm.getVolume();
		String issue = newJournalIssueForm.getIssue();
		String year = newJournalIssueForm.getYear();
		String month = newJournalIssueForm.getMonth();
		Boolean shouldUpload = newJournalIssueForm.getUploadCover();
		
		Integer jid = newJournalIssueForm.getJournalId();
		MultipartFile coverPage = newJournalIssueForm.getCoverPage();
		
		if(jid==null)
		{
			logger.error("Journal ID is NULL. Can not create new journal issue");
			throw new Exception("Journal ID is NULL. Can not create new journal issue");
		}
		
		Journal journal = journalRepo.findById(jid).orElse(null);	
		
		if(journal==null)
		{
			logger.error("Journal ID is invalid");
			throw new Exception("Journal ID is invalid");
		}
				
		logger.info("Trying to create new journal issue (Volume: {}, Issue: {}) for journal id: {}",volume, issue, jid);

		// JournalIssue Object
		JournalIssue journalIssue = new JournalIssue();		
		
		
		journalIssue.setIssueNum(issue);
		journalIssue.setVolumeNum(volume);
		journalIssue.setYear(year);
		journalIssue.setMonth(month);
		
//		journalIssue.setCreatedDate();
		
		journalIssue.setStatus("created");
		
		journalIssue.setJournal(journal);
        
		JournalIssue jissue = journalIssueRepository.save(journalIssue);
		
		if(shouldUpload.booleanValue()) {
			if(coverPage == null || coverPage.isEmpty())
			{
				logger.error("Cover Page for new issue should be provided");
				throw new Exception("Cover Page for new issue should be provided");
			}
			else
				updateJournalIssueCoverPage(jissue.getId(),coverPage);
		}
		
		
		
		logger.info("Created new journal issue (Volume: {}, Issue: {}) for journal id: {}",volume, issue, jid);		
		
		
	}
	public void updateJournalIssueCoverPage(int jiid, MultipartFile file) throws Exception
	{
		logger.info("Trying to update cover Page of journal issue id {}",jiid);
		
		if (file == null || file.isEmpty())
		{
			logger.error("Cover Page is not uploaded. returning without cover page update");
			throw new Exception("Cover page should be uploaded!!");
		}
		
		JournalIssue issue = null;
		
		try {
			issue = journalIssueRepository.getOne(jiid);	
			
		}
		catch (EntityNotFoundException e) {
			logger.error("Invalid Journal Issue");
			throw new Exception("Invalid Journal Issue!!");
		}
		catch (Exception e) {
			logger.error("Invalid Journal Issue");
			throw new Exception("Invalid Journal Issue!!");
		}
		
		if (issue == null) {
			logger.error("Invalid Journal Issue");
			throw new Exception("Invalid Journal Issue!!");
		}
		
		String fileContentType = file.getContentType();
		
		if (journalCoverContentTypes.contains(fileContentType))
		{
			String coverPage = issue.getCoverImageFileName();	
			
			String fileName = "JournalIssue_Cover_"+issue.getId()+"."+FilenameUtils.getExtension(file.getOriginalFilename());
			
			logger.info("Trying to store new cover page replacing old one");
			
			if (coverPage != null)
				FileStorageService.deleteFile(coverpage, coverPage);
			
			FileStorageService.uploadFile(coverpage,fileName, file);
			
			logger.info("Successfully replaced cover image");
			
			issue.setCoverImageFileName(fileName);
			
			journalIssueRepository.save(issue);
			
			logger.info("Journal Issue Entity saved");
		}
		else {
			logger.error("Journal Issue Cover picture content type is not acceptable.");
			throw new Exception("Journal Issue Cover picture content type is not acceptable. Upload cover page as .jpg, .png or .gif format.");
		}
		
		
	}

	public void deleteJournalIssue(int jid) throws Exception 
	{
		
		JournalIssue issue = null;
		
		try {
			issue = journalIssueRepository.getOne(jid);	
			
			// find all articles scheduled to this issue and change their status to ''Unscheduled'
			
			issue.getArticles().forEach(a -> {
				a.setStatus("Unscheduled");
				a.setJournalissue(null);
				a.setJournalsection(null);
				a.setTocOrder(null);
			});
			
			
			
			// remove all articles scheduled to this issue
			issue.removeAllArticles();
			
			String coverPage = issue.getCoverImageFileName();		
			
			if (coverPage != null)
				FileStorageService.deleteFile(coverpage, coverPage);
			
			journalIssueRepository.delete(issue);
			
		}
		catch (EntityNotFoundException e) {
			logger.error("Invalid Journal Issue");
			throw new Exception("Invalid Journal Issue!!");
		}
		catch (Exception e) {
			logger.error("Invalid Journal Issue");
			throw new Exception("Invalid Journal Issue!!");
		}		
	}

	public void updateJournalIssue(UpdateJournalIssueForm updateJournalIssueForm) throws Exception 
	{
			Integer jiid = updateJournalIssueForm.getJournalIssueId();
			
			JournalIssue issue = journalIssueRepository.findById(jiid).orElse(null);
			
			if (issue == null) {
				logger.error("Journal Issue with id {} is not found!",jiid);
				throw new Exception("Invalid Journal Issue");
			}
			
			logger.info("Trying to update journal issue {}",jiid);
					
			
			issue.setIssueNum(updateJournalIssueForm.getIssue());
			issue.setVolumeNum(updateJournalIssueForm.getVolume());
			issue.setYear(updateJournalIssueForm.getYear());
			issue.setMonth(updateJournalIssueForm.getMonth());
			
			journalIssueRepository.save(issue);	
			
			logger.info("Journal Issue {} is updated",jiid);
			
		
		
	}

	public void uploadEditorialPDF(int id, MultipartFile file, String updatedBy) throws Exception 
	{
		logger.info("Preparing to upload PDF version of article id {}",id);
		
		JournalIssue issue = journalIssueRepository.findById(id).orElse(null);
		
		if(issue==null)
		{
			logger.error("Invalid Journal Issue Id {}",id);
			throw new Exception("Invalid Journal Issue");
		}
		
		List<JournalSection> sections = getAllSections();
		
		JournalSection section = sections.stream().filter(s -> s.getSectionName().toLowerCase().contains("editorial")).findFirst().orElse(null);
		
		if(section == null)
		{
			logger.error("Editorial section is not defined.");
			throw new Exception("Editorial section is not defined. create a section named 'Editorial' to continue.");
		}
		
		
		if (file == null || file.isEmpty())
		{
			logger.error("Editorial file is not uploaded or empty.");
			throw new Exception("Editorial file PDF should be uploaded");
		}
		
		String fileMime = file.getContentType();
		// Validate PDF Content type here
		if(!fileMime.equalsIgnoreCase(pdfFileMime))
		{
			logger.error("Uploaded file should be valid PDF document");
			throw new Exception("Editorial should be valid PDF document");
		}	
		
		
		
		// Article Object
        Article article = new Article();
        
        article.setTopic("Editorial");
        article.setStatus("Updated to PDF");
        article.setAuthorid(updatedBy);
        article.setUploadedBy(updatedBy);
        
        
        article.setJournalsection(section);
        article.setJournalissue(issue);
        
        
       
        article.setUploadDate(new java.sql.Date(System.currentTimeMillis()));
        article.setIsFavorite(false); // By default, all submitted articles are non-favorite.
        
        Article a = articleRepo.save(article);
        
        String editorialFileName = "Editorial_"+issue.getId()+a.getId()+"."+FilenameUtils.getExtension(file.getOriginalFilename());
        
        a.setFileName(editorialFileName);
        
        FileStorageService.uploadFile(articlePath, editorialFileName, file);
        
        a.setPageCount(FileStorageService.getPageCount(articlePath, editorialFileName));
        
        
        
        logger.info("Article saved at {} with file name {}", articlePath, editorialFileName);
        
        
        articleRepo.save(a);
        
       
		
	}

	/**
	 * 
	 * @param jid
	 * @param article
	 * 
	 * Removes the scheduled article from the journal issue and make it's status changed to 'UnScheduled' (which is equivalent to 'Approved')
	 * @throws Exception 
	 */
	public void removeArticle(int jid, int article) throws Exception 
	{
		JournalIssue issue = journalIssueRepository.findById(jid).orElse(null);
		
		if(issue==null)
		{
			logger.error("Invalid Journal Issue Id {}",jid);
			throw new Exception("Invalid Journal Issue");
		}
		
		Article a = articleRepo.findById(article).orElse(null);
		
		if (a == null) 
		{
			
			logger.error("Article not found for id {}",article);
			throw new Exception("Article not found for given id "+article);
		}
		
		boolean status = issue.removeArticle(a);
		
		if(!status)
		{
			logger.error("Article {} is not assigned to the issue {}",article, jid);
			throw new Exception("Article is not assigned to this issue ");
		}
		
		a.setJournalissue(null);
		a.setJournalsection(null);
		a.setTocOrder(null);
		a.setStatus("Unscheduled");
		
		articleRepo.save(a);
		journalIssueRepository.save(issue);
		
	}
}
