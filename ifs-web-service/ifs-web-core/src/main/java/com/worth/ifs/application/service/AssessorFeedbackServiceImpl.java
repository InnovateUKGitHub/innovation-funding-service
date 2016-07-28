package com.worth.ifs.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssessorFeedbackServiceImpl implements AssessorFeedbackService {

	@Autowired
	private AssessorFeedbackRestService assessorFeedbackRestService;
	
	@Override
	public void submitAssessorFeedback(Long competitionId) {
		assessorFeedbackRestService.submitAssessorFeedback(competitionId).getSuccessObjectOrThrowException();
	}

	@Override
	public boolean feedbackUploaded(Long competitionId) {
		return assessorFeedbackRestService.feedbackUploaded(competitionId).getSuccessObjectOrThrowException();
	}

}
