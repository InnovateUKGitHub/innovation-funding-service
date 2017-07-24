package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.assessment.resource.AssessorCompetitionSummaryResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional service for retrieving {@link AssessorCompetitionSummaryResource}s.
 */
public interface AssessorCompetitionSummaryService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(
            value = "READ",
            description = "Comp Admins can see all competition assessors and summaries of their assessments",
            securedType = AssessorCompetitionSummaryResource.class
    )
    ServiceResult<AssessorCompetitionSummaryResource> getAssessorSummary(long assessorId, long competitionId);
}
