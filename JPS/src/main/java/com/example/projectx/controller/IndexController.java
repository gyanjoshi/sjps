package com.example.projectx.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.projectx.dto.ArticleDto;
import com.example.projectx.dto.PublishedJournalDto;
import com.example.projectx.dto.UserDto;
import com.example.projectx.exception.ArticleNotFoundException;
import com.example.projectx.exception.ExistingUserException;
import com.example.projectx.form.AuthorRegistrationForm;
import com.example.projectx.form.AuthorValidator;
import com.example.projectx.form.PasswordResetForm;
import com.example.projectx.model.Announcement;
import com.example.projectx.model.AppUser;
import com.example.projectx.model.Editor;
import com.example.projectx.model.JournalIssue;
import com.example.projectx.model.Notice;
import com.example.projectx.model.Person;
import com.example.projectx.repository.DownloadRepository;
import com.example.projectx.repository.NoticeRepository;
import com.example.projectx.repository.PageRepository;
import com.example.projectx.service.ArticleService;
import com.example.projectx.service.DownloadService;
import com.example.projectx.service.EditorService;
import com.example.projectx.service.JournalService;
import com.example.projectx.service.UserDetailsServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

@Controller
public class IndexController {
	

	@Autowired
	private PageRepository pagerepo;
	@Autowired
	private DownloadRepository downloadrepo;
	@Autowired
	private NoticeRepository noticerepo;
	
	@Autowired
	private JournalService journalService;
	
	@Autowired
	private ArticleService articleService;
	
	@Autowired
	private EditorService editorService;

	
	@Autowired
	private UserDetailsServiceImpl userService;
	
	@Autowired
	private DownloadService downloadService;
	
	
	private static final Logger logger=LoggerFactory.getLogger(IndexController.class);
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage(AuthorRegistrationForm authorRegistrationForm, Model model) {
		
		
        
        return "loginregister";
    }
	
	@RequestMapping(value = "/loginregister", method = RequestMethod.GET)
    public String loginRegisterPage(AuthorRegistrationForm authorRegistrationForm, Model model) {
		
		return "loginregister";
    }
	
	@RequestMapping(path = "/register", method = RequestMethod.GET)
	public String register(AuthorRegistrationForm authorRegistrationForm, Model model) {
		
		return "loginregister";
	}
	
	@RequestMapping(path = "/register", method = RequestMethod.POST)
	public String registerPost(@Valid AuthorRegistrationForm authorRegistrationForm,BindingResult errors,RedirectAttributes redirectAttributes, Model model) {
		String message = null;
		String returnView = null;
		
		AuthorValidator av = new AuthorValidator();
		
		av.validate(authorRegistrationForm, errors);
		
		if(errors.hasErrors())
		{
			model.addAttribute("registermessage",errors.getGlobalError().getDefaultMessage());
			returnView = "/loginregister";
		}
		else
		{
			try 
			{
				userService.registerAuthor(authorRegistrationForm);
				message = "Your are now registered ! Please log on";
//				model.addAttribute("message", message);
				redirectAttributes.addFlashAttribute("message", message);
				return "redirect:/login";
			} 
			catch (ExistingUserException e) 
			{
//				System.out.println("Exception: "+e.getMessage());
				model.addAttribute("registermessage",e.getMessage());
				returnView = "/loginregister";
				
			}
			
		}
		
		return returnView;
		
	}
	
	@RequestMapping(path = "/resetpassword", method = RequestMethod.GET)
	public String resetPassword(PasswordResetForm passwordResetForm, Model model) {		

		return "resetpassword";
	}
	
	@RequestMapping(path = "/resetpassword", method = RequestMethod.POST)
	public String resetPassowrdPost(@Valid PasswordResetForm passwordResetForm,BindingResult errors,RedirectAttributes redirectAttributes,  Model model) 
	{
		
		
		String message = null;
		
		String returnView = null;
		
		if(errors.hasErrors())
		{
			model.addAttribute("message",errors.getGlobalError().getDefaultMessage());
			returnView = "/resetpassword";
		}
		else
		{
			UserDto user = userService.getUserByEmail(passwordResetForm.getEmail());
			
			if(user != null)
			{
				try {
					userService.resetPassword(user);
					message = "Your password has been sent to your email. Please check email and login using new credentials.";
					model.addAttribute("message", message);
					returnView =  "/login";
				} 
				catch (Exception e) {
					logger.error("Could not reset password");
					model.addAttribute("message", e.getMessage());
					returnView = "/resetpassword";
				}				
			}
			else
			{
				message = "Your email is not registered yet. Please enter valid email address.";
				model.addAttribute("message", message);
				returnView = "/resetpassword";
				
			}
		}		

		return returnView;
   
		
	}
		
