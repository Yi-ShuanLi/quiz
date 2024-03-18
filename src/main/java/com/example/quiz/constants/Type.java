package com.example.quiz.constants;

public enum Type {
	SIGNLE_CHOICE("single choice"),//
	MULTI_CHOICE("multi choice"),//
	TEXT("text");
	
	private String type;

	private Type(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
