package com.codecompiler.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.codecompiler.dto.McqSubmitDto;
import com.codecompiler.entity.Contest;
import com.codecompiler.response.ResponseHandler;
import com.codecompiler.service.ContestService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@Slf4j
public class ContestController {

	@Autowired
	private ContestService contestService;

	@GetMapping("admin/getAllContestList")
	public ResponseEntity<Object> showContestList() {
		log.info("showContestList: started");
		List<Contest> contestList = contestService.findAllContest();
		log.info("showContestList: contestList size :: {}", contestList.size());
		return ResponseHandler.generateResponse("success", HttpStatus.OK, contestList);
	}

	@DeleteMapping("admin/deleteContest")
	public ResponseEntity<Object> deleteContest(@RequestParam String contestId) {
		log.info("deleteContest: started contestId = {}", contestId);
		contestService.deleteContest(contestId);
		log.info("deleteContest: deleted successfully contestId :: {}", contestId);
		return ResponseHandler.generateResponse("success", HttpStatus.OK, "Contest Deleted Successfully");
	}

	@PostMapping("admin/createContest")
	public ResponseEntity<Object> addContest(@RequestBody Contest contest) {
		log.info("addContest: started contestName = {}", contest.getContestName());
		Contest existingContest = contestService.findByContestName(contest.getContestName());
		if (existingContest == null) {
			contest.setDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
			contest = contestService.saveContest(contest);
			log.info("Contest added successfully");
			return ResponseHandler.generateResponse("success", HttpStatus.OK, contest);
		} else {
			return ResponseHandler.generateResponse("error", HttpStatus.CONFLICT,
					"Contest with the same name already exists");
		}
	}

	@GetMapping("admin/getContestDetail")
	public ResponseEntity<Object> getContestDetail(@RequestParam String contestId, @RequestParam String contestType) {
		log.info("getContestDetail: started contestId {} and contestType = {}", contestId, contestType);
		Map<String, Object> contestDetail = contestService.getContestDetail(contestId, contestType);
		log.info("getContestDetail: ended contestDetails size :: {}", contestDetail.size());
		return ResponseHandler.generateResponse("success", HttpStatus.OK, contestDetail);
	}

	@PostMapping("startContestPage")
	public ResponseEntity<Object> contestPage(@RequestParam(value = "contestId", required = false) String contestId,
											  @RequestParam(value = "studentId", required = false) String studentId,
											  @RequestParam(value = "language", required = false) String selectLanguage) {
		log.info("contestPage: started contestId = {}, studentId = {}, language = {}", contestId, studentId, selectLanguage);
		return ResponseHandler.generateResponse("success", HttpStatus.OK,
				contestService.contestPage(contestId, studentId, selectLanguage));
	}

	@PostMapping("startMCQContest")
	public ResponseEntity<Object> fetchAllUploadedQuestions(@RequestParam(value = "contestId") String contestId,
															@RequestParam(value = "studentId") String studentId) {
		log.info("Start MCQ Contest : And contestId = {}", contestId);
		Map<String, Object> mcq = contestService.findAllUploadedQuestions(contestId, studentId);
		if (mcq != null)
			return ResponseHandler.generateResponse("success", HttpStatus.OK, mcq);
		else
			return ResponseHandler.generateResponse("Contest Or Contest Questions are not present", HttpStatus.NOT_FOUND,
					null);
	}

	@PostMapping("submitMcqContest")
	public ResponseEntity<Object> submitMcqContest(@RequestBody McqSubmitDto mcqSubmitDto) {
		log.info("Mcq contest submit : ");
		if (contestService.submitMcqContest(mcqSubmitDto))
			return ResponseHandler.generateResponse("success", HttpStatus.OK, "Test Submitted Successfully");
		else
			return ResponseHandler.generateResponse("Something went wrong....", HttpStatus.OK, null);
	}
}
