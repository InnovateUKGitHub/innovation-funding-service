package com.worth.ifs.application.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import org.springframework.stereotype.Service;

@Service
public class ApplicationFinanceRestServiceImpl extends BaseRestService implements ApplicationFinanceRestService {

    private String applicationFinanceRestURL = "/applicationfinance";

    @Override
    public RestResult<ApplicationFinanceResource> getApplicationOrganisationFinances(Long applicationId, Long organisationId) {

        return getWithRestResult(applicationFinanceRestURL + "/finances" + "/" + applicationId + "/" + organisationId, ApplicationFinanceResource.class);
    }
}