	@RequestMapping(value = { "/"}, method = RequestMethod.GET)
    public String indexPage(Model model) {
		
		model.addAttribute("title", "Welcome");
        model.addAttribute("page", pagerepo.getOne(1));
        model.addAttribute("downloads",downloadrepo.findAll() );
        
        model.addAttribute("notices",noticerepo.findAll());
        model.addAttribute("announcements",editorService.getAllAnnouncements() );
               
        List<PublishedJournalDto> journalissues = journalService.getAllPublishedJournals();
   
        Optional<JournalIssue> issue = journalService.getAllJournals()
        			.stream()
        			.map(j -> journalService.getCurrentJournalIssue(j.getId()))
        			.filter(j -> j != null)
        			.findFirst();
        
        if(issue.isPresent())
        {
        	 model.addAttribute("currentissue",issue.get());
        	 byte[] coverImage = journalService.getAllJournalIssueCoverImage().get(issue.get().getId());
        	 model.addAttribute("coverImage", coverImage);
        	 
        }
        
        
        
        
        model.addAttribute("issues", journalissues);
        
        model.addAttribute("coverimages", journalService.getAllJournalIssueCoverImage());
        

        model.addAttribute("favarticles", articleService.getEditorPickArticles());
        
  
        return "index";
    }
	
	@RequestMapping(value = { "/index"}, method = RequestMethod.GET)
    public String indexPage1(Model model) 
	{	
		model.addAttribute("title", "Welcome");
        model.addAttribute("page", pagerepo.getOne(1));
        model.addAttribute("downloads",downloadrepo.findAll() );
        
        model.addAttribute("notices",noticerepo.findAll());
        model.addAttribute("announcements",editorService.getAllAnnouncements() );
               
        List<PublishedJournalDto> journalissues = journalService.getAllPublishedJournals();
   
        Optional<JournalIssue> issue = journalService.getAllJournals()
        			.stream()
        			.map(j -> journalService.getCurrentJournalIssue(j.getId()))
        			.filter(j -> j != null)
        			.findFirst();
        
        if(issue.isPresent())
        {
        	 model.addAttribute("currentissue",issue.get());
        	 byte[] coverImage = journalService.getAllJournalIssueCoverImage().get(issue.get().getId());
        	 model.addAttribute("coverImage", coverImage);
        	 
        }
        
        
        
        
        model.addAttribute("issues", journalissues);
        
        model.addAttribute("coverimages", journalService.getAllJournalIssueCoverImage());
        

        model.addAttribute("favarticles", articleService.getEditorPickArticles());
        
  
        return "index";
    }
	
	@RequestMapping(value = { "/favarticles"}, method = RequestMethod.GET)
    public String allFavorites(Model model) 
	{
		model.addAttribute("favarticles", articleService.getEditorPickArticles());
		return "favarticles";
	}
	
	@RequestMapping(value="403", method = RequestMethod.GET)
	public ModelAndView error() {
	    ModelAndView mav = new ModelAndView();
	    String errorMessage= "You are not authorized for the requested data.";
	    mav.addObject("errorMsg", errorMessage);
	    mav.setViewName("403");
	    return mav;
        }
	
	@RequestMapping(value="/error", method = RequestMethod.GET)
	public ModelAndView errorPage() {
	    ModelAndView mav = new ModelAndView();
	    String errorMessage= "Error occured. Please contact site administrator @ ***REMOVED***";
	    mav.addObject("errorMsg", errorMessage);
	    mav.setViewName("error");
	    return mav;
        }
    
	@RequestMapping(value = { "/contact" }, method = RequestMethod.GET)
    public String contactPage(Model model) 
	{
    	
        return "contactus";
    }
    
    @RequestMapping(value = "/guidelines", method = RequestMethod.GET)
    public String authorGuideline(Model model) {
    	model.addAttribute("guidelines", pagerepo.getOne(3));
//    	model.addAttribute("page", pagerepo.getOne(1));
        return "/author-guidelines";
    }
    
    @RequestMapping(value = "/aboutus", method = RequestMethod.GET)
    public String aboutUs(Model model) {
    	model.addAttribute("aboutus", pagerepo.getOne(1));
//    	model.addAttribute("page", pagerepo.getOne(1));
        return "aboutus";
    }
    
   
    @RequestMapping(value = "/downloads", method = RequestMethod.GET)
    public String getDownloads(Model model) {
    	model.addAttribute("downloads",downloadrepo.findAll() );
    	
        return "/downloads_1";
    } 
    
    @RequestMapping(value = "/announcements", method = RequestMethod.GET)
    public String getAnnouncements(Model model, Principal principal) 
    {
    	if(principal != null)
    	{
    		User loginedUser = (User) ((Authentication) principal).getPrincipal();
    		model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
    	}    	
		
    	model.addAttribute("announcements",editorService.getAllAnnouncements() );
    	
        return "/announcements";
    }
    
