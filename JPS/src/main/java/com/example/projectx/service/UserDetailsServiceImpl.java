package com.example.projectx.service;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.projectx.dao.AppUserDao;
import com.example.projectx.dto.UserDto;
import com.example.projectx.exception.ExistingUserException;
import com.example.projectx.form.AuthorProfileForm;
import com.example.projectx.form.AuthorRegistrationForm;
import com.example.projectx.form.EditorProfileForm;
import com.example.projectx.form.PasswordChangeForm;
import com.example.projectx.form.UserForm;
import com.example.projectx.mail.EmailService;
import com.example.projectx.mail.Mail;
import com.example.projectx.model.AppRole;
import com.example.projectx.model.AppUser;

import com.example.projectx.model.Qualification;
import com.example.projectx.model.Title;
import com.example.projectx.repository.UserRepository;
import com.example.projectx.utils.EmailValidator;
import com.example.projectx.utils.EncryptedPasswordUtils;
import com.example.projectx.utils.ImageUtils;
import com.example.projectx.utils.PasswordGenerator;

import jdk.internal.jline.internal.Log;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
 
	@Value("${upload.path.profile}")
	private String profilePath;
	
	@Value("${upload.image.size}")
	private String imageSize;
	
	@Autowired
    private AppUserDao userDao;
    
    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private EmailService emailService;
    
    private static final List<String> profileContentTypes = Arrays.asList("image/png", "image/jpeg", "image/gif");
    
    
 
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		com.example.projectx.model.AppUser user = userDao.getActiveUser(userName);
		
		if(user == null){
			logger.error("UserName {} is not found", userName);
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		
		logger.info("UserName {} with Role {} is found", userName, user.getRole());
		
		GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
		UserDetails userDetails = (UserDetails)new User(user.getUserName(),
				user.getPassword(), Arrays.asList(authority));
		return userDetails;		
		
	}
    
    public List<AppUser> getAllEditors()
    {
    	return userDao.getAllEditors();
    }
    
    public AppUser getUserByUsername(String userName)
    {
    	return userDao.findUserAccount(userName);
    }

	public List<AppUser> getAllAuthors() {
		
		return userDao.getAllAuthors();
	}
	
	public List<AppRole> getAllRoles()
	{
		return userDao.getAllRoles();
	}
	public List<Title> getAllTitles()
	{
		return userDao.getAllTitles();
	}
	public List<Qualification> getAllQualifications()
	{
		return userDao.getAllQualifications();
	}
	
	public void addUser(UserForm user) throws Exception
	{
		logger.info("Registering new user from admin dashboard");
		
		AppUser u = userDao.findUserAccount(user.getUserName());
		AppUser u1 = userDao.findUserAccountByEmail(user.getEmail());
		
		
		
		if (u1 != null)
			throw new Exception("This email is already registered");
		if (u != null)
			throw new Exception("This username is already taken. Please choose another username.");

		AppUser newUser = new AppUser();
		
		if(u==null)
		{
			newUser.setTitle(user.getTitle());
			newUser.setFullName(user.getFullName());
			newUser.setAddress1(user.getAddress1());
			newUser.setAddress2(user.getAddress2());
			
			if(user.getEmail().equals(user.getConfirmEmail()) && EmailValidator.emailValidator(user.getEmail()))
				newUser.setEmail(user.getEmail());
			else 
			{
				logger.error("Email is not valid or does not match");
				throw new Exception("Email is not valid or do not match");			
			}
				
				
			newUser.setPhone(user.getPhone());
			newUser.setCity(user.getCity());
			newUser.setState(user.getState());
			newUser.setQualification(user.getQualification());
			newUser.setProfession(user.getProfession());			
			
			newUser.setUserName(user.getUserName());
			if(user.getPassword().contentEquals(user.getConfirmpassword()))
				newUser.setPassword(EncryptedPasswordUtils.encrytePassword(user.getPassword()));
			else
			{
				logger.warn("Passwords do not match");
				throw new Exception("Passwords do not match");
			}
				
			newUser.setRole(user.getRole());
			
			newUser.setEnabled((short) 1);
			
			MultipartFile file = user.getProfilePicture();
			
			if(file !=null && !file.isEmpty())
			{
				String fileContentType = file.getContentType();
				
				if (profileContentTypes.contains(fileContentType))
				{
					String profileFileName = user.getUserName()+"_Profile."+FilenameUtils.getExtension(file.getOriginalFilename());				
					
					newUser.setProfilePicture(profileFileName);
					FileStorageService.uploadFile(profilePath,profileFileName, file);
					logger.info("Profile picture of user {} saved at {}", user.getFullName(), profilePath);
				}
				
				else
				{
					logger.error("Profile picture content type is not acceptable.");
				}			
				
			}
			else
			{
				logger.warn("Profile picture of user {} is not provided", user.getFullName());
			}
			
			userRepo.save(newUser);
			
						
			logger.info("User {} Registered successfully", user.getUserName());
			
			logger.info("Trying to send email to new user: {}", user.getUserName());
			
			// send email to user
			
			Mail mail = new Mail();

            
            mail.addTo(user.getEmail());
            mail.setSubject("You are now registered !!");
            mail.setContent("Dear "+user.getFullName()+",<br>"
            		+ "Congratulations!!<br>"
            		+ "You are registered in our Journal Publication system as a "+user.getRole().replaceAll("ROLE_", "")+"<br>"
            		+ "UserName :"+user.getUserName()+"<br>"
            		+ "Password :"+user.getPassword()+"<br>"
            		+ " Regards,<br>"
            		+ "Administrator<br>"
            		);
            
            try {
     			emailService.sendHtmlMessage(mail);
     			logger.info("Email sent to user {}", user.getEmail());
     		} catch (MessagingException e) {
     			logger.error("Email could not be sent to user {}", user.getEmail());
     			e.printStackTrace();
     		} catch (Exception e) {
				logger.error("Error in sending email to user {}", user.getEmail());
				e.printStackTrace();
			}		
			
			
		}
		
		
	}
    
	public Map<String,byte[]> getAllProfilePictures()
	{
		Map<String,byte[]> profileMap = new HashMap<String,byte[]>();
		
		List<AppUser> users = userDao.getAllUsers();
		
		if(users != null)
		{
			users.forEach(u -> profileMap.put(u.getUserName(), getProfilePicture(u.getUserName())));
		}		
		
		return profileMap;
	}
	
	public File getProfilePictureAsFile(String username) {
		
		String fileName=getUserByUsername(username).getProfilePicture();
		if(fileName != null)
		{
			String absoultePath = profilePath + fileName;
			
			File f = new File(absoultePath);
			if (f.exists())
			{
				return f;
				
			}
			
		}
		return null;
		
	}
	public byte[] getProfilePicture(String username)
	{
		String fileName=getUserByUsername(username).getProfilePicture();
		
		byte[] bytes=null;
		
		if(fileName != null)
		{
			String absoultePath = profilePath + fileName;
			
			File f = new File(absoultePath);
			if (f.exists())
			{
				
				try {
					bytes =  Files.readAllBytes(f.toPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	
		return bytes;
		
	}
	
	public String getProfileLocation()
	{
		return profilePath;
		
	}
	
	public String updateProfilePicture(String uname, MultipartFile file) throws Exception
	{
		logger.info("Trying to update profile picture of user: {}",uname);		
		
		if(file !=null && !file.isEmpty())
		{
			String fileContentType = file.getContentType();
			
			if (profileContentTypes.contains(fileContentType))
			{
				AppUser user = userRepo.findByUserName(uname).get(0);
				
				String existingFileName = user.getProfilePicture();				
				
				String profileFileName = user.getUserName()+"_Profile."+FilenameUtils.getExtension(file.getOriginalFilename());				
				
				user.setProfilePicture(profileFileName);
				
				if (existingFileName != null)
					FileStorageService.deleteFile(profilePath,existingFileName);
				
				
				// Check the size of input image and resize it if it is larger than 200*200 size.
				File imageFile = FileStorageService.multipartToFile(file, file.getOriginalFilename());
//				
				BufferedImage bimg = ImageUtils.getBufferedImage(imageFile);
//				
				int width = bimg.getWidth();
				int height = bimg.getHeight();
//				
				int size = Integer.parseInt(imageSize);
				
				logger.info("width= {}, height={}",width, height);
//				
				if (width > size || height > size) 
				{
					logger.error("Image is larger than permitted size of 200*200.");
					throw new Exception("Image is larger than permitted size of 200*200.");
					
				}
				
				
				
				FileStorageService.uploadFile(profilePath,profileFileName, imageFile);		
				
				
				userRepo.save(user);				
				logger.info("Profile picture updated @ {}",profilePath);
				// delete the temporary file used.
				imageFile.delete();
				
				return "Profile picture updated !!";
				
				
				
				
			}
			
			else
			{
				logger.error("Profile picture content type is not acceptable.");
				throw new Exception("Only images (jpg, png, gif) are accepted for profile picture.");
				
			}			
			
		}
		
		else
		{
			logger.error("Profile picture file is null or empty for user: {}", uname);
			throw new Exception("Please upload profile picture");
		}
	}
	public void editUser(String uname, AppUser newUser, String role) throws Exception 
	{
		logger.info("Trying to edit User information for {}", uname);
		
		List<AppUser> users = userRepo.findByUserName(uname);
		
		if(users == null || users.size() ==0)
		{
			logger.error("No use found with specified username");
			throw new Exception("No use found with specified username");
		}
		
		else
		{
			AppUser oldUser = users.get(0);
			
			String email = newUser.getEmail();
			
			if(email == null)
			{
				logger.error("Email is null");
				throw new Exception ("Email should be provided");
			}
			
			if(!email.equals(oldUser.getEmail()))
			{
				logger.info("Email has been edited");
				
				AppUser u1 = userDao.findUserAccountByEmail(email);
				
				if(u1 != null)
				{
					logger.error("New email is already registered");
					throw new Exception("New email is already registered with other user");
				}
				
			}
			
			if(!EmailValidator.emailValidator(email))
			{
				logger.error("New email is invalid");
				throw new Exception("New email is invalid");
			}
			
			if(role.equalsIgnoreCase("ROLE_AUTHOR"))
			{
				oldUser.setTitle(newUser.getTitle());
				oldUser.setFullName(newUser.getFullName());
				oldUser.setAddress1(newUser.getAddress1());
				oldUser.setAddress2(newUser.getAddress2());
				oldUser.setPhone(newUser.getPhone());
				oldUser.setCity(newUser.getCity());
				oldUser.setState(newUser.getState());
				oldUser.setQualification(newUser.getQualification());
				oldUser.setProfession(newUser.getProfession());			
				oldUser.setEmail(newUser.getEmail());
			}
			else
			{
				oldUser.setTitle(newUser.getTitle());
				oldUser.setFullName(newUser.getFullName());
				oldUser.setAddress1(newUser.getAddress1());
				oldUser.setAddress2(newUser.getAddress2());
				oldUser.setPhone(newUser.getPhone());
				oldUser.setCity(newUser.getCity());
				oldUser.setState(newUser.getState());
				oldUser.setQualification(newUser.getQualification());
				oldUser.setProfession(newUser.getProfession());			
				oldUser.setEmail(newUser.getEmail());
				oldUser.setRole(newUser.getRole());// for admin right users only
			}		
			
			userRepo.save(oldUser);
			
			logger.info("Updated user information for {}",uname);
			
			
		}	
		
	}


	public void changePassword(String userName, PasswordChangeForm passwordChangeForm) throws Exception {
		
		logger.info("Trying to change password for {}",userName);
		AppUser user = userRepo.findByUserName(userName).get(0);
		
		if (user == null) {
			logger.error("Invalid User {} trying to change password",userName);
			throw new Exception("User is not valid");
		}
		
		String cPassword = user.getPassword();
				
		if(!EncryptedPasswordUtils.matches(passwordChangeForm.getCurrentPassword(), cPassword)) {
			logger.error("Current Password is not correct.");
			throw new Exception("Current password is not correct.");
		}
		
		else {
			String newPassword = passwordChangeForm.getPassword();
			String retypePassword = passwordChangeForm.getConfirmPassword();
			
			if(newPassword.equals(retypePassword)) {
				logger.info("Changing password for username {}",userName);
				String encryptedPassword = EncryptedPasswordUtils.encrytePassword(newPassword);
				
				user.setPassword(encryptedPassword);
				
				userRepo.save(user);
				
				logger.info("Password changed for user {}", userName);
				
				logger.info("Trying to send email to user {} after password change",user.getEmail());
				
				// send email to user
				
							Mail mail = new Mail();

				            
				            mail.addTo(user.getEmail());
				            mail.setSubject("Password reset");
				            mail.setContent("Dear "+user.getFullName()+",<br>"
				            		+ "Your Login password for craiaj.com has been changed. Following is your new password.<br>"		            		
				            		+ "UserName :"+user.getUserName()+"<br>"
				            		+ "Password :"+newPassword+"<br>"
				            		+ " Regards,<br>"
				            		+ "Administrator<br>"
				            		);
				            
				            try {
				     			emailService.sendHtmlMessage(mail);
				     			logger.info("Email sent to {} after password change",user.getEmail());
				     		} catch (MessagingException e) {
				     			logger.error("Could not send email to {} after password change",user.getEmail());
				     			e.printStackTrace();
				     		} catch (Exception e) {
				     			logger.error("Unknown error occured while sending email to {} after password change",user.getEmail());
								e.printStackTrace();
							}				
			}
			else {
				logger.error("New Password and retype password did not match");
				throw new Exception("New password and retype password did not match");
			}
		}		
	}
	
	// Private method for resetting password
	private void resetPassword(String userName, String newPassword) throws Exception 
	{
		
		logger.info("Trying to change password for {}",userName);
		AppUser user = userRepo.findByUserName(userName).get(0);
		
		if (user == null) {
			logger.error("Invalid User {} trying to change password",userName);
			throw new Exception("User is not valid");
		}
		
				
		else {
			
				logger.info("Changing password for username {}",userName);
				String encryptedPassword = EncryptedPasswordUtils.encrytePassword(newPassword);
				
				user.setPassword(encryptedPassword);
				
				userRepo.save(user);
				
				logger.info("Password changed for user {}", userName);
				
				logger.info("Trying to send email to user {} after password change",user.getEmail());
				
				// send email to user
				
							Mail mail = new Mail();

				            
				            mail.addTo(user.getEmail());
				            mail.setSubject("Password reset");
				            mail.setContent("Dear "+user.getFullName()+",<br>"
				            		+ "Your Login password for craiaj.com has been changed. Following is your new password.<br>"		            		
				            		+ "UserName :"+user.getUserName()+"<br>"
				            		+ "Password :"+newPassword+"<br>"
				            		+ " Regards,<br>"
				            		+ "Administrator<br>"
				            		);
				            
				            try {
				     			emailService.sendHtmlMessage(mail);
				     			logger.info("Email sent to {} after password change",user.getEmail());
				     		} catch (MessagingException e) {
				     			logger.error("Could not send email to {} after password change",user.getEmail());
				     			e.printStackTrace();
				     		} catch (Exception e) {
				     			logger.error("Unknown error occured while sending email to {} after password change",user.getEmail());
								e.printStackTrace();
							}				
			}
}

	public UserDto getUserByEmail(String email) {
		
		logger.info("Trying to search user with given email :"+email);
		List<UserDto> users = userRepo.findByEmail(email);
		
		if(users == null || users.size() == 0 )
		{
			logger.error("User not found with email :"+email);
			return null;
		}
		else
		{
			if (users.size() > 1)
				logger.error("Found more than one User with email "+email+". Only first user will be returned.");
				
			return users.get(0);
		}		
		
	}


	//fetch editor's profile pictures
	public Map<String,byte[]> getEditorsProfilePictures()
	{
		Map<String,byte[]> profileMap = new HashMap<String,byte[]>();
		
		List<AppUser> users = userDao.getAllEditors();
		
		if(users != null)
		{
			for(AppUser j: users)
			{
				byte[] bytes = getProfilePicture(j.getUserName());
				profileMap.put(j.getUserName(), bytes);
			}
		}
		
		
		return profileMap;
	}
	public void resetPassword(UserDto user) throws Exception 
	{
		
		logger.info("Resetting password for user {}", user.getUserName());
		String randomPassword = PasswordGenerator.getAlphaNumericString(8);
		
		
		resetPassword(user.getUserName(), randomPassword);
		
		logger.info("Password has been reset for user : {}",user.getUserName());		
		
	}

	private void registerAuthor(String email, String username, String password, String fullName) {
		
		logger.info("Regestering author..");
		AppUser user = new AppUser();
		
		String encryptedPassword = EncryptedPasswordUtils.encrytePassword(password);
		
		user.setEmail(email);
		user.setUserName(username);
		user.setPassword(encryptedPassword);
		user.setFullName(fullName);
		
		user.setRole("ROLE_AUTHOR");
		user.setEnabled((short) 1);
		
		userRepo.save(user);
		
		logger.info("Author {} is registered",username);
		logger.info("Trying to send email to new author");
		// send email to user
		
					Mail mail = new Mail();

		            
		            mail.addTo(user.getEmail());
		            mail.setSubject("You are now registered !!");
		            mail.setContent("Dear "+user.getFullName()+",<br>"
		            		+ "Congratulations!!<br>"
		            		+ "You are registered in our Journal Publication system as an author. <br>"
		            		+ "UserName :"+user.getUserName()+"<br>"
		            		+ "Password :"+user.getPassword()+"<br>"
		            		+ " Regards,<br>"
		            		+ "Administrator<br>"
		            		);
		            
		            try {
		     			emailService.sendHtmlMessage(mail);
		     			logger.info("Email sent to new author {}",user.getEmail());
		     		} catch (MessagingException e) {
		     			logger.error("Email could not be sent to the author {}",user.getEmail());
		     			e.printStackTrace();
		     		} catch (Exception e) {
		     			logger.error("Email could not be sent to the author {} due to unknown reason",user.getEmail());
						e.printStackTrace();
					}
		
	}

	public void editAuthorProfile(String username, AuthorProfileForm af) 
	{
		logger.info("Trying to edit Author Profile for {}", username);
		
		AppUser oldUser = userRepo.findByUserName(username).get(0);
		
		
			oldUser.setTitle(af.getTitle());
			oldUser.setFullName(af.getFullName());
			oldUser.setQualification(af.getQualification());
			oldUser.setAffiliation(af.getAffiliation());
			
			oldUser.setAddress1(af.getAddress());			
			oldUser.setPhone(af.getPhone());
			oldUser.setCity(af.getCity());
			oldUser.setState(af.getState());
			oldUser.setCountry(af.getCountry());
			
			oldUser.setAboutme(af.getAboutme());		
		
		
		userRepo.save(oldUser);
		
		logger.info("Updated author profile for {}",username);
		
	}

	public void registerAuthor(AuthorRegistrationForm arf) throws ExistingUserException 
	{
		
		String email = arf.getEmail();
		String fullName = arf.getFullName();
		String username = arf.getUsername();
		String password = arf.getPassword();
		String cpassword = arf.getCpassword();
		
		AppUser  user = userDao.findUserAccountByEmail(email);
		AppUser user1 = userDao.findUserAccount(username);
		
		if (user != null)
			throw new ExistingUserException("This email is already registered. If you forgot your login username/password, you can reset your password.");
		if (user1 != null)
			throw new ExistingUserException("This username is already taken. Please choose another username.");
		
		if (user == null && user1 == null)
		{
			if(password != null && cpassword != null && email != null && username != null
					&& password !="" &&cpassword !="" &&email !="" &&username !="")
				{
					if(password.equals(cpassword))
					{
						registerAuthor(email, username, password, fullName);
					}
				}
		}
		
		
	}

	public void editEditorProfile(String username, EditorProfileForm af) 
	{
		
		logger.info("Trying to edit Author Profile for {}", username);
		
		AppUser oldUser = userRepo.findByUserName(username).get(0);
		
		
			oldUser.setTitle(af.getTitle());
			oldUser.setFullName(af.getFullName());
			oldUser.setQualification(af.getQualification());
			oldUser.setAffiliation(af.getAffiliation());
			
			oldUser.setAddress1(af.getAddress());			
			oldUser.setPhone(af.getPhone());
			oldUser.setCity(af.getCity());
			oldUser.setState(af.getState());
			oldUser.setCountry(af.getCountry());
			
			oldUser.setAboutme(af.getAboutme());		
		
		
		userRepo.save(oldUser);
		
		logger.info("Updated author profile for {}",username);
		
	}

	public void deleteUser(String uname) throws Exception
	{
		AppUser user = getUserByUsername(uname);
    	
    	if(user == null)
    	{
    		logger.error("Invalid username");
    		throw new Exception("Invalid username");
    	}
    	
    	logger.info("deleting username {}",uname);
    	
    	
    	userRepo.delete(user);
    	
    	String fileName = user.getProfilePicture();	
		
		if (fileName != null)
			FileStorageService.deleteFile(profilePath,fileName);
    	
    	logger.info("Username {} is deleted",uname);
		
	}

	public void changePassword(String userName, String password) throws Exception 
	{
		logger.info("Trying to change password for {}",userName);
		AppUser user = userRepo.findByUserName(userName).get(0);
		
		if (user == null) 
		{
			logger.error("Invalid User",userName);
			throw new Exception("User is not valid");
		}
		
		logger.info("Changing password for username {}",userName);
		String encryptedPassword = EncryptedPasswordUtils.encrytePassword(password);
		
		user.setPassword(encryptedPassword);
		
		userRepo.save(user);
		
		logger.info("Password changed for user {}", userName);
		
		logger.info("Trying to send email to user {} after password change",user.getEmail());
		
		// send email to user
		
					Mail mail = new Mail();

		            
		            mail.addTo(user.getEmail());
		            mail.setSubject("Password reset");
		            mail.setContent("Dear "+user.getFullName()+",<br>"
		            		+ "Your Login password for craiaj.com has been changed. Following is your new password.<br>"		            		
		            		+ "UserName :"+user.getUserName()+"<br>"
		            		+ "Password :"+password+"<br>"
		            		+ " Regards,<br>"
		            		+ "Administrator<br>"
		            		);
		            
		            try {
		     			emailService.sendHtmlMessage(mail);
		     			logger.info("Email sent to {} after password change",user.getEmail());
		     		} catch (MessagingException e) {
		     			logger.error("Could not send email to {} after password change",user.getEmail());
		     			e.printStackTrace();
		     		} catch (Exception e) {
		     			logger.error("Unknown error occured while sending email to {} after password change",user.getEmail());
						e.printStackTrace();
					}
		
	}

}
