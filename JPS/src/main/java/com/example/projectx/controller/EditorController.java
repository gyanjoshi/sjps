package com.example.projectx.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.projectx.dto.ArticleDto;
import com.example.projectx.dto.JournalIssueDto;
import com.example.projectx.exception.ArticleNotFoundException;
import com.example.projectx.exception.UnauthorizedAccessException;
import com.example.projectx.form.EditorProfileForm;
import com.example.projectx.form.AnnouncementForm;
import com.example.projectx.form.ApproveArticleForm;
import com.example.projectx.form.ArticleInfoUpdateForm;
import com.example.projectx.form.AssignReviewerForm;
import com.example.projectx.form.EditorForm;

import com.example.projectx.form.JournalForm;
import com.example.projectx.form.NewJournalIssueForm;
import com.example.projectx.form.PasswordChangeForm;
import com.example.projectx.form.ScheduleArticleForm;
import com.example.projectx.form.UpdateJournalIssueForm;
import com.example.projectx.model.Announcement;
import com.example.projectx.model.AnnouncementType;
import com.example.projectx.model.AppUser;
import com.example.projectx.model.Article;
import com.example.projectx.model.Category;
import com.example.projectx.model.Editor;
import com.example.projectx.model.Journal;
import com.example.projectx.model.JournalIssue;
import com.example.projectx.model.JournalSection;
import com.example.projectx.repository.EditorRepository;
import com.example.projectx.repository.JournalIssueRepository;
import com.example.projectx.repository.JournalRepository;
import com.example.projectx.service.ArticleService;
import com.example.projectx.service.EditorService;
import com.example.projectx.service.JournalService;
import com.example.projectx.service.UserDetailsServiceImpl;

@Controller
public class EditorController {
	
	@Autowired
	private JournalService journalService;
	
	@Autowired
	private ArticleService articleService;
	
	@Autowired
	private JournalRepository journalRepo;
	
	@Autowired
	private JournalIssueRepository journalIssueRepo;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private EditorService editorService;
	
	@Autowired
	private EditorRepository editorRepo;
	
	@Value("${upload.image.size}")
	private String imageSize;
	
	private static final Logger logger=LoggerFactory.getLogger(EditorController.class);
	
	
	@RequestMapping(path = "/editor", method = RequestMethod.GET)
	public String editor(Model model, Principal principal) 
	{
		
		return "redirect:/editor/submissions";
//		User loginedUser = (User) ((Authentication) principal).getPrincipal();
//		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
//		return "/editor/editorhome";
	}
	
	@RequestMapping(path = "/editor/editors", method = RequestMethod.GET)
	public String editors(Model model, Principal principal) {
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));		
		List<Editor> editors = editorService.getAllEditors();		
		model.addAttribute("editors", editors);
		model.addAttribute("profiles", editorService.getAllProfilePictures());
		return "/editor/editors_1";
	}
	
	@RequestMapping(path = "/editor/add-editor", method = RequestMethod.GET)
	public String addEditor(EditorForm editorForm, Model model, Principal principal) {
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));		
		
		model.addAttribute("titles", userDetailsService.getAllTitles());
    	model.addAttribute("qualifications", userDetailsService.getAllQualifications());
    	
		return "/editor/addeditor";
	}
	
	@RequestMapping(value = "/editor/add-editor", method = RequestMethod.POST)
    public String addEditorPost(@Valid EditorForm editorForm, BindingResult errors, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
		
    	editorForm.validate(errors);
    	
    	if(errors.hasErrors())
    	{
    		logger.error("Validation errors in add-editor form");
    		User loginedUser = (User) ((Authentication) principal).getPrincipal();
    		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));		
    		
    		model.addAttribute("titles", userDetailsService.getAllTitles());
        	model.addAttribute("qualifications", userDetailsService.getAllQualifications());
        	
        	return "/editor/addeditor";
    		
    	}
    	
    	else
    	{
    		try
    		{
    			editorService.addEditor(editorForm);
    			redirectAttributes.addFlashAttribute("message", "Reviewer added");
    			return "redirect:/editor/editors";
    		}
    		catch (Exception e)
    		{
    			User loginedUser = (User) ((Authentication) principal).getPrincipal();
        		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));		
        		
        		model.addAttribute("titles", userDetailsService.getAllTitles());
            	model.addAttribute("qualifications", userDetailsService.getAllQualifications());
            	model.addAttribute("errormessage", e.getMessage());
            	return "/editor/addeditor";
            	
    		}
    	}    	
    	
    }
	
	@RequestMapping(value = "/editor/edit-editor", method = RequestMethod.GET)
    public String editEditor(@RequestParam Long id, EditorProfileForm editorProfileForm, Model model, Principal principal) 
	{
    	
		Editor editor = editorRepo.findById(id).orElse(null);
		
		if(editor == null)
		{
			logger.error("Invalid id. Reviewer with id {} is not found.",id);
			
			return "/editor/editors_1";
			
		}
		
		editorProfileForm.setFullName(editor.getFullName());
    	editorProfileForm.setAddress(editor.getAddress1());
    	editorProfileForm.setAffiliation(editor.getAffiliation());
    	editorProfileForm.setPhone(editor.getPhone());
    	
    	editorProfileForm.setCity(editor.getCity());
    	editorProfileForm.setState(editor.getState());
    	editorProfileForm.setCountry(editor.getCountry());
    	
    	editorProfileForm.setAboutme(editor.getAboutme());
    	
    	
    	
    	editorProfileForm.setQualification(editor.getQualification());
    	editorProfileForm.setTitle(editor.getTitle());
		
    	
		model.addAttribute("titles", userDetailsService.getAllTitles());
    	model.addAttribute("qualifications", userDetailsService.getAllQualifications()); 
    	model.addAttribute("id", id);
		
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));	
		

    	
		return "/editor/editeditor";
    }
    
    @RequestMapping(value = "/editor/editeditor", method = RequestMethod.POST)
    public String editEditorPost(@RequestParam Long id, @Valid EditorProfileForm editorProfileForm,BindingResult errors, RedirectAttributes redirectAttributes, Model model, Principal principal) 
    {
    	
    	if(errors.hasErrors())
    	{
    		logger.error("Form validation errors in edit-editor");
    		
    		User loginedUser = (User) ((Authentication) principal).getPrincipal();
    		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));	
    		
    		return "/editor/editeditor";
    	}
    	
    	else
    	{
    		try 
    		{
				editorService.editEditor(id, editorProfileForm);
				redirectAttributes.addFlashAttribute("message", "Reviewer profile updated");        	
	            return "redirect:/editor/editors";
	            
			} 
    		catch (Exception e) 
    		{
				logger.error("Could not update reviewer profile");
				
				redirectAttributes.addFlashAttribute("errormessage", e.getMessage()); 
				
				return "redirect:/editor/editors";
			}
    		
    	}

    	
    }
    
    @RequestMapping(value = "editor/delete-editor", method = RequestMethod.GET)
    public String deleteEditor(@RequestParam Long id,RedirectAttributes redirectAttributes, Model model) 
    {
    	
    	try 
    	{
			editorService.deleteEditor(id);
			redirectAttributes.addFlashAttribute("message", "Reviewer deleted");
	        return "redirect:/editor/editors";
		} 
    	catch (Exception e) 
    	{
    		redirectAttributes.addFlashAttribute("errormessage", e.getMessage());
	        return "redirect:/editor/editors";
		}
    	
    }
    
    @RequestMapping(path = "/editor/add-profile", method = RequestMethod.GET)
	public String addProfile(@RequestParam String id, Model model, Principal principal) 
    {		
		model.addAttribute("id", id);	
		// read max preview width and height from configuration.
		// for now hard coded
		model.addAttribute("maxwidth", imageSize);
		model.addAttribute("maxheight", imageSize);
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));	
		
		
		return "/editor/addprofile";
	}
	
	@RequestMapping(path = "/editor/add-profile", method = RequestMethod.POST)
	public String addProfilePost(@RequestParam("id") Long id, MultipartFile croppedfile, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		
		logger.info("Add-profile post called");
		logger.info("editor id: {}",id);
		
		
		
		if (croppedfile == null || croppedfile.isEmpty())
		{
			model.addAttribute("maxwidth", imageSize);
			model.addAttribute("maxheight", imageSize);
			model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
			model.addAttribute("id", id);
			
			model.addAttribute("message", "Please select an image for profile picture!!");
			
			return "/editor/changeprofilepicture";
		}
		
		else {
			
			try 
			{
				editorService.updateProfilePicture(id, croppedfile);
				
				redirectAttributes.addFlashAttribute("message", "Profile photo updated");
			    
				return "redirect:/editor/editors";
			} 
			catch (Exception e) 
			{
				redirectAttributes.addFlashAttribute("errormessage", e.getMessage());
			    
				return "redirect:/editor/editors";
			}
			
			
		}
	}

