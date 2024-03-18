package com.example.quiz.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.quiz.constants.RtnCode;
import com.example.quiz.entity.Answer;
import com.example.quiz.entity.Quiz;
import com.example.quiz.repository.AnswerDao;
import com.example.quiz.repository.QuizDao;

import com.example.quiz.service.ifs.QuizService;
import com.example.quiz.vo.CreateOrUpdateReq;
import com.example.quiz.vo.DeleteQuizReq;
import com.example.quiz.vo.SearchRes;
import com.example.quiz.vo.StatisticsRes;
import com.example.quiz.vo.AnswerReq;
import com.example.quiz.vo.BaseRes;



@Service
public class QuizServiceImpl implements QuizService{
	@Autowired
	private QuizDao quizDao;
	private AnswerDao answerDao;
	@Override
	public BaseRes create(CreateOrUpdateReq req) {
		return checkParams(req,true);
	}

	@Override
	public SearchRes search(String quizName, LocalDate starDate, LocalDate endDate,boolean isBackend) {
		if(!StringUtils.hasText(quizName)) {
			quizName="";//countaining 代的參數值為空字串，表示會撈取全部
		}
		if(starDate==null) {
			starDate = LocalDate.of(1970, 1, 1);//將開始時間設定為很早之前的時間
		}
		if(endDate==null) {
			endDate=LocalDate.of(2099, 12, 31);//將結束時間設定為在很久之後的時間
		}
		if(isBackend) {
			return new SearchRes (RtnCode.SUCCESS.getCode(),RtnCode.SUCCESS.getMessage(),
					quizDao.findByQuizNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual
					(quizName,starDate,endDate));
		}else {
			return new SearchRes (RtnCode.SUCCESS.getCode(),RtnCode.SUCCESS.getMessage(),
					quizDao.findByQuizNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndPublishedTrue
					(quizName,starDate,endDate));
		}		
	}

	@Override
	public BaseRes deleteQuiz(DeleteQuizReq req) {
		if(CollectionUtils.isEmpty(req.getQuizIds()) ) {//同時判斷quizIds是否為null以及空集合
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(),RtnCode.PARAM_ERROR.getMessage());
		}
		quizDao.deleteAllByQuizIdInAndPublishedFalseOrQuizIdInAndStartDateAfter(//
				req.getQuizIds() ,req.getQuizIds(),LocalDate.now());		
		return new SearchRes (RtnCode.SUCCESS.getCode(),RtnCode.SUCCESS.getMessage());
	}

	@Override
	public BaseRes deleteQuestions(int quizId,List <Integer> quIds) {
		if(quizId<=0 ||CollectionUtils.isEmpty(quIds)) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(),RtnCode.PARAM_ERROR.getMessage());
		}
		// 根據(quizId問卷ID and 未發布 ) OR (quizId問卷ID and 尚未開始)找問卷，已經把該問卷所有能刪的題都找出了
		List <Quiz> res=quizDao.findByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfterOrderByQuId(//
				quizId,quizId,LocalDate.now());
		if(res.isEmpty()) {
			return new BaseRes(RtnCode.QUIZ_NOT_FOUND.getCode(),RtnCode.QUIZ_NOT_FOUND.getMessage());
		}
		//使用for-each不能對遍歷的對象做增加刪除
//		int j=0;
//		for(int item :quIds) {// quId=1 ,4
//			// 1:j=0,item=1,item-1-j=1-1-0=0:
//			// 2:j=0,item=4,item-1-j=4-1-1=2:
//			res.remove(item-1-j);
//			j++;
//		}
//		for(int i=0; i<res.size() ;i++) {
//			res.get(i).setQuId(i+1);
//		}
		List <Quiz> retainList =new ArrayList<>();
		for(Quiz item :res) {
			if(!quIds.contains(item.getQuId())) {//保留不在刪除清單中的
				retainList.add(item);
			}
		}
		for(int i=0 ;i<retainList.size();i++) {
			retainList.get(i).setQuId(i+1);
		}
		//刪除整張問卷
		quizDao.deleteByQuizId(quizId);
		if(!retainList.isEmpty()) {
			quizDao.saveAll(retainList);
		}
		return new SearchRes (RtnCode.SUCCESS.getCode(),RtnCode.SUCCESS.getMessage());
	}

	@Override
	public BaseRes update(CreateOrUpdateReq req ) {
		
		return checkParams(req,false);
	}
	private BaseRes checkParams (CreateOrUpdateReq req,boolean isCreate) {
		if(CollectionUtils.isEmpty(req.getQuizList())) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(),RtnCode.PARAM_ERROR.getMessage());
		}
		// 檢查必填項目
		for (Quiz item : req.getQuizList()) {
			if (item.getQuizId() <= 0 || item.getQuId() <= 0 || !StringUtils.hasText(item.getQuizName())
					|| item.getStartDate() == null || item.getEndDate() == null
					|| !StringUtils.hasText(item.getQuestion()) || !StringUtils.hasText(item.getType())) {
				return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
			}
		}
