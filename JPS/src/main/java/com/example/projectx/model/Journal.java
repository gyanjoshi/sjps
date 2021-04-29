package com.example.projectx.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name = "Journals")
public class Journal {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="journal_seq_gen")
	@SequenceGenerator(name="journal_seq_gen", sequenceName="journal_seq")
	
	@Column(name="journal_id")
	private Integer Id;
	@Column(name="journal_title")
	private String JournalTopic;
	
	@Column(name="coverimagefile")
	private String coverImageFileName;
	
	@Column(name="printissn")
	private String printissn;
	
	@Column(name="onlineissn")
	private String onlineissn;
	
	@Column(name="publisher")
	private String publisher;
	
	@OneToMany(mappedBy = "journal", cascade = CascadeType.ALL)
    private Set<JournalIssue> issues;
	
	//private File coverPage;
	
	public Integer getId() {
		return Id;
	}
	public void setId(Integer id) {
		Id = id;
	}
	public String getJournalTopic() {
		return JournalTopic;
	}
	public void setJournalTopic(String journalTopic) {
		JournalTopic = journalTopic;
	}
	
	public String getCoverImageFileName() {
		return coverImageFileName;
	}
	public void setCoverImageFileName(String coverImageFileName) {
		this.coverImageFileName = coverImageFileName;
	}
	public String getPrintissn() {
		return printissn;
	}
	public void setPrintissn(String printissn) {
		this.printissn = printissn;
	}
	public String getOnlineissn() {
		return onlineissn;
	}
	public void setOnlineissn(String onlineissn) {
		this.onlineissn = onlineissn;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	
	
}