/**
 * Start of Other Setting methods: Category, sections setting
 */
	@RequestMapping(path = "/editor/categories", method = RequestMethod.GET)
	public String categories(Model model, Principal principal) 
	{
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));	
		
		model.addAttribute("cats", articleService.getAllCategories());
		
		return "/editor/categories";
	}
	
	@RequestMapping(path = "/editor/newcategory", method = RequestMethod.GET)
	public String createCategory(Model model, Principal principal) 
	{
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));			
		
		
		return "/editor/newcategory";
	}
	
	@RequestMapping(path = "/editor/newcategory", method = RequestMethod.POST)
	public String createCategoryPost(@RequestParam("category") String category, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
		
		try 
		{
			editorService.createCategory(category);
			redirectAttributes.addFlashAttribute("message", "New category added");
			return "redirect:/editor/categories";
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			model.addAttribute("errorMessage", e.getMessage());				
			
			
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));				
			
			return "/editor/newcategory";
			
		}
	}
	
	@RequestMapping(value = "/editor/edit-category", method = RequestMethod.GET)
    public String editCategory(@RequestParam int id, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
		
		Optional<Category> a = articleService.getAllCategories()
								   .stream()
								   .filter(c -> c.getId()==id)
								   .findFirst();
		
		if(!a.isPresent())
		{
			logger.error("Invalid Category id");
			redirectAttributes.addFlashAttribute("errormessage", "Invalid Category id");
			return "redirect:/editor/categories";
		}
		
		Category c = a.get();
		
		
		model.addAttribute("id", id);
		model.addAttribute("category", c.getCategory());
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
		
		return "/editor/edit-category";
    }
	
	@RequestMapping(value = "/editor/edit-category", method = RequestMethod.POST)
    public String editCategoryPost(@RequestParam int id, @RequestParam String category, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
		
		Optional<Category> a = articleService.getAllCategories()
				   .stream()
				   .filter(c -> c.getId()==id)
				   .findFirst();

		if(!a.isPresent())
		{
			logger.error("Invalid Category id");
			redirectAttributes.addFlashAttribute("errormessage", "Invalid Category id");
			return "redirect:/editor/categories";
		}
		
		

		try 
		{
			editorService.editCategory(id, category);
			redirectAttributes.addFlashAttribute("message", "Category Updated");
			return "redirect:/editor/categories";
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			model.addAttribute("message", e.getMessage());
			
			Category c = a.get();
			
			model.addAttribute("id", id);	
			model.addAttribute("category", c.getCategory());
			
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
			
			return "/editor/edit-category";
			
		}
			
}
	
	@RequestMapping(value = "/editor/delete-category", method = RequestMethod.GET)
    public String deleteCategory(@RequestParam int id,Model model, RedirectAttributes redirectAttributes, Principal principal) 
	{
			
				
		try 
		{
			editorService.deleteCategory(id);
			redirectAttributes.addFlashAttribute("message", "Category Deleted Successfully!!");			
			return "redirect:/editor/categories";
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errormessage", e.getMessage());
			return "redirect:/editor/categories";
		}
		
		
    }
	
	@RequestMapping(path = "/editor/sections", method = RequestMethod.GET)
	public String sections(Model model, Principal principal) 
	{
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));	
		
		model.addAttribute("sections", journalService.getAllSections());
		
		return "/editor/sections";
	}
	
	@RequestMapping(path = "/editor/newsection", method = RequestMethod.GET)
	public String createSection(Model model, Principal principal) 
	{
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));			
		
		
		return "/editor/newsection";
	}
	
	@RequestMapping(path = "/editor/newsection", method = RequestMethod.POST)
	public String createSectionPost(@RequestParam("section") String section, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
		
		try 
		{
			editorService.createSection(section);
			redirectAttributes.addFlashAttribute("message", "New Section added");
			return "redirect:/editor/sections";
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			model.addAttribute("errorMessage", e.getMessage());				
			
			
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));				
			
			return "/editor/newsection";
			
		}
	}
	
	@RequestMapping(value = "/editor/edit-section", method = RequestMethod.GET)
    public String editSection(@RequestParam int id, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
		
		Optional<JournalSection> a = journalService.getAllSections()
								   .stream()
								   .filter(c -> c.getId()==id)
								   .findFirst();
		
		if(!a.isPresent())
		{
			logger.error("Invalid Section id");
			redirectAttributes.addFlashAttribute("errormessage", "Invalid Section id");
			return "redirect:/editor/sections";
		}
		
		JournalSection c = a.get();
		
		
		model.addAttribute("id", id);
		model.addAttribute("section", c.getSectionName());
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
		
		return "/editor/edit-section";
    }
	
	@RequestMapping(value = "/editor/edit-section", method = RequestMethod.POST)
    public String editSectionPost(@RequestParam int id, @RequestParam String section, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
		
		Optional<JournalSection> a = journalService.getAllSections()
				   .stream()
				   .filter(c -> c.getId()==id)
				   .findFirst();

		if(!a.isPresent())
		{
			logger.error("Invalid Section id");
			redirectAttributes.addFlashAttribute("errormessage", "Invalid Section id");
			return "redirect:/editor/sections";
		}
		
		

		try 
		{
			editorService.editSection(id, section);
			redirectAttributes.addFlashAttribute("message", "Section Updated");
			return "redirect:/editor/sections";
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			model.addAttribute("message", e.getMessage());
			
			JournalSection c = a.get();
			
			model.addAttribute("id", id);	
			model.addAttribute("section", c.getSectionName());
			
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
			
			return "/editor/edit-section";
			
		}
			
}	
/* *** Start of Editor Account Setting methods *********/
	
	@RequestMapping(value = "/editor/editprofile", method = RequestMethod.GET)
    public String editEditorProfile(EditorProfileForm editorProfileForm, Model model, Principal principal) {
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();	
    	
    	AppUser user = userDetailsService.getUserByUsername(loginedUser.getUsername());
    	
    	editorProfileForm.setFullName(user.getFullName());
    	editorProfileForm.setAddress(user.getAddress1());
    	editorProfileForm.setAffiliation(user.getAffiliation());
    	editorProfileForm.setPhone(user.getPhone());
    	
    	editorProfileForm.setCity(user.getCity());
    	editorProfileForm.setState(user.getState());
    	editorProfileForm.setCountry(user.getCountry());
    	
    	editorProfileForm.setAboutme(user.getAboutme());
    	
    	
    	
    	editorProfileForm.setQualification(user.getQualification());
    	editorProfileForm.setTitle(user.getTitle());

		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
    	model.addAttribute("titles", userDetailsService.getAllTitles());
    	model.addAttribute("qualifications", userDetailsService.getAllQualifications());
    	
		return "/editor/edit-profile";
    }
	
	 @RequestMapping(value = "/editor/editprofile", method = RequestMethod.POST)
	    public String editEditorProfilePost(@Valid EditorProfileForm editorProfileForm,BindingResult errors, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	    
	    {
	    	User loginedUser = (User) ((Authentication) principal).getPrincipal();
	    	String message = null;
			String returnView = null;
		    
			if (errors.hasErrors())
			{
				logger.error("Validation error exist in author profile edit form.");
				
				model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
				model.addAttribute("titles", userDetailsService.getAllTitles());
		    	model.addAttribute("qualifications", userDetailsService.getAllQualifications());	    	
				returnView =  "/editor/edit-profile"; 
			}
			else {
				
				userDetailsService.editEditorProfile(loginedUser.getUsername(), editorProfileForm);
				model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
				
				message = "Profile updated!!";
				returnView = "redirect:/editor";
				
				redirectAttributes.addFlashAttribute("message", message);
				
				
			}

			
	    		    	
	    	
	        return returnView;
	    }
	    
	    @RequestMapping(path = "/editor/changeprofilepicture", method = RequestMethod.GET)
		public String changeProfilePicture(Model model, Principal principal) {
			
			
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
			
			// read max preview width and height from configuration.
			// for now hard coded
			model.addAttribute("maxwidth", imageSize);
			model.addAttribute("maxheight", imageSize);

			return "/editor/changeprofile";
		}
		
		@RequestMapping(path = "/editor/changeprofile", method = RequestMethod.POST)
		public String changeProfilePicturePost(MultipartFile croppedfile, RedirectAttributes redirectAttributes, Model model, Principal principal) {
			
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			String userName = loginedUser.getUsername();
			
			String message = null;
			String returnView = null;
			
			if (croppedfile == null || croppedfile.isEmpty())
			{
				model.addAttribute("maxwidth", imageSize);
				model.addAttribute("maxheight", imageSize);
				model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
				
				message = "Please select an image for profile picture!!";
				returnView = "/editor/changeprofilepicture";
				model.addAttribute("message", message);
			}
			else
			{
				
				try {
					userDetailsService.updateProfilePicture(userName, croppedfile);
					message = "Profile picture updated !!";
					returnView = "redirect:/editor";
					redirectAttributes.addFlashAttribute("message", message);
					
				} catch (Exception e) {
					logger.error(e.getMessage());
					message = e.getMessage();
					returnView = "/editor/changeprofilepicture";
					model.addAttribute("message", message);
					
				}
			}
		    
			model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
			
		    
			return returnView;
		}
		
		@RequestMapping(path = "/editor/changepassword", method = RequestMethod.GET)
		public String changePassword(PasswordChangeForm passwordChangeForm, Model model, Principal principal) {
			
			
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));		
			
			return "/editor/change-password";
		}
		
		@RequestMapping(path = "/editor/changepassword", method = RequestMethod.POST)
		public String ChangePassowrdPost(@Valid PasswordChangeForm passwordChangeForm, BindingResult errors, RedirectAttributes redirectAttributes, Model model, Principal principal) {
			
			
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			String userName = loginedUser.getUsername();
			
			passwordChangeForm.validate(errors);
			
			if (errors.hasErrors())
			{
				model.addAttribute("currentProfile", userDetailsService.getProfilePicture(userName));	
				return "/editor/change-password";
			}
			
			else
			{
				try {
					userDetailsService.changePassword(userName, passwordChangeForm);
					redirectAttributes.addFlashAttribute("message", "Password Changed!!");		    
					return "redirect:/editor";
					
				} catch (Exception e) {
					logger.error("Exception occured while chaning password");
					errors.reject("message", e.getMessage());
					return "/editor/change-password";
				}
				
			}			   
			
		}
		
