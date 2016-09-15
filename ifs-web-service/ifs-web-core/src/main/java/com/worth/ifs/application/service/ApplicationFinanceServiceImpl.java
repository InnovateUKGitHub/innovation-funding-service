package com.worth.ifs.application.service;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.service.ApplicationFinanceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationFinanceServiceImpl implements ApplicationFinanceService {

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Override
    public ServiceResult<ApplicationFinanceResource> getApplicationOrganisationFinances(Long applicationId, Long organisationId) {

        return applicationFinanceRestService.getApplicationOrganisationFinances(applicationId, organisationId).toServiceResult();

    }
}
