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

			return mcqRepository.findByMcqQuestionIdIn(mcqQuestionsInContest);
		} else {
			return mcqRepository.findByMcqQuestionStatus(true);
		}
	}

	public List<String> saveMCQContest(Contest contest, List<MCQ> allTrueQuestions) {
		ArrayList<MCQStatusDTO> mcqStatusList = new ArrayList<MCQStatusDTO>();
		allTrueQuestions.forEach(latestUploadedQuestions -> {
			MCQStatusDTO mcqStatus = new MCQStatusDTO();
			mcqStatus.setMcqId(latestUploadedQuestions.getMcqQuestionId());
			mcqStatus.setMcqstatus(true);
			mcqStatusList.addAll(contest.getMcqQuestionStatus());
			mcqStatusList.add(mcqStatus);
		});
		System.out.println("mca=  " + contest);
		contest.setMcqQuestionStatus(mcqStatusList);

		contestRepository.save(contest);
		return contest.getMcqQuestionStatus().stream().map(MCQStatusDTO::getMcqId).collect(Collectors.toList());
	}

}
