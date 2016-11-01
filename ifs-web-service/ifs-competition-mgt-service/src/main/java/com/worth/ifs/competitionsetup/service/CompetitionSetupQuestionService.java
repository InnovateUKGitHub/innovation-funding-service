package com.worth.ifs.competitionsetup.service;

import com.worth.ifs.competitionsetup.model.application.Question;

/**
 * Service for logic around handling the questions handled in the applicationForm section of competition setup.
 */
public interface CompetitionSetupQuestionService {

	Question getQuestion(Long questionId);

	void updateQuestion(Question question);
}
