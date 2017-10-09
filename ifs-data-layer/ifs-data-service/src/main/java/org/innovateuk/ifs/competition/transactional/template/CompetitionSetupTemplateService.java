package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;

/**
 * Service interface defining security rules for creating full or partial copies of competition templates.
 */
public interface CompetitionSetupTemplateService {
    @NotSecured(value = "Service creates template copies for other services. The calling services should be secured.")
    ServiceResult<Competition> createCompetitionByCompetitionTemplate(Competition competition, Competition template);
}
