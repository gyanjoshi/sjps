package com.example.projectx.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.example.projectx.mail.Mail;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;
    
    @Value("${spring.mail.username}")
    private String username;
    
    private static final Logger logger=LoggerFactory.getLogger(EmailService.class);
    

    @Async
    public void sendSimpleMessage(Mail mail) throws MessagingException {
    	
    	logger.info("Sending simple email with no attachment to {}", mail.getTo());

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        String[] receipants = mail.getTo().stream().toArray(String[]::new);

        helper.setSubject(mail.getSubject());
        helper.setText(mail.getContent());
        
        helper.setTo(receipants);
        
        helper.setFrom(username);       
       

        emailSender.send(message);
        
        logger.info("Sent simple email with no attachment to {}", mail.getTo());

    }
    
    @Async
    public void sendSimpleMessage(Mail mail, File file) throws MessagingException {

    	logger.info("Sending simple email with single attachment to {}", mail.getTo());
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String[] receipants = mail.getTo().stream().toArray(String[]::new);
        
        helper.setSubject(mail.getSubject());
        helper.setText(mail.getContent());
        helper.setTo(receipants);
        
        helper.setFrom(username); 
        
        if (file.exists())
        	helper.addAttachment(file.getName(), file); 
        else
        	logger.error("File is not found. Sending email without attachment");
         

        emailSender.send(message);
        
        logger.info("Sending simple html email with no attachment to {}", mail.getTo());

    }
    
    @Async
    
    public void sendHtmlMessage(Mail mail, File file) throws MessagingException {

    	logger.info("Sending simple html email with single attachment");
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        String[] receipants = mail.getTo().stream().toArray(String[]::new);


        helper.setSubject(mail.getSubject());
        
        helper.setText(mail.getContent(),true);
        
        helper.setTo(receipants);
        
        helper.setFrom(username); 
        
        if (file.exists())
        	helper.addAttachment(file.getName(), file); 
        else
        	logger.error("File is not found. Sending email without attachment");
        

        emailSender.send(message);
        
        logger.info("Sent simple html email with single attachment");

    }
    @Async
    public void sendHtmlMessage(Mail mail, List<File> files) throws MessagingException {

    	logger.info("Sending simple html email with multiple attachment");
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        String[] receipants = mail.getTo().stream().toArray(String[]::new);

        helper.setSubject(mail.getSubject());
        
        helper.setText(mail.getContent(),true);
        helper.setTo(receipants);
        
        helper.setFrom(username); 
        
//        files.forEach(f -> helper.addAttachment(f.getName(), f));
        
        for(File file:files)
        {
        	if (file.exists())
            	helper.addAttachment(file.getName(), file); 
            else
            	logger.error("File {} is not found. Sending email without this attachment", file.getAbsolutePath());        	
        	
        }        
        

        emailSender.send(message);
        logger.info("Sent simple html email with multiple attachment");

    }
    @Async
    
    public void sendHtmlMessage(Mail mail) throws MessagingException {

    	
    	logger.info("Sending simple html email with no attachment to {}", mail.getTo());
    	
        MimeMessage message = emailSender.createMimeMessage();
        
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        String[] receipants = mail.getTo().stream().toArray(String[]::new);
        
        
           
        helper.setSubject(mail.getSubject());
        
        helper.setText(mail.getContent(),true);
        helper.setTo(receipants);
        helper.setFrom(username);     
            

        emailSender.send(message);
        
        logger.info("Sent simple html email with no attachment to {}", mail.getTo());

    }
    
    

}
