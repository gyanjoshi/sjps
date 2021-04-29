/**
 * 
 */
package com.example.projectx.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author Gyan Prakash Joshi
 * Apr 7, 2021
 *
 */
@Entity
@Table(name = "Announcements")
public class Announcement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="announcement_seq_gen")
	@SequenceGenerator(name="announcement_seq_gen", sequenceName="announcement_seq")
	
	@Column(name="announcement_id")
	private Long Id;
	
	@Column(name="title")
	private String title;
	
	@Lob
	private String shortDescription;
	
	@Lob
	private String fullTextDescription;
	
	private Date creationDate;
	
	private Date modifiedDate;
	
	private Date ExpiryDate;
	
	@ManyToOne
    @JoinColumn
	private AnnouncementType announcementType;

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getFullTextDescription() {
		return fullTextDescription;
	}

	public void setFullTextDescription(String fullTextDescription) {
		this.fullTextDescription = fullTextDescription;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Date getExpiryDate() {
		return ExpiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		ExpiryDate = expiryDate;
	}

	public AnnouncementType getAnnouncementType() {
		return announcementType;
	}

	public void setAnnouncementType(AnnouncementType announcementType) {
		this.announcementType = announcementType;
	}
	
	public String getAnnouncementTypeString()
	{
		return announcementType.getAnnouncementType();
	}
}
