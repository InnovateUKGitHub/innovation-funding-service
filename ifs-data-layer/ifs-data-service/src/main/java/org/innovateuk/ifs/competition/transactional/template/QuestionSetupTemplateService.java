package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;

/**
 * TODO: Add description
 */
public interface QuestionSetupTemplateService {
    @NotSecured(value = "Service creates template copies for other services. The calling services should be secured.")
    ServiceResult<Question> createDefaultForApplicationSection(Competition competition);
}