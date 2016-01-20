package com.worth.ifs.finance.handler;

import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResourceId;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface ApplicationFinanceHandler {
    @PreAuthorize("hasPermission(#applicationFinanceResourceId, 'READ')")
    ApplicationFinanceResource getApplicationOrganisationFinances(ApplicationFinanceResourceId applicationFinanceResourceId);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.domain.Application', 'READ')")
    List<ApplicationFinanceResource> getApplicationTotals(Long applicationId);
}
