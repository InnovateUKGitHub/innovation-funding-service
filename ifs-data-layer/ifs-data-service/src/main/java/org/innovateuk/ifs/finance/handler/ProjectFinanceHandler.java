package org.innovateuk.ifs.finance.handler;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResourceId;

import java.math.BigDecimal;
import java.util.List;

/**
 * Handler for retrieving project finance data.
 */
public interface ProjectFinanceHandler {
    @NotSecured(value = "This service must be secured by other services")
    BigDecimal getResearchParticipationPercentageFromProject(long projectId);

    @NotSecured(value = "This service should be secured by others. Unless being called by a scheduled job.", mustBeSecuredByOtherServices = false)
    ServiceResult<ProjectFinanceResource> getProjectOrganisationFinances(ProjectFinanceResourceId projectFinanceResourceId);

    @NotSecured(value = "This service must be secured by other service")
    List<ProjectFinanceResource> getFinanceChecksTotals(long projectId);
}
