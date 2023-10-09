package com.codecompiler.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codecompiler.dto.McqSubmitDto;
import com.codecompiler.entity.Contest;
import com.codecompiler.reponse.ResponseHandler;
import com.codecompiler.service.ContestService;

import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin(origins = "*")
@Slf4j
public class ContestController {

	@Autowired
	private ContestService contestService;

	@GetMapping("admin/getAllContestList")
	public ResponseEntity<Object> showContestList() {
		log.info("showContestList: started");

		List<Contest> contesList = contestService.findAllContest();
		log.info("showContestList: contestList size ::" + contesList.size());
		return ResponseHandler.generateResponse("success", HttpStatus.OK, contesList);

	}

	@DeleteMapping("admin/deleteContest")
	public ResponseEntity<Object> deleteContest(@RequestParam String contestId) {
		log.info("deleteContest: started contestId = " + contestId);

		contestService.deleteContest(contestId);
		log.info("deleteContest: deleted successfully contestId ::" + contestId);
		return ResponseHandler.generateResponse("success", HttpStatus.OK, "Contest Deleted Successfully");

	}

	@PostMapping("admin/createContest")
	public ResponseEntity<Object> addContest(@RequestBody Contest contest) {
		log.info("addContest: started contestName = " + contest.getContestName());

		Contest exsistingContest = contestService.findByContestName(contest.getContestName());
		if (exsistingContest == null) {
			contest.setDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
			contest = contestService.saveContest(contest);
			log.info("Contest added successfully");
			return ResponseHandler.generateResponse("success", HttpStatus.OK, contest);
		} else {
			return ResponseHandler.generateResponse("error", HttpStatus.CONFLICT,
					"Contest with same name already exsists");
		}

	}

	@GetMapping("admin/getContestDetail")
	public ResponseEntity<Object> getContestDetail(@RequestParam String contestId, @RequestParam String contestType) {
		log.info("getContestDetail: started contestId " + contestId + " and contestType = " + contestType);

		Map<String, Object> contestDetail = contestService.getContestDetail(contestId, contestType);
		log.info("getContestDetail: ended contestDetails size ::" + contestDetail.size());
		return ResponseHandler.generateResponse("success", HttpStatus.OK, contestDetail);

	}

	@PostMapping("startContestPage")
	public ResponseEntity<Object> contestPage(@RequestParam(value = "contestId", required = false) String contestId,
			@RequestParam(value = "studentId", required = false) String studentId,
			@RequestParam(value = "language", required = false) String selectlanguage) {
		log.info("contestPage: started contestId = " + contestId + ", studentId = " + studentId + ", language = "
				+ selectlanguage);

		return ResponseHandler.generateResponse("success", HttpStatus.OK,
				contestService.contestPage(contestId, studentId, selectlanguage));

	}

	@PostMapping("startMCQContest")
	public ResponseEntity<Object> fetchAllUploadedQuetions(@RequestParam(value = "contestId") String contestId,
			@RequestParam(value = "studentId") String studentId) {
		log.info("Start MCQ Contest : And contestId = " + contestId);

		Map<String, Object> mcq = contestService.findAllUploadedQuetions(contestId, studentId);
		if (mcq != null)
			return ResponseHandler.generateResponse("success", HttpStatus.OK, mcq);
		else
			return ResponseHandler.generateResponse("Contest Or Contest Quetions is not present", HttpStatus.NOT_FOUND,
					null);

	}

	@PostMapping("submitMcqContest")
	public ResponseEntity<Object> submitMcqContest(@RequestBody McqSubmitDto mcqSubmitDto) {
		log.info("Mcq contest submit : ");

		if (contestService.submitMcqContest(mcqSubmitDto))
			return ResponseHandler.generateResponse("success", HttpStatus.OK, "Test Submited Successfully");
		else
			return ResponseHandler.generateResponse("Something went wrong....", HttpStatus.OK, null);

	}
}
