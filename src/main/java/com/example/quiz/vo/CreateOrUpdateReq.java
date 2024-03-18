package com.example.quiz.vo;


import java.util.List;

import com.example.quiz.entity.Quiz;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateOrUpdateReq {
	@JsonProperty("quiz_list")
	private List <Quiz> quizList;
	@JsonProperty("is_published")
	private boolean published;
	
	public CreateOrUpdateReq(List<Quiz> quizList, boolean published) {
		super();
		this.quizList = quizList;
		this.published = published;
	}
	public CreateOrUpdateReq() {
		super();
		// TODO Auto-generated constructor stub
	}
	public List<Quiz> getQuizList() {
		return quizList;
	}
	public void setQuizList(List<Quiz> quizList) {
		this.quizList = quizList;
	}
	public boolean isPublished() {
		return published;
	}
	public void setPublished(boolean published) {
		this.published = published;
	}
	
}
