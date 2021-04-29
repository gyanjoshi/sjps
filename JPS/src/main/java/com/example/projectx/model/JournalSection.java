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
 * Mar 22, 2021
 * This is an entity class representing Journal sections like 'Editorial', 'Article', Interview etc. 
 * Every article in journal should be assigned a section while publishing the journal
 *
 */
@Entity
@Table(name = "Journal_Sections")
public class JournalSection implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="section_seq_gen")
	@SequenceGenerator(name="section_seq_gen", sequenceName="section_seq")
	
	@Column(name="section_id")
	private Long Id;
	
	@Column(name="section_name")
	private String sectionName;
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
}
