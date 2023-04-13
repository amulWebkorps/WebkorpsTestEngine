package com.codecompiler.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Setter
@Getter
@ToString
@Data
public class MCQStatusDTO {
	private String mcqId;
	private boolean mcqstatus;
}
