package com.codecompiler.controllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.controller.QuestionController;
import com.codecompiler.entity.MCQ;
import com.codecompiler.service.MCQService;

public class QuestionControllerTest {
	@Mock
	private MCQService mcqService;

	@InjectMocks
	private QuestionController questionController;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);

	}

	@Test
	void testMCQUpload() throws Exception {

		String contestId = "1";
		List<MCQ> mockAllMCQList = new ArrayList<>();

		byte[] content = "test data".getBytes();
		MockMultipartFile mockFile = new MockMultipartFile("file", "test.xlsx",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", content);

		when(mcqService.saveFileForBulkMCQ(any(MultipartFile.class), eq(contestId))).thenReturn(mockAllMCQList);

		ResponseEntity<Object> response = questionController.mcqUpload(mockFile, contestId);

		assertNotNull(response);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("success", ((Map<String, Object>) response.getBody()).get("message"));
		verify(mcqService, times(1)).saveFileForBulkMCQ(any(MultipartFile.class), eq(contestId));
		assertNotNull(((Map<String, Object>) response.getBody()).get("data"));
	}

	@Test
	void testAddSelectedAvailableMCQToContest() {

		Map<String, List<String>> mcqIdList = new HashMap<>();
		List<String> ids = new ArrayList<>();
		ids.add(anyString());
		mcqIdList.put("MCQIds", ids);

		List<MCQ> mockMCQDetails = new ArrayList<>();
		when(mcqService.getAllMCQs(mcqIdList)).thenReturn(mockMCQDetails);

		ResponseEntity<Object> response = questionController.addSelectedAvailableMCQToContest(mcqIdList);
		Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
		assertEquals("success", responseBody.get("message"));
		assertEquals(HttpStatus.OK.value(), responseBody.get("statusCode"));
		assertEquals(mockMCQDetails, responseBody.get("data"));
	}

	@Test
	void testUpdateMcqQuestionStatus() {

		ArrayList<String> contestAndMcqQuestionId = new ArrayList<>();
		contestAndMcqQuestionId.add("CONTEST_ID");
		contestAndMcqQuestionId.add("MCQ_QUESTION_ID");

		ResponseEntity<Object> response = questionController.updateMcqQuestionStatus(contestAndMcqQuestionId);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		verify(mcqService).saveMcqQuestionOrContest(contestAndMcqQuestionId);

		assertNotNull(response.getBody());

		if (response.getBody() instanceof Map) {
			Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
			assertEquals("success", responseBody.get("message"));
			assertEquals(HttpStatus.OK.value(), responseBody.get("statusCode"));
			assertNotNull(responseBody.get("data"));

			assertTrue(responseBody.get("data") instanceof List);
		} else {
			fail("Response body structure is unexpected");
		}

	}

	@Test
	void testGetAllMcq() {

		List<MCQ> mockMcqs = new ArrayList<>();
		mockMcqs.add(new MCQ());

		when(mcqService.getAllMcq()).thenReturn(mockMcqs);

		ResponseEntity<Object> response = questionController.getAllMcq();

		assertEquals(HttpStatus.OK, response.getStatusCode());

		verify(mcqService).getAllMcq();

		assertNotNull(response.getBody());

		if (response.getBody() instanceof Map) {
			Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
			assertEquals("success", responseBody.get("message"));
			assertEquals(HttpStatus.OK.value(), responseBody.get("statusCode"));
			assertNotNull(responseBody.get("data"));

			assertTrue(responseBody.get("data") instanceof List);
			assertEquals(mockMcqs.size(), ((List<?>) responseBody.get("data")).size());
		} else {
			fail("Response body structure is unexpected");
		}
	}

	@Test
	void testDeleteMcq() {

		String mockMcqId = "yourMcqId";
		MCQ mockDeletedMcq = new MCQ();

		when(mcqService.deleteMcq(mockMcqId)).thenReturn(mockDeletedMcq);

		ResponseEntity<Object> response = questionController.deleteMcq(mockMcqId);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		verify(mcqService).deleteMcq(mockMcqId);

		assertNotNull(response.getBody());

		if (response.getBody() instanceof Map) {
			Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
			assertEquals("success", responseBody.get("message"));
			assertEquals(HttpStatus.OK.value(), responseBody.get("statusCode"));
			assertNotNull(responseBody.get("data"));
			assertEquals(mockDeletedMcq, responseBody.get("data"));
		} else {
			fail("Response body structure is unexpected");
		}
	}
}