/* *** End of Editor Account Setting methods *********/
	
	@RequestMapping(path = "/editor/journalsetting", method = RequestMethod.GET)
	public String journalSetting(Model model, Principal principal) {
		
		
	    User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
	    
		List<Journal> list = journalService.getAllJournals();
		
		if(list != null)
		{
			
				model.addAttribute("journals",list );
			    model.addAttribute("coverimages", journalService.getAllJournalCoverImage());
			    return "/editor/viewjournals_1";
			
		}
		else
		{
			model.addAttribute("message", "No active journals found. Please create journal");
		}
			
		
		return "/editor/editorhome";
	}
	
	@RequestMapping(path = "/editor/createjournal", method = RequestMethod.GET)
	public String createNewJournal(Model model, Principal principal) {
		
		
	    User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
	    
		return "/editor/createjournal";
	}
	
	@RequestMapping(path = "/editor/createjournal", method = RequestMethod.POST)
	public String createNewJournalPost(@RequestParam String title, @RequestParam MultipartFile coverPage, Model model, Principal principal) {
		
		journalService.createJournal(title, coverPage);
		
		List<Journal> list = journalService.getAllJournals();
		
	    User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
		
		model.addAttribute("journals", list);
		model.addAttribute("coverimages", journalService.getAllJournalIssueCoverImage());
	    
		return "/editor/viewjournals";
	}
	
	@RequestMapping(path = "/editor/viewjournals", method = RequestMethod.GET)
	public String viewJournal(Model model, Principal principal) {
		
		List<Journal> list = journalService.getAllJournals();
		
		if(list != null)
		{
			if(list.size() > 0)
			{
				model.addAttribute("journals",list );
			    model.addAttribute("coverimages", journalService.getAllJournalIssueCoverImage());
			}
		}
		else
		{
			model.addAttribute("message", "No active journals found. Please create journal");
		}		
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
	    
		return "/editor/viewjournals";
	}
	
	@RequestMapping(value = "/editor/editjournal", method = RequestMethod.GET)
    public String editJournal(@RequestParam int id, JournalForm journalForm, Model model, Principal principal) {
		
		Journal journal = journalRepo.findById(id).get(); 
		
		journalForm.setJournalTopic(journal.getJournalTopic());
		journalForm.setPublisher(journal.getPublisher());
		journalForm.setOnlineissn(journal.getOnlineissn());
		journalForm.setPrintissn(journal.getPrintissn());
		journalForm.setId(id);
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
		
		return "/editor/editjournal_1";
    }
	
	@RequestMapping(value = "/editor/editjournal", method = RequestMethod.POST)
    
	public String editJournalPost(@Valid JournalForm journalForm, BindingResult errors, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
    	
		if (errors.hasErrors()) {
			return "/editor/editjournal_1";
			 
		}
		
		else 
		{
			try 
			{
				journalService.updateJournal(journalForm);
				model.addAttribute("journals", journalService.getAllJournals());
			    model.addAttribute("coverimages", journalService.getAllJournalIssueCoverImage());
			    
			    redirectAttributes.addFlashAttribute("message", "Journal Updated");
			   
		        return "redirect:/editor/journalsetting";
			} 
			catch (Exception e) 
			{
				model.addAttribute("message", e.getMessage());
				return "/editor/editjournal_1";
			}
		}
		
		
		
    }
	@RequestMapping(path = {"/editor/editjournalcover","/editor/add-cover"}, method = RequestMethod.GET)
	public String editJournalCover(@RequestParam int id, Model model, Principal principal) {
		
		model.addAttribute("id", id);
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
	    
		return "/editor/editjournalcover_1";
	}
	
	@RequestMapping(path = {"/editor/editjournalcover","/editor/add-cover"}, method = RequestMethod.POST)
	public String editJournalCoverPost(@RequestParam int id,@RequestParam MultipartFile file, RedirectAttributes redirectAttributes, Model model, Principal principal) {
		
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
		
		try {
			journalService.updateJournalCoverPage(id, file);			
		    
			redirectAttributes.addFlashAttribute("message", "Journal Cover Updated");
			   
	        return "redirect:/editor/journalsetting";
		} catch (Exception e) {
			
			model.addAttribute("message", e.getMessage());
			return "/editor/editjournalcover_1";
		}    
	    
	}	
	
	@RequestMapping(path = "/editor/viewjournalissues", method = RequestMethod.GET)
	public String viewJournalIssues(Model model, Principal principal) {
		
		List<Journal> list = journalService.getAllJournals();
		
		if(list != null)
		{
			if(list.size() > 0)
			{
				model.addAttribute("journals",list );
			    model.addAttribute("coverimages", journalService.getAllJournalIssueCoverImage());
			}
		}
		else
		{
			model.addAttribute("message", "No active journals found. Please create journal");
		}		
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
	    
		return "/editor/viewjournalissues";
	}
	
	
	@RequestMapping(value = "/editor/journalissues/{journalid}", method = RequestMethod.GET)
	public String showJournalIssues(Model model, @PathVariable("journalid") int journalid, Principal principal) {
	    
		List<JournalIssue> list = journalService.getAllJournalIssues(journalid);
		
		model.addAttribute("journalissues", list);
		
		System.out.println("Journal Id:"+journalid);
		
		model.addAttribute("id", journalid);
		model.addAttribute("title", journalRepo.findById(journalid).get().getJournalTopic());
		model.addAttribute("coverimage", journalService.getJournalCoverImage(journalid));
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
	    
	    return "/editor/journalissues :: resultsList";
	}

	/***** Submissions view part ******/

	@RequestMapping(path = "/editor/submissions", method = RequestMethod.GET)
	public String submissions(Model model, Principal principal) {
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		
		model.addAttribute("underreview", articleService.getUnderReviewArticles());
		model.addAttribute("unassigned", articleService.getUnAssignedArticles());
		model.addAttribute("assignedtome", articleService.getAllAssignedTo(loginedUser.getUsername()));
		model.addAttribute("approved", articleService.getApprovedArticles());
		
		model.addAttribute("scheduled", articleService.getScheduledArticles());
		
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
		
		model.addAttribute("allarchived", articleService.getArchivedArticles());
	    
		return "/editor/submissions";
	}
