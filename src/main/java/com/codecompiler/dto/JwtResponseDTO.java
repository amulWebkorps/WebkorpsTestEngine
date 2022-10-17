package com.codecompiler.dto;

import com.codecompiler.entity.Student;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JwtResponseDTO {
    String token;
    Student student;
}
