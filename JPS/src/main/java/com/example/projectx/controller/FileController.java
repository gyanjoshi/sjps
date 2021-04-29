package com.example.projectx.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.projectx.form.ArticleUploadForm;
import com.example.projectx.service.ArticleService;
import com.example.projectx.service.DownloadService;

//imported for download part
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import javax.servlet.http.HttpServletResponse;

import org.springframework.util.FileCopyUtils;

@Controller
public class FileController {

	
	@Autowired
    private ArticleService articleService;
	
	@Autowired
	private DownloadService downloadService;


	
	@RequestMapping(value = { "/upload" }, method = RequestMethod.GET)
    public String singleFileUpload(HttpServletRequest request,Model model) {		
        
		ArticleUploadForm articleUploadForm = new ArticleUploadForm();
	    model.addAttribute("articleUploadForm", articleUploadForm);
//		request.setAttribute("articleUploadForm", articleUploadForm);
	      
        return "fileupload";
    }
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	   public String uploadOneFileHandlerPOST(HttpServletRequest request, //
	         Model model, //
	         @ModelAttribute("articleUploadForm") ArticleUploadForm articleUploadForm,

	         BindingResult errors,
	         RedirectAttributes redirectAttributes
	         ) 
		{
			String message = doUpload(request, model, articleUploadForm);
			redirectAttributes.addFlashAttribute("message", message);
			
			return "redirect:/";
			
	 
	   }
	
	private String doUpload(HttpServletRequest request, Model model, ArticleUploadForm articleUploadForm) {
		
			String topic = articleUploadForm.getTopic();
			String abs = articleUploadForm.getArticleAbstract();
			MultipartFile file = articleUploadForm.getFileData();
	
			String message = null;
	      
		if (file.isEmpty()) 
	        {

				message =  "Please select a file to upload";
				return message;
	            
	        } 
	        
	        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
	        String username = loggedInUser.getName();
	    	System.out.println("Author="+username);    	
	    	articleService.saveArticle(topic,abs, file, username);
	    	
	    	message = "Article uploaded successfully.";
	       
	        return message;
	    }

	
	

	
	//start of download controller
	
	@RequestMapping("/download")
	public void downloadPDFResource(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("file") String fileName,
			@RequestParam("type") String type) throws IOException 
	{
		
		String path = downloadService.getDownloadPath(type);
		
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
			//response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

			 //Here we have mentioned it to show as attachment
			 response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() + "\""));

			response.setContentLength((int) file.length());

			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

			FileCopyUtils.copy(inputStream, response.getOutputStream());

		}
	}
	
	

//	@RequestMapping("/download/file/{fileName:.+}")
//	public void downloadPDFResource(HttpServletRequest request, HttpServletResponse response,
//			@PathVariable("fileName") String fileName) throws IOException {
//		
//		String path = downloadService.getDownloadPath();
//		
//		File file = new File(path + fileName);
//		if (file.exists()) {
//
//			//get the mimetype
//			String mimeType = URLConnection.guessContentTypeFromName(file.getName());
//			if (mimeType == null) {
//				//unknown mimetype so set the mimetype to application/octet-stream
//				mimeType = "application/octet-stream";
//			}
//
//			response.setContentType(mimeType);
//
//			/**
//			 * In a regular HTTP response, the Content-Disposition response header is a
//			 * header indicating if the content is expected to be displayed inline in the
//			 * browser, that is, as a Web page or as part of a Web page, or as an
//			 * attachment, that is downloaded and saved locally.
//			 * 
//			 */
//
//			/**
//			 * Here we have mentioned it to show inline
//			 */
//			//response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
//
//			 //Here we have mentioned it to show as attachment
//			 response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() + "\""));
//
//			response.setContentLength((int) file.length());
//
//			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
//
//			FileCopyUtils.copy(inputStream, response.getOutputStream());
//
//		}
//	}

	

}
