package com.example.projectx.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;

/**
 * 
 * @author Gyan Prakash Joshi
 * Mar 8, 2021
 * 
 * This is parent class for AppUser, Editor, Reviewer
 * It encapsulates all person attributes
 *
 */

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Person {
	
	@Id    
	@GeneratedValue(strategy=GenerationType.AUTO, generator="person_seq_gen")
	@SequenceGenerator(name="person_seq_gen", sequenceName="person_seq")
    protected Long id;
	
	@Column(name="full_name")	
	private String fullName;
	@Column(name="address1")
	private String address1;
	@Column(name="address2")
	private String address2;
	@Column(name="city")
	private String city;
	@Column(name="state")
	private String state;
	@Column(name="email")
	private String email;
	@Column(name="country")	
	private String country;
	@Column(name="phone")
	private String phone;
	
	@Column(name="aboutme")
	private String aboutme;
	
	@Column(name="title")
	private String title;
	
	@Column(name="profession")
	private String profession;
	
	@Column(name="qualification")
	private String qualification;
	
	@Column(name="affiliation")
	private String affiliation;
	
	@ManyToMany(mappedBy = "reviewers")
	private Set<Article> articles = new HashSet<Article>();
	
	public Person(String name) {
		this.fullName = name;
	}
	
	public Person() {
		super();
	}

	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


//	public Set<Article> getArticles() {
//		return articles;
//	}
//
//
//	public void setArticles(Set<Article> articles) {
//		this.articles = articles;
//	}


	public String getFullName() {
		return fullName;
	}


	public void setFullName(String fullName) {
		this.fullName = fullName;
	}


	public String getAddress1() {
		return address1;
	}


	public void setAddress1(String address1) {
		this.address1 = address1;
	}


	public String getAddress2() {
		return address2;
	}


	public void setAddress2(String address2) {
		this.address2 = address2;
	}


	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getAboutme() {
		return aboutme;
	}


	public void setAboutme(String aboutme) {
		this.aboutme = aboutme;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getProfession() {
		return profession;
	}


	public void setProfession(String profession) {
		this.profession = profession;
	}


	public String getQualification() {
		return qualification;
	}


	public void setQualification(String qualification) {
		this.qualification = qualification;
	}


	public String getAffiliation() {
		return affiliation;
	}


	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}
	
	public void assignArticle(Article a) {
		// TODO Auto-generated method stub
		articles.add(a);
		
	}

	public void revokeArticle(Article a) {
		// TODO Auto-generated method stub
		articles.remove(a);
		
	}

	public boolean isAssigned(Article a) {
		// TODO Auto-generated method stub
		
		return articles.contains(a);
		
	}
	
	@Override
	public String toString() {
		return fullName +"@"+id;
		
	}

}
