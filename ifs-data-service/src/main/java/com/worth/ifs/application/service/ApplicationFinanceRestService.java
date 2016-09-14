package com.worth.ifs.application.service;


import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;

public interface ApplicationFinanceRestService {

    RestResult<ApplicationFinanceResource> getApplicationOrganisationFinances(final Long applicationId, final Long organisationId);
}
