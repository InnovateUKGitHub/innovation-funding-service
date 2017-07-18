package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

/**
 * Service for retrieving statistics about applications
 */
public interface ApplicationCountSummaryService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ", description = "Comp Admins can see all Application Summary counts across the whole system", securedType = ApplicationCountSummaryResource.class)
    ServiceResult<ApplicationCountSummaryPageResource> getApplicationCountSummariesByCompetitionId(Long competitionId, int pageIndex, int pageSize, Optional<String> filter);
}

