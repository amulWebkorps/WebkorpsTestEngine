package com.codecompiler.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.codecompiler.dto.MCQDTO;
import com.codecompiler.dto.MCQStatusDTO;
import com.codecompiler.dto.McqSubmitDto;
import com.codecompiler.dto.QuestionStatusDTO;
import com.codecompiler.entity.Contest;
import com.codecompiler.entity.Language;
import com.codecompiler.entity.MCQ;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.Student;
import com.codecompiler.exception.RecordNotFoundException;
import com.codecompiler.repository.ContestRepository;
import com.codecompiler.repository.MCQRepository;
import com.codecompiler.repository.QuestionRepository;
import com.codecompiler.repository.StudentRepository;
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
	private MCQRepository mcqRepository;

	@Autowired
	private LanguageService languageService;

	@Autowired
	private MCQServiceImpl mcqServiceImpl;

	@Autowired
	private StudentRepository studentRepository;

	public static final Logger logger = LogManager.getLogger(ContestServiceImpl.class);

	public Contest saveContest(Contest contest) {
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
		if (contestId == null)
			throw new NullPointerException();
		else if (contestId.isBlank())
			throw new IllegalArgumentException();
		Contest contest = contestRepository.findByContestId(contestId);
		if (contest == null)
			throw new RecordNotFoundException("Contest not exist");
		return contest;
	}

	public void deleteContest(String contestId) {
		if (contestId == null)
			throw new NullPointerException();
		else if (contestId.isBlank())
			throw new IllegalArgumentException();
		contestRepository.deleteById(contestId);
	}

	@Override
	public List<Contest> findAllContest() {
		logger.info("getAllContest: started");
		List<Contest> contestList = contestRepository.findAll();
		List<Contest> contestIdAndName = contestList.stream()
				.map(contest -> new Contest(contest.getContestId(), contest.getContestName(), contest.getContestLevel(),
						contest.getContestDescription(), contest.getDate(), contest.getContestTime(), contest.getContestType()))
				.collect(Collectors.toList());

		logger.info("getAllContest: ended");
		return contestIdAndName;
	}


	@Override
	public Map<String, Object> getContestDetail(String contestId, String contestType) {
		Map<String, Object> contestDetail = new HashedMap<String, Object>();
		ArrayList<String> qListStatusTrue = new ArrayList<>();

		Contest contestRecord = this.findByContestId(contestId);
		contestDetail.put("contest", contestRecord);
		if (contestType.equalsIgnoreCase("Question")) {
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
		} else {
			ArrayList<MCQStatusDTO> mcqStatusTemp = contestRecord.getMcqStatus();
			for (MCQStatusDTO mcqStatus : mcqStatusTemp) {
				if (mcqStatus.isMcqstatus()) {
					qListStatusTrue.add(mcqStatus.getMcqId());
				}
			}
			List<MCQ> mcqDetailList = mcqRepository.findByMcqIdIn(qListStatusTrue);
			List<MCQ> totalMCQWithStatusTrue = findAllMcq();
			for (MCQ mcqs : mcqDetailList)
				totalMCQWithStatusTrue.removeIf(mcq -> mcq.getMcqId().equalsIgnoreCase(mcqs.getMcqId()));

			contestDetail.put("contestMCQDetail", mcqDetailList);

			List<MCQ> totalMCQnWithStatusTrueFormat = new ArrayList<>();
			for (MCQ mcqs : totalMCQWithStatusTrue) {
				MCQ formateMCQ = new MCQ();
				formateMCQ.setMcqId(mcqs.getMcqId());
				formateMCQ.setMcqQuestion(mcqs.getMcqQuestion());
				totalMCQnWithStatusTrueFormat.add(formateMCQ);
			}
			contestDetail.put("totalAvailableMCQ", totalMCQnWithStatusTrueFormat);
		}
		return contestDetail;

	}

	@Override
	@Cacheable(value = "contest", key = "#selectlanguage")
	public Map<String, Object> contestPage(String contestId, String studentId, String selectlanguage) {
		Language language = languageService.findByLanguage(selectlanguage);
		Contest contestTime = this.findByContestId(contestId);
		List<Question> contestQuestionsList = getAllQuestion(contestId, studentId);
		
		if(selectlanguage.equalsIgnoreCase("java")) {
			contestQuestionsList=contestQuestionsList.stream().map(q -> {
				q.setSampleCode(q.getJavaSampleCode());
				return q;
			}).collect(Collectors.toList());
		}else if(selectlanguage.equalsIgnoreCase("c")) {
			contestQuestionsList=contestQuestionsList.stream().map(q -> {
				q.setSampleCode(q.getCSampleCode());
				return q;
			}).collect(Collectors.toList());
		}else if(selectlanguage.equalsIgnoreCase("cpp")) {
			contestQuestionsList=contestQuestionsList.stream().map(q -> {
				q.setSampleCode(q.getcPlusPluseSampleCode());
				return q;
			}).collect(Collectors.toList());
		}else if(selectlanguage.equalsIgnoreCase("Python")) {
			contestQuestionsList=contestQuestionsList.stream().map(q -> {
				q.setSampleCode(q.getPythonSampleCode());
				return q;
			}).collect(Collectors.toList());
		}
		for(int i=0;i<contestQuestionsList.size();i++) {
			String result="";
			String input=contestQuestionsList.get(i).getSampleTestCase().get(0).getInput();
			if(contestQuestionsList.get(i).getQuestionType().equalsIgnoreCase("Array")) {
				if(contestQuestionsList.get(i).getQuestionType().equalsIgnoreCase("Array")) {
	              	Pattern keyValuePattern = Pattern.compile("([^=,]+)=([^=,]+)");
	              	Matcher keyValueMatcher = keyValuePattern.matcher(input);
	              	
	              	while (keyValueMatcher.find()) {
	              	    String value = keyValueMatcher.group(2);
	              	    String key= keyValueMatcher.group(1);
//	              	    String key="a";
	              	    if (value.startsWith("[") && value.endsWith("]")) {
	              	        // Extract array values
	              	        String[] stringArray = value.substring(1, value.length() - 1).split("/");
	              	        int[] intArray = new int[stringArray.length];
	              	        for (int k = 0; k < stringArray.length; k++) {
	              	            intArray[k] = Integer.parseInt(stringArray[k]);
	              	        }
	              	        String arr="[";
	              	        for(int k=0;k<intArray.length-1;k++)
	              	        	arr=arr+intArray[k]+",";
	              	        arr=arr+intArray[intArray.length-1]+"]";
	              	      result = result + key + "=" + arr + "\n";
	              	    } else {
	              	        // Treat value as integer
	              	        int intValue = Integer.parseInt(value);
	              	      result = result + key + "=" + intValue + "\n";
	              	    }
	              	}
	              }else if(contestQuestionsList.get(i).getQuestionType().equalsIgnoreCase("String")) {
	              	String[] parts = input.split(",");
	          		for (String part : parts) {
	              	    String[] keyValue = part.split("=");
	              	    if (keyValue.length == 2) {
	              	        String value = keyValue[1];
	              	        String key = keyValue[0];
	              	      result = result+key+"=" + value + "\n";
	              	    }else if (keyValue.length==1) {
	              	    	String key = "a";
	              	    	String value = keyValue[0];
	              	    	result = result +key+ "=" + value + "\n";
	          			}
	              	}
	              	
	              }
				contestQuestionsList.get(i).getSampleTestCase().get(0).setInput(result);
			}
		}
		
		for(int i=0;contestQuestionsList.size()>i;i++) {
			for(int j=0;j<contestQuestionsList.get(i).getTestcases().size();j++) {
				String input=contestQuestionsList.get(i).getTestcases().get(j).getInput();
				String result="";
				if(contestQuestionsList.get(i).getQuestionType().equalsIgnoreCase("Array")) {
	              	Pattern keyValuePattern = Pattern.compile("([^=,]+)=([^=,]+)");
	              	Matcher keyValueMatcher = keyValuePattern.matcher(input);
	              	
	              	while (keyValueMatcher.find()) {
	              	    String value = keyValueMatcher.group(2);
	              	    String key= keyValueMatcher.group(1);
	              	    if (value.startsWith("[") && value.endsWith("]")) {
	              	        // Extract array values
	              	        String[] stringArray = value.substring(1, value.length() - 1).split("/");
	              	        int[] intArray = new int[stringArray.length];
	              	        for (int k = 0; k < stringArray.length; k++) {
	              	            intArray[k] = Integer.parseInt(stringArray[k]);
	              	        }
	              	        String arr="[";
	              	        for(int k=0;k<intArray.length-1;k++)
	              	        	arr=arr+intArray[k]+",";
	              	        arr=arr+intArray[intArray.length-1]+"]";
	              	      result = result + key + "=" + arr + "\n";
	              	    } else {
	              	        // Treat value as integer
	              	        int intValue = Integer.parseInt(value);
	              	      result = result + key + "=" + intValue + "\n";
	              	    }
	              	}
	              }else if(contestQuestionsList.get(i).getQuestionType().equalsIgnoreCase("String")) {
	              	String[] parts = input.split(",");
	          		for (String part : parts) {
	              	    String[] keyValue = part.split("=");
	              	    if (keyValue.length == 2) {
	              	        String value = keyValue[1];
	              	        String key = keyValue[0];
	              	      result = result+key+"=" + value + "\n";
	              	    }else if (keyValue.length==1) {
	              	    	String key = "a";
	              	    	String value = keyValue[0];
	              	    	result = result +key+ "=" + value + "\n";
	          			}
	              	}
	              	
	              }
				contestQuestionsList.get(i).getTestcases().get(j).setInput(result);
			}
			
		}
		
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
		if (questions == null) {
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

	@Override
	public Contest findByContestName(String contestName) {
		return contestRepository.findByContestName(contestName);
	}

	public List<MCQ> findAllMcq() {
		List<MCQ> totalMCQWithStatusTrue = new ArrayList<>();
		List<MCQ> mcqs = mcqRepository.findAll();
		if (mcqs == null) {
			throw new RecordNotFoundException("findAllMCQ:: Questions doesn't found");
		}
		for (MCQ verifyQuestion : mcqs) {
			if (verifyQuestion.isMcqStatus()) {
				totalMCQWithStatusTrue.add(verifyQuestion);
			}
		}
		return totalMCQWithStatusTrue;
	}

	@Override
	public Map<String, Object> findAllUploadedQuestions(String contestId, String studentId) {
		Contest contest = contestRepository.findByContestId(contestId);
		List<MCQDTO> list = new ArrayList<MCQDTO>();

		if (contest != null) {
			List<MCQStatusDTO> quetionsId = contest.getMcqStatus();
			// Contest contestTime = this.findByContestId(contestId);
			Map<String, Object> mcqMap = new HashedMap<String, Object>();
			for (int i = 0; i < quetionsId.size(); i++) {
				if (quetionsId.get(i).isMcqstatus()) {
					MCQDTO mcqDto = new MCQDTO();
					MCQ mcq = mcqServiceImpl.findByMcqId(quetionsId.get(i).getMcqId());
					mcqDto.setMcqId(mcq.getMcqId());

					mcqDto.setMcqQuestion(mcq.getMcqQuestion());
					mcqDto.setOption1(mcq.getOption1());
					mcqDto.setOption2(mcq.getOption2());
					mcqDto.setOption3(mcq.getOption3());
					mcqDto.setOption4(mcq.getOption4());
					list.add(mcqDto);
				}
			}
			mcqMap.put("contestId", contestId);
			mcqMap.put("studentId", studentId);
			mcqMap.put("contestTime", contest.getContestTime());
			mcqMap.put("mcqList", list);
			return mcqMap;
		}
		return null;

	}

	@Override
	public boolean submitMcqContest(McqSubmitDto mcqSubmitDto) {
		Student student = studentRepository.findById(mcqSubmitDto.getStudentId());
		if (student != null) {
			student.setPassword(null);
			student.setMcqQuetionsId(mcqSubmitDto.getMcqQuetionsId());
			student.setCorrectAnswers(mcqSubmitDto.getCorrectAnswers());
			student.setContestId(mcqSubmitDto.getContestId());
			if (studentRepository.save(student) != null)
				return true;
		}
		return false;
	}

	@Override
	public String findContestTypeByContestId(String contestId) {
		
		String contestType = contestRepository.findByContestId(contestId).getContestType();
		return contestType;
	}
	
	

}
