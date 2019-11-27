package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface ApplicationFinanceService {
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ApplicationFinanceResource> findApplicationFinanceByApplicationIdAndOrganisation(long applicationId, long organisationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<ApplicationFinanceResource>> findApplicationFinanceByApplication(long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ_RESEARCH_PARTICIPATION_PERCENTAGE')")
    ServiceResult<Double> getResearchParticipationPercentage(long applicationId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ApplicationFinanceResource> getApplicationFinanceById(long applicationFinanceId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ApplicationFinanceResource> financeDetails(long applicationId, long organisationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ_FINANCE_DETAILS')")
    ServiceResult<List<ApplicationFinanceResource>> financeDetails(long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ_FINANCE_TOTALS')")
    ServiceResult<List<ApplicationFinanceResource>> financeTotals(long applicationId);

    @PreAuthorize("hasPermission(#applicationFinanceId, 'org.innovateuk.ifs.finance.resource.ApplicationFinanceResource', 'UPDATE_COST')")
    ServiceResult<ApplicationFinanceResource> updateApplicationFinance(long applicationFinanceId, ApplicationFinanceResource applicationFinance);

    /**
     * Not included in REST API classes as only meant to be used within data layer
     */
    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource','READ_ORGANISATION_FUNDING_STATUS')")
    ServiceResult<Boolean> organisationSeeksFunding(long projectId, long applicationId, long organisationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource','CHECK_COLLABORATIVE_FUNDING_CRITERIA_MET')")
    ServiceResult<Boolean> collaborativeFundingCriteriaMet(long applicationId);
}
