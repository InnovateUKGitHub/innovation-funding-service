package org.innovateuk.ifs.competitionsetup.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competitionsetup.form.LandingPageForm;
import org.springframework.validation.BindingResult;

/**
 * Service for logic around handling the questions handled in the applicationForm section of competition setup.
 */
public interface CompetitionSetupQuestionService {

	ServiceResult<CompetitionSetupQuestionResource> getQuestion(Long questionId);

	ServiceResult<Void> updateQuestion(CompetitionSetupQuestionResource question);

	ServiceResult<CompetitionSetupQuestionResource> createDefaultQuestion(Long competitionId);

	ServiceResult<Void> deleteQuestion(Long questionId);

    ServiceResult<Void> validateApplicationQuestions(CompetitionResource competitionResource, LandingPageForm form, BindingResult result);
}
