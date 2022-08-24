package com.codecompiler.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.codecompiler.dao.QuestionRepository;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.SampleTestCase;
import com.codecompiler.entity.TestCases;

@Service
public class CommonService {

	@Autowired private QuestionRepository questionRepository;
	
	@Autowired private MongoTemplate mongoTemplate;
	//@Autowired private BinaryDataController binaryDataController;
	public Question saveQuestion(Question question) {		
		return questionRepository.save(question);				
	}
	
	public Question saveUpdatedQuestion(Question question) {
		System.out.println("Commaon service : "+question);
		Query query=new Query();
		query.addCriteria(Criteria.where("_id").is(question.getQuestionId()));
		Question q = mongoTemplate.findOne(query, Question.class);
		mongoTemplate.save(question);
		System.out.println("**** >"+q);
		return question;
	}
	
	public int numberOfQuestionsCount() {
		return (int) questionRepository.count();
		
	}
		
	public List<Question> getQuestionFromDataBase(String questionId) {
		List<Question> question = null;
		//question = questionRepository.findByQuestionId(questionId);
		return question;
	}
	
	

	public List<Question> getAllQuestionFromDataBase() {
		List<Question> question = questionRepository.findAll();		
		return question;
	}
	
	public List<TestCases> getTestCase(String questionId){
		List<Question>  question= getQuestionFromDataBase(questionId);	
		List<TestCases> testCasesCollection = new ArrayList<>();
		for (Question q : question) {
			testCasesCollection.addAll(q.getTestcases());
			testCasesCollection = q.getTestcases();
			System.out.println(testCasesCollection);
		}
		return testCasesCollection;
	}
	
	public List<SampleTestCase> getSampleTestCase(String questionId){
		List<Question>  question= getQuestionFromDataBase(questionId);	
		
		List<SampleTestCase> sampleTestCaseCollection = null;
		for (Question q : question) {
			sampleTestCaseCollection = q.getSampleTestCase();
			System.out.println(sampleTestCaseCollection);
		}
		return sampleTestCaseCollection;
	}
		
	public ArrayList<Question>  findQuestionByContestLevel(String contestLevel){
	//ArrayList<Question> question = questionRepository.findByContestLevel(contestLevel);       
	return null;
    }
	
	
	
	public String deleteQuestion(String questionId){
		questionId=questionId.subSequence(1, questionId.length()-1).toString();
		questionRepository.deleteByQuestionId(questionId);
		return "Deleting....";
	}
	
	public List<Question> getAllQuestion(List<String> QuestionId) {
		List<Question> question = questionRepository.findByQuestionIdIn(QuestionId);
		return question;
	}
}