    @RequestMapping(value = "/listnotices", method = RequestMethod.GET)
    public String getNoticeList(Model model, Principal principal) 
    {
    	
    	if(principal != null)
    	{
    		User loginedUser = (User) ((Authentication) principal).getPrincipal();
    		model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
    	} 
    	
    	//Notice n = noticerepo.findById(id).get();

    	model.addAttribute("notices",noticerepo.findAll());

    	//model.addAttribute("notice", n);
 	
        return "notices";
    }
    
    @RequestMapping(value = "/announcement", method = RequestMethod.GET)
    public String viewAnnouncement(@RequestParam("id") int id, Model model, Principal principal) 
    {
    	if(principal != null)
    	{
    		User loginedUser = (User) ((Authentication) principal).getPrincipal();
    		model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
    	}
    	
    	Announcement a = editorService.getAnnouncementById(id);
    	
    	if(a== null)
    	{
    		logger.error("Invalid announcement id");
    		model.addAttribute("errormessage","Announcement does not exist");
    	}
    	
    	else
    	{
    		model.addAttribute("announcement", a);
    	}	
    	
    	
        return "/announcement";
    }
    

    @RequestMapping(value = "/notices", method = RequestMethod.GET)
    public String getNotices(@RequestParam("id") int id, Model model,  Principal principal) 
    {
    	if(principal != null)
    	{
    		User loginedUser = (User) ((Authentication) principal).getPrincipal();
    		model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
    	}
    	
    	Notice n = noticerepo.findById(id).get();
    	
    	if(n== null)
    	{
    		logger.error("Invalid Notice id");
    		model.addAttribute("errormessage","Notice does not exist");
    	}
    	
    	else
    	{
    		model.addAttribute("notice", n);
    	}    	
 	
        return "notice";
    }
    
    
    
    @RequestMapping(value="/viewarticle", method=RequestMethod.GET)
    public String viewArticle(HttpServletRequest request, HttpServletResponse response, @RequestParam("aid") int aid, Model model)
    {
    	try
    	{
    		ArticleDto adto = articleService.getPublishedArticleById(aid);
    		model.addAttribute("filename", adto.getFileName());
    		
    		String path = downloadService.getDownloadPath("article");
    		
    		String fileName=adto.getFileName();
    		
    		File file = new File(path + fileName);
    		if (file.exists()) {

    			//get the mimetype
    			String mimeType = URLConnection.guessContentTypeFromName(file.getName());
    			if (mimeType == null) {
    				//unknown mimetype so set the mimetype to application/octet-stream
    				mimeType = "application/octet-stream";
    			}

    			response.setContentType(mimeType);

    			/**
    			 * In a regular HTTP response, the Content-Disposition response header is a
    			 * header indicating if the content is expected to be displayed inline in the
    			 * browser, that is, as a Web page or as part of a Web page, or as an
    			 * attachment, that is downloaded and saved locally.
    			 * 
    			 */

    			/**
    			 * Here we have mentioned it to show inline
    			 */
    			response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

    			 //Here we have mentioned it to show as attachment
//    			 response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() + "\""));

    			response.setContentLength((int) file.length());

    			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

    			FileCopyUtils.copy(inputStream, response.getOutputStream());
    			
    			// Update view count in Article Object
    			articleService.incrementViewCounter(aid);
    			
    			logger.info("view count={}",adto.getViewCount());
    			
    			response.flushBuffer();
    			
    			
    			
    			
    		return null;
    		
    		}
    	}
    	catch(ArticleNotFoundException exp)
    	{
    		logger.error("Article is not found");
    		model.addAttribute("message", exp.getMessage());
    		return "error";
    	} catch (FileNotFoundException e) {
    		logger.error("Article file is not found");
    		model.addAttribute("message", "Article is not found");
    		return "error";
		} catch (IOException e) {
			logger.error("Article file could not be read");
    		model.addAttribute("message", "Article is not found");
    		return "error";
		}  	
    	return "viewarticle";
    	
    }

    @RequestMapping(value = "/viewjournal", method = RequestMethod.GET)
	public String viewJournal( @RequestParam("jid") int jid , @RequestParam("jiid") int jiid,  Model model)
    {
    	JournalIssue selectedJournal = journalService.getJournalIssueById(jiid);
    	List<ArticleDto> mainList = new ArrayList<ArticleDto>();
    	
    	mainList = articleService.getPublishedArticles(jiid);
    	
    	mainList.sort(new Comparator<ArticleDto>()
		{
    		public int compare(ArticleDto o1, ArticleDto o2){
    	         if(o1.getTocorder() == o2.getTocorder())
    	             return 0;
    	         return o1.getTocorder() < o2.getTocorder() ? -1 : 1;
    		}
		});
   	
    	// update Page From and To for each item (for shwoing in Table of content)
    	int curIdx = 0;//editorpagecount;
    	for (ArticleDto a: mainList)
    	{
    		
    		int numPages = a.getPageCount();
    		
    		int from = curIdx+1;
    		int to = from+numPages-1;
    		
    		curIdx = to;
    		
    		String pagefromto = from + " - " + to;
    		a.setPageFromTo(pagefromto);
    	}
    	

    	
    	model.addAttribute("selectedJournal", selectedJournal);
    	model.addAttribute("articles", mainList);
    	model.addAttribute("journals",journalService.getAllPublishedJournals());
        model.addAttribute("coverpage", journalService.getJournalIssueCoverImage(jiid));
        model.addAttribute("currentjournal", selectedJournal.getJournal());
       
    	
    	return "view-journal";
    }
    
