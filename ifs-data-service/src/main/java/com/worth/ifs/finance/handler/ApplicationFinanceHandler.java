package com.worth.ifs.finance.handler;

import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResourceId;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.util.List;

public interface ApplicationFinanceHandler {
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ApplicationFinanceResource getApplicationOrganisationFinances(final ApplicationFinanceResourceId applicationFinanceResourceId);

    @PostFilter("hasPermission(returnObject, 'READ')")
    List<ApplicationFinanceResource> getApplicationTotals(final Long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'READ_RESEARCH_PARTICIPATION_PERCENTAGE')")
    BigDecimal getResearchParticipationPercentage(final Long applicationId);
}
