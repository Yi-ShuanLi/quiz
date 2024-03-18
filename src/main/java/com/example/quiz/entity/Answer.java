package com.example.quiz.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.Table;

@Entity
//@IdClass(value=QuizId.class)
@Table(name="answer")
public class Answer {
	//�]����id�b�����ODB AI(Auto Incremental)�A�ҥH�n�[�W������
	//GrnerationType.IDENTITY �O�D�䪺�Ʀr�W����Ѹ�Ʈw
	//���ݩʪ���ƫ��A�OInteger�ɡA�n�[
	//���ݩʪ���ƫ��A�O int �ɡA�D�����A���Y�b�s�W��ƫ�(JPA ��SAVE)�A�Y�ɨ��o�s�W��ƪ��y�����A�N�n�[
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Id
	@Column(name="id")
	private int id;
	@Column(name="neame")
	private String neame;
	@Column(name="phone")
	private String phone;
	@Column(name="email")
	private String email;
	@Column(name="age")
	private int age;
	@Column(name="quizId")
	private int quizId;
	@Column(name="quId")
	private int quId;
	@Column(name="answer")
	private String answer;
	
	
	
	public Answer() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Answer(String neame, String phone, String email, int age, int quizId, int quId, String answer ) {
		super();
		this.neame = neame;
		this.phone = phone;
		this.email = email;
		this.age = age;
		this.quizId = quizId;
		this.quId = quId;
		this.answer = answer;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNeame() {
		return neame;
	}
	public void setNeame(String neame) {
		this.neame = neame;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
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
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	
	
}
