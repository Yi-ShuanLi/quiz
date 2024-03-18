package com.example.quiz.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@IdClass(value=QuizId.class)
@Table(name="quiz")
public class Quiz {
	@Id
	@Column(name="quiz_id")
	@JsonProperty("quiz_id")
	private int quizId;
	
	@Id
	@Column(name="qu_id")
	@JsonProperty("qu_id")
	private int quId;
	
	@Column(name="quiz_name")
	@JsonProperty("quiz_name")
	private String quizName;
	
	@Column(name="quiz_decription")
	@JsonProperty("quiz_decription")
	private String quizDecription;
	
	@Column(name="start_date")
	@JsonProperty("start_date")
	private LocalDate startDate;
	
	@Column(name="end_date")
	@JsonProperty("end_date")
	private LocalDate endDate;
	
	@Column(name="question")
	@JsonProperty("question_name")
	private String question;
	
	@Column(name="type")
	@JsonProperty("question_type")
	private String type;
	
	@Column(name="necessary")
	@JsonProperty("is_necessary")
	private boolean necessary;
	
	@Column(name="options")
	@JsonProperty("question_options")
	private String options;
	
	@Column(name="published")
	@JsonProperty("is_published")
	private boolean published;
	
	
	
	
	public Quiz(int quizId, int quId, String quizName, String quizDecription, LocalDate startDate, LocalDate endDate,
			String question, String type, boolean necessary, String options,boolean published) {
		super();
		this.quizId = quizId;
		this.quId = quId;
		this.quizName = quizName;
		this.quizDecription = quizDecription;
		this.startDate = startDate;
		this.endDate = endDate;
		this.question = question;
		this.type = type;
		this.necessary = necessary;
		this.options = options;
		this.published=published;
	}
	public Quiz() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int getQuizId() {
		return quizId;
	}
	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}
	public int getQuId() {
		return quId;
	}
	public void setQuId(int quId) {
		this.quId = quId;
	}
	public String getQuizName() {
		return quizName;
	}
	public void setQuizName(String quizName) {
		this.quizName = quizName;
	}
	public String getQuizDecription() {
		return quizDecription;
	}
	public void setQuizDecription(String quizDecription) {
		this.quizDecription = quizDecription;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isNecessary() {
		return necessary;
	}
	public void setNecessary(boolean necessary) {
		this.necessary = necessary;
	}
	public String getOptions() {
		return options;
	}
	public void setOptions(String options) {
		this.options = options;
	}
	public boolean isPublished() {
		return published;
	}
	public void setPublished(boolean published) {
		this.published = published;
	}
	
}
