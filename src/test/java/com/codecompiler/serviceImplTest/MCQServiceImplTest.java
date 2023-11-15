package com.codecompiler.serviceImplTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
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
import org.springframework.data.mongodb.core.aggregation.SetOperators.AnyElementTrue;
import org.springframework.mock.web.MockMultipartFile;

import com.codecompiler.dto.MCQStatusDTO;
import com.codecompiler.dto.MyCellDTO;
import com.codecompiler.entity.Contest;
import com.codecompiler.entity.MCQ;
import com.codecompiler.exception.InsufficientDataException;
import com.codecompiler.exception.RecordNotFoundException;
import com.codecompiler.helper.ExcelPOIHelper;
import com.codecompiler.repository.ContestRepository;
import com.codecompiler.repository.MCQRepository;
import com.codecompiler.service.ContestService;
import com.codecompiler.service.ExcelConvertorService;
import com.codecompiler.service.impl.MCQServiceImpl;

public class MCQServiceImplTest {
	@Mock
	MCQRepository mcqRepository;
	@Mock
	private ContestService contestService;
	@Mock
	ExcelConvertorService excelConvertorService;

	@Mock
	ExcelPOIHelper excelPOIHelper;
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
		mcqIdList.put("contestId", Collections.singletonList("1"));

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
		Contest contest = new Contest();
		MCQ mcq1 = new MCQ();
		mcq1.setMcqId("1");
		mcq1.setMcqQuestion("Question 1");
		mcq1.setMcqStatus(true);

		MCQ mcq2 = new MCQ();
		mcq2.setMcqId("2");
		mcq2.setMcqQuestion("Question 2");
		mcq2.setMcqStatus(true);
		List<MCQ> allTrueQuestions = Arrays.asList(mcq1, mcq2);

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

