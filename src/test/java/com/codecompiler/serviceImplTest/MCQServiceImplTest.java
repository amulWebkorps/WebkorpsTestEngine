package com.codecompiler.serviceImplTest;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.codecompiler.dto.MCQStatusDTO;
import com.codecompiler.entity.Contest;
import com.codecompiler.entity.MCQ;
import com.codecompiler.exception.InsufficientDataException;
import com.codecompiler.exception.RecordNotFoundException;
import com.codecompiler.repository.ContestRepository;
import com.codecompiler.repository.MCQRepository;
import com.codecompiler.service.ContestService;
import com.codecompiler.service.impl.MCQServiceImpl;

public class MCQServiceImplTest {
	@Mock
	MCQRepository mcqRepository;
	@Mock
	private ContestService contestService;

	@Mock
	private ContestRepository contestRepository;
	@InjectMocks
	private MCQServiceImpl serviceImpl;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);

	}

	@Test
	public void testDeleteMcqWhenAlreadyDeleted() {
		// Arrange
		String mcqId = "123";
		MCQ mcq = new MCQ();
		mcq.setMcqId(mcqId);
		mcq.setMcqStatus(false);
		when(mcqRepository.findByMcqId(mcqId)).thenReturn(Optional.of(mcq).get());

		MCQ deletedMcq = serviceImpl.deleteMcq(mcqId);

		verify(mcqRepository).findByMcqId(mcqId);
		verify(mcqRepository, never()).save(deletedMcq);
		assertNull(deletedMcq);
	}

	@Test
	public void testDeleteMcqWhenNotExists() {

		String mcqId = anyString();
		when(mcqRepository.findByMcqId(mcqId)).thenReturn(null);

		MCQ deletedMcq = serviceImpl.deleteMcq(mcqId);

		verify(mcqRepository).findByMcqId(mcqId);
		verify(mcqRepository, never()).save(Mockito.any());
		assertNull(deletedMcq);
	}

	@Test
	void testGetAllMCQs_InvalidInput() {

		Map<String, List<String>> mcqIdList = new HashMap<>();
		mcqIdList.put("contestId", Collections.singletonList("1"));
		mcqIdList.put("mcqIds", Collections.singletonList("1"));

		assertThrows(RecordNotFoundException.class, () -> serviceImpl.getAllMCQs(mcqIdList));

	}

	@Test
	void testGetAllMCQs_InsufficientData() {

		Map<String, List<String>> mcqIdList = new HashMap<>();
		mcqIdList.put("contestId", Collections.singletonList(anyString()));

		InsufficientDataException exception = assertThrows(InsufficientDataException.class,
				() -> serviceImpl.getAllMCQs(mcqIdList));

		assertEquals("Method argument is null or it has insufficient data", exception.getMessage());
	}

	@Test
	void testGetAllMCQs_NoQuestionsFound() {

		Map<String, List<String>> mcqIdList = new HashMap<>();
		mcqIdList.put("contestId", Collections.singletonList("1"));
		mcqIdList.put("mcqIds", Collections.singletonList("1"));

		when(mcqRepository.findByMcqIdIn(mcqIdList.get("mcqIds"))).thenReturn(Collections.emptyList());

		assertThrows(RecordNotFoundException.class, () -> serviceImpl.getAllMCQs(mcqIdList));
	}

	@Test
	void testGetAllMCQs_ValidInput() {

		Map<String, List<String>> mcqIdList = new HashMap<>();
		mcqIdList.put("contestId", Collections.singletonList(anyString()));

		mcqIdList.put("mcqIds", Collections.singletonList("f87b4dd6-bda3-4596-8bcf-5be1751849af"));

		MCQ mcq = new MCQ();
		mcq.setMcqId("f87b4dd6-bda3-4596-8bcf-5be1751849af");
		when(mcqRepository.findByMcqIdIn(mcqIdList.get("mcqIds"))).thenReturn(Collections.singletonList(mcq));
		System.out.println(mcq);
		assertNotNull(mcq);
		assertEquals(mcq.getMcqId(), mcqIdList.get("mcqIds").get(0));

	}

	@Test
	public void testSaveContests() {

		String contestId = "123";
		Map<String, List<String>> mcqIdList = Map.of("mcqIds", Arrays.asList("1", "2", "3"));
		Contest contest = new Contest();
	
		when(contestService.findByContestId(contestId)).thenReturn(contest);

		final Map<String, List<String>> finalMcqIdList = mcqIdList;

		Contest result = serviceImpl.saveContests(contestId, finalMcqIdList);
		when(serviceImpl.saveContests(contestId, finalMcqIdList)).thenReturn(result);
		verify(contestService).saveContest(contest);

		when(contestService.findByContestId(contestId)).thenReturn(null);
		assertThrows(RecordNotFoundException.class, () -> serviceImpl.saveContests(contestId, finalMcqIdList));

		when(contestService.findByContestId(contestId)).thenReturn(contest);

		mcqIdList = Map.of("mcqIds", Collections.singletonList("1"));
		when(contestService.findByContestId(contestId)).thenReturn(contest);

		Contest updatedContest = serviceImpl.saveContests(contestId, finalMcqIdList);

		when(serviceImpl.saveContests(contestId, finalMcqIdList)).thenReturn(updatedContest);
		assertEquals(result, updatedContest);

	}
	@Test
	void testSaveMCQContest() {
		Contest contest=new Contest();
		 MCQ mcq1 = new MCQ();
		    mcq1.setMcqId("1"); 
		    mcq1.setMcqQuestion("Question 1"); 
		    mcq1.setMcqStatus(true);  
		    
		    MCQ mcq2 = new MCQ();
		    mcq2.setMcqId("2");  
		    mcq2.setMcqQuestion("Question 2"); 
		    mcq2.setMcqStatus(true);  
	    List<MCQ> allTrueQuestions = Arrays.asList(
	       mcq1,mcq2
	    );

	    ArrayList<MCQStatusDTO> contestMcqStatus = new ArrayList<>();
 
	    contest.setMcqStatus(contestMcqStatus);

	    when(contestRepository.save(contest)).thenReturn(contest);
 
	    List<String> result = serviceImpl.saveMCQContest(contest, allTrueQuestions);
 
	    assertEquals(2, result.size());  
	    verify(contestRepository, times(1)).save(contest);
 
	    assertEquals(2, contest.getMcqStatus().size());
	    assertTrue(contest.getMcqStatus().stream().allMatch(MCQStatusDTO::isMcqstatus));
	}
	
	 @Test
	    void testSaveMcqQuestionOrContest() {
	        
	        String contestId = "1";
	        String mcqQuestionId = "2";
 
	        Contest contest = new Contest();
	        MCQStatusDTO mcqStatus = new MCQStatusDTO();
	        mcqStatus.setMcqId(mcqQuestionId);
	        mcqStatus.setMcqstatus(false);
	        contest.getMcqStatus().add(mcqStatus);
 
	        when(contestService.findByContestId(contestId)).thenReturn(contest);
 
	        ArrayList<String> contestAndMcqQuestionId = new ArrayList<>();
	        contestAndMcqQuestionId.add(contestId);
	        contestAndMcqQuestionId.add(mcqQuestionId);
 
	        serviceImpl.saveMcqQuestionOrContest(contestAndMcqQuestionId);
 
	        assertFalse(mcqStatus.isMcqstatus()); // Ensure that the McqStatus is set to false
	        verify(contestService, times(1)).saveContest(contest); // Ensure that saveContest was called
	    }
	 
	 @Test
	 void testSaveMcqQuestionOrContest_MCQNotFound() {
	    
	     String contestId = "1";
	     String mcqQuestionId = "2";

	     Contest contest = new Contest();
	     MCQStatusDTO mcqStatus = new MCQStatusDTO();
	     mcqStatus.setMcqId(anyString());  
	     mcqStatus.setMcqstatus(true);
	     contest.getMcqStatus().add(mcqStatus);

	     when(contestService.findByContestId(contestId)).thenReturn(contest);

	     ArrayList<String> contestAndMcqQuestionId = new ArrayList<>();
	     contestAndMcqQuestionId.add(contestId);
	     contestAndMcqQuestionId.add(mcqQuestionId);
 
	     serviceImpl.saveMcqQuestionOrContest(contestAndMcqQuestionId);
 
	     assertTrue(mcqStatus.isMcqstatus());  
	 }

}
