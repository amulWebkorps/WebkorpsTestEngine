package com.codecompiler.entity;

import java.io.Serializable;
import java.util.List;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import nonapi.io.github.classgraph.json.Id;

@Document(collection = "MCQQuestion")
@EntityScan
@NoArgsConstructor
@ToString
@Data
public class MCQ implements Serializable {
	@Id
	private String mcqQuestionId;
	private String mcqQuestion;
	private String option1=null;
	private String option2=null;
	private String option3=null;
	private String option4=null;
	private List<String> correctOption;
	private boolean mcqQuestionStatus;
	private String createdDate;
}
