package com.worth.ifs.application.service;

/**
 * This service is responsible for managing assessor feedback.
 */
public interface AssessorFeedbackService {

	void submitAssessorFeedback(Long competitionId);

	boolean feedbackUploaded(Long competitionId);
}
