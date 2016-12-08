package com.worth.ifs.competitionsetup.service;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupQuestionResource;
import com.worth.ifs.competitionsetup.form.LandingPageForm;
import org.springframework.validation.BindingResult;

/**
 * Service for logic around handling the questions handled in the applicationForm section of competition setup.
 */
public interface CompetitionSetupQuestionService {

	ServiceResult<CompetitionSetupQuestionResource> getQuestion(Long questionId);

	ServiceResult<Void> updateQuestion(CompetitionSetupQuestionResource question);

    ServiceResult<Void> validateApplicationQuestions(CompetitionResource competitionResource, LandingPageForm form, BindingResult result);
}
