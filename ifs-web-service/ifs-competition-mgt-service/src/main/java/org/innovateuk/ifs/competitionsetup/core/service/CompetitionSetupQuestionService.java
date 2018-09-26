package org.innovateuk.ifs.competitionsetup.core.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competitionsetup.application.form.LandingPageForm;
import org.springframework.validation.BindingResult;

/**
 * Service for logic around handling the questions handled in the applicationForm section of competition setup.
 */
public interface CompetitionSetupQuestionService {

    ServiceResult<Void> validateApplicationQuestions(CompetitionResource competitionResource,
                                                     LandingPageForm form,
                                                     BindingResult result);
}