//		 蒐集 req 中所有的 quizId
//		 原則上是一個 req 中所有的 quizId 會相同(一張問卷多個問題)，但也是有可能其中一筆資料的quizId 是錯的
//		 為保證所以資料的正確性，就先去蒐集req 中所有的quizId
//				List <Integer> quizIds=new ArrayList<>(); //List 允許重複的值存在
//				for(Quiz item :req.getQuizList()) {
//					if(!quizIds.contains(item.getQuizId())) {
//						quizIds.add(item.getQuizId());
//					}
//				}
//		 以下用set 的寫法與上面用List 的寫法結果一模一樣
		Set<Integer> quizIds = new HashSet<>(); // Set 不存在相同值
		Set<Integer> quIds = new HashSet<>();
		for (Quiz item : req.getQuizList()) {
			quizIds.add(item.getQuizId());
			quIds.add(item.getQuId());
		}
		if (quizIds.size() != 1) {
			return new BaseRes(RtnCode.QUIZ_ID_DOES_NOT_MATCH.getCode(), RtnCode.QUIZ_ID_DOES_NOT_MATCH.getMessage());
		}
		if (quIds.size() != req.getQuizList().size()) {
			return new BaseRes(RtnCode.DUPLICATED_QUESTION_ID.getCode(), RtnCode.DUPLICATED_QUESTION_ID.getMessage());
		}

		for (Quiz item : req.getQuizList()) {
			if (item.getStartDate().isAfter(item.getEndDate())) {
				return new BaseRes(RtnCode.TIME_FORMAT_ERROR.getCode(), RtnCode.TIME_FORMAT_ERROR.getMessage());
			}
		}
		if(isCreate) {
			if(quizDao.existsByQuizId(req.getQuizList().get(0).getQuizId())){
				return new BaseRes(RtnCode.QUIZ_EXISTS.getCode(),RtnCode.QUIZ_EXISTS.getMessage());
			}
		}else {
			if(!quizDao.existsByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfter(//
					req.getQuizList().get(0).getQuizId(),req.getQuizList().get(0).getQuizId(), LocalDate.now())){
				return new BaseRes(RtnCode.QUIZ_NOT_FOUND.getCode(),RtnCode.QUIZ_NOT_FOUND.getMessage());
			}
		}
		
		try {
			quizDao.deleteByQuizId(req.getQuizList().get(0).getQuizId());
		}catch(Exception e) {
			return new BaseRes(RtnCode.DELETE_QUIZ_ERROR.getCode(),RtnCode.DELETE_QUIZ_ERROR.getMessage());
		}
		// 根據是否要發布，再把published set 的值set到傳送過來的quizList中
		for (Quiz item : req.getQuizList()) {
			item.setPublished(req.isPublished());
		}
		
		quizDao.saveAll(req.getQuizList());
		return new BaseRes(RtnCode.SUCCESS.getCode(),RtnCode.SUCCESS.getMessage());
	}

	
	
	@Override
	public BaseRes answer(AnswerReq req) {
		if(CollectionUtils.isEmpty(req.getAnswerList())) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(),RtnCode.PARAM_ERROR.getMessage());
		}
		for (Answer item : req.getAnswerList()) {
			if (!StringUtils.hasText(item.getNeame()) || !StringUtils.hasText(item.getPhone())
					|| !StringUtils.hasText(item.getEmail()) || item.getQuizId() <= 0 || item.getQuId() <= 0
					|| item.getAge() <= 0) {
				return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
			}
		}
		Set<Integer> quizIds = new HashSet<>(); // Set 不存在相同值
		Set<Integer> quIds = new HashSet<>();
		for (Answer item : req.getAnswerList()) {
			quizIds.add(item.getQuizId());
			quIds.add(item.getQuId());
		}
		if (quizIds.size() != 1) {
			return new BaseRes(RtnCode.QUIZ_EXISTS.getCode(), RtnCode.QUIZ_EXISTS.getMessage());
		}
		if (quIds.size() != req.getAnswerList().size()) {
			return new BaseRes(RtnCode.DUPLICATED_QUESTION_ID.getCode(), RtnCode.DUPLICATED_QUESTION_ID.getMessage());
		}
		
		// 檢查必填問題是否有答案
		// 取出使用者的問題，透過使用者所填的問卷編號去找出問題編號，並符合必要是true
		List<Integer> res = quizDao.findQuIdByQuizIdAndNecessaryTrue(req.getAnswerList().get(0).getQuizId());
		for(Answer item : req.getAnswerList()) {//遍歷使用者所填的問卷中所有問題
			if(res.contains(item.getQuId()) && !StringUtils.hasText(item.getAnswer())) {
				return new BaseRes(RtnCode.QUESTION_NO_ANSWER.getCode(), RtnCode.QUESTION_NO_ANSWER.getMessage());
			}
		}
