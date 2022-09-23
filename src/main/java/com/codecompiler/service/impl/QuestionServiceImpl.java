package com.codecompiler.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.dto.MyCellDTO;
import com.codecompiler.dto.QuestionStatusDTO;
import com.codecompiler.entity.Contest;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.TestCases;
import com.codecompiler.exception.RecordNotFoundException;
import com.codecompiler.exception.SavedQuestionStatusFalseException;
import com.codecompiler.exception.UnSupportedFormatException;
import com.codecompiler.helper.ExcelPOIHelper;
import com.codecompiler.repository.ContestRepository;
import com.codecompiler.repository.QuestionRepository;
import com.codecompiler.service.ContestService;
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

	@Autowired
	private ContestService contestService;

	@Resource(name = "excelPOIHelper")
	private ExcelPOIHelper excelPOIHelper;

	@Override
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

	public List<Question> saveFileForBulkQuestion(MultipartFile file, String contestId, String contestLevel) throws IOException{
		if (!ExcelConvertorService.checkExcelFormat(file)) {
			throw new UnSupportedFormatException("saveFileForBulkQuestion::Given file format is not supported");
		}
		Contest contest = contestRepository.findByContestId(contestId);
		List<Question> allTrueQuestions = null;
		Map<Integer, List<MyCellDTO>> data = excelPOIHelper.readExcel(file.getInputStream(),
				file.getOriginalFilename());
		allTrueQuestions = excelConvertorService.convertExcelToListOfQuestions(data);
		if(allTrueQuestions.isEmpty() || allTrueQuestions == null) {
			throw new RecordNotFoundException("saveFileForBulkQuestion:: Data isn't present in the file");
		} 
		allTrueQuestions = questionRepository.saveAll(allTrueQuestions);
		if (contest != null) {
			List<String> questionsInContest = saveContest(contest,allTrueQuestions);
			return 	questionRepository.findByQuestionIdIn(questionsInContest);
		} else {
			return questionRepository.findByContestLevelAndQuestionStatus(contestLevel, "true");
		}
	}

	public List<String> saveContest(Contest contest,List<Question> allTrueQuestions) {
		ArrayList<QuestionStatusDTO> queStatusList = new ArrayList<QuestionStatusDTO>();
		allTrueQuestions.forEach(latestUploadedQuestions -> {
			QuestionStatusDTO queStatus = new QuestionStatusDTO();
			queStatus.setQuestionId(latestUploadedQuestions.getQuestionId());
			queStatus.setStatus(true);
			queStatusList.addAll(contest.getQuestionStatus());
			queStatusList.add(queStatus);
		});
		contest.setQuestionStatus(queStatusList);
		contestRepository.save(contest);
		return contest.getQuestionStatus().stream().map(QuestionStatusDTO::getQuestionId).collect(Collectors.toList());
	}



	//point of discussion regarding contest save, length, question save 2 times , 
	public Question saveQuestion(Question question) {
		String[] stringOfCidAndCl = new String[2];
		stringOfCidAndCl = question.getContestLevel().split("@");
		String tempQid = question.getQuestionId();
		if (tempQid.isBlank()) {
			tempQid = UUID.randomUUID().toString();
			question.setQuestionId(tempQid);
			question.setQuestionStatus("true");
		}
		Question savedQuestion = new Question();
		if (stringOfCidAndCl.length == 1) {
			question.setContestLevel(stringOfCidAndCl[0]);
			savedQuestion = questionRepository.save(question);		
		} else {
			Contest contest = new Contest(); // id, level
			contest = contestService.findByContestId(stringOfCidAndCl[1]);
			question.setContestLevel(stringOfCidAndCl[0]);
			savedQuestion = questionRepository.save(question);
			QuestionStatusDTO questionStatus = new QuestionStatusDTO();
			questionStatus.setQuestionId(savedQuestion.getQuestionId());
			questionStatus.setStatus(true);
			contest.getQuestionStatus().add(questionStatus);
			contestService.saveContest(contest);
		}
//		if(savedQuestion.getQuestionStatus().equals("false")) {
//			throw new SavedQuestionStatusFalseException("saveQuestions:: recently saved question status expected true but it is false: "+ savedQuestion.getQuestionId());
//		}
		return savedQuestion;
	}



	public Question findByQuestionId(String questionId) {
		if(questionId == null) 
			throw new NullPointerException();
		else if (questionId.isBlank()) 
			throw new IllegalArgumentException();	
		Question question = questionRepository.findByQuestionId(questionId);
		if(question == null)
			 throw new RecordNotFoundException(questionId +" not exist");
		return question;
	}

	public List<Question> findByContestLevel(String filterByString) {
		ArrayList<Question> totalQuestionWithStatusTrue = new ArrayList<Question>();
		List<Question> questions = questionRepository.findByContestLevel(filterByString);
		for (Question verifyQuestion : questions) {
			if (verifyQuestion.getQuestionStatus() != null && verifyQuestion.getQuestionStatus().equals("true")) {
				totalQuestionWithStatusTrue.add(verifyQuestion);
			}
		}
		return totalQuestionWithStatusTrue;
	}

	@Override
	public List<TestCases> getTestCase(String questionId) {
		Question questions = questionRepository.findByQuestionId(questionId);
		return questions.getTestcases();
	}

	@Override
	public List<Question> getAllQuestions(Map<String, List<String>> questionIdList) {
		String contestId = questionIdList.get("contestId").get(0);
		if(contestId.isBlank()) {
			throw new RecordNotFoundException("getAllQuestions:: contentId does not found in the questionIdList: "+ contestId);
		}
		List<Question> questionDetails = questionRepository.findByQuestionIdIn(questionIdList.get("questionsIds"));
		if(questionDetails == null) {
			throw new RecordNotFoundException("getAllQuestions:: Questions does not found");
		}
		Contest contest = saveContests(contestId,questionIdList);
		return questionDetails;	
	}

	public Contest saveContests(String contestId,Map<String, List<String>> questionIdList) {
		Contest contest = contestService.findByContestId(contestId);
		if(contest == null) {
			throw new RecordNotFoundException("saveContests:: Content does not found for contestId: " + contestId);
		}
		ArrayList<QuestionStatusDTO> questionStatus = contest.getQuestionStatus();
		if(questionStatus == null) {
			throw new RecordNotFoundException("saveContests:: QuestionStatus does not found");
		}
		boolean flag = false;
		for (String idToChangeStatus : questionIdList.get("questionsIds")) {
			int index = 0;
			for (QuestionStatusDTO qs : questionStatus) {
				if (idToChangeStatus.equals(qs.getQuestionId())) {
					if (qs.getStatus() == false) {
						contest.getQuestionStatus().get(index).setStatus(true);
						flag = true;
					} else if (qs.getStatus()) {
						flag = true;
					}
				}
				index++;
			}
			if (flag == false) {
				QuestionStatusDTO qsTemp = new QuestionStatusDTO();
				qsTemp.setQuestionId(idToChangeStatus);
				qsTemp.setStatus(true);
				contest.getQuestionStatus().add(qsTemp);
			} else {
				flag = false;
			}
		}
		return contestService.saveContest(contest);
	}

	@Override
	public void saveQuestionOrContest(ArrayList<String> contestAndQuestionId) {
		if(contestAndQuestionId == null) {
			throw new RecordNotFoundException("saveQuestionOrContest:: contestAndQuestionId does not found :"+contestAndQuestionId.size());
		}
		if (contestAndQuestionId.get(0).equals("questionForLevel")) {
			Question questionStatusChange = findByQuestionId(contestAndQuestionId.get(1));
			questionStatusChange.setQuestionStatus("false");
			saveQuestion(questionStatusChange);
		} else {
			Contest contest = new Contest();
			contest = contestService.findByContestId(contestAndQuestionId.get(0));
			int index = 0;
			for (QuestionStatusDTO qs : contest.getQuestionStatus()) {
				if (qs.getQuestionId().equals(contestAndQuestionId.get(1))) {
					contest.getQuestionStatus().get(index).setStatus(false);
				}
				index++;
			}
			contestService.saveContest(contest);
		}
	}

	@Override
	public List<Question> filterQuestion(String filterByString) {
		List<Question> totalQuestionByFilter = new ArrayList<Question>();
		if(filterByString.isBlank()) {
			throw new RecordNotFoundException("filterQuestion:: filterByString does not found :"+filterByString);
		}
		if (filterByString.equals("Level 1") || filterByString.equals("Level 2"))
			totalQuestionByFilter = findByContestLevel(filterByString);
		else
			totalQuestionByFilter = findAllQuestion();
		return totalQuestionByFilter;
	}
	
	public void  deleteQuestionTestCase(Question question) {
		questionRepository.delete(question);		 
	}
}
