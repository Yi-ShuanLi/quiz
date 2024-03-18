package com.example.quiz.vo;

import java.util.List;

import com.example.quiz.entity.Answer;

public class AnswerReq {
	private List <Answer> AnswerList;

	
	
	public AnswerReq() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AnswerReq(List<Answer> answerList) {
		super();
		AnswerList = answerList;
	}

	public List<Answer> getAnswerList() {
		return AnswerList;
	}

	public void setAnswerList(List<Answer> answerList) {
		AnswerList = answerList;
	}
	
	
}
