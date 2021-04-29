/**
 * 
 */
package com.example.projectx.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author Gyan Prakash Joshi
 * Apr 7, 2021
 *
 */
@Entity
@Table(name = "Announcement_types")
public class AnnouncementType implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="atype_seq_gen")
	@SequenceGenerator(name="atype_seq_gen", sequenceName="atype_seq")
	
	@Column(name="atype_id")
	private Long Id;
	
	@Column(name="atype")
	private String announcementType;

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getAnnouncementType() {
		return announcementType;
	}

	public void setAnnouncementType(String announcementType) {
		this.announcementType = announcementType;
	}
	
	

}
