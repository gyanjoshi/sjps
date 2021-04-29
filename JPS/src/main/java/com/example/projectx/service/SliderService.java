package com.example.projectx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.example.projectx.model.Slider;
import com.example.projectx.repository.SliderRepository;

public class SliderService {
	
	@Value("${slider.path}")
    private String path;
	
	@Autowired
    private SliderRepository sliderRepo;
	
	public void saveSlide(String topic, String subtopic, String status, MultipartFile file)
	{
		String fileName = file.getOriginalFilename();
		Slider sl = new Slider();
		
		sl.setFilename(fileName);
		sl.setTopic(topic);
		sl.setSubtopic(subtopic);
		sl.setStatus(status);
		
		sliderRepo.save(sl);
		
		
	}

}
