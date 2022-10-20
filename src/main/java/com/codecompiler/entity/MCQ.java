package com.codecompiler.entity;

import java.io.Serializable;
import java.util.List;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import nonapi.io.github.classgraph.json.Id;

@Document(collection = "MCQQuestion")
@EntityScan
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class MCQ implements Serializable {

	@Id
	private String mcqId;
	private String mcqQuestion;
	private String option1;
	private String option2;
	private String option3;
	private String option4;
	private List<String> correctOption;
	private boolean mcqStatus;
	private String createdDate;
}
