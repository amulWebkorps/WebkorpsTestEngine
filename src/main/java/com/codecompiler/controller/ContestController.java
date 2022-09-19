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
		try {
			List<Contest> contesList = contestService.findAllContest();
			log.info("showContestList: contestList size ::"+contesList.size());
			return ResponseHandler.generateResponse("success", HttpStatus.OK, contesList);
		} catch (Exception ex) {
			log.error("Exception occured in getContestDetail :: "+ex.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}	
	} 

	@DeleteMapping("admin/deleteContest")
	public ResponseEntity<Object> deleteContest(@RequestParam String contestId) {
		log.info("deleteContest: started contestId = "+contestId);
		try {
			contestService.deleteContest(contestId);
			log.info("deleteContest: deleted successfully contestId ::"+contestId);
			return ResponseHandler.generateResponse("success", HttpStatus.OK, "Contest Deleted Successfully");
		} catch (Exception e) {
			log.error("Exception occured in deleteContest :: "+e.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
	@PostMapping("admin/createContest")
	public ResponseEntity<Object> addContest(@RequestBody Contest contest) {
		log.info("addContest: started contestName = "+contest.getContestName());
		try {
			contest.setDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
			contest = contestService.saveContest(contest);
			log.info("Contest added successfully");
			return ResponseHandler.generateResponse("success", HttpStatus.OK, contest);
		} catch (Exception ex) {
			log.error("Exception occured in addContest :: "+ex.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	@GetMapping("admin/getContestDetail")
	public ResponseEntity<Object> getContestDetail(@RequestParam String contestId) {
		log.info("getContestDetail: started contestId = "+contestId);
		try {
			Map<String, Object> contestDetail = contestService.getContestDetail(contestId);
			log.info("getContestDetail: ended contestDetails size ::"+contestDetail.size());
			return ResponseHandler.generateResponse("success", HttpStatus.OK, contestDetail);
		} catch (Exception ex) {
			log.error("Exception occured in getContestDetail :: "+ex.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.OK, ex.getMessage());
		}
		
	}
	
	
	@PostMapping("startContestPage")
	public ResponseEntity<Object> contestPage(@RequestParam(value = "contestId", required = false) String contestId,
			@RequestParam(value = "studentId", required = false) String studentId,
			@RequestParam(value = "language", required = false) String selectlanguage) {
		log.info("contestPage: started contestId = " + contestId + ", studentId = "+studentId+", language = "+selectlanguage);
		try {
			return ResponseHandler.generateResponse("success", HttpStatus.OK, contestService.contestPage(contestId, studentId, selectlanguage));
		}catch (Exception e) {
			log.error("Exception occured in contestPage :: "+e.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.OK, e.getMessage());
		}
		
	}
	


}
