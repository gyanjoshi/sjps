package com.example.projectx.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.projectx.dao.GenericDao;
import com.example.projectx.dto.EditorDto;
import com.example.projectx.form.AnnouncementForm;
import com.example.projectx.form.ApproveArticleForm;
import com.example.projectx.form.AssignReviewerForm;
import com.example.projectx.form.EditorForm;
import com.example.projectx.form.EditorProfileForm;
import com.example.projectx.form.ScheduleArticleForm;
import com.example.projectx.mail.EmailService;
import com.example.projectx.mail.Mail;
import com.example.projectx.model.Announcement;
import com.example.projectx.model.AnnouncementType;
import com.example.projectx.model.AppUser;
import com.example.projectx.model.Article;
import com.example.projectx.model.Category;
import com.example.projectx.model.Editor;
import com.example.projectx.model.JournalIssue;
import com.example.projectx.model.JournalSection;
import com.example.projectx.model.Person;
import com.example.projectx.repository.ArticleRepository;
import com.example.projectx.repository.EditorRepository;
import com.example.projectx.repository.JournalIssueRepository;
import com.example.projectx.repository.PersonRepository;

@Service
public class EditorService {

	@Autowired
	private EditorRepository editorRepo;

	@Value("${upload.path.profile}")
	private String profilePath;

	@Value("${upload.path.article}")
	private String path;

	@Autowired
	private EmailService emailService;

	@Autowired
	private ArticleRepository articleRepo;
	
	@Autowired
	private UserDetailsServiceImpl userService;
	
	@Autowired
	PersonRepository personRepo;
	
	@Autowired
	private JournalIssueRepository journalIssueRepository;
	
	@Autowired
	private GenericDao<JournalSection> sectionDao;
	
	@Autowired
	private GenericDao<Announcement> announcementDao;
	

	@Autowired
	private GenericDao<AnnouncementType> announcementTypeDao;
	
	@Autowired
	private GenericDao<Category> categoryDao;
	
	private static final Logger logger = LoggerFactory.getLogger(EditorService.class);

