package com.example.projectx.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "Page")

public class Page {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="page_seq_gen")
	@SequenceGenerator(name="page_seq_gen", sequenceName="page_seq")
	
	@Column(name="page_id")
	private Integer Id;
	@Column(name="title")
	private String title;
	@Column(name="subtitle")
	private String subtitle;
	@Column(name="url")
	private String url;
	
	@Column (name="html")
	@Type(type="text")
	private String html;
	@Column(name="added_date")
	private Date added_date;
	
	@Column(name="modified_date")
	private Date modified_date;

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public Date getAdded_date() {
		return added_date;
	}

	public void setAdded_date(Date added_date) {
		this.added_date = added_date;
	}

	public Date getModified_date() {
		return modified_date;
	}

	public void setModified_date(Date modified_date) {
		this.modified_date = modified_date;
	}
	
	
	
	
}
