package com.example.projectx.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "Slider")

public class Slider {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="slider_seq_gen")
	@SequenceGenerator(name="slider_seq_gen", sequenceName="slider_seq")
	
	@Column(name="slide_id")
	private Integer Id;
	@Column(name="topic")
	private String topic;
	@Column(name="subtopic")
	private String subtopic;
	@Column(name="status")
	private String status;
	@Column(name="image_filename")
	private String filename;
	public Integer getId() {
		return Id;
	}
	public void setId(Integer id) {
		Id = id;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getSubtopic() {
		return subtopic;
	}
	public void setSubtopic(String subtopic) {
		this.subtopic = subtopic;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	
	

}
