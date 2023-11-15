package com.codecompiler.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.dto.MCQStatusDTO;
import com.codecompiler.dto.MyCellDTO;
import com.codecompiler.entity.Contest;
import com.codecompiler.entity.MCQ;
import com.codecompiler.exception.InsufficientDataException;
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

 

	@Override
	public List<MCQ> saveFileForBulkMCQ(MultipartFile file, String contestId) throws IOException {
		if (!ExcelConvertorService.checkExcelFormat(file)) {
			throw new UnSupportedFormatException("saveFileForBulkMCQ::Given file format is not supported");
		}

		Map<Integer, List<MyCellDTO>> data = excelPOIHelper.readExcel(file.getInputStream(),
				file.getOriginalFilename());

		List<MCQ> allMCQ = excelConvertorService.convertExcelToListOfMCQ(data);

		if (contestId.equals("null")) {
			return saveMcq(allMCQ);
		}

		Contest contest = contestRepository.findByContestId(contestId);

		if (allMCQ.isEmpty()) {
			throw new RecordNotFoundException("saveFileForBulkMCQ:: Data isn't present in the file");
		}

		allMCQ = this.saveAllMcq(allMCQ, contest);

		List<String> mcqInContest = this.saveMCQContest(contest, allMCQ);
		return mcqRepository.findByMcqIdIn(mcqInContest);
	}

	@Override
	public List<MCQ> getAllMCQs(Map<String, List<String>> mcqIdList) {
		String contestId = mcqIdList.getOrDefault("contestId", Collections.emptyList()).stream().findFirst()
				.orElseThrow(() -> new NullPointerException("Method argument list does not contain a valid MCQ id"));

		if (mcqIdList.size() < 2) {
			throw new InsufficientDataException("Method argument is null or it has insufficient data");
		}

		List<MCQ> mcqDetails = mcqRepository.findByMcqIdIn(mcqIdList.getOrDefault("mcqIds", Collections.emptyList()));

		if (mcqDetails.isEmpty()) {
			throw new RecordNotFoundException("getAllMCQ:: Questions not found");
		}

		this.saveContests(contestId, mcqIdList);
		return mcqDetails;
	}

	public Contest saveContests(String contestId, Map<String, List<String>> mcqIdList) {
		Contest contest = Optional.ofNullable(contestService.findByContestId(contestId)).orElseThrow(
				() -> new RecordNotFoundException("saveContests:: Content does not found for contestId: " + contestId));

		List<MCQStatusDTO> mcqStatus = Optional.ofNullable(contest.getMcqStatus())
				.orElseThrow(() -> new RecordNotFoundException("saveContests:: McqStatus does not found"));

		mcqIdList.get("mcqIds").forEach(idToChangeStatus -> {
			mcqStatus.stream().filter(qs -> idToChangeStatus.equals(qs.getMcqId())).findFirst().ifPresentOrElse(qs -> {
				if (!qs.isMcqstatus()) {
					qs.setMcqstatus(true);
				}
			}, () -> {
				MCQStatusDTO mcqTemp = new MCQStatusDTO();
				mcqTemp.setMcqId(idToChangeStatus);
				mcqTemp.setMcqstatus(true);
				mcqStatus.add(mcqTemp);
			});
		});

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
			throw new InsufficientDataException("Method argument is null or it has insufficient data");
		}

		String contestId = contestAndMcqQuestionId.get(0);
		String mcqQuestionId = contestAndMcqQuestionId.get(1);

		Contest contest = contestService.findByContestId(contestId);

		contest.getMcqStatus().stream().filter(qs -> qs.getMcqId().equals(mcqQuestionId)).findFirst()
				.ifPresent(qs -> qs.setMcqstatus(false));

		contestService.saveContest(contest);
	}

	@Override
	public MCQ findByMcqId(String mcqId) {
		return mcqRepository.findByMcqId(mcqId);
	}

	@Override
	public List<MCQ> getAllMcq() {
		return mcqRepository.findAllMCQ(true);
	}

	@Override
	public MCQ deleteMcq(String mcqId) {
		return Optional.ofNullable(mcqRepository.findByMcqId(mcqId)).map(mcq -> {
			mcq.setMcqStatus(false);
			return mcqRepository.save(mcq);
		}).orElse(null);
	}
	public List<MCQ> saveAllMcq(List<MCQ> addMcq, Contest contest) {
	    List<String> contestPresentMcqsId = contest.getMcqStatus().stream()
	            .map(MCQStatusDTO::getMcqId)
	            .collect(Collectors.toList());

	    List<MCQ> contestPresentMcqs = mcqRepository.findByMcqIdIn(contestPresentMcqsId);
	    List<MCQ> allMcq = mcqRepository.findAll();

	    List<MCQ> answer = new ArrayList<>(addMcq.size());

	    if (contestPresentMcqs.isEmpty() || !allMcq.isEmpty()) {
	        Map<String, MCQ> existingMcqMap = contestPresentMcqs.stream()
	                .collect(Collectors.toMap(
	                        mcq -> mcq.getMcqQuestion().toLowerCase().trim(),
	                        mcq -> mcq
	                ));

	        for (MCQ mcq : addMcq) {
	            String question = mcq.getMcqQuestion().toLowerCase().trim();
	            if (existingMcqMap.containsKey(question)) {
	                MCQ existingMcq = existingMcqMap.get(question);
	                if (!existingMcq.isMcqStatus()) {
	                    existingMcq.setMcqStatus(true);
	                    answer.add(existingMcq);
	                }
	            } else {
	                answer.add(mcq);
	            }
	        }

	        if (!answer.isEmpty()) {
	            mcqRepository.saveAll(answer);
	        }
	    }

	    return answer;
	}


	public List<MCQ> saveMcq(List<MCQ> allMcq) {
		List<MCQ> oldMcq = mcqRepository.findAll();

		allMcq.forEach(newMcq -> {
			String newQuestion = newMcq.getMcqQuestion().toLowerCase().trim();
			oldMcq.stream().filter(existingMcq -> newQuestion.equals(existingMcq.getMcqQuestion().toLowerCase().trim()))
					.findFirst().ifPresent(existingMcq -> {
						if (!existingMcq.isMcqStatus()) {
							existingMcq.setMcqStatus(true);
							mcqRepository.save(existingMcq);
						}
					});
		});

		List<MCQ> newMcq = allMcq.stream()
				.filter(mcq -> oldMcq.stream().noneMatch(
						existingMcq -> mcq.getMcqQuestion().equalsIgnoreCase(existingMcq.getMcqQuestion().trim())))
				.collect(Collectors.toList());

		if (!newMcq.isEmpty()) {
			mcqRepository.saveAll(newMcq);
		}

		return newMcq;
	}

}
