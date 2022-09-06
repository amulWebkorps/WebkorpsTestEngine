package com.codecompiler.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.entity.Contest;
import com.codecompiler.entity.MyCell;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.QuestionStatus;
import com.codecompiler.entity.TestCases;
import com.codecompiler.helper.ExcelPOIHelper;
import com.codecompiler.repository.ContestRepository;
import com.codecompiler.repository.QuestionRepository;
import com.codecompiler.service.ExcelConvertorService;
import com.codecompiler.service.QuestionService;

@Service
public class QuestionServiceImpl implements QuestionService {

	@Autowired
	private ContestRepository contestRepository;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private ExcelConvertorService excelConvertorService;

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

	public List<Question> saveFileForBulkQuestion(MultipartFile file, String contestId) {
		List<Question> allTrueQuestions = new ArrayList<>();
		try {
			Map<Integer, List<MyCell>> data = excelPOIHelper.readExcel(file.getInputStream(),
					file.getOriginalFilename());
			allTrueQuestions = excelConvertorService.convertExcelToListOfQuestions(data);
			questionRepository.saveAll(allTrueQuestions);
			ArrayList<QuestionStatus> queStatusList = new ArrayList<>();
			allTrueQuestions.forEach(latestUploadedQuestions -> {
				QuestionStatus queStatus = new QuestionStatus();
				queStatus.setQuestionId(latestUploadedQuestions.getQuestionId());
				queStatus.setStatus(true);
				queStatusList.add(queStatus);
			});
			Contest contest = contestRepository.findByContestId(contestId);
			if (contest != null) {
				contest.setQuestionStatus(queStatusList);
				contestRepository.save(contest);
			}
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

	public List<Question> findByContestLevel(String filterByString) {
		ArrayList<Question> totalQuestionWithStatusTrue = new ArrayList<>();
		for (Question verifyQuestion : questionRepository.findByContestLevel(filterByString)) {
			if (verifyQuestion.getQuestionStatus() != null) {
				if (verifyQuestion.getQuestionStatus().equals("true"))
					totalQuestionWithStatusTrue.add(verifyQuestion);
			}
		}
		return totalQuestionWithStatusTrue;
	}

	@Override
	public List<TestCases> getTestCase(String questionId) {
		Question questions = questionRepository.findByQuestionId(questionId);
		List<TestCases> testCasesCollection = new ArrayList<>();
		testCasesCollection.addAll(questions.getTestcases());
		return testCasesCollection;
	}
}
