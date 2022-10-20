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
	private MCQRepository mcqRepository;

	@Autowired
	private ContestService contestService;

	@Autowired
	private ContestRepository contestRepository;

	@Autowired
	private ExcelConvertorService excelConvertorService;

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

	@Resource(name = "excelPOIHelper")
	private ExcelPOIHelper excelPOIHelper;

	public List<MCQ> saveFileForBulkMCQQuestion(MultipartFile file, String contestId) throws IOException {
		if (!ExcelConvertorService.checkExcelFormat(file)) {
			throw new UnSupportedFormatException("saveFileForBulkQuestion::Given file format is not supported");
		}
		Contest contest = contestRepository.findByContestId(contestId);
		List<MCQ> allTrueQuestions = null;
		Map<Integer, List<MyCellDTO>> data = excelPOIHelper.readExcel(file.getInputStream(),
				file.getOriginalFilename());
		System.out.println("Data= " + data);
		allTrueQuestions = excelConvertorService.convertExcelToListOfMCQQuestions(data);
		if (allTrueQuestions.isEmpty() || allTrueQuestions == null) {
			throw new RecordNotFoundException("saveFileForBulkQuestion:: Data isn't present in the file");
		}
		allTrueQuestions = mcqRepository.saveAll(allTrueQuestions);
		if (contest != null) {
			List<String> mcqQuestionsInContest = this.saveMCQContest(contest, allTrueQuestions);

			return mcqRepository.findByMcqIdIn(mcqQuestionsInContest);
		} else {
			return mcqRepository.findByMcqStatus(true);
		}
	}

	public List<String> saveMCQContest(Contest contest, List<MCQ> allTrueQuestions) {
		ArrayList<MCQStatusDTO> mcqStatusList = new ArrayList<MCQStatusDTO>();
		allTrueQuestions.forEach(latestUploadedQuestions -> {
			MCQStatusDTO mcqStatus = new MCQStatusDTO();
			mcqStatus.setMcqId(latestUploadedQuestions.getMcqId());
			mcqStatus.setMcqstatus(true);
			mcqStatusList.addAll(contest.getMcqStatus());
			mcqStatusList.add(mcqStatus);
		});
		contest.setMcqStatus(mcqStatusList);
		contestRepository.save(contest);
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
}
