package com.codecompiler.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.codecompiler.entity.Contest;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.SampleTestCase;
import com.codecompiler.entity.TestCases;
import com.codecompiler.service.CommonService;
import com.codecompiler.service.ContestService;

@Controller
public class HomeController {
	
	@Autowired
	private CommonService commonService;	
	@Autowired
	private ContestService contestService;
	
	public String contestId="";
	public String contestLevel="";	
	public String contestLevelForPage="";
	SampleTestCase sampleTestCase = new SampleTestCase();	
	int questionID;	
	Question q = new Question();	
	List<Question> allQuestionsSpecificContestLevel = new ArrayList<>();
	
	@RequestMapping("/adminhome")
	public String adminHome(Model model){
		List<Contest> con = new ArrayList<>();
		con = contestService.getAllContest();
		model.addAttribute("contest",con);		
		return "adminHome";		
	}
	
	@RequestMapping("/home")
	public String home(Model model) {
//		List<Question> question = commonService.getQuestionFromDataBase("40");
//		Question localQuestion = new Question();
//		List <SampleTestCase> sampleTestCase = new ArrayList<>();
//		List<TestCases> testCasesList = new ArrayList<>();
//		for (Question q : question) {
//			sampleTestCase = q.getSampleTestCase();
//			testCasesList = q.getTestcases();
//			localQuestion = q;
//		}
//		SampleTestCase localSampleTestCase = new SampleTestCase();
//		
//		 for(SampleTestCase s : sampleTestCase) localSampleTestCase = s; 
//		 
//        System.out.println("Que. 1 => "+localQuestion);
//		System.out.println("Que. 2 => "+localSampleTestCase);
//
//		model.addAttribute("question",localQuestion);
//		model.addAttribute("stc",localSampleTestCase);
//		
		return "IDECompiler";		
	}
//
//	@RequestMapping("/contest") 
//	private String addContest() {
//	    return "contest";
//	}
//		
//	@RequestMapping("/studenttestscreen") 
//	private String studentTestScreen() {
//	    return "studentTestScreen";
//	}
//
//	@RequestMapping("/contestlist") 
//	private String contestList(Model model) {
//		List<Contest> contestList = contestService.getAllContest();
//		model.addAttribute("contestList", contestList);		
//		return "listOfContest";
//	}
	
	@RequestMapping("/findcontest") 
	private ResponseEntity<String> findContest(@RequestBody Contest contest,Model model) {
		System.out.println("ContestId : "+contest.getContestId());		
		System.out.println("ContestLevel : "+contest.getContestLevel());
		contestId = contest.getContestId();
		contestLevel = contest.getContestLevel();	
		return ResponseEntity.ok("valueSet");
	}
	
	//question related to contest id and contest level return questionListAndAddNewQuestion
	@RequestMapping("/questionlistforspecificcontest")
	public String questionsListOfContest(Model model) {
		List<Question> allQuestionsOfSpecificContestLevel = new ArrayList<>();
		Contest contest = new Contest();

		//contest = contestService.getContestBasedOnContestIdAndLevel(contestId, contestLevel);
		
		contest = contestService.findByContestId(contestId);

		allQuestionsOfSpecificContestLevel = commonService.findQuestionByContestLevel(contestLevel);

		System.out.println("allQuestionsOfSpecificContestLevel : " + allQuestionsOfSpecificContestLevel);

		List<Question> Qlist = new ArrayList<>();
		
		ArrayList<String> qidListOfContest = contest.getQuestionIds();
	
		 System.out.println("qidListOfContest "+qidListOfContest);
		 
		 for(String qidOfContest : qidListOfContest) {
			 for(Question q : allQuestionsOfSpecificContestLevel) {
				 if(qidOfContest.equals(q.getQuestionId())) {
					 Qlist.add(q);
					 break;
				 }
				}
			}
		
       System.out.println("question list : " + Qlist);

		model.addAttribute("questions", Qlist);
		model.addAttribute("contestId", contestId);
		model.addAttribute("contestLevel", contestLevel);
		
		return "questionListAndAddNewQuestion";
	}
	
	
	@RequestMapping("/returnquestionlistforspecificcontestlevel")
	public String QuestionsList(Model model) {				
		allQuestionsSpecificContestLevel = commonService.findQuestionByContestLevel(contestLevel);
		model.addAttribute("contestId", contestId);
		model.addAttribute("contestLevel", contestLevel);
		model.addAttribute("questions", allQuestionsSpecificContestLevel);
		return "questionListAndAddNewQuestion";
	}
	
		
	@RequestMapping("/addcontest") 
	private ResponseEntity<Contest> addContest(@RequestBody Contest contest,Model model) {
		System.out.println(contest.getContestName());
		System.out.println(contest.getContestDescription());
		System.out.println(contest.getContestLevel());
		Contest contestToSave = new Contest();
		contestToSave.setContestName(contest.getContestName());
		contestToSave.setContestDescription(contest.getContestDescription());		
		contestToSave.setContestLevel(contest.getContestLevel());
		Contest con1 = contestService.saveContest(contestToSave);	
		System.out.println("con : "+con1);
				return ResponseEntity.ok(con1);
	}
	
