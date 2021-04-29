package com.example.projectx.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Component;

import com.example.projectx.service.UserDetailsServiceImpl;

public class CustomSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler  {

	private static final Logger logger=LoggerFactory.getLogger(CustomSuccessHandler.class);
	
	public static final String REDIRECT_URL_SESSION_ATTRIBUTE_NAME = "REDIRECT_URL";
	
	@Autowired
	private UserDetailsServiceImpl userInfoService;
    
        public CustomSuccessHandler(String defaultTargetUrl) {        	
        	
            setDefaultTargetUrl(defaultTargetUrl);
            setUseReferer(true);
        }
     @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException 
    {
           
            String targetUrl = determineTargetUrl(request,authentication);
            
            Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
            String username = loggedInUser.getName();           
            HttpSession session = request.getSession();
            session.setAttribute("userInfo", userInfoService.loadUserByUsername(username));      
            
            logger.info("User: {} logged in successfully..", username);
            
            logger.info("Redirecting to {} after successfull login..", targetUrl);
          
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
            
            logger.info("Redirected to {} after successfull login..", targetUrl);
            
            
            
            
        }


    /*
     * This method extracts the roles of currently logged-in user and returns
     * appropriate URL according to his/her role.
     */
    protected String determineTargetUrl(HttpServletRequest request, Authentication authentication) {
        String url = "";

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        List<String> roles = new ArrayList<String>();
           
        
        
        
        for (GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }

        if (isAdmin(roles)) {
            url = "/admin/user";
        } else if (isEditor(roles)) {
            url = "/editor";
        }
        else if (isAuthor(roles)) {
            url = "/author";
        }
        else {
            url = "/";
        }
        
        DefaultSavedRequest defaultSavedRequest = (DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        if(defaultSavedRequest != null)
           url = defaultSavedRequest.getRedirectUrl();        

        return url;
    }

    private boolean isEditor(List<String> roles) {
    	if (roles.contains("ROLE_EDITOR")) {
            return true;
        }
		return false;
	}

	private boolean isAuthor(List<String> roles) {
        if (roles.contains("ROLE_AUTHOR")) {
            return true;
        }
        return false;
    }

    private boolean isAdmin(List<String> roles) {
        if (roles.contains("ROLE_ADMIN")) {
            return true;
        }
        return false;
    }
}
