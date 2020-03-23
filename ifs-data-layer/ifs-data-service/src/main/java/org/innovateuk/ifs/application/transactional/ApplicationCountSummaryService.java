package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource.Sort;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

/**
 * Service for retrieving statistics about applications
 */
public interface ApplicationCountSummaryService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ", description = "Comp Admins and project finance can see all Application Summary counts across the whole system", securedType = ApplicationCountSummaryResource.class)
    ServiceResult<ApplicationCountSummaryPageResource> getApplicationCountSummariesByCompetitionId(long competitionId,
                                                                                                   int pageIndex,
                                                                                                   int pageSize,
                                                                                                   Optional<String> filter);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ", description = "Comp Admins and project finance can see all Application Summary counts accros the whole system", securedType = ApplicationCountSummaryResource.class)
    ServiceResult<ApplicationCountSummaryPageResource> getApplicationCountSummariesByCompetitionIdAndAssessorId(
                                                                                    long competitionId,
                                                                                    long assessorId,
                                                                                    int page,
                                                                                    int size,
                                                                                    Sort sort,
                                                                                    String filter);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ", description = "Comp Admins and project finance can see all Application Summary counts accros the whole system", securedType = ApplicationCountSummaryResource.class)
    ServiceResult<List<Long>> getApplicationIdsByCompetitionIdAndAssessorId(long competitionId, long assessorId, String filter);
}

