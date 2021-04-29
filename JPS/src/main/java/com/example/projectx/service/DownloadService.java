package com.example.projectx.service;


import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.projectx.form.AddDownloadForm;

import com.example.projectx.form.AddNoticeForm;

import com.example.projectx.model.Download;
import com.example.projectx.model.Notice;
import com.example.projectx.repository.DownloadRepository;
import com.example.projectx.repository.NoticeRepository;

@Service
public class DownloadService {
	@Autowired
	DownloadRepository downloadrepo;
	@Autowired
	NoticeRepository noticerepo;
	@Autowired
	FileStorageService fileservice;
	
	@Value("${upload.path.download}")
    private String downloadspath;
	
	@Value("${upload.path.article}")
    private String articlespath;
	
	@Value("${upload.path.journal}")
    private String journalspath;
	
	@Value("${upload.path.coverimage}")
    private String coverpagepath;
	
	@Value("${upload.path}")
    private String basepath;
	
	@Value("${logging.file}")
	private String logfile;
	
		
	@Autowired
	private DownloadRepository downloadRepo;
	
	@Autowired
	private NoticeRepository noticeRepo;
	
	private static final Logger logger=LoggerFactory.getLogger(DownloadService.class);
	
	
	public void addDownload(AddDownloadForm download)
	{
		logger.info("Adding download file..");
		String title = download.getDownloadTopic();
		if (title == null)
		{
			logger.error("Download title is null. exiting without adding download..");
			return;
		}
		MultipartFile file = download.getDownloadFilePath();
		
		if (file == null)
		{
			logger.error("Uploaded download file is NULL. exiting without adding download.");
			return;
		}
		if (file.isEmpty()) {
			logger.error("Uploaded download file is empty. exiting without adding download.");
			return;
		}
		// add in database
		// Download Object
		Download d = new Download();
		d.setDownloadTopic(title);
		d.setUploadedDate(new java.sql.Date(System.currentTimeMillis()));   
        
        
        Download obj = downloadRepo.save(d);        
		
		String downloadFileName = "Download_"+obj.getId()+"."+FilenameUtils.getExtension(file.getOriginalFilename());
		
		logger.info("Saving download file {} to filesystem path {}", downloadFileName, downloadspath);
		
		FileStorageService.uploadFile(downloadspath,downloadFileName, file );
		
		logger.info("Saved download file {} to filesystem path {}", downloadFileName, downloadspath);
		
		d.setDownloadFilePath(downloadFileName);
		
		downloadRepo.save(d);
		
		logger.info("Download entity saved");
		
	}
	
	public String getDownloadPath(String type)
	{
		if(type.equalsIgnoreCase("article"))
			return articlespath;
		else if (type.equalsIgnoreCase("journal"))
			return journalspath;
		else if	(type.equalsIgnoreCase("cover"))
			return coverpagepath;
		else if	(type.equalsIgnoreCase("download"))
			return downloadspath;
		else if	(type.equalsIgnoreCase("editorial"))
			return journalspath;
		else
			return basepath;
	}


	public String getLogFileName()
	{
		return logfile;
	}
	
