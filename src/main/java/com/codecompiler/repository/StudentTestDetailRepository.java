package com.codecompiler.repository;

import com.codecompiler.entity.Student;
import com.codecompiler.entity.StudentTestDetail;
import com.codecompiler.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface StudentTestDetailRepository extends MongoRepository<StudentTestDetail,String> {
  public  StudentTestDetail findByStudentId(String studentId);
  List<StudentTestDetail> findByContestId(String contestId);
}
