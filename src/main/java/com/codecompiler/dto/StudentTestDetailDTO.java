package com.codecompiler.dto;

import com.codecompiler.entity.StudentTestDetail;
import com.codecompiler.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class StudentTestDetailDTO extends User {

  private String studentId;

  private String contestId;

  private String code;

  private String codeLanguage;

  private List<QuestionDetailDTO> questionDetails;

  private Double studentPercentage;

  private String compilationMessage;

  public StudentTestDetailDTO(String studentId, String contestId, String codeLanguage, List<QuestionDetailDTO> questionDetails) {
    this.studentId = studentId;
    this.contestId = contestId;
    this.codeLanguage = codeLanguage;
    this.questionDetails = questionDetails;
  }

  public StudentTestDetailDTO(String studentId, String contestId, String codeLanguage, List<QuestionDetailDTO> questionDetails,
                              Double studentPercentage) {
    this.studentId = studentId;
    this.contestId = contestId;
    this.codeLanguage = codeLanguage;
    this.questionDetails = questionDetails;
    this.studentPercentage = studentPercentage;
  }

  public StudentTestDetail prepareStudentObj(StudentTestDetailDTO studentTestDetailsDTO) {
    return new StudentTestDetail(studentTestDetailsDTO.getStudentId(), studentTestDetailsDTO.getContestId(),
        studentTestDetailsDTO.getCodeLanguage(), LocalDateTime.now(), studentTestDetailsDTO.getQuestionDetails());
  }

  public StudentTestDetailDTO prepareStudentObj(StudentTestDetail studentTestDetails) {
    return new StudentTestDetailDTO(studentTestDetails.getStudentId(), studentTestDetails.getContestId(),
        studentTestDetails.getCodeLanguage(), studentTestDetails.getQuestionDetails());
  }
}
