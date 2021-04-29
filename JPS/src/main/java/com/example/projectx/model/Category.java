package com.example.projectx.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "Categories")
public class Category implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="category_seq_gen")
	@SequenceGenerator(name="category_seq_gen", sequenceName="category_seq")
	
	@Column(name="cat_id")
	private Long Id;
	
	@Column(name="category")
	private String category;
	
	@ManyToMany(mappedBy = "categories")
    private Set<Article> articles = new HashSet<Article>();
	
	public Category() {
		
	}
	public Category(String category) {
		this.category = category;
	}
	
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Set<Article> getArticles() {
		return articles;
	}
	public void setArticles(Set<Article> articles) {
		this.articles = articles;
	}
	
	public void addArticle(Article a)
	{
		articles.add(a);
	}
	public void removeArticle(Article a)
	{
		System.out.println("removing article "+a.getId()+" for category "+this.category);
		articles.remove(a);
	}
	
	

}
