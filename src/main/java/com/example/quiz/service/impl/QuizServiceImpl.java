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
			quizName="";//countaining �N���ѼƭȬ��Ŧr��A��ܷ|��������
		}
		if(starDate==null) {
			starDate = LocalDate.of(1970, 1, 1);//�N�}�l�ɶ��]�w���ܦ����e���ɶ�
		}
		if(endDate==null) {
			endDate=LocalDate.of(2099, 12, 31);//�N�����ɶ��]�w���b�ܤ[���᪺�ɶ�
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
		if(CollectionUtils.isEmpty(req.getQuizIds()) ) {//�P�ɧP�_quizIds�O�_��null�H�ΪŶ��X
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
		// �ھ�(quizId�ݨ�ID and ���o�� ) OR (quizId�ݨ�ID and �|���}�l)��ݨ��A�w�g��Ӱݨ��Ҧ���R���D����X�F
		List <Quiz> res=quizDao.findByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfterOrderByQuId(//
				quizId,quizId,LocalDate.now());
		if(res.isEmpty()) {
			return new BaseRes(RtnCode.QUIZ_NOT_FOUND.getCode(),RtnCode.QUIZ_NOT_FOUND.getMessage());
		}
		//�ϥ�for-each�����M������H���W�[�R��
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
			if(!quIds.contains(item.getQuId())) {//�O�d���b�R���M�椤��
				retainList.add(item);
			}
		}
		for(int i=0 ;i<retainList.size();i++) {
			retainList.get(i).setQuId(i+1);
		}
		//�R����i�ݨ�
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
		// �ˬd���񶵥�
		for (Quiz item : req.getQuizList()) {
			if (item.getQuizId() <= 0 || item.getQuId() <= 0 || !StringUtils.hasText(item.getQuizName())
					|| item.getStartDate() == null || item.getEndDate() == null
					|| !StringUtils.hasText(item.getQuestion()) || !StringUtils.hasText(item.getType())) {
				return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
			}
		}
//		 �`�� req ���Ҧ��� quizId
//		 ��h�W�O�@�� req ���Ҧ��� quizId �|�ۦP(�@�i�ݨ��h�Ӱ��D)�A���]�O���i��䤤�@����ƪ�quizId �O����
//		 ���O�ҩҥH��ƪ����T�ʡA�N���h�`��req ���Ҧ���quizId
//				List <Integer> quizIds=new ArrayList<>(); //List ���\���ƪ��Ȧs�b
//				for(Quiz item :req.getQuizList()) {
//					if(!quizIds.contains(item.getQuizId())) {
//						quizIds.add(item.getQuizId());
//					}
//				}
//		 �H�U��set ���g�k�P�W����List ���g�k���G�@�Ҥ@��
		Set<Integer> quizIds = new HashSet<>(); // Set ���s�b�ۦP��
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
		// �ھڬO�_�n�o���A�A��published set ����set��ǰe�L�Ӫ�quizList��
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
		Set<Integer> quizIds = new HashSet<>(); // Set ���s�b�ۦP��
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
		
		// �ˬd������D�O�_������
		// ���X�ϥΪ̪����D�A�z�L�ϥΪ̩Ҷ񪺰ݨ��s���h��X���D�s���A�òŦX���n�Otrue
		List<Integer> res = quizDao.findQuIdByQuizIdAndNecessaryTrue(req.getAnswerList().get(0).getQuizId());
		for(Answer item : req.getAnswerList()) {//�M���ϥΪ̩Ҷ񪺰ݨ����Ҧ����D
			if(res.contains(item.getQuId()) && !StringUtils.hasText(item.getAnswer())) {
				return new BaseRes(RtnCode.QUESTION_NO_ANSWER.getCode(), RtnCode.QUESTION_NO_ANSWER.getMessage());
			}
		}
//		for(int item :res) {//�⥲���s�������e�����X��
//			Answer ans=req.getAnswerList().get(item-1);//�s���ͤ@��List�A�q�ϥΪ̶�g���D�ب�����������index�A���e�s��(�ƭ�)�Pindex����=���e�s��-1
//			if(!StringUtils.hasText(ans.getAnswer())) {//�T�{�C���D�������e�A�����ܦ^��true�A�S������false�A�ۤϡA�ܦ�true�h����
//				return new BaseRes(RtnCode.QUESTION_NO_ANSWER.getCode(), RtnCode.QUESTION_NO_ANSWER.getMessage());
//			}
//		}
		//�T�{�P�@��email���୫�ƶ�g�P�@�i�ݨ�
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
		//�����ݨ����o���D��type�O�D²���D
		List <Quiz> quizs=quizDao.findByQuizId(quizId);
		//qus�O�k�D²���D���D�ؽs�������X
		List<Integer> qus=new ArrayList<>();
		//�Y�O²���D�Aoptions �O�Ū�
		for(Quiz item:quizs) {
			if(StringUtils.hasText(item.getOptions())) {
				qus.add(item.getQuId());
			}
		}
		List <Answer> answers=answerDao.findByQuizIdOrderByQuId(quizId);
		//quIdAnswerMap:���D�s���P����mapping
		Map <Integer,String> quIdAnswerMap =new HashMap<>();
		//��D²���D�����צꦨ�r��A�Y�@�ӿﶵ(����)�|���@�Ӧr��
		for(Answer item:answers) {
			//�Y�O�]�t�bqus�A��List�����N��ܬO����D�A�N�O����D(��B�h��)
			if(qus.contains(item.getQuId())) {
				//�Ykey�Ȥw�s�b
				if(quIdAnswerMap.containsKey(item.getQuId())) {
					//1.�z�Lkey���o������value
					String str=quIdAnswerMap.get(item.getQuId());
					//2.��즳���ȩM�o�����o���Ȧ걵�ܦ��s����
					str+=item.getAnswer();
					//3.�N�s���ȩ��쥻��key���U
					quIdAnswerMap.put(item.getQuId() ,str);
				}else {//key���s�b�����s�Wkey�Mvalue
					quIdAnswerMap.put(item.getQuId() ,item.getAnswer());
				}
			}
		}
		//�p��C�ӿﶵ������
		//answerCountMap:�ﶵ(����)�P���ƪ�mapping
		Map <Integer , Map <String ,Integer>> quizIdAndAnsCountMap =new HashMap<> ();
		//�ϥ�foreach �M�� map �����C�Ӷ���
		//�M������H�qmap�ন entrySet �A�n�B�O�i�H�������o map ���� key�M value
		for(Entry<Integer ,String> item : quIdAnswerMap.entrySet()) {
			//���o�C�Ӱ��D���ﶵ
			Map<String , Integer > answerCountMap=new HashMap<>();
			//Map ����Map <String , Integer >�����O�W���� answerCountMap
			String [] optionList =quizs.get(item.getKey()-1).getOptions().split(";");
			//����D���ﶵ�P���ư� mapping
			for(String option :optionList) {
				String newStr=item.getValue();
				int length1=newStr.length();
				newStr=newStr.replace(option,"");
				int length2=newStr.length();
				//�n��option����]�OOption �O�ﶵ�����e�A�Ӥ��O�ﶵ���s��
				int count=(length1-length2)/option.length();
				answerCountMap.put(option,count);
			}
			quizIdAndAnsCountMap.put(item.getKey(), answerCountMap);
		}
			
		return new StatisticsRes(RtnCode.SUCCESS.getCode(),RtnCode.SUCCESS.getMessage(),quizIdAndAnsCountMap);
	}

	

	

	

	
}
