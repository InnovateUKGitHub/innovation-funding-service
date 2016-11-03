package com.worth.ifs.competitionsetup.service;

import com.worth.ifs.competitionsetup.viewmodel.application.QuestionViewModel;

/**
 * Service for logic around handling the questions handled in the applicationForm section of competition setup.
 */
public interface CompetitionSetupQuestionService {

	QuestionViewModel getQuestion(Long questionId);

	void updateQuestion(QuestionViewModel question);
}