	@RequestMapping("/deletecontest") 
	private  ResponseEntity<String> deleteContest(@RequestBody String contestId) {
		contestId = contestId.subSequence(1, contestId.length()-1).toString();
		System.out.println("@@@@@@@@"+contestId);
		contestService.deleteContest(contestId);
		return ResponseEntity.ok("ok");
	}
	
	@PostMapping("/savequestion")
	public ResponseEntity<String> saveQuestion(@RequestBody Question question, Model model) throws IOException {		
        System.out.println("savequestion Obj Prev : "+question);
        
        System.out.println("Obj q id : "+question.getQuestionId());
        String [] stringOfCidAndCl=new String[2];
        stringOfCidAndCl = question.getContestLevel().split("@");
        Contest contest = new Contest();		                                  // id, level
	    contest = contestService.getContestBasedOnContestIdAndLevel(stringOfCidAndCl[1], stringOfCidAndCl[0]);
     	question.setContestLevel(stringOfCidAndCl[0]);
			         
        String tempQid = question.getQuestionId();
        if(tempQid == ("")) {
        	System.out.println(" inside if condition ");
        	tempQid = UUID.randomUUID().toString();
        question.setQuestionId(tempQid);
        System.out.println("tempQid -> "+tempQid);
        }
        Question savedQuestion =  commonService.saveUpdatedQuestion(question);
        contest.getQuestionIds().add(savedQuestion.getQuestionId());

       System.out.println("contest after :  "+contest); 
       contestService.saveContest(contest);               
	   return ResponseEntity.ok("");
	}
	
	@PostMapping("/saveupdatedquestion")
	public ResponseEntity<String> saveUpdatedQuestion(@RequestBody Question question, Model model) throws IOException {
        System.out.println("saveupdatedquestion Obj prev : "+question);
        String [] stringOfCidAndCl=new String[2];
        stringOfCidAndCl = question.getContestLevel().split("@");
        question.setContestLevel(stringOfCidAndCl[0]);
        System.out.println("Obj after : "+question);
        commonService.saveUpdatedQuestion(question);
        model.addAttribute("questions",commonService.getAllQuestionFromDataBase());        
		return ResponseEntity.ok("");
	}
	
	
	
	@RequestMapping("/deletequestion") //qid, cid, level
	private ResponseEntity<String> deleteQuestion(@RequestBody ArrayList<String> ids) {
		System.out.println("cid.........."+ids.get(0));
		System.out.println("qid.........."+ids.get(1));
		System.out.println("level.........."+ids.get(2));
		Contest contest = new Contest();		                  
	    contest = contestService.findByContestId(ids.get(0));
	    contest.getQuestionIds().remove(ids.get(1));	    
	    contestService.saveContest(contest);
		System.out.println(contest);
		return ResponseEntity.ok("Done");
	}
	
