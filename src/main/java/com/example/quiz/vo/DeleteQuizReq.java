package com.example.quiz.vo;

import java.util.List;

public class DeleteQuizReq {
	private List <Integer> quizIds;

	public List<Integer> getQuizIds() {
		return quizIds;
	}

	public void setQuizIds(List<Integer> quizIds) {
		this.quizIds = quizIds;
	}

	public DeleteQuizReq(List<Integer> quizIds) {
		super();
		this.quizIds = quizIds;
	}

	public DeleteQuizReq() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
