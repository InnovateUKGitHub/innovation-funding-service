package com.worth.ifs.application.service;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;

public interface ApplicationFinanceService {

    ServiceResult<ApplicationFinanceResource> getApplicationOrganisationFinances(final Long applicationId, final Long organisationId);
}
