package com.worth.ifs.application.service;

import com.worth.ifs.finance.resource.ApplicationFinanceResource;

public interface ApplicationFinanceService {

    ApplicationFinanceResource getApplicationOrganisationFinances(final Long applicationId, final Long organisationId);
}
