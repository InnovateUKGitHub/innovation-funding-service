package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

/**
 * Service for retrieving statistics about assessor assessments
 */
public interface AssessorCountSummaryService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ", description = "Comp Admins can see all Assessor Summary counts across the whole system", securedType = AssessorCountSummaryResource.class)
    ServiceResult<AssessorCountSummaryPageResource> getAssessorCountSummariesByCompetitionId(long competitionId, Optional<Long> innovationSector, Optional<BusinessType> businessType, int pageIndex, int pageSize);
}

