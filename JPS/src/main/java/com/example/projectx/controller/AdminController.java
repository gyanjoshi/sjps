package com.example.projectx.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.projectx.form.AddDownloadForm;
import com.example.projectx.form.AddNoticeForm;
import com.example.projectx.form.EditorProfileForm;
import com.example.projectx.form.PasswordChangeForm;
import com.example.projectx.form.UserForm;
import com.example.projectx.model.AppUser;
import com.example.projectx.model.Download;
import com.example.projectx.model.Notice;
import com.example.projectx.model.Page;
import com.example.projectx.repository.DownloadRepository;
import com.example.projectx.repository.NoticeRepository;
import com.example.projectx.repository.PageRepository;
import com.example.projectx.repository.UserRepository;
import com.example.projectx.service.DownloadService;
import com.example.projectx.service.UserDetailsServiceImpl;
import com.example.projectx.utils.WebUtils;

@Controller
public class AdminController {
	@Autowired
	private DownloadRepository downloadrepo;
	
	@Autowired
	private DownloadService downloadService;
	
	@Autowired
	private NoticeRepository noticerepo;
	
	@Autowired
	private UserRepository userrepo;
	
	@Autowired
	private UserDetailsServiceImpl userService;

	@Autowired
	private PageRepository pagerepo;
	
	@Value("${upload.image.size}")
	private String imageSize;
	
	private static final Logger logger= LoggerFactory.getLogger(AdminController.class);
	
	 
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminPage(Model model, Principal principal) {
         
        User loginedUser = (User) ((Authentication) principal).getPrincipal();
 
        
        String userInfo = WebUtils.toString(loginedUser);
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("profiles", userService.getAllProfilePictures());
        model.addAttribute("currentProfile", userService.getAllProfilePictures().get(loginedUser.getUsername()));
        
       // model.addAttribute("users", userrepo.findAll()); 
        return "/admin/adminhome";
    }
	
	@RequestMapping(value = "/admin/user/add-user", method = RequestMethod.GET)
    public String addUser(UserForm userForm, Model model, Principal principal) 
	{
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userService.getAllProfilePictures().get(loginedUser.getUsername()));

    	model.addAttribute("roles", userService.getAllRoles());
    	model.addAttribute("titles", userService.getAllTitles());
    	model.addAttribute("qualifications", userService.getAllQualifications());
    	