	/**
	 * This method finds all reviewers in the system those may or may not be appuser. 
	 * Reviewer may be either {@AppUser} or {@Editor} object.
	 * @return List of all Reviewers (executive or normal editors/reviewers)
	 */
	public List<Person> getAllReviewers() {
		
		List<Person> reviewers = new ArrayList<Person>();
		
		List<Editor> editors = editorRepo.findAll()
										.stream()
										.filter(e -> e.getActive()==null?true:e.getActive())
										.collect(Collectors.toList());
		
		if(editors.size() > 0) {
			editors.forEach( e -> {
								
				reviewers.add(e);
			});
		}
		else {
			logger.error("No Editor found in system");
		}
			
		List<AppUser> exeditors = userService.getAllEditors();
		
		if (exeditors.size() > 0) {
			exeditors.forEach( e -> {
				reviewers.add(e);
			});
		}
		else {
			logger.error("No user with role editor found !!");
		}
		
		return reviewers;
		
	}
	public List<Editor> getAllEditors() {
		return editorRepo.findAll()
				.stream()
				.filter(e -> e.getActive()==null?true:e.getActive())
				.collect(Collectors.toList());
	}
	public Map<Long, byte[]> getAllProfilePictures() {
		Map<Long, byte[]> profileMap = new HashMap<Long, byte[]>();

		List<Editor> editors = editorRepo.findAll()
										.stream()
										.filter(e -> e.getActive()==null?true:e.getActive())
										.collect(Collectors.toList());

		if (editors != null) {
			logger.trace("Editors found. Getting profile pictures of all editors.");
			editors.forEach(e -> profileMap.put(e.getId(), getProfilePicture(e.getProfilePicture())));
		} else {
			logger.debug("Editors not found");
		}

		return profileMap;
	}
	private byte[] getProfilePicture(String profileFileName) {
		byte[] bytes = null;

		if (profileFileName != null) {
			String absoultePath = profilePath + profileFileName;

			File f = new File(absoultePath);

			if (f.exists()) {

				try {
					bytes = Files.readAllBytes(f.toPath());
				} catch (IOException e) {
					logger.error("Could not read bytes from profile image {}", absoultePath);
					e.printStackTrace();
				}
			} else {
				logger.error("Profile image {} does not exists..", absoultePath);
			}

		} else {
			logger.error("Profile image filename is null.");

		}

		return bytes;

	}
	public void addEditor(EditorForm editor) throws Exception 
	{
		logger.info("Trying to add new editor..");
		
		
		List<EditorDto> existing = editorRepo.findEditorByEmail(editor.getEmail());
		
    	if (existing != null && existing.size() > 0)
    	{
    		
    		logger.error("This email {} is already registered as reviewer", editor.getEmail());
    		throw new Exception("This email "+editor.getEmail()+" is already registered as reviewer");
        }
    	
		Editor neweditor = new Editor();

		neweditor.setTitle(editor.getTitle());
		neweditor.setFullName(editor.getFullName());
		neweditor.setQualification(editor.getQualification());
		neweditor.setAffiliation(editor.getAffiliation());
		
		neweditor.setAddress1(editor.getAddress());			
		neweditor.setPhone(editor.getPhone());
		neweditor.setCity(editor.getCity());
		neweditor.setState(editor.getState());
		neweditor.setCountry(editor.getCountry());
		
		neweditor.setEmail(editor.getEmail());
		
		neweditor.setAboutme(editor.getAboutme());	
		neweditor.setActive(true);


		editorRepo.save(neweditor);

		logger.info("Editor registered successfully");

	}
	public void editEditor(Long id, EditorProfileForm editorProfileForm) throws Exception 
	{

		logger.info("Editing editor {} information.",id);
		
		Editor editor = editorRepo.findById(id).orElse(null);
		
		if(editor == null)
		{
			logger.error("Invalid editor id");
			throw new Exception("Invalid editor id");
		}
		
			
		editor.setTitle(editorProfileForm.getTitle());
		editor.setFullName(editorProfileForm.getFullName());
		editor.setQualification(editorProfileForm.getQualification());
		editor.setAffiliation(editorProfileForm.getAffiliation());
		
		editor.setAddress1(editorProfileForm.getAddress());			
		editor.setPhone(editorProfileForm.getPhone());
		editor.setCity(editorProfileForm.getCity());
		editor.setState(editorProfileForm.getState());
		editor.setCountry(editorProfileForm.getCountry());
		
		editor.setAboutme(editorProfileForm.getAboutme());

		editorRepo.save(editor);
		
		logger.info("Successfully updated editor {} information",id);

	}
	public void updateProfilePicture(Long id, MultipartFile file) throws Exception 
	{
		logger.info("Trying to update profile picture of editor id {}",id);
		
		if (file == null || file.isEmpty()) {
			logger.error("Profile picture is not provided. Can not continue without it.");
			throw new Exception("Profile picture is not provided. Can not continue without it.");
		}
			
		
		Editor editor = editorRepo.findById(id).orElse(null);
		
		if(editor == null)
		{
			logger.error("Invalid editor id");
			throw new Exception("Invalid reviewer id");
		}
		String existingFile = editor.getProfilePicture();

		if (existingFile != null) 
		{
			FileStorageService.deleteFile(profilePath, existingFile);
			logger.info("Existing profile picture {} is deleted from file system path {}",existingFile, profilePath);
		}

		String profileFileName = editor.getFullName() + "_editorProfile."
				+ FilenameUtils.getExtension(file.getOriginalFilename());

		editor.setProfilePicture(profileFileName);
	
		FileStorageService.uploadFile(profilePath, profileFileName, file);
		
		editorRepo.save(editor);
		
		logger.info("Profile picture {} is saved at file system path {}",profileFileName, profilePath);

}
	public void deleteEditor(Long id) throws Exception 
	{
		if(id==null)
		{
			logger.error("NULL editor id");
			throw new Exception("Invalid reviewer id");
		}
		
		Editor editor = editorRepo.findById(id).orElse(null);
		
		if(editor == null)
		{
			logger.error("Invalid editor id");
			throw new Exception("Invalid reviewer id");
		}
		
		logger.info("Deleteing editor id {}", id);
		
		String existingFile = editor.getProfilePicture();

		if (existingFile != null) 
		{
			FileStorageService.deleteFile(profilePath, existingFile);
			logger.info("Profile picture {} of {} is deleted from file system path {}",existingFile,editor.getFullName(), profilePath);
		}
		
		editorRepo.delete(editor);
    	
		
	}
	public void assignReviewer(AssignReviewerForm assignReviewerForm) throws Exception {
		
		Integer id = assignReviewerForm.getArticle_id();
		
		if(id==null) {
			logger.error("Article ID is null");
			throw new Exception("Article ID is NULL");
		}
		
		Article a = articleRepo.findById(id).orElse(null);
		
		if (a == null) {
			
			logger.error("Article not found for id {}",id);
			throw new Exception("Article not found for given id "+id);
		}
		
		List<Long> reviewerList = Arrays.asList(assignReviewerForm.getReviewers());
		
		
		logger.info("Trying to send article id {} to reviewer..", id);
		
		if (reviewerList == null)
		{
			logger.error("Reviewer list is null or empty.");
			throw new Exception("Reviewer list is null or empty.");
		}
		
		MultipartFile file = assignReviewerForm.getEvaluationSheet();
		
		if (file == null || file.isEmpty()) {
			logger.error("Evaluation sheet file is null or empty");
			throw new Exception("Evaluation sheet file is not uploaded.");
		}
		
		String message = assignReviewerForm.getEmailMessage();
		
		
		
		List<Person> persons = personRepo.findAllById(reviewerList);

		Set<Person> personSet = new HashSet<>(persons);
		
//		
//		
//		// Assign article a to each person object (reviewer)
//		persons.forEach(p -> {
//			p.assignArticle(a);
//		});
		
		
		a.setReviewers(personSet);
		
		/**
		 * Preparing to send email with attachments as article file from storage location and evaluation sheet
		 */


		File f = new File(path + a.getFileName());
		File f2 = null;
		try {
			f2 = FileStorageService.multipartToFile(file, file.getOriginalFilename());
		} catch (IllegalStateException | IOException e1) {
			logger.error("Could not read the uploaded file..");
			e1.printStackTrace();
		}

		List<File> files = new ArrayList<File>();
		files.add(f);
		files.add(f2);
		
		/**
		 * Email is sent to reviewers only if the article file is found in storage locations.
		 * If Article is not found (which is unlikely in most cases), email is not sent.
		 */

		if (f.exists()) {
			Mail mail = new Mail();

					
			
			mail.setSubject("Request to Review article: " + a.getTopic());
			mail.setContent(message);
			
			persons.forEach(p -> {
				mail.addTo(p.getEmail());
			});
			
			
			
			
				

			try {
					emailService.sendHtmlMessage(mail, files);
					a.setStatus("Sent to Reviewer");
					articleRepo.save(a);
					logger.info("Email sent to reviewers {} and article entity saved", mail.getTo());

				} catch (MessagingException ex) {
					logger.error("Could not send email to reviewers {}",mail.getTo());
					ex.printStackTrace();
				} catch (Exception ex) {
					// TODO Auto-generated catch block
					logger.error("Error in sending email or saving article entity");
					ex.printStackTrace();
				}
			}

		else {
			logger.error("Article file {} does not exist.", f.getAbsolutePath());			
		}
		
	}
	public void approve(ApproveArticleForm approveArticleForm) throws Exception 
	{
		
		Integer id = approveArticleForm.getArticle_id();
		
		if(id==null) {
			logger.error("Article ID is null");
			throw new Exception("Article ID is NULL");
		}
		
		Article a = articleRepo.findById(id).orElse(null);
		
		if (a == null) {
			
			logger.error("Article not found for id {}",id);
			throw new Exception("Article not found for given id "+id);
		}
		
		
		AppUser user = userService.getUserByUsername(a.getAuthorid());
		
		if(user == null) {
			logger.error("Invalid author {} for article {}",a.getAuthorid(),id);
			throw new Exception("Invalid author");
		}
		
		MultipartFile file = approveArticleForm.getApprovalCertificate();
		
		if (file == null || file.isEmpty()) {
			logger.error("Approval certificate is null or empty. Returning without approving the article.");
			throw new Exception("Approval certificate is null or empty.");
		}
		
		String message = approveArticleForm.getEmailMessage();
		
		logger.info("Preparing email to notify the author {}",user.getEmail());
		
		Mail mail = new Mail();

        
        mail.addTo(user.getEmail());
        mail.setSubject("Your article "+a.getTopic()+" is approved");
        mail.setContent(message);
        
        try {
 			emailService.sendHtmlMessage(mail, FileStorageService.multipartToFile(file, file.getOriginalFilename()));
 			logger.info("Email sent to notify the author {} about approved article ",user.getEmail(), id);
 		} catch (MessagingException e) {
 			logger.error("Could not send email to the author {} ",user.getEmail());
 			e.printStackTrace();
 		} catch (Exception e) {
 			logger.error("Error in sending email to the author {} ",user.getEmail());
			e.printStackTrace();
		}
        
        a.setStatus("Approved");
               
        
        articleRepo.save(a);
        
        
        logger.info("Status of article {} updated to 'Approved'",id);
		
	}
	public void scheduleArticle(ScheduleArticleForm scheduleArticleForm) throws Exception 
	{
		Integer id = scheduleArticleForm.getArticleId();
		
		if(id==null) {
			logger.error("Article ID is null");
			throw new Exception("Article ID is NULL");
		}
		
		Article a = articleRepo.findById(id).orElse(null);
		
		if (a == null) {
			
			logger.error("Article not found for id {}",id);
			throw new Exception("Article not found for given id "+id);
		}
		
		
		AppUser user = userService.getUserByUsername(a.getAuthorid());
		
		if(user == null) {
			logger.error("Invalid author {} for article {}",a.getAuthorid(),id);
			throw new Exception("Invalid author");
		}
		
		Integer issueId = scheduleArticleForm.getJournalIssueId();
		Integer journalId = scheduleArticleForm.getJournalId();
		Long sectionId = scheduleArticleForm.getJournalSectionId();
		int tocorder = scheduleArticleForm.getTocOrder();
		boolean isFav = scheduleArticleForm.getIsFavorite();
		
		if(issueId == null || journalId == null || sectionId == null) 
		{
			logger.error("Required parameters Journal Issue, Journal and Section is not provided");
			throw new Exception("Required parameters Journal Issue, Journal and Section is not provided");
		}
		
		JournalIssue j = journalIssueRepository.findById(issueId).orElse(null);
		
		if(j==null) 
		{
			logger.error("Invalid Journal Issue provided");
			throw new Exception("Invalid Journal Issue provided");
		}
		
		if (j.getJournal().getId().intValue() != journalId.intValue())
		{
			logger.error("Invalid Journal ID detected.");
			throw new Exception("Invalid Journal ID detected.");
		}
		JournalSection section = null;
		try {
			
			section = sectionDao.findOne(JournalSection.class, sectionId);
		}
		catch(Exception exp) {
			exp.printStackTrace();
		}
		
		if(section == null)
		{
			logger.error("Invalid Journal Section ID provided");
			throw new Exception("Invalid Journal Section ID provided");
		}
		
		
		// Validation completed. 
		// Assign article to the journal issue
		
		j.addArticle(a);
		a.setJournalissue(j);
		a.setJournalsection(section);
		a.setStatus("Scheduled");
		a.setTocOrder(tocorder);
		a.setIsFavorite(isFav);
		
		articleRepo.save(a);
		
		
		
		
	}
	public List<Announcement> getAllAnnouncements() 
	{
		List<Announcement> as = announcementDao.findAll(Announcement.class);		
		
		return as;
	}
	public List<AnnouncementType> getAllAnnouncementType()
	{
		return announcementTypeDao.findAll(AnnouncementType.class);
	}
	public void createAnnouncement(AnnouncementForm announcementForm) throws Exception 
	{
		Long atypeid = announcementForm.getAnnouncementTypeid();
		
		AnnouncementType atype = announcementTypeDao.findOne(AnnouncementType.class, atypeid);
		
		if(atype == null)
		{
			logger.error("Invalid Announcement Type ID");
			throw new Exception("Invalid announcement type");
		}
		
		
		String title = announcementForm.getTitle();
		String shortdesc = announcementForm.getShortDescription();
		String longdesc = announcementForm.getFullDescription();
		java.util.Date expdate = announcementForm.getExpiryDate();
		
		boolean sendEmail  = announcementForm.getSendEmail();
		
		Announcement a = new Announcement();
		
		a.setAnnouncementType(atype);
		a.setTitle(title);
		a.setShortDescription(shortdesc);
		a.setFullTextDescription(longdesc);
		a.setCreationDate(new java.sql.Date(System.currentTimeMillis()));
		a.setExpiryDate(expdate);
		
		announcementDao.save(a);
		
		logger.info("New Announcement Created");
		
		
		
		if(sendEmail)
		{
			logger.info("trying to send email to all regiserted authors");
			
			List<AppUser> authors = userService.getAllAuthors();			
			
			
			authors.forEach(u -> {
				
				Mail mail = new Mail();

                
                mail.addTo(u.getEmail());
                mail.setSubject(title);
                mail.setContent(longdesc);
              
                
                try 
                {
         			emailService.sendHtmlMessage(mail);
         		} 
                catch (MessagingException e) 
                {
         			logger.error(e.getMessage());
         			e.printStackTrace();
         		} 
                catch (Exception e) 
                {
    				logger.error(e.getMessage());
    				e.printStackTrace();
    			}
				
				
			});	        
	       
		}
		
		
	}
	public Announcement getAnnouncementById(int id) 
	{
		return announcementDao.findOne(Announcement.class, id);
	}
	public void editAnnouncement(int id, AnnouncementForm announcementForm) throws Exception 
	{
		Long atypeid = announcementForm.getAnnouncementTypeid();
		
		AnnouncementType atype = announcementTypeDao.findOne(AnnouncementType.class, atypeid);
		
		if(atype == null)
		{
			logger.error("Invalid Announcement Type ID");
			throw new Exception("Invalid announcement type");
		}
		
		Announcement a = announcementDao.findOne(Announcement.class, id);
		
		if(a == null)
		{
			logger.error("Invalid Announcement ID");
			throw new Exception("Invalid announcement");
		}
		
		String title = announcementForm.getTitle();
		String shortdesc = announcementForm.getShortDescription();
		String longdesc = announcementForm.getFullDescription();
		java.util.Date expdate = announcementForm.getExpiryDate();		
		
		a.setAnnouncementType(atype);
		a.setTitle(title);
		a.setShortDescription(shortdesc);
		a.setFullTextDescription(longdesc);
		a.setModifiedDate(new java.sql.Date(System.currentTimeMillis()));
		a.setExpiryDate(expdate);
		
		announcementDao.save(a);
		
		logger.info("Announcement Updated");
		
	}
	public void deleteAnnouncement(int id) throws Exception 
	{
		Announcement a = announcementDao.findOne(Announcement.class, id);
		
		if(a == null)
		{
			logger.error("Invalid Announcement ID");
			throw new Exception("Invalid announcement");
		}
		
		a.setAnnouncementType(null);
		
		announcementDao.delete(a);		
		
	}
	public void createAnnouncementType(String description) throws Exception 
	{
		List<AnnouncementType> atypes = announcementTypeDao.findAll(AnnouncementType.class);
		
		Optional<String> existing = atypes.stream()
			.map(t -> t.getAnnouncementType().trim())
			.filter(t -> t.equalsIgnoreCase(description))
			.findFirst();
		
		if(existing.isPresent())
		{
			logger.error("Specified announcement type {} already exists", description);
			throw new Exception("Specified announcement type "+description+" already exists");
		}
		
		AnnouncementType atype = new AnnouncementType();
		atype.setAnnouncementType(description);
		
		announcementTypeDao.save(atype);
		
		logger.info("New announcement Type created");
		
	}
	