	@RequestMapping("/getsavedquestion") 
	private ResponseEntity<Question> getSavedQuestion(@RequestBody String questionId) {
		questionId = questionId.subSequence(1, questionId.length()-1).toString();
		System.out.println("1 =>"+questionId);
		List<Question>  question = commonService.getQuestionFromDataBase(questionId);
		Question questionObj = new Question();
		for (Question q : question) {
			questionObj = q;
		}
		System.out.println("2 =>"+questionObj);
	
				return ResponseEntity.ok(questionObj);
	}
	
	
//	@PostMapping("/add-test-cases-api") 
//	public ResponseEntity<Question> saveTestCases(@RequestBody ArrayList<TestCases> testCasesobject,Model model) {
//		//TestCases test = new TestCases();
//		List <TestCases> test = new ArrayList<>();
//		int testCaseId = 1;
//		for(TestCases testCase : testCasesobject) {
//			System.out.println("it is qid from FE : "+testCase.getId());
//			testCase.setId(testCaseId++);			
//			System.out.println(testCase.getInput());
//			System.out.println(testCase.getOutput());
//			test.add(testCase);
//		}
//		q.setTestcases(testCasesobject);
//		System.out.println(q);
//		 Question qsave=commonService.saveQuestion(q);		
//		return ResponseEntity.ok(qsave);
//	}
//	
//	@RequestMapping("/questions")
//	public String redirectToQuestionsList(Model model) {
//		List<Question> allQuestions = commonService.getAllQuestionFromDataBase();
//		model.addAttribute("question", allQuestions);
//		return "questions";
//	}
	
	@RequestMapping("/viewparticipators") 
	private String viewParticipators() {
	    return "participators";
	}
	
	@RequestMapping("/participatordetail") 
	private String participatorDetail() {
	    return "participatorDetail";
	}
	
//	@RequestMapping("/compiler") 
//	private String IDECompiler() {
//	    return "IDECompiler";
//	}
	
	@RequestMapping("/level1questions") 
	private String level1Questions(Model model) {
		ArrayList<Question> contestQuestions = new ArrayList<>();
		contestQuestions = commonService.findQuestionByContestLevel("Level 1");			
		model.addAttribute("questions", contestQuestions);
	    return "level1Questions";
	}
	
	@RequestMapping("/level2questions") 
	private String level2Questions(Model model) {
		ArrayList<Question> contestQuestions = new ArrayList<>();
		contestQuestions = commonService.findQuestionByContestLevel("Level 2");		
		model.addAttribute("questions", contestQuestions);	   
	    return "level2Questions";
	}
	
	@RequestMapping("/allquestions") 
	private String allQuestions(Model model) {
		ArrayList<Question> contestQuestions = new ArrayList<>();
		ArrayList<Question> contestQuestionsTemp = new ArrayList<>();
		contestQuestionsTemp = commonService.findQuestionByContestLevel("Level 1");
		for (Question q : contestQuestionsTemp) {
			contestQuestions.add(q);
		}
		contestQuestionsTemp = commonService.findQuestionByContestLevel("Level 2");
		for (Question q : contestQuestionsTemp) {
			contestQuestions.add(q);
		}
		model.addAttribute("questions", contestQuestions);		
		return "allQuestions";
	}
	
	@RequestMapping("/filterbasedonlevel") 
	private ResponseEntity<String> filterBasedOnLevel(@RequestBody String level, Model model) {
		System.out.println("L : "+level);
		contestLevelForPage = level.subSequence(1, level.length()-1).toString();		
		return ResponseEntity.ok("setData");		
	}
	
	@RequestMapping("/returnpagebasedonfilter") 
	private String returnPageBasedOnLevel(Model model) {
		ArrayList<Question> contestQuestions = new ArrayList<>();
		ArrayList<Question> contestQuestionsTemp = new ArrayList<>();		
		if(contestLevelForPage.equals("All")) {
			contestQuestionsTemp = commonService.findQuestionByContestLevel("Level 1");
			for (Question q : contestQuestionsTemp) {
				contestQuestions.add(q);
			}
			contestQuestionsTemp = commonService.findQuestionByContestLevel("Level 2");
			for (Question q : contestQuestionsTemp) {
				contestQuestions.add(q);
			}			
		}else {
			contestQuestions = commonService.findQuestionByContestLevel(contestLevelForPage);
		}
		model.addAttribute("questions", contestQuestions);		
		return "allquestions";
	}
	
