package com.codecompiler.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.codecompilercontroller.ExcelPOIHelper;
import com.codecompiler.dao.ContestRepository;
import com.codecompiler.dao.QuestionRepository;
import com.codecompiler.entity.Contest;
import com.codecompiler.entity.MyCell;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.QuestionStatus;
import com.codecompiler.helper.Helper;
import com.codecompiler.service.QuestionService1;

@Service
public class QuestionServiceImpl implements QuestionService1 {

	@Autowired
	private ContestRepository contestRepository;

	@Autowired
	private QuestionRepository questionRepository;
	
	@Resource(name = "excelPOIHelper")
	private ExcelPOIHelper excelPOIHelper;

	public List<Question> getAllQuestion(String contestId, String studentId) {

		Contest contest = contestRepository.findByContestId(contestId);
		ArrayList<QuestionStatus> qStatusList = new ArrayList<>();
		qStatusList = contest.getQuestionStatus();
		ArrayList<String> qListStatusTrue = new ArrayList<>();
		for (QuestionStatus questionStatus : qStatusList) {
			if (questionStatus.getStatus()) {
				qListStatusTrue.add(questionStatus.getQuestionId());
			}
		}
		return questionRepository.findByQuestionIdIn(qListStatusTrue);
	}

	@Override
	public List<Question> findAllQuestion() {
		List<Question> totalQuestionWithStatusTrue = new ArrayList<>();
		for (Question verifyQuestion : questionRepository.findAll()) {
			if (verifyQuestion.getQuestionStatus() != null) {
				if (verifyQuestion.getQuestionStatus().equals("true"))
					totalQuestionWithStatusTrue.add(verifyQuestion);
			}
		}
		return totalQuestionWithStatusTrue;
	}

	public List<Question> findByQuestionIdIn(List<String> questionListStatusTrue) {
		return questionRepository.findByQuestionIdIn(questionListStatusTrue);
	}
	
	public List<Question> saveFileForBulkQuestion(MultipartFile file) {
		List<Question> allTrueQuestions = new ArrayList<>();
		try {
			Map<Integer, List<MyCell>> data = excelPOIHelper.readExcel(file.getInputStream(), file.getOriginalFilename());
			List<Question> students = Helper.convertExcelToListOfQuestions(data);
			questionRepository.saveAll(students);
			allTrueQuestions = questionRepository.findByQuestionStatus(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return allTrueQuestions;
	}

	public Question saveQuestion(Question question) {		
		return questionRepository.save(question);				
	}
	
	public Question findByQuestionId(String questionId) {
		return questionRepository.findByQuestionId(questionId);
	}
	
}





