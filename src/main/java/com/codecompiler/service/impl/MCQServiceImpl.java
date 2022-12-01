package com.codecompiler.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.dto.MCQStatusDTO;
import com.codecompiler.dto.MyCellDTO;
import com.codecompiler.entity.Contest;
import com.codecompiler.entity.MCQ;
import com.codecompiler.exception.RecordNotFoundException;
import com.codecompiler.exception.UnSupportedFormatException;
import com.codecompiler.helper.ExcelPOIHelper;
import com.codecompiler.repository.ContestRepository;
import com.codecompiler.repository.MCQRepository;
import com.codecompiler.service.ContestService;
import com.codecompiler.service.ExcelConvertorService;
import com.codecompiler.service.MCQService;

@Service
public class MCQServiceImpl implements MCQService {

	@Autowired
	private ContestRepository contestRepository;

	@Autowired
	private ExcelConvertorService excelConvertorService;

	@Autowired
	private MCQRepository mcqRepository;

	@Resource(name = "excelPOIHelper")
	private ExcelPOIHelper excelPOIHelper;

	@Autowired
	private ContestService contestService;

	public List<MCQ> saveFileForBulkMCQ(MultipartFile file, String contestId) throws IOException {
		if (!ExcelConvertorService.checkExcelFormat(file)) {
			throw new UnSupportedFormatException("saveFileForBulkMCQ::Given file format is not supported");
		}
		Contest contest = contestRepository.findByContestId(contestId);
		List<MCQ> allMCQ = null;
		Map<Integer, List<MyCellDTO>> data = excelPOIHelper.readExcel(file.getInputStream(),
				file.getOriginalFilename());
		allMCQ = excelConvertorService.convertExcelToListOfMCQ(data);

		if (allMCQ.isEmpty() || allMCQ == null) {
			throw new RecordNotFoundException("saveFileForBulkMCQ:: Data isn't present in the file");
		}

		allMCQ = saveAllMcq(allMCQ, contest);

		List<String> mcqInContest = this.saveMCQContest(contest, allMCQ);
		return mcqRepository.findByMcqIdIn(mcqInContest);
	}

	@Override
	public List<MCQ> getAllMCQs(Map<String, List<String>> mcqIdList) {
		// TODO Auto-generated method stub
		String contestId = mcqIdList.get("contestId").get(0);
		if (contestId.isBlank() || contestId == null) {
			throw new NullPointerException("Method argument list does not contain valid MCQ id");
		}

		if (mcqIdList.size() < 2) {

			throw new NullPointerException("Method argument is null or it have insufficient data");
		}
		List<MCQ> mcqDetails = mcqRepository.findByMcqIdIn(mcqIdList.get("mcqIds"));
		if (mcqDetails == null) {
			throw new RecordNotFoundException("getAllMCQ:: Questions does not found");
		}
		this.saveContests(contestId, mcqIdList);
		return mcqDetails;
	}

	public Contest saveContests(String contestId, Map<String, List<String>> mcqIdList) {
		Contest contest = contestService.findByContestId(contestId);
		if (contest == null) {
			throw new RecordNotFoundException("saveContests:: Content does not found for contestId: " + contestId);
		}
		ArrayList<MCQStatusDTO> mcqStatus = contest.getMcqStatus();
		if (mcqStatus == null) {
			throw new RecordNotFoundException("saveContests:: McqStatus does not found");
		}
		boolean flag = false;
		for (String idToChangeStatus : mcqIdList.get("mcqIds")) {
			int index = 0;
			for (MCQStatusDTO qs : mcqStatus) {

				if (idToChangeStatus.equals(qs.getMcqId())) {
					if (qs.isMcqstatus() == false) {
						contest.getMcqStatus().get(index).setMcqstatus(true);
						flag = true;
					} else if (qs.isMcqstatus()) {
						flag = true;
					}
				}
				index++;
			}
			if (flag == false) {
				MCQStatusDTO mcqTemp = new MCQStatusDTO();
				mcqTemp.setMcqId(idToChangeStatus);
				mcqTemp.setMcqstatus(true);
				contest.getMcqStatus().add(mcqTemp);
			} else {
				flag = false;
			}
		}
		return contestService.saveContest(contest);
	}