        return "/admin/user/userform";
    }
    @RequestMapping(value = "/admin/user/add-user", method = RequestMethod.POST)
    public String addUser(@Valid UserForm userForm, BindingResult result,RedirectAttributes redirectAttributes, Model model, Principal principal) 
    {
    	userForm.validate(result);
    	
    	if (result.hasErrors())
    	{
    		model.addAttribute("roles", userService.getAllRoles());
        	model.addAttribute("titles", userService.getAllTitles());
        	model.addAttribute("qualifications", userService.getAllQualifications());
        	
        	User loginedUser = (User) ((Authentication) principal).getPrincipal();
    		model.addAttribute("currentProfile", userService.getAllProfilePictures().get(loginedUser.getUsername()));
    		
    		System.out.println("Error: Form validation errors exist.");
    		
            return "/admin/user/userform";
        }
    	
    	else
    	{
    		try
    		{
    			userService.addUser(userForm);
    			redirectAttributes.addFlashAttribute("message", "User added");
        	    return "redirect:/admin/user";
    		}
    		catch(Exception e)
    		{
    			model.addAttribute("roles", userService.getAllRoles());
            	model.addAttribute("titles", userService.getAllTitles());
            	model.addAttribute("qualifications", userService.getAllQualifications());
            	
            	User loginedUser = (User) ((Authentication) principal).getPrincipal();
        		model.addAttribute("currentProfile", userService.getAllProfilePictures().get(loginedUser.getUsername()));
        		
        		model.addAttribute("errormessage", e.getMessage());        		
        		
        		
        		return "/admin/user/userform";
    			
    		}
    	}
    	
    }
    
    
	
	  @RequestMapping(value = "/admin/user", method = RequestMethod.GET) 
	  public String userPage(Model model, Principal principal) 
	  { 
		  
		  model.addAttribute("users", userrepo.findAll());
		  User loginedUser = (User) ((Authentication) principal).getPrincipal();
		  
		  model.addAttribute("currentProfile", userService.getAllProfilePictures().get(loginedUser.getUsername()));
	      
	      model.addAttribute("profiles", userService.getAllProfilePictures());
	      
		  return "/admin/user/userlist"; 
	  }
	 
    
    
    @RequestMapping(value = "/admin/user/edit-user", method = RequestMethod.GET)
    public String editUser(@RequestParam String uname, Model model, Principal principal) 
    {
    	AppUser user = userrepo.findByUserName(uname).get(0);
    	
		model.addAttribute("user", user);
		model.addAttribute("profiles", userService.getAllProfilePictures());
		model.addAttribute("roles", userService.getAllRoles());
    	model.addAttribute("titles", userService.getAllTitles());
    	model.addAttribute("qualifications", userService.getAllQualifications());
    	
    	User loginedUser = (User) ((Authentication) principal).getPrincipal();
		  
		model.addAttribute("currentProfile", userService.getAllProfilePictures().get(loginedUser.getUsername()));
    	
		return "/admin/user/edituser";
    }
    
    @RequestMapping(value = "/admin/user/edit-user", method = RequestMethod.POST)
    public String editUserPage(@RequestParam("userName") String uname, @ModelAttribute AppUser user, RedirectAttributes redirectAttributes, Model model, Principal principal) 
    {

    	try 
    	{
			userService.editUser(uname, user, "ROLE_ADMIN");
			redirectAttributes.addFlashAttribute("message", "User Info updated");
			return "redirect:/admin/user";
			
		} 
    	catch (Exception e) 
    	{
    		model.addAttribute("user", user);
    		model.addAttribute("profiles", userService.getAllProfilePictures());
    		model.addAttribute("roles", userService.getAllRoles());
        	model.addAttribute("titles", userService.getAllTitles());
        	model.addAttribute("qualifications", userService.getAllQualifications());
        	
        	User loginedUser = (User) ((Authentication) principal).getPrincipal();
    		  
    		model.addAttribute("currentProfile", userService.getAllProfilePictures().get(loginedUser.getUsername()));
    		
    		model.addAttribute("errormessage", e.getMessage());
        	
    		return "/admin/user/edituser";
		}
    	
        
    }
    
    @RequestMapping(value = "admin/user/delete-user", method = RequestMethod.GET)
    public String delUserPage(@RequestParam String uname,RedirectAttributes redirectAttributes,Model model) 
    {
    	
    	
    	try 
    	{
			userService.deleteUser(uname);
			redirectAttributes.addFlashAttribute("message", "User deleted");
			return "redirect:/admin/user";
		} 
    	catch (Exception e) 
    	{
    		redirectAttributes.addFlashAttribute("errormessage", e.getMessage());
			return "redirect:/admin/user";
		} 	
    	
       
    }
    
    @RequestMapping(path = "/admin/user/add-profile", method = RequestMethod.GET)
	public String add_cover(@RequestParam String uname, Model model, Principal principal) 
    {
		
		model.addAttribute("uname", uname);
		model.addAttribute("maxwidth", imageSize);
		model.addAttribute("maxheight", imageSize);
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userService.getAllProfilePictures().get(loginedUser.getUsername()));
		
		return "/admin/user/updateprofile";
	}
	
	@RequestMapping(path = "/admin/user/add-profile", method = RequestMethod.POST)
	public String addCoverPage(@RequestParam("uname") String userName, MultipartFile croppedfile, RedirectAttributes redirectAttributes, Model model, Principal principal) 
	{
		if (croppedfile == null || croppedfile.isEmpty())
		{
			model.addAttribute("maxwidth", imageSize);
			model.addAttribute("maxheight", imageSize);
			
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userService.getAllProfilePictures().get(loginedUser.getUsername()));
			
			model.addAttribute("uname", userName);
			
			model.addAttribute("message", "Please select an image for profile picture!!");
			
			return "/admin/user/updateprofile";
		}
		
		else
		{
			try 
			{
				userService.updateProfilePicture(userName, croppedfile);
				redirectAttributes.addFlashAttribute("message", "Profile photo updated");
				return "redirect:/admin/user";
			} 
			catch (Exception e) 
			{
				model.addAttribute("maxwidth", imageSize);
				model.addAttribute("maxheight", imageSize);
				
				User loginedUser = (User) ((Authentication) principal).getPrincipal();
				model.addAttribute("currentProfile", userService.getAllProfilePictures().get(loginedUser.getUsername()));
				
				model.addAttribute("uname", userName);
				
				model.addAttribute("message", e.getMessage());
				
				return "/admin/user/updateprofile";
			}
			

		    
		}
		
		
	}
	
	@RequestMapping(path = "/admin/user/reset-password", method = RequestMethod.GET)
	public String changePassword(@RequestParam String uname, Model model, Principal principal) {
		
		//AppUser usr = userService.getUserByUsername(uname);
		model.addAttribute("uname", uname);
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userService.getAllProfilePictures().get(loginedUser.getUsername()));

		return "/admin/user/reset-password";
	}
	
	@RequestMapping(path = "/admin/user/reset-password", method = RequestMethod.POST)
	public String ChangePassowrdPost(@RequestParam String userName, @RequestParam String password, @RequestParam String confirmPassword,  RedirectAttributes redirectAttributes,Model model, Principal principal) {
		
		String message = null;
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userService.getAllProfilePictures().get(loginedUser.getUsername()));

		
		if(password != null && confirmPassword != null)
		{
			if(password.equals(confirmPassword))
			{
				try 
				{
					userService.changePassword(userName, password);
					redirectAttributes.addFlashAttribute("message", "Password changed");
					return "redirect:/admin/user";
				} 
				catch (Exception e) 
				{
					model.addAttribute("uname", userName);
					model.addAttribute("message", e.getMessage());
					return "/admin/user/reset-password";
				}				
				
			}
			else
			{
				message = "Passwords do not match !!";
				model.addAttribute("uname", userName);
				model.addAttribute("message",message);
				return "/admin/user/reset-password";
			}
				
		}
		else
		{
			message = "Passowrd can not be empty";
			model.addAttribute("uname", userName);
			model.addAttribute("message",message);
			return "/admin/user/reset-password";
		}	   
		
	}

	 @RequestMapping(value = "/admin/downloads", method = RequestMethod.GET)
	 public String getDownloads(Model model, Principal principal) 
	 {
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
		model.addAttribute("downloads",downloadrepo.findAll() );
	    return "/admin/downloads/downloadlist";
	 }
	    
	 @RequestMapping(value = "/admin/downloads/add-download", method = RequestMethod.GET)
	 public String addDownload(Model model, Principal principal) 
	 {
		 User loginedUser = (User) ((Authentication) principal).getPrincipal();
		 model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
		 
		 model.addAttribute("download", new AddDownloadForm());
		 return "/admin/downloads/adddownload";
	 }
	 
	    @RequestMapping(value = "/admin/downloads/add-download", method = RequestMethod.POST)
	    public String postDownload(@ModelAttribute AddDownloadForm download ,Model model) 
	    {
	    	
	    	downloadService.addDownload(download);
	    	return "redirect:/admin/downloads";
	    }
	    
	    
	    //edit-downloads added on 8/17/2019 12:!3 pm
	    @RequestMapping(value = "/admin/downloads/edit-download", method = RequestMethod.GET)
	    public String editDownload(@RequestParam Integer id, Model model, Principal principal) 
	    {
	    	User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
			 
	    	Download download = downloadrepo.findById(id).get();
			model.addAttribute("download", download);
			model.addAttribute("id", id);
			return "/admin/downloads/editdownload";   
	    }
	    
	    @RequestMapping(value = "/admin/downloads/edit-download", method = RequestMethod.POST)
	    public String editDownload(@RequestParam("id") int id, @ModelAttribute AddDownloadForm download ,Model model) 
	    {
		    downloadService.editDownload(id, download);
	
	        return "redirect:/admin/downloads";
	    }
	    
	    @RequestMapping(value = "/admin/downloads/delete-download", method = RequestMethod.GET)
	    public String deleteDownload(@RequestParam("id") int id ,Model model) 
	    {
	    	downloadService.deleteDownload(id);
	    	return "redirect:/admin/downloads";
	    }
	    
	    
	    @RequestMapping(value = "/admin/notices", method = RequestMethod.GET)
	    public String getNotice(Model model, Principal principal) 
	    {
	    	User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
			  
	    	model.addAttribute("notices",noticerepo.findAll() );
	        return "/admin/notices/noticelist";
	    }
	    
	      
	    @RequestMapping(value = "/admin/notices/add-notice", method = RequestMethod.GET)
	    public String addNotice(Model model, Principal principal) 
	    {
	    	User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
			
	    	model.addAttribute("notice", new AddNoticeForm());
	        return "/admin/notices/addnotice";
	    }
	    @RequestMapping(value = "/admin/notices/addnotice", method = RequestMethod.POST)
	    public String postNotice(@ModelAttribute AddNoticeForm notice ,Model model, Principal principal) 
	    {
	    	//download service provides downloads and notice related services,hence downloadservice is used here

	    	try 
	    	{
				downloadService.addNotice(notice);
				return "redirect:/admin/notices";
			} 
	    	catch (Exception e) 
	    	{
				model.addAttribute("errorMessage", e.getMessage());
				User loginedUser = (User) ((Authentication) principal).getPrincipal();
				model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
				model.addAttribute("notice", notice);
		        return "/admin/notices/addnotice";
			}
	    	
	        
	    }
	    
	    @RequestMapping(value = "/admin/notices/attach-file", method = RequestMethod.GET)
	    public String attachFile(@RequestParam int id, Model model, Principal principal) 
	    {
	    	User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
			
	    	model.addAttribute("id", id);
	        return "/admin/notices/attachfile";
	    }
	    @RequestMapping(value = "/admin/notices/attach-file", method = RequestMethod.POST)
	    public String attachFilePost(@RequestParam int id, @RequestParam MultipartFile file, Model model, Principal principal) 
	    {
	    	//download service provides downloads and notice related services,hence downloadservice is used here
	    
	    	try 
	    	{
	    		downloadService.attachFile(id,file);
	    		return "redirect:/admin/notices";
	    	}
	    	catch(MaxUploadSizeExceededException e)
	    	{
	    		User loginedUser = (User) ((Authentication) principal).getPrincipal();
				model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
				
	    		model.addAttribute("errorMessage", "Attached file is very big. Please choose small size file");
	    		model.addAttribute("id", id);
		        return "/admin/notices/attachfile";
	    		
	    	}
	    	
	    }
	    
	    
	    @RequestMapping(value = "/admin/notices/delete-notice", method = RequestMethod.GET)
	    public String deleteNotice(@RequestParam("id") int id ,Model model) 
	    {
	    	downloadService.deleteNotice(id);
	    	
	        return "redirect:/admin/notices";
	    }
	    
	    
	    //edit-downloads added on 8/17/2019 12:!3 pm
	    @RequestMapping(value = "/admin/notices/edit-notice", method = RequestMethod.GET)
	    public String editNotice(@RequestParam Integer id, Model model, Principal principal) 
	    {
	    	Notice notice = noticerepo.findById(id).get();
	    	
			model.addAttribute("notice", notice);
			model.addAttribute("id", id);
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
			
			return "/admin/notices/editnotice";   
	    }
	    
	    @RequestMapping(value = "/admin/notices/edit-notice", method = RequestMethod.POST)
	    public String editNotice(@RequestParam("id") int id, @ModelAttribute AddNoticeForm notice ,Model model) 
	    {
		    downloadService.editnotice(id, notice);
		
		    model.addAttribute("notices",noticerepo.findAll());
		        return "redirect:/admin/notices";
	    }
	    
	    

		
		
		  @RequestMapping(value = "/admin/pages", method = RequestMethod.GET) 
		  public String pageList(Model model, Principal principal) 
		  { 
			  User loginedUser = (User) ((Authentication) principal).getPrincipal();
			  model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));	
				
			  model.addAttribute("pages", pagerepo.findAll());
			  return "/admin/page/pagelist"; 
		  }
		 
		  @RequestMapping(value = "/admin/edit-page", method = RequestMethod.GET) 
		  public String editPage(@RequestParam Integer id,Model model, Principal principal) 
		  { 
			  Page page = pagerepo.findById(id).get(); 
			  model.addAttribute("page", page);
			  
			  User loginedUser = (User) ((Authentication) principal).getPrincipal();
			  model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
			  
			  return "/admin/page/editpage"; 
		}
		  
		  @RequestMapping(value = "/admin/editpage", method = RequestMethod.POST) 
		  public String addUserPage(@ModelAttribute Page  page,Model model) 
		  {
			  
			  pagerepo.save(page);
			//  model.addAttribute("page", page);
			  return "redirect:/admin/pages"; 
		  }
		  
