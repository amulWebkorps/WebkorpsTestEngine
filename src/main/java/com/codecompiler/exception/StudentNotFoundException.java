package com.codecompiler.exception;

public class StudentNotFoundException extends RuntimeException{

  public StudentNotFoundException() {
    super();
  }

  public StudentNotFoundException(String invalide_student_details) {
    super(invalide_student_details);
  }
}
