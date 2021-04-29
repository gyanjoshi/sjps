package com.example.projectx.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.projectx.model.Category;

public interface CategoryRepository extends JpaRepository<Category , Long> {
	
	@Query("SELECT new com.example.projectx.model.Category(b.category) \r\n" + 
			"FROM Article a\r\n" + 
			"join a.categories b\r\n" + 
			"where a.Id=:articleId")
	public List<Category> getAllCategories(Integer articleId);

}
