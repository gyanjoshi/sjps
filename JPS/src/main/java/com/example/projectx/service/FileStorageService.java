package com.example.projectx.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.projectx.exception.StorageException;


@Service
public class FileStorageService {
	
	

	private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    public static void uploadFile(String path, String fileName, MultipartFile file) 
    {
    	
    	logger.info("Trying to upload the file {} and save it on file system path {} with file name {}",file.getOriginalFilename(), path, fileName);
    	
    	String directoryPath = null;

        if (file.isEmpty()) {
        	logger.error("File provided for upload is empty");
            throw new StorageException("Failed to store empty file");
        }
        
        String separator = File.separator;
        
        if(path.endsWith(separator))
        	directoryPath = path.substring(0, path.length()-1);
        else
        	directoryPath = path;
        
        File directory = new File(directoryPath);
        
        logger.info("Trying to create directory {}, if it does not exist",directoryPath);
        
        boolean status = directory.mkdirs();
        
        
        if(status)
        	logger.info("Directory created: {} ",directoryPath);
        	
        else
        	logger.info("Directory already exists: {}", directoryPath);

        logger.info("Trying to read the uploaded file {}", file.getOriginalFilename());
        try {	           
	            InputStream is = file.getInputStream();
	            Files.copy(is, Paths.get(directoryPath + separator+fileName),StandardCopyOption.REPLACE_EXISTING);
	            
	            logger.info("Uploaded file {} saved at {} with file name {}", file.getOriginalFilename(), directoryPath, fileName);
        } catch (IOException e) {

            String msg = String.format("Failed to store file", file.getOriginalFilename());
            
            logger.error("Failed to store file: {}", file.getOriginalFilename());

            throw new StorageException(msg, e);
        }       
        

    }
    public  static File multipartToFile(MultipartFile multipart, String fileName) throws IllegalStateException, IOException {
	    File convFile = new File(System.getProperty("java.io.tmpdir")+"/"+fileName);
	    logger.info("temp file name path= {}", convFile.getAbsolutePath());
	    multipart.transferTo(convFile);
	    return convFile;
	}
    
	public static void deleteFile(String path, String file) {
		
		logger.info("Trying to delete file {} @ {}", file, path);
		File _file = new File(path+file);
		
		if(_file.exists()) {
			_file.delete();
			logger.info("File {} deleted @ {}", file, path);
		}
		else
		{
			logger.error("File {} does not exist at {}", file, path);
		}		
		
		
	}
	
	public static int getPageCount(String path, String file)
	{
		logger.info("Trying to get page count on the document {} at path {}", file, path);
		
		File _file = new File(path+file);
		
		PDDocument doc = null;
		int count = 0;
		try {
			doc = PDDocument.load(_file);
			count = doc.getNumberOfPages();
			logger.info("Paged count calculated in file {} at path {}", file, path);
		} catch (IOException e) {
			logger.error("Count not count pages in file {} at path {}", file, path);
			e.printStackTrace();
		}
		finally
		{
			logger.info("Trying to close the pdf document {}", file);
			try {
				doc.close();
				logger.info("PDF document {} is successfully closed.",file);
			} catch (IOException e) {
				logger.error("Could not close PDF document {}",file);
				e.printStackTrace();
			}
		}
		return count;
	}
	public static void uploadFile(String path, String fileName, File file) {
		// copy the file imageFile to new location
		logger.info("Trying to upload the file {} @ {}",fileName, path);
    	
    	String directoryPath = null;
    	
    	if (file == null) {
    		logger.error("File object is null ");
    		throw new StorageException("File object is null");
    	}
    	

    	if(!file.exists()) {
		    
		    	logger.error("File provided for upload does not exist");
		        throw new StorageException("Failed to store non-existing file");
		    }
		else {
			String separator = File.separator;
		    
		    if(path.endsWith(separator))
		    	directoryPath = path.substring(0, path.length()-1);
		    else
		    	directoryPath = path;
		    
		    File directory = new File(directoryPath);
		    
		    logger.info("Trying to create directory {}, if it does not exist",directoryPath);
		    
		    boolean status = directory.mkdirs();
		    
		    
		    if(status)
		    	logger.info("Directory created: {} ",directoryPath);
		    	
		    else
		    	logger.info("Directory already exists: {}", directoryPath);
		    
		    logger.info("Trying to copy the file from temp dir to the path {}", directoryPath + separator+fileName);
		    
		        	        
		    try {	           
		    	Files.copy(file.toPath(), Paths.get(directoryPath + separator+fileName), StandardCopyOption.REPLACE_EXISTING);
		    	logger.info("Uploaded file {} saved at {} with file name {}", file.getName(), directoryPath, fileName);
		    } catch (IOException e) {

		        String msg = String.format("Failed to store file ",file.getName());
		        
		        logger.error("Failed to store file: {}", file.getName());

		        throw new StorageException(msg, e);
		    } 
		}
		
	}

}