		assertFalse(mcqStatus.isMcqstatus());
		verify(contestService, times(1)).saveContest(contest);
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
		assertNotNull(contestAndMcqQuestionId);
		assertNotNull(contest);
		assertTrue(mcqStatus.isMcqstatus());
	}

	@Test
	void testGetAllMcq() {

		MCQ mcq1 = new MCQ();
		mcq1.setMcqId("1");
		mcq1.setMcqQuestion("Question 1");

		MCQ mcq2 = new MCQ();
		mcq2.setMcqId("2");
		mcq2.setMcqQuestion("Question 2");

		List<MCQ> expectedMcqs = Arrays.asList(mcq1, mcq2);
		when(mcqRepository.findAllMCQ(true)).thenReturn(expectedMcqs);

		List<MCQ> result = serviceImpl.getAllMcq();

		assertNotNull(mcq1);
		assertNotNull(mcq2);
		assertNotNull(expectedMcqs);
		assertEquals(expectedMcqs, result);
	}

	@Test
	void testFindByMcqId() {

		String mcqId = "1";
		MCQ expectedMcq = new MCQ();
		expectedMcq.setMcqId(mcqId);
		expectedMcq.setMcqQuestion("Sample Question");

		when(mcqRepository.findByMcqId(mcqId)).thenReturn(expectedMcq);

		MCQ result = serviceImpl.findByMcqId(mcqId);

		assertNotNull(result);
		assertNotNull(expectedMcq);
		assertEquals(expectedMcq, result);
		String nonExistentMcqId = "999";
		when(mcqRepository.findByMcqId(nonExistentMcqId)).thenReturn(null);

		MCQ nonExistentResult = serviceImpl.findByMcqId(nonExistentMcqId);
		assertNull(nonExistentResult);

		verify(mcqRepository, times(2)).findByMcqId(anyString());

		verify(mcqRepository, times(1)).findByMcqId(mcqId);
		verify(mcqRepository, times(1)).findByMcqId(nonExistentMcqId);
	}

	@Test
	void testSaveFileForBulkMCQ_ContestIdIsNull() throws IOException {

		MockMultipartFile mockFile = new MockMultipartFile("file", "test.xlsx",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "test data".getBytes());

		List<MyCellDTO> sampleData = new ArrayList<>();

		sampleData.add(new MyCellDTO("Option A"));
		sampleData.add(new MyCellDTO("Option B"));
		sampleData.add(new MyCellDTO("Option C"));

		Map<Integer, List<MyCellDTO>> sampleExcelData = new HashMap<>();
		sampleExcelData.put(0, sampleData);
		when(excelPOIHelper.readExcel(mockFile.getInputStream(), mockFile.getOriginalFilename()))
				.thenReturn(sampleExcelData);
		MCQ mcq1 = new MCQ();
		mcq1.setMcqId("1");
		mcq1.setMcqQuestion("What is the capital of France?");
		mcq1.setOption1("Berlin");
		mcq1.setOption2("London");
		mcq1.setOption3("Madrid");
		mcq1.setOption4("Paris");
		mcq1.setCorrectOption(Arrays.asList("Paris"));
		mcq1.setMcqStatus(true);
		mcq1.setCreatedDate("2023-10-12");
		MCQ mcq2 = new MCQ();
		mcq2.setMcqId("2");
		mcq2.setMcqQuestion("Which planet is known as the Red Planet?");
		mcq2.setOption1("Venus");
		mcq2.setOption2("Mars");
		mcq2.setOption3("Saturn");
		mcq2.setOption4("Jupiter");
		mcq2.setCorrectOption(Arrays.asList("Mars"));
		mcq2.setMcqStatus(true);
		mcq2.setCreatedDate("2023-10-12");
		List<MCQ> mockAllMCQ = Arrays.asList(mcq1, mcq2);
		when(excelConvertorService.convertExcelToListOfMCQ(sampleExcelData)).thenReturn(mockAllMCQ);
		when(mcqRepository.findByMcqIdIn(anyList())).thenReturn(mockAllMCQ);

		assertNotNull(mockAllMCQ);
		assertNotNull(sampleExcelData);

	}

	@Test
	void testSaveFileForBulkMCQ_DataNotPresent() throws IOException {
		Map<Integer, List<MyCellDTO>> emptyData = new HashMap<>();
		MockMultipartFile mockFile = new MockMultipartFile("file", "test.xlsx",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);

		when(excelPOIHelper.readExcel(any(InputStream.class), eq("test.xlsx"))).thenReturn(emptyData);

		RecordNotFoundException exception = assertThrows(RecordNotFoundException.class, () -> {
			serviceImpl.saveFileForBulkMCQ(mockFile, "contestId");
		});

		assertEquals("saveFileForBulkMCQ:: Data isn't present in the file", exception.getMessage());
	}

	@Test
	void testSaveMcq() {

		MCQ existingMCQ1 = new MCQ();
		existingMCQ1.setMcqId("1");
		existingMCQ1.setMcqQuestion("Sample Question 1");
		existingMCQ1.setMcqStatus(false);

		MCQ existingMCQ2 = new MCQ();
		existingMCQ2.setMcqId("2");
		existingMCQ2.setMcqQuestion("Sample Question 2");
		existingMCQ2.setMcqStatus(true);

		List<MCQ> oldMcqList = Arrays.asList(existingMCQ1, existingMCQ2);

		MCQ newMCQ1 = new MCQ();
		newMCQ1.setMcqQuestion("Sample Question 1");
		newMCQ1.setMcqStatus(true);

		MCQ newMCQ2 = new MCQ();
		newMCQ2.setMcqQuestion("New Question 1");
		newMCQ2.setMcqStatus(true);

		MCQ newMCQ3 = new MCQ();
		newMCQ3.setMcqQuestion("New Question 2");
		newMCQ3.setMcqStatus(true);
		List<MCQ> allMcqList = Arrays.asList(newMCQ1, newMCQ2, newMCQ3);

		when(mcqRepository.findAll()).thenReturn(oldMcqList);

		List<MCQ> result = serviceImpl.saveMcq(allMcqList);

		assertNotNull(result);

		verify(mcqRepository, times(1)).save(existingMCQ1);

		verify(mcqRepository, times(1)).saveAll(Arrays.asList(newMCQ2, newMCQ3));

		assertTrue(existingMCQ1.isMcqStatus());
		assertTrue(existingMCQ2.isMcqStatus());

		assertTrue(newMCQ1.isMcqStatus());
		assertTrue(newMCQ2.isMcqStatus());
		assertTrue(newMCQ3.isMcqStatus());
	}

	@Test
	void testSaveMcq_NoNewMCQs() {

		MCQ existingMCQ1 = new MCQ();
		existingMCQ1.setMcqId("1");
		existingMCQ1.setMcqQuestion("Sample Question 1");
		existingMCQ1.setMcqStatus(true);

		MCQ existingMCQ2 = new MCQ();
		existingMCQ2.setMcqId("2");
		existingMCQ2.setMcqQuestion("Sample Question 2");
		existingMCQ2.setMcqStatus(true);

		List<MCQ> oldMcqList = Arrays.asList(existingMCQ1, existingMCQ2);

		MCQ newMCQ1 = new MCQ();
		newMCQ1.setMcqQuestion("Sample Question 1");
		newMCQ1.setMcqStatus(true);
		MCQ newMCQ2 = new MCQ();
		newMCQ2.setMcqQuestion("Sample Question 2");
		newMCQ2.setMcqStatus(true);
		List<MCQ> allMcqList = Arrays.asList(newMCQ1, newMCQ2);

		when(mcqRepository.findAll()).thenReturn(oldMcqList);

		List<MCQ> result = serviceImpl.saveMcq(allMcqList);

		assertNotNull(result);

		verify(mcqRepository, never()).save(any(MCQ.class));
		verify(mcqRepository, never()).saveAll(anyList());

		assertTrue(existingMCQ1.isMcqStatus());
		assertTrue(existingMCQ2.isMcqStatus());
		assertTrue(newMCQ1.isMcqStatus());
		assertTrue(newMCQ2.isMcqStatus());
	}

	@Test
	void testSaveMcq_AllDuplicates() {

		MCQ existingMCQ1 = new MCQ();
		existingMCQ1.setMcqId("1");
		existingMCQ1.setMcqQuestion("Sample Question 1");
		existingMCQ1.setMcqStatus(true);
	 
		
		MCQ existingMCQ2 = new MCQ();
		existingMCQ2.setMcqId("2");
		existingMCQ2.setMcqQuestion("Sample Question 2");
		existingMCQ2.setMcqStatus(true);

		 
		List<MCQ> oldMcqList = Arrays.asList(existingMCQ1, existingMCQ2);

		MCQ newMCQ1 = new MCQ();
		newMCQ1.setMcqQuestion("Sample Question 1");
		newMCQ1.setMcqStatus(true);
		MCQ newMCQ2 = new MCQ();
		newMCQ2.setMcqQuestion("Sample Question 2");
		newMCQ2.setMcqStatus(true);
		List<MCQ> allMcqList = Arrays.asList(newMCQ1, newMCQ2);

		when(mcqRepository.findAll()).thenReturn(oldMcqList);

		List<MCQ> result = serviceImpl.saveMcq(allMcqList);

		assertNotNull(result);

		verify(mcqRepository, never()).save(any(MCQ.class));
		verify(mcqRepository, never()).saveAll(anyList());

		assertTrue(existingMCQ1.isMcqStatus());
		assertTrue(existingMCQ2.isMcqStatus());
		assertTrue(newMCQ1.isMcqStatus());
		assertTrue(newMCQ2.isMcqStatus());
	}

 
}
