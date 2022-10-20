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
		allMCQ = mcqRepository.saveAll(allMCQ);
		if (contest != null) {
			List<String> mcqInContest = this.saveMCQContest(contest, allMCQ);
			return mcqRepository.findByMcqIdIn(mcqInContest);
		} else {
			return mcqRepository.findByMcqStatus(true);
		}
	}

	public List<String> saveMCQContest(Contest contest, List<MCQ> allMCQ) {
		ArrayList<MCQStatusDTO> mcqStatusList = new ArrayList<MCQStatusDTO>();
		allMCQ.forEach(latestUploadedMCQ -> {
			MCQStatusDTO mcqStatus = new MCQStatusDTO();
			mcqStatus.setMcqId(latestUploadedMCQ.getMcqId());
			mcqStatus.setMcqstatus(true);
			mcqStatusList.addAll(contest.getMcqStatus());
			mcqStatusList.add(mcqStatus);
		});
		contest.setMcqStatus(mcqStatusList);
		contestRepository.save(contest);
		return contest.getMcqStatus().stream().map(MCQStatusDTO::getMcqId).collect(Collectors.toList());
	}

}
