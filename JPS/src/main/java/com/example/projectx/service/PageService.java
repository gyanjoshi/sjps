package com.example.projectx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.projectx.model.Page;
import com.example.projectx.repository.PageRepository;

@Service
public class PageService {

	@Autowired
	PageRepository pageRepo;
	
	public void savePage(Page page)
	{
		pageRepo.save(page);
	}
}