/***** End of Submissions view *********/
	@RequestMapping(value = "/editor/getIssuesList/{journalid}", method = RequestMethod.GET)
	public String getIssuesList(Model model, @PathVariable("journalid") int journalid, Principal principal) {
	    
		List<JournalIssue> list = journalService.getDraftJournalIssues(journalid);
		
		model.addAttribute("journalissues", list);
		
		
	    return "/editor/getIssuesList :: resultsList";
	}
	
	@RequestMapping(path = "/editor/approvedlist", method = RequestMethod.GET)
	public String editApprovedArticle(Model model, Principal principal) {
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
	    
		return "editor/pendingreview";
	}
	
	@RequestMapping(value = "/editor/approved-articles/{journalid}", method = RequestMethod.GET)
	public String showApprovedArticles(Model model, @PathVariable("journalid") int journalid, Principal principal) {
	    
		List<ArticleDto> list = articleService.getApprovedArticles(journalid);
		String message = null;
		if(list != null)
		{
			if(list.size() == 0)
			  message = "There are no articles approved for this issue. Please approve and assign the articles for this issue.";	
		}
		else
			message = "There are no articles approved for this issue. Please approve and assign the articles for this issue.";
		
		model.addAttribute("message", message);
		model.addAttribute("articles", list);
		System.out.println("Journal Id:"+journalid);
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
	    
	    return "/editor/approved-articles :: resultsList";
	}
