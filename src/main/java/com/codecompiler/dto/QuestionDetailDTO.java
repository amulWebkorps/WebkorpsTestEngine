package com.codecompiler.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionDetailDTO {

  private String questionId;
  private String code;
  private List<Boolean> testCasesResult;
}