    @RequestMapping(value = "/currentissue", method = RequestMethod.GET)
	public String viewJournal(RedirectAttributes redirectAttributes, Model model)
    {
    	Optional<JournalIssue> issue = journalService.getAllJournals()
    			.stream()
    			.map(j -> journalService.getCurrentJournalIssue(j.getId()))
    			.filter(j -> j != null)
    			.findFirst();
    
	    if(issue.isPresent())
	    {
	    	
	    	int journalId = issue.get().getJournalId();
	    	int journalIssueId = issue.get().getId();
	    	
	    	redirectAttributes.addAttribute("jid", journalId);
	    	redirectAttributes.addAttribute("jiid", journalIssueId);
	    	
	    	return "redirect:/viewjournal";
	    	 
	    	 
	    }
    	
    	
    	
    	return "index";
    }
    
    @RequestMapping(value = "/allissues", method = RequestMethod.GET)
    public String getAllIssues(Model model)
    {
    	
    	List<PublishedJournalDto> journalissues = journalService.getAllPublishedJournals();  
    	
    	if(journalissues != null && journalissues.size() > 0)
    	{
    		int total = journalissues.size();
    		int rows =  (int) Math.floor(Math.sqrt(total));
    		int cols =  (int) Math.ceil(Math.sqrt(total));
    		
    		
    		model.addAttribute("issues", journalissues);
    		model.addAttribute("rows", rows);
    		model.addAttribute("cols", cols);
    		
    		
            
            model.addAttribute("coverimages", journalService.getAllJournalIssueCoverImage());
    	}
            	
    	
        
    	return "allissues";
    }

    

    @RequestMapping(value = "/editorboard", method = RequestMethod.GET)
    public String getEditorTeam( Model model)
    {
    	List<AppUser> exeditors = userService.getAllEditors();
    	List<Editor> editors = editorService.getAllEditors();
    	
    	if(exeditors != null && exeditors.size() > 0)
    	{
    		int total = exeditors.size();
    		int rows =  (int) Math.floor(Math.sqrt(total));
    		int cols =  (int) Math.ceil(Math.sqrt(total));
    		
    		model.addAttribute("rows", rows);
    		model.addAttribute("cols", cols);
    		
    		model.addAttribute("exeditors",exeditors);
    		model.addAttribute("profiles", userService.getEditorsProfilePictures());
    		
    		
    	}
    	
    	if(editors != null && editors.size() > 0)
    	{
    		int total = editors.size();
    		int rows =  (int) Math.floor(Math.sqrt(total));
    		int cols =  (int) Math.ceil(Math.sqrt(total));
    		
    		model.addAttribute("rows_b", rows);
    		model.addAttribute("cols_b", cols);
    		
    		model.addAttribute("boardeditors",editors);
    		model.addAttribute("boardprofiles", editorService.getAllProfilePictures());
    		
    		
    	}        
        
        model.addAttribute("page", pagerepo.getOne(1));
    	return "editorialboard";
    }
    
    @RequestMapping(value = "/viewreviewer", method = RequestMethod.GET)
    public String viewReviewerDetails(@RequestParam("type") int type , @RequestParam("editor") String editor,  Model model)
    {
    	Person p = null;
    	if(type==1)
    	{
    		Optional<AppUser> u = userService.getAllEditors().stream().filter(e -> e.getUserName().equals(editor))
    				.findFirst();
    		if(u.isPresent())
    			p = u.get();
    	}
    	else if(type==2)
    	{
    		Optional<Editor> reviewer = editorService.getAllEditors().stream().filter(e -> e.getId().toString().equals(editor)).findFirst();
    				
    		if(reviewer.isPresent())
    			p = reviewer.get();
    	}
    	
    	else
    	{
    		logger.error("Invalid type parameter");
    		return "redirect:/error";
    	}
    	
    	if(p != null)
    	{
    		model.addAttribute("person", p);
    		return "viewreviewer";
    	}
    	else
    	{
    		logger.error("Reviewer detail not found");
    		return "redirect:/error";
    	}
		
    	
    }
    
    
    
}
