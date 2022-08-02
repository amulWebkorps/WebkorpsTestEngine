package com.codecompiler.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.codecompiler.entity.Contest;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.QuestionStatus;
import com.codecompiler.entity.SampleTestCase;
import com.codecompiler.entity.Student;
import com.codecompiler.service.CommonService;
import com.codecompiler.service.ContestService;
import com.codecompiler.service.StudentService;

@Controller
public class HomeController {
	
	@Autowired
	private CommonService commonService;	
	@Autowired
	private ContestService contestService;
	@Autowired
	StudentService studentService;
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

	@RequestMapping("/uploadparticipatorandemail")
	public String uploadParticipatorAndEmail(Model model){		
		return "uploadParticipatorAndEmail.html";		
	}
	
	
	@RequestMapping("/home")
	public String startContest(Model model) {
		return "startContest";		
	}
	
	@RequestMapping("/viewparticipators") 
	private String viewParticipators(Model model) {
		//Contest contest = contestService.findByContestId(contestId);		
		List<Student> studentTemp = new ArrayList<>();		
		studentTemp = studentService.findByContestId(contestId);
		System.out.println("student size:"+studentTemp.size());		
		model.addAttribute("contestId", contestId);
		model.addAttribute("student", studentTemp);
	    return "participators";
	}
	
	
	@RequestMapping("/participatordetail")
	public String participatorDetailInIDE(Model model) {	
       return "IDECompiler";		
	}

	@RequestMapping("/findcontest") 
	private ResponseEntity<String> findContest(@RequestBody Contest contest,Model model) {
		//System.out.println("ContestId : "+contest.getContestId());		
		contestId = contest.getContestId();		
		return ResponseEntity.ok("valueSet");
	}
	
