package com.codecompiler;

import com.codecompiler.dto.MyCellDTO;
import com.codecompiler.entity.Contest;
import com.codecompiler.entity.Question;
import com.codecompiler.exception.InsufficientDataException;
import com.codecompiler.exception.RecordNotFoundException;
import com.codecompiler.exception.UnSupportedFormatException;
import com.codecompiler.helper.ExcelPOIHelper;
import com.codecompiler.repository.ContestRepository;
import com.codecompiler.repository.QuestionRepository;
import com.codecompiler.service.ContestService;
import com.codecompiler.service.ExcelConvertorService;
import com.codecompiler.service.impl.QuestionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceImplTest {

    @Mock
    private ContestRepository contestRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private ExcelConvertorService excelConvertorService;

    @Mock
    private ContestService contestService;

    @Mock
    private ExcelPOIHelper excelPOIHelper;

    @InjectMocks
    private QuestionServiceImpl questionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveFileForBulkQuestion_Success() throws IOException {
        // Arrange
        String fileName = "SampleQuestion1.xlsx";
        byte[] fileContent = Files.readAllBytes(Paths.get("/Users/apple/Downloads/CSVFiles", fileName));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                fileName,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                fileContent
        );

        Contest contest = new Contest();
        List<Question> questions = Arrays.asList(new Question(), new Question());
        Map<Integer, List<MyCellDTO>> data = Collections.singletonMap(1, Collections.emptyList());

        when(contestRepository.findByContestId(anyString())).thenReturn(contest);
        when(excelPOIHelper.readExcel(any(InputStream.class), eq(fileName))).thenReturn(data);
        when(excelConvertorService.convertExcelToListOfQuestions(eq(data))).thenReturn(questions);
        when(questionRepository.saveAll(eq(questions))).thenReturn(questions);
//        when(contestService.saveContest(any(Contest.class))).thenReturn(contest);

        // Act
        List<Question> result = questionService.saveFileForBulkQuestion(file, "contestId", "contestLevel");

        // Assert
        assertNotNull(result);
//        assertEquals(2, result.size());

        // Verify interactions
        verify(contestRepository).findByContestId(eq("contestId"));
        verify(excelPOIHelper).readExcel(any(InputStream.class), eq(fileName));
        verify(excelConvertorService).convertExcelToListOfQuestions(eq(data));
        verify(questionRepository).saveAll(eq(questions));
//        verify(contestService).saveContest(eq(contest));
    }

    @Test
    public void testSaveFileForBulkQuestion_EmptyFile() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "content".getBytes());
        Contest contest = new Contest();
        Map<Integer, List<MyCellDTO>> data = Collections.emptyMap();

        when(contestRepository.findByContestId(anyString())).thenReturn(contest);
        when(excelPOIHelper.readExcel(any(InputStream.class), anyString())).thenReturn(data);

        // Act and Assert
        assertThrows(RecordNotFoundException.class, () -> questionService.saveFileForBulkQuestion(file, "contestId", "contestLevel"));
        verify(contestRepository).findByContestId(eq("contestId"));
        verify(excelPOIHelper).readExcel(any(InputStream.class), eq("test.xlsx"));
    }

    @Test
    public void testSaveFileForBulkQuestion_InvalidFileFormat() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",  // Use an unsupported file format
                "text/plain",
                "content".getBytes());

        // Act and Assert
        assertThrows(UnSupportedFormatException.class, () -> questionService.saveFileForBulkQuestion(file, "contestId", "contestLevel"));
        verify(contestRepository, never()).findByContestId(anyString());
        verify(excelPOIHelper, never()).readExcel(any(InputStream.class), anyString());
        verify(excelConvertorService, never()).convertExcelToListOfQuestions(anyMap());
        verify(questionRepository, never()).saveAll(anyList());
        verify(contestService, never()).saveContest(any(Contest.class));
    }


    @Test
    public void testSaveFileForBulkQuestionWithUnsupportedFormat() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());

        assertThrows(UnSupportedFormatException.class,
                () -> questionService.saveFileForBulkQuestion(file, "contestId", "contestLevel"));
    }


    @Test
    public void testSaveContestsWhenContestIsNull() {
        when(contestService.findByContestId(anyString())).thenReturn(null);

        assertThrows(RecordNotFoundException.class,
                () -> questionService.saveContests("contestId", new HashMap<>()));
    }

    @Test
    public void testGetAllQuestionsWithInsufficientData() {
        Map<String, List<String>> questionIdList = Collections.singletonMap("contestId", Collections.singletonList("contestId"));

        assertThrows(InsufficientDataException.class, () -> questionService.getAllQuestions(questionIdList));
    }

    @Test
    public void testGetAllQuestionsWithNullContestId() {
        Map<String, List<String>> questionIdList = new HashMap<>();
        questionIdList.put("questionsIds", Collections.singletonList("questionId1"));

        assertThrows(NullPointerException.class, () -> questionService.getAllQuestions(questionIdList));
    }
}