@RequestMapping("/admin/downloadlog")
public void downloadLogFile(HttpServletRequest request, HttpServletResponse response) throws IOException 
{
	String fileName = downloadService.getLogFileName();
	
	File file = new File(fileName);
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
		//response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

		 //Here we have mentioned it to show as attachment
		 response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() + "\""));

		response.setContentLength((int) file.length());

		InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

		FileCopyUtils.copy(inputStream, response.getOutputStream());

	}
}

@RequestMapping(value = "/admin/editprofile", method = RequestMethod.GET)
public String editEditorProfile(EditorProfileForm editorProfileForm, Model model, Principal principal) {
	
	User loginedUser = (User) ((Authentication) principal).getPrincipal();	
	
	AppUser user = userService.getUserByUsername(loginedUser.getUsername());
	
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

	model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
	model.addAttribute("titles", userService.getAllTitles());
	model.addAttribute("qualifications", userService.getAllQualifications());
	
	return "/admin/edit-profile";
}

 @RequestMapping(value = "/admin/editprofile", method = RequestMethod.POST)
    public String editEditorProfilePost(@Valid EditorProfileForm editorProfileForm,BindingResult errors, RedirectAttributes redirectAttributes, Model model, Principal principal) 
    
    {
    	User loginedUser = (User) ((Authentication) principal).getPrincipal();
    	String message = null;
		String returnView = null;
	    
		if (errors.hasErrors())
		{
			logger.error("Validation error exist in admin profile edit form.");
			
			model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
			model.addAttribute("titles", userService.getAllTitles());
	    	model.addAttribute("qualifications", userService.getAllQualifications());	    	
			returnView =  "/admin/edit-profile"; 
		}
		else {
			
			userService.editEditorProfile(loginedUser.getUsername(), editorProfileForm);
			model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
			
			message = "Profile updated!!";
			returnView = "redirect:/admin";
			
			redirectAttributes.addFlashAttribute("message", message);
			
			
		}    		    	
    	
        return returnView;
    }

