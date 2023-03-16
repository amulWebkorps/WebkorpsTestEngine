package com.codecompiler.entity;

import com.codecompiler.dto.QuestionDetailDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@Document(collection = "StudentTestDetail")
@EntityScan
public class StudentTestDetail {

  @org.springframework.data.annotation.Id
  private String id;
  private String studentId;
  private String contestId;
  private String codeLanguage;
  private String code;

  private Set<QuestionDetailDTO> questionDetails;

  private double percentage;

  private LocalDateTime testSubmissionDate;

  public StudentTestDetail(String studentId, String contestId, String codeLanguage,
                           LocalDateTime testSubmissionDate,Set<QuestionDetailDTO> questionDetails) {
    this.studentId = studentId;
    this.questionDetails = questionDetails;
    this.contestId = contestId;
    this.codeLanguage = codeLanguage;
    this.testSubmissionDate = testSubmissionDate;
  }
}
