package com.example.quiz.repository;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.quiz.entity.Quiz;
import com.example.quiz.entity.QuizId;
@Repository
@Transactional
public interface QuizDao extends JpaRepository<Quiz,QuizId> {
	public boolean existsByQuizId(int quizId);
	public List <Quiz> findByQuizNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(String quizName ,LocalDate starDate ,LocalDate endDate);
	public List <Quiz> findByQuizNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndPublishedTrue(String quizName ,LocalDate starDate ,LocalDate endDate);
	public List <Quiz> findAllByQuizIdIn(List <Integer > quizIds);
	
	public void deleteAllByQuizIdInAndPublishedFalseOrQuizIdInAndStartDateAfter(//
			List <Integer > quizIds1 ,List <Integer > quizIds2,LocalDate now);
	public void deleteByQuizIdAndQuIdInAndPublishedFalseOrQuizIdAndQuIdInAndStartDateAfter(//
			int quizId1,List <Integer> quIds1,int quizId2,List <Integer> quIds2,LocalDate now);
	public List <Quiz> findByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfterOrderByQuId(//
			int quizId1,int quizId2,LocalDate now);
	public boolean existsByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfter(//
			int quizId1,int quizId2,LocalDate now);
	public void deleteByQuizId(int quizId);
	//select ���O�s�W�R���ק�A���ݭn�[@Modifying
	@Query(value="select qu_id from quiz where quiz_id=?1 And Necessary=true ",nativeQuery=true) //�����h��Ʈw���A�gtrue ;��entity�ݩʥi�H�gfalse
	public List <Integer> findQuIdByQuizIdAndNecessaryTrue(int quizId);
	public List <Quiz> findByQuizId(int quizId);
}