//		for(int item :res) {//把必答編號的內容提取出來
//			Answer ans=req.getAnswerList().get(item-1);//新產生一個List，從使用者填寫的題目卷提取對應的index，內容編號(數值)與index對應=內容編號-1
//			if(!StringUtils.hasText(ans.getAnswer())) {//確認每個題都有內容，有的話回傳true，沒有的話false再相反，變成true去執行
//				return new BaseRes(RtnCode.QUESTION_NO_ANSWER.getCode(), RtnCode.QUESTION_NO_ANSWER.getMessage());
//			}
//		}
		//確認同一個email不能重複填寫同一張問卷
		if(answerDao.existsByQuizIdAndEmail(req.getAnswerList().get(0).getQuizId(),
				req.getAnswerList().get(0).getEmail())) {
			return new BaseRes(RtnCode.DUPLICATED_QUIZ_ANSWER.getCode(),RtnCode.DUPLICATED_QUIZ_ANSWER.getMessage());
		}
		answerDao.saveAll(req.getAnswerList());
		return new BaseRes(RtnCode.SUCCESS.getCode(),RtnCode.SUCCESS.getMessage());
		
	}

	@Override
	public StatisticsRes statistics(int quizId) {
		if(quizId<=0) {
			return new StatisticsRes(RtnCode.PARAM_ERROR.getCode(),RtnCode.PARAM_ERROR.getMessage());
		}
		//撈取問卷取得問題的type是非簡答題
		List <Quiz> quizs=quizDao.findByQuizId(quizId);
		//qus是法非簡答題的題目編號之集合
		List<Integer> qus=new ArrayList<>();
		//若是簡答題，options 是空的
		for(Quiz item:quizs) {
			if(StringUtils.hasText(item.getOptions())) {
				qus.add(item.getQuId());
			}
		}
		List <Answer> answers=answerDao.findByQuizIdOrderByQuId(quizId);
		//quIdAnswerMap:問題編號與答案mapping
		Map <Integer,String> quIdAnswerMap =new HashMap<>();
		//把非簡答題的答案串成字串，即一個選項(答案)會有一個字串
		for(Answer item:answers) {
			//若是包含在qus，此List中的就表示是選擇題，就是選擇題(單、多選)
			if(qus.contains(item.getQuId())) {
				//若key值已存在
				if(quIdAnswerMap.containsKey(item.getQuId())) {
					//1.透過key取得對應的value
					String str=quIdAnswerMap.get(item.getQuId());
					//2.把原有的值和這次取得的值串接變成新的值
					str+=item.getAnswer();
					//3.將新的值放到原本的key之下
					quIdAnswerMap.put(item.getQuId() ,str);
				}else {//key不存在直接新增key和value
					quIdAnswerMap.put(item.getQuId() ,item.getAnswer());
				}
			}
		}
		//計算每個選項的次數
		//answerCountMap:選項(答案)與次數的mapping
		Map <Integer , Map <String ,Integer>> quizIdAndAnsCountMap =new HashMap<> ();
		//使用foreach 遍歷 map 中的每個項目
		//遍歷的對象從map轉成 entrySet ，好處是可以直接取得 map 中的 key和 value
		for(Entry<Integer ,String> item : quIdAnswerMap.entrySet()) {
			//取得每個問題的選項
			Map<String , Integer > answerCountMap=new HashMap<>();
			//Map 中的Map <String , Integer >指的是上面的 answerCountMap
			String [] optionList =quizs.get(item.getKey()-1).getOptions().split(";");
			//把問題的選項與次數做 mapping
			for(String option :optionList) {
				String newStr=item.getValue();
				int length1=newStr.length();
				newStr=newStr.replace(option,"");
				int length2=newStr.length();
				//要除option的原因是Option 是選項的內容，而不是選項的編號
				int count=(length1-length2)/option.length();
				answerCountMap.put(option,count);
			}
			quizIdAndAnsCountMap.put(item.getKey(), answerCountMap);
		}
			
		return new StatisticsRes(RtnCode.SUCCESS.getCode(),RtnCode.SUCCESS.getMessage(),quizIdAndAnsCountMap);
	}

	

	

	

	
}
