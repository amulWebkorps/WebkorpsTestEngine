package com.codecompiler.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.codecompiler.dto.QuestionStatusDTO;
import com.codecompiler.entity.Contest;
import com.codecompiler.entity.Language;
import com.codecompiler.entity.Question;
import com.codecompiler.exception.RecordNotFoundException;
import com.codecompiler.repository.ContestRepository;
import com.codecompiler.repository.QuestionRepository;
import com.codecompiler.service.ContestService;
import com.codecompiler.service.LanguageService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ContestServiceImpl implements ContestService {

	@Autowired
	private ContestRepository contestRepository;
	
	@Autowired
	private QuestionRepository questionRepository;
	
	@Autowired
	private LanguageService languageService;
	
	public static final Logger logger = LogManager.getLogger(ContestServiceImpl.class);
	
	public Contest saveContest(Contest contest){
//		if(!contest.getContestName().isBlank())
//		 throw new NullPointerException();
		return contestRepository.save(contest);				
	}
	
	public List<Contest> getAllContest() {
		List<Contest> contestList = contestRepository.findAll();	
		return contestList;
	}
	
	public Contest getContestBasedOnContestIdAndLevel(String cId, String clevel) {
		Contest contest = contestRepository.findByContestIdAndContestLevel(cId, clevel);	
		return contest;
		
	}
	
	@Override
	public Contest findByContestId(String contestId) {
		if(contestId == null) 
			throw new NullPointerException();
		else if (contestId.isBlank()) 
			throw new IllegalArgumentException();			
		Contest contest = contestRepository.findByContestId(contestId);
		if(contest == null)
			throw new RecordNotFoundException("Contest not exist");
		return contest;			
	}

	public void deleteContest(String contestId) {
		if(contestId == null) 
			throw new NullPointerException();
		else if (contestId.isBlank()) 
			throw new IllegalArgumentException();
		contestRepository.deleteById(contestId);		
	}

	@Override
	public List<Contest> findAllContest() {
		logger.info("getAllContest: started");
		List<Contest> contestIdAndName = new ArrayList<>();
		List<Contest> contestList = contestRepository.findAll();		
		contestList.forEach(eachContestRecord -> {
			Contest contestRecord = new Contest();
			contestRecord.setContestId(eachContestRecord.getContestId());
			contestRecord.setContestName(eachContestRecord.getContestName());
			contestRecord.setContestLevel(eachContestRecord.getContestLevel());
			contestRecord.setContestDescription(eachContestRecord.getContestDescription());
			contestRecord.setDate(eachContestRecord.getDate());
			contestRecord.setContestTime(eachContestRecord.getContestTime());
			contestIdAndName.add(contestRecord);
		});	
		logger.info("getAllContest: ended");
		if(contestIdAndName.isEmpty()) {
			 throw new RecordNotFoundException("Records unavailable");
		}
		return contestIdAndName;
	}

	@Override
	public Map<String, Object> getContestDetail(String contestId) {
	//	log.info("getContestDetail: started contestId = "+contestId);
		Map<String, Object> contestDetail = new HashedMap<String, Object>();
		ArrayList<String> qListStatusTrue = new ArrayList<>();
		
			Contest contestRecord = this.findByContestId(contestId);
			contestDetail.put("contest", contestRecord);
			ArrayList<QuestionStatusDTO> questionStatusTemp = contestRecord.getQuestionStatus();
			for (QuestionStatusDTO questionStatus : questionStatusTemp) {
				if (questionStatus.getStatus()) {
					qListStatusTrue.add(questionStatus.getQuestionId());
				}
			}
			List<Question> questionDetailList = questionRepository.findByQuestionIdIn(qListStatusTrue);
			List<Question> totalQuestionWithStatusTrue = findAllQuestion();			
			for (Question question : questionDetailList) 
				totalQuestionWithStatusTrue.removeIf(x -> x.getQuestionId().equalsIgnoreCase(question.getQuestionId()));
						
			contestDetail.put("contestQuestionDetail", questionDetailList);			

			List<Question> totalQuestionWithStatusTrueFormat = new ArrayList<>();
			for (Question question : totalQuestionWithStatusTrue) {
				Question formateQuestion = new Question();
				formateQuestion.setQuestionId(question.getQuestionId());
				formateQuestion.setQuestion(question.getQuestion());
				totalQuestionWithStatusTrueFormat.add(formateQuestion);
			}
			contestDetail.put("totalAvailableQuestion", totalQuestionWithStatusTrueFormat);
			return contestDetail;
		
	}

	@Override
	@Cacheable(value = "contest", key = "#selectlanguage")
	public Map<String, Object> contestPage(String contestId, String studentId, String selectlanguage) {
		Language language = languageService.findByLanguage(selectlanguage);
		Contest contestTime = this.findByContestId(contestId);
		List<Question> contestQuestionsList = getAllQuestion(contestId, studentId);
		Map<String, Object> mp = new HashedMap<String, Object>();
		mp.put("QuestionList", contestQuestionsList);
		mp.put("languageCode", language);
		mp.put("contestId", contestId);
		mp.put("studentId", studentId);
		mp.put("contestTime", contestTime);
		mp.put("nextQuestion", 0);
		mp.put("previous", false);
		mp.put("next", true);
		return mp;
	}
	
	public List<Question> getAllQuestion(String contestId, String studentId) {
		Contest contest = contestRepository.findByContestId(contestId);
		ArrayList<QuestionStatusDTO> qStatusList = new ArrayList<>();
		qStatusList = contest.getQuestionStatus();
		ArrayList<String> qListStatusTrue = new ArrayList<>();
		for (QuestionStatusDTO questionStatus : qStatusList) {
			if (questionStatus.getStatus()) {
				qListStatusTrue.add(questionStatus.getQuestionId());
			}
		}
		return questionRepository.findByQuestionIdIn(qListStatusTrue);
	}
	
	public List<Question> findAllQuestion() {
		List<Question> totalQuestionWithStatusTrue = new ArrayList<>();
		List<Question> questions = questionRepository.findAll();
		if(questions == null) {
			throw new RecordNotFoundException("findAllQuestion:: Questions doesn't found");	
		}
		for (Question verifyQuestion : questions) {
			if (verifyQuestion.getQuestionStatus() != null) {
				if (verifyQuestion.getQuestionStatus().equals("true"))
					totalQuestionWithStatusTrue.add(verifyQuestion);
			}
		}
		return totalQuestionWithStatusTrue;
	}

}