	public void createCategory(String category) throws Exception 
	{
		List<Category> categories = categoryDao.findAll(Category.class);
		
		Optional<String> existing = categories.stream()
			.map(c -> c.getCategory().trim())
			.filter(c -> c.equalsIgnoreCase(category))
			.findFirst();
		
		if(existing.isPresent())
		{
			logger.error("Specified Category {} already exists", category);
			throw new Exception("Specified Category "+category+" already exists");
		}
		
		Category cat = new Category();
		
		cat.setCategory(category);
		
		categoryDao.save(cat);
		
		logger.info("New Category created");
		
	}
	
	public AnnouncementType getAnnouncementTypeById(int id) 
	{
		return announcementTypeDao.findOne(AnnouncementType.class, id);
	}
	
	public void editAnnouncementType(int id, String description) throws Exception 
	{
		AnnouncementType a = announcementTypeDao.findOne(AnnouncementType.class, id);
		
		if(a == null)
		{
			logger.error("Invalid Announcement Type ID");
			throw new Exception("Invalid announcement Type");
		}
		
		List<AnnouncementType> atypes = announcementTypeDao.findAll(AnnouncementType.class);
		
		Optional<String> existing = atypes.stream()
				.filter(t -> t.getId()!=id)
				.map(t -> t.getAnnouncementType().trim())
				.filter(t -> t.equalsIgnoreCase(description))
				.findFirst();
		
		if(existing.isPresent())
		{
			logger.error("Specified announcement type {} already exists", description);
			throw new Exception("Specified announcement type "+description+" already exists");
		}
		
		a.setAnnouncementType(description);
		
		announcementTypeDao.save(a);
		
		logger.info("Announcement Type Updated");
		
	}
	public void deleteAnnouncementType(int id) throws Exception 
	{
		AnnouncementType a = announcementTypeDao.findOne(AnnouncementType.class, id);
		
		if(a == null)
		{
			logger.error("Invalid Announcement Type ID");
			throw new Exception("Invalid announcement type");
		}
		
				
		announcementTypeDao.delete(a);
		
	}
	public void createSection(String section) throws Exception
	{
		
		List<JournalSection> sections = sectionDao.findAll(JournalSection.class);
		
		Optional<String> existing = sections.stream()
			.map(s -> s.getSectionName().trim())
			.filter(s -> s.equalsIgnoreCase(section))
			.findFirst();
		
		if(existing.isPresent())
		{
			logger.error("Specified Section {} already exists", section);
			throw new Exception("Specified Section "+section+" already exists");
		}
		
		JournalSection cat = new JournalSection();
		
		cat.setSectionName(section);
		
		sectionDao.save(cat);
		
		logger.info("New Section created");
		
	}
	public void deleteCategory(int id) throws Exception 
	{
		Category c = categoryDao.findOne(Category.class, id);
		
		
		
		if(c == null)
		{
			logger.error("Invalid Category ID");
			throw new Exception("Invalid Category Id");
		}
		
		Set<Article> articles = c.getArticles();
		
		articles.forEach(a -> a.removeCategory(c));
		

		
		
		categoryDao.delete(c);
	}
	
//	public void deleteSection(int id) throws Exception 
//	{
//		JournalSection c = sectionDao.findOne(JournalSection.class, id);
//		
//		if(c == null)
//		{
//			logger.error("Invalid Section ID");
//			throw new Exception("Invalid Section Id");
//		}
//		
//				
//		sectionDao.delete(c);
//		
//	}
	public void editCategory(int id, String category) throws Exception 
	{
		List<Category> categories = categoryDao.findAll(Category.class);
		
		Category c = null;
		
		try {
			
			Optional<Category> oc = categories.stream().filter(x -> x.getId()==id).findFirst();
			
			if(oc.isPresent())
				c = oc.get();
		}
		catch(NullPointerException e)
		{
			logger.error("Category id is not found");
		}
		
		if(c == null)
		{
			logger.error("Invalid Category id");
			throw new Exception("Invalid Category");
					
		}
		
		
		Optional<String> existing = categories.stream()
				.filter(t -> t.getId()!=id)
				.map(t -> t.getCategory().trim())
				.filter(t -> t.equalsIgnoreCase(category))
				.findFirst();
		
		if(existing.isPresent())
		{
			logger.error("Specified category {} already exists", category);
			throw new Exception("Specified category "+category+" already exists");
		}
		
		c.setCategory(category);
		
		categoryDao.save(c);
		
		logger.info("Category Updated");
		
	}
	
	public void editSection(int id, String section) throws Exception 
	{
		List<JournalSection> sections = sectionDao.findAll(JournalSection.class);
		
		JournalSection c = null;
		
		try {
			
			Optional<JournalSection> oc = sections.stream().filter(x -> x.getId()==id).findFirst();
			
			if(oc.isPresent())
				c = oc.get();
		}
		catch(NullPointerException e)
		{
			logger.error("Section id is not found");
		}
		
		if(c == null)
		{
			logger.error("Invalid Section id");
			throw new Exception("Invalid Section");
					
		}
		
		
		Optional<String> existing = sections.stream()
				.filter(t -> t.getId()!=id)
				.map(t -> t.getSectionName().trim())
				.filter(t -> t.equalsIgnoreCase(section))
				.findFirst();
		
		if(existing.isPresent())
		{
			logger.error("Specified section {} already exists", section);
			throw new Exception("Specified section "+section+" already exists");
		}
		
		c.setSectionName(section);
		
		sectionDao.save(c);
		
		logger.info("Section Updated");
		
	}
}
