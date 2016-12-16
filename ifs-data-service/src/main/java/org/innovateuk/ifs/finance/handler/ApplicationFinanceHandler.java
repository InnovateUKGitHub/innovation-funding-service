package org.innovateuk.ifs.finance.handler;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResourceId;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResourceId;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.util.List;

public interface ApplicationFinanceHandler {

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ApplicationFinanceResource getApplicationOrganisationFinances(final ApplicationFinanceResourceId applicationFinanceResourceId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ_FINANCE_TOTALS')")
    List<ApplicationFinanceResource> getApplicationTotals(@P("applicationId")final Long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ_RESEARCH_PARTICIPATION_PERCENTAGE')")
    BigDecimal getResearchParticipationPercentage(@P("applicationId")final Long applicationId);

    // TODO DW - INFUND-4825 - is this permission too broad?
    @PreAuthorize("hasAnyAuthority('comp_admin','project_finance')")
    @SecuredBySpring(value = "READ", securedType = ProjectFinanceResource.class, description = "Internal users can view the Project Finances during the Finance Checks process")
    ProjectFinanceResource getProjectOrganisationFinances(ProjectFinanceResourceId projectFinanceResourceId);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "READ", securedType = ProjectFinanceResource.class, description = "Project Finance users " +
            "can view the overall Project Finances for a Project during the Finance Checks process")
    List<ProjectFinanceResource> getFinanceChecksTotals(Long projectId);
}
