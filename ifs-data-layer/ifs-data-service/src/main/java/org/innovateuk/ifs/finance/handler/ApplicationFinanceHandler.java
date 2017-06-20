package org.innovateuk.ifs.finance.handler;

import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResourceId;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.util.List;

public interface ApplicationFinanceHandler {

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ApplicationFinanceResource getApplicationOrganisationFinances(final ApplicationFinanceResourceId applicationFinanceResourceId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ_FINANCE_DETAILS')")
    List<ApplicationFinanceResource> getApplicationFinances(final Long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ_FINANCE_TOTALS')")
    List<ApplicationFinanceResource> getApplicationTotals(@P("applicationId")final Long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ_RESEARCH_PARTICIPATION_PERCENTAGE')")
    BigDecimal getResearchParticipationPercentage(@P("applicationId")final Long applicationId);
}