	@RequestMapping("/selectavailablequestion")
	public String selectAvailableQuestion(Model model){	
		ArrayList<Question> contestQuestions = new ArrayList<>();
		ArrayList<Question> contestQuestionsTemp = new ArrayList<>();
		contestQuestionsTemp = commonService.findQuestionByContestLevel("Level 1");
		for (Question q : contestQuestionsTemp) {
			contestQuestions.add(q);
		}
		contestQuestionsTemp = commonService.findQuestionByContestLevel("Level 2");
		for (Question q : contestQuestionsTemp) {
			contestQuestions.add(q);
		}
		model.addAttribute("questions", contestQuestions);	
		return "selectAvailableQuestion";		
	}
	
		
//	@RequestMapping("/idforquestiondetail")
//	public ResponseEntity<Integer> idForQuestionDetail(@RequestBody int quesionId) {
//		System.out.println("idforquestiondetail BE"+quesionId);
//		questionID = quesionId;
//		return ResponseEntity.ok(questionID);
//	}
//	


	
	
	
	
	
	
	
	
	
	
	
	
	
//	IDECompiler.html
//	@RequestMapping("/questiondetail")
//	public String questionDetail(Model model) {
//		List<Question> question = commonService.getQuestionFromDataBase(questionID);
//		Question localQuestion = new Question();
//		List <SampleTestCase> sampleTestCase = new ArrayList<>();
//		List<TestCases> testCasesList = new ArrayList<>();
//		for (Question q : question) {
//			sampleTestCase = q.getSampleTestCase();
//			testCasesList = q.getTestcases();
//			localQuestion = q;
//		}
//		SampleTestCase localSampleTestCase = new SampleTestCase();
//		for(SampleTestCase s : sampleTestCase){
//			localSampleTestCase = s;			
//		}	
//		System.out.println("outside for each loop : "+localQuestion);
//		model.addAttribute("question",localQuestion);
//		model.addAttribute("stc",localSampleTestCase);		
//		model.addAttribute("tc",testCasesList);
//		return "QuestionDetail";
//	}
//	

//	
//	
//	
//	@RequestMapping("/editquestiondetails")
//	public String editQuestions(Model model) {
//		List<Question> question = commonService.getQuestionFromDataBase(questionID);
//		Question localQuestion = new Question();
//		List <SampleTestCase> sampleTestCase = new ArrayList<>();
//		List<TestCases> testCasesList = new ArrayList<>();
//		for (Question q : question) {
//			sampleTestCase = q.getSampleTestCase();
//			testCasesList = q.getTestcases();
//			localQuestion = q;
//		}
//		SampleTestCase localSampleTestCase = new SampleTestCase();
//		for(SampleTestCase s : sampleTestCase){
//			localSampleTestCase = s;			
//		}			
//		model.addAttribute("question",localQuestion);
//		model.addAttribute("stc",localSampleTestCase);		
//		model.addAttribute("tc",testCasesList);
//        return "editQuestion";
//	}
//	
//	@PostMapping("/addupdatedquestion")
//	public ResponseEntity<String> addUpdatedQuestion(@RequestBody Question q) {
//		commonService.saveQuestion(q);		
//		return ResponseEntity.ok("ok");
//	}	
//	
//	
//	
//	
//	
//	@GetMapping("/getquestion/{questionId}")
//	public ResponseEntity<List<TestCases>> getQuestion(@PathVariable int questionId) {
//		List<Question> question = commonService.getQuestionFromDataBase(questionId);
//		//System.out.println(question);
//		List<TestCases> testCasesCollection = null;
//		for (Question q : question) {
//			testCasesCollection = q.getTestcases();
//		}
//		for (TestCases tastCases : testCasesCollection) {			
//           System.out.println(tastCases.getOutput());
//		}
//		return ResponseEntity.ok(testCasesCollection);		
//	}
}


	

