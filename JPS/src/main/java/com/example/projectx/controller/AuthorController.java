package com.example.projectx.controller;

import java.security.Principal;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.projectx.exception.ArticleNotFoundException;
import com.example.projectx.exception.InvalidCategoryException;
import com.example.projectx.exception.UnauthorizedAccessException;
import com.example.projectx.form.ArticleUploadForm;
import com.example.projectx.form.ArticleValidator;
import com.example.projectx.form.AuthorProfileForm;
import com.example.projectx.form.PasswordChangeForm;
import com.example.projectx.model.AppUser;
import com.example.projectx.model.Article;
import com.example.projectx.model.Category;
import com.example.projectx.service.ArticleService;
import com.example.projectx.service.UserDetailsServiceImpl;
import com.example.projectx.utils.WebUtils;

@Controller
public class AuthorController {
	
	@Autowired
	private UserDetailsServiceImpl userService;
	
	@Autowired
    private ArticleService articleService;	
	
	@Value("${upload.image.size}")
	private String imageSize;
	
	private static final Logger logger=LoggerFactory.getLogger(AuthorController.class);
	
	
	@RequestMapping(path = "/author", method = RequestMethod.GET)
	public String author(Model model, Principal principal) {
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
 
        
        String userInfo = WebUtils.toString(loginedUser);
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
        
		return "author/authorpage";
	}
	
	@RequestMapping(path = "/author/submitarticle", method = RequestMethod.GET)
	public String submitArticle(ArticleUploadForm articleUploadForm, Model model, Principal principal) {
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
	    model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
	    
	    
	    List<Category> categories = articleService.getAllCategories();
	    
	    model.addAttribute("cats", categories);
	    
	    
		return "/author/submitarticle";
	}
	
	@RequestMapping(value = "/author/submitarticle", method = RequestMethod.POST)
	public String createNewArticle(@Valid ArticleUploadForm articleUploadForm,BindingResult errors, RedirectAttributes redirectAttributes, Model model, Principal principal)
	{
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String message = null;
		String returnView = null;
		
		ArticleValidator av = new ArticleValidator();
		av.validate(articleUploadForm, errors);
	    
		if (errors.hasErrors())
		{
			List<Category> categories = articleService.getAllCategories();
		    
		    model.addAttribute("cats", categories);
		    
			returnView =  "/author/submitarticle"; 
		}
		
		else
		{
			try {
					articleService.saveArticle(articleUploadForm, loginedUser.getUsername());
					message = "Article uploaded successfully!!";	    	
			    	redirectAttributes.addFlashAttribute("message", message);
			    	returnView = "redirect:/author/pending";
				} 
		    	catch (InvalidCategoryException e) {
		    		
					returnView =  "/author/submitarticle";
					model.addAttribute("message", e.getMessage());
					
					List<Category> categories = articleService.getAllCategories();
				    
				    model.addAttribute("cats", categories);
				}		    	
		    	
		}		
        
		return returnView;
	 
	}
	
	@RequestMapping(path = "/author/pending", method = RequestMethod.GET)
	public String pendingReview(Model model, Principal principal) {		
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
		
		model.addAttribute("pending", articleService.getPendingArticles(loginedUser.getUsername()));
	    
		return "/author/underreview";
	}
	
	@RequestMapping(path = "/author/published", method = RequestMethod.GET)
	public String publishedArticles(Model model, Principal principal) {		
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
		
		model.addAttribute("published", articleService.getPublishedArticleByUser(loginedUser.getUsername()));
	    
		return "/author/publishedarticles";
	}
	
	@RequestMapping(value = "/author/delete-article", method = RequestMethod.GET)
    public String deleteArticle(@RequestParam int article,Model model, RedirectAttributes redirectAttributes, Principal principal) 
	{
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		
		
				
		try {
			articleService.deleteArticle(article, loginedUser.getUsername());
		} catch (ArticleNotFoundException | UnauthorizedAccessException e) {
			model.addAttribute("message", e.getMessage());
			return "/error";
		}
		
		redirectAttributes.addFlashAttribute("message", "Article Deleted Successfully!!");
		
        return "redirect:/author/pending";
    }
	
