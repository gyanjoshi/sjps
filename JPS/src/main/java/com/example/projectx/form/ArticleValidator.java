/**
 * 
 */
package com.example.projectx.form;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author Gyan Prakash Joshi
 * Jan 31, 2021
 *
 */
public class ArticleValidator implements Validator {
	
	private static final List<String> articleContentTypes = Arrays.asList("application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
	
	private static final Logger logger=LoggerFactory.getLogger(ArticleValidator.class);

	@Override
	public boolean supports(Class<?> clazz) {
		return ArticleUploadForm.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		ArticleUploadForm arf = (ArticleUploadForm) target;
		
		MultipartFile mpf = arf.getFileData();
		
		Long[] cats = arf.getCategories();
		
		if(!arf.isUpdate()) 
		{ // Validation for new article submission
			if(mpf == null || mpf.isEmpty())			
				errors.reject("fileData", "Please select an article file to upload");
			else if (!articleContentTypes.contains(mpf.getContentType())) {
				errors.reject("fileData", "Unsupported file format detected. Please select a Microsoft word (.doc or .docx) file to upload");
				logger.info("Unsupported file format {}",mpf.getContentType());
			}		
		}
		else
		{ // Validation for update article
			
			if(mpf != null && !mpf.isEmpty())			
			{
				if (!articleContentTypes.contains(mpf.getContentType())) 
				{
					errors.reject("fileData", "Unsupported file format detected. Please select a Microsoft word (.doc or .docx) file to upload");
					logger.error("Unsupported file format {}",mpf.getContentType());
				}
			}
			
		}
		
		
		if(cats == null ||cats.length == 0)
			errors.reject("categories", "Please select at least one category");
		
		
		
	}
		
	}
