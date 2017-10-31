package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Service interface defining security rules for creating full or partial copies of competition templates.
 */
public interface CompetitionSetupTemplateService {
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<Competition> initializeCompetitionByCompetitionTemplate(Long competitionId, Long competitionTypeId);

    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<Question> addDefaultAssessedQuestionToCompetition(Competition competition);

    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<Void> deleteAssessedQuestionInCompetition(Long questionId);
}