	@RequestMapping(value = "/author/update-article", method = RequestMethod.GET)
    public String updateArticle(@RequestParam("article") int id, ArticleUploadForm articleUploadForm, Model model, Principal principal) 
    
	{
    	
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		
//		System.out.println("Update article called for id "+ id);
		    
		Article a = articleService.getArticleById(id);		
		articleUploadForm.setTopic(a.getTopic());
		articleUploadForm.setArticleAbstract(a.getArticleAbstract());
		articleUploadForm.setOtherauthors(a.getOtherAuthors());
		
			
		articleUploadForm.setCategories(a.getCategories().stream().toArray(Long[]::new));
		
		
		
		String articleFileName = a.getFileName();
		
		List<Category> categories = articleService.getAllCategories();
	    
	    model.addAttribute("cats", categories);

//		model.addAttribute("articleUploadForm", articleUploadForm);
		model.addAttribute("Id", id);
		model.addAttribute("articleFileName", articleFileName);
	  
		model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
		
        return "/author/updatearticle";
    }
	
	@RequestMapping(value = "/author/update-article", method = RequestMethod.POST)
    public String updateArticlePost(@RequestParam("id") int id, 
    		@Valid ArticleUploadForm articleUploadForm,BindingResult errors, RedirectAttributes redirectAttributes, Model model, Principal principal) 
    {
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String message = null;
		String returnView = null;
		
		articleUploadForm.setUpdate(true);// set validation mode to 'Update'
		
		ArticleValidator av = new ArticleValidator();
		av.validate(articleUploadForm, errors);
	    
		if (errors.hasErrors())
		{
			
			logger.error("Form validation errors exist");
			
			Article a = articleService.getArticleById(id);				
			model.addAttribute("Id", id);
			model.addAttribute("articleFileName", a.getFileName());
			
			// Get Categories of article and send back to view
			
			Long[] cats = new Long[a.getCategories().size()];
			int i = 0;
			
			for (Category c : a.getCategories())
			{
				cats[i++] = c.getId();
			}
			
			articleUploadForm.setCategories(cats);
			
			List<Category> categories = articleService.getAllCategories();
		    
		    model.addAttribute("cats", categories);
			
			returnView =  "/author/updatearticle"; 
		}
		else
		{
			try {
				articleService.updateArticle(id, articleUploadForm, loginedUser.getUsername());
				message = "Article updated successfully!!";	    	
		    	redirectAttributes.addFlashAttribute("message", message);
		    	returnView = "redirect:/author/pending";
			} 
	    	catch (InvalidCategoryException | ArticleNotFoundException | UnauthorizedAccessException e) {
	    		
				returnView =  "/error";
				model.addAttribute("message", e.getMessage());
				return returnView;
			}
		}		
		
		model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
    	model.addAttribute("message",message);	
    	redirectAttributes.addFlashAttribute("message", message);
    	model.addAttribute("pending", articleService.getPendingArticles(loginedUser.getUsername()));
       
        return returnView;
    }
	
	@RequestMapping(value = "/author/editprofile", method = RequestMethod.GET)
    public String editAuthor(AuthorProfileForm authorProfileForm, Model model, Principal principal) {
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();	
    	
    	AppUser user = userService.getUserByUsername(loginedUser.getUsername());
    	
    	authorProfileForm.setFullName(user.getFullName());
    	authorProfileForm.setAddress(user.getAddress1());
    	authorProfileForm.setAffiliation(user.getAffiliation());
    	authorProfileForm.setPhone(user.getPhone());
    	
    	authorProfileForm.setCity(user.getCity());
    	authorProfileForm.setState(user.getState());
    	authorProfileForm.setCountry(user.getCountry());
    	
    	authorProfileForm.setAboutme(user.getAboutme());
    	
    	
    	
    	authorProfileForm.setQualification(user.getQualification());
    	authorProfileForm.setTitle(user.getTitle());

		model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
    	model.addAttribute("titles", userService.getAllTitles());
    	model.addAttribute("qualifications", userService.getAllQualifications());
    	
		return "/author/edit-profile";
    }
    
