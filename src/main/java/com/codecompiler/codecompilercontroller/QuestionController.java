package com.codecompiler.codecompilercontroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.entity.Contest;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.QuestionStatus;
import com.codecompiler.service.ContestService;
import com.codecompiler.service.ExcelConvertorService;
import com.codecompiler.service.QuestionService1;

@Controller
@CrossOrigin(origins = "*")
public class QuestionController {

	@Autowired
	private QuestionService1 questionService;
	
	@Autowired
	private ContestService contestService;
	
	@Autowired
	private ExcelConvertorService excelConvertorService;

	@PostMapping(value = "/questionUpload", headers = "content-type=multipart/*")
	public ResponseEntity<Object> upload(@RequestParam("file") MultipartFile file, @RequestParam("level") String level) {
		if (excelConvertorService.checkExcelFormat(file)) {
			try {
				List<Question> allQuestions = questionService.saveFileForBulkQuestion(file);
				return new ResponseEntity<Object>(allQuestions, HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Excel not uploaded");
			}
		} else {
			return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Please check excel file format");
		}
	}
	
	@PostMapping("savequestion")
	public ResponseEntity<Object> saveQuestion(@RequestBody Question question) throws IOException {
		System.out.println("savequestion Obj Prev : " + question);
		Question savedQuestion = null;
		String[] stringOfCidAndCl = new String[2];		
		stringOfCidAndCl = question.getContestLevel().split("@");
		String tempQid = question.getQuestionId();
		try {
		if (tempQid == ("")) {			
			tempQid = UUID.randomUUID().toString();
			question.setQuestionId(tempQid);
			question.setQuestionStatus("true");
		}		
		if(stringOfCidAndCl.length == 1) {
			question.setContestLevel(stringOfCidAndCl[0]);			
			savedQuestion = questionService.saveQuestion(question);
			}else {			
			Contest contest = new Contest(); // id, level
			contest = contestService.findByContestId(stringOfCidAndCl[1]);
			question.setContestLevel(stringOfCidAndCl[0]);
			savedQuestion = questionService.saveQuestion(question);
			QuestionStatus queStatus = new QuestionStatus();
			queStatus.setQuestionId(savedQuestion.getQuestionId());
			queStatus.setStatus(true);
			contest.getQuestionStatus().add(queStatus);
			contestService.saveContest(contest);
		}
		
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<Object>(savedQuestion, HttpStatus.OK);
	}

	@PostMapping("addselectedavailablequestiontocontest")
	private ResponseEntity<Object> addSelectedAvailableQueToContest(@RequestBody ArrayList<String> questionIdList) {
		List<Question> questionDetails = new ArrayList<>();
		try {
			String contestId = questionIdList.get(0);
			questionIdList.remove(0);
			Contest con = new Contest();
			con = contestService.findByContestId(contestId);
			questionDetails = questionService.findByQuestionIdIn(questionIdList);
			ArrayList<QuestionStatus> idWithstatus = con.getQuestionStatus();
			boolean flag = false;
			for (String idToChangeStatus : questionIdList) {
				int index = 0;
				for (QuestionStatus qs : idWithstatus) {
					if (idToChangeStatus.equals(qs.getQuestionId())) {
						if (qs.getStatus() == false) {
							con.getQuestionStatus().get(index).setStatus(true);
							flag = true;
						} else if (qs.getStatus() == true) {
							flag = true;
						}
					}
					index++;
				}
				if (flag == false) {
					QuestionStatus qsTemp = new QuestionStatus();
					qsTemp.setQuestionId(idToChangeStatus);
					qsTemp.setStatus(true);
					con.getQuestionStatus().add(qsTemp);
				} else {
					flag = false;
				}
			}
			contestService.saveContest(con);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return new ResponseEntity<Object>(questionDetails, HttpStatus.OK);		
	}
	
	@DeleteMapping("deletequestion") // cid, qid
	private ResponseEntity<Object> deleteQuestion(@RequestBody ArrayList<String> contestAndQuestionId) {
		try {
			if (contestAndQuestionId.get(0).equals("questionForLevel")) {
				Question questionStatusChange = questionService.findByQuestionId(contestAndQuestionId.get(1));
				questionStatusChange.setQuestionStatus("false");
				questionService.saveQuestion(questionStatusChange);
			} else {
				Contest contest = new Contest();
				contest = contestService.findByContestId(contestAndQuestionId.get(0));
				int index = 0;
				for (QuestionStatus qs : contest.getQuestionStatus()) {
					if (qs.getQuestionId().equals(contestAndQuestionId.get(1))) {
						contest.getQuestionStatus().get(index).setStatus(false);
					}
					index++;
				}
				contestService.saveContest(contest);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Please check Input ");
		}
		return ResponseEntity.status(HttpStatus.OK).body("Question deleted successfully");
	}
	
}
