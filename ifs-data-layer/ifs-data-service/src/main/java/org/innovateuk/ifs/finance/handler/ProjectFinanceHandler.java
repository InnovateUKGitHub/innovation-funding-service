package org.innovateuk.ifs.finance.handler;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResourceId;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.util.List;

/**
 * Handler for retrieving project finance data.
 */
public interface ProjectFinanceHandler {
    @PreAuthorize("hasPermission(#projectId, 'READ_OVERVIEW')")
    BigDecimal getResearchParticipationPercentageFromProject(@P("projectId")final Long projectId);

    @PostAuthorize("hasPermission(returnObject, 'READ_PROJECT_FINANCE')")
    ServiceResult<ProjectFinanceResource> getProjectOrganisationFinances(ProjectFinanceResourceId projectFinanceResourceId);

    @NotSecured(value = "This service must be secured by other services", mustBeSecuredByOtherServices = true)
    List<ProjectFinanceResource> getFinanceChecksTotals(Long projectId);
}
