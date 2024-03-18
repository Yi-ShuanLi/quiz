package com.example.quiz.vo;

import java.util.List;

import com.example.quiz.entity.Quiz;

public class SearchRes extends BaseRes {
	
	private List <Quiz> quizList;
	

	public SearchRes(int code, String message, List<Quiz> quizList) {
		super(code, message);
		this.quizList = quizList;
	}
	
	public SearchRes(int code, String message) {
		super(code, message);
		// TODO Auto-generated constructor stub
	}
	
	public SearchRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<Quiz> getQuizList() {
		return quizList;
	}

	public void setQuizList(List<Quiz> quizList) {
		this.quizList = quizList;
	}

	
}
