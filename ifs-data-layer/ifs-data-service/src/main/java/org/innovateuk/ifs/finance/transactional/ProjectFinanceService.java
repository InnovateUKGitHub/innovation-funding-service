package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface ProjectFinanceService {

    @PostAuthorize("hasPermission(returnObject, 'READ_PROJECT_FINANCE')")
    ServiceResult<ProjectFinanceResource> financeChecksDetails(long projectId, long organisationId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'READ_OVERVIEW')")
    ServiceResult<List<ProjectFinanceResource>> financeChecksTotals(long projectId);

    @NotSecured(value = "Should only be called from other secure services")
    ServiceResult<Void> createProjectFinance(long projectId, long organisationId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'READ_OVERVIEW')")
    ServiceResult<Double> getResearchParticipationPercentageFromProject(long projectId);

    @PreAuthorize("hasPermission(#projectFinanceResource, 'UPDATE_PROJECT_FINANCE')")
    ServiceResult<Void> updateProjectFinance(ProjectFinanceResource projectFinanceResource);

    @PreAuthorize("hasPermission(#projectFinanceResource, 'UPDATE_PROJECT_FINANCE')")
    ServiceResult<Boolean> hasAnyProjectOrganisationSizeChangedFromApplication(long projectId);
}