	@RequestMapping("/questionlistforspecificcontest")
	public String questionsListOfContest(Model model) {
		List<Question> allQuestionsOfSpecificContestLevel = new ArrayList<>();
		Contest contest = new Contest();
		contest = contestService.findByContestId(contestId);
		contestLevel = contest.getContestLevel();
		allQuestionsOfSpecificContestLevel = commonService.findQuestionByContestLevel(contest.getContestLevel());
		System.out.println("allQuestionsOfSpecificContestLevel : " + allQuestionsOfSpecificContestLevel);
		List<Question> Qlist = new ArrayList<>();
		ArrayList<QuestionStatus> qStatusOfContest = contest.getQuestionStatus();
		System.out.println("qStatusOfContest " + qStatusOfContest);

		for (QuestionStatus qsid : qStatusOfContest) {
			if (qsid.getStatus()) {
				Qlist.add(commonService.getQuestionFromDataBase(qsid.getQuestionId()).get(0));
			}
		}
		for (Question q : Qlist) {
			System.out.println("question list : " + q);
		}
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
		model.addAttribute("totalQuestions", contestQuestions);

		model.addAttribute("questions", Qlist);
		model.addAttribute("contestId", contestId);
		model.addAttribute("contestLevel", contest.getContestLevel());

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
        QuestionStatus queStatus = new QuestionStatus();
        queStatus.setQuestionId(savedQuestion.getQuestionId());
        queStatus.setStatus(true);
       // contest.getQuestionIds().add(savedQuestion.getQuestionId());
        contest.getQuestionStatus().add(queStatus);
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
	
	
	
	@RequestMapping("/deletequestion") // cid, qid, level
	private ResponseEntity<String> deleteQuestion(@RequestBody ArrayList<String> ids) {
		System.out.println("cid.........."+ids.get(0));
		System.out.println("qid.........."+ids.get(1));
		System.out.println("level.........."+ids.get(2));
		Contest contest = new Contest();		                  
	    contest = contestService.findByContestId(ids.get(0));
	    System.out.println("contest * => 1 "+ contest);
	   // contest.getQuestionIds().remove(ids.get(1));
	    int index=0;
	    for(QuestionStatus qs : contest.getQuestionStatus()) {
	    	System.out.println("qs"+qs);
	    	if(qs.getQuestionId().equals(ids.get(1))) {
	    		System.out.println("index "+index);	    		
	    		contest.getQuestionStatus().get(index).setStatus(false);
	    	}
	    	index++;
	    }
	    System.out.println("contest * => 2 "+ contest);
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
	
	@RequestMapping("/filteravailablequebasedonlevel") 
	private ResponseEntity<ArrayList<Question>> filteravailablequebasedonlevel(@RequestBody String level, Model model) {
		System.out.println("New L : "+level);
		contestLevelForPage = level.subSequence(1, level.length()-1).toString();
		ArrayList<Question> q = new ArrayList<>();
		q = demo(level.subSequence(1, level.length()-1).toString());
		System.out.println("q"+q);
		return ResponseEntity.ok(q);		
	}
	
	private ArrayList<Question> demo(String contestLevelForPage) {
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
		return contestQuestions;
	}
	
	@RequestMapping("/addselectedavailablequetocontest")
	private ResponseEntity<ArrayList<Question>> addSelectedAvailableQueToContest(@RequestBody ArrayList<String> ids) {		
		String contestId = ids.get(0);
		ids.remove(0);
	
		Contest con = new Contest();
		con = contestService.findByContestId(contestId);
		ArrayList<Question> qdetails = new ArrayList<>();
		for(String id : ids) {			
		ArrayList<Question> question = (ArrayList<Question>) commonService.getQuestionFromDataBase(id);		
	    qdetails.add(question.get(0));	   	
		}
	    
	    ArrayList<QuestionStatus> idWithstatus = con.getQuestionStatus();
	    
	    //************************************************* 
	    boolean flag = false;
	    for(String idToChangeStatus : ids) {	
	    	int index=0;
	     for(QuestionStatus qs : idWithstatus) {
	    	if(idToChangeStatus.equals(qs.getQuestionId())) {
	    		if(qs.getStatus() == false) {
	    			
	    			con.getQuestionStatus().get(index).setStatus(true);
	    			flag=true;
	    		}else if(qs.getStatus() == true) {
	    			flag=true;
	    		}
	    	}
	    	index++;
	    }
	     if(flag == false) {
	    	QuestionStatus qsTemp = new QuestionStatus();
	    	qsTemp.setQuestionId(idToChangeStatus);
	 	    qsTemp.setStatus(true);
	 	    con.getQuestionStatus().add(qsTemp); 
	     }else {
	    	 flag = false;
	     }
	    }
	    
        contestService.saveContest(con);
		return ResponseEntity.ok(qdetails);
	}


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

	@PostMapping("startContestPage")
	public ModelAndView contestPage(@RequestParam(value="contestId", required=false) String contestId, @RequestParam(value="studentId", required=false) int studentId) {
		System.out.println("contestId....."+contestId);
		Contest contest =  contestService.findByContestId(contestId);
		ArrayList <QuestionStatus> qStatusList = new ArrayList<>();
		qStatusList = contest.getQuestionStatus();		
		ArrayList <String> qListStatusTrue = new ArrayList<>();
		for(QuestionStatus questionStatus : qStatusList) {
			if(questionStatus.getStatus()) {
				qListStatusTrue.add(questionStatus.getQuestionId());
			}
		}
		List<Question> contestQuestions = commonService.getAllQuestion(qListStatusTrue);
		ModelAndView  mv = new ModelAndView("IDECompiler","contestQuestions",contestQuestions.get(0));
		mv.addObject("contestId", contestId);
		mv.addObject("studentId", studentId);
		return mv;		
	}

	@RequestMapping("studentsubmitedcontest")
    public ModelAndView studentSubmitedContest(@RequestBody Student student, Model model) {
		
		
		
		
		
	System.out.println("contestId....."+contestId);
	Contest contest =  contestService.findByContestId(contestId);
	ArrayList <QuestionStatus> qStatusList = new ArrayList<>();
	qStatusList = contest.getQuestionStatus();		
	ArrayList <String> qListStatusTrue = new ArrayList<>();
	for(QuestionStatus questionStatus : qStatusList) {
		if(questionStatus.getStatus()) {
			qListStatusTrue.add(questionStatus.getQuestionId());
		}
	}	
	List<Question> contestQuestions = commonService.getAllQuestion(qListStatusTrue);
	ModelAndView  mv = new ModelAndView("IDECompiler","contestQuestions",contestQuestions.get(1));
	return mv;		
}

}
	