	public List<String> saveMCQContest(Contest contest, List<MCQ> allTrueQuestions) {
		ArrayList<MCQStatusDTO> mcqStatusList = new ArrayList<MCQStatusDTO>();
		allTrueQuestions.forEach(latestUploadedQuestions -> {
			MCQStatusDTO mcqStatus = new MCQStatusDTO();
			mcqStatus.setMcqId(latestUploadedQuestions.getMcqId());
			mcqStatus.setMcqstatus(true);
			mcqStatusList.add(mcqStatus);
		});
		mcqStatusList.addAll(contest.getMcqStatus());
		contest.setMcqStatus(mcqStatusList);
		contest = contestRepository.save(contest);
		return contest.getMcqStatus().stream().map(MCQStatusDTO::getMcqId).collect(Collectors.toList());
	}

	/* McqQuestoins Delete */
	@Override
	public void saveMcqQuestionOrContest(ArrayList<String> contestAndMcqQuestionId) {
		if (contestAndMcqQuestionId == null || contestAndMcqQuestionId.size() < 2) {
			throw new NullPointerException("Method argument is null or it have insufficient data");
		}
		Contest contest = new Contest();
		contest = contestService.findByContestId(contestAndMcqQuestionId.get(0));
		int index = 0;
		for (MCQStatusDTO qs : contest.getMcqStatus()) {
			if (qs.getMcqId().equals(contestAndMcqQuestionId.get(1))) {
				contest.getMcqStatus().get(index).setMcqstatus(false);
			}
			index++;
		}
		contestService.saveContest(contest);
	}

	@Override
	public MCQ findByMcqId(String mcqId) {
		return mcqRepository.findByMcqId(mcqId);
	}

	@Override
	public List<MCQ> getAllMcq() {
		List<MCQ> mcqs = mcqRepository.findAllMCQ(true);
		return mcqs;
	}

	@Override
	public MCQ deleteMcq(String mcqId) {
		MCQ mcq = mcqRepository.findByMcqId(mcqId);
		if (mcq != null) {
			mcq.setMcqStatus(false);
			mcq = mcqRepository.save(mcq);
		}
		return mcq;
	}

	public List<MCQ> saveAllMcq(List<MCQ> addMcq, Contest contest) {
		List<MCQ> answer = new ArrayList<MCQ>();

		List<String> contestPresentMcqsId = new ArrayList<String>();
		
		for (int i = 0; i < contest.getMcqStatus().size(); i++)
			contestPresentMcqsId.add(contest.getMcqStatus().get(i).getMcqId());
		
		List<MCQ> contestPresentMcqs = mcqRepository.findByMcqIdIn(contestPresentMcqsId);
		List<MCQ> allMcq = mcqRepository.findAll();
		
		if(contestPresentMcqs.size() == 0 && allMcq.size() > 0) {
			List<MCQ> addMcqInContest=new ArrayList<MCQ>();
			int index=0;
			for(int i=0;i<allMcq.size();i++) {
				boolean status=true;
				for(int j=0;j<addMcq.size();j++) {
					if(allMcq.get(i).getMcqQuestion().toLowerCase().trim().equals(addMcq.get(j).getMcqQuestion().toLowerCase().trim()))
					{
						status=false;
						break;
					}
					index++;
				}
				if(status) {
					answer.add(addMcq.get(index));
					addMcqInContest.add(addMcq.get(index));
				}
				else
					addMcqInContest.add(allMcq.get(i));
				index=0;
			}
			if(answer.size()>0)
				mcqRepository.saveAll(answer);
			return addMcqInContest;
		}
		
		if (allMcq.size() > 0) {
			//here we filter those mcq which are not present in contest already
			for(int i=0;i<addMcq.size();i++) {
				boolean status=true;
				for(int j=0;j<contestPresentMcqs.size();j++) {
					if(contestPresentMcqs.get(j).getMcqQuestion().toLowerCase().trim().equals(addMcq.get(i).getMcqQuestion().toLowerCase().trim()))
					{
						status=false;
						break;
					}
				}
				if(status) {
					answer.add(addMcq.get(i));
					System.out.println(addMcq.get(i));
				}
			}
			
			addMcq=new ArrayList<MCQ>();
			
			//here we filter those mcq which are not present in MCQ
			for(int i=0;i<answer.size();i++) {
				boolean status=true;
				for(int j=0;j<allMcq.size();j++) {
					if(allMcq.get(j).getMcqQuestion().toLowerCase().trim().equals(answer.get(i).getMcqQuestion().toLowerCase().trim()))
					{
						status=false;
						break;
					}
				}
				if(status)
					addMcq.add(answer.get(i));
			}
			if(addMcq.size()>0)
				mcqRepository.saveAll(addMcq);
		} 
		else 
			answer = mcqRepository.saveAll(addMcq);
		
		return answer;
	}
}