@RequestMapping(path = "/admin/changeprofilepicture", method = RequestMethod.GET)
public String changeProfilePicture(Model model, Principal principal) 
{
	
	
	User loginedUser = (User) ((Authentication) principal).getPrincipal();
	model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
	
	// read max preview width and height from configuration.
	// for now hard coded
	model.addAttribute("maxwidth", imageSize);
	model.addAttribute("maxheight", imageSize);

	return "/admin/changeprofile";
}

@RequestMapping(path = "/admin/changeprofile", method = RequestMethod.POST)
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
		returnView = "/admin/changeprofile";
		model.addAttribute("message", message);
	}
	else
	{
		
		try {
			userService.updateProfilePicture(userName, croppedfile);
			message = "Profile picture updated !!";
			returnView = "redirect:/admin";
			redirectAttributes.addFlashAttribute("message", message);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			message = e.getMessage();
			returnView = "/admin/changeprofile";
			model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
			
			model.addAttribute("message", message);
			
		}
	}
    
	model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
	
    
	return returnView;
}

@RequestMapping(path = "/admin/changepassword", method = RequestMethod.GET)
public String changePassword(PasswordChangeForm passwordChangeForm, Model model, Principal principal) {
	
	
	User loginedUser = (User) ((Authentication) principal).getPrincipal();
	model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));		
	
	return "/admin/change-password";
}

@RequestMapping(path = "/admin/changepassword", method = RequestMethod.POST)
public String ChangePassowrdPost(@Valid PasswordChangeForm passwordChangeForm, BindingResult errors, RedirectAttributes redirectAttributes, Model model, Principal principal) {
	
	
	User loginedUser = (User) ((Authentication) principal).getPrincipal();
	String userName = loginedUser.getUsername();
	
	passwordChangeForm.validate(errors);
	
	if (errors.hasErrors())
	{
		model.addAttribute("currentProfile", userService.getProfilePicture(userName));	
		return "/admin/change-password";
	}
	
	else
	{
		try {
			userService.changePassword(userName, passwordChangeForm);
			redirectAttributes.addFlashAttribute("message", "Password Changed!!");		    
			return "redirect:/admin";
			
		} catch (Exception e) {
			logger.error("Exception occured while changing password");
			errors.reject("message", e.getMessage());
			model.addAttribute("currentProfile", userService.getProfilePicture(loginedUser.getUsername()));
			
			return "/admin/change-password";
		}
		
	}			   
	
}
		

	    
	    
	    
}
