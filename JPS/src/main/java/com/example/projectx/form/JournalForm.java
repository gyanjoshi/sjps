package com.example.projectx.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class JournalForm {
	
	private Integer Id;
	
	@NotNull(message = "Journal title can not be null!!")
    @NotEmpty(message = "Journal title can not be empty!!")	
	private String JournalTopic;
	
	
	private String printissn;
	
	@NotNull(message = "Online ISSN can not be null!!")
    @NotEmpty(message = "Online ISSN can not be empty!!")	
	private String onlineissn;
	
	@NotNull(message = "Publisher can not be null!!")
    @NotEmpty(message = "Publisher title can not be empty!!")	
	private String publisher;

	public String getJournalTopic() {
		return JournalTopic;
	}

	public void setJournalTopic(String journalTopic) {
		JournalTopic = journalTopic;
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

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}
	
	

}