	public void editDownload(int id, AddDownloadForm download)
	{
		
		logger.info("Preparing to edit download..");
		String title = download.getDownloadTopic();
		
		if (title == null)
		{
			logger.error("Download title is null. exiting without editing download..");
			return;
		}
		
		MultipartFile file = download.getDownloadFilePath();
		
		if (file == null || file.isEmpty())
		{
			logger.error("Uploaded download file is NULL or empty. exiting without editing download.");
			return;
		}
		
		// Deleting existing download file, if it exists.
		
		Download downloadobj = downloadrepo.getOne(id);
		String filename = downloadobj.getDownloadFilePath();
		
		logger.info("Deleting existing download file {} from filesystem path {}", filename, downloadspath);
		FileStorageService.deleteFile(downloadspath, filename);
		
		logger.info("Deleted existing download file {} from filesystem path {}", filename, downloadspath);
		
		String downloadFileName = "Download_"+id+"."+FilenameUtils.getExtension(file.getOriginalFilename());
        
		logger.info("Saving new download file {} to filesystem path {}", downloadFileName, downloadspath);
		FileStorageService.uploadFile(downloadspath,downloadFileName,file);
		logger.info("Saved new download file {} to filesystem path {}", downloadFileName, downloadspath);
		
        Date date = new java.sql.Date(System.currentTimeMillis());        


        downloadRepo.editDownload(id, title, downloadFileName, date);
        
        logger.info("Edited Download file successfully.");
	}

	
	public void addNotice(AddNoticeForm notice) throws Exception {
		
		logger.info("Preparing to add new notice..");
		String noticenumber  = notice.getNoticeNumber();
		String noticetitle = notice.getNoticeTitle();
		String noticetext = notice.getNoticeText();
		
		if (noticenumber == null || noticenumber.trim().isEmpty())
		{
			logger.error("Notice number is null or empty");
			throw new Exception("Notice number is not provided");
		}
		if (noticetitle == null || noticetitle.trim().isEmpty())
		{
			logger.error("Notice title is null or empty");
			throw new Exception("Notice title is not provided");
		}
		if (noticetext == null || noticetext.trim().isEmpty())
		{
			logger.error("Notice text is null or empty");
			throw new Exception("Notice Text is not provided");
		}

		
		Notice n = new Notice();
		
		n.setNoticeNumber(noticenumber);
		n.setNoticeText(noticetext);
		n.setNoticeTitle(noticetitle);
		//n.setNoticeFileName(noticefilename);
        n.setUploadedDate(new java.sql.Date(System.currentTimeMillis()));    
        
        
        noticeRepo.save(n);
        
        logger.info("Notice added successfully..");
        
		
	}

	public void editnotice(int id, AddNoticeForm notice) {
		
		logger.info("Preparing to edit notice of id {} ", id);
		
		String noticenumber  = notice.getNoticeNumber();
		String noticetitle = notice.getNoticeTitle();
		String noticetext = notice.getNoticeText();
		
		if (noticenumber == null)
		{
			logger.error("Notice number is null or empty");
			return;
		}
		if (noticetitle == null)
		{
			logger.error("Notice title is null or empty");
			return;
		}
		if (noticetext == null)
		{
			logger.error("Notice text is null or empty");
			return;
		}
		
		logger.info("Searching notice entity with id {}", id);
		
		noticeRepo.updatenotice(id,noticenumber,noticetitle,noticetext);
		
		logger.info("Notice updated successfully.");
		
	}

	public void attachFile(int id, MultipartFile file) {
		
		logger.info("Searching repository for notice id {}",id);
		Notice notice = noticeRepo.getOne(id);
		
		logger.info("Notice entity found for notice id {}",id);
		
		if (file == null || file.isEmpty())
		{
			logger.error("File is null or empty. Returning without attaching file.");
			return;
		}
		
		String noticeFileName = "Notice_"+id+"."+FilenameUtils.getExtension(file.getOriginalFilename());
		
		notice.setNoticeFileName(noticeFileName);
		
		FileStorageService.uploadFile(downloadspath,noticeFileName, file);
		
		logger.info("Uploaded notice file {} from file system path {}", noticeFileName, downloadspath);
		
		noticeRepo.save(notice);
		
		logger.info("Notice entity saved after attaching file");
		
		
		
	}

	public void deleteDownload(int id) {
		
		logger.info("Searching download repo for download id {}",id);
		Download download = downloadrepo.getOne(id);
		logger.info("found download entity for download id {}",id);
		String filename = download.getDownloadFilePath();
		if (filename != null) {
			logger.info("Trying to delete download file {} from file system path {}", filename, downloadspath);
			FileStorageService.deleteFile(downloadspath, filename);
			logger.info("Deleted download file {} from file system path {}", filename, downloadspath);
		}
		else {
			logger.error("Download file is NULL");
		}
		
		downloadrepo.deleteById(id);
		logger.info("Download entity with id {} is deleted", id);
		
	}
	
	public void deleteNotice(int id) {
		logger.info("Searching notice repo for notice id {}",id);
		Notice notice = noticerepo.getOne(id);
		logger.info("Found notice entity for notice id {}",id);
		String filename = notice.getNoticeFileName();
		
		if (filename != null) {
			logger.info("Trying to delete notice file {} from file system path {}", filename, downloadspath);
			FileStorageService.deleteFile(downloadspath, filename);
			logger.info("Deleted notice file {} from file system path {}", filename, downloadspath);
		}
		else {
			logger.error("Download file is NULL");
		}
		noticeRepo.deleteById(id);
		logger.info("Notice entity with id {} is deleted.",id);
		
	}
}