    @RequestMapping(value = "/author/editprofile", method = RequestMethod.POST)
    public String editAuthorPost(@Valid AuthorProfileForm authorProfileForm,BindingResult errors, RedirectAttributes redirectAttributes, Model model, Principal principal) 
    
    {
    	User loginedUser = (User) ((Authentication) principal).getPrincipal();
    	String message = null;
		String returnView = null;
	    
		if (errors.hasErrors())
		{
			logger.error("Validation error exist in author profile edit form.");
			
			model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
			model.addAttribute("titles", userService.getAllTitles());
	    	model.addAttribute("qualifications", userService.getAllQualifications());	    	
			returnView =  "/author/edit-profile"; 
		}
		else {
			
			userService.editAuthorProfile(loginedUser.getUsername(), authorProfileForm);
			model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
			
			message = "Profile updated!!";
			returnView = "redirect:/author";
			
			redirectAttributes.addFlashAttribute("message", message);
			
			
		}

		
    		    	
    	
        return returnView;
    }
    
    @RequestMapping(path = "/author/changeprofilepicture", method = RequestMethod.GET)
	public String changeProfilePicture(Model model, Principal principal) {
		
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
		
		// read max preview width and height from configuration.
		// for now hard coded
		model.addAttribute("maxwidth", imageSize);
		model.addAttribute("maxheight", imageSize);

		return "/author/changeprofile";
	}
	
	@RequestMapping(path = "/author/changeprofile", method = RequestMethod.POST)
	public String changeProfilePicturePost(MultipartFile croppedfile, RedirectAttributes redirectAttributes, Model model, Principal principal) {
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String userName = loginedUser.getUsername();
		
		String message = null;
		String returnView = null;
		
		if (croppedfile == null || croppedfile.isEmpty())
		{
			model.addAttribute("maxwidth", imageSize);
			model.addAttribute("maxheight", imageSize);
			model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
			
			message = "Please select an image for profile picture!!";
			returnView = "/author/changeprofilepicture";
			model.addAttribute("message", message);
		}
		else
		{
			
			try {
				userService.updateProfilePicture(userName, croppedfile);
				message = "Profile picture updated !!";
				returnView = "redirect:/author";
				redirectAttributes.addFlashAttribute("message", message);
				
			} catch (Exception e) {
				logger.error(e.getMessage());
				message = e.getMessage();
				returnView = "/author/changeprofilepicture";
				model.addAttribute("message", message);
				
			}
		}
	    
		model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
		
	    
		return returnView;
	}
	
	@RequestMapping(path = "/author/changepassword", method = RequestMethod.GET)
	public String changePassword(PasswordChangeForm passwordChangeForm, Model model, Principal principal) {
		
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));		
		
		return "/author/change-password";
	}
	
	@RequestMapping(path = "/author/changepassword", method = RequestMethod.POST)
	public String ChangePassowrdPost(@Valid PasswordChangeForm passwordChangeForm, BindingResult errors, RedirectAttributes redirectAttributes, Model model, Principal principal) {
		
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String userName = loginedUser.getUsername();
		
		passwordChangeForm.validate(errors);
		
		if (errors.hasErrors())
		{
			model.addAttribute("currentProfile", userService.getProfilePicture(userName));	
			return "/author/change-password";
		}
		
		else
		{
			try {
				userService.changePassword(userName, passwordChangeForm);
				redirectAttributes.addFlashAttribute("message", "Password Changed!!");		    
				return "redirect:/author";
				
			} catch (Exception e) {
				logger.error("Exception occured while chaning password");
				errors.reject("message", e.getMessage());
				return "/author/change-password";
			}
			
		}			   
		
	}

}
