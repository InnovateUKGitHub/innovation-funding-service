package com.worth.ifs.finance.handler;

import com.worth.ifs.finance.resource.ApplicationFinanceResource;

import java.util.List;

public interface ApplicationFinanceHandler {
    ApplicationFinanceResource getApplicationOrganisationFinances(Long applicationId, Long organisationId);
    List<ApplicationFinanceResource> getApplicationTotals(Long applicationId);
}
