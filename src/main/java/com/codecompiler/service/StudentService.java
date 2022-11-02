package com.codecompiler.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.dto.ParticipantDTO;
import com.codecompiler.dto.StudentDTO;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.Student;

public interface StudentService {
    
	public Student findById(String studentId);
	
	public Student findByEmailAndPassword(String email, String password);
	
	public Student findByEmail(String studentEmail);
	
	public List<StudentDTO> findByContestId(String contestId);
	
	public Student saveStudent(Student studentDetails);
	
	public List<String> findEmailByStatus(Boolean True);
	
	public List<String> saveFileForBulkParticipator(MultipartFile file);
	
	public Student deleteByEmail(String emailId);
		
	public Student updateStudentDetails(String studentId, String contestId, Set<String> questionIds,
			ArrayList<Boolean> testCasesSuccess, String complilationMessage, String fileName);
	
	public Student finalSubmitContest(String studentId, Double percentage);
	
	public List<String> findAll();
	
	Map<String, Object> getParticipatorDetail( String studentId) throws IOException;
	
	public List<String> filterParticipants(String filterByString);
	
	public List<String> findByContestLevel(String filterByString);

	public List<ParticipantDTO> findByContestIdForMCQ(String contestId);


}
