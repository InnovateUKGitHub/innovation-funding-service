package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Service interface defining security rules for creating full or partial copies of competition templates.
 */
public interface CompetitionSetupTemplateService {
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    @SecuredBySpring(value = "INITIALIZE_COMPETITION_BY_TEMPLATE",
            description = "The Competition Admin user and Project Finance users can create competition invites for existing users")
    ServiceResult<Competition> initializeCompetitionByCompetitionTemplate(Long competitionId, Long competitionTypeId);
}
