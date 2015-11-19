package com.worth.ifs.finance.service;

import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.finance.domain.ApplicationFinance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * ApplicationFinanceRestServiceImpl is a utility for CRUD operations on {@link ApplicationFinance}.
 * This class connects to the {@link com.worth.ifs.finance.controller.ApplicationFinanceController}
 * through a REST call.
 */
@Service
public class ApplicationFinanceRestServiceImpl extends BaseRestService implements ApplicationFinanceRestService {
    @Value("${ifs.data.service.rest.applicationfinance}")
    String applicationFinanceRestURL;

    @Override
    public ApplicationFinance getApplicationFinance(Long applicationId, Long organisationId) {
        if(applicationId == null || organisationId == null){
            return null;
        }
        return restGet(applicationFinanceRestURL + "/findByApplicationOrganisation/" + applicationId + "/" + organisationId, ApplicationFinance.class);
    }

    @Override
    public List<ApplicationFinance> getApplicationFinances(Long applicationId) {
        if(applicationId == null) {
            return null;
        }

        return asList(restGet(applicationFinanceRestURL + "/findByApplication/" + applicationId, ApplicationFinance[].class));
    }

    @Override
    public ApplicationFinance addApplicationFinanceForOrganisation(Long applicationId, Long organisationId) {
        if(applicationId == null || organisationId == null) {
            return null;
        }

        return restPost(applicationFinanceRestURL + "/add/" + applicationId + "/" + organisationId, null, ApplicationFinance.class);
    }
}
