package com.codecompiler.dto;

import com.codecompiler.entity.StudentTestDetail;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class StudentTestDetailDTO {

  private String studentId;

  private String contestId;

  private String code;

  private String codeLanguage;

  private Set<QuestionDetailDTO> questionDetails;

  public StudentTestDetail prepareStudentObj(StudentTestDetailDTO studentTestDetailsDTO) {
    return new StudentTestDetail(studentTestDetailsDTO.getStudentId(), studentTestDetailsDTO.getContestId(),
        studentTestDetailsDTO.getCodeLanguage(), LocalDateTime.now(),studentTestDetailsDTO.getQuestionDetails());
  }
}