/******************************************************************************/
/********* Menu -> Submissions -> Under Review Articles **********************/
/************* Actions: Send Feedback, Assign reviewer, Reject, approve, schedule for publication *******/
	
	
	@RequestMapping(path = "/editor/send-feedback", method = RequestMethod.POST)
	public String postFeedback(@RequestParam int articleId, @RequestParam String feedbackMessage, RedirectAttributes redirectAttributes, Model model, Principal principal) {
		
		
		try {
			articleService.sendFeedback(articleId, feedbackMessage);
			
			redirectAttributes.addFlashAttribute("message", "Feedback sent successfully!");			
		    
			return "redirect:/editor/submissions";
			
		} catch (Exception e) {
			logger.error("Error in sending feedback to author");
			logger.error(e.getMessage());
			
			redirectAttributes.addFlashAttribute("message", "Error in sending feedback to author.");
			return "redirect:/editor/submissions";
		}
	}
	
	// send to reviewer
	@RequestMapping(path = "/editor/to-reviewer", method = RequestMethod.GET)
	public String toReviewer(@RequestParam int article,AssignReviewerForm assignReviewerForm, Model model, Principal principal) {
		
		Article a = articleService.getArticleById(article);
		
		
		
		assignReviewerForm.setTopic(a.getTopic());
		assignReviewerForm.setArticle_id(article);
		assignReviewerForm.setAuthorUserName(a.getAuthorid());
		
		assignReviewerForm.setReviewers(a.getReviewers().stream().map(p->p.getId()).toArray(Long[]::new));
		
		model.addAttribute("title", a.getTopic());
		
		model.addAttribute("editors", editorService.getAllReviewers());
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
		
			    
		
		return "/editor/assignreviewer";
	}
	
	@RequestMapping(path = "/editor/to-reviewer", method = RequestMethod.POST)
	public String toReviewerPost(@Valid AssignReviewerForm assignReviewerForm, BindingResult errors, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{

		assignReviewerForm.validate(errors);
		
		if (errors.hasErrors()) {
			logger.error("Validation errors exist.");
			
			logger.info("Topic : {}", assignReviewerForm.getTopic());
			
			model.addAttribute("title", assignReviewerForm.getTopic());
			model.addAttribute("editors", editorService.getAllReviewers());
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
			
			return "/editor/assignreviewer";
		}
		
		else {
			
			
			try {
				editorService.assignReviewer(assignReviewerForm);
				
				
				redirectAttributes.addFlashAttribute("message", "Reviewer assigned successfully!");
				
			    
				return "redirect:/editor/submissions";
				
			} catch (Exception e) {
				logger.error("Error in Reviewer assignment process");
				logger.error(e.getMessage());
				
				model.addAttribute("title", assignReviewerForm.getTopic());
				model.addAttribute("editors", editorService.getAllReviewers());
				User loginedUser = (User) ((Authentication) principal).getPrincipal();
				model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
				
				return "/editor/assignreviewer";
				
			}
			
		}
	}
	
	@RequestMapping(path = "/editor/approve", method = RequestMethod.GET)
	public String approveArticle(@RequestParam int article,ApproveArticleForm approveArticleForm, Model model, Principal principal) {
		
		Article a = articleService.getArticleById(article);
		
		if(a==null) {
			logger.error("Article with id {} is not found",article);
			model.addAttribute("errorMessage", "Invalid Article ID");
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
			return "/editor/submissions";
		}
		
		else 
		{			
			
			approveArticleForm.setTopic(a.getTopic());
			approveArticleForm.setArticle_id(article);
		
			model.addAttribute("article", a);
			model.addAttribute("title", a.getTopic());
			
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
		    
			return "/editor/approve-article";
			
		}
		
		
	}
	
	@RequestMapping(path = "/editor/approve", method = RequestMethod.POST)
	public String approveArticlePost(@Valid ApproveArticleForm approveArticleForm, BindingResult errors, RedirectAttributes redirectAttributes, Model model,Principal principal) {
		
		approveArticleForm.validate(errors);
		
		if (errors.hasErrors()) {
			logger.error("Validation errors exist.");			
			
			model.addAttribute("title", approveArticleForm.getTopic());
			
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
			
			return "/editor/approve-article";
		}
		
		else {
			
			
			try {
				editorService.approve(approveArticleForm);
				
				
				redirectAttributes.addFlashAttribute("message", "Article Approved successfully!");
				
			    
				return "redirect:/editor/submissions";
				
			} catch (Exception e) {
				logger.error("Error in Article Approval process");
				logger.error(e.getMessage());
				
				model.addAttribute("title", approveArticleForm.getTopic());
				
				User loginedUser = (User) ((Authentication) principal).getPrincipal();
				model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
				
				return "/editor/approve-article";
				
			}
			
		}
		
	}
	
	@RequestMapping(path = "/editor/reject", method = RequestMethod.POST)
	public String rejectArticlePost(@RequestParam int articleId, @RequestParam String rejectMessage, RedirectAttributes redirectAttributes, Model model, Principal principal) {
		
		try {
			articleService.reject(articleId,rejectMessage);
			
			redirectAttributes.addFlashAttribute("message", "Article Rejected!");			
		    
			return "redirect:/editor/submissions";
			
		} catch (Exception e) {
			logger.error("Error in sending reject message to author");
			logger.error(e.getMessage());
			
			redirectAttributes.addFlashAttribute("message", "Error in sending reject message to author.");
			return "redirect:/editor/submissions";
		}
		
	}
	/**
	 * 
	 */
	@RequestMapping(path = "/editor/uploadPDF", method = RequestMethod.GET)
	public String uploadPDF(@RequestParam("article") Integer article, Model model,Principal principal) 
	{
		Article a = articleService.getArticleById(article);
		
		
		if(a==null) 
		{
			logger.error("Article with id {} is not found",article);
			model.addAttribute("errorMessage", "Invalid Article ID");
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
			return "/editor/submissions";
		}
		
		model.addAttribute("article", article);	
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
		
		return "/editor/uploadPDF";
	}
	
	@RequestMapping(path = "/editor/uploadPDF", method = RequestMethod.POST)
	public String uploadPDFPost(@RequestParam("article") Integer article, MultipartFile file, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
		
		try {
			articleService.uploadPDFArticle(article, file);
			redirectAttributes.addFlashAttribute("message", "Article updated successfully!");
			return "redirect:/editor/submissions";
		} 
		catch (Exception e) 
		{
			logger.error("Could not update article to PDF");
			redirectAttributes.addFlashAttribute("errormessage", e.getMessage());
			return "redirect:/editor/submissions";
			
		}	    
	}
	@RequestMapping(path = "/editor/schedule-issue", method = RequestMethod.GET)
	public String scheduleArticle(@RequestParam int article,ScheduleArticleForm scheduleArticleForm, Model model, Principal principal) {
		
		Article a = articleService.getArticleById(article);
		
		
		if(a==null) {
			logger.error("Article with id {} is not found",article);
			model.addAttribute("errorMessage", "Invalid Article ID");
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
			return "/editor/submissions";
		}		
		
		
		else 
		{
			
			List<Journal> journals = journalService.getAllJournals();
			
			if(journals.size() == 1)
			{
				
				scheduleArticleForm.setJournalId(journals.get(0).getId());				
				List<JournalIssue> list = journalService.getDraftJournalIssues(journals.get(0).getId());				
				model.addAttribute("journalissues", list);								
				
			}
			
			model.addAttribute("journals", journalService.getAllJournals());
			List<JournalSection> sections = journalService.getAllSections();						
			model.addAttribute("sections", sections);						
			
			scheduleArticleForm.setTopic(a.getTopic());
			scheduleArticleForm.setArticleId(article);
			
			// Check if the article is already scheduled in some journal issue
			// If article is already scheduled, populate current information in form
			
				JournalIssue issue  = a.getJournalissue();
				if (issue != null) {
					
					scheduleArticleForm.setJournalId(issue.getJournal().getId());
					scheduleArticleForm.setJournalIssueId(issue.getId());
					scheduleArticleForm.setJournalSectionId(a.getJournalsection().getId());
					int toc = a.getTocOrder() != null ? a.getTocOrder().intValue() : 0;
					scheduleArticleForm.setTocOrder(toc);
					
					boolean isFav = a.getIsFavorite() !=null ? a.getIsFavorite().booleanValue() : false;
					
					scheduleArticleForm.setIsFavorite(isFav);
					
					List<JournalIssue> list = journalService.getDraftJournalIssues(issue.getJournal().getId());				
					model.addAttribute("journalissues", list);
					
				}
				
			
				 
			
			
			
			model.addAttribute("title", a.getTopic());
			
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
		    
			return "/editor/schedule-issue";
			
		}
		
		
	}
	
	@RequestMapping(path = "editor/schedule-issue", method = RequestMethod.POST)
	public String scheduleArticle(@Valid ScheduleArticleForm scheduleArticleForm, BindingResult errors, RedirectAttributes redirectAttributes, Model model,Principal principal) {
		
		//approveArticleForm.validate(errors);
		
		if (errors.hasErrors()) {
			logger.error("Validation errors exist.");			
			
			List<Journal> journals = journalService.getAllJournals();
			model.addAttribute("journals", journals);
			
			List<JournalIssue> list = journalService.getDraftJournalIssues(scheduleArticleForm.getJournalId());				
			model.addAttribute("journalissues", list);
			
			
			List<JournalSection> sections = journalService.getAllSections();
						
			model.addAttribute("sections", sections);			
			
			model.addAttribute("title", scheduleArticleForm.getTopic());
			
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
		    
			return "/editor/schedule-issue";
		}
		
		else {
			
			
			try {
				editorService.scheduleArticle(scheduleArticleForm);
				
				
				redirectAttributes.addFlashAttribute("message", "Article scheduled successfully!");
				
			    
				return "redirect:/editor/submissions";
				
			} catch (Exception e) {
				logger.error("Error in Article Scheduling process");
				logger.error(e.getMessage());
				
				List<Journal> journals = journalService.getAllJournals();
				model.addAttribute("journals", journals);
				
				List<JournalIssue> list = journalService.getDraftJournalIssues(scheduleArticleForm.getJournalId());				
				model.addAttribute("journalissues", list);
				
				
				List<JournalSection> sections = journalService.getAllSections();
							
				model.addAttribute("sections", sections);			
				
				model.addAttribute("title", scheduleArticleForm.getTopic());
				
				User loginedUser = (User) ((Authentication) principal).getPrincipal();
				model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
			    
				return "/editor/schedule-issue";
				
			}
			
		}
		
	}
	
	
/******************************************************************************/
/********* Menu -> Submissions -> Under Review Articles **********************/
/************* END OF SUBMISSIONS -> Under Review Articles Actions ** *******/

/******************************************************************************************************************************/
/******************* Menu -> Issues ******************************************************************************************/
/****************************************************************************************************************************/
	
	@RequestMapping(path = "/editor/issues", method = RequestMethod.GET)
	public String getJournalIssues(Model model, Principal principal) {
		
				
		List<JournalIssueDto> future = journalService.getAllFutureIssues();
		List<JournalIssueDto> back = journalService.getAllBackIssues();
		
		model.addAttribute("futureissues", future);
		model.addAttribute("backissues", back);
		model.addAttribute("journals", journalService.getAllJournals());		
				
		model.addAttribute("coverimages", journalService.getAllJournalIssueCoverImage());
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
	    
		return "/editor/issues";
	}
	/**
	 * Menu -> Issues -> Create New Issue **************************************
	 */
	
	@RequestMapping(path = "/editor/newissue", method = RequestMethod.GET)
	public String newJournalIssue(NewJournalIssueForm newJournalIssueForm, Model model, Principal principal) {		
	    
	    model.addAttribute("journals", journalService.getAllJournals());
	    User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
	    
		return "/editor/newissue";
	}
	
	@RequestMapping(path = "/editor/newissue", method = RequestMethod.POST)
	public String newJournalIssuePost(@Valid NewJournalIssueForm newJournalIssueForm, BindingResult errors, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{		
	    
		newJournalIssueForm.validate(errors);
		
		if(errors.hasErrors()) 
		{
			logger.error("Validation errors in newissue form");
			
			model.addAttribute("journals", journalService.getAllJournals());
		    User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
		    
			return "/editor/newissue";
			
		}
		
		else {
			try {
				journalService.createJournalIssue(newJournalIssueForm);
				redirectAttributes.addFlashAttribute("message", "Journal Issue created");
				return "redirect:/editor/issues";
			}
			catch(Exception e) {
				logger.error("Could not create new journal issue");
				model.addAttribute("journals", journalService.getAllJournals());
				model.addAttribute("errorMessage",e.getMessage());
			    User loginedUser = (User) ((Authentication) principal).getPrincipal();
				model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
				return "/editor/newissue";
			}
		}
	}
	
	/**
	 * Menu -> Issues -> Create New Issue Ended **************************************
	 * 
	 * Menu -> Issues -> Delete (journal issue) started
	 */
	
	@RequestMapping(value = "editor/deletejournalissue", method = RequestMethod.GET)
    public String deleteJournalIssue(@RequestParam int jid,RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
		try {
			journalService.deleteJournalIssue(jid);
			redirectAttributes.addFlashAttribute("message", "Journal Issue deleted");
			
		} catch (Exception e) {
			logger.error("Could not delete journal issue");
			redirectAttributes.addFlashAttribute("errormessage", "Journal Issue could not be deleted");
			
		}
		
    	
		 return "redirect:/editor/issues";
    }
	
	/**
	 * Menu --> Issues --> Edit Journal Issue (Update Info)
	 */
	
	
	@RequestMapping(value = "/editor/editjournalissue", method = RequestMethod.GET)
    public String editJournalIssue(@RequestParam int jiid, UpdateJournalIssueForm updateJournalIssueForm, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
		
		JournalIssue issue = journalService.getJournalIssueById(jiid);
		
		
		
		if(issue==null) 
		{
			logger.error("Invalid Journal Issue");
			redirectAttributes.addFlashAttribute("errormessage", "Journal Issue is invalid");
			
			return "redirect:/editor/issues";
			
		}
		
		updateJournalIssueForm.setJournalIssueId(issue.getId());
		updateJournalIssueForm.setIssue(issue.getIssueNum());
		updateJournalIssueForm.setVolume(issue.getVolumeNum());
		updateJournalIssueForm.setYear(issue.getYear());
		updateJournalIssueForm.setMonth(issue.getMonth());
		
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
		
		return "/editor/editjournalissue";		
		
    }
	
	@RequestMapping(value = "/editor/editjournalissue", method = RequestMethod.POST)
    
	public String editJournalIssuePost(@Valid UpdateJournalIssueForm updateJournalIssueForm, BindingResult errors, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
    	
		if (errors.hasErrors()) 
		{		
			logger.error("Form validaiton error in update journal issue");
			return "/editor/editjournalissue";
			 
		}
		
		else 
		{
			try 
			{
				journalService.updateJournalIssue(updateJournalIssueForm);
							    
			    redirectAttributes.addFlashAttribute("message", "Journal Issue Updated");
			   
		        return "redirect:/editor/issues";
			} 
			catch (Exception e) 
			{
				redirectAttributes.addFlashAttribute("errormessage", e.getMessage());
				 return "redirect:/editor/issues";
			}
		}
		
		
		
    }
	/**
	 * Menu --> Issues --> Update journal Issue Cover page (Update Cover)
	 */
	@RequestMapping(path = {"/editor/editjournalissuecover"}, method = RequestMethod.GET)
	public String editJournalIssueCover(@RequestParam int jiid, Model model, Principal principal) {
		
		model.addAttribute("id", jiid);
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
	    
		return "/editor/editjournalissuecover";
	}
	
	@RequestMapping(path = {"/editor/editjournalissuecover"}, method = RequestMethod.POST)
	public String editJournalIssueCoverPost(@RequestParam int id,@RequestParam MultipartFile file, RedirectAttributes redirectAttributes, Model model, Principal principal) {
		
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
		
		try {
			journalService.updateJournalIssueCoverPage(id, file);			
		    
			redirectAttributes.addFlashAttribute("message", "Journal Issue Cover Updated");
			   
	        return "redirect:/editor/issues";
		} catch (Exception e) {
			
			model.addAttribute("message", e.getMessage());
			return "/editor/editjournalissuecover";
		}    
	    
	}
	
	/**
	 * Menu --> Issues --> Publish Journal Issue
	 */

	@RequestMapping(path = {"/editor/publishissue-preview"}, method = RequestMethod.GET)
	public String publishPreview(@RequestParam int jiid, Model model, Principal principal) {
		
		model.addAttribute("id", jiid);
		
		
		
		Map<Long,List<ArticleDto>> articleMap = new HashMap<Long,List<ArticleDto>>();
		
		List<JournalSection> sections = journalService.getAllSections();
		
		sections.forEach(s -> {
			List<ArticleDto> articles = articleService.getAllScheduledArticles(jiid, s.getId());
			articleMap.put(s.getId(), articles);			
		});
		
		model.addAttribute("amap", articleMap);		
		model.addAttribute("sections", sections);		
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
	    
		return "/editor/publishissue-preview";
	}
	@RequestMapping(path = {"/editor/uploadEditorialPDF"}, method = RequestMethod.GET)
	public String uploadEditorialPDF(@RequestParam int jiid, Model model, Principal principal)
	{
		model.addAttribute("id", jiid);
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
	    
		return "/editor/uploadEditorialPDF";
	}
	
	@RequestMapping(path = {"/editor/uploadEditorialPDF"}, method = RequestMethod.POST)
	public String uploadEditorialPDFPost(@RequestParam int id, MultipartFile file,RedirectAttributes redirectAttributes,Model model, Principal principal)
	{
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		
		try {
			journalService.uploadEditorialPDF(id, file,loginedUser.getUsername());
			redirectAttributes.addFlashAttribute("message", "Editorial updated successfully!");
			redirectAttributes.addAttribute("jiid", id);
			return "redirect:/editor/publishissue-preview";
		} 
		catch (Exception e) 
		{
			logger.error("Could not upload Editorial");
			model.addAttribute("errormessage", e.getMessage());
			model.addAttribute("id", id);
			
			model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
			
			return "/editor/uploadEditorialPDF";
			
		}
	}
	
	@RequestMapping(value = "/editor/remove", method = RequestMethod.GET)
    public String removeArticleFromJournalIssue(@RequestParam int jid,@RequestParam int article, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
		try 
		{
			journalService.removeArticle(jid, article);
			redirectAttributes.addFlashAttribute("message", "Article removed from issue");
			
		} catch (Exception e) {
			logger.error("Article could not be removed from issue");
			redirectAttributes.addFlashAttribute("errormessage", "Article could not be removed from issue");
			
		}
		
    	
		 return "redirect:/editor/issues";
    }
	
	@RequestMapping(path = "/editor/publish", method = RequestMethod.GET)
	public String publishFinal(@RequestParam("jid") int issueid, RedirectAttributes redirectAttributes,Model model, Principal principal) 
	{
		
		try 
		{
			journalService.publish(issueid);
			redirectAttributes.addFlashAttribute("message", "Journal Issue published !");
			return "redirect:/editor/issues";
		} 
		catch (Exception e) 
		{
			logger.error("Journal issue could not be publihed due to errors");
			redirectAttributes.addAttribute("jiid", issueid);
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
			
			return "redirect:/editor/publishissue-preview";
			
		}
	
	}
	
	@RequestMapping(value = "/editor/update-info", method = RequestMethod.GET)
    public String editArticleInfo(@RequestParam int article, ArticleInfoUpdateForm articleInfoUpdateForm, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
		
		Article a = articleService.getArticleById(article);
		
		
		
		if(a==null) 
		{
			logger.error("Invalid Article id");
			redirectAttributes.addFlashAttribute("errormessage", "Article Id is invalid");			
			return "redirect:/editor/issues";			
		}
		
		articleInfoUpdateForm.setArticleId(article);		
		articleInfoUpdateForm.setTopic(a.getTopic());
		articleInfoUpdateForm.setOtherauthors(a.getOtherAuthors());
		
		
		articleInfoUpdateForm.setCategories(a.getCategories().stream().map(c -> c.getId()).toArray(Long[]::new));	
		
		
		articleInfoUpdateForm.setReviewers(a.getReviewers().stream().map(p -> p.getId()).toArray(Long[]::new));		
		
		
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
		
		model.addAttribute("editors", editorService.getAllReviewers());
			    
	    model.addAttribute("cats", articleService.getAllCategories());
		
		return "/editor/updatearticle";		
		
    }
	
	@RequestMapping(value = "/editor/update-info", method = RequestMethod.POST)
    
	public String editArticleInfoPost(@Valid ArticleInfoUpdateForm articleInfoUpdateForm, BindingResult errors, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
    	
		if (errors.hasErrors()) 
		{		
			logger.error("Form validaiton error in update article info");
			
			model.addAttribute("editors", editorService.getAllReviewers());
		    
		    model.addAttribute("cats", articleService.getAllCategories());
		    
			return "/editor/updatearticle";
			 
		}
		
		else 
		{
			try 
			{
				int id = articleInfoUpdateForm.getArticleId();
				articleService.updateArticleInfo(id, articleInfoUpdateForm);
							    
			    redirectAttributes.addFlashAttribute("message", "Article Info updated.");
			   
		        return "redirect:/editor/submissions";
			} 
			catch (Exception e) 
			{
				redirectAttributes.addFlashAttribute("errormessage", e.getMessage());
				 return "redirect:/editor/submissions";
			}
		}
		
		
		
    }
	
	@RequestMapping(value = "/editor/unapprove", method = RequestMethod.GET)
    public String unapproveArticle(@RequestParam int article, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
		try 
		{
			articleService.unapproveArticle(article);
			redirectAttributes.addFlashAttribute("message", "Article unapproved.");
			
		} 
		catch (Exception e) 
		{
			logger.error("Article could not be unapproved.");
			redirectAttributes.addFlashAttribute("errormessage", "Article could not be unapproved.");
			
		}
		
    	
		 return "redirect:/editor/submissions";
    }
	
	@RequestMapping(value = "/editor/delete-article", method = RequestMethod.GET)
    public String deleteArticle(@RequestParam int article,Model model, RedirectAttributes redirectAttributes, Principal principal) 
	{
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		
		
				
		try 
		{
			articleService.deleteArticle(article, loginedUser.getUsername());
			redirectAttributes.addFlashAttribute("message", "Article Deleted Successfully!!");			
			return "redirect:/editor/submissions";
		} 
		catch (ArticleNotFoundException | UnauthorizedAccessException e) 
		{
			redirectAttributes.addFlashAttribute("errormessage", e.getMessage());
			return "redirect:/editor/submissions";
		}
		
		
    }
	
	@RequestMapping(value = "/editor/setfavorite", method = RequestMethod.GET)
    public String setFavoriteArticle(@RequestParam int article,Model model, RedirectAttributes redirectAttributes, Principal principal) 
	{
		try 
		{
			articleService.setFavorite(article);
			redirectAttributes.addFlashAttribute("message", "Article set as editor's favorite Successfully!!");			
			return "redirect:/editor/submissions";
		} 
		catch (Exception e) 
		{
			redirectAttributes.addFlashAttribute("errormessage", e.getMessage());
			return "redirect:/editor/submissions";
		}
		
		
    }
	
	@RequestMapping(value = "/editor/unpublish", method = RequestMethod.GET)
    public String unpublishArticle(@RequestParam int article,Model model, RedirectAttributes redirectAttributes, Principal principal) 
	{
		try 
		{
			articleService.unpublishArticle(article);
			redirectAttributes.addFlashAttribute("message", "Article unpublished Successfully!!");			
			return "redirect:/editor/submissions";
		} 
		catch (Exception e) 
		{
			redirectAttributes.addFlashAttribute("errormessage", e.getMessage());
			return "redirect:/editor/submissions";
		}
		
		
    }
	
	@RequestMapping(value = "/editor/announcements", method = RequestMethod.GET)
    public String viewAnnouncements(Model model, Principal principal) 
	{
		
		List<Announcement> announcements = editorService.getAllAnnouncements();
		
		
		List<AnnouncementType> announcementtypes = editorService.getAllAnnouncementType();
		
		model.addAttribute("announcements", announcements);
		model.addAttribute("atypes", announcementtypes);
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
		
		return "/editor/announcements";
    }
	
	@RequestMapping(value = "/editor/newannouncement", method = RequestMethod.GET)
    public String createAnnouncement(AnnouncementForm announcementForm, Model model, Principal principal) 
	{
		List<AnnouncementType> announcementtypes = editorService.getAllAnnouncementType();
		
		model.addAttribute("atypes", announcementtypes);
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
		
		return "/editor/newannouncement";
    }
	
	@RequestMapping(value = "/editor/newannouncement", method = RequestMethod.POST)
    public String createAnnouncementPost(@Valid AnnouncementForm announcementForm, BindingResult errors, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
		if(errors.hasErrors())
		{
			logger.info("Validation errors in new announcement form");
			
			List<AnnouncementType> announcementtypes = editorService.getAllAnnouncementType();
			
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
			
			model.addAttribute("atypes", announcementtypes);
			return "/editor/newannouncement";
		}
		
		else
		{
			try 
			{
				editorService.createAnnouncement(announcementForm);
				redirectAttributes.addFlashAttribute("message", "Announcement created");
				return "redirect:/editor/announcements";
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				model.addAttribute("message", e.getMessage());
				
				List<AnnouncementType> announcementtypes = editorService.getAllAnnouncementType();
				
				User loginedUser = (User) ((Authentication) principal).getPrincipal();
				model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
				
				model.addAttribute("atypes", announcementtypes);
				return "/editor/newannouncement";
				
			}
			
		}		
		
    }
	
	// Edit announcements
	
	@RequestMapping(value = "/editor/editannouncement", method = RequestMethod.GET)
    public String editAnnouncement(@RequestParam int id, AnnouncementForm announcementForm, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
		
		List<AnnouncementType> announcementtypes = editorService.getAllAnnouncementType();
		
		Announcement a = editorService.getAnnouncementById(id);
		
		if(a==null)
		{
			logger.error("Invalid Announcement id");
			redirectAttributes.addFlashAttribute("errormessage", "Invalid announcement id");
			return "redirect:/editor/announcements";
			
		}
		
		announcementForm.setAnnouncementTypeid(a.getAnnouncementType().getId());
		announcementForm.setTitle(a.getTitle());
		announcementForm.setShortDescription(a.getShortDescription());
		announcementForm.setFullDescription(a.getFullTextDescription());
		announcementForm.setExpiryDate(a.getExpiryDate());
		
		announcementForm.setHasExpiryDate(a.getExpiryDate() == null ? false: true);
		
		
		
		model.addAttribute("atypes", announcementtypes);
		model.addAttribute("id", id);
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
		
		return "/editor/editannouncement";
    }
	
	@RequestMapping(value = "/editor/editannouncement", method = RequestMethod.POST)
    public String editAnnouncementPost(@RequestParam int id, @Valid AnnouncementForm announcementForm, BindingResult errors, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
		if(errors.hasErrors())
		{
			logger.info("Validation errors in edit announcement form");
			
			List<AnnouncementType> announcementtypes = editorService.getAllAnnouncementType();
			
			Announcement a = editorService.getAnnouncementById(id);
			
			if(a==null)
			{
				logger.error("Invalid Announcement id");
				redirectAttributes.addFlashAttribute("errormessage", "Invalid announcement id");
				return "redirect:/editor/announcements";
				
			}
			
			announcementForm.setAnnouncementTypeid(a.getAnnouncementType().getId());
			announcementForm.setTitle(a.getTitle());
			announcementForm.setShortDescription(a.getShortDescription());
			announcementForm.setFullDescription(a.getFullTextDescription());
			announcementForm.setExpiryDate(a.getExpiryDate());
			
			announcementForm.setHasExpiryDate(a.getExpiryDate() == null ? false: true);			
			
			model.addAttribute("id", id);			
			model.addAttribute("atypes", announcementtypes);
			
			return "/editor/editannouncement";
		}
		
		else
		{
			try 
			{
				editorService.editAnnouncement(id, announcementForm);
				redirectAttributes.addFlashAttribute("message", "Announcement Updated");
				return "redirect:/editor/announcements";
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				model.addAttribute("message", e.getMessage());
				
				List<AnnouncementType> announcementtypes = editorService.getAllAnnouncementType();
				
				User loginedUser = (User) ((Authentication) principal).getPrincipal();
				model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
				
				Announcement a = editorService.getAnnouncementById(id);
				
				if(a==null)
				{
					logger.error("Invalid Announcement id");
					redirectAttributes.addFlashAttribute("errormessage", "Invalid announcement id");
					return "redirect:/editor/announcements";
					
				}
				
				announcementForm.setAnnouncementTypeid(a.getAnnouncementType().getId());
				announcementForm.setTitle(a.getTitle());
				announcementForm.setShortDescription(a.getShortDescription());
				announcementForm.setFullDescription(a.getFullTextDescription());
				announcementForm.setExpiryDate(a.getExpiryDate());
				
				announcementForm.setHasExpiryDate(a.getExpiryDate() == null ? false: true);			
				
				model.addAttribute("id", id);			
				model.addAttribute("atypes", announcementtypes);
				
				return "/editor/editannouncement";
				
			}
			
		}		
		
    }
	
	@RequestMapping(value = "/editor/deleteannouncement", method = RequestMethod.GET)
    public String deleteAnnouncement(@RequestParam int id,Model model, RedirectAttributes redirectAttributes, Principal principal) 
	{
			
				
		try 
		{
			editorService.deleteAnnouncement(id);
			redirectAttributes.addFlashAttribute("message", "Announcement Deleted Successfully!!");			
			return "redirect:/editor/announcements";
		} 
		catch (Exception e) 
		{
			redirectAttributes.addFlashAttribute("errormessage", e.getMessage());
			return "redirect:/editor/announcements";
		}
		
		
    }
	
	// announcement type
	
	@RequestMapping(value = "/editor/newannouncementtype", method = RequestMethod.GET)
    public String createAnnouncementtype(Model model, Principal principal) 
	{
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
		
		return "/editor/newannouncementtype";
    }
	
	@RequestMapping(value = "/editor/newannouncementtype", method = RequestMethod.POST)
    public String createAnnouncementPost(@RequestParam String description, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
			try 
			{
				editorService.createAnnouncementType(description);
				redirectAttributes.addFlashAttribute("message", "Announcement type created");
				return "redirect:/editor/announcements";
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				model.addAttribute("errorMessage", e.getMessage());				
				
				
				User loginedUser = (User) ((Authentication) principal).getPrincipal();
				model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));				
				
				return "/editor/newannouncementtype";
				
			}
		
    }
	
	// Edit announcements
	
	@RequestMapping(value = "/editor/editannouncementtype", method = RequestMethod.GET)
    public String editAnnouncementType(@RequestParam int id, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
		
		AnnouncementType a = editorService.getAnnouncementTypeById(id);
		
		if(a==null)
		{
			logger.error("Invalid Announcement Type id");
			redirectAttributes.addFlashAttribute("errormessage", "Invalid announcement type id");
			return "redirect:/editor/announcements";
			
		}
		
		
		model.addAttribute("id", id);
		model.addAttribute("description", a.getAnnouncementType());
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
		
		return "/editor/editannouncementtype";
    }
	
	@RequestMapping(value = "/editor/editannouncementtype", method = RequestMethod.POST)
    public String editAnnouncementPost(@RequestParam int id, @RequestParam String description, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
		AnnouncementType a = editorService.getAnnouncementTypeById(id);
		
		if(a==null)
		{
			logger.error("Invalid Announcement Type id");
			redirectAttributes.addFlashAttribute("errormessage", "Invalid announcement type id");
			return "redirect:/editor/announcements";
			
		}
		
		try 
		{
			editorService.editAnnouncementType(id, description);
			redirectAttributes.addFlashAttribute("message", "Announcement type Updated");
			return "redirect:/editor/announcements";
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			model.addAttribute("message", e.getMessage());
					
			
			model.addAttribute("id", id);	
			model.addAttribute("description", a.getAnnouncementType());
			
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userDetailsService.getProfilePicture(loginedUser.getUsername()));
			
			return "/editor/editannouncementtype";
			
		}
			
}
	
	@RequestMapping(value = "/editor/deleteannouncementtype", method = RequestMethod.GET)
    public String deleteAnnouncementType(@RequestParam int id,Model model, RedirectAttributes redirectAttributes, Principal principal) 
	{
			
				
		try 
		{
			editorService.deleteAnnouncementType(id);
			redirectAttributes.addFlashAttribute("message", "Announcement Type Deleted Successfully!!");			
			return "redirect:/editor/announcements";
		} 
		catch (Exception e) 
		{
			redirectAttributes.addFlashAttribute("errormessage", e.getMessage());
			return "redirect:/editor/announcements";
		}
		
		
    }
	
}
