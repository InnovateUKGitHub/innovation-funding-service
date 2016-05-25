package com.worth.ifs.application.service;

public interface AssessorFeedbackService {

	void submitAssessorFeedback(Long competitionId);

	boolean feedbackUploaded(Long competitionId);
}
