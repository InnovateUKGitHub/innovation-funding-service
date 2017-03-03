package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorPageResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service for retrieving {@link ApplicationAssessmentSummaryResource}'s.
 */
public interface ApplicationAssessmentSummaryService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ", description = "Comp Admins can see all Competition Assessors and statistics about them", securedType = ApplicationAssessorResource.class)
    ServiceResult<ApplicationAssessorPageResource> getAvailableAssessors(long applicationId, int pageIndex, int pageSize, Long filterInnovationArea);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ", description = "Comp Admins can see all Competition Assessors and statistics about them", securedType = ApplicationAssessorResource.class)
    ServiceResult<List<ApplicationAssessorResource>> getAssignedAssessors(long applicationId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ", description = "Comp Admins can see all Application assessment summaries across the whole system", securedType = ApplicationAssessmentSummaryResource.class)
    ServiceResult<ApplicationAssessmentSummaryResource> getApplicationAssessmentSummary(Long applicationId);

}